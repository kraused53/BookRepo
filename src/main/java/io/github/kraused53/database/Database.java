package io.github.kraused53.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class creates connections to configured databases.
 */
public class Database {

    /**
     * Connect to the default database defined in database.properties.
     *
     * @return a connection to the default database
     * @throws SQLException if the connection fails
     */
    public static Connection getConnection() throws SQLException {
        return getConnection(
                DatabaseConfig.getUrl(),
                DatabaseConfig.getUsername(),
                DatabaseConfig.getPassword()
        );
    }

    /**
     * Connect to a specified database.
     *
     * @param url the JDBC URL of the database
     * @param username the database username
     * @param password the database password
     * @return a connection to the specified database
     * @throws SQLException if the connection fails
     */
    public static Connection getConnection(
            String url,
            String username,
            String password
    ) throws SQLException {

        return DriverManager.getConnection(url, username, password);
    }
}