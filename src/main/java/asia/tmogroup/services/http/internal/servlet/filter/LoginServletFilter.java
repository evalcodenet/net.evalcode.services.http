package net.evalcode.services.http.internal.servlet.filter;


import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import net.evalcode.services.http.security.SecurityContext;


/**
 * LoginServletFilter
 *
 * @author evalcode.net
 */
@Singleton
public class LoginServletFilter implements Filter
{
  // MEMBERS
  final Provider<SecurityContext> securityContext;


  // CONSTRUCTION
  @Inject
  public LoginServletFilter(final Provider<SecurityContext> securityContext)
  {
    this.securityContext=securityContext;
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public void doFilter(final ServletRequest servletRequest,
      final ServletResponse servletResponse, final FilterChain filterChain)
    throws IOException, ServletException
  {
    final String username=((HttpServletRequest)servletRequest).getHeader("Username");
    final String password=((HttpServletRequest)servletRequest).getHeader("Password");

    if(null!=username && null!=password)
      securityContext.get().login((HttpServletRequest)servletRequest, username, password);

    filterChain.doFilter(servletRequest, servletResponse);
  }

  @Override
  public void init(final FilterConfig filterConfig) throws ServletException
  {
    // Do nothing ...
  }

  @Override
  public void destroy()
  {
    // Do nothing ...
  }
}
