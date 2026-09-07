package io.github.kraused53.Database;

import io.github.kraused53.DataModels.Author.Author;
import io.github.kraused53.DataModels.Book.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseBook {
    public Book findByID(int id) throws SQLException {
        Book book = new Book();

        // Load book information
        try(Connection conn = Database.getConnection()) {

            // Base information
            PreparedStatement book_info = conn.prepareStatement("SELECT * FROM books WHERE id = ?");
            book_info.setInt(1, id);

            ResultSet info_set = book_info.executeQuery();

            if(info_set.next()) {
                book.setId(info_set.getInt("id"));
                book.setTitle(info_set.getString("title"));
                book.setIsbn(info_set.getString("isbn"));
                book.setDescription(info_set.getString("description"));
            }else {
                return book;
            }

            // Authors
            PreparedStatement auth_info = conn.prepareStatement(
                    "SELECT a.id, a.name FROM authors a JOIN book_authors ba ON a.id = ba.author_id WHERE ba.book_id = ?"
            );
            auth_info.setInt(1, id);

            ResultSet auth_set = auth_info.executeQuery();

            while(auth_set.next()) {
                Author author = new Author();
                author.setId(auth_set.getInt("id"));
                author.setName(auth_set.getString("name"));

                book.addAuthor(author);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error while finding book by ID...");
        }

        return book;
    }

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books =  new ArrayList<>();

        try ( Connection conn = Database.getConnection() ) {

            // Query to get all books from database
            PreparedStatement all_books = conn.prepareStatement("SELECT * FROM books");

            ResultSet all_books_set = all_books.executeQuery();

            // Cycle through each book
            while(all_books_set.next()) {
                // Generate new book
                Book book = new Book();

                // Fill in base info
                book.setId(all_books_set.getInt("id"));
                book.setTitle(all_books_set.getString("title"));
                book.setIsbn(all_books_set.getString("isbn"));
                book.setDescription(all_books_set.getString("description"));

                // Query to find all authors associated with this book
                PreparedStatement author_ids = conn.prepareStatement("SELECT author_id FROM book_authors WHERE book_id = ?");
                author_ids.setInt(1, book.getId());

                // Store the results of this query in a set
                ResultSet authors_set = author_ids.executeQuery();

                // Cycle through the authors for this book
                while(authors_set.next()) {
                    // Define a new author
                    Author author = getAuthorByID(authors_set.getInt("author_id"));

                    // Add author to book
                    book.addAuthor(author);
                }

                // Add book to list
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error generating list of all books...");
        }

        return books;
    }

    public Author getAuthorByID(int id) throws SQLException {
        Author author = new Author();

        try(Connection conn = Database.getConnection() ) {
            PreparedStatement author_info = conn.prepareStatement("SELECT * FROM authors WHERE id = ?");
            author_info.setInt(1, id);

            ResultSet authors_set = author_info.executeQuery();
            while(authors_set.next()) {
                author.setId(authors_set.getInt("id"));
                author.setName(authors_set.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error getting author by ID...");
        }

        return author;
    }

    public Book findByISBN(String isbn) {
        return null;
    }

    public List<Book> searchByTitle(String title) {
        return null;
    }

    public void save(Book book) {

    }

    public void delete(int id) {

    }
}
