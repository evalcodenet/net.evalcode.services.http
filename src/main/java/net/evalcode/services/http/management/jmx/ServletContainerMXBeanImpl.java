package net.evalcode.services.http.management.jmx;


import javax.inject.Inject;
import javax.inject.Singleton;
import net.evalcode.services.http.HttpComponentModule;
import net.evalcode.services.http.internal.servlet.ServletContainer;
import net.evalcode.services.manager.annotation.Component;
import net.evalcode.services.manager.management.jmx.ServiceMBean;
import net.evalcode.services.manager.management.logging.Log;


/**
 * ServletContainerMXBeanImpl
 *
 * @author carsten.schipke@gmail.com
 */
@Singleton
@Component(module=HttpComponentModule.class)
public class ServletContainerMXBeanImpl implements ServiceMBean, ServletContainerMXBean
{
  // PREDEFINED PROPERTIES
  private static final String NAME="net.evalcode.services.http";


  // MEMBERS
  @Inject
  private ServletContainer servletContainer;


  // OVERRIDES/IMPLEMENTS
  @Log
  @Override
  public String getName()
  {
    return NAME;
  }

  @Log
  @Override
  public void start()
  {
    servletContainer.start();
  }

  @Log
  @Override
  public void stop()
  {
    servletContainer.stop();
  }
}
