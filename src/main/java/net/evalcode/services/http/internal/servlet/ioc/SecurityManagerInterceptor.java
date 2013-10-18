package net.evalcode.services.http.internal.servlet.ioc;


import javax.annotation.security.DenyAll;
import javax.servlet.http.HttpServletRequest;
import net.evalcode.services.http.exception.ForbiddenException;
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
    provider.get().getInstance(HttpServletRequest.class).getSession();

    if(null!=methodInvocation.getMethod().getAnnotation(DenyAll.class))
    {
      throw new ForbiddenException(String.format(
        "Method invocation of %1$s is not allowed.", methodInvocation.getMethod().getName()
      ));
    }

    // TODO Implement @RolesAllowed to enforce authentication by declaration

    return methodInvocation.proceed();
  }
}
