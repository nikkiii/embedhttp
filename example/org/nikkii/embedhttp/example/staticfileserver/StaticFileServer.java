package org.nikkii.embedhttp.example.staticfileserver;

import java.io.File;
import java.io.IOException;

import org.nikkii.embedhttp.HttpServer;
import org.nikkii.embedhttp.handler.HttpStaticFileHandler;

/**
 * A sample HttpServer which will serve files out of the working directory
 * 
 * @author Nikki
 *
 */
public class StaticFileServer {

	public static void main(String[] args) throws IOException {
		// Setup a new instance
		HttpServer server = new HttpServer();
		// Add a request handler (You can add multiple if you want to include multiple directories)
		server.addRequestHandler(new HttpStaticFileHandler(new File(".")));
		// Bind it to port 8081
		server.bind(8081);
		// Start the server thread
		server.start();
	}
	
}
