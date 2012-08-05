package net.evalcode.services.http.exception;


import javax.ws.rs.core.Response;
import net.evalcode.services.http.internal.servlet.exception.WebApplicationServiceException;


/**
 * NotFoundException
 *
 * @author carsten.schipke@gmail.com
 */
public class NotFoundException extends WebApplicationServiceException
{
  // PREDEFINED PROPERTIES
  private static final long serialVersionUID=1L;


  // CONSTRUCTION
  public NotFoundException(final String message)
  {
    this(message, null, true);
  }

  public NotFoundException(final Throwable throwable)
  {
    this(throwable.getMessage(), throwable, true);
  }

  public NotFoundException(final String message, final Throwable throwable)
  {
    this(message, throwable, true);
  }

  public NotFoundException(final String message, final Throwable throwable, final boolean log)
  {
    super(Response.Status.NOT_FOUND, message, throwable, log);
  }
}
