package org.nikkii.embedhttp.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * A class to read Multipart form data while respecting the boundaries.
 * 
 * @author Nikki
 * 
 */
public class MultipartReader {

	/**
	 * The input stream to read from
	 */
	private InputStream input;

	/**
	 * A temporary buffer with leftover bytes from previous reads
	 */
	private ByteBuffer leftover;

	/**
	 * Flag whether to ignore the next data read (Due to the file ending
	 * usually)
	 */
	private boolean ignoreNextRead = false;

	/**
	 * The boundary as a normal string
	 */
	private String boundary;

	/**
	 * The boundary as a byte array
	 */
	private byte[] boundaryBytes;

	/**
	 * Construct a new MultipartReader
	 * 
	 * @param input
	 *            The input stream
	 * @param boundary
	 *            The boundary
	 */
	public MultipartReader(InputStream input, String boundary) {
		this.input = input;
		this.boundary = boundary;
		this.boundaryBytes = boundary.getBytes();
	}

	/**
	 * Read a single byte, except it will try to use the leftover bytes from the
	 * previous read first.
	 * 
	 * @return The single byte read
	 * @throws IOException
	 *             If an error occurs
	 */
	public int read() throws IOException {
		if (leftover != null && leftover.remaining() > 0) {
			int b = leftover.get();
			if (leftover.remaining() == 0) {
				leftover = null;
			}
			return b;
		}
		return input.read();
	}

	/**
	 * Similar to the read(byte[] b, int off, int len) method in InputStream
	 * Will read as much as it can until either the boundary is found or it runs
	 * out of data.
	 * 
	 * @param b
	 *            The byte array to store to
	 * @param off
	 *            The offset in the byte array
	 * @param len
	 *            The maximum length to read
	 * @return The amount of bytes read, or -1 for none
	 * @throws IOException
	 *             If an error occurred while reading
	 */
	public int readUntilBoundary(byte[] b, int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if (off < 0 || len < 0 || len > b.length - off) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}

		if (ignoreNextRead) {
			ignoreNextRead = false;
			return -1;
		}

		int c = input.read();
		if (c == -1) {
			return -1;
		}
		b[off] = (byte) c;

		int count = 0;
		int i = 1;
		try {
			for (; i < len; i++) {
				c = read();
				if (c == -1) {
					break;
				}
				b[off + i] = (byte) c;
				// Verify that we aren't reading into our boundary
				if (b[off + i] == boundaryBytes[count]) {
					count++;
					if (count >= boundaryBytes.length) {
						ignoreNextRead = true;
						break;
					}
				} else {
					count = 0;
				}
			}
		} catch (IOException ee) {
		}
		if (ignoreNextRead) {
			byte[] left = new byte[count + 1];
			System.arraycopy(b, off + i - count, left, 0, left.length);
			leftover = ByteBuffer.wrap(left);
			for (int j = i - count; j < i; j++) {
				b[j + off] = -1;
			}
			// Peek at the data, make sure we aren't leaving a \r\n
			int d = leftover.get(leftover.position());
			if (d == '\r' || d == '\n') {
				leftover.position(leftover.position() + d == '\r' ? 2 : 1);
			}
		}
		return i - count;
	}

	/**
	 * Reads a line up until \r\n This will act similar to
	 * BufferedReader.readLine
	 * 
	 * @return The line
	 * @throws IOException
	 *             If an error occurs while reading
	 */
	public String readLine() throws IOException {
		StringBuilder bldr = new StringBuilder();
		while (true) {
			int b = read();
			if (b == -1 || b == 10) {
				break;
			} else if (b != '\r') {
				bldr.append((char) ((byte) b));
			}
		}
		return bldr.toString();
	}

	/**
	 * Reads a line up until \r\n OR the boundary This will act similar to
	 * BufferedReader.readLine, except that it will stop at the defined
	 * boundary.
	 * 
	 * @return The line
	 * @throws IOException
	 *             If an error occurs while reading
	 */
	public String readLineUntilBoundary() throws IOException {
		StringBuilder bldr = new StringBuilder();
		while (true) {
			int b = read();
			if (bldr.indexOf(boundary) == 0) {
				leftover = ByteBuffer.wrap(boundaryBytes);
				return null;
			} else if (b == -1 || b == 10) {
				break;
			} else if (b != '\r') {
				bldr.append((char) ((byte) b));
			}
		}
		return bldr.toString();
	}
}
