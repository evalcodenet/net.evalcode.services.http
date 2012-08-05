package net.evalcode.services.http.service;


import net.evalcode.services.manager.annotation.Service;
import net.evalcode.services.manager.component.ServiceComponent;


/**
 * HttpService
 *
 * @author carsten.schipke@gmail.com
 */
@Service
public interface HttpService extends ServiceComponent
{
  // ACCESSORS/MUTATORS
  HttpServiceServletModule getServletModule();
}
