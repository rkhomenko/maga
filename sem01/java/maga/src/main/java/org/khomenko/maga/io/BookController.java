package org.khomenko.maga.io;

import org.khomenko.maga.http.HttpMethod;
import org.khomenko.maga.http.HttpResponse;
import org.khomenko.maga.http.RouteHandler;
import org.khomenko.maga.http.Route;

@Route(path = "/books")
public class BookController {

    @RouteHandler(type = HttpMethod.GET)
    public void getAllBooks() {

    }

    @RouteHandler(type = HttpMethod.GET, withArgument = true)
    public HttpResponse getBook(String id) {
        return null;
    }

    @RouteHandler(type = HttpMethod.POST)
    public HttpResponse createBook() {
        return null;
    }

    @RouteHandler(type = HttpMethod.DELETE, withArgument = true)
    public HttpResponse deleteBook (String id) {
        return null;
    }
}
