package io.github.kraused53;

import java.sql.Connection;
import java.sql.SQLException;

import io.github.kraused53.database.Database;

public class BookRepo {
    public static void main(String[] args) {

        System.out.println("Attempting database connection...");

        try (Connection connection = Database.getConnection()) {

            System.out.println("Connected to the database!");

        } catch (SQLException e) {
            System.err.println("Failed to connect to the database!");
            e.printStackTrace();
        }

        System.out.println("Exiting...");
    }
}
