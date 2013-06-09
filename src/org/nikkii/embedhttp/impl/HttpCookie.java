package org.nikkii.embedhttp.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * A basic Http Cookie implementation
 * 
 * @author Nikki
 *
 */
public class HttpCookie {
	
	/**
	 * The RFC1123 date pattern
	 */
	private final static String RFC1123_PATTERN =
	        "EEE, dd MMM yyyy HH:mm:ss z";
	
	/**
	 * GMT Timezone
	 */
	private final static TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
	
	/**
	 * The RFC1123 Date Formatter
	 */
	private static DateFormat RFC_DATEFORMAT = new SimpleDateFormat(RFC1123_PATTERN, Locale.US);
	
	static {
		RFC_DATEFORMAT.setTimeZone(GMT_ZONE);
	}

	/**
	 * The cookie name
	 */
	private String name;
	
	/**
	 * The cookie value
	 */
	private String value;
	
	/**
	 * The cookie's expire time
	 */
	private long expireTime = -1;
	
	/**
	 * The cookie's path
	 */
	private String path;
	
	/**
	 * The cookie's domain
	 */
	private String domain;
	
	/**
	 * Whether or not the cookie is only available on secure connections
	 */
	private boolean secure;
	
	/**
	 * Constructs a new basic cookie
	 * @param name
	 * 			The cookie name
	 * @param value
	 * 			The cookie value
	 */
	public HttpCookie(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Gets the cookie's name
	 * @return
	 * 		The cookie name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the cookie's value
	 * @return
	 * 		The cookie value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Gets the cookie's expire time in milliseconds from epoch
	 * @return
	 * 		The expire time
	 */
	public long getExpireTime() {
		return expireTime;
	}
	
	/**
	 * Returns whether or not the cookie has expired
	 * @return
	 * 		True if expireTime > currentTime
	 */
	public boolean hasExpired() {
		return expireTime > System.currentTimeMillis();
	}
	
	/**
	 * Gets the cookie's path
	 * @return
	 * 		The cookie's path
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Gets the cookie's domain
	 * @return
	 * 		The cookie's domain
	 */
	public String getDomain() {
		return domain;
	}
	
	/**
	 * Gets whether the cookie is available only on secure connections
	 * @return
	 * 		The cookie's secure setting
	 */
	public boolean isSecure() {
		return secure;
	}
	
	/**
	 * Transforms this cookie into a Set-Cookie header field
	 * @return
	 * 			The header
	 */
	public String toHeader() {
		StringBuilder header = new StringBuilder();
		header.append(name).append('=').append(value);
		if(domain != null) {
			header.append("; ").append("Domain=").append(domain);
		}
		if(path != null) {
			header.append("; ").append("Path=").append(path);
		}
		if(expireTime > -1) {
			header.append("; ").append("Expires=").append(RFC_DATEFORMAT.format(new Date(expireTime)));
		}
		if(secure) {
			header.append("; ").append("Secure");
		}
		return header.toString();
	}
	
	@Override
	public String toString() {
		return name + '=' + value;
	}
	
}
