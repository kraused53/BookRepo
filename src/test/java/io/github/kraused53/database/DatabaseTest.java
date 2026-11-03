package io.github.kraused53.database;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import java.sql.Connection;

import org.junit.jupiter.api.Test;

public class DatabaseTest {
    
    @Test 
    void testDatabaseConnection() {
        assertDoesNotThrow( () -> {
            Connection conn = Database.getConnection();
        });
    }
}
