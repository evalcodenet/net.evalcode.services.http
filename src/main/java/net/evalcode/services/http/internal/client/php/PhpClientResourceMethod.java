package net.evalcode.services.http.internal.client.php;


import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;


/**
 * PhpClientResourceMethod
 *
 * @author carsten.schipke@gmail.com
 */
public class PhpClientResourceMethod extends PhpClientMethod
{
  // MEMBERS
  final String path;
  final String httpMethod;


  // CONSTRUCTION
  public PhpClientResourceMethod(final PhpClientResourceClass type,
    final String name, final String httpMethod, final String path, final String returnType)
  {
    super(type, name, returnType);

    this.path=path;
    this.httpMethod=httpMethod;
  }


  // ACCESSORS/MUTATORS
  public String getPath()
  {
    return getPath(true);
  }

  public String getPath(final boolean full)
  {
    if(full)
    {
      if(null==path)
        return ((PhpClientResourceClass)getType()).getPath();

      return ((PhpClientResourceClass)getType()).getPath().concat(path);
    }

    return path;
  }

  public String getUrl()
  {
    if(null==path)
      return ((PhpClientResourceClass)getType()).getUrl();

    return ((PhpClientResourceClass)getType()).getUrl().concat(path);
  }

  public String getHttpMethod()
  {
    return httpMethod;
  }


  // IMPLEMENTATION
  @Override
  String getSignature()
  {
    return String.format("    public function %1$s(%2$s)\n",
      getName(),
      getSignatureParameters()
    );
  }

  @Override
  String getSignatureParameters()
  {
    final List<String> signatureParameters=new ArrayList<String>();

    for(final PhpClientMethodParameter parameter : getParameters())
      signatureParameters.add(parameter.toString());

    return StringUtils.join(signatureParameters.toArray(), ", ");
  }

  @Override
  String getPhpDoc()
  {
    final StringBuffer stringBuffer=new StringBuffer(256);

    stringBuffer.append("    /**\n");
    stringBuffer.append(String.format("     * @%1$s\n", httpMethod));

    if(null!=path)
      stringBuffer.append(String.format("     * @Path(%1$s)\n", path));

    if(0<getParameters().size())
    {
      if(null!=path)
        stringBuffer.append("     *\n");

      for(final PhpClientMethodParameter parameter : getParameters())
      {
        stringBuffer.append(String.format("     * @param %1$s $%2$s_\n",
          parameter.getPhpDocType(),
          parameter.getNameCamelCase()
        ));
      }
    }

    final String returnType=getReturnType();

    if(null!=returnType)
    {
      if(null!=path || 0<getParameters().size())
        stringBuffer.append("     *\n");

      stringBuffer.append(String.format("     * @return %1$s\n", returnType));
    }

    stringBuffer.append("     */\n");

    return stringBuffer.toString();
  }

  @Override
  String getBody()
  {
    return "\n      return $this->invoke(__METHOD__, func_get_args());\n";
  }
}
