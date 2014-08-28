package net.evalcode.services.http.internal.client.resource;


import java.net.MalformedURLException;
import java.net.URL;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.evalcode.services.http.internal.client.entity.BarEntity;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.evalcode.javax.xml.bind.XmlSet;


/**
 * FooResource
 *
 * @author carsten.schipke@gmail.com
 */
@Ignore
@Path(/*http/rest*/"foo")
public class FooResource
{
  // PREDEFINED PROPERTIES
  private static final Logger LOG=LoggerFactory.getLogger(FooResource.class);


  // ACCESSORS/MUTATORS
  @GET
  @Path(/*http/rest/foo*/"all")
  @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
  public XmlSet<BarEntity> getValues()
  {
    return new XmlSet<>();
  }

  @GET
  @Path(/*http/rest/foo*/"get/{key}")
  @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
  public String getValue(@PathParam("key") final String key)
  {
    return key;
  }

  @GET
  @Path(/*http/rest/foo*/"set/{entity}")
  @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
  public BarEntity setEntity(@PathParam("entity") final BarEntity entity)
  {
    return entity;
  }

  /**
   * Further documentation
   */
  @GET
  @Path(/*http/rest/foo*/"entity")
  @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
  public BarEntity getEntity()
  {
    final BarEntity entity=new BarEntity();

    entity.id=1L;
    entity.serviceName="bar";
    entity.minute=0;
    entity.hour=0;
    entity.dayOfWeek=0;
    entity.month=0;
    entity.dayOfMonth=0;

    entity.authToken="foobar";

    try
    {
      entity.invokeUrl=new URL("http://domain.tld/bar/");
    }
    catch(final MalformedURLException e)
    {
      LOG.debug(e.getMessage(), e);
    }

    return entity;
  }
}
