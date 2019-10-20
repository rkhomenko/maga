package org.khomenko.maga.io;

import org.khomenko.maga.http.HttpMethod;
import org.khomenko.maga.http.Route;
import org.khomenko.maga.http.RouteHandler;

@Route(path = "/hello")
public class HelloController {
    @RouteHandler(type = HttpMethod.GET)
    void sayHello() {

    }
}
