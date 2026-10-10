package io.github.kraused53.repository.book;

import io.github.kraused53.database.Database;
import io.github.kraused53.exceptions.DuplicateEntryException;
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
}
