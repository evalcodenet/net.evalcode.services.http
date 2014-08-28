package net.evalcode.services.http;


import javax.inject.Singleton;
import net.evalcode.services.http.internal.servlet.ServletContainer;
import net.evalcode.services.http.internal.servlet.container.JettyServletContainer;
import net.evalcode.services.manager.component.ServiceComponentModule;
import org.eclipse.jetty.server.Server;


/**
 * HttpComponentModule
 *
 * @author carsten.schipke@gmail.com
 */
@Singleton
public class HttpComponentModule extends ServiceComponentModule
{
  // IMPLEMENTATION
  @Override
  protected void configure()
  {
    super.configure();

    bind(Server.class);

    bind(ServletContainer.class)
      .to(JettyServletContainer.class)
      .in(Singleton.class);

    bind(HttpComponent.class);
    bind(HttpComponentServletModule.class);
  }
}
