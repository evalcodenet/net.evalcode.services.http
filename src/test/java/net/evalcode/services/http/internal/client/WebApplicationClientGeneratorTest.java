package net.evalcode.services.http.internal.client;


import static org.junit.Assert.assertNotNull;
import java.io.IOException;
import java.net.URL;
import net.evalcode.services.http.internal.client.WebApplicationClientGenerator;
import net.evalcode.services.http.internal.client.WebApplicationClientGeneratorPhp;
import net.evalcode.services.http.internal.client.resource.FooResource;
import org.junit.Test;


/**
 * Test {@link WebApplicationClientGenerator}
 *
 * @author carsten.schipke@gmail.com
 */
public class WebApplicationClientGeneratorTest
{
  @Test
  public void testGenerate() throws IOException
  {
    final WebApplicationClientGenerator generator=new WebApplicationClientGeneratorPhp();

    generator.setApplicationName("Http");
    generator.setBaseUrl(new URL("http://localhost:8080"));
    generator.setContextPath("/http/rest");

    generator.addResource(FooResource.class);

    assertNotNull(generator.generateApplicationClient());
  }
}
