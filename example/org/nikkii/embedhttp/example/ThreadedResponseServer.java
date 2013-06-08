package org.nikkii.embedhttp.example;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.nikkii.embedhttp.HttpServer;
import org.nikkii.embedhttp.handler.HttpRequestHandler;
import org.nikkii.embedhttp.impl.HttpCapability;
import org.nikkii.embedhttp.impl.HttpRequest;
import org.nikkii.embedhttp.impl.HttpResponse;
import org.nikkii.embedhttp.impl.HttpStatus;

/**
 * A sample HttpServer which will allow Threaded/Delayed responses
 * 
 * @author Nikki
 *
 */
public class ThreadedResponseServer {
	
	private static ScheduledExecutorService service = Executors.newScheduledThreadPool(8);

	public static void main(String[] args) throws IOException {
		// Setup a new instance
		HttpServer server = new HttpServer();
		// Enable threaded/delayed responses
		server.setCapability(HttpCapability.THREADEDRESPONSE, true);
		// Add a request handler (You can add multiple if you want to include multiple directories)
		server.addRequestHandler(new HttpRequestHandler() {
			@Override
			public HttpResponse handleRequest(final HttpRequest request) {
				// Do some processing
				service.schedule(new Runnable() {
					public void run() {
						try {
							request.getSession().sendResponse(new HttpResponse(HttpStatus.OK, "Hello, world!"));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}, 1000, TimeUnit.MILLISECONDS);
				return null;
			}
		});
		// Bind it to port 8081
		server.bind(8081);
		// Start the server thread
		server.start();
	}
	
}
