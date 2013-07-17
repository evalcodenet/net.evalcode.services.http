package net.evalcode.services.http.service.xml;


import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.evalcode.services.manager.configuration.Environment;


/**
 * HttpConnector
 *
 * @author carsten.schipke@gmail.com
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class HttpConnector
{
  /**
   * Scheme
   *
   * @author carsten.schipke@gmail.com
   */
  public static enum Scheme
  {
    HTTP,
    HTTPS;
  }


  // PREDEFINED PROPERTIES
  static final int DEFAULT_PORT=8080;
  static final String DEFAULT_HOST="localhost";
  static final String DEFAULT_KEYSTORE="keystore";
  static final String DEFAULT_KEYSTORE_PASSWORD="services";
  static final String DEFAULT_TRUSTSTORE="truststore";
  static final String DEFAULT_TRUSTSTORE_PASSWORD="services";
  static final String DEFAULT_CERTIFICATE_ALIAS="localhost";


  // BASIC SETTINGS
  @XmlElement(name="enabled")
  boolean enabled=true;
  @XmlElement(name="scheme")
  Scheme scheme=Scheme.HTTP;
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


  // MEMBERS
  @Inject
  private Environment environment;


  // ACCESSORS/MUTATORS
  public boolean isEnabled()
  {
    return Boolean.valueOf(enabled).booleanValue();
  }

  public Scheme getScheme()
  {
    return scheme;
  }

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
      return Runtime.getRuntime().availableProcessors()+1;

    return acceptors;
  }

  public boolean isDirectBuffersEnabled()
  {
    return Boolean.valueOf(enableDirectBuffers).booleanValue();
  }

  public boolean isReverseLookupEnabled()
  {
    return Boolean.valueOf(enableReverseLookup).booleanValue();
  }

  public boolean isStatisticsEnabled()
  {
    return Boolean.valueOf(enableStatistics).booleanValue();
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

    stringBuilder.append(HttpConnector.class.getSimpleName());
    stringBuilder.append("{");

    stringBuilder.append(String.format("enabled: %1$s, ", String.valueOf(isEnabled())));
    stringBuilder.append(String.format("scheme: %1$s, ", getScheme()));
    stringBuilder.append(String.format("host: %1$s, ", getHost()));
    stringBuilder.append(String.format("port: %1$s, ", String.valueOf(getPort())));

    stringBuilder.append(String.format("acceptors: %1$s, ",
      String.valueOf(getAcceptors())
    ));
    stringBuilder.append(String.format("enable_direct_buffers: %1$s, ",
      String.valueOf(isDirectBuffersEnabled())
    ));
    stringBuilder.append(String.format("enable_reverse_lookup: %1$s, ",
      String.valueOf(isReverseLookupEnabled())
    ));
    stringBuilder.append(String.format("enable_statistics: %1$s, ",
      String.valueOf(isStatisticsEnabled())
    ));

    stringBuilder.append(String.format("keystore: %1$s, ", getKeyStore()));
    if(null!=environment && environment.isDevelopment())
      stringBuilder.append(String.format("keystore_password: %1$s, ", getKeyStorePassword()));
    else
      stringBuilder.append("keystore_password: *****, ");

    stringBuilder.append(String.format("truststore: %1$s, ", getTrustStore()));
    if(null!=environment && environment.isDevelopment())
      stringBuilder.append(String.format("truststore_password: %1$s, ", getTrustStorePassword()));
    else
      stringBuilder.append("truststore_password: *****, ");

    stringBuilder.append(String.format("certificate_alias: %1$s, ",
      String.valueOf(getCertificateAlias())
    ));

    stringBuilder.append("}");

    return stringBuilder.toString();
  }
}
