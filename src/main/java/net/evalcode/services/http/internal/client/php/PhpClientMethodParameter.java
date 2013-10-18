package net.evalcode.services.http.internal.client.php;

import net.evalcode.services.http.internal.client.WebApplicationClientGeneratorPhp;




/**
 * PhpClientMethodParameter
 *
 * @author carsten.schipke@gmail.com
 */
public class PhpClientMethodParameter
{
  // MEMBERS
  final boolean assignMember;
  final boolean optional;
  final String name;
  final String type;
  final String typeHint;


  // CONSTRUCTION
  public PhpClientMethodParameter(final String name, final String type,
    final String typeHint, final boolean assignMember)
  {
    this(name, type, typeHint, assignMember, false);
  }

  public PhpClientMethodParameter(final String name, final String type,
    final String typeHint, final boolean assignMember, final boolean optional)
  {
    this.name=name;
    this.type=type;
    this.typeHint=typeHint;
    this.assignMember=assignMember;
    this.optional=optional;
  }


  // ACCESSORS/MUTATORS
  public String getName()
  {
    return name;
  }

  public String getType()
  {
    return type;
  }

  public String getTypeHint()
  {
    return typeHint;
  }

  public String getPhpDocType()
  {
    if(WebApplicationClientGeneratorPhp.PHP_COLLECTION_WRAPPER_TYPE_NAME.equals(getTypeHint()))
      return WebApplicationClientGeneratorPhp.PHP_COLLECTION_WRAPPER_TYPE_PHPDOC;

    if(null==getTypeHint())
      return getType();

    return getTypeHint();
  }

  public boolean assignMember()
  {
    return assignMember;
  }

  public boolean isOptional()
  {
    return optional;
  }

  public String getInitialValue()
  {
    if(!isOptional())
      return null;

    if(WebApplicationClientGeneratorPhp.PHP_COLLECTION_WRAPPER_TYPE_NAME.equals(getTypeHint()))
      return WebApplicationClientGeneratorPhp.PHP_COLLECTION_WRAPPER_TYPE_INITIALIZATION;

    return WebApplicationClientGeneratorPhp.PHP_DEFAULT_TYPE_INITIALIZATION;
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public String toString()
  {
    final StringBuilder stringBuilder=new StringBuilder();

    final String typeHint=getTypeHint();
    final String initialValue=getInitialValue();

    if(null!=typeHint)
    {
      stringBuilder.append(typeHint);
      stringBuilder.append(" ");
    }

    stringBuilder.append(String.format("$%1$s_", getName()));

    if(null!=initialValue)
    {
      stringBuilder.append("=");
      stringBuilder.append(initialValue);
    }

    return stringBuilder.toString();
  }
}
