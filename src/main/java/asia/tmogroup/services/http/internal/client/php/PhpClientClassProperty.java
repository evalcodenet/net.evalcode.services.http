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
  final String nameMapped;
  final String value;
  final String type;


  // CONSTRUCTION
  public PhpClientClassProperty(final String name, final String nameMapped, final String value)
  {
    this(name, nameMapped, value, null);
  }

  public PhpClientClassProperty(final String name, final String nameMapped, final String value,
    final String type)
  {
    this.name=name;
    this.nameMapped=nameMapped;
    this.value=value;
    this.type=type;
  }


  // ACCESSORS/MUTATORS
  public String getName()
  {
    return name;
  }

  public String getNameMapped()
  {
    return nameMapped;
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
    if(name==nameMapped && null==type)
      return "";

    final StringBuffer stringBuffer=new StringBuffer(32);

    stringBuffer.append("    /**\n");
    if(name!=nameMapped)
      stringBuffer.append(String.format("     * @name %1$s\n", nameMapped));
    if(null!=type)
      stringBuffer.append(String.format("     * @var %1$s\n", type));
    stringBuffer.append("     */\n");

    return stringBuffer.toString();
  }
}
