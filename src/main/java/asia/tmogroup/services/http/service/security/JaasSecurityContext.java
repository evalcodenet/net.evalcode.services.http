package net.evalcode.services.http.service.security;


import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.evalcode.services.manager.service.cache.annotation.CacheInstance;
import net.evalcode.services.manager.service.cache.annotation.Region;
import net.evalcode.services.manager.service.cache.spi.Cache;
import net.evalcode.services.manager.util.security.Hash;
import com.google.common.collect.Sets;


/**
 * JaasSecurityContext
 *
 * @author evalcode.net
 */
public class JaasSecurityContext implements SecurityContext
{
  // PREDEFINED PROPERTIES
  static final Logger LOG=LoggerFactory.getLogger(JaasSecurityContext.class);
  static final String STORAGE_REGION="net.evalcode.services.cache.http.security";


  // MEMBERS
  final Provider<HttpServletRequest> httpServletRequest;


  // CONSTRUCTION
  @Inject
  public JaasSecurityContext(final Provider<HttpServletRequest> httpServletRequest)
  {
    this.httpServletRequest=httpServletRequest;
  }


  // ACCESSORS/MUTATORS
  public String login(final String principal, final String credential) throws ServletException
  {
    final HttpServletRequest httpServletRequest=this.httpServletRequest.get();

    httpServletRequest.login(principal, credential);

    final Principal userPrincipal=httpServletRequest.getUserPrincipal();
    final ServiceUserPrincipal serviceUserPrincipal=(ServiceUserPrincipal)userPrincipal;

    getStorage().put(serviceUserPrincipal.getToken(), serviceUserPrincipal);

    return serviceUserPrincipal.getToken();
  }

  public void logout() throws ServletException
  {
    httpServletRequest.get().logout();
  }

  public boolean isLoggedIn()
  {
    return null!=httpServletRequest.get().getUserPrincipal();
  }

  public Set<String> getUserRoles()
  {
    return Collections.emptySet();
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public Principal getUserPrincipal()
  {
    return httpServletRequest.get().getUserPrincipal();
  }

  @Override
  public boolean isUserInRole(final String role)
  {
    return getUserRoles().contains(role);
  }

  @Override
  public boolean isSecure()
  {
    return httpServletRequest.get().isSecure();
  }

  @Override
  public String getAuthenticationScheme()
  {
    return httpServletRequest.get().getAuthType();
  }


  // IMPLEMENTATION
  // TODO Define/share region by security realm.
  @CacheInstance(region=@Region(STORAGE_REGION))
  Cache<?> getStorage()
  {
    return null;
  }


  /**
   * ServiceUserInfo
   *
   * @author evalcode.net
   */
  public interface ServiceUserInfo
  {
    // ACCESSORS/MUTATORS
    ServiceUserPrincipal getPrincipal();
  }


  /**
   * ServiceUserPrincipal
   *
   * @author evalcode.net
   */
  public static class ServiceUserPrincipal implements Serializable, Principal
  {
    // PREDEFINED PROPERTIES
    static final long serialVersionUID=1L;


    // MEMBERS
    final String token;
    final String name;
    final Set<String> roles;


    // CONSTRUCTION
    public ServiceUserPrincipal(final String name)
    {
      this(name, null, new HashSet<String>());
    }

    public ServiceUserPrincipal(final String name, final String token)
    {
      this(name, token, new HashSet<String>());
    }

    public ServiceUserPrincipal(final String name, final String token, final String[] roles)
    {
      this(name, token, Sets.newHashSet(roles));
    }

    public ServiceUserPrincipal(final String name, final String token, final Set<String> roles)
    {
      this.name=name;
      this.roles=Collections.unmodifiableSet(roles);

      if(null==token)
        this.token=Hash.random();
      else
        this.token=token;
    }


    // ACCESSORS/MUTATORS
    public String getToken()
    {
      return token;
    }

    public Set<String> getRoles()
    {
      return roles;
    }

    public Map<String, Integer> getAcl()
    {
      return Collections.emptyMap();
    }


    // OVERRIDES/IMPLEMENTS
    @Override
    public String getName()
    {
      return name;
    }

    @Override
    public String toString()
    {
      return String.format("%s{name: %s, roles: %s}",
        ServiceUserPrincipal.class.getSimpleName(), name, roles
      );
    }
  }


  /**
   * AuthenticationFilter
   *
   * @author evalcode.net
   */
  @Singleton
  public static class AuthenticationFilter implements Filter
  {
    // PREDEFINED PROPERTIES
    public static final String HEADER_SERVICES_TOKEN="Services-Token";
    public static final String HEADER_SERVICES_PRINCIPAL="Services-Principal";
    public static final String HEADER_SERVICES_CREDENTIAL="Services-Credential";


    // MEMBERS
    final Provider<JaasSecurityContext> securityContext;


    // CONSTRUCTION
    @Inject
    public AuthenticationFilter(final Provider<JaasSecurityContext> securityContext)
    {
      this.securityContext=securityContext;
    }


    // OVERRIDES/IMPLEMENTS
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException
    {
      // Nothing to do ...

    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
        final FilterChain filterChain)
      throws IOException, ServletException
    {
      final HttpServletRequest httpServletRequest=(HttpServletRequest)servletRequest;
      final HttpServletResponse httpServletResponse=(HttpServletResponse)servletResponse;

      final String token=httpServletRequest.getHeader(HEADER_SERVICES_TOKEN);

      if(null==token)
      {
        final String principal=httpServletRequest.getHeader(HEADER_SERVICES_PRINCIPAL);
        final String credential=httpServletRequest.getHeader(HEADER_SERVICES_CREDENTIAL);

        if(null!=principal && null!=credential)
        {
          httpServletResponse.setHeader(HEADER_SERVICES_TOKEN,
            securityContext.get().login(principal, credential)
          );
        }
      }
      else
      {
        httpServletResponse.setHeader(HEADER_SERVICES_TOKEN,
          securityContext.get().login(token, null)
        );
      }
    }

    @Override
    public void destroy()
    {
      // Nothing to do ...
    }
  }
}
