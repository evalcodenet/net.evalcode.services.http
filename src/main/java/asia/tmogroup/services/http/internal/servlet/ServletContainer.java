package net.evalcode.services.http.internal.servlet;


import net.evalcode.services.http.service.HttpService;


/**
 * ServletContainer
 *
 * @author carsten.schipke@gmail.com
 */
public interface ServletContainer
{
  // ACCESSORS/MUTATORS
  void start();
  void stop();
  void shutdown();

  boolean isStarted();
  boolean isStopped();

  void addHttpService(final HttpService httpService);
  void removeHttpService(final HttpService httpService);
}
