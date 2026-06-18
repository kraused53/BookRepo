package io.github.kraused53;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    public static Connection getConnection() throws SQLException {
        logger.info("Connecting to database...");

        // Load properties file
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
        } catch (FileNotFoundException e) {
            logger.error("Missing config.properties file...");
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Error reading config.properties file...");
            throw new RuntimeException(e);
        }

        return DriverManager.getConnection(props.getProperty("db.url"), props.getProperty("db.user"), props.getProperty("db.password"));
    }
}
