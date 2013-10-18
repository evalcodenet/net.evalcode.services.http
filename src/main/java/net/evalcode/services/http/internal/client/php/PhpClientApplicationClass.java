package net.evalcode.services.http.internal.client.php;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import net.evalcode.services.http.internal.client.WebApplicationClientGeneratorPhp;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * PhpClientApplicationClass
 *
 * @author carsten.schipke@gmail.com
 */
public class PhpClientApplicationClass extends PhpClientClass
{
  // PREDEFINED PROPERTIES
  static final Logger LOG=LoggerFactory.getLogger(PhpClientApplicationClass.class);

  static final int BUFFER_SIZE=4096;

  static final String KEY_APPLICATION_NAME="%APPLICATION_NAME%";
  static final String KEY_BASE_URL="%BASE_URL%";

  static final String RESOURCE_TEMPLATE_PATH=
    "net/evalcode/services/http/internal/client/php/";
  static final String RESOURCE_TEMPLATE="PhpClientApplicationClass.php";


  // CONSTRUCTION
  public PhpClientApplicationClass(final PhpClientApplication application,
    final String name, final String fileName)
  {
    super(application, PhpClientApplicationClass.class, name, fileName);
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
      final byte[] buffer=new byte[BUFFER_SIZE];

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

    final String applicationName=String.format(
      WebApplicationClientGeneratorPhp.PATTERN_APPLICATION_ROOT_PATH, application.getName()
    );

    content=StringUtils.replace(
      content, KEY_CLASS_AUTHOR, PhpClientApplication.DEFAULT_CLASS_AUTHOR
    );
    content=StringUtils.replace(
      content, KEY_CLASS_PACKAGE, PhpClientApplication.DEFAULT_CLASS_PACKAGE
    );

    content=StringUtils.replace(content, KEY_APPLICATION_NAME, applicationName);
    content=StringUtils.replace(content, KEY_CLASS_NAME, getName());
    content=StringUtils.replace(content, KEY_BASE_URL, getApplication().getUrl());

    return content;
  }
}
