package io.github.kraused53.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

import io.github.kraused53.database.Database;
import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.exceptions.InvalidAuthorException;
import io.github.kraused53.exceptions.InvalidBookException;
import io.github.kraused53.exceptions.InvalidISBNException;
import io.github.kraused53.models.Author;
import io.github.kraused53.models.Book;

public class BookAuthorsRepositoryTest {
    private final BookAuthorsRepository bookAuthorsRepository = new BookAuthorsRepository();

    private final AuthorRepository authorRepository = new AuthorRepository();
    private static final AtomicLong AUTHOR_COUNTER = new AtomicLong();

    private final BookRepository bookRepository = new BookRepository();
    private static final AtomicLong ISBN_COUNTER = new AtomicLong();

    @Test
    void createNewRelationSuccessfully() throws SQLException {
        int cleanupAuthorId = -1;
        int cleanupBookId = -1;
        try(
            Connection conn = Database.getConnection()
        ) {
            Author author = newAuthor();
            final int authorId = authorRepository.insert(conn, author);
            cleanupAuthorId = authorId;

            Book book = newBook();
            final int bookId = bookRepository.insert(conn, book);
            cleanupBookId = bookId;

            assertDoesNotThrow(() -> {
                bookAuthorsRepository.createNewRelation(conn, bookId, authorId);
            });

            assertTrue(bookAuthorsRepository.doesRelationExist(conn, bookId, authorId));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup(cleanupBookId, cleanupAuthorId);
        }
    }

    @Test
    void createNewRelationThrowsDuplicateEntryException() throws SQLException {
        int cleanupAuthorId = -1;
        int cleanupBookId = -1;
        try(
            Connection conn = Database.getConnection()
        ) {
            Author author = newAuthor();
            final int authorId = authorRepository.insert(conn, author);
            cleanupAuthorId = authorId;

            Book book = newBook();
            final int bookId = bookRepository.insert(conn, book);
            cleanupBookId = bookId;

            assertDoesNotThrow(() -> {
                bookAuthorsRepository.createNewRelation(conn, bookId, authorId);
            });

            assertThrows(DuplicateEntryException.class, () ->
                bookAuthorsRepository.createNewRelation(conn, bookId, authorId)
            );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup(cleanupBookId, cleanupAuthorId);
        }
    }

    @Test
    void createNewRelationRejectsInvalidBookId()
            throws SQLException {

        int cleanupAuthorId = -1;
        int cleanupBookId = -1;

        try(
            Connection conn = Database.getConnection()
        ) {
            Author author = newAuthor();
            final int authorId = authorRepository.insert(conn, author);
            cleanupAuthorId = authorId;

            assertThrows(IllegalArgumentException.class, () ->
                bookAuthorsRepository.createNewRelation(conn, 0, authorId)
            );

            assertThrows(IllegalArgumentException.class, () ->
                bookAuthorsRepository.createNewRelation(conn, -1, authorId)
            );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup(cleanupBookId, cleanupAuthorId);
        }
    }

    @Test
    void createNewRelationRejectsInvalidAuthorId()
            throws SQLException {

        int cleanupAuthorId = -1;
        int cleanupBookId = -1;

        try(Connection conn = Database.getConnection()) {
            Book book = newBook();
            final int bookId = bookRepository.insert(conn, book);
            cleanupBookId = bookId;

            assertThrows(IllegalArgumentException.class, () ->
                bookAuthorsRepository.createNewRelation(conn, bookId, 0)
            );

            assertThrows(IllegalArgumentException.class, () ->
                bookAuthorsRepository.createNewRelation(conn, bookId, -1)
            );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup(cleanupBookId, cleanupAuthorId);
        }
    }

    @Test
    void fetchAllBooksIdsRelatedToAuthorIdReturnsAllRelatedBooks() {
        
        int cleanupAuthorId = -1;
        int cleanupBookId_1 = -1;
        int cleanupBookId_2 = -1;

        try(
            Connection conn = Database.getConnection()
        ) {
            Author author = newAuthor();
            final int authorId = authorRepository.insert(conn, author);
            cleanupAuthorId = authorId;

            Book book_1 = newBook();
            final int bookId_1 = bookRepository.insert(conn, book_1);
            cleanupBookId_1 = bookId_1;

            Book book_2 = newBook();
            final int bookId_2 = bookRepository.insert(conn, book_2);
            cleanupBookId_2 = bookId_2;

            assertDoesNotThrow(() -> {
                bookAuthorsRepository.createNewRelation(conn, bookId_1, authorId);
            });

            assertDoesNotThrow(() -> {
                bookAuthorsRepository.createNewRelation(conn, bookId_2, authorId);
            });

            List<Integer> results = bookAuthorsRepository.fetchAllBooksIdsRelatedToAuthorId(conn, authorId);

            assertEquals(2, results.size());
            assertTrue(results.contains(bookId_1));
            assertTrue(results.contains(bookId_2));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup(cleanupBookId_1, cleanupAuthorId);
            cleanup(cleanupBookId_2, cleanupAuthorId);
        }
    }

    @Test
    void fetchAllAuthorsIdsRelatedToBookIdReturnsAllRelatedAuthors() {
        
        int cleanupAuthorId_1 = -1;
        int cleanupAuthorId_2 = -1;
        int cleanupBookId = -1;

        try(
            Connection conn = Database.getConnection()
        ) {
            Author author_1 = newAuthor();
            final int authorId_1 = authorRepository.insert(conn, author_1);
            cleanupAuthorId_1 = authorId_1;

            Author author_2 = newAuthor();
            final int authorId_2 = authorRepository.insert(conn, author_2);
            cleanupAuthorId_2 = authorId_2;

            Book book = newBook();
            final int bookId = bookRepository.insert(conn, book);
            cleanupBookId = bookId;

            assertDoesNotThrow(() -> {
                bookAuthorsRepository.createNewRelation(conn, bookId, authorId_1);
            });

            assertDoesNotThrow(() -> {
                bookAuthorsRepository.createNewRelation(conn, bookId, authorId_2);
            });

            List<Integer> results = bookAuthorsRepository.fetchAllAuthorIdsRelatedToBookId(conn, bookId);

            assertEquals(2, results.size());
            assertTrue(results.contains(authorId_1));
            assertTrue(results.contains(authorId_2));
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup(cleanupBookId, cleanupAuthorId_1);
            cleanup(cleanupBookId, cleanupAuthorId_2);
        }
    }

    @Test
    void fetchAllBooksIdsRelatedToAuthorIdReturnsEmptyListWhenNoBooks() {

        int cleanupAuthorId = -1;
        int cleanupBookId = -1;

        try(
            Connection conn = Database.getConnection()
        ) {
            Author author = newAuthor();
            final int authorId = authorRepository.insert(conn, author);
            cleanupAuthorId = authorId;

            List<Integer> results = bookAuthorsRepository.fetchAllBooksIdsRelatedToAuthorId(conn, authorId);

            assertNotNull(results);
            assertTrue(results.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup(cleanupBookId, cleanupAuthorId);
        }
    }

    @Test
    void fetchAllBooksIdsRelatedToAuthorIdRejectsInvalidAuthorId() throws SQLException {
        try(Connection conn = Database.getConnection()) {
            assertThrows(InvalidAuthorException.class, () ->
                bookAuthorsRepository.fetchAllBooksIdsRelatedToAuthorId(conn, 0)
            );

            assertThrows(InvalidAuthorException.class, () ->
                bookAuthorsRepository.fetchAllBooksIdsRelatedToAuthorId(conn, -1)
            );
        }
    }

    @Test
    void fetchAllAuthorsIdsRelatedToBookIdReturnsEmptyListWhenNoAuthors() {

        int cleanupAuthorId = -1;
        int cleanupBookId = -1;

        try(Connection conn = Database.getConnection()) {
            Book book = newBook();
            final int bookId = bookRepository.insert(conn, book);
            cleanupBookId = bookId;

            List<Integer> results = bookAuthorsRepository.fetchAllAuthorIdsRelatedToBookId(conn, bookId);

            assertNotNull(results);
            assertTrue(results.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup(cleanupBookId, cleanupAuthorId);
        }
    }

    @Test
    void fetchAllBooksIdsRelatedToAuthorIdRejectsInvalidBookId() throws SQLException {
        try(Connection conn = Database.getConnection()) {
            assertThrows(InvalidBookException.class, () ->
                bookAuthorsRepository.fetchAllAuthorIdsRelatedToBookId(conn, 0)
            );

            assertThrows(InvalidBookException.class, () ->
                bookAuthorsRepository.fetchAllAuthorIdsRelatedToBookId(conn, -1)
            );
        }
    }

    @Test
    void deleteRelationSuccessfully() {

        int cleanupAuthorId = -1;
        int cleanupBookId = -1;
        try(
            Connection conn = Database.getConnection()
        ) {
            Author author = newAuthor();
            final int authorId = authorRepository.insert(conn, author);
            cleanupAuthorId = authorId;

            Book book = newBook();
            final int bookId = bookRepository.insert(conn, book);
            cleanupBookId = bookId;

            assertDoesNotThrow(() -> {
                bookAuthorsRepository.createNewRelation(conn, bookId, authorId);
            });

            assertDoesNotThrow(() -> {
                bookAuthorsRepository.deleteRelation(conn, bookId, authorId);
            });

            assertFalse(bookAuthorsRepository.doesRelationExist(conn, bookId, authorId));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup(cleanupBookId, cleanupAuthorId);
        }
    }

    @Test
    void deleteRelationThrowsEntryNotFoundException() {

        int cleanupAuthorId = -1;
        int cleanupBookId = -1;
        try(
            Connection conn = Database.getConnection()
        ) {
            Author author = newAuthor();
            final int authorId = authorRepository.insert(conn, author);
            cleanupAuthorId = authorId;

            Book book = newBook();
            final int bookId = bookRepository.insert(conn, book);
            cleanupBookId = bookId;

            assertThrows(EntryNotFoundException.class, () ->
                bookAuthorsRepository.deleteRelation(conn, bookId, authorId)
            );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup(cleanupBookId, cleanupAuthorId);
        }
    }

    @Test
    void deleteRelationRejectsInvalidBookId() throws SQLException {
        try(Connection conn = Database.getConnection()) {
            assertThrows(IllegalArgumentException.class, () ->
                bookAuthorsRepository.deleteRelation(conn, 0, 1)
            );

            assertThrows(IllegalArgumentException.class, () ->
                bookAuthorsRepository.deleteRelation(conn, -1, 1)
            );
        }
    }

    @Test
    void deleteRelationRejectsInvalidAuthorId() throws SQLException {
        try(Connection conn = Database.getConnection()) {
            assertThrows(IllegalArgumentException.class, () ->
                bookAuthorsRepository.deleteRelation(conn, 1, 0)
            );

            assertThrows(IllegalArgumentException.class, () ->
                bookAuthorsRepository.deleteRelation(conn, 1, -1)
            );
        }
    }

    @Test
    void doesRelationExistReturnsFalseForMissingRelation() {

        int cleanupAuthorId = -1;
        int cleanupBookId = -1;
        try(
            Connection conn = Database.getConnection()
        ) {
            Author author = newAuthor();
            final int authorId = authorRepository.insert(conn, author);
            cleanupAuthorId = authorId;

            Book book = newBook();
            final int bookId = bookRepository.insert(conn, book);
            cleanupBookId = bookId;

            assertFalse(bookAuthorsRepository.doesRelationExist(conn, bookId, authorId));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup(cleanupBookId, cleanupAuthorId);
        }

    }

    @Test
    void doesRelationExistRejectsInvalidBookId() throws SQLException {
        try(Connection conn = Database.getConnection()) {
            assertThrows(IllegalArgumentException.class, () ->
                bookAuthorsRepository.doesRelationExist(conn, 0, 1)
            );

            assertThrows(IllegalArgumentException.class, () ->
                bookAuthorsRepository.doesRelationExist(conn, -1, 1)
            );
        }
    }

    @Test
    void doesRelationExistRejectsInvalidAuthorId() throws SQLException {
        try(Connection conn = Database.getConnection()) {
            assertThrows(IllegalArgumentException.class, () ->
                bookAuthorsRepository.doesRelationExist(conn, 1, 0)
            );

            assertThrows(IllegalArgumentException.class, () ->
                bookAuthorsRepository.doesRelationExist(conn, 1, -1)
            );
        }
    }

    // ------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------

    private record TestAuthor(Author author, int id) {}

    private TestAuthor insertTestAuthor()
            throws SQLException, DuplicateEntryException, InvalidAuthorException {

        try(
            Connection conn = Database.getConnection()
        ) {
            Author author = newAuthor();

            return new TestAuthor(author, authorRepository.insert(conn, author));
        }
    }

    private Author newAuthor() {
        return new Author(uniqueAuthorName());
    }

    private String uniqueAuthorName() {
        return "Repository Test Author " + System.currentTimeMillis()
                + "-" + AUTHOR_COUNTER.incrementAndGet();
    }

    private record TestBook(Book book, int id) {}

    private TestBook insertTestBook()
            throws SQLException, DuplicateEntryException, InvalidBookException,
                   InvalidISBNException {
        try(Connection conn = Database.getConnection()) {
            Book book = newBook();
            return new TestBook(book, bookRepository.insert(conn, book));
        }
    }

    private Book newBook() throws InvalidISBNException {
        String isbn10 = uniqueISBN10();
        String isbn13 = isbn13FromISBN10(isbn10);

        return new Book(
            "Repository Test Book " + ISBN_COUNTER.get(),
            isbn10,
            isbn13,
            123,
            "Temporary test description."
        );
    }

    /*
     * Produces a valid ISBN-10 from a 9-digit payload and its checksum.
     * Timestamp plus a counter makes collisions unlikely across test runs.
     */
    private String uniqueISBN10() {
        long value = (System.currentTimeMillis() + ISBN_COUNTER.incrementAndGet())
                % 1_000_000_000L;
        String payload = String.format("%09d", value);

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(payload.charAt(i)) * (10 - i);
        }

        int check = (11 - sum % 11) % 11;
        return payload + (check == 10 ? "X" : Integer.toString(check));
    }

    private String isbn13FromISBN10(String isbn10) {
        String payload = "978" + isbn10.substring(0, 9);
        int sum = 0;

        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(payload.charAt(i));
            sum += digit * (i % 2 == 0 ? 1 : 3);
        }

        return payload + ((10 - sum % 10) % 10);
    }



    private void cleanup(int bookId, int authorId) {
        // Cleanup author
        if (authorId != -1) {
            try(Connection conn = Database.getConnection()) {
                if (authorRepository.existsById(conn, authorId)) {
                    authorRepository.deleteById(conn, authorId);
                }
            } catch (Exception e) {
                System.out.println("Could not remove author with id: "+authorId);
                e.printStackTrace();
            }
        }

        // Cleanup book
        if (bookId != -1) {
            try(Connection conn = Database.getConnection()) {
                if (bookRepository.existsById(conn, bookId)) {
                    bookRepository.deleteById(conn, bookId);
                }
            } catch (Exception e) {
                System.out.println("Could not remove book with id: "+bookId);
                e.printStackTrace();
            }
        }

        // Database handles book_authors deletion through cascade
    }
}
