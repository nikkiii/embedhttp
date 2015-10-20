package org.nikkii.embedhttp.util.content;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.FileNameMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A content type utility which uses file extensions and a standard formatted mime type file, like the one included in the usual Linux distributions.
 *
 * @author Nikki
 */
public class ContentTypeMap implements FileNameMap {

	/**
	 * Pattern to match valid type -> extension mappings
	 */
	private static final Pattern TYPE_PATTERN = Pattern.compile("^(.*?)\\s+(.*)$");

	/**
	 * Map containing a list of extensions -> content types
	 */
	private final Map<String, String> contentTypes = new HashMap<String, String>();

	/**
	 * Initialize a map from the included mime.types
	 */
	public ContentTypeMap() {
		initialize(ContentTypeMap.class.getResourceAsStream("/mime.types"));
	}

	/**
	 * Initialize a map from the specified input stream.
	 *
	 * @param input The input stream to read the types from.
	 */
	public ContentTypeMap(InputStream input) {
		initialize(input);
	}

	/**
	 * Read and parse the type file.
	 *
	 * @param input The input stream to read the file from.
	 */
	private void initialize(InputStream input) {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(input));

			String line;

			Matcher m;

			while ((line = reader.readLine()) != null) {
				m = TYPE_PATTERN.matcher(line);

				if (m.find()) {
					String type = m.group(1);
					String[] extensions = m.group(2).split(" ");

					for (String extension : extensions) {
						contentTypes.put(extension, type);
					}
				}
			}
		} catch (IOException e) {
			// Unable to load
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String getContentTypeFor(String fileName) {
		return contentTypes.get(getExtension(fileName));
	}

	/**
	 * Get the extension of a file.
	 *
	 * @param fileName The file name.
	 * @return The file extension.
	 */
	private static final String getExtension(String fileName) {
		return fileName.indexOf('.') == -1 ? null : fileName.substring(fileName.lastIndexOf('.') + 1);
	}
}
