package org.khomenko.maga.http;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerMain {
    public static void main(String[] args) throws UnknownHostException {
        HttpServer httpServer = new HttpServer(27772, InetAddress.getByName("localhost"), 10);

        httpServer.run();
    }
}
