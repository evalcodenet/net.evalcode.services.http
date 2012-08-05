package net.evalcode.services.http.internal.client.php;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;


/**
 * PhpClientResourceMethod
 *
 * @author carsten.schipke@gmail.com
 */
public class PhpClientResourceMethod extends PhpClientMethod
{
  // MEMBERS
  private final String path;


  // CONSTRUCTION
  public PhpClientResourceMethod(final PhpClientResourceClass type,
    final String name, final String path, final String returnType)
  {
    super(type, name, returnType);

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
      return ((PhpClientResourceClass)getType()).getPath().concat(path);

    return path;
  }

  public String getUrl()
  {
    return ((PhpClientResourceClass)getType()).getUrl().concat(path);
  }


  // IMPLEMENTATION
  @Override
  String getSignature()
  {
    return String.format("    public function %1$s(%2$s)\n",
      getName(), getSignatureParameters()
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
    final String path=getPath();
    final String returnType=getReturnType();

    if(null==path && null==returnType && 1>getParameters().size())
      return "";

    final StringBuffer stringBuffer=new StringBuffer();

    stringBuffer.append("    /**\n");

    if(null!=path)
      stringBuffer.append(String.format("     * uri: %1$s\n", path));

    if(0<getParameters().size())
    {
      if(null!=path)
        stringBuffer.append("     *\n");

      for(final PhpClientMethodParameter parameter : getParameters())
      {
        stringBuffer.append(String.format("     * @param %1$s $%2$s_\n",
          parameter.getPhpDocType(), parameter.getName()
        ));
      }
    }

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
    final StringBuffer stringBuffer=new StringBuffer();
    final String applicationClassName=getType().getApplication().getApplicationClass().getName();

    stringBuffer.append(
      String.format("      $url=$this->m_client->getBaseUrl();\n", applicationClassName)
    );

    final String[] pathSegments=StringUtils.split(
      ((PhpClientResourceClass)getType()).getPath(false).concat(path), "/"
    );

    final Map<String, PhpClientResourceMethodParameter> pathParameters=
      new HashMap<String, PhpClientResourceMethodParameter>();

    for(final PhpClientMethodParameter parameter : getParameters())
    {
      if(!(parameter instanceof PhpClientResourceMethodParameter))
        continue;

      if(((PhpClientResourceMethodParameter)parameter).isQueryParam())
      {
        stringBuffer.append(
          String.format("      $url->setQueryParam('%1$s', $%1$s_);\n", parameter.getName())
        );
      }
      else
      {
        pathParameters.put(((PhpClientResourceMethodParameter)parameter).getName(),
          (PhpClientResourceMethodParameter)parameter
        );
      }
    }

    for(final String pathSegment : pathSegments)
    {
      if(pathParameters.containsKey(pathSegment.substring(1, pathSegment.length()-1)))
      {
        stringBuffer.append(
          String.format("      $url->pushPathParam($%1$s_);\n",
            pathSegment.substring(1, pathSegment.length()-1)
        ));
      }
      else
      {
        stringBuffer.append(
          String.format("      $url->pushPathParam('%1$s');\n", pathSegment)
        );
      }
    }

    if(null==getReturnType())
    {
      stringBuffer.append(
        String.format("\n      return $this->m_client->resolveUrl($url);\n", applicationClassName)
      );
    }
    else
    {
      stringBuffer.append(
        String.format("\n      return $this->m_client->resolveUrl($url, '%2$s');\n",
          applicationClassName, getReturnType()
      ));
    }

    return stringBuffer.toString();
  }
}
