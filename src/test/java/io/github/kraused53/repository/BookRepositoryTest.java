package io.github.kraused53.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import io.github.kraused53.exceptions.InvalidBookException;
import io.github.kraused53.exceptions.InvalidISBNException;
import io.github.kraused53.models.Book;

public class BookRepositoryTest {
    private final BookRepository repository = new BookRepository();
    private static final AtomicLong ISBN_COUNTER = new AtomicLong();
    
    // ------------------------------------------------------------
    // insert()
    // ------------------------------------------------------------

    @Test
    void insertReturnsGeneratedIdAndPersistsBook() throws Exception {
        Book book = newBook();
        int id = -1;

        try(Connection conn = Database.getConnection()) {
            id = repository.insert(conn, book);
            assertTrue(id > 0);
            Book loaded = repository.fetchById(conn, id);

            assertEquals(id, loaded.getId());
            assertEquals(book.getTitle(), loaded.getTitle());
            assertEquals(book.getIsbn10(), loaded.getIsbn10());
            assertEquals(book.getIsbn13(), loaded.getIsbn13());
            assertEquals(book.getPageCount(), loaded.getPageCount());
            assertEquals(book.getDescription(), loaded.getDescription());
        } finally {
            cleanup(id);
        }
    }

    @Test
    void insertRejectsDuplicateISBN() throws Exception {
        TestBook inserted = insertTestBook();

        try(Connection conn = Database.getConnection()) {
            Book duplicate = new Book(
                inserted.book().getTitle(),
                inserted.book().getIsbn10(),
                inserted.book().getIsbn13(),
                inserted.book().getPageCount(),
                inserted.book().getDescription()
            );

            assertThrows(DuplicateEntryException.class,
                () -> repository.insert(conn, duplicate));
        } finally {
            cleanup(inserted.id());
        }
    }

    // ------------------------------------------------------------
    // fetchAll()
    // ------------------------------------------------------------

    @Test
    void fetchAllIncludesInsertedBookAndItsId() throws Exception {
        TestBook inserted = insertTestBook();

        try(Connection conn = Database.getConnection()) {
            List<Book> books = repository.fetchAll(conn);

            Book found = books.stream()
                .filter(b -> b.getIsbn13().equals(inserted.book().getIsbn13()))
                .findFirst()
                .orElseThrow();

            assertEquals(inserted.id(), found.getId());
            assertEquals(inserted.book().getTitle(), found.getTitle());
        } finally {
            cleanup(inserted.id());
        }
    }

    // ------------------------------------------------------------
    // fetchById()
    // ------------------------------------------------------------

    @Test
    void fetchByIdReturnsMatchingBook() throws Exception {
        TestBook inserted = insertTestBook();

        try(Connection conn = Database.getConnection()) {
            Book result = repository.fetchById(conn, inserted.id());

            assertEquals(inserted.id(), result.getId());
            assertEquals(inserted.book().getTitle(), result.getTitle());
            assertEquals(inserted.book().getIsbn10(), result.getIsbn10());
            assertEquals(inserted.book().getIsbn13(), result.getIsbn13());
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void fetchByIdThrowsWhenBookDoesNotExist() throws SQLException {
        try(Connection conn = Database.getConnection()) {
            assertThrows(EntryNotFoundException.class,
                () -> repository.fetchById(conn, Integer.MAX_VALUE));
        }
    }

    @Test
    void fetchByIdRejectsNonpositiveId() throws SQLException {
        try(Connection conn = Database.getConnection()) {
            assertThrows(IllegalArgumentException.class,
                () -> repository.fetchById(conn, 0));
            assertThrows(IllegalArgumentException.class,
                () -> repository.fetchById(conn, -1));
        }
    }

    // ------------------------------------------------------------
    // deleteById()
    // ------------------------------------------------------------

    @Test
    void deleteByIdRemovesBook() throws Exception {
        TestBook inserted = insertTestBook();

        try(Connection conn = Database.getConnection()) {
            repository.deleteById(conn, inserted.id());

            assertFalse(repository.existsById(conn, inserted.id()));
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void deleteByIdRejectsNonpositiveId() throws SQLException {
        try(Connection conn = Database.getConnection()) {
            assertThrows(IllegalArgumentException.class,
                () -> repository.deleteById(conn, 0));
            assertThrows(IllegalArgumentException.class,
                () -> repository.deleteById(conn, -1));
        }
    }

    @Test
    void deleteByIdThrowsForMissingBook() throws SQLException {
        try(Connection conn = Database.getConnection()) {
            assertThrows(EntryNotFoundException.class,
                () -> repository.deleteById(conn, Integer.MAX_VALUE));
        }
    }

    // ------------------------------------------------------------
    // deleteByIsbn()
    // ------------------------------------------------------------

    @Test
    void deleteByIsbnRemovesBookUsingISBN10() throws Exception {
        TestBook inserted = insertTestBook();

        try(Connection conn = Database.getConnection()) {
            repository.deleteByIsbn(conn, inserted.book().getIsbn10());

            assertFalse(repository.existsById(conn, inserted.id()));
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void deleteByIsbnRemovesBookUsingISBN13() throws Exception {
        TestBook inserted = insertTestBook();

        try(Connection conn = Database.getConnection()) {
            repository.deleteByIsbn(conn, inserted.book().getIsbn13());

            assertFalse(repository.existsById(conn, inserted.id()));
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void deleteByIsbnRejectsInvalidISBN() throws SQLException {
        try(Connection conn = Database.getConnection()) {
            assertThrows(InvalidISBNException.class,
                () -> repository.deleteByIsbn(conn, "invalid"));
        }
    }

    @Test
    void deleteByIsbnThrowsWhenBookDoesNotExist() throws Exception {
        try(Connection conn = Database.getConnection()) {
            Book book = newBook();

            assertThrows(EntryNotFoundException.class,
                () -> repository.deleteByIsbn(conn, book.getIsbn13()));
        }
    }

    // ------------------------------------------------------------
    // existsById()
    // ------------------------------------------------------------

    @Test
    void existsByIdReturnsTrueForExistingBook() throws Exception {
        TestBook inserted = insertTestBook();

        try(Connection conn = Database.getConnection()) {
            assertTrue(repository.existsById(conn, inserted.id()));
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void existsByIdReturnsFalseForMissingBook() throws SQLException {
        try(Connection conn = Database.getConnection()) {
            assertFalse(repository.existsById(conn, Integer.MAX_VALUE));
        }
    }

    @Test
    void existsByIdRejectsNonpositiveId() throws SQLException {
        try(Connection conn = Database.getConnection()) {
            assertThrows(IllegalArgumentException.class,
                () -> repository.existsById(conn, 0));
            assertThrows(IllegalArgumentException.class,
                () -> repository.existsById(conn, -1));
        }
    }

    // ------------------------------------------------------------
    // existsByISBN()
    // ------------------------------------------------------------

    @Test
    void existsByISBNReturnsTrueForISBN10AndISBN13() throws Exception {
        TestBook inserted = insertTestBook();

        try(Connection conn = Database.getConnection()) {
            assertTrue(repository.existsByISBN(conn, inserted.book().getIsbn10()));
            assertTrue(repository.existsByISBN(conn, inserted.book().getIsbn13()));
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void existsByISBNReturnsFalseForValidISBNNotInDatabase() throws Exception {
        try(Connection conn = Database.getConnection()) {
            Book book = newBook();

            assertFalse(repository.existsByISBN(conn, book.getIsbn10()));
            assertFalse(repository.existsByISBN(conn, book.getIsbn13()));
        }
    }

    @Test
    void existsByISBNRejectsInvalidISBN() throws SQLException {
        try(Connection conn = Database.getConnection()) {
            assertThrows(InvalidISBNException.class,
                () -> repository.existsByISBN(conn, "invalid"));
        }
    }

    /*** Utility ***/
    
    private record TestBook(Book book, int id) {}

    private TestBook insertTestBook()
            throws SQLException, DuplicateEntryException, InvalidBookException,
                   InvalidISBNException {
        try(Connection conn = Database.getConnection()) {
            Book book = newBook();
            return new TestBook(book, repository.insert(conn, book));
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

    private void cleanup(int id) throws SQLException, EntryNotFoundException {
        try(Connection conn = Database.getConnection()) {
            if (repository.existsById(conn, id)) {
                repository.deleteById(conn, id);
            }
        }
    }
}
