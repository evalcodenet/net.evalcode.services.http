package net.evalcode.services.http.internal.servlet.interceptor;


import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import net.evalcode.services.http.annotation.Transactional;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import com.google.inject.Injector;
import com.google.inject.Provider;


/**
 * TransactionManagerInterceptor
 *
 * @author carsten.schipke@gmail.com
 */
public class TransactionManagerInterceptor implements MethodInterceptor
{
  // MEMBERS
  private final Provider<Injector> provider;


  // CONSTRUCTION
  public TransactionManagerInterceptor(final Provider<Injector> provider)
  {
    this.provider=provider;
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public Object invoke(final MethodInvocation methodInvocation) throws Throwable
  {
    EntityTransaction transaction=null;

    if(null!=methodInvocation.getMethod().getAnnotation(Transactional.class))
    {
      transaction=provider.get().getInstance(EntityManager.class).getTransaction();

      transaction.begin();
    }

    try
    {
      return methodInvocation.proceed();
    }
    catch(final Exception e)
    {
      if(null!=transaction)
        transaction.rollback();

      throw e;
    }
    finally
    {
      if(null!=transaction && transaction.isActive())
        transaction.commit();
    }
  }
}
