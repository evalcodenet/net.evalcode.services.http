package net.evalcode.services.http.service.security;


import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.security.auth.Subject;
import org.eclipse.jetty.http.security.Credential;
import org.eclipse.jetty.plus.jaas.JAASRole;
import org.eclipse.jetty.plus.jaas.spi.AbstractLoginModule;
import org.eclipse.jetty.plus.jaas.spi.UserInfo;
import net.evalcode.services.http.service.security.JaasSecurityContext.ServiceUserPrincipal;
import com.google.common.collect.Lists;


/**
 * JaasLoginModule
 *
 * @author carsten.schipke@gmail.com
 */
public abstract class JaasLoginModule extends AbstractLoginModule
{
  /**
   * ServiceUserInfo
   *
   * @author carsten.schipke@gmail.com
   */
  public class ServiceUserInfo extends JAASUserInfo
  {
    // MEMBERS
    final String username;
    final String credential;
    final Principal principal;
    final List<JAASRole> roles=new ArrayList<>();


    // CONSTRUCTION
    public ServiceUserInfo(final String username, final String credential, final Set<String> roleNames)
    {
      super(new UserInfo(
        username,
        Credential.getCredential(credential),
        Lists.newArrayList(roleNames)
      ));

      this.username=username;
      this.credential=credential;
      this.principal=new ServiceUserPrincipal(username, credential, roleNames);

      for(final String roleName : roleNames)
        roles.add(new JAASRole(roleName));
    }


    // ACCESSORS/MUTATORS
    public String getCredential()
    {
      return credential;
    }


    // OVERRIDES/IMPLEMENS
    @Override
    public String getUserName()
    {
      return username;
    }

    @Override
    public Principal getPrincipal()
    {
      return principal;
    }

    @Override
    public void setUserInfo(final UserInfo userInfo)
    {
      // Do nothing ...
    }

    @Override
    public void setJAASInfo(final Subject subject)
    {
      subject.getPrincipals().add(principal);
      subject.getPrivateCredentials().add(credential);
      subject.getPrincipals().addAll(roles);
    }

    @Override
    public void unsetJAASInfo(Subject subject)
    {
      subject.getPrincipals().remove(principal);
      subject.getPrivateCredentials().remove(credential);
      subject.getPrincipals().removeAll(roles);
    }

    @Override
    public boolean checkCredential(final Object other)
    {
      if(other instanceof Credential)
        return ((Credential)other).check(credential);

      if(null==credential)
        return credential==other;

      return credential.equals(other);
    }
  }
}
