package net.evalcode.services.http.internal.client.php;


/**
 * PhpClientResourceMethodParameter
 *
 * @author carsten.schipke@gmail.com
 */
public class PhpClientResourceMethodParameter extends PhpClientMethodParameter
{
  // MEMBERS
  final boolean isQueryParam;


  // CONSTRUCTION
  public PhpClientResourceMethodParameter(final String name, final String type,
    final String typeHint, final boolean isQueryParam)
  {
    super(name, type, typeHint, false, isQueryParam);

    this.isQueryParam=isQueryParam;
  }


  // ACCESSORS/MUTATORS
  public boolean isQueryParam()
  {
    return isQueryParam;
  }
}
