package org.nikkii.embedhttp.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An Http Response.
 * 
 * @author Nikki
 * 
 */
public class HttpResponse {

	/**
	 * The Http response status
	 */
	private HttpStatus status;

	/**
	 * The Http response headers
	 */
	private Map<String, List<Object>> headers = new HashMap<String, List<Object>>();

	/**
	 * The response (InputStream, String or byte array)
	 */
	private Object response;

	/**
	 * The response length
	 */
	private long responseLength = 0;

	/**
	 * Construct a new empty Http response
	 */
	public HttpResponse() {
		this.status = HttpStatus.OK;
		this.response = "";
	}

	/**
	 * Construct a new empty Http response
	 */
	public HttpResponse(HttpStatus status) {
		this.status = status;
		this.response = "";
	}

	/**
	 * Construct a new Http Response from an InputStream. Note: When using this
	 * make sure to add a request header for length! The auto-calculated header
	 * WILL NOT be accurate.
	 * 
	 * @param status
	 *            The response status
	 * @param response
	 *            The response data
	 */
	public HttpResponse(HttpStatus status, InputStream response) {
		this.status = status;
		this.response = response;
		try {
			this.responseLength = response.available();
		} catch (IOException e) {
			// This shouldn't happen.
		}
	}

	/**
	 * Construct a new Http response with a string as the data
	 * 
	 * @param status
	 *            The response status
	 * @param response
	 *            The response data
	 */
	public HttpResponse(HttpStatus status, String response) {
		this.status = status;
		this.response = response;
		this.responseLength = response.length();
	}

	/**
	 * Construct a new Http response with a byte array as the data
	 * 
	 * @param status
	 *            The response status
	 * @param response
	 *            The response data
	 */
	public HttpResponse(HttpStatus status, byte[] response) {
		this.status = status;
		this.response = response;
		this.responseLength = response.length;
	}

	/**
	 * Get the Http response status
	 * 
	 * @return The response status
	 */
	public HttpStatus getStatus() {
		return status;
	}
	
	/**
	 * Adds a cookie to this response
	 * @param cookie
	 * 			The cookie to add
	 */
	public void addCookie(HttpCookie cookie) {
		addHeader(HttpHeader.SET_COOKIE, cookie.toHeader());
	}

	/**
	 * Add a header to the response
	 * 
	 * @param key
	 *            The header name
	 * @param value
	 *            The header value
	 */
	public void addHeader(String key, Object value) {
		List<Object> values = headers.get(key);
		if(values == null) {
			headers.put(key, values = new ArrayList<Object>());
		}
		values.add(value);
	}

	/**
	 * Get a response header
	 * 
	 * @param key
	 *            The header name (Case sensitive)
	 * @return The header value
	 */
	public List<Object> getHeaders(String key) {
		return headers.get(key);
	}

	/**
	 * Get the response headers
	 * 
	 * @return The header map
	 */
	public Map<String, List<Object>> getHeaders() {
		return headers;
	}

	/**
	 * Get the response as the specified type
	 * 
	 * @return The response
	 */
	@SuppressWarnings("unchecked")
	public <T> T getResponse() {
		return (T) response;
	}
	
	/**
	 * Set the response string
	 * 
	 * @param response
	 * 			The response to set
	 */
	public void setResponse(String response) {
		this.response = response;
		this.responseLength = response.length();
	}

	/**
	 * Set the response Input Stream
	 * 
	 * @param response
	 * 			The response to set
	 */
	public void setResponse(InputStream response) {
		this.response = response;
		try {
			this.responseLength = response.available();
		} catch (IOException e) {
			// This shouldn't happen.
		}
	}

	/**
	 * Get the response length
	 * 
	 * @return The response length, may not be accurate
	 */
	public long getResponseLength() {
		return responseLength;
	}

	/**
	 * Set the response length
	 * 
	 * @param length
	 *            The response length to set.
	 */
	public void setResponseLength(long responseLength) {
		this.responseLength = responseLength;
	}
}
