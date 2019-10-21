package org.khomenko.maga.http;

public enum HttpMethod {
    GET, POST, PUT, DELETE, UNKNOWN;

    public static HttpMethod fromString(String s) {
        return switch (s) {
            case "GET" -> HttpMethod.GET;
            case "POST" -> HttpMethod.POST;
            case "PUT" -> HttpMethod.PUT;
            case "DELETE" -> HttpMethod.DELETE;
            default -> HttpMethod.UNKNOWN;
        };
    }
}
