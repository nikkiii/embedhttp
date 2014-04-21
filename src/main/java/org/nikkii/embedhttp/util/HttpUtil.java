package org.nikkii.embedhttp.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpUtil {
	/**
	 * Parse POST data or GET data from the request URI
	 * 
	 * @param data
	 *            The data string
	 * @return A map containing the values
	 */
	public static Map<String, Object> parseData(String data) {
		Map<String, Object> ret = new HashMap<String, Object>();
		String[] split = data.split("&");
		for (String s : split) {
			int idx = s.indexOf('=');
			try {
				if (idx != -1) {
					ret.put(URLDecoder.decode(s.substring(0, idx), "UTF-8"), URLDecoder.decode(s.substring(idx + 1), "UTF-8"));
				} else {
					ret.put(URLDecoder.decode(s, "UTF-8"), "true");
				}
			} catch (UnsupportedEncodingException e) {
				// Why.
			}
		}
		return ret;
	}

	/**
	 * Fixes capitalization on headers
	 * 
	 * @param header
	 *            The header input
	 * @return A header with all characters after '-' capitalized
	 */
	public static String capitalizeHeader(String header) {
		StringTokenizer st = new StringTokenizer(header, "-");
		StringBuilder out = new StringBuilder();
		while (st.hasMoreTokens()) {
			String l = st.nextToken();
			out.append(Character.toUpperCase(l.charAt(0)));
			if (l.length() > 1) {
				out.append(l.substring(1));
			}
			if (st.hasMoreTokens())
				out.append('-');
		}
		return out.toString();
	}
}
