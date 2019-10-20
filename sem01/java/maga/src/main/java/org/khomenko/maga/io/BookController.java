package org.khomenko.maga.io;

import org.khomenko.maga.http.HttpMethod;
import org.khomenko.maga.http.RouteHandler;
import org.khomenko.maga.http.Route;

@Route(path = "/books")
public class BookController {

    @RouteHandler(type = HttpMethod.GET)
    public void getAllBooks() {

    }

    @RouteHandler(type = HttpMethod.GET, withArgument = true)
    public void getBook(int id) {

    }

    @RouteHandler(type = HttpMethod.POST)
    public void createBook() {

    }

    @RouteHandler(type = HttpMethod.DELETE, withArgument = true)
    void deleteBook (int id) {

    }
}
