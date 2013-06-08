package org.nikkii.embedhttp.example;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

import org.nikkii.embedhttp.HttpServer;
import org.nikkii.embedhttp.handler.HttpRequestHandler;
import org.nikkii.embedhttp.impl.HttpHeader;
import org.nikkii.embedhttp.impl.HttpRequest;
import org.nikkii.embedhttp.impl.HttpResponse;
import org.nikkii.embedhttp.impl.HttpStatus;

/**
 * An example of an HttpServer which can proxy requests to a specific website (Reverse proxy)
 * 
 * @author Nikki
 *
 */
public class ReverseProxyServer {

	public static void main(String[] args) throws IOException {
		final URL base = new URL("http://example.com");
		// Initialize a new instance
		HttpServer server = new HttpServer();
		// Add a request handler which will connect to the specified URL + the URI we received and return the data for the client
		server.addRequestHandler(new HttpRequestHandler() {

			@Override
			public HttpResponse handleRequest(HttpRequest request) {
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(base + request.getUri()).openConnection();
					connection.connect();
					
					HttpResponse response = new HttpResponse(HttpStatus.forCode(connection.getResponseCode()), connection.getInputStream());
					response.setResponseLength(connection.getContentLengthLong());
					for (Entry<String, List<String>> prop : connection.getHeaderFields().entrySet()) {
						String key = prop.getKey();
						// We want to undo Transfer encoding, since the server doesn't support it yet
						if(key == null || key.equals(HttpHeader.TRANSFER_ENCODING)) {
							continue;
						}
						List<String> values = prop.getValue();
						for(String s : values) {
							response.addHeader(key, s);
						}
					}
					return response;
				} catch (IOException e) {
					return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.toString());
				}
			}
			
		});
		// Bind to port 8081
		server.bind(8081);
		// Start the server
		server.start();
	}
	
}
