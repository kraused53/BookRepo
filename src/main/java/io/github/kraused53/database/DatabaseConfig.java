package io.github.kraused53.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This class allows the Java project to access database configuration.
 *
 * By default, configuration is loaded from config/database.properties.
 * When USE_TEST_DATABASE=true, test database credentials are loaded
 * from environment variables instead.
 */
public class DatabaseConfig {

    private static final Properties properties = new Properties();

    static {
         try (FileInputStream input =
                     new FileInputStream("config/database.properties")) {

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to load config/database.properties", e);
        }
    }

    /**
     * Return the database URL.
     */
    public static String getUrl() {
        return properties.getProperty("db.url");
    }

    /**
     * Return the database username.
     */
    public static String getUsername() {
        return properties.getProperty("db.username");
    }

    /**
     * Return the database password.
     */
    public static String getPassword() {
        return properties.getProperty("db.password");
    }
}