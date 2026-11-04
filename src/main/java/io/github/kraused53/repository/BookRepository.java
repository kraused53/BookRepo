package io.github.kraused53.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import io.github.kraused53.database.Database;
import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.exceptions.InvalidISBNException;
import io.github.kraused53.exceptions.InvalidBookException;
import io.github.kraused53.models.Book;
import io.github.kraused53.utils.ISBNValidator;

/**
 * This class handles database access for books.
 * All SQL operations related to books are performed here.
 */
public class BookRepository {

    /**
     * Nothing needs to be initialized when this class is instantiated.
     */
    public BookRepository() {}

    /* CRUD - Create Section */

    /**
     * 
     * @param book The book object to add to the database.
     * @return The id if the book after it is added to the database. {@code int}
     * @throws SQLException If there is an issue connecting to the database
     * @throws DuplicateEntryException If the book is already in the database
     * @throws InvalidBookException If the book is not valid
     */
    public int insert(Book book) throws SQLException, DuplicateEntryException, InvalidBookException {
        if(book == null) {
            throw new InvalidBookException("The given book is null!");
        }
        
        if(!book.isValid()) {
            throw new InvalidBookException("The given book is invalid!");
        }

        String query = """
            INSERT INTO books
                (title, isbn_13, isbn_10, page_count, description)
            VALUES (?, ?, ?, ?, ?)
        """;

        try(
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getIsbn13());
            stmt.setString(3, book.getIsbn10());
            stmt.setInt(4, book.getPageCount());
            stmt.setString(5, book.getDescription());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int bookId = generatedKeys.getInt(1);

                    return bookId;
                }

                throw new SQLException("Failed to retrieve the generated ID for book '" + book.getTitle() + "'.");
            }
        } catch (SQLException sqle) {
            if(sqle.getErrorCode() == 1062) {
                throw new DuplicateEntryException("This book is already in the database! (ISBN match)");
            } else {
                throw sqle;
            }
        }
    }

    /* CRUD - Read Section */

    /**
     * Fetch all books from the database.
     *
     * @return A list of all books in the database. {@code List<Book>}
     * @throws SQLException if the database operation fails. 
     */
    public List<Book> fetchAll() throws SQLException {
        List<Book> books = new ArrayList<>();

        String query = "SELECT id, title, isbn_13, isbn_10, page_count, description FROM books";

        try (
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
        ) {

            while (rs.next()) {
                try {
                    // Attempt to load a new book object with the contents of the current result
                    Book book = new Book(
                        rs.getString("title"),
                        rs.getString("isbn_10"),
                        rs.getString("isbn_13"),
                        rs.getInt("page_count"),
                        rs.getString("description")
                    );

                    book.setId(rs.getInt("id"));

                    // Add the book to the
                    books.add(book);
                } catch (InvalidISBNException | IllegalArgumentException e) {
                    throw new SQLException(
                        "Failed to load book with ID " + rs.getInt("id"),
                        e
                    );
                }
            }
        }

        return books;
    }

    /**
     * Fetch a book by its ID.
     *
     * @param bookId the ID of the requested book. {@code int}
     * @return the matching book. {@code Book}
     * @throws SQLException if the database operation fails.
     * @throws EntryNotFoundException if no book has the given ID.
     * @throws InvalidBookException 
     * @throws InvalidISBNException 
     */
    public Book fetchById(int bookId) throws SQLException, EntryNotFoundException, InvalidBookException {
        if (bookId <= 0) {
            throw new IllegalArgumentException("A book ID must be greater than zero;");
        }
        
        String query = """
                SELECT id, title, isbn_10, isbn_13, page_count, description
                FROM books
                WHERE id = ?
                """;
        
        try(
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            // Load book ID into query
            stmt.setInt(1, bookId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntryNotFoundException("No book was found with id: " + bookId);
                }

                try {
                    Book book = new Book(
                        rs.getString("title"),
                        rs.getString("isbn_10"),
                        rs.getString("isbn_13"),
                        rs.getInt("page_count"),
                        rs.getString("description")
                    );

                    book.setId(rs.getInt("id"));

                    return book;
                } catch (InvalidISBNException | IllegalArgumentException e) {
                    throw new InvalidBookException("There is a problem with the book that has ID: "+bookId);
                }
            }
        }
    }


    /**
     * This function will take an isbn, validate it, then use it to fetch a book from the database.
     * 
     * @param isbn The isbn to use in the search. Can be either ISBN10 or ISBN13 {@code String}
     * @return The matching book object {@code Book}
     * @throws SQLException If there is an issue connecting to the database
     * @throws InvalidISBNException  If the given 
     * @throws EntryNotFoundException 
     * @throws InvalidBookException 
     */
    public Book fetchByISBN(String isbn) throws SQLException, InvalidISBNException, EntryNotFoundException, InvalidBookException {

        String parsedIsbn = "";

        // Attempt to parse the given isbn
        try {
            parsedIsbn = ISBNValidator.validateISBN10(isbn);
        } catch (InvalidISBNException notISBN13) {
            try {
                parsedIsbn = ISBNValidator.validateISBN13(isbn);
            } catch (InvalidISBNException e) {
                throw new InvalidISBNException("The given isbn ["+isbn+"] is not a valid ISBN10 or ISBN13!");
            }
        }

        String query = "SELECT id, title, isbn_10, isbn_13, page_count, description FROM books WHERE (isbn_10 = ? OR isbn_13 = ?)";

        try(
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            // Load ISBN into both query slots
            stmt.setString(1, parsedIsbn);
            stmt.setString(2, parsedIsbn);

            try (ResultSet rs = stmt.executeQuery()) {
                
                if (!rs.next()) {
                    throw new EntryNotFoundException("No book was found with isbn: " + isbn);
                }

                try {
                    Book book = new Book(
                        rs.getString("title"),
                        rs.getString("isbn_10"),
                        rs.getString("isbn_13"),
                        rs.getInt("page_count"),
                        rs.getString("description")
                    );

                    book.setId(rs.getInt("id"));

                    return book;
                } catch (InvalidISBNException | IllegalArgumentException e) {
                    throw new InvalidBookException("There is a problem with the book that has ISBN: "+isbn);
                }
            }
        }
    }


    /* CRUD - Update Section */

    /**
     * Update a book's title
     * 
     * @param id The id of the book to update
     * @param title New book title
     * @throws EntryNotFoundException If there is not a book with the given ID in the database.
     * @throws SQLException If there is an issue accesing the database.
     */
    public void updateTitle(int id, String title) throws EntryNotFoundException, SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Given an invalid ID");
        }
        
        if(title == null || title.isBlank()) {
            throw new IllegalArgumentException("Given an invalid title.");
        }

        String query = "UPDATE books SET title = ? WHERE id = ?";

        try (
            Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
            stmt.setString(1, title);
            stmt.setInt(2, id);


            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new EntryNotFoundException("No book was found with id: " + id);
            }
        }
    }

    /**
     * 
     * @param id The id of the book to update
     * @param isbn10 New book ISBN10
     * @throws EntryNotFoundException If there is not a book with the given ID in the database.
     * @throws SQLException If there is an issue accesing the database.
     * @throws InvalidISBNException When given an invalid ISBN.
     */
    public void updateIsbn10(int id, String isbn10) throws SQLException, EntryNotFoundException, InvalidISBNException {
        if (id <= 0) {
            throw new IllegalArgumentException("Given an invalid ID");
        }
        
        String parsedIsbn = ISBNValidator.validateISBN10(isbn10);

        String query = "UPDATE books SET isbn_10 = ? WHERE id = ?";

        try (
            Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
            stmt.setString(1, parsedIsbn);
            stmt.setInt(2, id);


            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new EntryNotFoundException("No book was found with id: " + id);
            }
        }
    }

    /**
     * 
     * @param id The id of the book to update
     * @param isbn13 New book ISBN13
     * @throws EntryNotFoundException If there is not a book with the given ID in the database.
     * @throws SQLException If there is an issue accesing the database.
     * @throws InvalidISBNException When given an invalid ISBN.
     */
    public void updateIsbn13(int id, String isbn13) throws EntryNotFoundException, InvalidISBNException, SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Given an invalid ID");
        }
        
        String parsedIsbn = ISBNValidator.validateISBN13(isbn13);
        
        String query = "UPDATE books SET isbn_13 = ? WHERE id = ?";

        try (
            Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
            stmt.setString(1, parsedIsbn);
            stmt.setInt(2, id);


            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new EntryNotFoundException("No book was found with id: " + id);
            }
        }
    }

    /**
     * 
     * @param id The id of the book to update
     * @param pageCount New book page count
     * @throws EntryNotFoundException If there is not a book with the given ID in the database.
     * @throws SQLException If there is an issue accesing the database.
     */
    public void updatePageCount(int id, int pageCount) throws SQLException, EntryNotFoundException {
        if (id <= 0) {
            throw new IllegalArgumentException("Given an invalid ID");
        }
        
        if(pageCount <= 0) {
            throw new IllegalArgumentException("Given an invalid page count");
        }

        String query = "UPDATE books SET page_count = ? WHERE id = ?";

        try (
            Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
            stmt.setInt(1, pageCount);
            stmt.setInt(2, id);


            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new EntryNotFoundException("No book was found with id: " + id);
            }
        }
    }

    /**
     * 
     * @param id The id of the book to update
     * @param description New book descriptin
     * @throws EntryNotFoundException If there is not a book with the given ID in the database.
     * @throws SQLException If there is an issue accesing the database.
     */
    public void updateBookDescription(int id, String description) throws SQLException, EntryNotFoundException {
        if (id <= 0) {
            throw new IllegalArgumentException("Given an invalid ID");
        }

        // Description can be either null or "", so no need to validate

        String query = "UPDATE books SET description = ? WHERE id = ?";

        try (
            Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
            stmt.setString(1, description);
            stmt.setInt(2, id);


            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new EntryNotFoundException("No book was found with id: " + id);
            }
        }
    }


    /* CRUD - Destroy Section */

    /**
     * Attempts to delete a book from the database by it's id
     * 
     * @param bookId The id of the book to delete
     * @throws SQLException If there is an error connecting to the database
     * @throws EntryNotFoundException If there is no book with this isbn in the database
     */
    public void deleteById(int bookId) throws SQLException, EntryNotFoundException {
        if (bookId <= 0) {
            throw new IllegalArgumentException("A book ID must be greater than zero;");
        }

        String query = "DELETE FROM books WHERE id = ?";

        try (
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
        ) {

            stmt.setInt(1, bookId);

            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new EntryNotFoundException("No book was found with id: " + bookId);
            }
        }
    }

    /**
     * Attempts to delete a book from the database by it's isbn (10 or 13)
     * 
     * @param isbn The isbn of the book to delete
     * @throws InvalidISBNException If the given ISBN is not valid
     * @throws SQLException If there is an error connecting to the database
     * @throws EntryNotFoundException If there is no book with this isbn in the database
     */
    public void deleteByIsbn(String isbn) throws InvalidISBNException, SQLException, EntryNotFoundException {
        String parsedIsbn = "";

        // Attempt to parse the given isbn
        try {
            parsedIsbn = ISBNValidator.validateISBN10(isbn);
        } catch (InvalidISBNException notISBN13) {
            try {
                parsedIsbn = ISBNValidator.validateISBN13(isbn);
            } catch (InvalidISBNException e) {
                throw new InvalidISBNException("The given isbn ["+isbn+"] is not a valid ISBN10 or ISBN13!");
            }
        }

        String query = "DELETE FROM books WHERE (isbn_10 = ? OR isbn_13 = ?)";

        try (
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
        ) {

            stmt.setString(1, parsedIsbn);
            stmt.setString(2, parsedIsbn);

            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new EntryNotFoundException("No book was found with isbn: " + isbn);
            }
        }
    }

    /* Utility */

    /**
     * Checks whether a book exists in the database by its ID.
     *
     * @param bookId The ID of the book to check.
     * @return true if the book exists, false otherwise.
     * @throws SQLException If there is an issue accessing the database.
     */
    public boolean existsById(int bookId) throws SQLException {
        if (bookId <= 0) {
            throw new IllegalArgumentException(
                "A book ID must be greater than zero."
            );
        }

        String query = "SELECT 1 FROM books WHERE id = ?";

        try (
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, bookId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Checks whether a book exists in the database by its ISBN.
     *
     * @param isbn The ISBN-10 or ISBN-13 of the book.
     * @return true if the book exists, false otherwise.
     * @throws SQLException If there is an issue accessing the database.
     * @throws InvalidISBNException If the given ISBN is invalid.
     */
    public boolean existsByISBN(String isbn)
            throws SQLException, InvalidISBNException {

        String parsedIsbn;

        try {
            parsedIsbn = ISBNValidator.validateISBN10(isbn);
        } catch (InvalidISBNException notISBN10) {
            parsedIsbn = ISBNValidator.validateISBN13(isbn);
        }

        String query = """
            SELECT 1 FROM books
            WHERE isbn_10 = ? OR isbn_13 = ?
            """;

        try (
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, parsedIsbn);
            stmt.setString(2, parsedIsbn);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
