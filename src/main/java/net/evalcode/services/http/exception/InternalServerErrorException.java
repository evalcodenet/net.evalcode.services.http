package net.evalcode.services.http.exception;


import javax.ws.rs.core.Response;
import net.evalcode.services.http.internal.servlet.exception.WebApplicationServiceException;


/**
 * InternalServerErrorException
 *
 * @author carsten.schipke@gmail.com
 */
public class InternalServerErrorException extends WebApplicationServiceException
{
  // PREDEFINED PROPERTIES
  private static final long serialVersionUID=1L;


  // CONSTRUCTION
  public InternalServerErrorException(final String message)
  {
    this(message, null, true);
  }

  public InternalServerErrorException(final Throwable throwable)
  {
    this(throwable.getMessage(), throwable, true);
  }

  public InternalServerErrorException(final String message, final Throwable throwable)
  {
    this(message, throwable, true);
  }

  public InternalServerErrorException(final String message,
    final Throwable throwable, final boolean log)
  {
    super(Response.Status.INTERNAL_SERVER_ERROR, message, throwable, log);
  }
}
