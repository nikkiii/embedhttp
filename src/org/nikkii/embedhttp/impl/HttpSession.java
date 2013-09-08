package org.nikkii.embedhttp.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.nikkii.embedhttp.HttpServer;
import org.nikkii.embedhttp.util.HttpUtil;
import org.nikkii.embedhttp.util.MultipartReader;

/**
 * Represents an Http Session
 * 
 * @author Nikki
 * 
 */
public class HttpSession implements Runnable {

	/**
	 * The HttpServer which this session belongs to
	 */
	private HttpServer server;

	/**
	 * The socket of the client
	 */
	private Socket socket;

	/**
	 * The InputStream of the socket
	 */
	private InputStream input;

	/**
	 * The OutputStream of the socket
	 */
	private OutputStream output;

	public HttpSession(HttpServer server, Socket socket) throws IOException {
		this.server = server;
		this.socket = socket;
		this.input = socket.getInputStream();
		this.output = socket.getOutputStream();
	}

	/**
	 * Parse the Http Request
	 */
	@Override
	public void run() {
		try {
			byte[] bytes = new byte[8192];

			int pos = 0;
			
			// Read the first part of the header
			while (true) {
				int read = input.read();
				if (read == -1) {
					break;
				}
				bytes[pos] = (byte) read;
				if (pos >= 4) {
					// Find \r\n\r\n
					if (bytes[pos - 3] == '\r' && bytes[pos - 2] == '\n' && bytes[pos - 1] == '\r' && bytes[pos] == '\n') {
						break;
					}
				}
				pos++;
			}

			// Read from the header data
			BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes, 0, pos)));

			// Read the first line, defined as the status line
			String l = reader.readLine();

			// Sanity check, after not returning data the client MIGHT attempt
			// to send something back and it will end up being something we
			// cannot read.
			if (l == null) {
				sendError(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.toString());
				return;
			}

			// Otherwise continue on
			int idx = l.indexOf(' ');

			// Split out the method and path
			HttpMethod method = HttpMethod.valueOf(l.substring(0, idx));

			// If it's an known method it won't be defined in the enum
			if (method == null) {
				sendError(HttpStatus.METHOD_NOT_ALLOWED, "This server currently does not support this method.");
				return;
			}

			// The URI
			String path = l.substring(idx + 1, l.lastIndexOf(' '));

			// Parse the headers
			Map<String, String> headers = new HashMap<String, String>();
			while ((l = reader.readLine()) != null) {
				// End header.
				if (l.equals(""))
					break;

				// Headers are usually Key: Value
				String key = l.substring(0, l.indexOf(':'));
				String value = l.substring(l.indexOf(':') + 1).trim();

				// Put the header in the map, correcting the header key if
				// needed.
				headers.put(HttpUtil.capitalizeHeader(key), value);
			}

			// Close the reader used for the header
			reader.close();

			HttpRequest request = new HttpRequest(this, method, path, headers);
			
			int questionIdx = path.indexOf('?');
			if(questionIdx != -1) {
				String queryString = path.substring(questionIdx+1);
				request.setQueryString(queryString);
				request.setGetData(HttpUtil.parseData(queryString));
				
				path = path.substring(0, questionIdx);
				request.setUri(path);
			}
			
			// Parse cookies, only if the server has the capability enabled (to save time, processing power, and memory if it isn't used)
			if(headers.containsKey(HttpHeader.COOKIE) && server.hasCapability(HttpCapability.COOKIES)) {
				List<HttpCookie> cookies = new LinkedList<HttpCookie>();
				StringTokenizer tok = new StringTokenizer(headers.get(HttpHeader.COOKIE), ";");
				while(tok.hasMoreTokens()) {
					String token = tok.nextToken();
					int eqIdx = token.indexOf('=');
					if(eqIdx == -1) {
						// Invalid cookie
						continue;
					}
					String key = token.substring(0, eqIdx);
					String value = token.substring(eqIdx + 1);
					
					cookies.add(new HttpCookie(key, value));
				}
				request.setCookies(cookies);
			}

			// Read the request data
			if (method == HttpMethod.POST) {
				boolean acceptsStandard = server.hasCapability(HttpCapability.STANDARD_POST), acceptsMultipart = server.hasCapability(HttpCapability.MULTIPART_POST);
				// Make sure the server will accept POST or Multipart POST
				// before we start checking the content
				if (acceptsStandard || acceptsMultipart) {
					// Validate that there's a length header
					if (!headers.containsKey(HttpHeader.CONTENT_LENGTH)) {
						// If there isn't, send the correct response
						sendError(HttpStatus.LENGTH_REQUIRED, HttpStatus.LENGTH_REQUIRED.toString());
					} else {
						// Otherwise, continue on
						int contentLength = Integer.parseInt(headers.get(HttpHeader.CONTENT_LENGTH));

						String contentTypeHeader = headers.get(HttpHeader.CONTENT_TYPE);

						// Copy it to trim to what we need, keeping the original
						// to parse the boundary
						String contentType = contentTypeHeader;

						if (contentTypeHeader.indexOf(';') != -1) {
							contentType = contentTypeHeader.substring(0, contentTypeHeader.indexOf(';'));
						}
						// Check the content type
						if (contentType.equalsIgnoreCase("multipart/form-data")) {
							if (acceptsMultipart) {
								// The server will accept post requests with
								// multipart data
								String boundary = contentTypeHeader.substring(contentTypeHeader.indexOf(';')).trim();
								boundary = boundary.substring(boundary.indexOf('=') + 1);
								// Parse file uploads etc.
								request.setPostData(readMultipartData(boundary));
							} else {
								// The server has the multipart post
								// capabilities disabled
								sendError(HttpStatus.BAD_REQUEST, "This server does not support multipart/form-data requests.");
							}
						} else {
							if (acceptsStandard) {
								// Read the reported content length, TODO some
								// kind of check/timeout to make sure it won't
								// hang the thread?
								byte[] b = new byte[contentLength + 1];
								int read, totalRead = 0;
								while (contentLength - totalRead > 0 && (read = input.read(b, totalRead, contentLength - totalRead)) > -1) {
									totalRead += read;
								}
								// We either read all of the data, or the
								// connection closed.
								if (totalRead < contentLength) {
									sendError(HttpStatus.BAD_REQUEST, "Unable to read correct amount of data!");
								} else {
									String data = new String(b);
									if (contentType.equalsIgnoreCase("application/x-www-form-urlencoded")) {
										// It is FOR SURE regular data.
										request.setPostData(HttpUtil.parseData(data));
									} else {
										// Could be JSON or XML etc
										request.setData(data);
									}
								}
							} else {
								// The server has the Standard post capabilities
								// disabled
								sendError(HttpStatus.BAD_REQUEST, "This server does not support POST requests.");
							}
						}
					}
				} else {
					// The server has the Standard and Multipart capabilities
					// disabled
					sendError(HttpStatus.METHOD_NOT_ALLOWED, "This server does not support POST requests.");
				}
			}

			server.dispatchRequest(request);
		} catch (SocketException e) {
			//Socket was closed probably
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read the data from a multipart/form-data request
	 * 
	 * @param boundary
	 *            The boundary specified by the client
	 * @return A map of the POST data.
	 * @throws IOException
	 *             If an error occurs
	 */
	public Map<String, Object> readMultipartData(String boundary) throws IOException {
		// Boundaries are '--' + the boundary.
		boundary = "--" + boundary;
		// Form data
		Map<String, Object> form = new HashMap<String, Object>();
		// Implementation of a reader to parse out form boundaries.
		MultipartReader reader = new MultipartReader(input, boundary);
		String l;
		while ((l = reader.readLine()) != null) {
			if (!l.startsWith(boundary) || l.startsWith(boundary) && l.endsWith("--")) {
				break;
			}
			// Read headers
			Map<String, String> props = new HashMap<String, String>();
			while ((l = reader.readLine()) != null && l.trim().length() > 0) {
				// Properties
				String key = HttpUtil.capitalizeHeader(l.substring(0, l.indexOf(':')));
				String value = l.substring(l.indexOf(':') + 1);
				if (value.charAt(0) == ' ')
					value = value.substring(1);

				props.put(key, value);
			}
			// Check if the line STILL isn't null
			if (l != null) {
				String contentDisposition = props.get(HttpHeader.CONTENT_DISPOSITION);
				Map<String, String> disposition = new HashMap<String, String>();
				String[] dis = contentDisposition.split("; ");
				for (String s : dis) {
					int eqIdx = s.indexOf('=');
					if (eqIdx != -1) {
						String key = s.substring(0, eqIdx);
						String value = s.substring(eqIdx + 1).trim();
						if (value.charAt(0) == '"') {
							value = value.substring(1, value.length() - 1);
						}
						disposition.put(key, value);
					}
				}
				String name = disposition.get("name");
				if (props.containsKey(HttpHeader.CONTENT_TYPE)) {
					String fileName = disposition.get("filename");
					// Create a temporary file, this'll hopefully be deleted
					// when the request object has finalize() called
					File tmp = File.createTempFile("upload", fileName);
					// Open an output stream to the new file
					FileOutputStream output = new FileOutputStream(tmp);
					// Read the file data right from the connection, NO MEMORY
					// CACHE.
					byte[] buffer = new byte[1024];
					while (true) {
						int read = reader.readUntilBoundary(buffer, 0, buffer.length);
						if (read == -1) {
							break;
						}
						output.write(buffer, 0, read);
					}
					// Close it
					output.close();
					// Put the temp file
					form.put(name, new HttpFileUpload(fileName, tmp));
				} else {
					String value = "";
					// String value
					while ((l = reader.readLineUntilBoundary()) != null) {
						value += l;
					}
					form.put(name, value);
				}
			}
		}
		return form;
	}

	/**
	 * Send a response
	 * 
	 * @param resp
	 *            The response to send
	 * @throws IOException
	 *             If an error occurred while sending
	 */
	public void sendResponse(HttpResponse resp) throws IOException {
		sendResponse(resp, true);
	}

	/**
	 * Send a response
	 * 
	 * @param resp
	 *            The response to send
	 * @param close
	 *            Whether to close the session
	 * @throws IOException
	 *             If an error occurred while sending
	 */
	public void sendResponse(HttpResponse resp, boolean close) throws IOException {
		StringBuilder header = new StringBuilder();
		header.append("HTTP/1.1").append(' ').append(resp.getStatus().getCode()).append(' ').append(resp.getStatus());
		header.append('\r').append('\n');
		// Set the content type header if it's not already set
		if (!resp.getHeaders().containsKey(HttpHeader.CONTENT_TYPE)) {
			resp.addHeader(HttpHeader.CONTENT_TYPE, "text/html");
		}
		// Set the content length header if it's not already set
		if (!resp.getHeaders().containsKey(HttpHeader.CONTENT_LENGTH)) {
			resp.addHeader(HttpHeader.CONTENT_LENGTH, resp.getResponseLength());
		}
		// Copy in the headers
		for (Entry<String, List<Object>> entry : resp.getHeaders().entrySet()) {
			String headerName = HttpUtil.capitalizeHeader(entry.getKey());
			for(Object o : entry.getValue()) {
				header.append(headerName);
				header.append(':').append(' ');
				header.append(o);
				header.append('\r').append('\n');
			}
		}
		header.append('\r').append('\n');
		// Write the header
		output.write(header.toString().getBytes());
		// Responses can be InputStreams or Strings
		if (resp.getResponse() instanceof InputStream) {
			// InputStreams will block the session thread (No big deal) and send
			// data without loading it into memory
			InputStream res = resp.getResponse();
			try {
				// Write the body
				byte[] buffer = new byte[1024];
				while (true) {
					int read = res.read(buffer, 0, buffer.length);
					if (read == -1)
						break;
					output.write(buffer, 0, read);
				}
			} finally {
				res.close();
			}
		} else if (resp.getResponse() instanceof String) {
			output.write(((String) resp.getResponse()).getBytes());
		} else if (resp.getResponse() instanceof byte[]) {
			output.write((byte[]) resp.getResponse());
		}
		// Close it if required.
		if (close) {
			socket.close();
		}
	}

	/**
	 * Send an HttpStatus
	 * 
	 * @param status
	 *            The status to send
	 * @throws IOException
	 *             If an error occurred while sending
	 */
	public void sendError(HttpStatus status) throws IOException {
		sendResponse(new HttpResponse(status, status.toString()));
	}

	/**
	 * Send an HttpStatus with the specified error message
	 * 
	 * @param status
	 *            The status to send
	 * @param message
	 *            The error message to send
	 * @throws IOException
	 *             If an error occurred while sending
	 */
	public void sendError(HttpStatus status, String message) throws IOException {
		sendResponse(new HttpResponse(status, status.toString() + ':' + message));
	}
	
	/**
	 * Get the remote address from this session
	 * @return
	 * 		The remote address
	 */
	public InetSocketAddress getRemoteAddress() {
		return (InetSocketAddress) socket.getRemoteSocketAddress();
	}
}
