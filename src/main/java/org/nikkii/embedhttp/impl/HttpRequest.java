package org.nikkii.embedhttp.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nikkii.embedhttp.util.HttpUtil;

/**
 * Represents an Http request
 * 
 * @author Nikki
 * 
 */
public class HttpRequest {

	/**
	 * The session which constructed this request
	 */
	private HttpSession session;

	/**
	 * The request method
	 */
	private HttpMethod method;

	/**
	 * The requested URI
	 */
	private String uri;

	/**
	 * The request headers
	 */
	private Map<String, String> headers;

	/**
	 * Raw Query String
	 */
	private String queryString;

	/**
	 * Raw POST data (Not applicable for form-encoded, automatically parsed)
	 */
	private String data;
	
	/**
	 * The parsed GET data
	 */
	private Map<String, Object> getData;

	/**
	 * The parsed POST data
	 */
	private Map<String, Object> postData;

	/**
	 * The list of parsed cookies
	 */
	private Map<String, HttpCookie> cookies;

	/**
	 * Construct a new HTTP request
	 * 
	 * @param session
	 *            The session which initiated the request
	 * @param method
	 *            The method used to request this page
	 * @param uri
	 *            The URI of the request
	 * @param headers
	 *            The request headers
	 */
	public HttpRequest(HttpSession session, HttpMethod method, String uri, Map<String, String> headers) {
		this.session = session;
		this.method = method;
		this.uri = uri;
		this.headers = headers;
	}

	/**
	 * Get the session which initiated this request
	 * 
	 * @return The session
	 */
	public HttpSession getSession() {
		return session;
	}

	/**
	 * Get the request method
	 * 
	 * @return The request method
	 */
	public HttpMethod getMethod() {
		return method;
	}

	/**
	 * Get the request uri
	 * 
	 * @return The request uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Get the request headers
	 * 
	 * @return The request headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	/**
	 * Gets a request header
	 * @param key
	 * 			The request header key
	 * @return
	 * 			The header value
	 */
	public String getHeader(String key) {
		return headers.get(HttpUtil.capitalizeHeader(key));
	}

	/**
	 * Set the request's raw POST data
	 * 
	 * @param data
	 *            The data to set
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * Set the request's parsed GET data
	 * @param getData
	 * 			The parsed data map to set
	 */
	public void setGetData(Map<String, Object> getData) {
		this.getData = getData;
	}

	/**
	 * Set the request's parsed POST data
	 * 
	 * @param postData
	 *            The parsed data map to set
	 */
	public void setPostData(Map<String, Object> postData) {
		this.postData = postData;
	}
	
	/**
	 * Set the request's URI
	 * 
	 * @param uri
	 * 			The uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	/**
	 * Sets the request's query string
	 * @param queryString
	 * 			The query string to set
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	
	/**
	 * Gets the request's query string
	 * 
	 * @return
	 * 			The query string
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * Get the request's raw POST data
	 * 
	 * @return The request's POST data
	 */
	public String getData() {
		return data;
	}
	
	/**
	 * Get the request's parsed GET data
	 * 
	 * @return the parsed data map
	 */
	public Map<String, Object> getGetData() {
		return getData;
	}

	/**
	 * Get the request's parsed POST data
	 * 
	 * @return The parsed data map
	 */
	public Map<String, Object> getPostData() {
		return postData;
	}

	/**
	 * Set the request's cookies
	 * 
	 * @param cookies
	 * 			The cookie list
	 */
	public void setCookies(List<HttpCookie> cookies) {
		Map<String, HttpCookie> map = new HashMap<String, HttpCookie>();
		for(HttpCookie cookie : cookies) {
			map.put(cookie.getName(), cookie);
		}
		this.cookies = map;
	}
	
	/**
	 * Get a cookie with the specified name
	 * @param name
	 * 			The cookie name
	 * @return
	 * 			The cookie
	 */
	public HttpCookie getCookie(String name) {
		return cookies.get(name);
	}
	
	/**
	 * Get the request's cookies
	 * @return
	 * 			The cookie list
	 */
	public Collection<HttpCookie> getCookies() {
		return cookies == null ? null : cookies.values();
	}

	@Override
	public void finalize() {
		if (postData != null) {
			for (Object value : postData.values()) {
				if (value instanceof HttpFileUpload) {
					HttpFileUpload u = (HttpFileUpload) value;
					if (!u.getTempFile().delete()) {
						u.getTempFile().deleteOnExit();
					}
				}
			}
		}
	}
}
