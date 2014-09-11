package net.evalcode.services.http.service.security;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.eclipse.jetty.http.security.Credential;
import org.eclipse.jetty.plus.jaas.spi.AbstractLoginModule;
import org.eclipse.jetty.plus.jaas.spi.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.evalcode.services.http.service.security.JaasSecurityContext.ServiceUserPrincipal;
import net.evalcode.services.manager.service.cache.impl.ehcache.EhcacheCache;
import net.evalcode.services.manager.service.cache.impl.ehcache.EhcacheCacheManagerFactory;
import net.evalcode.services.manager.service.cache.spi.Cache;
import com.google.common.collect.Lists;


/**
 * JaasTokenLoginModule
 *
 * @author evalcode.net
 */
public class JaasTokenLoginModule extends AbstractLoginModule
{
  // PREDEFINED PROPERTIES
  static final Logger LOG=LoggerFactory.getLogger(JaasTokenLoginModule.class);


  // MEMBERS
  // FIXME Bind service offered by net.evalcode.services.manager.
  final CacheManager cacheManager=EhcacheCacheManagerFactory.get().getCacheManager();
  // TODO Define/share region by security realm.
  final Ehcache cache=cacheManager.getEhcache(JaasSecurityContext.STORAGE_REGION);

  final Map<String, String> config=new HashMap<>();


  // OVERRIDES/IMPLEMENTS
  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void initialize(final Subject subject, final CallbackHandler callbackHandler,
      final Map sharedState, final Map options)
  {
    super.initialize(subject, callbackHandler, sharedState, options);

    config.putAll(options);
  }

  @Override
  public boolean login() throws LoginException
  {
    setAuthenticated(false);

    final NameCallback callbackName=new NameCallback("Username");

    try
    {
      getCallbackHandler().handle(new Callback[] {callbackName});
    }
    catch(final UnsupportedCallbackException e)
    {
      LOG.error(e.getMessage(), e);

      throw new LoginException(e.getMessage());
    }
    catch(final IOException e)
    {
      LOG.error(e.getMessage(), e);

      throw new LoginException(e.getMessage());
    }

    final Object result=getStorage().get(callbackName.getName());

    if(null!=result)
    {
      if(result instanceof ServiceUserPrincipal)
      {
        final ServiceUserPrincipal serviceUserPrincipal=(ServiceUserPrincipal)result;

        setCurrentUser(new JAASUserInfo(new UserInfo(
          serviceUserPrincipal.getName(),
          Credential.getCredential(serviceUserPrincipal.getToken()),
          Lists.newArrayList(serviceUserPrincipal.getRoles())
        )));

        setAuthenticated(true);

        LOG.info("Authenticated [principal: {}, roles: {}].",
          serviceUserPrincipal.getName(),
          serviceUserPrincipal.getRoles()
        );

        return true;
      }
    }

    return false;
  }

  @Override
  public UserInfo getUserInfo(final String user)
  {
    return null;
  }


  // IMPLEMENTATION
  Cache<?> getStorage()
  {
    if(null==cache)
    {
      throw new RuntimeException(
        "Unable to access storage backend. "+
        "Please verify configuration of net.evalcode.services.cache."
      );
    }

    return new EhcacheCache(cache);
  }
}
