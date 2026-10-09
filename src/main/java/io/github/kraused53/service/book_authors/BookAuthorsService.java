package io.github.kraused53.service.book_authors;

import io.github.kraused53.models.Book;
import io.github.kraused53.repository.book.BookRepository;
import io.github.kraused53.repository.book_authors.BookAuthorsRepository;

import java.sql.SQLException;
import java.util.List;

/**
 * This class is a middle step between the user interface and the database access. All of the Book/Author related actions
 *      will be enumerated here, and each will use the bookRepository object to access the database.
 */
public class BookAuthorsService {

    /**
     * This BookAuthorsRepository object will be how the BookAuthorsService class interacts with the database
     */
    private final BookAuthorsRepository bookAuthorRepository;

    /**
     * The book author service class will be instantiated elsewhere and then connected here
     */
    public BookAuthorsService(BookAuthorsRepository bookAuthorRepository) {
        this.bookAuthorRepository = bookAuthorRepository;
    }

    /**
     * This function will fetch a list of all books and their authors
     *
     * @return A list of Book objects
     * @throws SQLException if there is an error with database access
     */
    public List<Book> getAllBooks() throws SQLException {
        return bookAuthorRepository.getAllBooks();
    }
}
