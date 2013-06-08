package org.nikkii.embedhttp.example;

import java.io.IOException;

import org.nikkii.embedhttp.HttpServer;
import org.nikkii.embedhttp.handler.HttpRequestHandler;
import org.nikkii.embedhttp.impl.HttpCapability;
import org.nikkii.embedhttp.impl.HttpMethod;
import org.nikkii.embedhttp.impl.HttpRequest;
import org.nikkii.embedhttp.impl.HttpResponse;
import org.nikkii.embedhttp.impl.HttpStatus;

/**
 * A sample HttpServer which serves the requested path back to requests
 * 
 * @author Nikki
 *
 */
public class DebugServer {

	public static void main(String[] args) throws IOException {
		HttpServer server = new HttpServer();
		server.setCapability(HttpCapability.MULTIPART_POST, true);
		server.addRequestHandler(new HttpRequestHandler() {
			@Override
			public HttpResponse handleRequest(HttpRequest request) {
				StringBuilder contents = new StringBuilder();
				contents.append("Host: " + request.getHeaders().get("Host"));
				contents.append("<br />");
				contents.append("URI: " + request.getUri());
				contents.append("<br />");
				contents.append("Method: " + request.getMethod());
				contents.append("<br />");
				contents.append("Headers: " + request.getHeaders());
				contents.append("<br />");
				contents.append("GET Data: " + request.getGetData());
				contents.append("<br />");
				if(request.getMethod() == HttpMethod.POST) {
					contents.append("POST Data: " + request.getPostData());
					contents.append("<br />");
				}
				return new HttpResponse(HttpStatus.OK, contents.toString());
			}
		});
		server.bind(8081);
		server.start();
	}

}
