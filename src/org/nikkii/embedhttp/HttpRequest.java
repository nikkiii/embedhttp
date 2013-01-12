package org.nikkii.embedhttp;

import java.util.Map;

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
	 * Raw POST data (Not applicable for form-encoded, automatically parsed)
	 */
	private String data;
	
	/**
	 * The parsed POST data
	 */
	private Map<String, Object> postData;
	
	/**
	 * Construct a new HTTP request
	 * @param session
	 * 			The session which initiated the request
	 * @param method
	 * 			The method used to request this page
	 * @param uri
	 * 			The URI of the request
	 * @param headers
	 * 			The request headers
	 */
	public HttpRequest(HttpSession session, HttpMethod method, String uri, Map<String, String> headers) {
		this.session = session;
		this.method = method;
		this.uri = uri;
		this.headers = headers;
	}

	/**
	 * Get the session which initiated this request
	 * @return
	 * 		The session
	 */
	public HttpSession getSession() {
		return session;
	}

	/**
	 * Get the request method
	 * @return
	 * 		The request method
	 */
	public HttpMethod getMethod() {
		return method;
	}

	/**
	 * Get the request uri
	 * @return
	 * 		The request uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Get the request headers
	 * @return
	 * 		The request headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	/**
	 * Set the request's raw POST data
	 * @param data
	 * 			The data to set
	 */
	public void setData(String data) {
		this.data = data;
	}
	
	/**
	 * Set the request's parsed POST data
	 * @param postData
	 * 			The parsed data map to set
	 */
	public void setPostData(Map<String, Object> postData) {
		this.postData = postData;
	}
	
	/**
	 * Get the request's POST data
	 * @return
	 * 		The request's POST data
	 */
	public String getData() {
		return data;
	}
	
	/**
	 * Get the request's parsed POST data
	 * @return
	 * 			The parsed data map
	 */
	public Map<String, Object> getPostData() {
		return postData;
	}
	
	@Override
	public void finalize() {
		if(postData != null) {
			for(Object value : postData.values()) {
				if(value instanceof HttpFileUpload) {
					HttpFileUpload u = (HttpFileUpload) value;
					if(!u.getTempFile().delete()) {
						u.getTempFile().deleteOnExit();
					}
				}
			}
		}
	}
}
