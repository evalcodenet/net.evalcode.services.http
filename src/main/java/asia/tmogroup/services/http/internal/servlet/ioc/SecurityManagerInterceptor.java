package net.evalcode.services.http.internal.servlet.ioc;


import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Singleton;
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
@Singleton
public class SecurityManagerInterceptor implements MethodInterceptor
{
  // MEMBERS
  final Provider<Injector> provider;


  // CONSTRUCTION
  @Inject
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
      final HttpServletRequest httpServletRequest=provider.get()
        .getInstance(HttpServletRequest.class);

      for(final String role : rolesAllowed.value())
      {
        if(httpServletRequest.isUserInRole(role))
          return methodInvocation.proceed();
      }
    }

    provider.get().getInstance(HttpServletResponse.class)
      .sendError(HttpServletResponse.SC_FORBIDDEN);

    return null;
  }
}
