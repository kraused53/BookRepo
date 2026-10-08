package io.github.kraused53.repository.author;

import io.github.kraused53.models.Author;
import io.github.kraused53.database.Database;
import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    /**
     * Fetch all authors from the database.
     *
     * @return A list of all authors in the database.
     * @throws SQLException if the database operation fails.
     */
    public List<Author> findAll() throws SQLException {
        List<Author> result = new ArrayList<>();

        String query = "SELECT id, name FROM authors";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Author author = new Author();

                author.setId(rs.getInt("id"));
                author.setName(rs.getString("name"));

                result.add(author);
            }
        }

        return result;
    }

    /**
     * Fetch an author by their ID.
     *
     * @param authorId the ID of the requested author.
     * @return the matching author.
     * @throws SQLException if the database operation fails.
     * @throws EntryNotFoundException if no author has the given ID.
     */
    public Author findById(int authorId)
            throws SQLException, EntryNotFoundException {

        String query = "SELECT name FROM authors WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, authorId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntryNotFoundException(
                            "No author was found with id: " + authorId
                    );
                }

                Author author = new Author();
                author.setId(authorId);
                author.setName(rs.getString("name"));

                return author;
            }
        }
    }

    /**
     * Insert a new author into the database.
     *
     * @param author the author to insert.
     * @throws SQLException if the database operation fails.
     * @throws DuplicateEntryException if an author violates a unique constraint.
     */
    public void insert(Author author)
            throws SQLException, DuplicateEntryException {

        String query = "INSERT INTO authors (name) VALUES (?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, author.getName());

            stmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new DuplicateEntryException(
                        "An author with this name already exists in the database!"
                );
            }

            throw e;
        }
    }

    /**
     * Update the name of an existing author.
     *
     * @param authorId the ID of the author to update.
     * @param newName the new author name.
     * @throws SQLException if the database operation fails.
     * @throws DuplicateEntryException if the name violates a unique constraint.
     * @throws EntryNotFoundException if the author does not exist.
     */
    public void update(int authorId, String newName)
            throws SQLException, DuplicateEntryException,
            EntryNotFoundException {

        String query = "UPDATE authors SET name = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newName);
            stmt.setInt(2, authorId);

            int rows = stmt.executeUpdate();

            if (rows == 0 && !existsById(conn, authorId)) {
                throw new EntryNotFoundException(
                        "No author was found with id: " + authorId
                );
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new DuplicateEntryException(
                        "An author with this name already exists in the database!"
                );
            }

            throw e;
        }
    }

    /**
     * Delete an author by their ID.
     *
     * @param authorId the ID of the author to delete.
     * @throws SQLException if the database operation fails.
     * @throws EntryNotFoundException if the author does not exist.
     */
    public void delete(int authorId)
            throws SQLException, EntryNotFoundException {

        String query = "DELETE FROM authors WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, authorId);

            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new EntryNotFoundException(
                        "No author was found with id: " + authorId
                );
            }
        }
    }

    /**
     * Check whether an author exists using an existing connection.
     *
     * @param conn the connection to use.
     * @param authorId the ID of the author to check.
     * @return true if the author exists; false otherwise.
     * @throws SQLException if the database operation fails.
     */
    private boolean existsById(Connection conn, int authorId)
            throws SQLException {

        String query = "SELECT 1 FROM authors WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, authorId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}