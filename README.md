# Embeddable Java Httpd

A small and easily embeddable Java Http server for use in applications.

## Requirements

- Java Development Kit/Java Runtime Environment 1.6+ (Could work in 1.5?)

## Usage

	HttpServer server = new HttpServer();
	//Add any request handlers, for instance the static file handler:
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

Copyright (c) 2013, Nikki (nospam at nikkii.us)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.