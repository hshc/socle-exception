package fr.hsh.socle.context.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

import fr.hsh.socle.context.ThreadContextParams;

/**
 * Description: <br>
 * This class MUST be inherited by a class packaged with the MANIFEST.MF of the web app.<br>
 * Assuming InitContextFilter extends AbsInitContextFilter , then you should declare it in the web.xml as following:
 * <blockquote>
 * <br>{@code  <filter>}
 * <br>{@code    <display-name>InitContextFilter</display-name>}
 * <br>{@code    <filter-name>InitContextFilter</filter-name>}
 * <br>{@code    <filter-class>fr.hsh.socle.InitContextFilter</filter-class>}
 * <br>{@code  </filter>}
 * <br>{@code  <filter-mapping>}
 * <br>{@code    <filter-name>InitContextFilter</filter-name>}
 * <br>{@code    <url-pattern>/*</url-pattern>}
 * <br>{@code  </filter-mapping>}
 * <br>{@code</code>}
 * </blockquote>
 * 
 * @version
 * @author
 */
public abstract class AbsInitContextFilter implements Filter {

	/**
	 * Description: In order to feed the ContextDescriptor with the right MANIFEST.MF, please implement this method in a class that is to be packaged with the MANIFEST.MF as so:
	 * <dd>
	 * <code>@Override</code>
	 * <br><code>protected Class getRealizingClass() {</code>
	 * <blockquote><code>return this.getClass();</code></blockquote>
	 * <code>}</code>
	 * </dd>
	 * 
	 * @return The class from witch the MANIFEST is found.
	 */
	protected abstract Class getRealizingClass();

	/**
	 * Default constructor. 
	 */
	public AbsInitContextFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		String lUser = null;
		// Basic, NTLM, Digest, Negotiate
		lUser = this.getBasicAuthenticationUser(request);

		ThreadContextParams.initializeContext(lUser, this.getRealizingClass());

		// pass the request along the filter chain
		chain.doFilter(request, response);

		ThreadContextParams.cleanContext();
	}

	private String getBasicAuthenticationUser(final ServletRequest request) {
		String rfc2617_credentials = null;
		String rfc2617_base64_user_pass = null;
		byte[] user_pass = null;
		String rfc2617_user_pass = null;
		String rfc2617_userid = null;
		String Basic_ = "Basic ";

		rfc2617_credentials = ((HttpServletRequest)request).getHeader("Authorization");
		if ((rfc2617_credentials != null) && (rfc2617_credentials.length() > Basic_.length())) {
			rfc2617_base64_user_pass = rfc2617_credentials.substring("Basic ".length());
			if (rfc2617_base64_user_pass != null) {
				user_pass = Base64.decodeBase64(rfc2617_base64_user_pass.getBytes());
				if (user_pass != null) {
					rfc2617_user_pass = new String(user_pass);
					int i = rfc2617_user_pass.indexOf(":");
					if (i > 0) {
						rfc2617_userid = rfc2617_user_pass.substring(0, i);
					}
				}
			}
		}
		return rfc2617_userid;
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(final FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
