package net.evalcode.services.http.service.servlet;


import java.io.IOException;
import javax.annotation.security.PermitAll;
import javax.inject.Singleton;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Singleton
public class ErrorServlet extends HttpServlet
{
  // PREDEFINED PROPERTIES
  private static final long serialVersionUID=1L;


  // OVERRIDES/IMPLEMENTS
  @Override
  @PermitAll
  protected void doGet(final HttpServletRequest httpServletRequest,
      final HttpServletResponse httpServletResponse) throws IOException
  {
    httpServletResponse.getOutputStream().println("error");
  }
}
