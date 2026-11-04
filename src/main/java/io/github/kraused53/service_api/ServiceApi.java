package io.github.kraused53.service_api;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.crypto.Data;

import io.github.kraused53.database.Database;
import io.github.kraused53.exceptions.ClientMadeBookException;
import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.exceptions.InvalidAuthorException;
import io.github.kraused53.exceptions.InvalidBookException;
import io.github.kraused53.exceptions.InvalidISBNException;
import io.github.kraused53.models.Author;
import io.github.kraused53.models.Book;
import io.github.kraused53.repository.AuthorRepository;
import io.github.kraused53.repository.BookAuthorsRepository;
import io.github.kraused53.repository.BookRepository;

/**
 * This class serves as an API between the main program and the repository layer. This layer 
 * ensures a seperation of concerns between the application layer and the repository layer. 
 * The application will be able to make calls to this API without having to have any knowledge 
 * of or involvement intdatabase operations
 */
public class ServiceApi {

    /**
     * Allow ServiceApi to manipulate book data
     */
    private final BookRepository bookRepository = new BookRepository();

    /**
     * Allow ServiceApi to manipulate author data
     */
    private final AuthorRepository authorRepository = new AuthorRepository();

    /**
     * Allow ServiceApi to manipulate book-author relations
     */
    private final BookAuthorsRepository bookAuthorsRepository = new BookAuthorsRepository();


    /**
     * Nothing needs to be initialized when this class is instantiated.
     */
    public ServiceApi() {}

    /* Fetch Specific Books */

    /**
     * Search the database for a book with a specific id.
     * 
     * @param bookId Id of the book to search for
     * @return The matching book object
     * @throws SQLException If there is an error accessing the database
     * @throws EntryNotFoundException If no book has that Id
     * @throws InvalidBookException If the book with that ID is malformed
     * @throws InvalidAuthorException If the call encounters a malformed author
     */
    public Book fetchBookById(int bookId) throws SQLException, EntryNotFoundException, InvalidBookException, InvalidAuthorException {
        try(Connection conn = Database.getConnection()) {
            Book book = bookRepository.fetchById(conn, bookId);

            book = fillInAuthorForBook(book);

            return book;
        }
    }

    /**
     * Search the database for a book with a specific ISBN.
     * 
     * @param isbn ISBN for the book to search for
     * @return The matching book object
     * @throws SQLException If there is an error accessing the database
     * @throws InvalidISBNException If the given ISBN is malformed
     * @throws EntryNotFoundException If no book has that Id
     * @throws InvalidBookException If the book with that ID is malformed
     * @throws InvalidAuthorException If the call encounters a malformed author
     */
    public Book fetchBookByISBN(String isbn) throws SQLException, InvalidISBNException, EntryNotFoundException, InvalidBookException, InvalidAuthorException {
        try(Connection conn = Database.getConnection()) {
            Book book = bookRepository.fetchByISBN(conn, isbn);
            
            book = fillInAuthorForBook(book);

            return book;
        }
    }

    /* Fetch all books */

    /**
     * Fetch a list of every book in the database. Has the ability to fill in
     * the authors associated with each book.
     * 
     * @param fillAuthors Flag to turn on / off filling book authors
     * @return A list of every book in the database
     * @throws SQLException If there is an error connecting to the database
     * @throws InvalidAuthorException If the function encounters a malformed Author Object
     * @throws InvalidBookException If the function encounters a malformed Book Object
     * @throws EntryNotFoundException If a Book Object is related to an author that does not exist in the database
     */
    public List<Book> fetchAllBooks(boolean fillAuthors) throws SQLException, InvalidAuthorException, InvalidBookException, EntryNotFoundException{
        try(Connection conn = Database.getConnection()) {
            List<Book> books = bookRepository.fetchAll(conn);

            if(fillAuthors) {
                for(Book b : books) {
                    b = fillInAuthorForBook(b);
                }
            }

            return books;
        }
    }

    /* Fetch specific author */
    
    /**
     * Fetches a specific author by their id
     * 
     * @param authorId The id of the author to search for
     * @return The matching Author Object
     * @throws SQLException If there is an error connecting to the database
     * @throws InvalidAuthorException If the function encounters a malformed Author Object
     * @throws EntryNotFoundException If the author does not exist
     */
    public Author fetchAuthorById(int authorId) throws SQLException, InvalidAuthorException, EntryNotFoundException {
        try (
            Connection conn = Database.getConnection();
        ) {
            return authorRepository.fetchById(conn, authorId);
        }
    }
    
    /**
     * Fetches a specific author by their name
     * 
     * @param name The name of the author to search for
     * @return The matching Author Object
     * @throws SQLException If there is an error connecting to the database
     * @throws InvalidAuthorException If the function encounters a malformed Author Object
     * @throws EntryNotFoundException If the author does not exist
     */
    public Author fetchAuthorByName(String name) throws SQLException, InvalidAuthorException, EntryNotFoundException {
        try(Connection conn = Database.getConnection()){
            return authorRepository.fetchByName(conn, name);
        }
    }

    /* Fetch all authors */

    /**
     * Fetch a list of all of the authors in the database.
     * 
     * @return A list of every author in the database
     * @throws SQLException If there is an error connecting to the database
     */
    public List<Author> fetchAllAuthors() throws SQLException {
        try(Connection conn = Database.getConnection()) {
            return authorRepository.fetchAll(conn);
        }
    }

    /* Fetch authors for book */

    /**
     * Takes a book id and returns a list of the authors associated with that book.
     * 
     * @param bookId The id of the book of interest
     * @return A list of the authors related with the given book
     * @throws SQLException If there is an error connecting to the database
     * @throws InvalidAuthorException If the function encounters a malformed Author Object
     * @throws InvalidBookException If the function encounters a malformed Book Object
     * @throws EntryNotFoundException If the book does not exist or there is an author assosciated with the book that does not exist
     */
    public List<Author> fetchAuthorsForBook(int bookId) throws InvalidAuthorException, SQLException, InvalidBookException, EntryNotFoundException {
        try(Connection conn = Database.getConnection()) {
            List<Integer> authorIds = bookAuthorsRepository.fetchAllAuthorIdsRelatedToBookId(conn, bookId);

            List<Author> authors = new ArrayList<>();

            for(int id : authorIds) {
                authors.add(authorRepository.fetchById(conn, id));
            }

            return authors;
        }
    }

    /**
     * Takes an author id and returns a list of the books associated with that author.
     * 
     * @param authorId The id of the author of interest
     * @return A list of the books related with the given author
     * @throws SQLException If there is an error connecting to the database
     * @throws InvalidAuthorException If the function encounters a malformed Author Object
     * @throws InvalidBookException If the function encounters a malformed Book Object
     * @throws EntryNotFoundException If the author does not exist or there is a book assosciated with the author that does not exist
     */
    public List<Book> fetchBooksForAuthor(int authorId) throws InvalidAuthorException, SQLException, InvalidBookException, EntryNotFoundException {
        try(Connection conn = Database.getConnection()) {
            List<Integer> bookIds = bookAuthorsRepository.fetchAllBooksIdsRelatedToAuthorId(conn, authorId);

            List<Book> books = new ArrayList<>();

            for(int id : bookIds) {
                books.add(fillInAuthorForBook(bookRepository.fetchById(conn, id)));
            }

            return books;
        }
    }

    /* Update Functions */

    /**
     * This function takes a book object and updates the database based on its new features.
     * Steps:
     *      1] Make sure the book was originally fetched from the database.
     *      2] Fetch the authors currently associated with this book.
     *      3] Fetch the authors associated with the updated book.
     *      4] Create a list of authors now associated with the updated book.
     *      5] Create a list of authors no longer associated with the updated book.
     *      6] Update each feature of the book in the database.
     *      7] Create a link for each author in step 4.
     *      8] Delete the links for each author in step 5.
     *
     * @param updatedBook The book with updated features
     * @throws ClientMadeBookException The given book was made by the client, not taken from the database
     * @throws InvalidAuthorException Encountered an invalid author
     * @throws SQLException If there is an error connecting to the database
     * @throws InvalidBookException The given book is not valid
     * @throws EntryNotFoundException Either the book or its authors are no longer in the database
     * @throws InvalidISBNException Encountered an invalid ISBN
     * @throws DuplicateEntryException There was an error finding authors for the book. The function attempted to add an author twice
     */
    public void updateBook(Book updatedBook)
            throws ClientMadeBookException,
                InvalidAuthorException,
                SQLException,
                InvalidBookException,
                EntryNotFoundException,
                InvalidISBNException,
                DuplicateEntryException {

        // Validate the provided book
        if (updatedBook == null) {
            throw new InvalidBookException("The given book is null!");
        }

        try (Connection conn = Database.getConnection()) {

            // Pause updating the database until all actions resolve
            conn.setAutoCommit(false);

            try {
                int bookId = updatedBook.getId();

                // Can only UPDATE a book that already exists in the database
                if (bookId < 1 || !bookRepository.existsById(conn, bookId)) {
                    throw new ClientMadeBookException(
                        "This book is not from the database"
                    );
                }

                // Fetch current version using the transaction's connection
                Book currentBook = bookRepository.fetchById(conn, bookId);

                // Get all authors currently associated with this book
                Set<Integer> currentAuthors = new HashSet<>(
                    bookAuthorsRepository.fetchAllAuthorIdsRelatedToBookId(
                        conn,
                        bookId
                    )
                );

                // Get all authors associated with the updated book
                Set<Integer> updatedAuthors = updatedBook.getAuthors()
                    .stream()
                    .map(Author::getId)
                    .collect(Collectors.toSet());

                // Validate that every author in the updated book exists
                for (int authorId : updatedAuthors) {
                    if (!authorRepository.existsById(conn, authorId)) {
                        throw new EntryNotFoundException(
                            "No author was found with id: " + authorId
                        );
                    }
                }

                // Authors in updated book but not in current list
                Set<Integer> linksToCreate = new HashSet<>(updatedAuthors);
                linksToCreate.removeAll(currentAuthors);

                // Authors in current list but not in updated book
                Set<Integer> linksToDelete = new HashSet<>(currentAuthors);
                linksToDelete.removeAll(updatedAuthors);

                // Update title
                if (!Objects.equals(
                        updatedBook.getTitle(),
                        currentBook.getTitle())) {

                    bookRepository.updateTitle(
                        conn,
                        bookId,
                        updatedBook.getTitle()
                    );
                }

                // Update ISBN10
                if (!Objects.equals(
                        updatedBook.getIsbn10(),
                        currentBook.getIsbn10())) {

                    bookRepository.updateIsbn10(
                        conn,
                        bookId,
                        updatedBook.getIsbn10()
                    );
                }

                // Update ISBN13
                if (!Objects.equals(
                        updatedBook.getIsbn13(),
                        currentBook.getIsbn13())) {

                    bookRepository.updateIsbn13(
                        conn,
                        bookId,
                        updatedBook.getIsbn13()
                    );
                }

                // Update page count
                if (updatedBook.getPageCount() != currentBook.getPageCount()) {
                    bookRepository.updatePageCount(
                        conn,
                        bookId,
                        updatedBook.getPageCount()
                    );
                }

                // Update description
                if (!Objects.equals(
                        updatedBook.getDescription(),
                        currentBook.getDescription())) {

                    bookRepository.updateDescription(
                        conn,
                        bookId,
                        updatedBook.getDescription()
                    );
                }

                // Create new author-book relationships
                for (int authorId : linksToCreate) {
                    bookAuthorsRepository.createNewRelation(
                        conn,
                        bookId,
                        authorId
                    );
                }

                // Delete obsolete author-book relationships
                for (int authorId : linksToDelete) {
                    bookAuthorsRepository.deleteRelation(
                        conn,
                        bookId,
                        authorId
                    );
                }

                // Commit all changes together
                conn.commit();

            } catch (
                SQLException |
                ClientMadeBookException |
                InvalidAuthorException |
                InvalidBookException |
                EntryNotFoundException |
                InvalidISBNException |
                DuplicateEntryException |
                RuntimeException e
            ) {
                conn.rollback();
                throw e;
            }
        }
    }

    /* Add new book with authors */

    public void insertBook(Book book)
        throws SQLException,
               InvalidAuthorException,
               InvalidISBNException,
               DuplicateEntryException,
               InvalidBookException {

        if (book == null) {
            throw new InvalidBookException("This book is null");
        }

        if (book.getAuthors() == null) {
            throw new InvalidBookException(
                "The book's author list is null!"
            );
        }

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Check ISBN uniqueness
                if (book.getIsbn13() != null &&
                        bookRepository.existsByISBN(conn, book.getIsbn13())) {
                    throw new DuplicateEntryException(
                        "A book with this ISBN-13 already exists"
                    );
                }

                if (book.getIsbn10() != null &&
                        bookRepository.existsByISBN(conn, book.getIsbn10())) {
                    throw new DuplicateEntryException(
                        "A book with this ISBN-10 already exists"
                    );
                }

                // Insert book and retrieve its generated ID
                int bookId = bookRepository.insert(conn, book);

                // Insert authors and collect their generated IDs
                Set<Integer> authorIds = new HashSet<>();

                for (Author author : book.getAuthors()) {
                    if(author == null) {
                        throw new InvalidAuthorException("The book contains a null author");
                    }

                    int authorId;

                    if(authorRepository.existsByName(conn, author.getName())) {
                        authorId = author.getId();
                    }else {
                        authorId = authorRepository.insert(conn, author);
                        author.setId(authorId);
                    }

                    authorIds.add(authorId);
                }

                // Create book-author relationships
                for (int authorId : authorIds) {
                    bookAuthorsRepository.createNewRelation(
                        conn,
                        bookId,
                        authorId
                    );
                }

                // Commit all database changes
                conn.commit();

                // Assign the generated ID after successful commit
                book.setId(bookId);

            } catch (
                SQLException |
                InvalidAuthorException |
                InvalidISBNException |
                InvalidBookException |
                DuplicateEntryException |
                RuntimeException e
            ) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }

                throw e;
            }
        }
    }

    /* Delete book */

    public void deleteBook(int bookId) throws SQLException, EntryNotFoundException {
        try(Connection conn = Database.getConnection()) {
            bookRepository.deleteById(conn, bookId);            
        }
    }


    /* Utility */
    private Book fillInAuthorForBook(Book book) throws InvalidAuthorException, SQLException, InvalidBookException, EntryNotFoundException {
        try(Connection conn = Database.getConnection()) {
            List<Integer> authorIds = bookAuthorsRepository.fetchAllAuthorIdsRelatedToBookId(conn, book.getId());

            for(int id : authorIds) {
                book.addAuthor(authorRepository.fetchById(conn, id));
            }

            return book;
        }
    }
}
