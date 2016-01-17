package net.evalcode.services.http.service.xml;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.evalcode.services.manager.component.annotation.Configuration;


/**
 * HttpConfiguration
 *
 * @author carsten.schipke@gmail.com
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Configuration("http.json")
public class HttpConfiguration
{
  // FIELDS
  @XmlElement(name="http", required=false)
  Listener http;
  @XmlElement(name="https", required=false)
  Listener https;


  // ACCESSORS/MUTATORS
  public Listener http()
  {
    return http;
  }

  public Listener https()
  {
    return https;
  }


  /**
   * Listener
   *
   * @author carsten.schipke@gmail.com
   */
  @XmlRootElement
  @XmlAccessorType(XmlAccessType.NONE)
  public static class Listener
  {
    // PREDEFINED PROPERTIES
    static final int DEFAULT_PORT=8080;
    static final String DEFAULT_HOST="localhost";
    static final String DEFAULT_KEYSTORE="keystore";
    static final String DEFAULT_KEYSTORE_PASSWORD="services";
    static final String DEFAULT_TRUSTSTORE="truststore";
    static final String DEFAULT_TRUSTSTORE_PASSWORD="services";
    static final String DEFAULT_CERTIFICATE_ALIAS="localhost";


    // BASIC SETTINGS
    @XmlElement(name="host")
    String host=DEFAULT_HOST;
    @XmlElement(name="port")
    int port=DEFAULT_PORT;

    // ADVANCED SETTINGS
    @XmlElement(name="acceptors")
    int acceptors=0;
    @XmlElement(name="enable_direct_buffers")
    boolean enableDirectBuffers=false;
    @XmlElement(name="enable_reverse_lookup")
    boolean enableReverseLookup=false;
    @XmlElement(name="enable_statistics")
    boolean enableStatistics=false;

    // SSL SETTINGS
    @XmlElement(name="keystore")
    String keystore=DEFAULT_KEYSTORE;
    @XmlElement(name="keystore_password")
    String keystorePassword=DEFAULT_KEYSTORE_PASSWORD;
    @XmlElement(name="truststore")
    String truststore=DEFAULT_TRUSTSTORE;
    @XmlElement(name="truststore_password")
    String truststorePassword=DEFAULT_TRUSTSTORE_PASSWORD;
    @XmlElement(name="certificate_alias")
    String certificateAlias=DEFAULT_CERTIFICATE_ALIAS;


    // ACCESSORS/MUTATORS
    public String getHost()
    {
      return host;
    }

    public int getPort()
    {
      return port;
    }

    public int getAcceptors()
    {
      if(1>acceptors)
        return 2*Runtime.getRuntime().availableProcessors();

      return acceptors;
    }

    public boolean isDirectBuffersEnabled()
    {
      return enableDirectBuffers;
    }

    public boolean isReverseLookupEnabled()
    {
      return enableReverseLookup;
    }

    public boolean isStatisticsEnabled()
    {
      return enableStatistics;
    }

    public String getKeyStore()
    {
      return keystore;
    }

    public String getKeyStorePassword()
    {
      return keystorePassword;
    }

    public String getTrustStore()
    {
      return truststore;
    }

    public String getTrustStorePassword()
    {
      return truststorePassword;
    }

    public String getCertificateAlias()
    {
      return certificateAlias;
    }


    // OVERRIDES/IMPLEMENTS
    @Override
    public String toString()
    {
      final StringBuilder stringBuilder=new StringBuilder(512);

      stringBuilder.append(Listener.class.getSimpleName());
      stringBuilder.append("{");

      stringBuilder.append(String.format("host: %s, ", getHost()));
      stringBuilder.append(String.format("port: %1$d, ", getPort()));

      stringBuilder.append(String.format("acceptors: %s, ",
        String.valueOf(getAcceptors())
      ));
      stringBuilder.append(String.format("enable_direct_buffers: %s, ",
        String.valueOf(isDirectBuffersEnabled())
      ));
      stringBuilder.append(String.format("enable_reverse_lookup: %s, ",
        String.valueOf(isReverseLookupEnabled())
      ));
      stringBuilder.append(String.format("enable_statistics: %s, ",
        String.valueOf(isStatisticsEnabled())
      ));

      stringBuilder.append(String.format("keystore: %s, ", getKeyStore()));
      stringBuilder.append("keystore_password: *****, ");

      stringBuilder.append(String.format("truststore: %s, ", getTrustStore()));
      stringBuilder.append("truststore_password: *****, ");

      stringBuilder.append(String.format("certificate_alias: %s, ",
        String.valueOf(getCertificateAlias())
      ));

      stringBuilder.append("}");

      return stringBuilder.toString();
    }
  }
}
