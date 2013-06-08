package org.nikkii.embedhttp.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.nikkii.embedhttp.impl.HttpRequest;
import org.nikkii.embedhttp.impl.HttpResponse;
import org.nikkii.embedhttp.impl.HttpStatus;

/**
 * A RequestHandler to handle serving of simple static files
 * 
 * @author Nikki
 * 
 */
public class HttpStaticFileHandler implements HttpRequestHandler {

	/**
	 * The document root
	 */
	private File documentRoot;

	/**
	 * The document root's path, cached to save time.
	 */
	private String documentRootPath;

	/**
	 * Construct a new static file server
	 * 
	 * @param documentRoot
	 *            The document root
	 */
	public HttpStaticFileHandler(File documentRoot) {
		this.documentRoot = documentRoot;
	}

	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		String uri = request.getUri();
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			uri = uri.replace("%20", " ");
		}
		File file = new File(documentRoot, uri);
		if (file.exists() && !file.isDirectory()) {
			try {
				if (documentRootPath == null) {
					documentRootPath = documentRoot.getAbsolutePath();
					if (documentRootPath.endsWith("/") || documentRootPath.endsWith(".")) {
						documentRootPath = documentRootPath.substring(0, documentRootPath.length() - 2);
					}
				}
				String requestPath = file.getCanonicalPath();
				if (requestPath.endsWith("/")) {
					requestPath = requestPath.substring(0, requestPath.length() - 2);
				}
				if (!requestPath.startsWith(documentRootPath)) {
					return new HttpResponse(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.toString());
				}
			} catch (IOException e) {
				return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.toString());
			}
			try {
				HttpResponse res = new HttpResponse(HttpStatus.OK, new FileInputStream(file));
				res.setResponseLength(file.length());
				return res;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
