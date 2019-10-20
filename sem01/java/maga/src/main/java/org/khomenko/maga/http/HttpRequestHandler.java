package org.khomenko.maga.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.khomenko.maga.io.ServerMain;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class HttpRequestHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ServerMain.class);
    private static final HttpResponse okResponse = new HttpResponse();
    private static final HttpResponse notFoundResponse = new HttpResponse();

    static {
        okResponse.setStatusCode(HttpStatusCode.OK);
        notFoundResponse.setStatusCode(HttpStatusCode.NOT_FOUND);
    }

    private Socket socket;
    private Map<String, HttpRouteHandlers> httpRouteHandlersMap;

    public HttpRequestHandler(Socket socket, Map<String, HttpRouteHandlers> httpRouteHandlersMap) {
        this.socket = socket;
        this.httpRouteHandlersMap = httpRouteHandlersMap;
    }

    @Override
    public void run() {
        char[] buffer = new char[256];
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
             OutputStreamWriter outputWriter = new OutputStreamWriter(socket.getOutputStream());
             Socket socket = this.socket) {
            do {
                int bytesCount = bufferedReader.read(buffer, 0, 255);
                stringBuilder.append(buffer, 0, bytesCount);
            } while (bufferedReader.ready());

            int headerEnd = stringBuilder.indexOf("\r\n\r\n");
            String headerStr = stringBuilder.substring(0, headerEnd);
            HttpHeader httpHeader = HttpHeader.fromString(headerStr);

            logger.info("" + httpHeader.getMethod() +
                    " request from " + httpHeader.getHost() +
                    ", route = " + httpHeader.getPath());

            String path = getRoute(httpHeader.getPath());
            HttpRouteHandlers httpRouteHandlers = httpRouteHandlersMap.get(path);
            if (httpRouteHandlers == null) {
                logger.fatal("No route for " + httpHeader.getPath());
                outputWriter.write(notFoundResponse.toString());
                outputWriter.flush();

                return;
            }

            logger.debug("Find route class " + httpRouteHandlers.getClassName() + " for path " + path);

            outputWriter.write(okResponse.toString());
            outputWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRoute(String path) {
        String[] strs = path.split("/");
        return "/" + strs[1];
    }
}
