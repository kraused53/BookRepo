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
        String useTestDatabase = System.getenv("USE_TEST_DATABASE");

        if ("true".equalsIgnoreCase(useTestDatabase)) {
            loadTestDatabaseConfig();
        } else {
            loadDefaultDatabaseConfig();
        }

        validateConfiguration();
    }

    /**
     * Load the normal application database configuration.
     */
    private static void loadDefaultDatabaseConfig() {
        try (FileInputStream input =
                     new FileInputStream("config/database.properties")) {

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to load config/database.properties", e);
        }
    }

    /**
     * Load test database configuration from environment variables.
     */
    private static void loadTestDatabaseConfig() {
        String url = System.getenv("TEST_DB_URL");
        String username = System.getenv("TEST_DB_USERNAME");
        String password = System.getenv("TEST_DB_PASSWORD");

        if (isBlank(url) || isBlank(username) || password == null) {
            throw new IllegalStateException(
                    "Test database configuration is incomplete. "
                            + "Set TEST_DB_URL, TEST_DB_USERNAME, and "
                            + "TEST_DB_PASSWORD.");
        }

        properties.setProperty("db.url", url);
        properties.setProperty("db.username", username);
        properties.setProperty("db.password", password);
    }

    /**
     * Ensure the required database properties are available.
     */
    private static void validateConfiguration() {
        if (isBlank(properties.getProperty("db.url"))
                || isBlank(properties.getProperty("db.username"))
                || properties.getProperty("db.password") == null) {
            throw new IllegalStateException(
                    "Database configuration is incomplete. "
                            + "Check db.url, db.username, and db.password.");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
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