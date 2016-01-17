package net.evalcode.services.http.internal.servlet.ioc;


import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
  @Test
  public void testMethodInvocation() throws Throwable
  {
    @SuppressWarnings("unchecked")
    final Provider<Injector> provider=Mockito.mock(Provider.class);
    final Injector injector=Mockito.mock(Injector.class);
    final HttpServletRequest httpServletRequest=Mockito.mock(HttpServletRequest.class);
    final HttpServletResponse httpServletResponse=Mockito.mock(HttpServletResponse.class);
    final HttpSession httpSession=Mockito.mock(HttpSession.class);

    Mockito.when(provider.get()).thenReturn(injector);
    Mockito.when(injector.getInstance(HttpServletRequest.class)).thenReturn(httpServletRequest);
    Mockito.when(injector.getInstance(HttpServletResponse.class)).thenReturn(httpServletResponse);
    Mockito.when(httpServletRequest.getSession()).thenReturn(httpSession);

    final SecurityManagerInterceptor securityManagerInterceptor=
        new SecurityManagerInterceptor(provider);

    final MethodInvocation methodInvocation=Mockito.mock(MethodInvocation.class);

    Mockito.when(methodInvocation.proceed())
      .thenReturn(Boolean.TRUE);

    Mockito.when(methodInvocation.getMethod())
      .thenReturn(Foo.class.getMethod("deny", new Class<?> [] {}));

    assertNull(securityManagerInterceptor.invoke(methodInvocation));

    Mockito.when(methodInvocation.getMethod())
      .thenReturn(Foo.class.getMethod("permit", new Class<?> [] {}));

    assertSame(Boolean.TRUE, securityManagerInterceptor.invoke(methodInvocation));
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
    public void deny()
    {

    }

    @PermitAll
    public void permit()
    {

    }
  }
}
