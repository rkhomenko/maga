package org.khomenko.maga.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpHeader {
    private String path;
    private HttpMethod method;
    private int contentLength;
    private String host;

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getHost() {
        return host;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public void setHost(String host) {
        this.host = host;
    }

    private static final Pattern anyWhitespacePattern =
            Pattern.compile("\\s+");
    private static final Pattern propertyPattern =
            Pattern.compile("^([a-zA-Z\\-]+):\\s*(.+)\\s*$");

    public static HttpHeader fromString(String s) {
        HttpHeader header = new HttpHeader();

        String[] lines = s.split("\n");

        var methodArr = anyWhitespacePattern.split(lines[0]);
        header.setMethod(HttpMethod.fromString(methodArr[0]));
        header.setPath(methodArr[1]);

        for (int i = 1; i < lines.length; i++) {
            Matcher matcher = propertyPattern.matcher(lines[i]);
            if (!matcher.find()) {
                throw new RuntimeException("Cannot parse HTTP header property");
            }

            String value = matcher.group(2);
            switch (matcher.group(1)) {
                case "Host" -> header.setHost(value);
                case "Content-Length" -> header.setContentLength(Integer.parseInt(value));
            }
        }

        return header;
    }
}
