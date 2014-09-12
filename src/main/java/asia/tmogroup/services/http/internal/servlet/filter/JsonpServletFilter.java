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


/**
 * JsonpServletFilter
 *
 * <p> Work-around for "Same Origin Policy".
 * <p> Enables cross-site requests for net.evalcode.services HTTP(S)/REST+JSON services.
 *
 * <pre>
 *   <script type="text/javascript">
 *
 *     // [1] JSONP callback.
 *     myCallback=function(json)
 *     {
 *       alert(json);
 *     }
 *
 *     // [2] create script-tag pointing to REST+JSONP service.
 *     var script=document.createElement("script");
 *     script.setAttribute("src", "http://domain.tld/path?jsonp=myCallback");
 *     script.setAttribute("type", "text/javascript");
 *
 *     // [3] append script-tag.
 *     document.body.appendChild(script);
 *   </script>
 *
 *   The browser will request the REST service for given url in listed script-tag
 *   and execute the javascript callback specified by optional query parameter
 *   "jsonp", by passing it the actual response.
 * <pre>
 *
 * TODO Check ResponseListener - maybe better solution.
 *
 * @author carsten.schipke@gmail.com
 */
@Singleton
public class JsonpServletFilter implements Filter
{
  // OVERRIDES/IMPLEMENTS
  @Override
  public void doFilter(final ServletRequest servletRequest,
      final ServletResponse servletResponse, final FilterChain filterChain)
    throws IOException, ServletException
  {
    final String callback=((HttpServletRequest)servletRequest).getParameter("jsonp");

    if(null==callback)
    {
      filterChain.doFilter(servletRequest, servletResponse);
    }
    else
    {
      servletResponse.getOutputStream().write(callback.concat("(").getBytes());

      filterChain.doFilter(servletRequest, servletResponse);

      servletResponse.getOutputStream().write(");".getBytes());
    }
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
