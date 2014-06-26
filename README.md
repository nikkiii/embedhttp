# Embeddable Java Httpd

A small and easily embeddable Java Http server for use in applications.

[![Build Status](https://travis-ci.org/nikkiii/embedhttp.svg?branch=master)](https://travis-ci.org/nikkiii/embedhttp)

## Requirements

- Java Development Kit/Java Runtime Environment 1.6+ (Could work in 1.5?)

## Usage

	HttpServer server = new HttpServer();
	
	//~ Add any request handlers
	// static file handler (from jar's resources) :
	server.addRequestHandler(new HttpStaticJarFileHandler());
    // static file handler (from jar's resources with path) :
    server.addRequestHandler(new HttpStaticJarFileHandler("/assets/http/"));
	// static file handler (from filesystem) :
	server.addRequestHandler(new HttpStaticFileHandler(new File(".")));
	//Or your own:
	server.addRequestHandler(new HttpRequestHandler() {
		@Override
		public HttpResponse handleRequest(HttpRequest request) {
			return new HttpResponse(HttpStatus.OK, "Hello, world!");
		}
	});
	server.bind(8081);
	server.start();
	
## License

The MIT License (MIT)

Copyright (c) 2014 Nikki

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.