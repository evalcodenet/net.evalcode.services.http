package net.evalcode.services.http.internal.servlet.container;


import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.plus.jaas.JAASLoginService;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.osgi.framework.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.evalcode.services.http.internal.servlet.ServletContainer;
import net.evalcode.services.http.service.HttpService;
import net.evalcode.services.http.service.HttpServiceServletModule;
import net.evalcode.services.http.service.xml.HttpConnector;
import net.evalcode.services.http.service.xml.HttpConnectors;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;


/**
 * JettyServletContainer
 *
 * @author carsten.schipke@gmail.com
 */
public class JettyServletContainer implements ServletContainer
{
  // PREDEFINED PROPERTIES
  static final Logger LOG=LoggerFactory.getLogger(JettyServletContainer.class);

  static final int TIMEOUT_GRACEFUL_SHUTDOWN=100;
  static final int DEFAULT_ACCEPTORS=2*Runtime.getRuntime().availableProcessors();


  // MEMBERS
  final Stage stage;
  final Server server;
  final HttpConnectors httpConnectors;

  final AtomicBoolean initialized=new AtomicBoolean(false);
  final Queue<HttpService> httpServices=new ConcurrentLinkedQueue<>();
  final ConcurrentHashMap<String, ServletContextHandler> servletContextHandlers=
    new ConcurrentHashMap<>();
  final ContextHandlerCollection contextHandlerContainer=new ContextHandlerCollection();


  // CONSTRUCTION
  @Inject
  JettyServletContainer(final Stage stage, final Server server, final HttpConnectors httpConnectors)
  {
    super();

    this.stage=stage;
    this.server=server;
    this.httpConnectors=httpConnectors;
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public void start()
  {
    if(!initialized.get())
      initialize();

    if(server.isStopped())
    {
      try
      {
        server.start();
      }
      catch(final Exception e)
      {
        LOG.debug("Failed to start servlet container.", e);

        throw new ServiceException("Failed to start servlet container.", e);
      }
    }
    else
    {
      try
      {
        for(final Connector connector : server.getConnectors())
          connector.start();

        contextHandlerContainer.start();
      }
      catch(final Exception e)
      {
        LOG.debug("Failed to start servlet container.", e);

        throw new ServiceException("Failed to start servlet container.", e);
      }
    }
  }

  @Override
  public void stop()
  {
    try
    {
      for(final Connector connector : server.getConnectors())
        connector.stop();

      contextHandlerContainer.stop();
    }
    catch(final Exception e)
    {
      LOG.debug("Failed to stop servlet container.", e);

      throw new ServiceException("Failed to stop servlet container.", e);
    }
  }

  /**
   * FIXME Should probably destroy connectors & context handlers or
   * fully re-initialize server/connectors/context-handlers during start/stop.
   */
  public void shutdown()
  {
    stop();

    try
    {
      server.stop();
    }
    catch(final Exception e)
    {
      LOG.debug("Failed to stop servlet container.", e);

      throw new ServiceException("Failed to stop servlet container.", e);
    }
  }

  @Override
  public boolean isStarted()
  {
    return contextHandlerContainer.isStarted();
  }

  @Override
  public boolean isStopped()
  {
    return contextHandlerContainer.isStopped();
  }

  @Override
  public void addHttpService(final HttpService httpService)
  {
    httpServices.add(httpService);

    addServletContext(httpService);

    try
    {
      if(contextHandlerContainer.isStarted())
        contextHandlerContainer.stop();

      contextHandlerContainer.start();
    }
    catch(final Exception e)
    {
      LOG.debug(e.getMessage(), e);

      throw new ServiceException("Failed to restart servlet context handler.", e);
    }
  }

  @Override
  public void removeHttpService(final HttpService httpService)
  {
    httpServices.remove(httpService);

    final String contextPath=httpService.getServletModule().getContextPath();
    final ServletContextHandler servletContextHandler=servletContextHandlers.remove(contextPath);

    if(null!=servletContextHandler)
      ((ContextHandlerCollection)server.getHandler()).removeHandler(servletContextHandler);
  }


  // IMPLEMENTATION
  private void initialize()
  {
    server.setSendServerVersion(false);
    server.setGracefulShutdown(TIMEOUT_GRACEFUL_SHUTDOWN);
    server.setConnectors(new Connector[] {});
    server.setHandler(contextHandlerContainer);

    final Set<HttpConnector> httpConnectors=this.httpConnectors.get();

    for(final HttpConnector httpConnector : httpConnectors)
    {
      if(!httpConnector.isEnabled())
        continue;

      if(HttpConnector.Scheme.HTTPS.equals(httpConnector.getScheme()))
        server.addConnector(toSslSelectChannelConnector(httpConnector));
      else
        server.addConnector(toSelectChannelConnector(httpConnector));
    }

    initialized.set(true);
  }

  private static SelectChannelConnector toSelectChannelConnector(final HttpConnector httpConnector)
  {
    final SelectChannelConnector selectChannelConnector=new SelectChannelConnector();

    selectChannelConnector.setHost(httpConnector.getHost());
    selectChannelConnector.setPort(httpConnector.getPort());

    selectChannelConnector.setAcceptors(httpConnector.getAcceptors());

    selectChannelConnector.setResolveNames(httpConnector.isReverseLookupEnabled());
    selectChannelConnector.setStatsOn(httpConnector.isStatisticsEnabled());
    selectChannelConnector.setUseDirectBuffers(httpConnector.isDirectBuffersEnabled());

    return selectChannelConnector;
  }

  private static Connector toSslSelectChannelConnector(final HttpConnector httpConnector)
  {
    final SslSelectChannelConnector sslSelectChannelConnector=new SslSelectChannelConnector();

    LOG.info("ssl connector: {}", httpConnector);

    sslSelectChannelConnector.setHost(httpConnector.getHost());
    sslSelectChannelConnector.setPort(httpConnector.getPort());

    sslSelectChannelConnector.getSslContextFactory()
      .setKeyStore(httpConnector.getKeyStore());
    sslSelectChannelConnector.getSslContextFactory()
      .setKeyStorePassword(httpConnector.getKeyStorePassword());
    sslSelectChannelConnector.getSslContextFactory()
      .setTrustStore(httpConnector.getTrustStore());
    sslSelectChannelConnector.getSslContextFactory()
      .setTrustStorePassword(httpConnector.getTrustStorePassword());
    sslSelectChannelConnector.getSslContextFactory()
      .setCertAlias(httpConnector.getCertificateAlias());

    sslSelectChannelConnector.setAcceptors(httpConnector.getAcceptors());

    sslSelectChannelConnector.setResolveNames(httpConnector.isReverseLookupEnabled());
    sslSelectChannelConnector.setStatsOn(httpConnector.isStatisticsEnabled());
    sslSelectChannelConnector.setUseDirectBuffers(httpConnector.isDirectBuffersEnabled());

    return sslSelectChannelConnector;
  }

  private void addServletContext(final HttpService httpService)
  {
    final HttpServiceServletModule servletModule=httpService.getServletModule();
    final String servletContextResourcePath=servletModule.getResourcePath();
    final String servletContextRealm=servletModule.getSecurityRealm();

    int servletContextHandlerFlags=0;

    if(!servletModule.isStateless())
      servletContextHandlerFlags|=ServletContextHandler.SESSIONS;
    if(null!=servletContextRealm)
      servletContextHandlerFlags|=ServletContextHandler.SECURITY;

    final ServletContextHandler servletContextHandler=new ServletContextHandler(
      contextHandlerContainer, servletModule.getContextPath(), servletContextHandlerFlags
    );

    if(null!=servletContextRealm)
    {
      final JAASLoginService jaasLoginService=new JAASLoginService(servletContextRealm);
      jaasLoginService.setLoginModuleName(servletContextRealm);

      final ConstraintSecurityHandler constraintSecurityHandler=new ConstraintSecurityHandler();
      constraintSecurityHandler.setLoginService(jaasLoginService);
      constraintSecurityHandler.setAuthMethod(servletModule.getSecurityAuthMethod());

      final Map<String, String> securityInitParameters=servletModule.getSecurityInitParameters();

      for(final String key : securityInitParameters.keySet())
        constraintSecurityHandler.setInitParameter(key, securityInitParameters.get(key));

      servletContextHandler.setSecurityHandler(constraintSecurityHandler);
    }

    if(null==servletContextResourcePath)
    {
      LOG.info("No static resources for context [context-path: {}].",
        servletModule.getContextPath()
      );
    }
    else
    {
      try
      {
        servletContextHandler.setBaseResource(Resource.newResource(servletContextResourcePath));

        LOG.info("Found static resources for context [context-path: {}, resource-path: {}].",
          servletModule.getContextPath(), servletContextResourcePath
        );
      }
      catch(final IOException e)
      {
        LOG.error("Unable to locate static resources for context [context-path: {}].",
          servletModule.getContextPath(), e
        );
      }
    }

    servletContextHandler.setContextPath(servletModule.getContextPath());
    servletContextHandler.addServlet(DefaultServlet.class, "/");
    servletContextHandler.addFilter(GuiceFilter.class, "*", EnumSet.of(DispatcherType.REQUEST));

    servletContextHandler.addEventListener(new GuiceServletContextListener()
    {
      @Override
      protected Injector getInjector()
      {
        return Guice.createInjector(stage, servletModule);
      }
    });

    servletContextHandlers.put(servletModule.getContextPath(), servletContextHandler);
  }
}
