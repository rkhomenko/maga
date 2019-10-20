package org.khomenko.maga.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.khomenko.maga.io.ServerMain;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer implements Runnable {
    private static final Logger logger = LogManager.getLogger(ServerMain.class);

    private int port;
    private InetAddress inetAddress;
    private int poolSize;
    private Map<String, HttpRouteHandlers> httpRouteHandlersMap;

    public HttpServer(int port, InetAddress inetAddress, int poolSize) {
        this.port = port;
        this.inetAddress = inetAddress;
        this.poolSize = poolSize;
        this.httpRouteHandlersMap = new HashMap<>();
    }

    public void addController(Class<?> controllerType) {
        Route route = controllerType.getAnnotation(Route.class);

        if (route == null) {
            throw new RuntimeException("Class must be annotated with @Route");
        }

        httpRouteHandlersMap.put(route.path(), HttpRouteHandlers.fromClass(controllerType));
    }

    @Override
    public void run() {
        ExecutorService service = Executors.newFixedThreadPool(poolSize);

        logger.info("Server started at {}:{}", inetAddress.toString(), port);

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
                service.submit(new HttpRequestHandler(socket, Collections.unmodifiableMap(httpRouteHandlersMap)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
