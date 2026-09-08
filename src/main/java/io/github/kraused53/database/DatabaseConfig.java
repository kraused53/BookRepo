package io.github.kraused53.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * The DatabaseConfig class provides a safe, reliable way for the Database class to access
 *     sensitive credentials in a way that does not leak secrets to version control.
 *
 * @author Daniel Krause
 * @version 1.0
 * @since 9/7/2026
 * @see Database
 */
public class DatabaseConfig {

    /**
     * Default constructor
     *
     * @throws SQLException Throws SQL exception if there is an error connecting to the database
     */
    public DatabaseConfig() throws SQLException {}

    /**
     * Make space to store parsed config data.
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
     * This method returns the database URL stored in the database.properties file.
     *
     * @return Returns the stored database URL. {@code String}
     */
    public static String getUrl() {
        return properties.getProperty("db.url");
    }

    /**
     * This method returns the database username stored in the database.properties file.
     *
     * @return Returns the stored database username. {@code String}
     */
    public static String getUsername() {
        return properties.getProperty("db.username");
    }

    /**
     * This method returns the password for the database username stored in the database.properties file.
     *
     * @return Returns the stored password for the database username. {@code String}
     */
    public static String getPassword() {
        return properties.getProperty("db.password");
    }
}