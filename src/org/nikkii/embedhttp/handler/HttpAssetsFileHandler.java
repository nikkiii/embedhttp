package org.nikkii.embedhttp.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.nikkii.embedhttp.impl.HttpRequest;
import org.nikkii.embedhttp.impl.HttpResponse;
import org.nikkii.embedhttp.impl.HttpStatus;

import android.content.Context;
import android.content.res.AssetManager;

/**
 * A RequestHandler to handle serving of simple static files from app assets folder
 * 
 * @author Nartex
 * 
 */
public class HttpAssetsFileHandler implements HttpRequestHandler {
	private AssetManager assets;
	private String path = "";

	/**
	 * Construct a new static file server with app assets
	 * 
	 * @param context
	 *            The app context
	 */
	public HttpAssetsFileHandler(Context context) {
		this.assets = context.getAssets();
	}
	
	/**
	 * Construct a new static file server with app assets
	 * 
	 * @param context
	 *            The app context
	 * @param
	 * 		   pathWithFinalSlash
	 * 				The root path in assets without first slash but with final slash (like "content/")
	 */
	public HttpAssetsFileHandler(Context context, String pathWithFinalSlash) {
		this.assets = context.getAssets();
		this.path = pathWithFinalSlash;
	}

	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		String uri = request.getUri();
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			uri = uri.replace("%20", " ");
		}
		
		try {
			InputStream inputStream = assets.open(path + uri.substring(1));
			HttpResponse res = new HttpResponse(HttpStatus.OK, inputStream);
			res.setResponseLength(inputStream.available());
			return res;
		} catch (IOException e) {
			e.printStackTrace();
			return new HttpResponse(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.toString());
		}
	}
}
