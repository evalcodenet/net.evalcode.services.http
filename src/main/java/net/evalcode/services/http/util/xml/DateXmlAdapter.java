package net.evalcode.services.http.util.xml;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import net.evalcode.services.http.HttpComponent;
import net.evalcode.services.http.internal.util.Messages;


/**
 * DateXmlAdapter
 *
 * @author carsten.schipke@gmail.com
 */
public class DateXmlAdapter extends XmlAdapter<String, Date>
{
  // MEMBERS
  final DateFormat format;


  // CONSTRUCTION
  public DateXmlAdapter()
  {
    this(Messages.DATE_FORMAT.get());
  }

  public DateXmlAdapter(final String pattern)
  {
    this(pattern, HttpComponent.get().getTimeZone(), HttpComponent.get().getLocale());
  }

  public DateXmlAdapter(final String pattern, final TimeZone timeZone, final Locale locale)
  {
    super();

    this.format=new SimpleDateFormat(pattern, locale);
    this.format.setTimeZone(timeZone);
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public String marshal(final Date value)
  {
    return format.format(value);
  }

  @Override
  public Date unmarshal(final String value) throws ParseException
  {
    return format.parse(value);
  }
}
