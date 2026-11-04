package io.github.kraused53.repository;

import io.github.kraused53.database.Database;
import io.github.kraused53.exceptions.*;
import io.github.kraused53.models.Author;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles database access for authors.
 * All SQL operations related to authors are performed here.
 */
public class AuthorRepository {

    /**
     * Nothing needs to be initialized when this class is instantiated.
     */
    public AuthorRepository() {}

    /* CRUD - Create Section */

    /**
     * Insert an author into the database
     * 
     * @param conn A connection to the database
     * @param author The author object to add to the database.
     * @return The id of the author after it is added to the database. {@code int}
     * @throws SQLException If there is an issue connecting to the database
     * @throws DuplicateEntryException If the author is already in the database
     * @throws InvalidAuthorException If the author is not valid
     */
    public int insert(Connection conn, Author author) throws InvalidAuthorException, DuplicateEntryException, SQLException {
        if(author == null) {
            throw new InvalidAuthorException("The given author is null!");
        }

        if(!author.isValid()) {
            throw new InvalidAuthorException("The given author is invalid!");
        }

        String query = """
            INSERT INTO authors (name)
            VALUES (?)
        """;

        try(
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString(1, author.getName());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }

                throw new SQLException("Failed to retrieve the generated ID for author '" + author.getName() + "'.");
            }
        } catch (SQLException e) {
            if(e.getErrorCode() == 1062) {
                throw new DuplicateEntryException("This author is already in the database! (name match)");
            } else {
                throw e;
            }
        }
    }

    /* CRUD - Read Section */

    /**
     * Fetch all authors from the database.
     *
     * @param conn A connection to the database
     * @return A list of all authors in the database. {@code List<Author>}
     * @throws SQLException if the database operation fails.
     */
    public List<Author> fetchAll(Connection conn) throws SQLException {
        List<Author> authors = new ArrayList<>();

        String query = "SELECT id, name FROM authors";

        try (
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {
                try {
                    // Attempt to load a new author object with the contents of the current result
                    Author author = new Author(
                            rs.getString("name")
                    );

                    author.setId(rs.getInt("id"));

                    // Add the author to the
                    authors.add(author);
                } catch (IllegalArgumentException e) {
                    throw new SQLException(
                            "Failed to load author with ID " + rs.getInt("id"),
                            e
                    );
                }
            }
        }

        return authors;
    }

    /**
     * Fetch an author by its ID.
     *
     * @param conn A connection to the database
     * @param authorId the ID of the requested author. {@code int}
     * @return the matching author. {@code Author}
     * @throws SQLException if the database operation fails.
     * @throws EntryNotFoundException if no author has the given ID.
     */
    public Author fetchById(Connection conn, int authorId) throws SQLException, InvalidAuthorException, EntryNotFoundException {
        if (authorId <= 0) {
            throw new IllegalArgumentException("An author ID must be greater than zero;");
        }

        String query = """
                SELECT id, name
                FROM authors
                WHERE id = ?
                """;

        try(
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            // Load book ID into query
            stmt.setInt(1, authorId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntryNotFoundException("No author was found with id: " + authorId);
                }

                try {
                    Author author = new Author(
                            rs.getString("name")
                    );

                    author.setId(rs.getInt("id"));

                    return author;
                } catch (IllegalArgumentException e) {
                    throw new InvalidAuthorException("There is a problem with the author that has ID: "+authorId);
                }
            }
        }
    }

    /**
     * This function will take a name, validate it, then use it to fetch an author from the database.
     *
     * @param conn A connection to the database
     * @param name The name to use in the search. {@code String}
     * @return The matching author object {@code Author}
     * @throws SQLException If there is an issue connecting to the database
     * @throws EntryNotFoundException If the author is not found
     */
    public Author fetchByName(Connection conn, String name) throws SQLException, EntryNotFoundException, InvalidAuthorException {

        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Author name can not be null or empty.");
        }

        String query = "SELECT id, name FROM authors WHERE (name = ?)";

        try(
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            // Load ISBN into both query slots
            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    throw new EntryNotFoundException("No author was found with name: " + name);
                }

                try {
                    Author author = new Author(
                            rs.getString("name")
                    );

                    author.setId(rs.getInt("id"));

                    return author;
                } catch (IllegalArgumentException e) {
                    throw new InvalidAuthorException("There is a problem with the author that has name: "+name);
                }
            }
        }
    }

    /* CRUD - Update Section */

    /**
     * Update an author's name
     *
     * @param conn A connection to the database
     * @param id The id of the author to update
     * @param name New author name
     * @throws EntryNotFoundException If there is not an author with the given ID in the database.
     * @throws SQLException If there is an issue accessing the database.
     */
    public void updateName(Connection conn, int id, String name) throws EntryNotFoundException, SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Given an invalid ID");
        }

        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Given an invalid name.");
        }

        String query = "UPDATE authors SET name = ? WHERE id = ?";

        try (
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, name);
            stmt.setInt(2, id);


            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new EntryNotFoundException("No author was found with id: " + id);
            }
        }
    }

    /* CRUD - Destroy Section */

    /**
     * Attempts to delete an author from the database by its id
     *
     * @param conn A connection to the database
     * @param authorId The id of the author to delete
     * @throws SQLException If there is an error connecting to the database
     * @throws EntryNotFoundException If there is no book with this ISBN in the database
     */
    public void deleteById(Connection conn, int authorId) throws SQLException, EntryNotFoundException {
        if (authorId <= 0) {
            throw new IllegalArgumentException("An author ID must be greater than zero;");
        }

        String query = "DELETE FROM authors WHERE id = ?";

        try (
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {

            stmt.setInt(1, authorId);

            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new EntryNotFoundException("No author was found with id: " + authorId);
            }
        }
    }

    /**
     * Attempts to delete an author from the database by its name
     *
     * @param conn A connection to the database
     * @param name The name of the author to delete
     * @throws SQLException If there is an error connecting to the database
     * @throws EntryNotFoundException If there is no book with this name in the database
     */
    public void deleteByName(Connection conn, String name) throws SQLException, EntryNotFoundException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("An author's name can not be null or blank");
        }

        String query = "DELETE FROM authors WHERE name = ?";

        try (
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {

            stmt.setString(1, name);

            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new EntryNotFoundException("No author was found with name: " + name);
            }
        }
    }

    /* Utility */

    /**
     * Checks whether an author exists in the database by its ID.
     *
     * @param conn A connection to the database
     * @param authorId The ID of the author to check.
     * @return true if the author exists, false otherwise.
     * @throws SQLException If there is an issue accessing the database.
     */
    public boolean existsById(Connection conn, int authorId) throws SQLException {
        if (authorId <= 0) {
            throw new IllegalArgumentException(
                    "An author ID must be greater than zero."
            );
        }

        String query = "SELECT 1 FROM authors WHERE id = ?";

        try (
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, authorId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Checks whether an author exists in the database by its name.
     *
     * @param conn A connection to the database
     * @param name The name of the author to check.
     * @return true if the author exists, false otherwise.
     * @throws SQLException If there is an issue accessing the database.
     */
    public boolean existsByName(Connection conn, String name) throws SQLException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "An author name can not be blank or null."
            );
        }

        String query = "SELECT 1 FROM authors WHERE name = ?";

        try (
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
