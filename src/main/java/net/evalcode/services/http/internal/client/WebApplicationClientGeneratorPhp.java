package net.evalcode.services.http.internal.client;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlTransient;
import net.evalcode.services.http.annotation.client.WebApplicationClientEntities;
import net.evalcode.services.http.annotation.client.WebApplicationClientType;
import net.evalcode.services.http.internal.client.php.PhpClientApplication;
import net.evalcode.services.http.internal.client.php.PhpClientClass;
import net.evalcode.services.http.internal.client.php.PhpClientClassConstant;
import net.evalcode.services.http.internal.client.php.PhpClientClassProperty;
import net.evalcode.services.http.internal.client.php.PhpClientResourceClass;
import net.evalcode.services.http.internal.client.php.PhpClientResourceMethod;
import net.evalcode.services.http.internal.client.php.PhpClientResourceMethodParameter;
import net.evalcode.services.http.internal.client.php.PhpEnumeration;
import org.apache.commons.lang.ClassUtils;
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
  public static final String NAMESPACE="Components";
  public static final String CLASS_RESOURCE="Rest_Resource";
  public static final String CLASS_OBJECT="Object";
  public static final String CLASS_PATH="source";
  public static final String CLASS_FILE_EXTENSION="php";

  public static final String PHP_TYPE_DEFAULT="null";
  public static final String PHP_TYPE_COLLECTION="array";
  public static final String PHP_TYPE_COLLECTION_DEFAULT="[]";

  public static final Map<Class<?>, String> TYPE_TABLE_PRIMITIVES=new HashMap<Class<?>, String>() {{
      put(boolean.class, TYPE_CONVERSION_BOOL);
      put(Boolean.class, TYPE_CONVERSION_BOOL);
      put(boolean.class, TYPE_CONVERSION_BOOL);
      put(Byte.class, TYPE_CONVERSION_INT);
      put(byte.class, TYPE_CONVERSION_INT);
      put(Character.class, TYPE_CONVERSION_INT);
      put(char.class, TYPE_CONVERSION_INT);
      put(Double.class, TYPE_CONVERSION_FLOAT);
      put(double.class, TYPE_CONVERSION_FLOAT);
      put(Float.class, TYPE_CONVERSION_FLOAT);
      put(float.class, TYPE_CONVERSION_FLOAT);
      put(Integer.class, TYPE_CONVERSION_INT);
      put(int.class, TYPE_CONVERSION_INT);
      put(Long.class, TYPE_CONVERSION_INT);
      put(long.class, TYPE_CONVERSION_INT);
      put(Short.class, TYPE_CONVERSION_INT);
      put(short.class, TYPE_CONVERSION_INT);
      put(String.class, TYPE_CONVERSION_STRING);
    }
    private static final long serialVersionUID=1L;
  };

  static final Logger LOG=LoggerFactory.getLogger(WebApplicationClientGeneratorPhp.class);


  // OVERRIDES/IMPLEMENTS
  @Override
  public PhpClientApplication generateApplicationClient()
  {
    final PhpClientApplication application=new PhpClientApplication(
      getApplicationName(), getApplicationUrlPath(), getApplicationUrl()
    );

    for(final Class<?> resourceClazz : getResources())
    {
      if(!application.getClasses().containsKey(getFileName(getClazzName(resourceClazz))))
        generateResourceClass(application, resourceClazz);
    }

    return application;
  }

  public PhpClientResourceClass generateResourceClass(final PhpClientApplication application,
    final Class<?> resourceClazz)
  {
    final PhpClientResourceClass resource=(PhpClientResourceClass)application.addClass(
      new PhpClientResourceClass(application, resourceClazz, getClazzName(resourceClazz),
        getFileName(getClazzName(resourceClazz)), getResourceUrlPath(resourceClazz)
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
          !application.getClasses().containsKey(getFileName(getClazzName(referencedEntityType))))
          addEntityType(application, resource, referencedEntityType);
      }
    }

    return resource;
  }

  public PhpClientResourceMethod generateResourceMethod(final PhpClientApplication application,
    final PhpClientResourceClass resource, final Method method)
  {
    Class<?> returnType=method.getReturnType();
    String returnTypeName=null;

    if(!void.class.equals(returnType))
    {
      Class<?> concreteReturnType=returnType;

      if(returnType.isEnum())
      {
        final XmlEnum xmlEnum=returnType.getAnnotation(XmlEnum.class);

        if(null==xmlEnum)
          concreteReturnType=String.class;
        else
          concreteReturnType=xmlEnum.value();
      }

      if(TYPE_TABLE_CONVERSION.containsKey(concreteReturnType))
        returnTypeName=TYPE_TABLE_CONVERSION.get(concreteReturnType);
      else
        returnTypeName=getClazzName(concreteReturnType);

      if(ClassUtils.getAllInterfaces(returnType).contains(Iterable.class))
      {
        final Type genericReturnType=method.getGenericReturnType();

        if(genericReturnType instanceof ParameterizedType)
        {
          final Type actualType=((ParameterizedType)genericReturnType).getActualTypeArguments()[0];

          concreteReturnType=(Class<?>)actualType;

          if(TYPE_TABLE_CONVERSION.containsKey(concreteReturnType))
            returnTypeName=TYPE_TABLE_CONVERSION.get(concreteReturnType).concat("[]");
          else
            returnTypeName=getClazzName(concreteReturnType).concat("[]");
        }
      }
      else if(returnType.isArray())
      {
        returnTypeName=returnTypeName.concat("[]");
      }

      if(isJaxbType(concreteReturnType))
      {
        if(!application.getClasses().containsKey(getFileName(getClazzName(concreteReturnType))))
          addEntityType(application, resource, concreteReturnType);
      }
    }

    final PhpClientResourceMethod resourceMethod=(PhpClientResourceMethod)resource.addMethod(
      new PhpClientResourceMethod(resource, getMethodName(method), getHttpMethod(method),
        getMethodUrlPath(method), returnTypeName
    ));

    final int parameterCount=method.getParameterTypes().length;

    for(int i=0; i<parameterCount; i++)
      generateResourceMethodParameter(application, resource, resourceMethod, method, i);

    return resourceMethod;
  }

  public PhpClientResourceMethodParameter generateResourceMethodParameter(
    final PhpClientApplication application, final PhpClientResourceClass resource,
    final PhpClientResourceMethod resourceMethod, final Method method, final int parameter)
  {
    final Class<?> parameterType=getMethodParameterType(method, parameter);

    Class<?> concreteParameterType=parameterType;
    String parameterTypeDoc=getClazzName(parameterType);
    String parameterDefault=null;

    if(isWebApplicationQueryParameter(method, parameter))
      parameterDefault=PHP_TYPE_DEFAULT;

    String parameterTypeHint;
    if(TYPE_TABLE_PRIMITIVES.containsKey(concreteParameterType))
      parameterTypeHint=null;
    else
      parameterTypeHint=getClazzName(parameterType);

    if(parameterType.isEnum())
    {
      final XmlEnum xmlEnum=parameterType.getAnnotation(XmlEnum.class);

      if(null==xmlEnum)
        concreteParameterType=String.class;
      else
        concreteParameterType=xmlEnum.value();
    }
    else if(ClassUtils.getAllInterfaces(parameterType).contains(Iterable.class))
    {
      final Type genericParameterType=getMethodGenericParameterType(method, parameter);

      if(genericParameterType instanceof ParameterizedType)
      {
        final Type actualType=((ParameterizedType)genericParameterType).getActualTypeArguments()[0];

        concreteParameterType=(Class<?>)actualType;
        parameterTypeHint=WebApplicationClientGeneratorPhp.PHP_TYPE_COLLECTION;

        if(TYPE_TABLE_CONVERSION.containsKey(concreteParameterType))
          parameterTypeDoc=TYPE_TABLE_CONVERSION.get(concreteParameterType).concat("[]");
        else
          parameterTypeDoc=getClazzName(concreteParameterType).concat("[]");

        if(null!=parameterDefault)
          parameterDefault=PHP_TYPE_COLLECTION_DEFAULT;
      }
    }
    else if(parameterType.isArray())
    {
      parameterTypeHint=WebApplicationClientGeneratorPhp.PHP_TYPE_COLLECTION;
      if(TYPE_TABLE_CONVERSION.containsKey(concreteParameterType))
        parameterTypeDoc=TYPE_TABLE_CONVERSION.get(concreteParameterType).concat("[]");
      else
        parameterTypeDoc=getClazzName(concreteParameterType).concat("[]");

      if(null!=parameterDefault)
        parameterDefault=PHP_TYPE_COLLECTION_DEFAULT;
    }
    else if(TYPE_TABLE_CONVERSION.containsKey(concreteParameterType))
    {
      parameterTypeHint=null;
      parameterTypeDoc=TYPE_TABLE_CONVERSION.get(concreteParameterType);
    }

    if(isJaxbType(concreteParameterType))
    {
      if(!application.getClasses().containsKey(getFileName(getClazzName(parameterType))))
        addEntityType(application, resource, parameterType);
    }

    return (PhpClientResourceMethodParameter)resourceMethod.addParameter(
      new PhpClientResourceMethodParameter(
        getMethodParameterName(method, parameter),
        concreteParameterType,
        parameterTypeHint,
        parameterTypeDoc,
        parameterDefault,
        isWebApplicationQueryParameter(method, parameter)
    ));
  }

  public PhpClientClass addEntityType(final PhpClientApplication application,
    final PhpClientResourceClass resource, final Class<?> clazz)
  {
    if(clazz.isEnum())
      return addEnumeration(application, resource, clazz);

    return addEntityClass(application, resource, clazz);
  }

  public PhpEnumeration addEnumeration(final PhpClientApplication application,
    final PhpClientResourceClass resource, final Class<?> clazz)
  {
    final PhpEnumeration enumeration=new PhpEnumeration(
      application, clazz, getClazzName(clazz), getFileName(getClazzName(clazz))
    );

    application.addClass(enumeration);
    resource.addEntityClass(enumeration);

    final XmlEnum xmlEnum=clazz.getAnnotation(XmlEnum.class);
    final boolean numeric=AtomicInteger.class.equals(xmlEnum.value()) ||
      AtomicLong.class.equals(xmlEnum.value()) ||
      BigDecimal.class.equals(xmlEnum.value()) ||
      BigInteger.class.equals(xmlEnum.value()) ||
      Double.class.equals(xmlEnum.value()) ||
      Float.class.equals(xmlEnum.value()) ||
      Integer.class.equals(xmlEnum.value()) ||
      Long.class.equals(xmlEnum.value()) ||
      Short.class.equals(xmlEnum.value());

    for(final Field field : clazz.getDeclaredFields())
    {
      final XmlEnumValue enumValue=field.getAnnotation(XmlEnumValue.class);

      if(null==enumValue)
        continue;

      if(numeric)
      {
        enumeration.addConstant(new PhpClientClassConstant(field.getName(), enumValue.value()));
      }
      else
      {
        enumeration.addConstant(new PhpClientClassConstant(
          field.getName(), "'"+enumValue.value()+"'"
        ));
      }
    }

    return enumeration;
  }

  public PhpClientClass addEntityClass(final PhpClientApplication application,
    final PhpClientResourceClass resource, final Class<?> clazz)
  {
    final PhpClientClass entityClazz=application.addClass(
      new PhpClientClass(application, clazz, getClazzName(clazz), getFileName(getClazzName(clazz)))
    );

    entityClazz.addInterface(CLASS_OBJECT);

    resource.addEntityClass(entityClazz);

    XmlAccessType accessType=XmlAccessType.PUBLIC_MEMBER;
    if(null!=clazz.getAnnotation(XmlAccessorType.class))
      accessType=clazz.getAnnotation(XmlAccessorType.class).value();

    for(final Field field : clazz.getDeclaredFields())
    {
      if(Modifier.isStatic(field.getModifiers()) ||
        Modifier.isTransient(field.getModifiers()) ||
        null!=field.getAnnotation(XmlTransient.class))
        continue;

      if(!Modifier.isPublic(field.getModifiers()) &&
        null==field.getAnnotation(XmlElement.class) &&
        XmlAccessType.FIELD!=accessType)
        continue;

      String fieldValue=null;
      String fieldTypeName=null;

      final WebApplicationClientType webApplicationClientType=
        field.getAnnotation(WebApplicationClientType.class);

      if(null!=webApplicationClientType)
      {
        if(webApplicationClientType.value().isEmpty())
          fieldTypeName=webApplicationClientType.type().value;
        else
          fieldTypeName=webApplicationClientType.value();
      }

      if(null==fieldTypeName && TYPE_TABLE_CONVERSION.containsKey(field.getType()))
        fieldTypeName=TYPE_TABLE_CONVERSION.get(field.getType());

      if(null==fieldTypeName)
        fieldTypeName=TYPE_CONVERSION_STRING;

      if(isJaxbType(field.getType()))
      {
        fieldTypeName="\\".concat(NAMESPACE).concat("\\").concat(getClazzName(field.getType()));

        if(!application.getClasses().containsKey(getFileName(getClazzName(field.getType()))))
          addEntityType(application, resource, field.getType());
      }

      if(ClassUtils.getAllInterfaces(field.getType()).contains(Iterable.class)
        || field.getType().isArray())
      {
        final Type genericType=field.getGenericType();

        if(genericType instanceof ParameterizedType)
        {
          final Class<?> actualType=(Class<?>)((ParameterizedType)genericType)
            .getActualTypeArguments()[0];

          if(TYPE_TABLE_CONVERSION.containsKey(actualType))
            fieldTypeName=TYPE_TABLE_CONVERSION.get(actualType);
          else
            fieldTypeName=getClazzName(actualType);

          if(isJaxbType(actualType))
            fieldTypeName="\\".concat(NAMESPACE).concat("\\").concat(getClazzName(actualType));

          if(!application.getClasses().containsKey(getFileName(getClazzName(actualType))))
            addEntityType(application, resource, actualType);
        }

        fieldValue=PHP_TYPE_COLLECTION_DEFAULT;
        fieldTypeName=fieldTypeName.concat("[]");
      }

      entityClazz.addProperty(new PhpClientClassProperty(
        getFieldName(field), getFieldNameMapped(field), fieldValue, fieldTypeName
      ));
    }

    return entityClazz;
  }

  @Override
  String getNamespace()
  {
    return NAMESPACE;
  }

  @Override
  String getFileName(final String clazzName)
  {
    final String scopedClazzName=clazzName.replaceFirst(applicationName.concat("_"), "");

    return CLASS_PATH.concat(URL_PATH_SEPARATOR)
      .concat(scopedClazzName.replace("_", URL_PATH_SEPARATOR).toLowerCase())
      .concat(".").concat(CLASS_FILE_EXTENSION);
  }
}
