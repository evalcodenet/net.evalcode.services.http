package net.evalcode.services.http.exception;


import javax.ws.rs.core.Response;


/**
 * BadRequestException
 *
 * @author carsten.schipke@gmail.com
 */
public class BadRequestException extends HttpException
{
  // PREDEFINED PROPERTIES
  static final long serialVersionUID=1L;


  // CONSTRUCTION
  public BadRequestException(final String message)
  {
    this(message, null, true);
  }

  public BadRequestException(final Throwable throwable)
  {
    this(throwable.getMessage(), throwable, true);
  }

  public BadRequestException(final String message, final Throwable throwable)
  {
    this(message, throwable, true);
  }

  public BadRequestException(final String message, final Throwable throwable, final boolean log)
  {
    super(Response.Status.BAD_REQUEST, message, throwable, log);
  }
}
