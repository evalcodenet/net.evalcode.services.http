package net.evalcode.services.http.service.rest;


import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import net.evalcode.services.http.internal.client.WebApplicationClientGeneratorPhp;
import net.evalcode.services.http.internal.client.php.PhpClientApplication;
import net.evalcode.services.http.internal.client.php.PhpClientClass;
import net.evalcode.services.http.service.HttpServiceServletModule;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.Binding;
import com.google.inject.Injector;


/**
 * WebApplicationClientGeneratorResource
 *
 * @author carsten.schipke@gmail.com
 */
@Path(/*http/rest*/"client")
public class WebApplicationClientGeneratorResource
{
  // PREDEFDINED PROPERTIES
  static final Logger LOG=LoggerFactory.getLogger(WebApplicationClientGeneratorResource.class);


  // MEMBERS
  @Inject
  Injector injector;
  @Inject
  WebApplicationClientGeneratorPhp phpClientGenerator;
  @Inject
  Provider<HttpServletRequest> httpServletRequestProvider;


  // ACCESSORS/MUTATORS
  @GET
  @PermitAll
  @Path(/*http/rest/client*/"php.zip")
  @Produces({MediaType.APPLICATION_OCTET_STREAM})
  public StreamingOutput getPhpClient()
  {
    final Class<?> webApplicationClientGeneratorResourceType=getClass();

    return new StreamingOutput()
    {
      @Override
      public void write(final OutputStream outputStream) throws IOException
      {
        for(final Binding<?> binding : injector.getBindings().values())
        {
          final Class<?> type=binding.getKey().getTypeLiteral().getRawType();
          final Path annotation=type.getAnnotation(Path.class);

          if(null!=annotation && !webApplicationClientGeneratorResourceType.equals(type))
            phpClientGenerator.addResource(type);
        }

        phpClientGenerator.setApplicationName(
          StringUtils.capitalize(httpServletRequestProvider.get().getContextPath().substring(1))
        );

        phpClientGenerator.setBaseUrl(new URL(StringUtils.replace(
          httpServletRequestProvider.get().getRequestURL().toString(),
          httpServletRequestProvider.get().getRequestURI(), ""
        )));

        phpClientGenerator.setContextPath(httpServletRequestProvider.get().getContextPath()+"/"+
          HttpServiceServletModule.APPLICATION_PATH_REST
        );

        final PhpClientApplication client=phpClientGenerator.generateApplicationClient();
        final ZipOutputStream zip=new ZipOutputStream(outputStream);

        for(final PhpClientClass clientClazz : client.getClasses().values())
        {
          LOG.debug("Appending file to web application client [file: {}].",
            clientClazz.getFileName()
          );

          appendToZip(zip, clientClazz.getFileName(), clientClazz.toString());
        }

        zip.flush();
        zip.close();
      }
    };
  }

  /**
   * TODO Implement resource method for java client bundle:
   * - Entities
   * - Resource/Service Interfaces
   */


  // HELPERS
  static void appendToZip(final ZipOutputStream zip, final String fileName, final String content)
    throws IOException
  {
    zip.putNextEntry(new ZipEntry(fileName));
    zip.write(content.getBytes());
    zip.closeEntry();
  }
}
