package net.evalcode.services.http.service.servlet.security;


import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;
import net.evalcode.services.http.annotation.JaasRoles;


/**
 * ServletSecurityContext
 *
 * @author evalcode.net
 */
@Singleton
// TODO Reduce locking.
public class ServletSecurityContext implements SecurityContext
{
  // MEMBERS
  final Set<String> jaasRoles;
  final Provider<HttpServletRequest> httpServletRequest;

  String username;
  Set<String> roles;


  // CONSTRUCTION
  @Inject
  public ServletSecurityContext(final Provider<HttpServletRequest> httpServletRequest,
      @JaasRoles final Set<String> jaasRoles)
  {
    this.httpServletRequest=httpServletRequest;
    this.jaasRoles=jaasRoles;
  }


  // ACCESSORS/MUTATORS
  public synchronized void login(final HttpServletRequest httpServletRequest,
      final String username, final String password)
    throws ServletException
  {
    this.username=null;

    httpServletRequest.login(username, password);

    final Set<String> roles=new HashSet<>();

    for(final String role : jaasRoles)
    {
      if(httpServletRequest.isUserInRole(role))
        roles.add(role);
    }

    this.roles=roles;
    this.username=username;
  }

  public synchronized void logout()
  {
    username=null;
    roles=new HashSet<String>();
  }

  public synchronized boolean isLoggedIn()
  {
    return null!=username;
  }

  public synchronized Set<String> getUserRoles()
  {
    return Collections.unmodifiableSet(roles);
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public synchronized Principal getUserPrincipal()
  {
    return new Principal() {
      @Override
      public String getName()
      {
        return username;
      }
    };
  }

  @Override
  public synchronized boolean isUserInRole(final String role)
  {
    return roles.contains(role);
  }

  @Override
  public boolean isSecure()
  {
    return httpServletRequest.get().isSecure();
  }

  @Override
  public String getAuthenticationScheme()
  {
    return httpServletRequest.get().getAuthType();
  }
}
