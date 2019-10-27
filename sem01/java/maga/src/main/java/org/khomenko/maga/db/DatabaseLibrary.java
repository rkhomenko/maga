package org.khomenko.maga.db;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseLibrary implements Library {
    private static final String bookInsertQuery =
            "insert into BOOKS(BOOK_ID, BOOK_TITLE) values(%d, \'%s\')";
    private static final String abonentInsertQuery =
            "insert into ABONENTS(ABONENT_ID, ABONENT_NAME) values(%d, \'%s\')";
    private static final String abonentsToBooksInsertQuery =
            "insert into ABONENTS_TO_BOOKS(BOOK_ID, ABONENT_ID) values(%d, %d)";
    private static final String returnBookQuery =
            "delete from ABONENTS_TO_BOOKS where BOOK_ID = %d and ABONENT_ID = %d";
    private static final String selectAllQuery =
            "select * from %s";
    private static final String selectFreeBookQuery =
            "select * from BOOKS where BOOK_ID not in (select BOOK_ID from ABONENTS_TO_BOOKS)";

    private Connection connection;

    public DatabaseLibrary(Connection connection) {
        this.connection = connection;
    }

    public void addNewBook(Book book) {
        ExecuteQuery(bookInsertQuery, book.getId(), book.getTitle());
    }

    public void addAbonent(Student student) {
        ExecuteQuery(abonentInsertQuery, student.getId(), student.getName());
    }

    public void borrowBook(Book book, Student student) {
        ExecuteQuery(abonentsToBooksInsertQuery, book.getId(), student.getId());
    }

    public void returnBook(Book book, Student student) {
        ExecuteQuery(returnBookQuery, book.getId(), student.getId());
    }

    public List<Book> findAvailableBooks() {
        List<Book> books = new ArrayList<>();
        ExecuteQuery(Book.class, books::add, selectFreeBookQuery, "BOOKS");
        return books;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        ExecuteQuery(Student.class, students::add, selectAllQuery, "ABONENTS");
        return students;
    }

    private void ExecuteQuery(String query, Object... objects) {
        String resultQuery;
        if (objects == null) {
            resultQuery = query;
        } else {
            resultQuery = String.format(query, objects);
        }

        try {
            try (Statement stmt = connection.createStatement()) {
                connection.setAutoCommit(false);
                stmt.executeQuery(resultQuery);
                connection.commit();
            }
            catch (SQLException e) {
                connection.rollback();
                throw new QueryException(e.getMessage());
            }
            finally {
                connection.setAutoCommit(true);
            }
        }
        catch (SQLException e) {
            throw new QueryException(e.getMessage());
        }
    }

    private <T> void ExecuteQuery(Class<T> tClass, Consumer<T> consumer, String query, Object... objects) {
        String resultQuery = null;
        if (objects == null) {
            resultQuery = query;
        } else {
            resultQuery = String.format(query, objects);
        }

        try {
            try (Statement stmt = connection.createStatement()) {
                connection.setAutoCommit(false);
                ResultSet resultSet = stmt.executeQuery(resultQuery);

                Constructor<T> constructor = null;
                try {
                    constructor = tClass.getConstructor(Integer.TYPE, String.class);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                while (resultSet.next()) {
                    try {
                        consumer.accept(constructor.newInstance(resultSet.getInt(1),
                                resultSet.getString(2)));
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

                connection.commit();
            }
            catch (SQLException e) {
                connection.rollback();
                throw new QueryException(e.getMessage());
            }
            finally {
                connection.setAutoCommit(true);
            }
        }
        catch (SQLException e) {
            throw new QueryException(e.getMessage());
        }
    }
}
