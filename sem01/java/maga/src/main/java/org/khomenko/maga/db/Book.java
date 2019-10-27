package org.khomenko.maga.db;

public class Book {
    private int id;
    private String title;

    public Book(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object.getClass() != Book.class) {
            return false;
        }

        Book book = (Book)object;

        return getId() == book.getId();
    }
}
