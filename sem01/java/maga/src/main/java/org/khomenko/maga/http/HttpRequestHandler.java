package org.khomenko.maga.http;

import java.io.*;
import java.net.Socket;

public class HttpRequestHandler implements Runnable {
    private Socket socket;

    public HttpRequestHandler(Socket socket) {
        this.socket = socket;
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

            System.out.println(headerStr);

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
}
