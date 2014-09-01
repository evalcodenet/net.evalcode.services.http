package net.evalcode.services.http.internal.servlet.container;


import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.ws.rs.core.MediaType;
import com.google.inject.Injector;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;


/**
 * CustomGuiceContainer
 *
 * @author carsten.schipke@gmail.com
 */
@Singleton
public class CustomGuiceContainer extends GuiceContainer
{
  // PREDEFINED PROPERTIES
  private static final long serialVersionUID=1L;


  // CONSTRUCTION
  @Inject
  CustomGuiceContainer(final Injector injector)
  {
    super(injector);
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  protected ResourceConfig getDefaultResourceConfig(
      final Map<String, Object> properties, final WebConfig webConfig)
    throws ServletException
  {
    final ResourceConfig resourceConfig=super.getDefaultResourceConfig(properties, webConfig);

    resourceConfig.getMediaTypeMappings().put(
      MediaType.APPLICATION_JSON_TYPE.getSubtype(), MediaType.APPLICATION_JSON_TYPE
    );

    resourceConfig.getMediaTypeMappings().put(
      MediaType.TEXT_XML_TYPE.getSubtype(), MediaType.TEXT_XML_TYPE
    );

    return resourceConfig;
  }
}
