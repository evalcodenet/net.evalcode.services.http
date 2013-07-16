package net.evalcode.services.http.internal.xml;


import java.util.Set;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import net.evalcode.services.http.xml.DateXmlAdapter;
import net.evalcode.services.manager.management.logging.Log;
import net.evalcode.javax.xml.bind.XmlLinkedQueue;
import net.evalcode.javax.xml.bind.XmlList;
import net.evalcode.javax.xml.bind.XmlSet;


@Provider
public class JaxbContextResolver implements ContextResolver<JAXBContext>
{
  // PREDEFINED PROPERTIES
  private static final int COUNT_PREDEFINED_CONTEXT_CLASSES=5;


  // MEMBERS
  private final Class<?>[] classes;
  private volatile JAXBContext context;


  // CONSTRUCTION
  public JaxbContextResolver(final Set<Class<?>> classes)
  {
    // FIXME
    int i=0;
    this.classes=new Class<?>[classes.size()+COUNT_PREDEFINED_CONTEXT_CLASSES];

    this.classes[i++]=XmlSet.class;
    this.classes[i++]=XmlList.class;
    this.classes[i++]=XmlLinkedQueue.class;
    this.classes[i++]=XmlError.class;
    this.classes[i++]=DateXmlAdapter.class;

    for(final Class<?> clazz : classes)
      this.classes[i++]=clazz;
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  @Log(level=Log.Level.INFO)
  public JAXBContext getContext(final Class<?> clazz)
  {
    return getContextImpl();
  }


  // IMPLEMENTATION
  private JAXBContext getContextImpl()
  {
    JAXBContext tmp=this.context;

    if(null==tmp)
    {
      synchronized(this)
      {
        try
        {
          tmp=JAXBContext.newInstance(classes);

          this.context=tmp;
        }
        catch(final JAXBException e)
        {
          throw new RuntimeException(e.getMessage(), e);
        }
      }
    }

    return this.context;
  }
}