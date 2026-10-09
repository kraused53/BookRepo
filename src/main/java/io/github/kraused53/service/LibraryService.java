package io.github.kraused53.service;

import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.models.Author;
import io.github.kraused53.models.Book;
import io.github.kraused53.repository.author.AuthorRepository;
import io.github.kraused53.repository.book.BookRepository;
import io.github.kraused53.repository.book_authors.BookAuthorsRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.util.List;

public class LibraryService {

    // Define logging system
    private static final Logger logger = LoggerFactory.getLogger(LibraryService.class);

    // Define system services
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookAuthorsRepository bookAuthorsRepository;

    public LibraryService(
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            BookAuthorsRepository bookAuthorsRepository
    ) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookAuthorsRepository = bookAuthorsRepository;
    }

    /* These functions simply link to functions inside the nnnService classes */
    /**
     * This function returns a list of all the authors inside the database.
     *
     * @return A list of Author objects. One for each distinct author inside the database
     * @throws SQLException Thrown if there is an error interacting with the database.
     */
    public List<Author> getAllAuthors() throws SQLException {
        logger.info("LibraryService.getAllAuthors: Fetching all books.");
        return authorRepository.findAll();
    }

    /**
     * Insert a new author into the database
     *
     * @param author The author to add to the database
     * @throws SQLException if the database operation fails
     * @see AuthorRepository
     */
    public void addNewAuthor(Author author) throws SQLException, DuplicateEntryException {
        logger.info("LibraryService.addNewAuthor: Adding author '{}' to the database.", author.getName());
        authorRepository.insert(author);
    }

    /**
     * Insert a new author into the database
     *
     * @param authorId The id of the author to delete
     * @throws SQLException if the database operation fails
     * @see AuthorRepository
     */
    public void deleteAuthorById(int authorId) throws SQLException, DuplicateEntryException, EntryNotFoundException {
        logger.info("LibraryService.deleteAuthorById: Deleting author with id: {}", authorId);
        authorRepository.delete(authorId);
    }

    /**
     * Insert a new author into the database
     *
     * @param author The author to delete
     * @throws SQLException if the database operation fails
     * @see AuthorRepository
     */
    public void deleteAuthor(Author author) throws SQLException, DuplicateEntryException, EntryNotFoundException {
        logger.info("LibraryService.deleteAuthor: Deleting author with id: {}", author.getId());
        authorRepository.delete(author.getId());
    }

    /**
     * Update the name of an existing author
     *
     * @param authorId The id of the author to update
     * @param newName The new name to apply to the author with the given ID
     *
     * @see AuthorRepository
     */
    public void updateAuthorName(int authorId, String newName) throws SQLException, DuplicateEntryException, EntryNotFoundException {
        if(authorId <= 0) {
            logger.warn("LibraryService.updateAuthorName: Author ID must be greater than zero!");
            throw new IllegalArgumentException("Author id must be greater than zero!");
        }

        if (newName == null || newName.isBlank()) {
            logger.warn("LibraryService.updateAuthorName: New author name can not be null or blank!");
            throw new IllegalArgumentException("An author's name can not be null or empty");
        }
        authorRepository.update(authorId, newName);
    }

    /**
     * Update the name of an existing author
     *
     * @param author Updated author object
     *
     * @see AuthorRepository
     */
    public void updateAuthorName(Author author) throws SQLException, DuplicateEntryException, EntryNotFoundException {
        if(!author.isValid()) {
            logger.warn("LibraryService.updateAuthorName: Author is invalid!");
            throw new IllegalArgumentException("Author id must be greater than zero!");
        }

        logger.info("LibraryService.updateAuthorName: Changing Author [{}] name to '{}'", author.getId(), author.getName());

        authorRepository.update(author.getId(), author.getName());
    }

    /**
     * Fetch a list of all books with their authors
     *
     * @return A list of book objects
     * @throws SQLException if there is an error accessing the database
     */
    public List<Book> fetchAllBooksWithAuthors() throws SQLException {
        logger.info("LibraryService.fetchAllBooksWithAuthors: Fetching all books.");
        return bookAuthorsRepository.getAllBooks();
    }
}
