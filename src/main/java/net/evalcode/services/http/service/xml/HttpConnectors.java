package net.evalcode.services.http.service.xml;


import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.evalcode.services.manager.annotation.Configuration;


/**
 * HttpConnectors
 *
 * @author carsten.schipke@gmail.com
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Configuration("http-connectors.json")
public class HttpConnectors
{
  // FIELDS
  @XmlElement(name="http_connectors")
  private Set<HttpConnector> httpConnectors;


  // ACCESSORS/MUTATORS
  public Set<HttpConnector> get()
  {
    return httpConnectors;
  }
}
