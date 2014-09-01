package net.evalcode.services.http.internal.servlet.ioc;


import java.util.Set;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
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
      final HttpServletRequest httpServletRequest=provider.get().getInstance(HttpServletRequest.class);

      @SuppressWarnings("unchecked")
      final Set<String> roles=(Set<String>)httpServletRequest.getSession().getAttribute("roles");

      if(null!=roles)
      {
        for(final String role : rolesAllowed.value())
        {
          if(roles.contains(role))
            return methodInvocation.proceed();
        }
      }
    }

    provider.get().getInstance(HttpServletResponse.class)
      .sendError(403, "HTTP/1.1 403 Forbidden");

    return null;
  }
}
