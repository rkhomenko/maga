package org.khomenko.maga.http;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    public enum ContentType {
        HTML("text/html; charset=utf-8"),
        JSON("application/json; charset=utf-8"),
        DEFAULT("");

        private String typeName;

        ContentType(String typeName) {
            this.typeName = typeName;
        }

        public String getTypeName() {
            return typeName;
        }
    }

    private static final String CRLF = "\r\n";
    private static final String NL = "\n";
    private static final String SP = " ";

    private HttpStatusCode statusCode;
    private Map<String, String> headers;
    private String body;
    private ContentType contentType;

    public HttpResponse() {
        headers = new HashMap<>();
        body = "";
        contentType = ContentType.DEFAULT;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("HTTP/1.1 ")
                .append(statusCode.getCode())
                .append(SP)
                .append(statusCode.getReasonPhrase())
                .append(NL);

        for (var entry : headers.entrySet()) {
            stringBuilder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(NL);
        }

        if (body.length() != 0) {
            stringBuilder.append("Content-type: ")
                    .append(contentType.getTypeName())
                    .append(NL)
                    .append("Content-length: ")
                    .append(body.length())
                    .append(NL);
        }

        stringBuilder.append(CRLF)
                .append(body)
                .append(CRLF);

        return stringBuilder.toString();
    }
}
