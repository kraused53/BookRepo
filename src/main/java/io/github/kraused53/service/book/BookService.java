package io.github.kraused53.service.book;

import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.models.Book;
import io.github.kraused53.repository.book.BookRepository;

import java.sql.SQLException;
import java.util.List;

/**
 * This class is a middle step between the user interface and the database access. All of the Book related actions
 *      will be enumerated here, and each will use the bookRepository object to access the database.
 */
public class BookService {

    /**
     * This BookRepository object will be how the BookService class interacts with the database
     */
    private final BookRepository bookRepository;

    /**
     * The book service class will be instantiated elsewhere and then connected here
     */
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Fetch all the books in the database and return them as a list of Book objects.
     *
     * @return A list of all books present in the database.
     * @throws SQLException if the database operation fails
     *
     * @see BookRepository
     */
    public List<Book> getAllBooks() throws SQLException {
        return bookRepository.findAll();
    }

    /**
     * Fetch a specific book in the database by its ID and return a Book object.
     *
     * @param id Requested ID. Use this to search the database
     *
     * @return The book with the corresponding ID.
     * @throws SQLException if the database operation fails
     * @throws IllegalArgumentException if the given id is invalid
     *
     * @see BookRepository
     */
    public Book findById(int id) throws SQLException, EntryNotFoundException {
        // Input sanitation
        if(id <= 0) {
            throw new IllegalArgumentException("Book id must be greater than zero!");
        }

        return bookRepository.findById(id);
    }

    /**
     * Insert a new book into the database
     *
     * @param book The book to add to the database
     * @throws SQLException if the database operation fails
     * @see BookRepository
     */
    public void addBook(Book book) throws SQLException, DuplicateEntryException {
        // If the book is valid, no need to continue
        if(!book.isValid()) {
            throw new IllegalArgumentException("This book name is not valid!");
        }

        bookRepository.insert(book);

    }

    /**
     * Update the name of an existing book
     *
     * @param bookId The id of the book to update
     * @param book The book with updated data
     *
     * @see BookRepository
     */
    public void updateBook(int bookId, Book book) throws SQLException, DuplicateEntryException, EntryNotFoundException {
        if(bookId <= 0) {
            throw new IllegalArgumentException("Book id must be greater than zero!");
        }

        if (book == null || !book.isValid()) {
            throw new IllegalArgumentException("This book is not valid");
        }
        bookRepository.update(bookId, book);
    }

    public void deleteBook(int bookId) throws SQLException, EntryNotFoundException {
        if(bookId <= 0) {
            throw new IllegalArgumentException("Book id must be greater than zero!");
        }

        bookRepository.delete(bookId);
    }
}
