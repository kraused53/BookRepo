package io.github.kraused53.service;

import io.github.kraused53.database.Database;
import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.exceptions.FailedToMakeLinkException;
import io.github.kraused53.models.Author;
import io.github.kraused53.models.Book;
import io.github.kraused53.repository.author.AuthorRepository;
import io.github.kraused53.repository.book.BookRepository;
import io.github.kraused53.repository.book_authors.BookAuthorsRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
    public int addNewAuthor(Author author) throws SQLException, DuplicateEntryException {
        logger.info("LibraryService.addNewAuthor: Adding author '{}' to the database.", author.getName());
        return authorRepository.insert(author);
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
     * Fetch an author by searching for their name.
     *
     * @param name The name of the author you are looking for. Full match only
     * @return ab author object for the author with the given name
     * @throws SQLException If there is an error connecting to the database.
     * @throws EntryNotFoundException If the given name does not correspond to an author in the database.
     */
    public Author findAuthorByName(String name) throws SQLException, EntryNotFoundException {
        return authorRepository.lookupByName(name);
    }

    /**
     * Fetch a list of all books with their authors
     *
     * @return A list of book objects
     * @throws SQLException if there is an error accessing the database
     */
    public List<Book> getAllBooks() throws SQLException {
        logger.info("LibraryService.fetchAllBooksWithAuthors: Fetching all books.");
        return bookAuthorsRepository.getAllBooks();
    }

    public void addBook(Book book)
            throws SQLException, IllegalArgumentException, DuplicateEntryException {

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. Check whether the book already exists.
                if (bookRepository.exists(conn, book)) {
                    logger.warn(
                            "LibraryService.addBook: Book '{}' already exists.",
                            book.getTitle()
                    );

                    conn.rollback();
                    throw new DuplicateEntryException("This book already exists in the database!");
                }

                List<Author> authors = book.getAuthors();

                // 2. Resolve existing authors or insert missing ones.
                for (Author author : authors) {
                    try {
                        Author existing = authorRepository.lookupByName(
                                conn, author.getName()
                        );

                        author.setId(existing.getId());

                    } catch (EntryNotFoundException e) {
                        try {
                            author.setId(
                                    authorRepository.insert(conn, author)
                            );
                        } catch (DuplicateEntryException ex) {
                            throw new IllegalStateException(
                                    "Author lookup and insertion disagreed "
                                            + "for '" + author.getName() + "'.",
                                    ex
                            );
                        }
                    }
                }

                // 3. Insert the book and retrieve its generated ID.
                try {
                    book.setId(bookRepository.insert(conn, book));
                } catch (DuplicateEntryException e) {
                    throw new IllegalStateException(
                            "Book insertion reported a duplicate after "
                                    + "the existence check.",
                            e
                    );
                }

                // 4. Insert all book-author relationships.
                for (Author author : authors) {
                    try {
                        bookAuthorsRepository.makeLink(conn, book, author);
                    } catch (FailedToMakeLinkException e) {
                        throw new IllegalStateException(
                                "Failed to link book '" + book.getTitle()
                                        + "' to author '" + author.getName() + "'.",
                                e
                        );
                    }
                }

                // 5. Commit only after every operation succeeds.
                conn.commit();

                logger.info(
                        "LibraryService.addBook: Successfully added book '{}' "
                                + "with ID {}.",
                        book.getTitle(),
                        book.getId()
                );

            } catch (SQLException | RuntimeException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }

                throw e;
            }
        }
    }
}
