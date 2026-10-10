package io.github.kraused53.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This class allows the java project to access data inside the database.properties file
 */
public class DatabaseConfig {

    /**
     * Store data for properties object and attempt to load it when this static class is created
     */
    private static final Properties properties = new Properties();
    static {
        try (FileInputStream input =
                    new FileInputStream("config/database.properties")) {

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Unable to load database.properties", e);
        }
    }

    /**
     * Return the database url
     */
    public static String getUrl() {
        return properties.getProperty("db.url");
    }

    /**
     * Return the database username
     */
    public static String getUsername() {
        return properties.getProperty("db.username");
    }

    /**
     * Return the database password
     */
    public static String getPassword() {
        return properties.getProperty("db.password");
    }}
