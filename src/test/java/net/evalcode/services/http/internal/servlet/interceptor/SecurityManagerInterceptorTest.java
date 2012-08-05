package net.evalcode.services.http.internal.servlet.interceptor;


import javax.annotation.security.DenyAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import net.evalcode.services.http.internal.servlet.interceptor.SecurityManagerInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.mockito.Mockito;
import com.google.inject.Injector;
import com.google.inject.Provider;


/**
 * Test {@link SecurityManagerInterceptor}
 *
 * @author carsten.schipke@gmail.com
 */
public class SecurityManagerInterceptorTest
{
  // TESTS
  @Test(expected=WebApplicationException.class)
  public void testMethodInvocation() throws Throwable
  {
    @SuppressWarnings("unchecked")
    final Provider<Injector> provider=Mockito.mock(Provider.class);
    final Injector injector=Mockito.mock(Injector.class);
    final HttpServletRequest httpServletRequest=Mockito.mock(HttpServletRequest.class);
    final HttpSession httpSession=Mockito.mock(HttpSession.class);

    Mockito.when(provider.get()).thenReturn(injector);
    Mockito.when(injector.getInstance(HttpServletRequest.class)).thenReturn(httpServletRequest);
    Mockito.when(httpServletRequest.getSession()).thenReturn(httpSession);

    final MethodInvocation methodInvocation=Mockito.mock(MethodInvocation.class);

    Mockito.when(methodInvocation.getMethod())
      .thenReturn(Foo.class.getMethod("bar", new Class<?> [] {}));

    final SecurityManagerInterceptor securityManagerInterceptor=
      new SecurityManagerInterceptor(provider);

    securityManagerInterceptor.invoke(methodInvocation);
  }


  /**
   * Foo
   *
   * @author carsten.schipke@gmail.com
   */
  static class Foo
  {
    // ACCESSORS/MUTATORS
    @DenyAll
    public void bar()
    {

    }
  }
}
