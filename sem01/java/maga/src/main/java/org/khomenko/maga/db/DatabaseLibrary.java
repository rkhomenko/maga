package org.khomenko.maga.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DatabaseLibrary implements Library {
    private static final String BookDbName = "BOOKS";
    private static final String AbonentDbName = "ABONENTS";
    private static final String BookInsertQuery = "insert into BOOKS(TITLE) values(\"%s\")";
    private static final String AbonentInsertQuery = "insert into ABONENTS(NAME) values(\"%s\")";

    private Connection connection;

    public DatabaseLibrary(Connection connection) {
        this.connection = connection;
    }

    public void addNewBook(Book book) {
        ExecuteQuery(BookInsertQuery, book.getTitle());
    }

    public void addAbonent(Student student) {
        ExecuteQuery(AbonentInsertQuery, student.getName());
    }

    public void borrowBook(Book book, Student student) {
        throw new UnsupportedOperationException();
    }

    public void returnBook(Book book, Student student) {
        throw new UnsupportedOperationException();
    }

    public List<Book> findAvailableBooks() {
        throw new UnsupportedOperationException();
    }

    public List<Student> getAllStudents() {
        throw new UnsupportedOperationException();
    }

    private void ExecuteQuery(String query, Object... objects) {
        String resultQuery = null;
        if (objects == null) {
            resultQuery = query;
        } else {
            resultQuery = String.format(query, objects);
        }

        try {
            try (Statement stmt = connection.createStatement()) {
                stmt.executeQuery(resultQuery);
                connection.commit();
            }
            catch (SQLException e) {
                e.printStackTrace();
                connection.rollback();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
