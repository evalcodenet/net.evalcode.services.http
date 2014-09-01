package net.evalcode.services.http.security;


import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import net.evalcode.services.http.annotation.Roles;


/**
 * SecurityContext
 *
 * @author evalcode.net
 */
@Singleton
public final class SecurityContext
{
  // MEMBERS
  final Set<String> rolesAvailable;
  final AtomicReference<Set<String>> roles=new AtomicReference<>();
  final AtomicReference<String> name=new AtomicReference<>();


  // CONSTRUCTION
  @Inject
  public SecurityContext(@Roles final Set<String> rolesAvailable)
  {
    this.rolesAvailable=rolesAvailable;
  }


  // ACCESSORS
  // TODO Verify whether we need synchronization here.
  public void login(final HttpServletRequest httpServletRequest, final String username, final String password)
    throws ServletException
  {
    name.set(null);

    httpServletRequest.login(username, password);

    final Set<String> tRoles=new HashSet<>();

    for(final String role : rolesAvailable)
    {
      if(httpServletRequest.isUserInRole(role))
        tRoles.add(role);
    }

    roles.set(tRoles);
    name.set(username);
  }

  public void logout()
  {
    name.set(null);
  }

  public boolean isLoggedIn()
  {
    return null!=name.get();
  }

  public Principal getUserPrincipal()
  {
    return new Principal() {
      @Override
      public String getName()
      {
        return name.get();
      }
    };
  }

  public boolean isUserInRole(final String role)
  {
    return roles.get().contains(role);
  }

  public Set<String> getUserRoles()
  {
    return Collections.unmodifiableSet(roles.get());
  }
}
