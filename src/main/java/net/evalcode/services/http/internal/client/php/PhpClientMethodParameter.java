package net.evalcode.services.http.internal.client.php;


import net.evalcode.services.http.annotation.client.WebApplicationClientType;


/**
 * PhpClientMethodParameter
 *
 * @author carsten.schipke@gmail.com
 */
public class PhpClientMethodParameter
{
  // PREDEFINED PROPERTIES
  static final char[] MAP_TO_CAMELCASE=new char[] {
    '-',
    '_'
  };


  // MEMBERS
  final boolean assignMember;
  final boolean optional;
  final Class<?> type;
  final String name;
  final String typeHint;
  final String typeDoc;
  final String defaultValue;


  // CONSTRUCTION
  public PhpClientMethodParameter(final String name, final  Class<?> type,
    final String typeHint, final String typeDoc, final String defaultValue,
    final boolean assignMember)
  {
    this(name, type, typeHint, typeDoc, defaultValue, assignMember, false);
  }

  public PhpClientMethodParameter(final String name, final  Class<?> type,
    final String typeHint, final String typeDoc, final String defaultValue,
    final boolean assignMember, final boolean optional)
  {
    this.name=name;
    this.type=type;
    this.typeHint=typeHint;
    this.typeDoc=typeDoc;
    this.defaultValue=defaultValue;
    this.assignMember=assignMember;
    this.optional=optional;
  }


  // ACCESSORS/MUTATORS
  public String getName()
  {
    return name;
  }

  public String getNameCamelCase()
  {
    final StringBuffer stringBuffer=new StringBuffer(name.length());

    boolean uppercase=false;

    for(final byte b : name.getBytes())
    {
      final char c=(char)b;

      if(uppercase)
      {
        stringBuffer.append(Character.toUpperCase(c));
        uppercase=false;

        continue;
      }

      for(final char map : MAP_TO_CAMELCASE)
      {
        if(c==map)
        {
          uppercase=true;

          break;
        }
      }

      if(uppercase)
        continue;

      stringBuffer.append(c);
    }

    return stringBuffer.toString();
  }

  public String getType()
  {
    final String clazzName=type.getSimpleName();

    if(null!=type.getAnnotation(WebApplicationClientType.class))
      return type.getAnnotation(WebApplicationClientType.class).value();

    return clazzName;
  }

  public String getTypeHint()
  {
    return typeHint;
  }

  public String getPhpDocType()
  {
    return typeDoc;
  }

  public boolean assignMember()
  {
    return assignMember;
  }

  public boolean isOptional()
  {
    return optional;
  }

  public String getDefaultValue()
  {
    return defaultValue;
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public String toString()
  {
    final StringBuilder stringBuilder=new StringBuilder();

    final String typeHint=getTypeHint();
    final String defaultValue=getDefaultValue();

    if(null!=typeHint)
    {
      stringBuilder.append(typeHint);
      stringBuilder.append(" ");
    }

    stringBuilder.append(String.format("$%1$s_", getNameCamelCase()));

    if(null!=defaultValue)
    {
      stringBuilder.append("=");
      stringBuilder.append(defaultValue);
    }

    return stringBuilder.toString();
  }
}
