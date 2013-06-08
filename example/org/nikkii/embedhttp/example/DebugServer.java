package org.nikkii.embedhttp.example;

import java.io.IOException;
import java.util.Collection;

import org.nikkii.embedhttp.HttpServer;
import org.nikkii.embedhttp.handler.HttpRequestHandler;
import org.nikkii.embedhttp.impl.HttpCapability;
import org.nikkii.embedhttp.impl.HttpCookie;
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
		server.setCapability(HttpCapability.COOKIES, true);
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
				Collection<HttpCookie> cookies = request.getCookies();
				if(cookies != null) {
					contents.append("Cookies: " + cookies);
					contents.append("<br />");
				}
				if(request.getMethod() == HttpMethod.POST) {
					contents.append("POST Data: " + request.getPostData());
					contents.append("<br />");
				}
				HttpResponse resp = new HttpResponse(HttpStatus.OK, contents.toString());
				resp.addCookie(new HttpCookie("lastVisit", Long.toString(System.currentTimeMillis())));
				resp.addCookie(new HttpCookie("testCookie", "testing"));
				return resp;
			}
		});
		server.bind(8081);
		server.start();
	}

}
