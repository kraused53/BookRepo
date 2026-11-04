package io.github.kraused53.service_api;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
     *      2] Fetch the authors currently associated with this book
     *      3] Fetch the authors associated with the updated book
     *      4] Create a list of authors now associated with the updated book
     *      5] Create a list of authors no longer associated with the updated book
     *      6] Update each feature of the book in the database
     *      7] Create a link for each author in step 4
     *      8] Delete the links for each author in step 5
     * 
     * @param updatedBook The book with updated features
     * @throws ClientMadeBookException The given book was made by the client, not taken from the database
     * @throws InvalidAuthorException Encountered an invalid author
     * @throws SQLException If there is an error connecting to the database
     * @throws InvalidBookException The given book is not valid
     * @throws EntryNotFoundException Either the book or it's authors are no longer in the database
     * @throws InvalidISBNException Encountered an invalid ISBN
     * @throws DuplicateEntryException There was an error finding authors for the book. The function attempted to add an author twice
     */
    public void updateBook(Book updatedBook) throws ClientMadeBookException, InvalidAuthorException, SQLException, InvalidBookException, EntryNotFoundException, InvalidISBNException, DuplicateEntryException {
        try(Connection conn = Database.getConnection()) {
            // Can only UPDATE a book if it is not an entirely new book
            if(updatedBook.getId() < 1 || !bookRepository.existsById(conn, updatedBook.getId())) {
                throw new ClientMadeBookException("This book is not from the database");
            }

            // Current version of the book
            Book currentBook = fetchBookById(updatedBook.getId());

            // Get all authors currently associated with this book
            Set<Integer> currentAuthors = new HashSet<>(
                bookAuthorsRepository.fetchAllAuthorIdsRelatedToBookId(conn, updatedBook.getId())
            );

            // Get all authors in updated book
            Set<Integer> updatedAuthors = updatedBook.getAuthors()
                .stream()
                .map(Author::getId)
                .collect(Collectors.toSet());

            // Authors in Book Object not in current list
            Set<Integer> linksToCreate = new HashSet<>(updatedAuthors);
            linksToCreate.removeAll(currentAuthors);

            // Authors in current list that are not in Book Object
            Set<Integer> linksToDelete = new HashSet<>(currentAuthors);
            linksToDelete.removeAll(updatedAuthors);

            // Update the book title
            if(!updatedBook.getTitle().equals(currentBook.getTitle())) {
                bookRepository.updateTitle(
                    conn, 
                    updatedBook.getId(),
                    updatedBook.getTitle()
                );
            }

            // Update the book ISBN10
            if(!updatedBook.getIsbn10().equals(currentBook.getIsbn10())) {
                bookRepository.updateIsbn10(
                    conn, 
                    updatedBook.getId(),
                    updatedBook.getIsbn10()
                );
            }

            // Update the book ISBN13
            if(!updatedBook.getIsbn13().equals(currentBook.getIsbn13())) {
                bookRepository.updateIsbn13(
                    conn, 
                    updatedBook.getId(),
                    updatedBook.getIsbn13()
                );
            }

            // Update page count
            if(updatedBook.getPageCount() != currentBook.getPageCount()) {
                bookRepository.updatePageCount(
                    conn, 
                    updatedBook.getId(),
                    updatedBook.getPageCount()
                );
            }

            // Update description
            if(!updatedBook.getDescription().equals(currentBook.getDescription())) {
                bookRepository.updateDescription(
                    conn, 
                    updatedBook.getId(),
                    updatedBook.getDescription()
                );
            }

            // If there is an author in the new book that is not in the database, add it


            // Make new links
            for(int id : linksToCreate) {

                bookAuthorsRepository.createNewRelation(
                    conn,
                    updatedBook.getId(),
                    id
                );
            }

            // Delete broken links
            for(int id : linksToDelete) {
                bookAuthorsRepository.deleteRelation(
                    conn,
                    updatedBook.getId(),
                    id
                );
            }
        }
    }

    /* Add new book and authors */


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
