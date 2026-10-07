package io.github.kraused53;


import io.github.kraused53.Database.Database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Program execution begins here.
 */
public class Main {

    /**
     * Program execution begins here.
     */
    static void main() {

        try(Connection conn = Database.getConnection()) {
            System.out.println("Connected to jBook!");
        } catch (SQLException e) {
            System.out.println("Failed to connect to jBook!");
            throw new RuntimeException(e);
        }
    }

}
