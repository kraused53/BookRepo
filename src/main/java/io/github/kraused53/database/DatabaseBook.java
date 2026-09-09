package io.github.kraused53.database;

import io.github.kraused53.data_models.author.Author;
import io.github.kraused53.data_models.book.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The DatabaseBook serves as an interface between the application's book representation and the
 *     BookRepo database. This class handles the SQL quires needed to perform database actions
 *     involving books.
 *
 * @author Daniel Krause
 * @version 1.0
 * @since 9/7/2026
 * @see Database
 */
public class DatabaseBook {

    /**
     * Default constructor
     *
     * @throws SQLException Throws SQL exception if there is an error connecting to the database
     */
    public DatabaseBook() throws SQLException {}

    /**
     * This method searches the book table for a book matching the given ID.
     *
     * @param id The ID of the book to be searched for. {@code int}
     * @return Returns a Book object. If the book is not found, return null. {@code Book || null}
     * @throws SQLException Throws this exception if there is an error connecting to the database or executing a query.
     */
    public Book findByID(int id) throws SQLException {
        // Define an empty Book object
        Book book = new Book();

        // Load book information. Use try-with-resources for automatic destruction of the database connection
        try(Connection conn = Database.getConnection()) {

            // Query used to retrieve book from the database
            PreparedStatement book_info = conn.prepareStatement("SELECT * FROM books WHERE id = ?");
            book_info.setInt(1, id);

            // Execute the query and store the results in a set
            ResultSet info_set = book_info.executeQuery();

            // Check if a book was found
            if(info_set.next()) {
                // If it was, fill in the information in the Book object
                book.setId(info_set.getInt("id"));
                book.setTitle(info_set.getString("title"));
                book.setIsbn(info_set.getString("isbn"));
                book.setDescription(info_set.getString("description"));
            }else {
                // If no book is found, return null
                return null;
            }

            // SQL query to fetch all authors associated with this book
            PreparedStatement auth_info = conn.prepareStatement(
                    "SELECT a.id, a.name FROM authors a JOIN book_authors ba ON a.id = ba.author_id WHERE ba.book_id = ?"
            );
            auth_info.setInt(1, id);

            // Execute the query and store the results in a set
            ResultSet auth_set = auth_info.executeQuery();

            // For each found author
            while(auth_set.next()) {
                // Create a new author object
                Author author = new Author();

                // Fill i the Author's information
                author.setId(auth_set.getInt("id"));
                author.setName(auth_set.getString("name"));

                // Add it to the book's author list
                book.addAuthor(author);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error while finding book by ID...");
        }

        // Return the book
        return book;
    }

    /**
     * This method returns a list of all Books stored in the BookRepo database.
     *
     * @return Returns a list of book objects. {@code List<Book>}
     * @throws SQLException Throws this exception if there is an error connecting to the database or executing a query.
     */
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

    /**
     * This method returns an author object for the author with the given ID.
     *
     * @param id ID of the author to find {@code int}
     *
     * @return Returns an author object. {@code List<Book>}
     * @throws SQLException Throws this exception if there is an error connecting to the database or executing a query.
     */
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

    /**
     * This method searches the database of a book by its ISBN (NOT WRITTEN).
     *
     * @param isbn ISBN of book to search of {@code String}
     *
     * @return Returns a book object. {@code Book}
     * @throws SQLException Throws this exception if there is an error connecting to the database or executing a query.
     */
    public Book findByISBN(String isbn) throws SQLException {
        return null;
    }

    /**
     * This method searches the database of a book by its title (NOT WRITTEN).
     *
     * @param title Search parameter for database {@code String}
     *
     * @return Returns a book object. {@code Book}
     * @throws SQLException Throws this exception if there is an error connecting to the database or executing a query.
     */
    public List<Book> searchByTitle(String title) throws SQLException {
        // Use MySQL LIKE command to find list of book candidates. ( ... WHERE book.title LIKE %title%; )
        return null;
    }

    /**
     * This method saves a new book to the database (NOT WRITTEN).
     *
     * @param book The book to be saved to the database. {@code Book}
     *
     * @throws SQLException Throws SQL exception if there is an error connecting to the database
     */
    public void save(Book book) throws SQLException {
        /*
        * Steps to add a book to the database
        *     1) Is a book with this isbn already in the books table?
        *        Y) Exit
        *        N) Continue
         *
         *    2) For each of the book's author(s): are they in the database already?
         *       Y) Store their id
         *       N) Add them to the author table and copy their ID
         *
         *    3) Add book's information to the book table
         *
         *    4) For each Author: add a row to the book_authors table
        */

        if(isbn_exists(book.getIsbn())) {
            System.out.println("This book is already in the database!");
            return;
        }

        // Add book and author(s) to database
        try ( Connection conn = Database.getConnection() ) {
            // Generate query to store book info in book database
            PreparedStatement save_book = conn.prepareStatement("INSERT INTO books (title, isbn, description) VALUES (?, ?, ?)");
            save_book.setString(1, book.getTitle());
            save_book.setString(2, book.getIsbn());
            save_book.setString(3, book.getDescription());

            // Execute query
            save_book.executeUpdate();

            // Get book ID from ISBN
            int book_id = get_book_id_from_isbn(book.getIsbn());

            for (Author author : book.getAuthors()) {
                if(!author_exists(author.getName())) {
                    // Add author to database
                    PreparedStatement add_author = conn.prepareStatement("INSERT INTO authors (name) VALUES (?)");
                    add_author.setString(1, author.getName());

                    add_author.executeUpdate();
                }

                // Get author ID
                int author_id = get_author_id_from_name(author.getName());

                // Query to add book_author link
                PreparedStatement add_book_author = conn.prepareStatement("INSERT INTO book_authors (book_id, author_id) VALUES (?, ?)");
                add_book_author.setInt(1, book_id);
                add_book_author.setInt(2, author_id);

                add_book_author.executeUpdate();
            }
        }

    }

    /**
     * This method deletes a book from the database (NOT WRITTEN).
     *
     * @param id The book to be saved to the database. {@code int}
     *
     * @throws SQLException Throws SQL exception if there is an error connecting to the database
     */
    public void delete(int id) throws SQLException {
        /*
         * Steps to remove a book from the database
         *     1) Is a book with this ID in the books table?
         *        Y) Continue
         *        N) Exit
         *
         *    2) Use book ID to make list of associated authors
         *
         *    3) Remove entry from book table with matching ID
         *
         *    4) Remove all entries in book_authors table with matching book_id
         *
         *    5) Use list of authors to count how many times each one appears in the book_authors table now.
         *        0) Remove the author's entry in the author table
         *       1+) Continue
         */
    }

    /* Utility */

    /**
     * This method queries the database to see if a specific ISBN exists in the books table.
     *
     * @param isbn The ISBN to search the database for. {@code String}
     *
     * @throws SQLException Throws SQL exception if there is an error connecting to the database
     * @return Return true if the given ISBN is already in the database, else false {@code boolean}
     */
    boolean isbn_exists(String isbn) throws SQLException {
        try(Connection conn = Database.getConnection() ){
            // Prepare ISBN search query
            PreparedStatement query = conn.prepareStatement("SELECT * FROM books WHERE isbn = ?");
            query.setString(1, isbn);

            // Execute query and store result
            ResultSet isbn_set = query.executeQuery();

            // Will be true if the query returns a book
            return isbn_set.next();
        }catch(SQLException e){
            e.printStackTrace();
            throw new SQLException("Error in isbn_exists");
        }
    }

    /**
     * This method queries the database to see if a specific author exists in the authors table.
     *
     * @param author_name The name of the author to search the database for. {@code String}
     *
     * @throws SQLException Throws SQL exception if there is an error connecting to the database
     * @return Return true if the given author is already in the database, else false {@code boolean}
     */
    boolean author_exists(String author_name) throws SQLException {
        try(Connection conn = Database.getConnection() ){
            // Prepare ISBN search query
            PreparedStatement query = conn.prepareStatement("SELECT * FROM authors WHERE name = ?");
            query.setString(1, author_name);

            // Execute query and store result
            ResultSet isbn_set = query.executeQuery();

            // Will be true if the query returns a book
            return isbn_set.next();
        }catch(SQLException e){
            e.printStackTrace();
            throw new SQLException("Error in author_exists");
        }
    }

    /**
     * This method queries the database to get the ID of a book by its isbn.
     *
     * @param isbn The ISBN of the book to search the database for. {@code String}
     *
     * @throws SQLException Throws SQL exception if there is an error connecting to the database
     * @return Return ID or -1 if not found {@code int}
     */
    int get_book_id_from_isbn(String isbn) throws SQLException {
        int id = -1;

        try(Connection conn = Database.getConnection() ){
            // Prepare ISBN search query
            PreparedStatement query = conn.prepareStatement("SELECT id FROM books WHERE isbn = ?");
            query.setString(1, isbn);

            // Execute query and store result
            ResultSet isbn_set = query.executeQuery();

            if(isbn_set.next()) {
                id = isbn_set.getInt("id");
            }

        }catch(SQLException e){
            e.printStackTrace();
            throw new SQLException("Error in get_book_id_from_isbn");
        }

        return id;
    }

    /**
     * This method queries the database to get the ID of a book by its isbn.
     *
     * @param name The ISBN of the book to search the database for. {@code String}
     *
     * @throws SQLException Throws SQL exception if there is an error connecting to the database
     * @return Return ID or -1 if not found {@code int}
     */
    int get_author_id_from_name(String name) throws SQLException {
        int id = -1;

        try(Connection conn = Database.getConnection() ){
            // Prepare ISBN search query
            PreparedStatement query = conn.prepareStatement("SELECT id FROM authors WHERE name = ?");
            query.setString(1, name);

            // Execute query and store result
            ResultSet author_set = query.executeQuery();

            if(author_set.next()) {
                id = author_set.getInt("id");
            }

        }catch(SQLException e){
            e.printStackTrace();
            throw new SQLException("Error in get_author_id_from_name");
        }

        return id;
    }
}
