package net.evalcode.services.http.internal.client;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.evalcode.services.http.annotation.client.WebApplicationClientType;
import org.apache.commons.lang.StringUtils;


/**
 * WebApplicationClientGenerator
 *
 * @author carsten.schipke@gmail.com
 */
public abstract class WebApplicationClientGenerator
{
  // PREDEFINED PROPERTIES
  static final String URL_PATH_SEPARATOR="/";
  static final String METHOD_PARAMETER_NAME_DEFAULT="arg";
  static final String TYPE_CONVERSION_BOOL="bool";
  static final String TYPE_CONVERSION_FLOAT="float";
  static final String TYPE_CONVERSION_INT="int";
  static final String TYPE_CONVERSION_STRING="string";

  static final Map<Class<?>, String> TYPE_CONVERSION_TABLE=new HashMap<Class<?>, String>() {{
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
    }
    private static final long serialVersionUID=1L;
  };


  // MEMBERS
  final Set<Class<?>> resources=new HashSet<>();

  URL baseUrl;
  String contextPath;
  String applicationName;


  // ACCESSORS/MUTATORS
  public void setApplicationName(final String applicationName)
  {
    this.applicationName=applicationName;
  }

  public String getApplicationName()
  {
    return applicationName;
  }

  public void setBaseUrl(final URL baseUrl)
  {
    this.baseUrl=baseUrl;
  }

  public URL getBaseUrl()
  {
    return baseUrl;
  }

  public void setContextPath(final String contextPath)
  {
    this.contextPath=contextPath;
  }

  public String getContextPath()
  {
    return this.contextPath;
  }

  public void addResource(final Class<?> clazz)
  {
    resources.add(clazz);
  }

  public Set<Class<?>> getResources()
  {
    return resources;
  }


  // IMPLEMENTATION
  /**
   * TODO Create base class for result.
   */
  abstract Object generateApplicationClient();

  abstract String getPatternApplicationRootPath();
  abstract String getPatternApplicationClassName();
  abstract String getPatternApplicationFileName();
  abstract String getPatternClassName();
  abstract String getPatternClassFile();
  abstract String getNamespaceResource();
  abstract String getNamespaceEntity();


  String getApplicationFilePath()
  {
    return String.format(getPatternApplicationRootPath(), getApplicationName());
  }

  String getApplicationUrl()
  {
    return getBaseUrl().toString().concat(getContextPath());
  }

  String getApplicationUrlPath()
  {
    return getContextPath();
  }

  String getApplicationClassName()
  {
    return String.format(getPatternApplicationClassName(), getApplicationName());
  }

  String getApplicationFileName()
  {
    return String.format(getPatternApplicationFileName(), getApplicationName());
  }

  String getEntityName(final Class<?> entity)
  {
    String entityClazzName=entity.getSimpleName();
    if(null!=entity.getAnnotation(WebApplicationClientType.class))
      entityClazzName=entity.getAnnotation(WebApplicationClientType.class).value();

    return getEntityName(entityClazzName);
  }

  String getEntityName(final String entityClazzName)
  {
    return String.format(getPatternClassName(),
      StringUtils.capitalize(getApplicationName()),
      StringUtils.capitalize(getNamespaceEntity()),
      StringUtils.capitalize(entityClazzName)
    );
  }

  String getEntityFileName(final Class<?> entity)
  {
    String entityClazzName=entity.getSimpleName();
    if(null!=entity.getAnnotation(WebApplicationClientType.class))
      entityClazzName=entity.getAnnotation(WebApplicationClientType.class).value();

    return String.format(getPatternClassFile(),
      StringUtils.uncapitalize(getNamespaceEntity()),
      StringUtils.uncapitalize(entityClazzName)
    );
  }

  String getResourceName(final Class<?> resource)
  {
    String resourceClazzName=resource.getSimpleName();
    if(null!=resource.getAnnotation(WebApplicationClientType.class))
      resourceClazzName=resource.getAnnotation(WebApplicationClientType.class).value();

    return getResourceName(resourceClazzName);
  }

  String getResourceName(final String resourceClazzName)
  {
    return String.format(getPatternClassName(),
      StringUtils.capitalize(getApplicationName()),
      StringUtils.capitalize(getNamespaceResource()),
      StringUtils.capitalize(resourceClazzName)
    );
  }

  Class<?> getGenericType(final Field field)
  {
    try
    {
      return Class.forName(getGenericTypeName(field));
    }
    catch(final ClassNotFoundException e)
    {
      return null;
    }
  }

  String getGenericTypeName(final Field field)
  {
    return StringUtils.substringBetween(field.getGenericType().toString(), "<", ">");
  }

  String getResourceFileName(final Class<?> resource)
  {
    String resourceClazzName=resource.getSimpleName();
    if(null!=resource.getAnnotation(WebApplicationClientType.class))
      resourceClazzName=resource.getAnnotation(WebApplicationClientType.class).value();

    return String.format(getPatternClassFile(),
      StringUtils.uncapitalize(getNamespaceResource()),
      StringUtils.uncapitalize(resourceClazzName)
    );
  }

  String getResourceUrlPath(final Class<?> resource)
  {
    return getUrlPath(resource.getAnnotation(Path.class));
  }

  String getMethodName(final Method method)
  {
    return method.getName();
  }

  String getMethodUrlPath(final Method method)
  {
    return getUrlPath(method.getAnnotation(Path.class));
  }

  String getUrlPath(final Path path)
  {
    if(0!=path.value().indexOf(URL_PATH_SEPARATOR))
      return URL_PATH_SEPARATOR.concat(path.value());

    return path.value();
  }

  Class<?> getMethodParameterType(final Method method, final int parameter)
  {
    return method.getParameterTypes()[parameter];
  }

  String getMethodParameterName(final Method method, final int parameter)
  {
    for(final Annotation parameterAnnotation : method.getParameterAnnotations()[parameter])
    {
      if(parameterAnnotation instanceof PathParam)
        return ((PathParam)parameterAnnotation).value();

      if(parameterAnnotation instanceof QueryParam)
        return ((QueryParam)parameterAnnotation).value();
    }

    return getMethodParameterNameDefault().concat(String.valueOf(parameter));
  }

  String getMethodParameterNameDefault()
  {
    return METHOD_PARAMETER_NAME_DEFAULT;
  }

  String getFieldName(final Field field)
  {
    final XmlElement xmlElement=field.getAnnotation(XmlElement.class);

    if(null==xmlElement || null==xmlElement.name())
      return field.getName();

    return underscoreToCamelCase(xmlElement.name());
  }

  boolean isWebApplicationMethod(final Method method)
  {
    return null!=method.getAnnotation(Path.class);
  }

  boolean isWebApplicationMethodParameter(final Method method, final int parameter)
  {
    for(final Annotation parameterAnnotation : method.getParameterAnnotations()[parameter])
    {
      if(parameterAnnotation instanceof PathParam || parameterAnnotation instanceof QueryParam)
        return true;
    }

    return false;
  }

  boolean isWebApplicationQueryParameter(final Method method, final int parameter)
  {
    for(final Annotation parameterAnnotation : method.getParameterAnnotations()[parameter])
    {
      if(parameterAnnotation instanceof QueryParam)
        return true;
    }

    return false;
  }

  boolean isJaxbType(final Class<?> type)
  {
    return null!=type.getAnnotation(XmlRootElement.class) ||
      null!=type.getAnnotation(XmlType.class) ||
      null!=type.getAnnotation(XmlEnum.class);
  }

  String underscoreToCamelCase(final String string)
  {
    final StringBuffer stringBuffer=new StringBuffer();
    final String[] words=StringUtils.split(string, "_");

    if(2>words.length)
      return string;

    int i=0;

    stringBuffer.append(StringUtils.lowerCase(words[i++]));

    for(int j=i; j<words.length; j++)
      stringBuffer.append(StringUtils.capitalize(words[j]));

    return stringBuffer.toString();
  }
}
