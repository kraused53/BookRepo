package io.github.kraused53.repository.book_authors;

import io.github.kraused53.database.Database;
import io.github.kraused53.exceptions.FailedToMakeLinkException;
import io.github.kraused53.models.Author;
import io.github.kraused53.models.Book;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookAuthorsRepositoryTest {

    private BookAuthorsRepository repository;

    private int bookId;
    private int authorId;
    private int secondAuthorId;

    @BeforeEach
    void setUp() throws SQLException {
        repository = new BookAuthorsRepository();

        bookId = 0;
        authorId = 0;
        secondAuthorId = 0;

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement stmt = conn.prepareStatement("""
                        INSERT INTO books
                            (title, isbn_13, isbn_10, page_count, description)
                        VALUES (?, ?, ?, ?, ?)
                        """, Statement.RETURN_GENERATED_KEYS)) {

                    stmt.setString(1, "Repository Test Book");
                    stmt.setString(2, "9780306406157");
                    stmt.setString(3, "0306406152");
                    stmt.setInt(4, 200);
                    stmt.setString(5, "A test description.");

                    stmt.executeUpdate();

                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        assertTrue(keys.next());
                        bookId = keys.getInt(1);
                    }
                }

                authorId = insertAuthor(conn, "Repository Test Author");
                secondAuthorId = insertAuthor(conn, "Repository Test Author Two");

                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Delete relationships before deleting their parent records.
                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM book_authors WHERE book_id = ?")) {
                    stmt.setInt(1, bookId);
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM authors WHERE id IN (?, ?)")) {
                    stmt.setInt(1, authorId);
                    stmt.setInt(2, secondAuthorId);
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM books WHERE id = ?")) {
                    stmt.setInt(1, bookId);
                    stmt.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private int insertAuthor(Connection conn, String name)
            throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO authors (name) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                assertTrue(keys.next());
                return keys.getInt(1);
            }
        }
    }

    private void createLink(int bookId, int authorId) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     INSERT INTO book_authors (book_id, author_id)
                     VALUES (?, ?)
                     """)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, authorId);
            stmt.executeUpdate();
        }
    }

    @Test
    void getAllBooksReturnsBooksWithTheirAuthors() throws SQLException {
        createLink(bookId, authorId);

        List<Book> books = repository.getAllBooks();

        Book result = books.stream()
                .filter(book -> book.getId() == bookId)
                .findFirst()
                .orElseThrow();

        assertEquals("Repository Test Book", result.getTitle());
        assertEquals(1, result.getAuthors().size());
        assertEquals("Repository Test Author",
                result.getAuthors().get(0).getName());
    }

    @Test
    void getAllBooksReturnsOneBookWithMultipleAuthors()
            throws SQLException {
        createLink(bookId, authorId);
        createLink(bookId, secondAuthorId);

        List<Book> books = repository.getAllBooks();

        List<Book> matchingBooks = books.stream()
                .filter(book -> book.getId() == bookId)
                .toList();

        assertEquals(1, matchingBooks.size());

        Book result = matchingBooks.get(0);

        assertEquals(2, result.getAuthors().size());
        assertTrue(result.getAuthors().stream()
                .anyMatch(a -> a.getName().equals("Repository Test Author")));
        assertTrue(result.getAuthors().stream()
                .anyMatch(a -> a.getName().equals("Repository Test Author Two")));
    }

    @Test
    void getAllBooksReturnsEmptyListWhenNoRelationshipsExist()
            throws SQLException {
        List<Book> books = repository.getAllBooks();

        assertNotNull(books);
        assertTrue(books.stream().noneMatch(b -> b.getId() == bookId));
    }

    @Test
    void makeLinkCreatesBookAuthorRelationship() throws Exception {
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Repository Test Book");

        Author author = new Author();
        author.setId(authorId);
        author.setName("Repository Test Author");

        repository.makeLink(book, author);

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     SELECT 1 FROM book_authors
                     WHERE book_id = ? AND author_id = ?
                     """)) {

            stmt.setInt(1, bookId);
            stmt.setInt(2, authorId);

            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next());
            }
        }
    }

    @Test
    void makeLinkThrowsSQLExceptionForDuplicateRelationship()
            throws Exception {
        createLink(bookId, authorId);

        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Repository Test Book");

        Author author = new Author();
        author.setId(authorId);
        author.setName("Repository Test Author");

        assertThrows(SQLException.class,
                () -> repository.makeLink(book, author));
    }

    @Test
    void makeLinkWithSharedConnectionCreatesRelationship()
            throws Exception {
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Repository Test Book");

        Author author = new Author();
        author.setId(authorId);
        author.setName("Repository Test Author");

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            repository.makeLink(conn, book, author);
            conn.commit();
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     SELECT 1 FROM book_authors
                     WHERE book_id = ? AND author_id = ?
                     """)) {

            stmt.setInt(1, bookId);
            stmt.setInt(2, authorId);

            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next());
            }
        }
    }
}