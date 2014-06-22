package org.nikkii.embedhttp.handler;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.nikkii.embedhttp.impl.HttpRequest;
import org.nikkii.embedhttp.impl.HttpResponse;
import org.nikkii.embedhttp.impl.HttpStatus;

/**
 * A RequestHandler to handle serving of static files from inside a jar file
 * 
 * @author Nikki
 * @author boly38
 * 
 */
public class HttpStaticJarFileHandler implements HttpRequestHandler {
	
	/**
	 * The base path inside the jar file to append
	 */
	private String path;
	
	/**
	 * Construct a new static jar content handler with a base path of '/'
	 */
	public HttpStaticJarFileHandler() {
		this.path = "/";
	}
	/**
	 * Construct a new static jar content handler
	 * 
	 * @param path
	 * 			The base path in the jar file for resources
	 */
	public HttpStaticJarFileHandler(String path) {
		this.path = path;
	}

	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		String uri = request.getUri();
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			uri = uri.replace("%20", " ");
		}
		InputStream resourceAsStream = getClass().getResourceAsStream(path + uri.substring(1));
		if (resourceAsStream != null) {
			HttpResponse res = new HttpResponse(HttpStatus.OK, resourceAsStream);
			return res;
		}
		return null;
	}

}