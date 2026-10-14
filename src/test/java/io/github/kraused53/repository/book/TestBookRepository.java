package io.github.kraused53.repository.book;

import io.github.kraused53.database.Database;
import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.models.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestBookRepository {

    // Use this for testing
    private final BookRepository repository = new BookRepository();

    // Database testing credentials
    String url = System.getenv("TEST_DB_URL");
    String username = System.getenv("TEST_DB_USERNAME");
    String password = System.getenv("TEST_DB_PASSWORD");

    // Keep track of records created by each test.
    private final List<Integer> createdBookIds = new ArrayList<>();

    // Generate distinct, valid ISBNs for test records.
    private static final AtomicLong isbnSequence =
            new AtomicLong(System.currentTimeMillis() % 1_000_000_000L);

    @BeforeEach
    void setUp() {
        createdBookIds.clear();
    }

    /**
     * Delete test records directly through JDBC so cleanup does not depend
     * on the repository methods being tested.
     */
    @AfterEach
    void tearDown() throws Exception {
        try (Connection conn = Database.getConnection(
                    url,
                    username,
                    password
            );
             PreparedStatement stmt =
                     conn.prepareStatement("DELETE FROM books WHERE id = ?")) {

            for (int id : createdBookIds) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        }
    }

    private Book createBook() {
        long sequence = Math.floorMod(
                isbnSequence.incrementAndGet(),
                1_000_000_000L
        );

        String digits = String.format("%09d", sequence);

        Book book = new Book();
        book.setTitle("Repository Test Book " + sequence);
        book.setIsbn10(generateISBN10(digits));
        book.setIsbn13(generateISBN13("978" + digits));
        book.setPageCount(321);
        book.setDescription("A book created for repository integration tests.");

        return book;
    }

    private int insertTestBook(Book book) throws Exception {
        int id = repository.insert(book);
        createdBookIds.add(id);
        return id;
    }

    private Book copyBook(Book original) {
        Book copy = new Book();
        copy.setTitle(original.getTitle());
        copy.setIsbn10(original.getIsbn10());
        copy.setIsbn13(original.getIsbn13());
        copy.setPageCount(original.getPageCount());
        copy.setDescription(original.getDescription());
        return copy;
    }

    private String generateISBN10(String firstNineDigits) {
        int sum = 0;

        for (int i = 0; i < 9; i++) {
            sum += (firstNineDigits.charAt(i) - '0') * (10 - i);
        }

        int checkDigit = (11 - sum % 11) % 11;
        char check = checkDigit == 10
                ? 'X'
                : (char) ('0' + checkDigit);

        return firstNineDigits + check;
    }

    private String generateISBN13(String firstTwelveDigits) {
        int sum = 0;

        for (int i = 0; i < 12; i++) {
            int digit = firstTwelveDigits.charAt(i) - '0';
            sum += digit * (i % 2 == 0 ? 1 : 3);
        }

        int checkDigit = (10 - sum % 10) % 10;

        return firstTwelveDigits + checkDigit;
    }

// ---------------------------------------------------------------------
// Database setup
// ---------------------------------------------------------------------

    @Test
    void testBooksTableExists() throws Exception {
        try (Connection conn = Database.getConnection(
                    url,
                    username,
                    password
            );
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT 1 FROM books LIMIT 1"
             )) {

            assertNotNull(rs);
        }
    }

// ---------------------------------------------------------------------
// INSERT
// ---------------------------------------------------------------------

    @Test
    void testInsertBookReturnsGeneratedId() throws Exception {
        Book book = createBook();

        int id = insertTestBook(book);

        assertTrue(id > 0);
    }

    @Test
    void testInsertBookPersistsAllFields() throws Exception {
        Book book = createBook();

        int id = insertTestBook(book);
        Book saved = repository.findById(id);

        assertEquals(id, saved.getId());
        assertEquals(book.getTitle(), saved.getTitle());
        assertEquals(book.getIsbn10(), saved.getIsbn10());
        assertEquals(book.getIsbn13(), saved.getIsbn13());
        assertEquals(book.getPageCount(), saved.getPageCount());
        assertEquals(book.getDescription(), saved.getDescription());
    }

    @Test
    void testInsertDuplicateBookThrowsException() throws Exception {
        Book book = createBook();

        insertTestBook(book);

        Book duplicate = copyBook(book);
        duplicate.setTitle(book.getTitle() + " Duplicate");

        assertThrows(
                DuplicateEntryException.class,
                () -> repository.insert(duplicate)
        );
    }

    @Test
    void testInsertUsingExistingConnection() throws Exception {
        Book book = createBook();
        int id;

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            id = repository.insert(conn, book);
            createdBookIds.add(id);

            assertTrue(id > 0);
            assertTrue(repository.exists(conn, book));

            // The caller owns the connection and transaction.
            conn.commit();
        }

        Book saved = repository.findById(id);

        assertEquals(book.getTitle(), saved.getTitle());
        assertEquals(book.getIsbn13(), saved.getIsbn13());
    }

// ---------------------------------------------------------------------
// FIND
// ---------------------------------------------------------------------

    @Test
    void testFindAllContainsInsertedBook() throws Exception {
        Book book = createBook();
        int id = insertTestBook(book);

        List<Book> books = repository.findAll();

        assertNotNull(books);
        assertTrue(
                books.stream().anyMatch(found -> found.getId() == id),
                "findAll() should contain the inserted book"
        );
    }

    @Test
    void testFindByIdReturnsCorrectBook() throws Exception {
        Book book = createBook();
        int id = insertTestBook(book);

        Book found = repository.findById(id);

        assertNotNull(found);
        assertEquals(id, found.getId());
        assertEquals(book.getTitle(), found.getTitle());
        assertEquals(book.getIsbn10(), found.getIsbn10());
        assertEquals(book.getIsbn13(), found.getIsbn13());
        assertEquals(book.getPageCount(), found.getPageCount());
        assertEquals(book.getDescription(), found.getDescription());
    }

    @Test
    void testFindByIdThrowsWhenBookDoesNotExist() {
        assertThrows(
                EntryNotFoundException.class,
                () -> repository.findById(-1)
        );
    }

// ---------------------------------------------------------------------
// EXISTS
// ---------------------------------------------------------------------

    @Test
    void testExistsReturnsTrueForExistingBook() throws Exception {
        Book book = createBook();
        insertTestBook(book);

        assertTrue(repository.exists(book));
    }

    @Test
    void testExistsReturnsFalseForMissingBook() throws Exception {
        Book book = createBook();

        assertFalse(repository.exists(book));
    }

    @Test
    void testExistsMatchesISBN13Independently() throws Exception {
        Book saved = createBook();
        insertTestBook(saved);

        Book lookup = createBook();
        lookup.setIsbn13(saved.getIsbn13());

        assertTrue(repository.exists(lookup));
    }

    @Test
    void testExistsMatchesISBN10Independently() throws Exception {
        Book saved = createBook();
        insertTestBook(saved);

        Book lookup = createBook();
        lookup.setIsbn10(saved.getIsbn10());

        assertTrue(repository.exists(lookup));
    }

    @Test
    void testExistsRejectsNullBook() {
        assertThrows(
                IllegalArgumentException.class,
                () -> repository.exists((Book) null)
        );
    }

    @Test
    void testExistsUsingExistingConnection() throws Exception {
        Book book = createBook();

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            assertFalse(repository.exists(conn, book));

            int id = repository.insert(conn, book);
            createdBookIds.add(id);

            assertTrue(repository.exists(conn, book));

            conn.commit();
        }
    }

// ---------------------------------------------------------------------
// UPDATE
// ---------------------------------------------------------------------

    @Test
    void testUpdateChangesBookFields() throws Exception {
        Book original = createBook();
        int id = insertTestBook(original);

        Book updated = createBook();
        updated.setId(id);
        updated.setTitle("Updated Repository Test Book");
        updated.setPageCount(500);
        updated.setDescription("Updated description.");

        repository.update(updated);

        Book saved = repository.findById(id);

        assertEquals("Updated Repository Test Book", saved.getTitle());
        assertEquals(updated.getIsbn10(), saved.getIsbn10());
        assertEquals(updated.getIsbn13(), saved.getIsbn13());
        assertEquals(500, saved.getPageCount());
        assertEquals("Updated description.", saved.getDescription());
    }

    @Test
    void testUpdateUnchangedBookDoesNotThrow() throws Exception {
        Book book = createBook();
        int id = insertTestBook(book);
        book.setId(id);

        assertDoesNotThrow(() -> repository.update(book));

        Book saved = repository.findById(id);
        assertEquals(book.getTitle(), saved.getTitle());
    }

    @Test
    void testUpdateWithDuplicateISBNThrowsException() throws Exception {
        Book first = createBook();
        int firstId = insertTestBook(first);

        Book second = createBook();
        int secondId = insertTestBook(second);

        Book conflictingUpdate = copyBook(second);
        conflictingUpdate.setIsbn13(first.getIsbn13());
        conflictingUpdate.setId(secondId);

        assertThrows(
                DuplicateEntryException.class,
                () -> repository.update(conflictingUpdate)
        );

        // The failed update must not overwrite the existing ISBN.
        Book unchanged = repository.findById(conflictingUpdate.getId());
        assertEquals(second.getIsbn13(), unchanged.getIsbn13());
    }

// ---------------------------------------------------------------------
// DELETE
// ---------------------------------------------------------------------

    @Test
    void testDeleteRemovesBook() throws Exception {
        Book book = createBook();
        int id = insertTestBook(book);

        repository.delete(id);

        // The record is already gone; cleanup can safely attempt deletion
        // again without affecting the test.
        assertThrows(
                EntryNotFoundException.class,
                () -> repository.findById(id)
        );
    }

    @Test
    void testDeleteMissingBookThrowsException() {
        assertThrows(
                EntryNotFoundException.class,
                () -> repository.delete(-1)
        );
    }
}
