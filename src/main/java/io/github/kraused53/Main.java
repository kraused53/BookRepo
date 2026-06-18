package io.github.kraused53;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;


public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    static final String QUERY = "SELECT * FROM books";

    static void main() {

        try(
                Connection conn = DatabaseConnection.getConnection()
        ) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(QUERY);
            // Extract data from result set
            while (rs.next()) {
                // Retrieve by column name
                System.out.print("Title: " + rs.getString("title"));
                System.out.println(", ISBN: " + rs.getString("isbn"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        logger.info("Exiting, thank you for using my Book App!");
    }
}


