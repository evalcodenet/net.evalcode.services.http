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
  static final String DEFAULT_CLASS_AUTHOR="evalcode.net";


  // MEMBERS
  final Map<String, PhpClientClass> clazzes=new HashMap<>();

  final String name;
  final String url;
  final String path;


  // CONSTRUCTION
  public PhpClientApplication(final String name, final String path, final String url)
  {
    this.name=name;
    this.path=path;
    this.url=url;
  }


  // ACCESSORS/MUTATORS
  public String getName()
  {
    return name;
  }

  public String getPackageName()
  {
    return name.replace("_", ".").toLowerCase();
  }

  public String getSubPackageName(final String clazzName)
  {
    if(-1==clazzName.indexOf("_"))
      return null;

    final String[] chunks=clazzName.split("_");

    if(2==chunks.length)
      return null;

    final String[] chunksPackage=new String[chunks.length-2];
    System.arraycopy(chunks, 1, chunksPackage, 0, chunksPackage.length);

    final StringBuffer stringBuffer=new StringBuffer(clazzName.length());

    int i=0;

    for(final String chunk : chunksPackage)
    {
      if(1<++i)
        stringBuffer.append(".");

      stringBuffer.append(chunk.toLowerCase());
    }

    return stringBuffer.toString();
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
}
