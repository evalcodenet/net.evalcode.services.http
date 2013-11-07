package net.evalcode.services.http.service;


import java.nio.file.Path;
import java.util.Map;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import net.evalcode.services.http.annotation.Transactional;
import net.evalcode.services.http.internal.client.WebApplicationClientGeneratorPhp;
import net.evalcode.services.http.internal.persistence.EntityManagerProvider;
import net.evalcode.services.http.internal.servlet.container.CustomGuiceContainer;
import net.evalcode.services.http.internal.servlet.exception.NotFoundExceptionMapper;
import net.evalcode.services.http.internal.servlet.filter.JavaScriptCallbackServletFilter;
import net.evalcode.services.http.internal.servlet.ioc.SecurityManagerInterceptor;
import net.evalcode.services.http.internal.servlet.ioc.TransactionManagerInterceptor;
import net.evalcode.services.http.internal.xml.JaxbContextResolver;
import net.evalcode.services.http.service.rest.WebApplicationClientGeneratorResource;
import net.evalcode.services.manager.component.ComponentBundleInterface;
import net.evalcode.services.manager.service.cache.annotation.Cache;
import net.evalcode.services.manager.service.cache.ioc.MethodInvocationCache;
import net.evalcode.services.manager.service.logging.Log;
import net.evalcode.services.manager.service.logging.ioc.MethodInvocationLogger;
import net.evalcode.services.manager.service.statistics.Count;
import net.evalcode.services.manager.service.statistics.ioc.MethodInvocationCounter;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Stage;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.ServletScopes;
import com.sun.jersey.guice.JerseyServletModule;


/**
 * HttpServiceServletModule
 *
 * @author carsten.schipke@gmail.com
 */
public abstract class HttpServiceServletModule extends JerseyServletModule
{
  // PREDEFINED PROPERTIES
  public static final String APPLICATION_PATH_REST="rest";


  // MEMBERS
  @Inject
  Injector injector;


  // ACCESSORS/MUTATAORS
  public abstract String getContextPath();

  public String getResourcePath()
  {
    final ComponentBundleInterface bundle=injector.getInstance(ComponentBundleInterface.class);
    final Path resourcePath=bundle.getConfiguration().getResourcePath();

    if(resourcePath.toFile().exists())
      return resourcePath.toString();

    return null;
  }


  // IMPLEMENTATION
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  protected void configureServlets()
  {
    super.configureServlets();

    final Map<Key<?>, Binding<?>> bindings=injector.getBindings();

    for(final Binding<?> binding : bindings.values())
    {
      if(binding.getKey().getTypeLiteral().getRawType().equals(com.google.inject.Injector.class) ||
        binding.getKey().getTypeLiteral().getRawType().equals(java.util.logging.Logger.class) ||
        binding.getKey().getTypeLiteral().getRawType().equals(com.google.inject.Stage.class) ||
        binding.getKey().getTypeLiteral().getRawType().equals(EntityManager.class))
        continue;

      bind(binding.getKey()).toProvider((Provider)binding.getProvider());
    }

    bind(JaxbContextResolver.class);

    bind(NotFoundExceptionMapper.class);

    bind(EntityManager.class)
      .toProvider(EntityManagerProvider.class)
      .in(ServletScopes.REQUEST);

    bindInterceptor(Matchers.any(), Matchers.annotatedWith(Cache.class),
      new MethodInvocationCache(getProvider(Injector.class))
    );

    bindInterceptor(Matchers.any(), Matchers.annotatedWith(Count.class),
      new MethodInvocationCounter()
    );

    bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class),
      new TransactionManagerInterceptor(getProvider(Injector.class))
    );

    final Matcher securityAnnotationsMatcher=Matchers.annotatedWith(RolesAllowed.class)
      .or(Matchers.annotatedWith(DenyAll.class))
      .or(Matchers.annotatedWith(PermitAll.class));

    bindInterceptor(Matchers.any(), securityAnnotationsMatcher,
      new SecurityManagerInterceptor(getProvider(Injector.class))
    );

    if(Stage.DEVELOPMENT.equals(currentStage()))
    {
      bindInterceptor(Matchers.any(), Matchers.annotatedWith(Log.class),
        new MethodInvocationLogger()
      );
    }

    filter("*").through(JavaScriptCallbackServletFilter.class);

    bind(WebApplicationClientGeneratorPhp.class);
    bind(WebApplicationClientGeneratorResource.class);

    serve("/"+APPLICATION_PATH_REST+"/*").with(CustomGuiceContainer.class);
  }
}
