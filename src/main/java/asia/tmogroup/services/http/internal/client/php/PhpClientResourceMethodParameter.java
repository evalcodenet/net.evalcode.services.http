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
  public PhpClientResourceMethodParameter(final String name, final Class<?> type,
    final String typeHint, final String typeDoc, final String defaultValue,
    final boolean isQueryParam)
  {
    super(name, type, typeHint, typeDoc, defaultValue, false, isQueryParam);

    this.isQueryParam=isQueryParam;
  }


  // ACCESSORS/MUTATORS
  public boolean isQueryParam()
  {
    return isQueryParam;
  }

  public String getPhpDoc()
  {
    final StringBuilder stringBuilder=new StringBuilder();
    final String nameCamelCase=getNameCamelCase();

    stringBuilder.append("/** @");

    if(isQueryParam)
      stringBuilder.append("QueryParam");
    else
      stringBuilder.append("PathParam");

    final boolean hasArgs=null==typeHint || !name.equals(nameCamelCase);

    if(hasArgs)
      stringBuilder.append("(");

    if(isQueryParam && !name.equals(nameCamelCase))
    {
      stringBuilder.append("name=");
      stringBuilder.append(name);

      if(null==typeHint)
        stringBuilder.append(", ");
    }

    if(null==typeHint)
    {
      stringBuilder.append("type=");
      stringBuilder.append(typeDoc);
    }

    if(hasArgs)
      stringBuilder.append(")");

    stringBuilder.append(" */");

    return stringBuilder.toString();
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public String toString()
  {
    final StringBuilder stringBuilder=new StringBuilder();

    final String typeHint=getTypeHint();
    final String defaultValue=getDefaultValue();

    final String nameCamelCase=getNameCamelCase();
    final String doc=getPhpDoc();

    stringBuilder.append(doc);
    stringBuilder.append(" ");

    if(null!=typeHint)
    {
      stringBuilder.append(typeHint);
      stringBuilder.append(" ");
    }

    stringBuilder.append(String.format("$%1$s_", nameCamelCase));

    if(null!=defaultValue)
    {
      stringBuilder.append("=");
      stringBuilder.append(defaultValue);
    }

    return stringBuilder.toString();
  }
}
