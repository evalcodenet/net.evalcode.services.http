package net.evalcode.services.http.internal.client.php;


import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;


/**
 * PhpClientMethod
 *
 * @author carsten.schipke@gmail.com
 */
public class PhpClientMethod
{
  // PREDEFINED PROPERTIES
  static final String NAME_CONSTRUCTOR="__construct";


  // MEMBERS
  final List<PhpClientMethodParameter> parameters=new ArrayList<>();
  final String name;
  final String returnType;
  final PhpClientClass type;

  String body;


  // CONSTRUCTION
  public PhpClientMethod(final PhpClientClass type, final String name, final String returnType)
  {
    this.type=type;

    this.name=name;
    this.returnType=returnType;
  }


  // STATIC ACCESSORS
  public static PhpClientMethod createConstructor(final PhpClientClass type)
  {
    return new PhpClientMethod(type, NAME_CONSTRUCTOR, null);
  }


  // ACCESSORS/MUTATORS
  public PhpClientClass getType()
  {
    return type;
  }

  public String getName()
  {
    return name;
  }

  public String getReturnType()
  {
    return returnType;
  }

  public PhpClientMethodParameter addParameter(final PhpClientMethodParameter parameter)
  {
    parameters.add(parameter);

    return parameter;
  }

  public List<PhpClientMethodParameter> getParameters()
  {
    return parameters;
  }

  public void setBody(final String body)
  {
    this.body=body;
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public String toString()
  {
    final StringBuffer stringBuffer=new StringBuffer();

    stringBuffer.append(getPhpDoc());
    stringBuffer.append(getSignature());
    stringBuffer.append("    {");
    stringBuffer.append(getBody());
    stringBuffer.append("    }\n");

    return stringBuffer.toString();
  }


  // IMPLEMENTATION
  String getSignature()
  {
    return String.format("    public function %1$s(%2$s)\n",
      getName(), getSignatureParameters()
    );
  }

  String getSignatureParameters()
  {
    final List<String> signatureParameters=new ArrayList<String>();

    for(final PhpClientMethodParameter parameter : parameters)
      signatureParameters.add(parameter.toString());

    return StringUtils.join(signatureParameters.toArray(), ", ");
  }

  String getBody()
  {
    final StringBuffer stringBuffer=new StringBuffer();

    for(final PhpClientMethodParameter parameter : parameters)
    {
      if(parameter.assignMember())
        stringBuffer.append(String.format("      $this->m_%1$s=$%1$s_;\n", parameter.getNameCamelCase()));
    }

    if(null!=body)
      stringBuffer.append(body);

    return stringBuffer.toString();
  }

  String getPhpDoc()
  {
    final String returnType=getReturnType();

    if(null==returnType && 1>getParameters().size())
      return "";

    final StringBuffer stringBuffer=new StringBuffer(128);

    stringBuffer.append("    /**\n");

    if(0<getParameters().size())
    {
      for(final PhpClientMethodParameter parameter : getParameters())
      {
        stringBuffer.append(String.format("     * @param %1$s $%2$s_\n",
          parameter.getPhpDocType(), parameter.getNameCamelCase()
        ));
      }
    }

    if(null!=returnType)
    {
      if(0<getParameters().size())
        stringBuffer.append("     *\n");

      stringBuffer.append(String.format("     * @return %1$s\n", returnType));
    }

    stringBuffer.append("     */\n");

    return stringBuffer.toString();
  }
}
