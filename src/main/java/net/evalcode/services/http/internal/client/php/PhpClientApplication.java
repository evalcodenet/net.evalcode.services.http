package net.evalcode.services.http.internal.client.php;


import java.util.HashMap;
import java.util.Map;


/**
 * PhpClientApplication
 *
 * @author carsten.schipke@gmail.com
 */
public class PhpClientApplication
{
  // PREDEFINED PROPERTIES
  static final String DEFAULT_CLASS_PACKAGE="lib";
  static final String DEFAULT_CLASS_AUTHOR="evalcode.net";


  // MEMBERS
  final Map<String, PhpClientClass> clazzes=new HashMap<>();
  final String name;
  final String url;
  final String path;
  final String filePath;

  PhpClientApplicationClass applicationClazz;


  // CONSTRUCTION
  public PhpClientApplication(final String name, final String filePath,
    final String path, final String url)
  {
    this.name=name;
    this.filePath=filePath;
    this.path=path;
    this.url=url;
  }


  // ACCESSORS/MUTATORS
  public String getName()
  {
    return name;
  }

  public String getFilePath()
  {
    return filePath;
  }

  public String getPath()
  {
    return path;
  }

  public String getUrl()
  {
    return url;
  }

  public PhpClientClass addClass(final PhpClientClass clazz)
  {
    if(!clazzes.containsKey(clazz.getFileName()))
      clazzes.put(clazz.getFileName(), clazz);

    return clazz;
  }

  public Map<String, PhpClientClass> getClasses()
  {
    return clazzes;
  }

  public PhpClientApplicationClass getApplicationClass()
  {
    return applicationClazz;
  }

  public void setApplicationClass(final PhpClientApplicationClass applicationClazz)
  {
    this.applicationClazz=applicationClazz;
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public String toString()
  {
    return applicationClazz.toString();
  }
}
