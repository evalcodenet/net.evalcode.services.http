package net.evalcode.services.http.service.servlet;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.evalcode.services.http.annotation.Roles;


@Singleton
public class LoginServlet extends HttpServlet
{
  // PREDEFINED PROPERTIES
  private static final long serialVersionUID=1L;

  private static final Logger LOG=LoggerFactory.getLogger(LoginServlet.class);


  // MEMBERS
  final Set<String> roles;


  // CONSTRUCTION
  @Inject
  public LoginServlet(@Roles final Set<String> roles)
  {
    this.roles=roles;
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  @PermitAll
  protected void doGet(final HttpServletRequest httpServletRequest,
      final HttpServletResponse httpServletResponse) throws IOException
  {
    final Set<String> userRoles=new HashSet<>();

    try
    {
      httpServletRequest.login(
        httpServletRequest.getParameter("u"),
        httpServletRequest.getParameter("p")
      );

      for(final String role : roles)
      {
        if(httpServletRequest.isUserInRole(role))
          userRoles.add(role);
      }

      httpServletRequest.getSession().setAttribute("roles", userRoles);
    }
    catch(final ServletException e)
    {
      LOG.error(e.getMessage(), e);
    }

    LOG.info("Principal: {}", httpServletRequest.getUserPrincipal());
    LOG.info("Session Id: {}", httpServletRequest.getSession().getId());
    LOG.info("Roles: {}", userRoles);

    httpServletResponse.getOutputStream().println(userRoles.toString());
  }
}
