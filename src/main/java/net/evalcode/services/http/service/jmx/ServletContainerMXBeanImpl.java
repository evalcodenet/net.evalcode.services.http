package net.evalcode.services.http.service.jmx;


import javax.inject.Inject;
import javax.inject.Singleton;
import net.evalcode.services.http.HttpComponentModule;
import net.evalcode.services.http.internal.servlet.ServletContainer;
import net.evalcode.services.manager.component.annotation.Component;
import net.evalcode.services.manager.service.jmx.ServiceMBean;
import net.evalcode.services.manager.service.logging.Log;


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
  static final String NAME="net.evalcode.services.http";


  // MEMBERS
  final ServletContainer servletContainer;


  // CONSTRUCTION
  @Inject
  public ServletContainerMXBeanImpl(final ServletContainer servletContainer)
  {
    this.servletContainer=servletContainer;
  }


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
