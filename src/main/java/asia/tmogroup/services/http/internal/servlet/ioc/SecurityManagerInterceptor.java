package net.evalcode.services.http.internal.servlet.ioc;


import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import net.evalcode.services.http.security.SecurityContext;
import com.google.inject.Injector;
import com.google.inject.Provider;


/**
 * SecurityManagerInterceptor
 *
 * @author carsten.schipke@gmail.com
 */
public class SecurityManagerInterceptor implements MethodInterceptor
{
  // MEMBERS
  private final Provider<Injector> provider;


  // CONSTRUCTION
  public SecurityManagerInterceptor(final Provider<Injector> provider)
  {
    this.provider=provider;
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public Object invoke(final MethodInvocation methodInvocation) throws Throwable
  {
    if(null!=methodInvocation.getMethod().getAnnotation(PermitAll.class))
      return methodInvocation.proceed();

    final RolesAllowed rolesAllowed=methodInvocation.getMethod().getAnnotation(RolesAllowed.class);

    if(null!=rolesAllowed)
    {
      final SecurityContext securityContext=provider.get().getInstance(SecurityContext.class);

      if(securityContext.isLoggedIn())
      {
        for(final String role : rolesAllowed.value())
        {
          if(securityContext.isUserInRole(role))
            return methodInvocation.proceed();
        }
      }
    }

    provider.get().getInstance(HttpServletResponse.class)
      .sendError(HttpServletResponse.SC_FORBIDDEN);

    return null;
  }
}
