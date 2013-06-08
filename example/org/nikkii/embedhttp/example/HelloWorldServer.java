package org.nikkii.embedhttp.example;

import java.io.IOException;

import org.nikkii.embedhttp.HttpServer;
import org.nikkii.embedhttp.handler.HttpRequestHandler;
import org.nikkii.embedhttp.impl.HttpRequest;
import org.nikkii.embedhttp.impl.HttpResponse;
import org.nikkii.embedhttp.impl.HttpStatus;

/**
 * A sample HttpServer which serves 'Hello, world!' to every request
 * 
 * @author Nikki
 *
 */
public class HelloWorldServer {

	public static void main(String[] args) throws IOException {
		HttpServer server = new HttpServer();
		server.addRequestHandler(new HttpRequestHandler() {
			@Override
			public HttpResponse handleRequest(HttpRequest request) {
				return new HttpResponse(HttpStatus.OK, "Hello, world!");
			}
		});
		server.bind(8081);
		server.start();
	}

}
