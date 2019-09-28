package org.khomenko.maga.socialnetwork;

public class LinkExistsException extends RuntimeException {
    public LinkExistsException(String message) {
        super(message);
    }
}
