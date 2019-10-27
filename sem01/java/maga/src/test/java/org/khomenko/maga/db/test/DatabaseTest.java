package org.khomenko.maga.db.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTest {
    private static Connection connection;

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
    }

    @Test
    void doNothing() {

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
            stmt.executeQuery(dropAbonentToBooksQuery);
            stmt.executeQuery(dropBooksQuery);
            stmt.executeQuery(dropAbonentsQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
        }

        connection.close();
    }
}
