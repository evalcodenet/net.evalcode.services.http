package net.evalcode.services.http.internal.servlet.exception;


import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import net.evalcode.services.http.internal.xml.XmlError;
import com.sun.jersey.api.NotFoundException;


/**
 * NotFoundExceptionMapper
 *
 * @author carsten.schipke@gmail.com
 */
@Provider
@Singleton
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException>
{
  // OVERRIDES/IMPLEMENTS
  @Override
  public Response toResponse(final NotFoundException e)
  {
    return Response.status(Response.Status.NOT_FOUND)
      .entity(new XmlError(e))
      .build();
  }
}
