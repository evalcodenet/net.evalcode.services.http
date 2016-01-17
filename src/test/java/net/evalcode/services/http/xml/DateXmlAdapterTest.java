package net.evalcode.services.http.xml;


import static org.junit.Assert.assertEquals;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import net.evalcode.services.http.util.xml.DateXmlAdapter;
import org.junit.Test;


/**
 * Test {@link DateXmlAdapter}
 *
 * @author carsten.schipke@gmail.com
 */
public class DateXmlAdapterTest
{
  // TESTS
  @Test
  public void testMarshal() throws Exception
  {
    final DateXmlAdapter adapter0=new DateXmlAdapter("yyyy-MM-dd HH:mm:ss", TimeZone.getDefault(), Locale.getDefault());
    final DateXmlAdapter adapter1=new DateXmlAdapter("yyyy/MM/dd HH:mm:ss", TimeZone.getDefault(), Locale.getDefault());
    final DateXmlAdapter adapter2=new DateXmlAdapter("HH:mm:ss dd.MM.yyyy", TimeZone.getDefault(), Locale.getDefault());

    final String value0="1970-01-01 00:00:00";
    final String value1="1970/01/01 00:00:00";
    final String value2="00:00:00 01.01.2049";

    final Date date0=adapter0.unmarshal(value0);
    final Date date1=adapter1.unmarshal(value1);
    final Date date2=adapter2.unmarshal(value2);

    final String result0=adapter0.marshal(date0);
    final String result1=adapter1.marshal(date1);
    final String result2=adapter2.marshal(date2);

    assertEquals(value0, result0);
    assertEquals(value1, result1);
    assertEquals(value2, result2);
  }
}
