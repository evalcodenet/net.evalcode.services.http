package net.evalcode.services.http.internal.client;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlTransient;
import net.evalcode.services.http.annotation.client.WebApplicationClientEntities;
import net.evalcode.services.http.annotation.client.WebApplicationClientType;
import net.evalcode.services.http.internal.client.php.PhpClientApplication;
import net.evalcode.services.http.internal.client.php.PhpClientApplicationClass;
import net.evalcode.services.http.internal.client.php.PhpClientClass;
import net.evalcode.services.http.internal.client.php.PhpClientClassConstant;
import net.evalcode.services.http.internal.client.php.PhpClientClassProperty;
import net.evalcode.services.http.internal.client.php.PhpClientMethod;
import net.evalcode.services.http.internal.client.php.PhpClientMethodParameter;
import net.evalcode.services.http.internal.client.php.PhpClientResourceClass;
import net.evalcode.services.http.internal.client.php.PhpClientResourceMethod;
import net.evalcode.services.http.internal.client.php.PhpClientResourceMethodParameter;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * WebApplicationClientGeneratorPhp
 *
 * @author carsten.schipke@gmail.com
 */
public class WebApplicationClientGeneratorPhp extends WebApplicationClientGenerator
{
  // PREDEFINED PROPERTIES
  public static final String PATTERN_APPLICATION_ROOT_PATH="%1$s";
  public static final String PATTERN_APPLICATION_CLASS_NAME="%1$s_Client";
  public static final String PATTERN_CLASS_NAME="%1$s_%2$s_%3$s";
  public static final String PATTERN_CLASS_FILE="%1$s/%2$s.php";

  public static final String NAMESPACE_RESOURCE="resource";
  public static final String NAMESPACE_ENTITY="transport";

  public static final String APPLICATION_FILE_NAME="client.php";
  public static final String CLASS_NAME_SERVICES_CLIENT="Services_Client";
  public static final String CLASS_NAME_SERVICES_TRANSPORT_OBJECT="Services_Transport_Object";

  public static final String PHP_DEFAULT_TYPE_INITIALIZATION="null";

  public static final String PHP_COLLECTION_WRAPPER_TYPE_NAME="array";
  public static final String PHP_COLLECTION_WRAPPER_TYPE_PHPDOC="mixed";
  public static final String PHP_COLLECTION_WRAPPER_TYPE_INITIALIZATION="array()";

  static final Logger LOG=LoggerFactory.getLogger(WebApplicationClientGeneratorPhp.class);


  // OVERRIDES/IMPLEMENTS
  @Override
  public PhpClientApplication generateApplicationClient()
  {
    final PhpClientApplication application=new PhpClientApplication(
      getApplicationName(), getApplicationFilePath(), getApplicationUrlPath(), getApplicationUrl()
    );

    application.setApplicationClass(new PhpClientApplicationClass(
      application, getApplicationClassName(), getApplicationFileName()
    ));

    for(final Class<?> resourceClazz : getResources())
    {
      if(!application.getClasses().containsKey(getResourceFileName(resourceClazz)))
        generateResourceClass(application, resourceClazz);
    }

    return application;
  }

  public PhpClientResourceClass generateResourceClass(final PhpClientApplication application,
    final Class<?> resourceClazz)
  {
    final PhpClientResourceClass resource=(PhpClientResourceClass)application.addClass(
      new PhpClientResourceClass(application, resourceClazz, getResourceName(resourceClazz),
        getResourceFileName(resourceClazz), getResourceUrlPath(resourceClazz)
    ));

    final PhpClientMethod resourceConstructor=resource.addMethod(
      PhpClientMethod.createConstructor(resource)
    );

    resourceConstructor.addParameter(
      new PhpClientMethodParameter("client", CLASS_NAME_SERVICES_CLIENT,
        CLASS_NAME_SERVICES_CLIENT, true
    ));

    for(final Method method : resourceClazz.getMethods())
    {
      if(!isWebApplicationMethod(method))
        continue;

      generateResourceMethod(application, resource, method);
    }

    final WebApplicationClientEntities referencedEntityTypes=
      resourceClazz.getAnnotation(WebApplicationClientEntities.class);

    if(null!=referencedEntityTypes)
    {
      for(final Class<?> referencedEntityType : referencedEntityTypes.value())
      {
        if(isJaxbType(referencedEntityType) &&
          !application.getClasses().containsKey(getEntityFileName(referencedEntityType)))
          generateEntityClass(application, resource, referencedEntityType);
      }
    }

    return resource;
  }

  public PhpClientResourceMethod generateResourceMethod(final PhpClientApplication application,
    final PhpClientResourceClass resource, final Method method)
  {
    String returnTypeName=null;
    Class<?> returnType=method.getReturnType();

    if(ClassUtils.getAllInterfaces(method.getReturnType()).contains(Iterable.class))
    {
      final int countTypeArguments=((ParameterizedType)method.getGenericReturnType())
        .getActualTypeArguments().length;

      if(method.getGenericReturnType() instanceof ParameterizedType && 1==countTypeArguments)
      {
        final String genericReturnTypeName=
          ((Class<?>)((ParameterizedType)method.getGenericReturnType())
            .getActualTypeArguments()[0]).getName();

        try
        {
          returnType=Class.forName(genericReturnTypeName);
        }
        catch(final ClassNotFoundException e)
        {
          LOG.debug(e.getMessage(), e);
        }
      }
    }

    if(isJaxbType(returnType))
    {
      returnTypeName=getEntityName(returnType);

      if(!application.getClasses().containsKey(getEntityFileName(returnType)))
        generateEntityClass(application, resource, returnType);
    }
    else if(ClassUtils.getAllInterfaces(method.getReturnType()).contains(Iterable.class))
    {
      returnTypeName=PHP_COLLECTION_WRAPPER_TYPE_PHPDOC;
    }

    final PhpClientResourceMethod resourceMethod=(PhpClientResourceMethod)resource.addMethod(
      new PhpClientResourceMethod(resource, getMethodName(method),
        getMethodUrlPath(method), returnTypeName
    ));

    final int parameterCount=method.getParameterTypes().length;

    for(int i=0; i<parameterCount; i++)
    {
      if(!isWebApplicationMethodParameter(method, i))
        continue;

      generateResourceMethodParameter(application, resource, resourceMethod, method, i);
    }

    return resourceMethod;
  }

  public PhpClientResourceMethodParameter generateResourceMethodParameter(
    final PhpClientApplication application, final PhpClientResourceClass resource,
    final PhpClientResourceMethod resourceMethod, final Method method, final int parameter)
  {
    final Class<?> parameterType=getMethodParameterType(method, parameter);

    String parameterTypeName=null;
    if(ClassUtils.getAllInterfaces(parameterType).contains(Iterable.class))
    {
      parameterTypeName=PHP_COLLECTION_WRAPPER_TYPE_NAME;
    }
    else if(isJaxbType(parameterType))
    {
      parameterTypeName=getEntityName(parameterType);

      if(!application.getClasses().containsKey(getEntityFileName(parameterType)))
        generateEntityClass(application, resource, parameterType);
    }

    return (PhpClientResourceMethodParameter)resourceMethod.addParameter(
      new PhpClientResourceMethodParameter(
        getMethodParameterName(method, parameter),
        parameterType.getSimpleName(),
        parameterTypeName,
        isWebApplicationQueryParameter(method, parameter)
    ));
  }

  public PhpClientClass generateEntityClass(final PhpClientApplication application,
    final PhpClientResourceClass resource, final Class<?> clazz)
  {
    final PhpClientClass entityClazz=application.addClass(
      new PhpClientClass(application, clazz, getEntityName(clazz), getEntityFileName(clazz))
    );

    entityClazz.addInterface(CLASS_NAME_SERVICES_TRANSPORT_OBJECT);

    resource.addEntityClass(entityClazz);

    XmlAccessType accessType=XmlAccessType.PUBLIC_MEMBER;
    if(null!=clazz.getAnnotation(XmlAccessorType.class))
      accessType=clazz.getAnnotation(XmlAccessorType.class).value();

    for(final Field field : clazz.getDeclaredFields())
    {
      final XmlEnumValue enumValue=field.getAnnotation(XmlEnumValue.class);

      if(null!=enumValue)
        entityClazz.addConstant(new PhpClientClassConstant(field.getName(), enumValue.value()));

      if(clazz.isEnum())
        continue;

      if(Modifier.isStatic(field.getModifiers()) ||
        Modifier.isTransient(field.getModifiers()) ||
        null!=field.getAnnotation(XmlTransient.class))
        continue;

      if(!Modifier.isPublic(field.getModifiers()) &&
        null==field.getAnnotation(XmlElement.class) &&
        XmlAccessType.FIELD!=accessType)
        continue;

      final WebApplicationClientType webApplicationClientType=
        field.getAnnotation(WebApplicationClientType.class);

      String fieldValue=null;
      String fieldTypeName=null;

      if(ClassUtils.getAllInterfaces(field.getType()).contains(Iterable.class))
      {
        if(null==webApplicationClientType)
        {
          final Class<?> genericType=getGenericType(field);

          if(null==genericType)
          {
            fieldTypeName=getEntityName(getGenericTypeName(field));
          }
          else
          {
            fieldTypeName=getEntityName(genericType);
            if(!application.getClasses().containsKey(getEntityFileName(genericType)))
              generateEntityClass(application, resource, genericType);
          }
        }
        else
        {
          fieldTypeName=getEntityName(webApplicationClientType.value());
        }

        fieldValue=PHP_COLLECTION_WRAPPER_TYPE_INITIALIZATION;
      }
      else if(null!=webApplicationClientType)
      {
        String webApplicationClientTypeName=null;
        if(webApplicationClientType.value().isEmpty())
          webApplicationClientTypeName=webApplicationClientType.type().value;
        else
          webApplicationClientTypeName=webApplicationClientType.value();

        String webApplicationClientTypeProperties="";
        if(0<webApplicationClientType.properties().length)
        {
          webApplicationClientTypeProperties=String.format("|%1$s",
            StringUtils.join(webApplicationClientType.properties(), "|")
          );
        }

        if(null!=webApplicationClientTypeName && !webApplicationClientTypeName.isEmpty())
          fieldTypeName=webApplicationClientTypeName.concat(webApplicationClientTypeProperties);
      }
      else if(isJaxbType(field.getType()))
      {
        fieldTypeName=getEntityName(field.getType());

        if(!application.getClasses().containsKey(getEntityFileName(field.getType())))
          generateEntityClass(application, resource, field.getType());
      }

      if(null==fieldTypeName && TYPE_CONVERSION_TABLE.containsKey(field.getType()))
        fieldTypeName=TYPE_CONVERSION_TABLE.get(field.getType());

      if(null==fieldTypeName)
        fieldTypeName=TYPE_CONVERSION_STRING;

      entityClazz.addProperty(new PhpClientClassProperty(
        getFieldName(field), fieldValue, fieldTypeName
      ));
    }

    return entityClazz;
  }

  public PhpClientClass generateEntityClass(final PhpClientApplication application,
    final PhpClientResourceClass resource, final String clazzName)
  {
    try
    {
      return generateEntityClass(application, resource, Class.forName(clazzName));
    }
    catch(final ClassNotFoundException e)
    {
      return null;
    }
  }

  @Override
  String getPatternApplicationRootPath()
  {
    return PATTERN_APPLICATION_ROOT_PATH;
  }

  @Override
  String getApplicationFileName()
  {
    return APPLICATION_FILE_NAME;
  }

  @Override
  String getPatternApplicationFileName()
  {
    return null;
  }

  @Override
  String getPatternApplicationClassName()
  {
    return PATTERN_APPLICATION_CLASS_NAME;
  }

  @Override
  String getPatternClassFile()
  {
    return PATTERN_CLASS_FILE;
  }

  @Override
  String getPatternClassName()
  {
    return PATTERN_CLASS_NAME;
  }

  @Override
  String getNamespaceResource()
  {
    return NAMESPACE_RESOURCE;
  }

  @Override
  String getNamespaceEntity()
  {
    return NAMESPACE_ENTITY;
  }
}
