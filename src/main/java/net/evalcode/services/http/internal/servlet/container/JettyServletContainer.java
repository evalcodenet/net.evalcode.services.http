package net.evalcode.services.http.internal.servlet.container;


import java.io.IOException;
import java.util.EnumSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import net.evalcode.services.http.internal.servlet.ServletContainer;
import net.evalcode.services.http.service.HttpService;
import net.evalcode.services.http.service.HttpServiceServletModule;
import net.evalcode.services.http.service.xml.HttpConnector;
import net.evalcode.services.http.service.xml.HttpConnectors;
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

    final ContextHandlerCollection contextHandlerContainer=new ContextHandlerCollection();

    server.setHandler(contextHandlerContainer);

    for(final HttpService httpService : httpServices)
      addServletContext(contextHandlerContainer, httpService);

    if(null==contextHandlerContainer.getHandlers())
    {
      LOG.info("No HTTP services to serve ...");
    }
    else
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
  }

  @Override
  public void stop()
  {
    try
    {
      server.stop();
    }
    catch(final Exception e)
    {
      LOG.debug("Failed to start servlet container.", e);

      throw new ServiceException("Failed to stop servlet container.", e);
    }
  }

  @Override
  public boolean isStarted()
  {
    return server.isStarted();
  }

  @Override
  public boolean isStopped()
  {
    return server.isStopped();
  }

  @Override
  public void addHttpService(final HttpService httpService)
  {
    httpServices.add(httpService);

    restart();
  }

  @Override
  public void removeHttpService(final HttpService httpService)
  {
    httpServices.remove(httpService);

    restart();
  }


  // IMPLEMENTATION
  private void initialize()
  {
    server.setSendServerVersion(false);
    server.setGracefulShutdown(TIMEOUT_GRACEFUL_SHUTDOWN);
    server.setConnectors(new Connector[] {});

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

  private void addServletContext(final ContextHandlerCollection contextHandlerContainer,
    final HttpService httpService)
  {
    final HttpServiceServletModule servletModule=httpService.getServletModule();

    final ServletContextHandler servletContextHandler=new ServletContextHandler(
      contextHandlerContainer,
      servletModule.getContextPath(),
      ServletContextHandler.SESSIONS+ServletContextHandler.SECURITY
    );

    final String contextResourcePath=servletModule.getResourcePath();

    if(null==contextResourcePath)
    {
      LOG.info("No static resources for context [context-path: {}].",
        servletModule.getContextPath()
      );
    }
    else
    {
      try
      {
        servletContextHandler.setBaseResource(Resource.newResource(contextResourcePath));

        LOG.info("Found static resources for context [context-path: {}, resource-path: {}].",
          servletModule.getContextPath(), contextResourcePath
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
  }

  private void restart()
  {
    if(isStarted())
      stop();

    start();
  }
}
