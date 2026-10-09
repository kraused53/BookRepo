package io.github.kraused53.service.library;

import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.models.Author;
import io.github.kraused53.models.Book;
import io.github.kraused53.repository.author.AuthorRepository;
import io.github.kraused53.repository.book.BookRepository;
import io.github.kraused53.repository.book_authors.BookAuthorsRepository;
import io.github.kraused53.service.author.AuthorService;
import io.github.kraused53.service.book.BookService;
import io.github.kraused53.service.book_authors.BookAuthorsService;

import java.sql.SQLException;
import java.util.List;

public class LibraryService {

    // Define system services
    private final BookService bookService;
    private final AuthorService authorService;
    private final BookAuthorsService bookAuthorsService;

    public LibraryService(
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            BookAuthorsRepository bookAuthorsRepository
    ) {
        this.bookService = new BookService(bookRepository);
        this.authorService = new AuthorService(authorRepository);
        this.bookAuthorsService = new BookAuthorsService(bookAuthorsRepository);
    }

    /* These functions simply link to functions inside the nnnService classes */
    /**
     * This function returns a list of all the authors inside the database.
     *
     * @return A list of Author objects. One for each distinct author inside the database
     * @throws SQLException Thrown if there is an error interacting with the database.
     */
    public List<Author> getAllAuthors() throws SQLException {
        return authorService.getAllAuthors();
    }

    /**
     * Insert a new author into the database
     *
     * @param author The author to add to the database
     * @throws SQLException if the database operation fails
     * @see AuthorRepository
     */
    public void addNewAuthor(Author author) throws SQLException, DuplicateEntryException {
        authorService.addAuthor(author);
    }

    /**
     * Insert a new author into the database
     *
     * @param authorId The id of the author to delete
     * @throws SQLException if the database operation fails
     * @see AuthorRepository
     */
    public void deleteAuthorById(int authorId) throws SQLException, DuplicateEntryException, EntryNotFoundException {
        authorService.deleteAuthor(authorId);
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
            throw new IllegalArgumentException("Author id must be greater than zero!");
        }

        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("An author's name can not be null or empty");
        }
        authorService.updateAuthor(authorId, newName);
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
            throw new IllegalArgumentException("Author id must be greater than zero!");
        }
        authorService.updateAuthor(author.getId(), author.getName());
    }

    /**
     * Fetch a list of all books with their authors
     *
     * @return A list of book objects
     * @throws SQLException if there is an error accessing the database
     */
    public List<Book> fetchAllBooksWithAuthors() throws SQLException {
        return bookAuthorsService.getAllBooks();
    }
}
