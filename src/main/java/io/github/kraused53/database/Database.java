package io.github.kraused53.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The Database class provides a method for the application to safely connect to the BookRepo
 *     database. The credentials for access to the database are stored in the database.properties
 *     file in the config directory. Database.Config.java is used to provide safe, consistent
 *     access to this file.
 *
 * @author Daniel Krause
 * @version 1.0
 * @since 9/7/2026
 * @see DatabaseConfig
 */
public class Database {

    /**
     * Default constructor
     *
     * @throws SQLException Throws SQL exception if there is an error connecting to the database
     */
    public Database() throws SQLException {}

    /**
     * This static method uses the DatabaseConfig class to parse the database config file to
     *     access credentials. These credentials are then fed to the JDBC library and the
     *     system attempts to log into the database. On a success, a Connection object is
     *     returned. In the event of a failure, the method throws an SQLException for
     *     convenient error handling.
     *
     * @return Returns a connection object to provide access to the BookRepo database. Use
     *             with Try-with-resource blocks for automatic destruction. {@code Connection}
     * @throws SQLException Throws this exception if there is an error connecting to the database.
     *
     * @see DatabaseConfig
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.getUrl(),
                DatabaseConfig.getUsername(),
                DatabaseConfig.getPassword()
        );
    }

}
