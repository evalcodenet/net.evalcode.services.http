package net.evalcode.services.http.service.security;


import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.security.auth.Subject;
import org.eclipse.jetty.http.security.Password;
import org.eclipse.jetty.plus.jaas.JAASRole;
import org.eclipse.jetty.security.DefaultUserIdentity;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.RoleRunAsToken;
import org.eclipse.jetty.security.RunAsToken;
import org.eclipse.jetty.server.UserIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.evalcode.services.http.service.security.JaasSecurityContext.ServiceUserPrincipal;


/**
 * JaasIdentityService
 *
 * @author evalcode.net
 */
public class JaasIdentityService implements IdentityService
{
  // PREDEFINED PROPERTIES
  static final Logger LOG=LoggerFactory.getLogger(JaasIdentityService.class);


  // OVERRIDES/IMPLEMENS
  @Override
  public UserIdentity newUserIdentity(final Subject subject,
      final Principal principal, final String[] roles)
  {
    final Set<Password> credentials=subject.getPrivateCredentials(Password.class);

    String token=null;

    if(1==credentials.size())
      token=credentials.iterator().next().toString();

    final ServiceUserPrincipal serviceUserPrincipal=new ServiceUserPrincipal(
      principal.getName(), token, roles
    );

    final Set<Principal> principals=new HashSet<>();

    for(final String role : roles)
      principals.add(new JAASRole(role));

    principals.add(serviceUserPrincipal);

    return new DefaultUserIdentity(
      new Subject(subject.isReadOnly(), principals, Collections.emptySet(), Collections.emptySet()),
      serviceUserPrincipal,
      roles
    );
  }

  @Override
  public RunAsToken newRunAsToken(final String token)
  {
    return new RoleRunAsToken(token);
  }

  @Override
  public Object setRunAs(final UserIdentity userIdentity, final RunAsToken runAsToken)
  {
    return runAsToken;
  }

  @Override
  public void unsetRunAs(final Object object)
  {

  }

  @Override
  public Object associate(final UserIdentity userIdentity)
  {
    return null;
  }

  @Override
  public void disassociate(final Object object)
  {

  }

  @Override
  public UserIdentity getSystemUserIdentity()
  {
    return null;
  }
}