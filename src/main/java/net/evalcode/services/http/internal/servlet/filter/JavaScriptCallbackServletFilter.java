package net.evalcode.services.http.internal.servlet.filter;


import java.io.IOException;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;


/**
 * JavaScriptCallbackServletFilter
 *
 * <p> Work-around for "Same Origin Policy".
 * <p> Enables cross-site requests for net.evalcode.services HTTP/REST+JSON services.
 *
 * <pre>
 *   <script type="text/javascript">
 *
 *     // [1] JSON response callback.
 *     myCallback=function(json)
 *     {
 *       alert(json);
 *     }
 *
 *     // [2] create script-tag pointing to REST+JSON service.
 *     var script=document.createElement("script");
 *     script.setAttribute("src", "http://domain.tld/path?append-js-callback=myCallback");
 *     script.setAttribute("type", "text/javascript");
 *
 *     // [3] append script-tag.
 *     document.body.appendChild(script);
 *   </script>
 *
 *   The browser will request the REST service for given url in listed script-tag
 *   and execute the javascript callback specified by optional query parameter
 *   "append-js-callback", by passing it the actual response.
 * <pre>
 *
 * TODO Check ResponseListener - maybe better solution.
 *
 * @author carsten.schipke@gmail.com
 */
@Singleton
public class JavaScriptCallbackServletFilter implements Filter
{
  // OVERRIDES/IMPLEMENTS
  @Override
  public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
      final FilterChain filterChain)
    throws IOException, ServletException
  {
    String callback=StringUtils.substringAfterLast(
      ((HttpServletRequest)servletRequest).getQueryString(), "append-js-callback="
    );

    callback=StringUtils.substringBefore(callback, "&");

    boolean appendJsCallback=false;
    if(null!=callback && !callback.isEmpty())
    {
      servletResponse.getOutputStream().write(callback.concat("(").getBytes());
      appendJsCallback=true;
    }

    filterChain.doFilter(servletRequest, servletResponse);

    if(appendJsCallback)
      servletResponse.getOutputStream().write(");".getBytes());
  }

  @Override
  public void init(final FilterConfig filterConfig) throws ServletException
  {
    // Do nothing ...
  }

  @Override
  public void destroy()
  {
    // Do nothing ...
  }
}
