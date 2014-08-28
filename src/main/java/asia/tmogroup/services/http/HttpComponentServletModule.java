package net.evalcode.services.http;


import javax.inject.Singleton;
import net.evalcode.services.http.service.HttpServiceServletModule;


/**
 * HttpComponentServletModule
 *
 * @author carsten.schipke@gmail.com
 */
@Singleton
public class HttpComponentServletModule extends HttpServiceServletModule
{
  // OVERRIDES/IMPLEMENTS
  @Override
  public String getContextPath()
  {
    return "/http";
  }
}
