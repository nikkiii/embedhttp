package org.nikkii.embedhttp.handler;

import org.nikkii.embedhttp.impl.HttpRequest;
import org.nikkii.embedhttp.impl.HttpResponse;

/**
 * Allows asynchronous requests to be processed without the need to always return "null".
 *
 * @author Nikki
 */
public abstract class AsyncHttpRequestHandler implements HttpRequestHandler {
	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		handleAsyncRequest(request);
		return null;
	}

	/**
	 * Handle a request.
	 *
	 * @param request
	 *            The request to handle
	 */
	public abstract void handleAsyncRequest(HttpRequest request);
}
