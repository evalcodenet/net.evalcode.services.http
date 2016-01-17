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
import org.eclipse.jetty.plus.jaas.spi.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.evalcode.services.http.service.security.JaasSecurityContext.ServiceUserPrincipal;


/**
 * JaasTokenLoginModule
 *
 * @author carsten.schipke@gmail.com
 */
public class JaasTokenLoginModule extends JaasLoginModule
{
  // PREDEFINED PROPERTIES
  static final Logger LOG=LoggerFactory.getLogger(JaasTokenLoginModule.class);


  // MEMBERS
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
    catch(final UnsupportedCallbackException | IOException e)
    {
      throw new LoginException(e.getMessage());
    }

    final String region=config.get("cache");
    final Object result=JaasSecurityContext.getStorage(region).get(callbackName.getName());

    if(null!=result)
    {
      if(result instanceof ServiceUserPrincipal)
      {
        final ServiceUserPrincipal serviceUserPrincipal=(ServiceUserPrincipal)result;

        setCurrentUser(new ServiceUserInfo(
          serviceUserPrincipal.getName(),
          serviceUserPrincipal.getToken(),
          serviceUserPrincipal.getRoles()
        ));

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
}
