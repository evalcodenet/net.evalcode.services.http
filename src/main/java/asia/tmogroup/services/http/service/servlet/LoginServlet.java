package net.evalcode.services.http.service.servlet;


import java.io.IOException;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.evalcode.services.http.service.servlet.security.ServletSecurityContext;


/**
 * LoginServlet
 *
 * @author evalcode.net
 */
@Singleton
public class LoginServlet extends HttpServlet
{
  // PREDEFINED PROPERTIES
  private static final long serialVersionUID=1L;


  // MEMBERS
  final Provider<ServletSecurityContext> securityContext;


  // CONSTRUCTION
  @Inject
  public LoginServlet(final Provider<ServletSecurityContext> securityContext)
  {
    this.securityContext=securityContext;
  }


  // IMPLEMENTATION
  @Override
  @PermitAll
  protected void doGet(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    throws ServletException, IOException
  {
    httpServletResponse.getWriter().println(
      "<!doctype html>"+
      "<html>"+
      "<head>"+
      "<title>Login</title>"+
      "</head>"+
      "<body>"+
      "<h1>Login</h1>"+
      "<form method=\"POST\">"+
      "<label for=\"services-username\">Username</label>"+
      "<input type=\"text\" name=\"services_username\" id=\"services-username\"/>"+
      "<label for=\"services-password\">Password</label>"+
      "<input type=\"password\" name=\"services_password\" id=\"services-password\"/>"+
      "<button type=\"submit\">Submit</button>"+
      "</form>"+
      "</body>"+
      "</html>"
    );
  }

  @Override
  @PermitAll
  protected void doPost(final HttpServletRequest httpServletRequest,
      final HttpServletResponse httpServletResponse)
    throws ServletException, IOException
  {
    final String username=httpServletRequest.getParameter("services_username");
    final String password=httpServletRequest.getParameter("services_password");

    if(null!=username && null!=password)
      securityContext.get().login(httpServletRequest, username, password);

    httpServletResponse.getOutputStream().println(securityContext.get().getUserRoles().toString());
  }
}
