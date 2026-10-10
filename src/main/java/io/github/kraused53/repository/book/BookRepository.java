package io.github.kraused53.repository.book;

import io.github.kraused53.database.Database;
import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.models.Book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles database access for books.
 * All SQL operations related to books are performed here.
 */
public class BookRepository {

    // Define logging system
    private static final Logger logger = LoggerFactory.getLogger(BookRepository.class);

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

        logger.info("BookRepository.findAll: Fetching all books.");

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

        logger.info("BookRepository.findAll: Found {} books.", result.size());
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

        logger.info("BookRepository.findById: Searching for a book with id: {}.", bookId);

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bookId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    logger.warn("BookRepository.findById: No book with id {} was found.", bookId);
                    throw new EntryNotFoundException("No book was found with id: " + bookId);
                }

                Book book = new Book();

                book.setId(bookId);
                book.setTitle(rs.getString("title"));
                book.setIsbn10(rs.getString("isbn_10"));
                book.setIsbn13(rs.getString("isbn_13"));
                book.setPageCount(rs.getInt("page_count"));
                book.setDescription(rs.getString("description"));

                logger.info("BookRepository.findById: The book '{}' was found.", book.getTitle());

                return book;
            }
        }
    }

    /**
     * Insert a new book into the database.
     *
     * @param book the book to insert.
     * @return the ID of the newly inserted book.
     * @throws SQLException if the database operation fails.
     * @throws DuplicateEntryException if a unique constraint is violated.
     */
    public int insert(Book book)
            throws SQLException, DuplicateEntryException {

        String query = """
            INSERT INTO books
                (title, isbn_13, isbn_10, page_count, description)
            VALUES (?, ?, ?, ?, ?)
            """;

        logger.info(
                "BookRepository.insert: Adding the book '{}' to the database.",
                book.getTitle()
        );

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     query,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getIsbn13());
            stmt.setString(3, book.getIsbn10());
            stmt.setInt(4, book.getPageCount());
            stmt.setString(5, book.getDescription());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int bookId = generatedKeys.getInt(1);

                    logger.info(
                            "BookRepository.insert: Book '{}' added with ID {}.",
                            book.getTitle(),
                            bookId
                    );

                    return bookId;
                }

                throw new SQLException(
                        "Failed to retrieve the generated ID for book '"
                                + book.getTitle() + "'."
                );
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                logger.warn(
                        "BookRepository.insert: Book '{}' already exists in the database.",
                        book.getTitle()
                );

                throw new DuplicateEntryException(
                        "A book with a matching ISBN or other unique value "
                                + "already exists in the database!"
                );
            }

            logger.error(
                    "BookRepository.insert: Failed to add book '{}'.",
                    book.getTitle(),
                    e
            );

            throw e;
        }
    }

    /**
     * Insert a new book into the database.
     *
     * @param conn Shared database connection. allows the caller to better handle situations where an operation needs to be rolled-back
     * @param book the book to insert.
     * @return the ID of the newly inserted book.
     * @throws SQLException if the database operation fails.
     * @throws DuplicateEntryException if a unique constraint is violated.
     */
    public int insert(Connection conn, Book book)
            throws SQLException, DuplicateEntryException {

        String query = """
            INSERT INTO books
                (title, isbn_13, isbn_10, page_count, description)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(
                query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getIsbn13());
            stmt.setString(3, book.getIsbn10());
            stmt.setInt(4, book.getPageCount());
            stmt.setString(5, book.getDescription());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

            throw new SQLException("Failed to retrieve the generated book ID.");
        } catch (SQLException e){
            if (e.getErrorCode() == 1062) {
                logger.warn(
                        "BookRepository.insert: The book '{}' already exists in the database.",
                        book.getTitle()
                );

                throw new DuplicateEntryException(
                        "A book with this name already exists in the database!"
                );
            }

            logger.error(
                    "BookRepository.insert: Failed to add book '{}'.",
                    book.getTitle(),
                    e
            );

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

        logger.info("BookRepository.update: Updating the book with id: {}", bookId);

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
                    logger.warn("BookRepository.update: This book was not found.");
                    throw new EntryNotFoundException(
                            "No book was found with id: " + bookId
                    );
                }

                logger.info("BookRepository.update: There was nothing to update.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {

                logger.warn("BookRepository.update: This update would have caused an isbn overlap. No records altered.");
                throw new DuplicateEntryException(
                        "A book with a matching ISBN or other unique value "
                                + "already exists in the database!"
                );
            }

            throw e;
        }

        logger.info("BookRepository.update: The book was updated.");
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

        logger.info("BookRepository.delete: Deleting the book with id: {}", bookId);

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bookId);

            int rows = stmt.executeUpdate();

            if (rows == 0) {
                logger.warn("BookRepository.delete: The book was not found.");
                throw new EntryNotFoundException(
                        "No book was found with id: " + bookId
                );
            }
        }
        logger.info("BookRepository.delete: The book was deleted.");
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

        logger.debug("BookRepository.existsById: Looking for a book with id: {}", bookId);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean found = rs.next();

                if(found) {
                    logger.debug("BookRepository.existsById: Book found");
                    return true;
                }

                logger.debug("BookRepository.existsById: Book not found");
                return false;
            }
        }
    }

    /**
     * This function will check to see if a book exists in the database by using its ISBNs.
     *
     * @param book The book to check the database for.
     * @return True if the book is found, otherwise false
     * @throws SQLException
     */
    public boolean exists(Book book) throws SQLException {
        if (book == null) {
            logger.warn("BookRepository.bookExists: Given a null book object.");
            throw new IllegalArgumentException("Book cannot be null");
        }

        if (!book.isValid()) {
            logger.warn("BookRepository.bookExists: The given book object is not valid.");
            throw new IllegalArgumentException("Book is not valid");
        }

        String sql = """
        SELECT 1
        FROM books
        WHERE (? IS NOT NULL AND isbn_13 = ?)
           OR (? IS NOT NULL AND isbn_10 = ?)
        LIMIT 1
        """;

        logger.info("BookRepository.bookExists: Determining if a book with isbn {} or {} exists already.", book.getIsbn10(), book.getIsbn13());

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, book.getIsbn13());
            statement.setString(2, book.getIsbn13());
            statement.setString(3, book.getIsbn10());
            statement.setString(4, book.getIsbn10());

            try (ResultSet resultSet = statement.executeQuery()) {
                if(resultSet.next()) {
                    logger.info("BookRepository.bookExists: Book is already in the database.");
                    return true;
                }
                logger.info("BookRepository.bookExists: Book is not in the database already.");
                return false;
            }
        }
    }

    /**
     * This function will check to see if a book exists in the database by using its ISBNs.
     *
     * @param conn Shared database connection. allows the caller to better handle situations where an operation needs to be rolled-back
     * @param book The book to check the database for.
     * @return True if the book is found, otherwise false
     * @throws SQLException
     */
    public boolean exists(Connection conn, Book book) throws SQLException {
        if (book == null) {
            logger.warn("BookRepository.bookExists: Given a null book object.");
            throw new IllegalArgumentException("Book cannot be null");
        }

        if (!book.isValid()) {
            logger.warn("BookRepository.bookExists: The given book object is not valid.");
            throw new IllegalArgumentException("Book is not valid");
        }

        String sql = """
        SELECT 1
        FROM books
        WHERE (? IS NOT NULL AND isbn_13 = ?)
           OR (? IS NOT NULL AND isbn_10 = ?)
        LIMIT 1
        """;

        logger.info("BookRepository.bookExists: Determining if a book with isbn {} or {} exists already.", book.getIsbn10(), book.getIsbn13());

        try (PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, book.getIsbn13());
            statement.setString(2, book.getIsbn13());
            statement.setString(3, book.getIsbn10());
            statement.setString(4, book.getIsbn10());

            try (ResultSet resultSet = statement.executeQuery()) {
                if(resultSet.next()) {
                    logger.info("BookRepository.bookExists: Book is already in the database.");
                    return true;
                }
                logger.info("BookRepository.bookExists: Book is not in the database already.");
                return false;
            }
        }
    }
}