package net.evalcode.services.http.internal.client.entity;


import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import net.evalcode.services.http.annotation.client.WebApplicationClientType;
import org.junit.Ignore;


/**
 * BarEntity
 *
 * @author carsten.schipke@gmail.com
 */
@Ignore
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BarEntity implements Serializable
{
  // PREDEFINED PROPERTIES
  private static final long serialVersionUID=1L;


  // PROPERTIES
  public long id;
  public int key;

  public String serviceName;
  public Integer minute;
  public Integer hour;
  public Integer dayOfWeek;
  public Integer month;
  public Integer dayOfMonth;

  @WebApplicationClientType(type=WebApplicationClientType.Type.DATE)
  public Date date;

  public URL invokeUrl;
  public String authToken;

  public Set<BarEntity> children;
}
