package net.evalcode.services.http.internal.servlet.exception;


import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import net.evalcode.services.http.internal.xml.XmlError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * WebApplicationServiceException
 *
 * @author carsten.schipke@gmail.com
 */
public class WebApplicationServiceException extends WebApplicationException
{
  // PREDEFINED PROPERTIES
  private static final long serialVersionUID=1L;
  private static final Logger LOG=LoggerFactory.getLogger(WebApplicationServiceException.class);


  // CONSTRUCTION
  public WebApplicationServiceException(final Response.Status status,
    final String message, final Throwable throwable, final boolean log)
  {
    super(Response.status(status).entity(new XmlError(message, throwable)).build());

    if(log)
    {
      if(null==getCause())
        LOG.warn(getMessage(), this);
      else
        LOG.warn(getCause().getMessage(), getCause());
    }
  }
}
