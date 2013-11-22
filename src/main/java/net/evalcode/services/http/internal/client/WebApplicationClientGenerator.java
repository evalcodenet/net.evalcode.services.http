package net.evalcode.services.http.internal.client;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.evalcode.services.http.annotation.client.WebApplicationClientType;
import org.apache.commons.lang.StringUtils;
import com.google.common.collect.Lists;


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

  public static final Map<Class<?>, String> TYPE_TABLE_CONVERSION=new HashMap<Class<?>, String>() {{
      put(boolean.class, TYPE_CONVERSION_BOOL);
      put(Boolean.class, TYPE_CONVERSION_BOOL);
      put(boolean.class, TYPE_CONVERSION_BOOL);
      put(Byte.class, TYPE_CONVERSION_INT);
      put(byte.class, TYPE_CONVERSION_INT);
      put(BigDecimal.class, TYPE_CONVERSION_FLOAT);
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
  abstract String getNamespace();


  String getApplicationUrl()
  {
    return getBaseUrl().toString().concat(getContextPath());
  }

  String getApplicationUrlPath()
  {
    return getContextPath();
  }

  String getClazzName(final Class<?> clazz)
  {
    final WebApplicationClientType type=clazz.getAnnotation(WebApplicationClientType.class);

    if(null!=type && null!=type.value())
      return getClazzName(type.value());

    Class<?> enclosingClazz=clazz.getEnclosingClass();

    final List<String> list=new ArrayList<>();
    final StringBuffer fqn=new StringBuffer();

    list.add(clazz.getSimpleName());

    while(null!=enclosingClazz)
    {
      list.add(enclosingClazz.getSimpleName());
      enclosingClazz=enclosingClazz.getEnclosingClass();
    }

    final List<String> path=Lists.reverse(list);

    for(final String node : path)
      fqn.append(node);

    return getClazzName(fqn.toString());
  }

  String getClazzName(final String clazzName)
  {
    final StringBuffer stringBuffer=new StringBuffer(clazzName.length()+5);

    stringBuffer.append(applicationName);
    stringBuffer.append("_");

    int i=0;

    for(final byte b : clazzName.getBytes())
    {
      final char c=(char)b;

      if(++i==1)
      {
        stringBuffer.append(Character.toUpperCase(c));
      }
      else
      {
        if(Character.isUpperCase(c))
          stringBuffer.append("_");

        stringBuffer.append(c);
      }
    }

    return stringBuffer.toString();
  }

  String getFileName(final String clazzName)
  {
    return clazzName.replace("_", URL_PATH_SEPARATOR).toLowerCase();
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

  String getResourceUrlPath(final Class<?> resource)
  {
    return getUrlPath(resource.getAnnotation(Path.class));
  }

  String getMethodName(final Method method)
  {
    return method.getName();
  }

  String getHttpMethod(final Method method)
  {
    if(null!=method.getAnnotation(PUT.class))
      return "PUT";

    if(null!=method.getAnnotation(POST.class))
      return "POST";

    if(null!=method.getAnnotation(HEAD.class))
      return "HEAD";

    if(null!=method.getAnnotation(OPTIONS.class))
      return "OPTIONS";

    if(null!=method.getAnnotation(DELETE.class))
      return "DELETE";

    return "GET";
  }

  String getMethodUrlPath(final Method method)
  {
    final Path path=method.getAnnotation(Path.class);

    if(null==path)
      return null;

    return getUrlPath(path);
  }

  String getUrlPath(final Path path)
  {
    if(0==path.value().indexOf(URL_PATH_SEPARATOR))
      return path.value().substring(1);

    return path.value();
  }

  Class<?> getMethodParameterType(final Method method, final int parameter)
  {
    return method.getParameterTypes()[parameter];
  }

  Type getMethodGenericParameterType(final Method method, final int parameter)
  {
    return method.getGenericParameterTypes()[parameter];
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
    return field.getName();
  }

  String getFieldNameMapped(final Field field)
  {
    final XmlElement xmlElement=field.getAnnotation(XmlElement.class);

    if(null==xmlElement || null==xmlElement.name())
      return field.getName();

    return xmlElement.name();
  }

  boolean isWebApplicationMethod(final Method method)
  {
    return null!=method.getAnnotation(Path.class)
      || null!=method.getAnnotation(HEAD.class)
      || null!=method.getAnnotation(OPTIONS.class)
      || null!=method.getAnnotation(GET.class)
      || null!=method.getAnnotation(PUT.class)
      || null!=method.getAnnotation(POST.class)
      || null!=method.getAnnotation(DELETE.class);
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
