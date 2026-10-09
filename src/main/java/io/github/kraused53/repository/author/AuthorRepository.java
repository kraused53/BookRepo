package io.github.kraused53.repository.author;

import io.github.kraused53.models.Author;
import io.github.kraused53.database.Database;
import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    // Define logging system
    private static final Logger logger =
            LoggerFactory.getLogger(AuthorRepository.class);

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

        logger.info("AuthorRepository.findAll: Fetching all authors.");

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Author author = new Author();

                author.setId(rs.getInt("id"));
                author.setName(rs.getString("name"));

                result.add(author);
            }

        } catch (SQLException e) {
            logger.error(
                    "AuthorRepository.findAll: Failed to fetch authors.",
                    e
            );
            throw e;
        }

        logger.info(
                "AuthorRepository.findAll: Found {} authors.",
                result.size()
        );

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

        logger.info(
                "AuthorRepository.findById: Searching for an author with id: {}.",
                authorId
        );

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, authorId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    logger.warn(
                            "AuthorRepository.findById: No author with id {} was found.",
                            authorId
                    );

                    throw new EntryNotFoundException(
                            "No author was found with id: " + authorId
                    );
                }

                Author author = new Author();
                author.setId(authorId);
                author.setName(rs.getString("name"));

                logger.info(
                        "AuthorRepository.findById: The author '{}' was found.",
                        author.getName()
                );

                return author;
            }

        } catch (SQLException e) {
            logger.error(
                    "AuthorRepository.findById: Failed to fetch author with id {}.",
                    authorId,
                    e
            );
            throw e;
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

        logger.info(
                "AuthorRepository.insert: Adding the author '{}' to the database.",
                author.getName()
        );

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, author.getName());

            stmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                logger.warn(
                        "AuthorRepository.insert: The author '{}' already exists in the database.",
                        author.getName()
                );

                throw new DuplicateEntryException(
                        "An author with this name already exists in the database!"
                );
            }

            logger.error(
                    "AuthorRepository.insert: Failed to add author '{}'.",
                    author.getName(),
                    e
            );

            throw e;
        }

        logger.info(
                "AuthorRepository.insert: The author '{}' was added to the database.",
                author.getName()
        );
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

        logger.info(
                "AuthorRepository.update: Updating the author with id {} to '{}'.",
                authorId,
                newName
        );

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newName);
            stmt.setInt(2, authorId);

            int rows = stmt.executeUpdate();

            if (rows == 0) {
                if (!existsById(conn, authorId)) {
                    logger.warn(
                            "AuthorRepository.update: No author with id {} was found.",
                            authorId
                    );

                    throw new EntryNotFoundException(
                            "No author was found with id: " + authorId
                    );
                }

                logger.info(
                        "AuthorRepository.update: The author with id {} already has the specified name, or no values changed.",
                        authorId
                );
                return;
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                logger.warn(
                        "AuthorRepository.update: Updating author {} to '{}' would cause a duplicate name. No records altered.",
                        authorId,
                        newName
                );

                throw new DuplicateEntryException(
                        "An author with this name already exists in the database!"
                );
            }

            logger.error(
                    "AuthorRepository.update: Failed to update author with id {}.",
                    authorId,
                    e
            );

            throw e;
        }

        logger.info(
                "AuthorRepository.update: The author with id {} was updated.",
                authorId
        );
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

        logger.info(
                "AuthorRepository.delete: Deleting the author with id {}.",
                authorId
        );

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, authorId);

            int rows = stmt.executeUpdate();

            if (rows == 0) {
                logger.warn(
                        "AuthorRepository.delete: No author with id {} was found.",
                        authorId
                );

                throw new EntryNotFoundException(
                        "No author was found with id: " + authorId
                );
            }

        } catch (SQLException e) {
            logger.error(
                    "AuthorRepository.delete: Failed to delete author with id {}.",
                    authorId,
                    e
            );
            throw e;
        }

        logger.info(
                "AuthorRepository.delete: The author with id {} was deleted.",
                authorId
        );
    }

    /**
     * Check whether an author exists using an existing connection.
     *
     * @param conn the connection to use.
     * @param authorId the author ID to check.
     * @return true if the author exists; false otherwise.
     * @throws SQLException if the database operation fails.
     */
    private boolean existsById(Connection conn, int authorId)
            throws SQLException {

        String query = "SELECT 1 FROM authors WHERE id = ?";

        logger.info(
                "AuthorRepository.existsById: Looking for an author with id {}.",
                authorId
        );

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, authorId);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean found = rs.next();

                if (found) {
                    logger.info(
                            "AuthorRepository.existsById: Author found."
                    );
                    return true;
                }

                logger.info(
                        "AuthorRepository.existsById: Author not found."
                );
                return false;
            }
        }
    }
}
