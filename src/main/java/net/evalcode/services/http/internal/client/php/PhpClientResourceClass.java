package net.evalcode.services.http.internal.client.php;


import java.util.ArrayList;
import java.util.List;


/**
 * PhpClientResourceClass
 *
 * @author carsten.schipke@gmail.com
 */
public class PhpClientResourceClass extends PhpClientClass
{
  // MEMBERS
  final List<PhpClientClass> entityClasses=new ArrayList<>();
  final String path;


  // CONSTRUCTION
  public PhpClientResourceClass(final PhpClientApplication application, final Class<?> clazz,
    final String name, final String fileName, final String path)
  {
    super(application, clazz, name, fileName);

    this.path=path;
  }


  // ACCESSORS/MUTATORS
  public String getPath()
  {
    return getPath(true);
  }

  public String getPath(final boolean full)
  {
    if(full)
      return application.getPath().concat("/").concat(path);

    return path;
  }

  public String getUrl()
  {
    return application.getUrl().concat(getPath(true));
  }

  public void addEntityClass(final PhpClientClass entityClazz)
  {
    entityClasses.add(entityClazz);
  }

  public List<PhpClientClass> getEntityClasses()
  {
    return entityClasses;
  }


  // IMPLEMENTATION
  @Override
  String getSignature()
  {
    return String.format("  class %1$s\n", getName());
  }

  @Override
  String getPhpDoc()
  {
    final StringBuffer stringBuffer=new StringBuffer(128);
    final String subPackageName=getSubPackageName();

    stringBuffer.append("  /**\n");
    stringBuffer.append(String.format("   * %1$s\n", getName()));

    if(null!=getPath())
    {
      stringBuffer.append("   *\n");
      stringBuffer.append(String.format("   * @Path(%1$s)\n", getPath()));
    }

    stringBuffer.append("   *\n");
    stringBuffer.append(String.format("   * @package %1$s\n", getPackageName()));

    if(null!=subPackageName)
    {
      stringBuffer.append(
        String.format("   * @subpackage %1$s\n", getSubPackageName())
      );
    }

    stringBuffer.append("   *\n");
    stringBuffer.append(
      String.format("   * @author %1$s\n", PhpClientApplication.DEFAULT_CLASS_AUTHOR)
    );

    stringBuffer.append("   */\n");

    return stringBuffer.toString();
  }
}
