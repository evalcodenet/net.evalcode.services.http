package net.evalcode.services.http.exception;


import javax.ws.rs.core.Response;


/**
 * ForbiddenException
 *
 * @author carsten.schipke@gmail.com
 */
public class ForbiddenException extends HttpException
{
  // PREDEFINED PROPERTIES
  static final long serialVersionUID=1L;


  // CONSTRUCTION
  public ForbiddenException(final String message)
  {
    this(message, null, true);
  }

  public ForbiddenException(final Throwable throwable)
  {
    this(throwable.getMessage(), throwable, true);
  }

  public ForbiddenException(final String message, final Throwable throwable)
  {
    this(message, throwable, true);
  }

  public ForbiddenException(final String message, final Throwable throwable, final boolean log)
  {
    super(Response.Status.FORBIDDEN, message, throwable, log);
  }
}
