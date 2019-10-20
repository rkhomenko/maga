package org.khomenko.maga.io;

import java.io.Serializable;
import java.util.Arrays;

public class Book implements Serializable {
    private String isbn;
    private String title;
    private String[] authors;
    private String publisher;
    private String annotation;

    public Book() {
    }

    public Book(String isbn, String title, String[] authors, String publisher, String annotation) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.annotation = annotation;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String[] getAuthors() {
        return authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthors(String[] authors) {
        this.authors = authors;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", authors=" + Arrays.toString(authors) +
                ", publisher='" + publisher + '\'' +
                '}';
    }
}
