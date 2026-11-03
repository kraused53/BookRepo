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

    /*** CRUD - Create Section ***/

    /**
     * 
     * @param book The book object to add to the database.
     * @return The id if the book after it is added to the database. {@code int}
     * @throws SQLException If there is an issue connecting to the database
     * @throws DuplicateEntryException If the book is already in the database
     */
    public int insert(Book book) throws SQLException, DuplicateEntryException {
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

    /*** CRUD - Read Section ***/

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
            ResultSet rs = stmt.executeQuery()
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

                    // Add the book to the
                    books.add(book);
                } catch (Exception e) {
                    System.out.println("There is a problem with this book!");
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
        String query = """
                SELECT id, title, isbn_10, isbn_13, page_count, description
                FROM books
                WHERE id = ?
                """;
        
        try(
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
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
            PreparedStatement stmt = conn.prepareStatement(query)
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


    /*** CRUD - Update Section ***/


    /*** CRUD - Destroy Section ***/
}
