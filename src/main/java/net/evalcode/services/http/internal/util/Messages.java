package net.evalcode.services.http.internal.util;


import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * Messages
 *
 * @author carsten.schipke@gmail.com
 */
public enum Messages
{
  // net.evalcode.services.http.xml
  DATE_FORMAT("http.xml.date_format");


  // PREDEFINED PROPERTIES
  private final ResourceBundle resourceBundle=ResourceBundle.getBundle(
    "net.evalcode.services.http.messages"
  );


  // MEMBERS
  final String key;


  // CONSTRUCTION
  Messages(final String key)
  {
    this.key=key;
  }


  // ACCESSORS/MUTATORS
  public String get()
  {
    try
    {
      return resourceBundle.getString(key);
    }
    catch(final MissingResourceException e)
    {
      return '!'+key+'!';
    }
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public String toString()
  {
    return get();
  }
}
