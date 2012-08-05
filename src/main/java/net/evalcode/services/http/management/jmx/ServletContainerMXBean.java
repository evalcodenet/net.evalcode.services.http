package net.evalcode.services.http.management.jmx;


import javax.management.MXBean;


/**
 * ServletContainerMXBean
 *
 * @author carsten.schipke@gmail.com
 */
@MXBean
public interface ServletContainerMXBean
{
  // ACCESSORS/MUTATORS
  void start();
  void stop();
}
