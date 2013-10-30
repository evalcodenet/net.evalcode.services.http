package net.evalcode.services.http.exception;


import javax.ws.rs.core.Response;


/**
 * ServiceUnavailableException
 *
 * @author carsten.schipke@gmail.com
 */
public class ServiceUnavailableException extends HttpException
{
  // PREDEFINED PROPERTIES
  static final long serialVersionUID=1L;


  // CONSTRUCTION
  public ServiceUnavailableException(final String message)
  {
    this(message, null, true);
  }

  public ServiceUnavailableException(final Throwable throwable)
  {
    this(throwable.getMessage(), throwable, true);
  }

  public ServiceUnavailableException(final String message, final Throwable throwable)
  {
    this(message, throwable, true);
  }

  public ServiceUnavailableException(final String message,
    final Throwable throwable, final boolean log)
  {
    super(Response.Status.SERVICE_UNAVAILABLE, message, throwable, log);
  }
}
