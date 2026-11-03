package io.github.kraused53.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.kraused53.database.Database;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.exceptions.InvalidISBNException;
import io.github.kraused53.models.Book;

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
     * @param bookId the ID of the requested book.
     * @return the matching book.
     * @throws SQLException if the database operation fails.
     * @throws EntryNotFoundException if no book has the given ID.
     * @throws InvalidISBNException 
     */
    public Book fetchById(int bookId) throws SQLException, EntryNotFoundException {
        String query = """
                SELECT title, isbn_10, isbn_13, page_count, description
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
                    return new Book(
                        rs.getString("title"),
                        rs.getString("isbn_10"),
                        rs.getString("isbn_13"),
                        rs.getInt("page_count"),
                        rs.getString("description")
                    );
                } catch (InvalidISBNException e) {
                    System.out.println("There is a problem with this book!" + e);
                    return null;
                }
            }
        }
    }
}
