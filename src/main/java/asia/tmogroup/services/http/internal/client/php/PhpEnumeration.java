package net.evalcode.services.http.internal.client.php;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import net.evalcode.services.http.internal.client.WebApplicationClientGeneratorPhp;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * PhpEnumeration
 *
 * @author carsten.schipke@gmail.com
 */
public class PhpEnumeration extends PhpClientClass
{
  // PREDEFINED PROPERTIES
  static final Logger LOG=LoggerFactory.getLogger(PhpEnumeration.class);

  static final String RESOURCE_TEMPLATE_PATH=
    "asia/tmogroup/services/http/internal/client/php/";
  static final String RESOURCE_TEMPLATE="PhpEnumeration.php";

  static final String KEY_NAMESPACE="NAMESPACE";
  static final String KEY_TYPE="TYPE";
  static final String KEY_DOC="DOC";
  static final String KEY_AUTHOR="AUTHOR";
  static final String KEY_PACKAGE="PACKAGE";
  static final String KEY_SUBPACKAGE="SUBPACKAGE";
  static final String KEY_PROPERTIES="PROPERTIES";
  static final String KEY_VALUES="VALUES";


  // CONSTRUCTION
  public PhpEnumeration(final PhpClientApplication application,
    final Class<?> clazz, final String name, final String fileName)
  {
    super(application, clazz, name, fileName);
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public String toString()
  {
    String content="";

    final URL url=Thread.currentThread().getContextClassLoader().getResource(
      RESOURCE_TEMPLATE_PATH.concat(RESOURCE_TEMPLATE)
    );

    if(null==url)
      return content;

    try
    {
      final InputStream inputStream=url.openStream();

      int read=0;
      final byte[] buffer=new byte[4096];

      while(-1<(read=inputStream.read(buffer)))
      {
        content=content.concat(
          Charset.defaultCharset().decode(ByteBuffer.wrap(buffer, 0, read)).toString()
        );
      }
    }
    catch(final IOException e)
    {
      LOG.warn(e.getMessage(), e);
    }

    content=StringUtils.replace(content, "%"+KEY_NAMESPACE+"%",
      WebApplicationClientGeneratorPhp.NAMESPACE
    );
    content=StringUtils.replace(content, "%"+KEY_TYPE+"%", getName());
    content=StringUtils.replace(content, "%"+KEY_AUTHOR+"%",
      PhpClientApplication.DEFAULT_CLASS_AUTHOR
    );
    content=StringUtils.replace(content, "%"+KEY_PACKAGE+"%", getPackageName());
    content=StringUtils.replace(content, "%"+KEY_SUBPACKAGE+"%", getSubPackageName());

    final List<String> doc=new ArrayList<>();
    for(final PhpClientClassConstant constant : constants)
    {
      doc.add(String.format("   * @method \\%1$s\\%2$s %3$s",
        WebApplicationClientGeneratorPhp.NAMESPACE, getName(), constant.name
        ));
    }

    final List<String> properties=new ArrayList<>();
    for(final PhpClientClassConstant constant : constants)
    {
      properties.add(String.format("    const %1$s=%2$s;",
        constant.getName(), constant.getValue()
      ));
    }

    final List<String> values=new ArrayList<>();
    for(final PhpClientClassConstant constant : constants)
      values.add(String.format("      self::%1$s=>'%1$s'", constant.name));

    content=StringUtils.replace(content, "%"+KEY_DOC+"%", StringUtils.join(doc.toArray(), "\n"));
    content=StringUtils.replace(content, "%"+KEY_PROPERTIES+"%",
      StringUtils.join(properties.toArray(), "\n")
    );
    content=StringUtils.replace(content, "%"+KEY_VALUES+"%",
      StringUtils.join(values.toArray(), ",\n")
    );

    return content;
  }
}
