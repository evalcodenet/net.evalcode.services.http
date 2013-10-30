package net.evalcode.services.http.exception;


import javax.ws.rs.core.Response;
import net.evalcode.services.http.internal.servlet.exception.WebApplicationServiceException;


/**
 * HttpException
 *
 * @author carsten.schipke@gmail.com
 */
public abstract class HttpException extends WebApplicationServiceException
{
  // PREDEFINED PROPERTIES
  static final long serialVersionUID=1L;


  // CONSTRUCTION
  public HttpException(final Response.Status status, final String message)
  {
    this(status, message, null, true);
  }

  public HttpException(final Response.Status status, final Throwable throwable)
  {
    this(status, throwable.getMessage(), throwable, true);
  }

  public HttpException(final Response.Status status, final String message,
    final Throwable throwable)
  {
    this(status, message, throwable, true);
  }

  public HttpException(final Response.Status status, final String message,
    final Throwable throwable, final boolean log)
  {
    super(status, message, throwable, log);
  }
}
