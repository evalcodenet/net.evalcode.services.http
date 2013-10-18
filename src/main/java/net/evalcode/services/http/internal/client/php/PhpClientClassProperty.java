package net.evalcode.services.http.internal.client.php;


/**
 * PhpClientClassProperty
 *
 * @author carsten.schipke@gmail.com
 */
public class PhpClientClassProperty
{
  // MEMBERS
  final String name;
  final String value;
  final String type;


  // CONSTRUCTION
  public PhpClientClassProperty(final String name, final String value)
  {
    this(name, value, null);
  }

  public PhpClientClassProperty(final String name, final String value, final String type)
  {
    this.name=name;
    this.value=value;
    this.type=type;
  }


  // ACCESSORS/MUTATORS
  public String getName()
  {
    return name;
  }

  public String getValue()
  {
    return value;
  }

  public String getType()
  {
    return type;
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public String toString()
  {
    if(null==getValue())
      return String.format("%2$s    public $%1$s;\n", getName(), getPhpDoc());

    return String.format("%3$s    public $%1$s=%2$s;\n", getName(), getValue(), getPhpDoc());
  }


  // IMPLEMENTATION
  String getPhpDoc()
  {
    if(null==getType())
      return "";

    return String.format("    /**\n     * @var %2$s\n     */\n", getName(), getType());
  }
}
