package net.evalcode.services.http.internal.servlet.ioc;


import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import net.evalcode.services.http.annotation.Transactional;
import net.evalcode.services.http.internal.servlet.ioc.TransactionManagerInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.mockito.Mockito;
import com.google.inject.Injector;
import com.google.inject.Provider;


/**
 * Test {@link TransactionManagerInterceptor}
 *
 * @author carsten.schipke@gmail.com
 */
public class TransactionManagerInterceptorTest
{
  // TESTS
  @Test
  public void testMethodInvocation() throws Throwable
  {
    @SuppressWarnings("unchecked")
    final Provider<Injector> provider=Mockito.mock(Provider.class);
    final Injector injector=Mockito.mock(Injector.class);
    final EntityManager entityManager=Mockito.mock(EntityManager.class);
    final EntityTransaction entityTransaction=Mockito.mock(EntityTransaction .class);

    Mockito.when(provider.get()).thenReturn(injector);
    Mockito.when(injector.getInstance(EntityManager.class)).thenReturn(entityManager);
    Mockito.when(entityManager.getTransaction()).thenReturn(entityTransaction);

    final MethodInvocation methodInvocation=Mockito.mock(MethodInvocation.class);

    Mockito.when(methodInvocation.getMethod())
      .thenReturn(Foo.class.getMethod("bar", new Class<?> [] {}));

    final TransactionManagerInterceptor transactionManagerInterceptor=
      new TransactionManagerInterceptor(provider);

    // test begin + commit
    Mockito.when(entityTransaction.isActive()).thenReturn(true);
    transactionManagerInterceptor.invoke(methodInvocation);
    Mockito.verify(entityTransaction, Mockito.times(1)).commit();

    // test begin + rollback
    Mockito.when(methodInvocation.proceed()).thenThrow(new RuntimeException());

    try
    {
      transactionManagerInterceptor.invoke(methodInvocation);
    }
    catch(final Throwable t)
    {

    }

    Mockito.verify(entityTransaction, Mockito.times(2)).begin();
    Mockito.verify(entityTransaction, Mockito.times(1)).rollback();
  }


  /**
   * Foo
   *
   * @author carsten.schipke@gmail.com
   */
  static class Foo
  {
    // ACCESSORS/MUTATORS
    @Transactional
    public void bar()
    {

    }
  }
}
