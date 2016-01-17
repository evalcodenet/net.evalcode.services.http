package net.evalcode.services.http.internal.xml;


import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import net.evalcode.javax.xml.bind.XmlLinkedQueue;
import net.evalcode.javax.xml.bind.XmlList;
import net.evalcode.javax.xml.bind.XmlSet;
import net.evalcode.services.http.exception.ServiceUnavailableException;
import net.evalcode.services.http.util.xml.DateXmlAdapter;
import net.evalcode.services.manager.component.ComponentBundleInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.Injector;


@Provider
@Singleton
public class JaxbContextResolver implements ContextResolver<JAXBContext>
{
  // PREDEFINED PROPERTIES
  static final Logger LOG=LoggerFactory.getLogger(JaxbContextResolver.class);

  static final Class<?>[] BUILTIN=new Class<?>[] {
    XmlSet.class,
    XmlList.class,
    XmlLinkedQueue.class,
    XmlError.class,
    DateXmlAdapter.class
  };


  // MEMBERS
  final Injector injector;
  volatile JAXBContext context;


  // CONSTRUCTION
  @Inject
  public JaxbContextResolver(final Injector injector)
  {
    this.injector=injector;
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public JAXBContext getContext(final Class<?> clazz)
  {
    return getContextImpl();
  }


  // IMPLEMENTATION
  private JAXBContext getContextImpl()
  {
    if(null==context)
    {
      final ComponentBundleInterface bundle=injector.getInstance(ComponentBundleInterface.class);
      final Set<Class<?>> clazzes=new HashSet<>();

      for(final Class<?> clazz : BUILTIN)
        clazzes.add(clazz);
      for(final Class<?> clazz : bundle.getInspector().getExportedJaxbEntities())
        clazzes.add(clazz);

      final Class<?>[] array=clazzes.toArray(new Class<?>[0]);

      synchronized(this)
      {
        if(null==context)
        {
          try
          {
            context=JAXBContext.newInstance(array);
          }
          catch(final JAXBException e)
          {
            LOG.error(e.getMessage(), e);

            throw new ServiceUnavailableException(e.getMessage(), e);
          }
        }

        return context;
      }
    }

    return context;
  }
}
