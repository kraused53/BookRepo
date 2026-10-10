package io.github.kraused53.database;

import io.github.kraused53.exceptions.InvalidISBNException;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestDatabase {

    // Database testing credentials
    String url = System.getenv("TEST_DB_URL");
    String username = System.getenv("TEST_DB_USERNAME");
    String password = System.getenv("TEST_DB_PASSWORD");

    @Test
    void testDatabaseConnection() throws Exception {
        assertDoesNotThrow(() ->{
            try (Connection conn = Database.getConnection(
                    url,
                    username,
                    password
            )) {
                assertNotNull(conn);
                assertFalse(conn.isClosed());
            }
        });
    }

    @Test
    void testDatabaseConnectionInvalidPassword() throws Exception {
        assertThrows(SQLException.class, () -> {
            try (Connection conn = Database.getConnection(
                    url,
                    username,
                    "wrong-password"
            )) {
                assertNotNull(conn);
                assertFalse(conn.isClosed());
            }
        });
    }
}
