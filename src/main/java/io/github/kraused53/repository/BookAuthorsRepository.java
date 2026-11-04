package io.github.kraused53.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.kraused53.database.Database;
import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.exceptions.InvalidAuthorException;
import io.github.kraused53.exceptions.InvalidBookException;

/**
 * This class handles database access for book/author relations.
 * All SQL operations related to book/author relations are performed here.
 */
public class BookAuthorsRepository {

    /**
     * Nothing needs to be initialized when this class is instantiated.
     */
    public BookAuthorsRepository() {}

    /* CRUD - Create Section */

    /**
     * Create an new book / author relation
     * 
     * @param conn A connection to the database
     * @param bookId The id of the book
     * @param authorId The id of the author
     * @throws DuplicateEntryException If the book / author relation already exists
     * @throws SQLException If there is an error connecting to the database
     */
    public void createNewRelation(Connection conn, int bookId, int authorId) throws DuplicateEntryException, SQLException {
        // Validate Ids
        if(bookId <= 0) {
            throw new IllegalArgumentException("Given an invalid book Id.");
        }

        if(authorId <= 0) {
            throw new IllegalArgumentException("Given an invalid author Id.");
        }

        String query = "INSERT INTO book_authors (book_id, author_id) VALUES (?, ?)";

        try (
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, authorId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            if(e.getErrorCode() == 1062) {
                throw new DuplicateEntryException("This book/author relation is already in the database! ([book_id, author_id] match)");
            }

            throw e;
        }
    }

    /* CRUD - Read Section */

    /**
     * Fetch the id of every book currently related to the given author id
     * 
     * @param conn A connection to the database
     * @param authorId The id of the author
     * @return A list of the ids of all books related to the given author id
     * @throws SQLException If there is an issue connecting to the database
     * @throws InvalidBookException If the query returns an invalid book id
     * @throws InvalidAuthorException If given an invalid author id
     */
    public List<Integer> fetchAllBooksIdsRelatedToAuthorId(Connection conn, int authorId) throws SQLException, InvalidBookException, InvalidAuthorException {

        // Validate id
        if(authorId <= 0) {
            throw new InvalidAuthorException("Given invalid author ID: " + authorId);
        } 

        List<Integer> bookIds = new ArrayList<>();

        String query = "SELECT book_id FROM book_authors WHERE author_id = ?";

        try (
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, authorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    int bookId = rs.getInt("book_id");

                    if(bookId <= 0) {
                        throw new InvalidBookException("Found an invalid book id: " + bookId);
                    }

                    bookIds.add(bookId);
                }
            }
        }

        return bookIds;
    }

    /**
     * Fetch the id of every author currently related to the given book id
     * 
     * @param conn A connection to the database
     * @param bookId The id of the book
     * @return A list of the ids of all authors related to the given book id
     * @throws SQLException If there is an issue connecting to the database
     * @throws InvalidBookException If given an invalid book id
     * @throws InvalidAuthorException If the query returns an invalid author id
     */
    public List<Integer> fetchAllAuthorIdsRelatedToBookId(Connection conn, int bookId) throws InvalidAuthorException, SQLException, InvalidBookException {
        // Validate id
        if(bookId <= 0) {
            throw new InvalidBookException("Given invalid author ID: " + bookId);
        } 

        List<Integer> authorIds = new ArrayList<>();

        String query = "SELECT author_id FROM book_authors WHERE book_id = ?";

        try(
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, bookId);

            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    int authorId = rs.getInt("author_id");

                    if(authorId <= 0) {
                        throw new InvalidAuthorException("Found an invalid author id: " + authorId);
                    }

                    authorIds.add(authorId);
                }
            }
        }

        return authorIds;
    }

    /*
     * CRUD - Update Section
     *
     * Updating a relationship is handled by deleting the existing
     * relationship and creating a new one.
     */

    /* CRUD - Destroy Section */

    /**
     * Delete a book, author relation
     * 
     * @param conn A connection to the database
     * @param bookId The book's id
     * @param authorId The author's id
     * @throws SQLException If there is an error accessing the database
     * @throws EntryNotFoundException If no rows are deleted (indicates that relation did not exist)
     */
    public void deleteRelation(Connection conn, int bookId, int authorId) throws SQLException, EntryNotFoundException {
        // Validate Ids
        if(bookId <= 0) {
            throw new IllegalArgumentException("Given an invalid book Id: " + bookId);
        }

        if(authorId <= 0) {
            throw new IllegalArgumentException("Given an invalid author Id: " + authorId);
        }

        String query = "DELETE FROM book_authors WHERE book_id = ? AND author_id = ?";

        try (
            PreparedStatement stmt = conn.prepareStatement(query);
        ) {

            stmt.setInt(1, bookId);
            stmt.setInt(2, authorId);

            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new EntryNotFoundException("No relation was found [book_id, author_id] [" + bookId + ", " + authorId + "]");
            }
        }
    }

    /* Utility */

    /**
     * Checks whether a book and an author are related in the database by their IDs.
     *
     * @param conn A connection to the database
     * @param bookId The book's id
     * @param authorId The author's id
     * @return true if there is an existing relation, false otherwise
     * @throws SQLException If there is an error accessing the database
     */
    public boolean doesRelationExist(Connection conn, int bookId, int authorId) throws SQLException {
        // Validate Ids
        if(bookId <= 0) {
            throw new IllegalArgumentException("Given an invalid book Id.");
        }

        if(authorId <= 0) {
            throw new IllegalArgumentException("Given an invalid author Id.");
        }

        String query = "SELECT 1 FROM book_authors WHERE book_id = ? AND author_id = ?";

        try(
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, authorId);

            try(ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
