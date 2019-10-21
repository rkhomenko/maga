package org.khomenko.maga.io;

import org.khomenko.maga.http.*;

@Route(path = "/hello")
public class HelloController {
    @RouteHandler(type = HttpMethod.GET)
    public HttpResponse sayHello(String requestBody) {
        HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.OK);
        response.setContentType(HttpResponse.ContentType.HTML);
        response.setBody("<h1>Hello World</h1>");
        return response;
    }
}
