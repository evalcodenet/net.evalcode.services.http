package net.evalcode.services.http.internal.client.php;


/**
 * PhpClientClassConstant
 *
 * @author carsten.schipke@gmail.com
 */
public class PhpClientClassConstant extends PhpClientClassProperty
{
  // CONSTRUCTION
  public PhpClientClassConstant(final String name, final String value)
  {
    super(name, name, value);
  }

  public PhpClientClassConstant(final String name, final String value, final String type)
  {
    super(name, name, value, type);
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public String toString()
  {
    return String.format("    const %1$s=%2$s;\n", getName(), getValue());
  }
}
