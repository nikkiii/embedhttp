package org.nikkii.embedhttp.impl;

import java.io.File;

/**
 * Represents an uploaded file.
 * 
 * The file represented may be deleted after the HttpRequest object has been
 * discarded, make sure to copy it somewhere safe.
 * 
 * @author Nikki
 * 
 */
public class HttpFileUpload {

	/**
	 * The file name
	 */
	private String fileName;

	/**
	 * The temporary file
	 */
	private File tempFile;

	/**
	 * Construct a new file upload
	 * 
	 * @param fileName
	 *            The file name
	 * @param tempFile
	 *            The temporary file (Warning: May be deleted at any time)
	 */
	public HttpFileUpload(String fileName, File tempFile) {
		this.fileName = fileName;
		this.tempFile = tempFile;
	}

	/**
	 * Get the file name
	 * 
	 * @return The file's name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Get the temporary file
	 * 
	 * @return The temporary file
	 */
	public File getTempFile() {
		return tempFile;
	}
}
