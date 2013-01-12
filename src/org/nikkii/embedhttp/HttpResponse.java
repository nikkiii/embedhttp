package org.nikkii.embedhttp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
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
	private Map<String, String> headers = new HashMap<String, String>();
	
	/**
	 * The response (InputStream, String or byte array)
	 */
	private Object response;
	
	/**
	 * The response length
	 */
	private long responseLength = 0;
	
	/**
	 * Construct a new Http Response from an InputStream.
	 * Note: When using this make sure to add a request header for length!
	 * The auto-calculated header WILL NOT be accurate.
	 * 
	 * @param status
	 * 			The response status
	 * @param response
	 * 			The response data
	 */
	public HttpResponse(HttpStatus status, InputStream response) {
		this.status = status;
		this.response = response;
		try {
			this.responseLength = response.available();
		} catch (IOException e) {
			//This shouldn't happen.
		}
	}
	
	/**
	 * Construct a new Http response with a string as the data
	 * @param status
	 * 			The response status
	 * @param response
	 * 			The response data
	 */
	public HttpResponse(HttpStatus status, String response) {
		this.status = status;
		this.response = response;
		this.responseLength = response.length();
	}
	
	/**
	 * Construct a new Http response with a byte array as the data
	 * @param status
	 * 			The response status
	 * @param response
	 * 			The response data
	 */
	public HttpResponse(HttpStatus status, byte[] response) {
		this.status = status;
		this.response = response;
		this.responseLength = response.length;
	}
	
	/**
	 * Get the Http response status
	 * @return
	 * 		The response status
	 */
	public HttpStatus getStatus() {
		return status;
	}
	
	/**
	 * Add a header to the response
	 * @param key
	 * 			The header name
	 * @param value
	 * 			The header value
	 */
	public void addHeader(String key, Object value) {
		headers.put(key, value.toString());
	}
	
	/**
	 * Get a response header
	 * @param key
	 * 			The header name (Case sensitive)
	 * @return
	 * 			The header value
	 */
	public String getHeader(String key) {
		return headers.get(key);
	}

	/**
	 * Get the response headers
	 * @return
	 * 			The header map
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * Get the response as the specified type
	 * @return
	 * 		The response
	 */
	@SuppressWarnings("unchecked")
	public <T> T getResponse() {
		return (T) response;
	}

	/**
	 * Get the response length
	 * @return
	 * 		The response length, may not be accurate
	 */
	public long getResponseLength() {
		return responseLength;
	}

	/**
	 * Set the response length
	 * @param length
	 * 			The response length to set.
	 */
	public void setResponseLength(long responseLength) {
		this.responseLength = responseLength;
	}
}
