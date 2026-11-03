package io.github.kraused53.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class creates connections to configured databases.
 */
public class Database {

    /**
     * Connect to the database defined in database.properties.
     *
     * @return a connection to the database
     * @throws SQLException if the connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            DatabaseConfig.getUrl(),
            DatabaseConfig.getUsername(),
            DatabaseConfig.getPassword()
        );
    }
}