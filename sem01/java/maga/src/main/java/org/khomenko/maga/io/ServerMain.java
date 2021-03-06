package org.khomenko.maga.io;

import org.khomenko.maga.http.HttpServer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerMain {
    public static void main(String[] args) throws UnknownHostException {
        HttpServer httpServer = new HttpServer(27772, InetAddress.getByName("localhost"), 10);
        httpServer.addController(BookController.class);
        httpServer.addController(HelloController.class);
        httpServer.run();
    }
}
