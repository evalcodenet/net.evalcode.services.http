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
import javax.servlet.http.HttpServletResponse;
import net.evalcode.services.http.service.servlet.security.ServletSecurityContext;


/**
 * LoginServletFilter
 *
 * @author evalcode.net
 */
@Singleton
public class LoginServletFilter implements Filter
{
  // MEMBERS
  final Provider<ServletSecurityContext> securityContext;


  // CONSTRUCTION
  @Inject
  public LoginServletFilter(final Provider<ServletSecurityContext> securityContext)
  {
    this.securityContext=securityContext;
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public void doFilter(final ServletRequest servletRequest,
      final ServletResponse servletResponse, final FilterChain filterChain)
    throws IOException, ServletException
  {
    final HttpServletRequest httpServletRequest=(HttpServletRequest)servletRequest;
    final HttpServletResponse httpServletResponse=(HttpServletResponse)servletResponse;

    final String username=httpServletRequest.getHeader("Services-Username");
    final String password=httpServletRequest.getHeader("Services-Password");

    if(null!=username && null!=password)
      securityContext.get().login((HttpServletRequest)servletRequest, username, password);

    httpServletResponse.setHeader("Services-Authenticated",
      String.valueOf(securityContext.get().isLoggedIn())
    );

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
