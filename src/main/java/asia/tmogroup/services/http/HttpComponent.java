package net.evalcode.services.http;


import java.util.Locale;
import java.util.TimeZone;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import net.evalcode.services.http.internal.servlet.ServletContainer;
import net.evalcode.services.http.service.HttpService;
import net.evalcode.services.http.service.HttpServiceServletModule;
import net.evalcode.services.manager.component.annotation.Activate;
import net.evalcode.services.manager.component.annotation.Bind;
import net.evalcode.services.manager.component.annotation.Component;
import net.evalcode.services.manager.component.annotation.Deactivate;
import net.evalcode.services.manager.component.annotation.Unbind;
import net.evalcode.services.manager.service.logging.Log;


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
  static HttpComponent instance;

  @Inject
  ServletContainer servletContainer;
  @Inject
  HttpComponentServletModule componentServletModule;
  @Inject
  @Named("net.evalcode.services.locale")
  Locale locale;
  @Inject
  @Named("net.evalcode.services.timezone")
  TimeZone timeZone;


  // CONSTRUCTION
  public HttpComponent()
  {
    instance=this;
  }


  // STATIC ACCESSORS
  public static final HttpComponent get()
  {
    return instance;
  }


  // ACCESSORS/MUTATORS
  @Activate
  @Log(level=Log.Level.INFO)
  public void activate()
  {
    servletContainer.start();
  }

  @Deactivate
  @Log(level=Log.Level.INFO)
  public void deactivate()
  {
    servletContainer.shutdown();
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
  @Bind
  public void bind(final HttpService httpService)
  {
    servletContainer.addHttpService(httpService);
  }

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
