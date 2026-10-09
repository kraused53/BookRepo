package io.github.kraused53.repository.book_authors;

import io.github.kraused53.database.Database;
import io.github.kraused53.models.Author;
import io.github.kraused53.models.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * This class handles database access for books and authors at the same time.
 * All SQL operations related to book/author sets are performed here.
 */
public class BookAuthorsRepository {

    /**
     * Nothing needs to be initialized when this class is instantiated.
     */
    public BookAuthorsRepository() {}

    /**
     * Returns a list of all books with their respective authors.
     *
     *
     */
    public List<Book> getAllBooks() throws SQLException {
        Map<Integer, Book> books = new LinkedHashMap<>();

        String query = "SELECT b.id AS book_id, b.title AS title, b.isbn_13 AS isbn_13, b.isbn_10 AS isbn_10, b.page_count AS page_count, b.description AS description, a.id AS author_id, a.name AS name FROM jbook.books b INNER JOIN jbook.book_authors ba ON ba.book_id = b.id INNER JOIN jbook.authors a ON a.id = ba.author_id ORDER BY b.id";

        try (Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {

            while(rs.next()) {
                // Get the current book id
                int bookId = rs.getInt("book_id");

                // Try to fetch the book from the map
                Book book = books.get(bookId);

                // Null if this book ID is not in the map yet
                if(book == null) {
                    // Make a new book
                    book = mapBook(rs);

                    books.put(bookId, book);
                }
                Author author = mapAuthor(rs);
                book.addAuthor(author);
            }
        }

        return new ArrayList<>(books.values());
    }

    private Book mapBook(ResultSet rs) throws SQLException {
        // Make a new book
        Book book = new Book();

        // Fetch the rest of the book information
        int bookId = rs.getInt("book_id");
        String title = rs.getString("title");
        String isbn13 = rs.getString("isbn_13");
        String isbn10 = rs.getString("isbn_10");
        int pageCount = rs.getInt("page_count");
        String description = rs.getString("description");

        // Fill in the information
        book.setId(bookId);
        book.setTitle(title);
        book.setIsbn13(isbn13);
        book.setIsbn10(isbn10);
        book.setPageCount(pageCount);
        book.setDescription(description);

        // Return the book
        return book;
    }

    private Author mapAuthor(ResultSet rs) throws SQLException {
        // Make a new book
        Author author = new Author();

        // Fetch the rest of the book information
        int id = rs.getInt("author_id");
        String name = rs.getString("name");

        // Fill in the information
        author.setId(id);
        author.setName(name);

        // Return the book
        return author;
    }
}
