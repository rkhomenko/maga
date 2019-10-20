package org.khomenko.maga.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.khomenko.maga.io.ServerMain;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class HttpRequestHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ServerMain.class);

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

        try {
            InputStream stream = socket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
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
                String err = "No route for " + httpHeader.getPath();
                logger.fatal(err);
                throw new RuntimeException(err);
            }

            logger.debug("Find route class " + httpRouteHandlers.getClassName() + " for path " + path);

            OutputStreamWriter outputWriter = new OutputStreamWriter(socket.getOutputStream());
            outputWriter.write("HTTP/1.1 200 OK\r\n\r\n");
            outputWriter.flush();

            stream.close();
            outputWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRoute(String path) {
        String[] strs = path.split("/");
        return "/" + strs[1];
    }
}
