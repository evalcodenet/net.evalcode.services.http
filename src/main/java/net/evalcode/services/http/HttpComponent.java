package net.evalcode.services.http;


import java.util.Locale;
import java.util.TimeZone;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import net.evalcode.services.http.internal.servlet.ServletContainer;
import net.evalcode.services.http.service.HttpService;
import net.evalcode.services.http.service.HttpServiceServletModule;
import net.evalcode.services.manager.annotation.Bind;
import net.evalcode.services.manager.annotation.Component;
import net.evalcode.services.manager.annotation.Deactivate;
import net.evalcode.services.manager.annotation.Unbind;
import net.evalcode.services.manager.management.logging.Log;


/**
 * HttpComponent
 *
 * @author carsten.schipke@gmail.com
 */
@Singleton
@Component(module=HttpComponentModule.class)
public class HttpComponent implements HttpService
{
  // MEMBERS
  private static HttpComponent instance;
  @Inject
  private ServletContainer servletContainer;
  @Inject
  private HttpComponentServletModule componentServletModule;
  @Inject
  @Named("net.evalcode.services.locale")
  private Locale locale;
  @Inject
  @Named("net.evalcode.services.timezone")
  private TimeZone timeZone;


  // CONSTRUCTION
  public HttpComponent()
  {
    instance=this;
  }


  // STATIC ACCESSORS
  public static HttpComponent get()
  {
    return instance;
  }


  // ACCESSORS/MUTATORS
  @Log
  @Deactivate
  public void deactivate()
  {
    servletContainer.stop();
  }

  public Locale getLocale()
  {
    return locale;
  }

  public TimeZone getTimeZone()
  {
    return timeZone;
  }


  // OVERRIDES/IMPLEMENTS
  @Log
  @Bind
  public void bind(final HttpService httpService)
  {
    servletContainer.addHttpService(httpService);
  }

  @Log
  @Unbind
  public void unbind(final HttpService httpService)
  {
    servletContainer.removeHttpService(httpService);
  }

  @Override
  public HttpServiceServletModule getServletModule()
  {
    return componentServletModule;
  }
}
