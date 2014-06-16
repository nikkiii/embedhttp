package org.nikkii.embedhttp.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.nikkii.embedhttp.impl.HttpRequest;
import org.nikkii.embedhttp.impl.HttpResponse;
import org.nikkii.embedhttp.impl.HttpStatus;

/**
 * A RequestHandler to handle serving of simple static files
 * 
 * @author Nikki
 * 
 */
public class HttpStaticJarFileHandler implements HttpRequestHandler {
    /**
     * Construct a new static file server
     * 
     * @param documentRoot
     *            The document root
     */
    public HttpStaticJarFileHandler() {
    }

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        String uri = request.getUri();
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            uri = uri.replace("%20", " ");
        }
        InputStream resourceAsStream = getClass().getResourceAsStream(uri);
        if (resourceAsStream != null) {
            HttpResponse res = new HttpResponse(HttpStatus.OK,
                    resourceAsStream);
            return res;
        }
        return null;
    }

}
