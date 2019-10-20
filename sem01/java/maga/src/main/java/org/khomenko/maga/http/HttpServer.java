package org.khomenko.maga.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer implements Runnable {
    private int port;
    private InetAddress inetAddress;
    private int poolSize;

    public HttpServer(int port, InetAddress inetAddress, int poolSize) {
        this.port = port;
        this.inetAddress = inetAddress;
        this.poolSize = poolSize;
    }

    @Override
    public void run() {
        ExecutorService service = Executors.newFixedThreadPool(poolSize);

        try {
            ServerSocket serverSocket = new ServerSocket(port, 2 * poolSize, inetAddress);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            while (true) {
                Socket socket = serverSocket.accept();
                service.submit(new HttpRequestHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
