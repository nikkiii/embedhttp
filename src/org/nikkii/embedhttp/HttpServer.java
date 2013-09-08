package org.nikkii.embedhttp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.nikkii.embedhttp.handler.HttpRequestHandler;
import org.nikkii.embedhttp.impl.HttpCapability;
import org.nikkii.embedhttp.impl.HttpRequest;
import org.nikkii.embedhttp.impl.HttpResponse;
import org.nikkii.embedhttp.impl.HttpSession;
import org.nikkii.embedhttp.impl.HttpStatus;

/**
 * The main HttpServer class
 * 
 * @author Nikki
 * 
 */
public class HttpServer implements Runnable {

	/**
	 * The request service
	 */
	private ExecutorService service = Executors.newCachedThreadPool();

	/**
	 * The server socket
	 */
	private ServerSocket socket;

	/**
	 * A list of HttpRequestHandlers for the server
	 */
	private List<HttpRequestHandler> handlers = new LinkedList<HttpRequestHandler>();

	@SuppressWarnings("serial")
	private BitSet capabilities = new BitSet() {
		{
			set(HttpCapability.HTTP_1_1.ordinal(), true);
			set(HttpCapability.STANDARD_POST.ordinal(), true);
		}
	};

	private boolean running;

	/**
	 * Construct a new HttpServer
	 */
	public HttpServer() {

	}

	/**
	 * Construct and bind the HttpServer
	 * 
	 * @param port
	 *            The port to bind to
	 * @throws IOException
	 */
	public HttpServer(int port) throws IOException {
		bind(port);
	}

	/**
	 * Bind the server to the specified port
	 * 
	 * @param port
	 *            The port to bind to
	 * @throws IOException
	 */
	public void bind(int port) throws IOException {
		bind(new InetSocketAddress(port));
	}

	/**
	 * Bind the server to the specified SocketAddress
	 * 
	 * @param addr
	 *            The address to bind to
	 * @throws IOException
	 *             If an error occurs while binding, usually port already in
	 *             use.
	 */
	public void bind(SocketAddress addr) throws IOException {
		socket = new ServerSocket();
		socket.bind(addr);
	}

	/**
	 * Start the server in a new thread
	 */
	public void start() {
		if (socket == null) {
			throw new RuntimeException("Cannot bind a server that has not been initialized!");
		}
		running = true;
		
		Thread t = new Thread(this);
		t.setName("HttpServer");
		t.start();
	}
	
	/**
	 * Stop the server
	 */
	public void stop() {
		running = false;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Run and process requests.
	 */
	@Override
	public void run() {
		while (running) {
			try {
				// Read the request
				service.execute(new HttpSession(this, socket.accept()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Set a capability
	 * 
	 * @param capability
	 *            The capability to set
	 * @param value
	 *            The value to set
	 */
	public void setCapability(HttpCapability capability, boolean value) {
		capabilities.set(capability.ordinal(), value);
	}

	/**
	 * Add a request handler
	 * 
	 * @param handler
	 *            The request handler to add
	 */
	public void addRequestHandler(HttpRequestHandler handler) {
		handlers.add(handler);
	}

	/**
	 * Remove a request handler
	 * 
	 * @param handler
	 *            The request handler to remove
	 */
	public void removeRequestHandler(HttpRequestHandler handler) {
		handlers.remove(handler);
	}

	/**
	 * Dispatch a request to all handlers
	 * 
	 * @param httpRequest
	 *            The request to dispatch
	 * @throws IOException
	 *             If an error occurs while sending the response from the
	 *             handler
	 */
	public void dispatchRequest(HttpRequest httpRequest) throws IOException {
		for (HttpRequestHandler handler : handlers) {
			HttpResponse resp = handler.handleRequest(httpRequest);
			if (resp != null) {
				httpRequest.getSession().sendResponse(resp);
				return;
			}
		}
		if(!hasCapability(HttpCapability.THREADEDRESPONSE)) {
			// If it's still here nothing handled it.
			httpRequest.getSession().sendResponse(new HttpResponse(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.toString()));
		}
	}

	/**
	 * Check if the server has a specific capability defined
	 * 
	 * @param capability
	 *            The capability to check
	 * @return The capability flag
	 */
	public boolean hasCapability(HttpCapability capability) {
		return capabilities.get(capability.ordinal());
	}
}
