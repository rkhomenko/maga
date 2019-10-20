package org.khomenko.maga.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.khomenko.maga.io.ServerMain;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.Map;

public class HttpRequestHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ServerMain.class);

    private static final String END_SEQ = "\r\n\r\n";
    private static final HttpResponse okResponse = new HttpResponse();
    private static final HttpResponse notFoundResponse = new HttpResponse();
    private static final HttpResponse internalServerErrorResponse =
            new HttpResponse();

    static {
        okResponse.setStatusCode(HttpStatusCode.OK);
        notFoundResponse.setStatusCode(HttpStatusCode.NOT_FOUND);
        internalServerErrorResponse.setStatusCode(HttpStatusCode.SERVER_ERROR);
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

            int headerEnd = stringBuilder.indexOf(END_SEQ);
            String headerStr = stringBuilder.substring(0, headerEnd);
            String bodyStr = stringBuilder.substring(headerEnd + END_SEQ.length());

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

            String argument = getArgument(httpHeader.getPath());
            boolean withArgument = argument != null;
            HttpRouteHandlers.HttpMethodHandler httpMethodHandler = httpRouteHandlers.getHandler(httpHeader.getMethod(),
                    withArgument);

            var method = httpMethodHandler.getMethod();
            var object = httpMethodHandler.getObject();
            HttpResponse methodResponse = null;

            logger.debug("Trying execute method " + method.getName()
                    + (argument == null ? "" : " withArgument=" + argument));

            try {
                if (withArgument) {
                    methodResponse = (HttpResponse)method.invoke(object, bodyStr, argument);
                } else {
                    methodResponse = (HttpResponse) method.invoke(object, bodyStr);
                }
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                logger.fatal(e.getMessage());
            }

            methodResponse = methodResponse == null ? internalServerErrorResponse : methodResponse;

            outputWriter.write(methodResponse.toString());
            outputWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRoute(String path) {
        String[] strs = path.split("/");
        return "/" + strs[1];
    }

    private String getArgument(String path) {
        String[] strs = path.split("/");

        if (strs.length < 3) {
            return null;
        }

        return strs[2];
    }
}
