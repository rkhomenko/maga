package org.khomenko.maga.db.test;

import org.junit.jupiter.api.*;
import org.khomenko.maga.db.Book;
import org.khomenko.maga.db.DatabaseLibrary;
import org.khomenko.maga.db.QueryException;
import org.khomenko.maga.db.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class DatabaseTest {
    private static Connection connection;
    private static DatabaseLibrary databaseLibrary;

    @BeforeAll
    private static void initDatabase() throws SQLException {
        String createBooksQuery = """
            create table BOOKS(
                BOOK_ID integer primary key,
                BOOK_TITLE varchar2(255) not null
            )
        """;
        String createAbonetsQuery = """
            create table ABONENTS(
                ABONENT_ID integer primary key,
                ABONENT_NAME varchar2(255) not null
            )
        """;
        String createAbonentToBookQuery = """
            create table ABONENTS_TO_BOOKS(
                ABONENT_ID integer not null,
                BOOK_ID integer not null,
                constraint fk_abonent_id
                    foreign key (ABONENT_ID)
                    references ABONENTS(ABONENT_ID),
                constraint fk_book_id
                    foreign key (BOOK_ID)
                    references BOOKS(BOOK_ID),
                constraint fk_book_id_unique unique(BOOK_ID)
            )
        """;

        connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:11111:ORCLCDB", "rk", "rk");
        connection.setAutoCommit(false);

        try (Statement stmt = connection.createStatement()) {
            stmt.executeQuery(createBooksQuery);
            stmt.executeQuery(createAbonetsQuery);
            stmt.executeQuery(createAbonentToBookQuery);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
        finally {
            connection.setAutoCommit(true);
        }

        databaseLibrary = new DatabaseLibrary(connection);
    }

    @AfterAll
    private static void cleanupDatabase() throws SQLException {
        String dropAbonentToBooksQuery = """
            drop table ABONENTS_TO_BOOKS
        """;
        String dropBooksQuery = """
            drop table BOOKS
        """;
        String dropAbonentsQuery = """
            drop table ABONENTS
        """;

        try (Statement stmt = connection.createStatement()) {
            connection.setAutoCommit(false);

            stmt.executeQuery(dropAbonentToBooksQuery);
            stmt.executeQuery(dropBooksQuery);
            stmt.executeQuery(dropAbonentsQuery);

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
        } finally {
            connection.setAutoCommit(true);
        }

        connection.close();
        connection = null;
    }

    @AfterEach
    void truncateTables() throws SQLException {
        var constraints = new String[]{ "fk_book_id", "fk_abonent_id", "fk_book_id_unique" };
        String disableConstraints = "alter table ABONENTS_TO_BOOKS disable constraint ";
        String enableConstraints = "alter table ABONENTS_TO_BOOKS enable constraint ";
        String truncateAbonentToBooksQuery = """
            truncate table ABONENTS_TO_BOOKS
        """;
        String truncateBooksQuery = """
            truncate table BOOKS
        """;
        String truncateAbonentsQuery = """
            truncate table ABONENTS
        """;

        try (Statement stmt = connection.createStatement()) {
            connection.setAutoCommit(false);

            for (String constraint : constraints) {
                stmt.executeQuery(disableConstraints + constraint);
            }

            stmt.executeQuery(truncateAbonentToBooksQuery);
            stmt.executeQuery(truncateBooksQuery);
            stmt.executeQuery(truncateAbonentsQuery);

            for (String constraint : constraints) {
                stmt.executeQuery(enableConstraints + constraint);
            }

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
        } finally {
            connection.setAutoCommit(true);
        }
    }

    List<Book> insertBooksQuery() {
        String query = "insert into BOOKS(BOOK_ID, BOOK_TITLE) values(%d, \'%s\')";

        List<Book> books = new ArrayList<>();
        try {
            try (Statement stmt = connection.createStatement()) {
                connection.setAutoCommit(false);

                IntStream.rangeClosed(1, 1000)
                        .forEach(value -> {
                            var book = new Book(value, "book" + value);
                            books.add(book);
                            try {
                                stmt.executeQuery(String.format(query, book.getId(), book.getTitle()));
                            } catch (SQLException e) {
                                throw new RuntimeException(e.getMessage());
                            }
                        });

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return books;
    }

    @Test
    void executeQueryTest() {
        var insertedBooks = insertBooksQuery();
        List<Book> books = new ArrayList<>();
        DatabaseLibrary.ExecuteQuery(connection,
                Book.class, books::add, "select * from BOOKS", null);
        books.sort(Comparator.comparingInt(Book::getId));

        Assertions.assertEquals(insertedBooks, books);
    }

    @Test
    void bookUniqueTest() {
        Book book = new Book(1, "book");
        databaseLibrary.addNewBook(book);

        Assertions.assertThrows(QueryException.class, () -> {
            databaseLibrary.addNewBook(book);
        });
    }

    @Test
    void studentUniqueTest() {
        Student student = new Student(1, "student");
        databaseLibrary.addAbonent(student);

        Assertions.assertThrows(QueryException.class, () -> {
            databaseLibrary.addAbonent(student);
        });
    }

    @Test
    void addMultipleBookTest() {
        List<Book> books = Arrays.asList(
                new Book(1, "b1"),
                new Book(2, "b2")
        );

        for (var book : books) {
            databaseLibrary.addNewBook(book);
        }

        List<Book> insertedBooks = new ArrayList<>();
        DatabaseLibrary.ExecuteQuery(connection,
                Book.class, insertedBooks::add, "select * from BOOKS", null);

        Assertions.assertEquals(books, insertedBooks);
    }

    @Test
    void addMultipleStudentTest() {
        List<Student> students = Arrays.asList(
                new Student(1, "b1"),
                new Student(2, "b2")
        );

        for (var student : students) {
            databaseLibrary.addAbonent(student);
        }

        List<Student> insertedStudents = new ArrayList<>();
        DatabaseLibrary.ExecuteQuery(connection,
                Student.class, insertedStudents::add, "select * from ABONENTS", null);

        Assertions.assertEquals(students, insertedStudents);
    }

    @Test
    void testMultipleBorrowTest() {
        Student s = new Student(1, "s");

        Book b1 = new Book(1, "b1");
        Book b2 = new Book(2, "b2");

        databaseLibrary.addAbonent(s);
        databaseLibrary.addNewBook(b1);
        databaseLibrary.addNewBook(b2);

        databaseLibrary.borrowBook(b1, s);
        databaseLibrary.borrowBook(b2, s);

        var availableBooks = databaseLibrary.findAvailableBooks();
        Assertions.assertEquals(availableBooks.size(), 0);
    }

    @Test
    void borrowOneBookTest() {
        Student s1 = new Student(1, "st1");
        Student s2 = new Student(2, "st2");

        Book b = new Book(1, "b");

        databaseLibrary.addAbonent(s1);
        databaseLibrary.addAbonent(s2);
        databaseLibrary.addNewBook(b);

        databaseLibrary.borrowBook(b, s1);

        Assertions.assertThrows(QueryException.class, () -> {
            databaseLibrary.borrowBook(b, s2);
        });
    }

    @Test
    void selectAllStudentTest() {
        List<Student> students = new ArrayList<>();
        IntStream.rangeClosed(1, 1000).forEach((int x) -> {
            students.add(new Student(x, "student" + x));
        });

        students.forEach(databaseLibrary::addAbonent);

        List<Student> result = databaseLibrary.getAllStudents();
        result.sort(Comparator.comparingInt(Student::getId));

        Assertions.assertEquals(students, result);
    }

    @Test
    void selectFreeBookTest() {
        Student s = new Student(1, "s");
        databaseLibrary.addAbonent(s);

        List<Book> availableBooks = IntStream.rangeClosed(1, 1000)
                .mapToObj(x -> new Book(x, "book" + x))
                .peek(book -> {
                    databaseLibrary.addNewBook(book);
                    if (book.getId() % 2 == 0) {
                        databaseLibrary.borrowBook(book, s);
                    }
                })
                .filter(book -> book.getId() % 2 == 1)
                .collect(Collectors.toList());

        List<Book> result = databaseLibrary.findAvailableBooks();
        result.sort(Comparator.comparingInt(Book::getId));

        Assertions.assertEquals(availableBooks, result);
    }

    @Test
    void returnBookTest() {
        Student s = new Student(1, "s");
        databaseLibrary.addAbonent(s);

        List<Book> allBooks = IntStream.rangeClosed(1, 1000)
                .mapToObj(x -> new Book(x, "book" + x))
                .peek(book -> {
                    databaseLibrary.addNewBook(book);

                    if (book.getId() % 2 == 0) {
                        databaseLibrary.borrowBook(book, s);
                    }
                })
                .collect(Collectors.toList());

        allBooks.stream()
                .filter(book -> book.getId() % 2 == 0)
                .forEach(book -> databaseLibrary.returnBook(book, s));

        List<Book> result = databaseLibrary.findAvailableBooks();
        result.sort(Comparator.comparingInt(Book::getId));

        Assertions.assertEquals(allBooks, result);
    }
}
