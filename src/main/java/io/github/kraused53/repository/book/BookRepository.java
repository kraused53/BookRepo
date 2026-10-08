package io.github.kraused53.repository.book;

import io.github.kraused53.database.Database;
import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.models.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles database access for books.
 * All SQL operations related to books are performed here.
 */
public class BookRepository {

    /**
     * Nothing needs to be initialized when this class is instantiated.
     */
    public BookRepository() {}

    /**
     * Fetch all books from the database.
     *
     * @return A list of all books in the database.
     * @throws SQLException if the database operation fails.
     */
    public List<Book> findAll() throws SQLException {
        List<Book> result = new ArrayList<>();

        String query = """
                SELECT id, title, isbn_13, isbn_10, page_count, description
                FROM books
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Book book = new Book();

                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setIsbn13(rs.getString("isbn_13"));
                book.setIsbn10(rs.getString("isbn_10"));
                book.setPageCount(rs.getInt("page_count"));
                book.setDescription(rs.getString("description"));

                result.add(book);
            }
        }

        return result;
    }

    /**
     * Fetch a book by its ID.
     *
     * @param bookId the ID of the requested book.
     * @return the matching book.
     * @throws SQLException if the database operation fails.
     * @throws EntryNotFoundException if no book has the given ID.
     */
    public Book findById(int bookId)
            throws SQLException, EntryNotFoundException {

        String query = """
                SELECT title, isbn_10, isbn_13, page_count, description
                FROM books
                WHERE id = ?
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bookId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntryNotFoundException(
                            "No book was found with id: " + bookId
                    );
                }

                Book book = new Book();

                book.setId(bookId);
                book.setTitle(rs.getString("title"));
                book.setIsbn10(rs.getString("isbn_10"));
                book.setIsbn13(rs.getString("isbn_13"));
                book.setPageCount(rs.getInt("page_count"));
                book.setDescription(rs.getString("description"));

                return book;
            }
        }
    }

    /**
     * Insert a new book into the database.
     *
     * @param book the book to insert.
     * @throws SQLException if the database operation fails.
     * @throws DuplicateEntryException if a unique constraint is violated.
     */
    public void insert(Book book)
            throws SQLException, DuplicateEntryException {

        String query = """
                INSERT INTO books
                    (title, isbn_13, isbn_10, page_count, description)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getIsbn13());
            stmt.setString(3, book.getIsbn10());
            stmt.setInt(4, book.getPageCount());
            stmt.setString(5, book.getDescription());

            stmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new DuplicateEntryException(
                        "A book with a matching ISBN or other unique value "
                                + "already exists in the database!"
                );
            }

            throw e;
        }
    }

    /**
     * Update an existing book.
     *
     * @param bookId the ID of the book to update.
     * @param book the updated book data.
     * @throws SQLException if the database operation fails.
     * @throws DuplicateEntryException if a unique constraint is violated.
     * @throws EntryNotFoundException if the book does not exist.
     */
    public void update(int bookId, Book book)
            throws SQLException, DuplicateEntryException,
            EntryNotFoundException {

        String query = """
                UPDATE books
                SET title = ?,
                    isbn_10 = ?,
                    isbn_13 = ?,
                    page_count = ?,
                    description = ?
                WHERE id = ?
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getIsbn10());
            stmt.setString(3, book.getIsbn13());
            stmt.setInt(4, book.getPageCount());
            stmt.setString(5, book.getDescription());
            stmt.setInt(6, bookId);

            int rows = stmt.executeUpdate();

            if (rows == 0) {
                // MySQL may report zero when the record exists but no
                // values changed, so verify whether the ID exists.
                if (!existsById(conn, bookId)) {
                    throw new EntryNotFoundException(
                            "No book was found with id: " + bookId
                    );
                }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new DuplicateEntryException(
                        "A book with a matching ISBN or other unique value "
                                + "already exists in the database!"
                );
            }

            throw e;
        }
    }

    /**
     * Delete a book by its ID.
     *
     * @param bookId the ID of the book to delete.
     * @throws SQLException if the database operation fails.
     * @throws EntryNotFoundException if the book does not exist.
     */
    public void delete(int bookId)
            throws SQLException, EntryNotFoundException {

        String query = "DELETE FROM books WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bookId);

            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new EntryNotFoundException(
                        "No book was found with id: " + bookId
                );
            }
        }
    }

    /**
     * Check whether a book exists using an existing connection.
     * The caller retains ownership of the connection.
     *
     * @param conn the connection to use.
     * @param bookId the book ID to check.
     * @return true if the book exists; false otherwise.
     * @throws SQLException if the database operation fails.
     */
    private boolean existsById(Connection conn, int bookId)
            throws SQLException {

        String query = "SELECT 1 FROM books WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}