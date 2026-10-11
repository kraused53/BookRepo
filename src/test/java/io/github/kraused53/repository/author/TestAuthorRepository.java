package io.github.kraused53.repository.author;

import io.github.kraused53.database.Database;
import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.models.Author;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthorRepositoryTest {

    private AuthorRepository authorRepository;
    private final List<Integer> insertedIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        authorRepository = new AuthorRepository();
        insertedIds.clear();
    }

    @AfterEach
    void cleanUp() throws SQLException {
        // Remove only the authors created by this test class.
        // This avoids deleting data that existed before the tests ran.
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM authors WHERE id = ?")) {

            for (int authorId : insertedIds) {
                stmt.setInt(1, authorId);
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Create an Author object for use in tests.
     */
    private Author createAuthor(String name) {
        Author author = new Author();
        author.setName(name);
        return author;
    }

    /**
     * Insert an author and track its ID so it can be deleted during cleanup.
     */
    private int insertTracked(Author author)
            throws SQLException, DuplicateEntryException {

        int authorId = authorRepository.insert(author);
        insertedIds.add(authorId);
        return authorId;
    }

    // ---------------------------------------------------------
    // findAll tests
    // ---------------------------------------------------------

    @Test
    void findAllReturnsInsertedAuthors() throws Exception {
        // Arrange
        int firstId = insertTracked(createAuthor(
                "Author Test FindAll One"
        ));
        int secondId = insertTracked(createAuthor(
                "Author Test FindAll Two"
        ));

        // Act
        List<Author> authors = authorRepository.findAll();

        // Assert
        assertNotNull(authors);

        assertTrue(authors.stream().anyMatch(author ->
                author.getId() == firstId
                        && author.getName().equals(
                        "Author Test FindAll One"
                )
        ));

        assertTrue(authors.stream().anyMatch(author ->
                author.getId() == secondId
                        && author.getName().equals(
                        "Author Test FindAll Two"
                )
        ));
    }

    // ---------------------------------------------------------
    // findById tests
    // ---------------------------------------------------------

    @Test
    void findByIdReturnsInsertedAuthor() throws Exception {
        // Arrange
        Author expected = createAuthor(
                "Author Test FindById"
        );
        int authorId = insertTracked(expected);

        // Act
        Author actual = authorRepository.findById(authorId);

        // Assert
        assertNotNull(actual);
        assertEquals(authorId, actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    void findByIdThrowsWhenAuthorDoesNotExist() {
        // Arrange
        int missingAuthorId = -1;

        // Act and Assert
        assertThrows(
                EntryNotFoundException.class,
                () -> authorRepository.findById(missingAuthorId)
        );
    }

    // ---------------------------------------------------------
    // insert tests
    // ---------------------------------------------------------

    @Test
    void insertReturnsGeneratedId() throws Exception {
        // Arrange
        Author author = createAuthor(
                "Author Test Insert"
        );

        // Act
        int authorId = insertTracked(author);

        // Assert
        assertTrue(authorId > 0);

        Author inserted = authorRepository.findById(authorId);

        assertEquals(authorId, inserted.getId());
        assertEquals(author.getName(), inserted.getName());
    }

    @Test
    void insertThrowsForDuplicateAuthorName() throws Exception {
        // Arrange
        String name = "Author Test Duplicate Insert";

        insertTracked(createAuthor(name));

        Author duplicate = createAuthor(name);

        // Act and Assert
        assertThrows(
                DuplicateEntryException.class,
                () -> authorRepository.insert(duplicate)
        );
    }

    @Test
    void insertWithConnectionReturnsGeneratedId()
            throws Exception {

        // Arrange
        Author author = createAuthor(
                "Author Test Insert With Connection"
        );

        // Act
        int authorId;

        try (Connection conn = Database.getConnection()) {
            authorId = authorRepository.insert(conn, author);
        }

        // Track the inserted row for cleanup.
        insertedIds.add(authorId);

        // Assert
        assertTrue(authorId > 0);

        Author inserted = authorRepository.findById(authorId);

        assertEquals(authorId, inserted.getId());
        assertEquals(author.getName(), inserted.getName());
    }

    @Test
    void insertWithConnectionThrowsForDuplicateAuthorName()
            throws Exception {

        // Arrange
        String name = "Author Test Duplicate Shared Insert";

        insertTracked(createAuthor(name));

        Author duplicate = createAuthor(name);

        // Act and Assert
        try (Connection conn = Database.getConnection()) {
            assertThrows(
                    DuplicateEntryException.class,
                    () -> authorRepository.insert(conn, duplicate)
            );
        }
    }

    // ---------------------------------------------------------
    // update tests
    // ---------------------------------------------------------

    @Test
    void updateChangesAuthorName() throws Exception {
        // Arrange
        int authorId = insertTracked(
                createAuthor("Author Test Before Update")
        );

        String newName = "Author Test After Update";

        // Act
        authorRepository.update(authorId, newName);

        // Assert
        Author updated = authorRepository.findById(authorId);

        assertEquals(authorId, updated.getId());
        assertEquals(newName, updated.getName());
    }

    @Test
    void updateAllowsExistingName() throws Exception {
        // Arrange
        String name = "Author Test Unchanged Name";
        int authorId = insertTracked(createAuthor(name));

        // Act and Assert
        assertDoesNotThrow(
                () -> authorRepository.update(authorId, name)
        );

        Author unchanged = authorRepository.findById(authorId);

        assertEquals(authorId, unchanged.getId());
        assertEquals(name, unchanged.getName());
    }

    @Test
    void updateThrowsWhenAuthorDoesNotExist() {
        // Arrange
        int missingAuthorId = -1;

        // Act and Assert
        assertThrows(
                EntryNotFoundException.class,
                () -> authorRepository.update(
                        missingAuthorId,
                        "Author Test Missing Update"
                )
        );
    }

    @Test
    void updateThrowsForDuplicateAuthorName()
            throws Exception {

        // Arrange
        int firstId = insertTracked(
                createAuthor("Author Test Update First")
        );

        insertTracked(
                createAuthor("Author Test Update Second")
        );

        // Act and Assert
        assertThrows(
                DuplicateEntryException.class,
                () -> authorRepository.update(
                        firstId,
                        "Author Test Update Second"
                )
        );

        // Verify that the failed update did not change the record.
        Author unchanged = authorRepository.findById(firstId);

        assertEquals(
                "Author Test Update First",
                unchanged.getName()
        );
    }

    // ---------------------------------------------------------
    // delete tests
    // ---------------------------------------------------------

    @Test
    void deleteRemovesAuthor() throws Exception {
        // Arrange
        int authorId = insertTracked(
                createAuthor("Author Test Delete")
        );

        // Act
        authorRepository.delete(authorId);

        // Assert
        assertThrows(
                EntryNotFoundException.class,
                () -> authorRepository.findById(authorId)
        );
    }

    @Test
    void deleteThrowsWhenAuthorDoesNotExist() {
        // Arrange
        int missingAuthorId = -1;

        // Act and Assert
        assertThrows(
                EntryNotFoundException.class,
                () -> authorRepository.delete(missingAuthorId)
        );
    }

    // ---------------------------------------------------------
    // lookupByName tests
    // ---------------------------------------------------------

    @Test
    void lookupByNameReturnsMatchingAuthor() throws Exception {
        // Arrange
        String name = "Author Test Lookup By Name";
        int authorId = insertTracked(createAuthor(name));

        // Act
        Author actual = authorRepository.lookupByName(name);

        // Assert
        assertNotNull(actual);
        assertEquals(authorId, actual.getId());
        assertEquals(name, actual.getName());
    }

    @Test
    void lookupByNameThrowsWhenAuthorDoesNotExist() {
        // Arrange
        String missingName = "Author Test Missing Lookup";

        // Act and Assert
        assertThrows(
                EntryNotFoundException.class,
                () -> authorRepository.lookupByName(missingName)
        );
    }

    @Test
    void lookupByNameWithConnectionReturnsMatchingAuthor()
            throws Exception {

        // Arrange
        String name = "Author Test Shared Lookup";
        int authorId = insertTracked(createAuthor(name));

        // Act
        Author actual;

        try (Connection conn = Database.getConnection()) {
            actual = authorRepository.lookupByName(conn, name);
        }

        // Assert
        assertNotNull(actual);
        assertEquals(authorId, actual.getId());
        assertEquals(name, actual.getName());
    }

    @Test
    void lookupByNameWithConnectionThrowsWhenAuthorDoesNotExist()
            throws Exception {

        // Arrange
        String missingName = "Author Test Missing Shared Lookup";

        // Act and Assert
        try (Connection conn = Database.getConnection()) {
            assertThrows(
                    EntryNotFoundException.class,
                    () -> authorRepository.lookupByName(
                            conn,
                            missingName
                    )
            );
        }
    }
}