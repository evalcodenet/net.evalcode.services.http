package net.evalcode.services.http.internal.client.php;


import java.util.ArrayList;
import java.util.List;
import net.evalcode.services.http.internal.client.WebApplicationClientGeneratorPhp;


/**
 * PhpClientResourceClass
 *
 * @author carsten.schipke@gmail.com
 */
public class PhpClientResourceClass extends PhpClientClass
{
  // MEMBERS
  private final String path;
  private final List<PhpClientClass> entityClasses=new ArrayList<PhpClientClass>();


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
      return application.getPath().concat(path);

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

    final String applicationName=String.format(
      WebApplicationClientGeneratorPhp.PATTERN_APPLICATION_ROOT_PATH, application.getName()
    );

    stringBuffer.append("  /**\n");
    stringBuffer.append(String.format("   * %1$s\n", getName()));

    if(null!=getPath())
    {
      stringBuffer.append("   *\n");
      stringBuffer.append(String.format("   * uri: %1$s\n", getPath()));
    }

    stringBuffer.append("   *\n");
    stringBuffer.append(String.format("   * @package %1$s\n", applicationName));
    stringBuffer.append(
      String.format("   * @subpackage %1$s\n", PhpClientApplication.DEFAULT_CLASS_PACKAGE)
    );
    stringBuffer.append("   *\n");
    stringBuffer.append(
      String.format("   * @author %1$s\n", PhpClientApplication.DEFAULT_CLASS_AUTHOR)
    );

    stringBuffer.append("   */\n");

    return stringBuffer.toString();
  }
}
