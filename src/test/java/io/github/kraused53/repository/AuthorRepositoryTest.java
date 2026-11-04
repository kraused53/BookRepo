
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
import io.github.kraused53.exceptions.InvalidAuthorException;
import io.github.kraused53.models.Author;

public class AuthorRepositoryTest {

    private final AuthorRepository repository = new AuthorRepository();
    private static final AtomicLong AUTHOR_COUNTER = new AtomicLong();

    // ------------------------------------------------------------
    // insert()
    // ------------------------------------------------------------

    @Test
    void insertReturnsGeneratedIdAndPersistsAuthor() throws Exception {
        Author author = newAuthor();
        int id = -1;

        try(
            Connection conn = Database.getConnection();
        ) {
            id = repository.insert(conn, author);

            assertTrue(id > 0);

            Author loaded = repository.fetchById(conn, id);

            assertEquals(id, loaded.getId());
            assertEquals(author.getName(), loaded.getName());
        } finally {
            cleanup(id);
        }
    }

    @Test
    void insertRejectsDuplicateAuthorName() throws Exception {
        TestAuthor inserted = insertTestAuthor();

        try(
            Connection conn = Database.getConnection();
        ) {
            Author duplicate = new Author(inserted.author().getName());

            assertThrows(DuplicateEntryException.class,
                    () -> repository.insert(conn, duplicate));
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void insertRejectsNullAuthor() throws SQLException {
        try (
            Connection conn = Database.getConnection();
        ) {
            assertThrows(InvalidAuthorException.class,
                () -> repository.insert(conn, null));
        }
    }

    @Test
    void insertRejectsInvalidAuthor() throws SQLException {
        Author author = new Author("");
        
        try (
            Connection conn = Database.getConnection();
        ) {
            assertThrows(InvalidAuthorException.class,
                () -> repository.insert(conn, author));
        }
    }

    // ------------------------------------------------------------
    // fetchAll()
    // ------------------------------------------------------------

    @Test
    void fetchAllIncludesInsertedAuthorAndItsId() throws Exception {
        TestAuthor inserted = insertTestAuthor();

        try(
            Connection conn = Database.getConnection();
        ) {
            List<Author> authors = repository.fetchAll(conn);

            Author found = authors.stream()
                    .filter(a -> a.getName().equals(inserted.author().getName()))
                    .findFirst()
                    .orElseThrow();

            assertEquals(inserted.id(), found.getId());
            assertEquals(inserted.author().getName(), found.getName());
        } finally {
            cleanup(inserted.id());
        }
    }

    // ------------------------------------------------------------
    // fetchById()
    // ------------------------------------------------------------

    @Test
    void fetchByIdReturnsMatchingAuthor() throws Exception {
        TestAuthor inserted = insertTestAuthor();

        try(
            Connection conn = Database.getConnection();
        ) {
            Author result = repository.fetchById(conn, inserted.id());

            assertEquals(inserted.id(), result.getId());
            assertEquals(inserted.author().getName(), result.getName());
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void fetchByIdThrowsWhenAuthorDoesNotExist() throws SQLException {
        try (
            Connection conn = Database.getConnection();
        ) {
            assertThrows(EntryNotFoundException.class,
                () -> repository.fetchById(conn, Integer.MAX_VALUE));
        }
    }

    @Test
    void fetchByIdRejectsNonpositiveId() throws SQLException {
        try(
            Connection conn = Database.getConnection()
        ) {
            assertThrows(IllegalArgumentException.class,
                () -> repository.fetchById(conn, 0));

            assertThrows(IllegalArgumentException.class,
                () -> repository.fetchById(conn, -1));
        }
    }

    // ------------------------------------------------------------
    // fetchByName()
    // ------------------------------------------------------------

    @Test
    void fetchByNameReturnsMatchingAuthor() throws Exception {
        TestAuthor inserted = insertTestAuthor();

        try(
            Connection conn = Database.getConnection()
        ) {
            Author result = repository.fetchByName(conn, inserted.author().getName());

            assertEquals(inserted.id(), result.getId());
            assertEquals(inserted.author().getName(), result.getName());
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void fetchByNameThrowsWhenAuthorDoesNotExist() throws Exception {
        try(
            Connection conn = Database.getConnection()
        ) {
            Author author = newAuthor();

            assertThrows(EntryNotFoundException.class,
                () -> repository.fetchByName(conn, author.getName()));
        }
    }

    @Test
    void fetchByNameRejectsInvalidName() throws SQLException {
        try(
            Connection conn = Database.getConnection()
        ) {
            assertThrows(IllegalArgumentException.class,
                () -> repository.fetchByName(conn, null));

            assertThrows(IllegalArgumentException.class,
                () -> repository.fetchByName(conn, ""));

            assertThrows(IllegalArgumentException.class,
                () -> repository.fetchByName(conn, "   "));
        }
    }

    // ------------------------------------------------------------
    // updateName()
    // ------------------------------------------------------------

    @Test
    void updateNameChangesAuthorName() throws Exception {
        TestAuthor inserted = insertTestAuthor();
        String updatedName = uniqueAuthorName();

        try(
            Connection conn = Database.getConnection();
        ) {
            repository.updateName(conn, inserted.id(), updatedName);

            Author result = repository.fetchById(conn, inserted.id());

            assertEquals(updatedName, result.getName());
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void updateNameAllowsExistingName() throws Exception {
        TestAuthor inserted = insertTestAuthor();

        try(
            Connection conn = Database.getConnection();
        ) {
            repository.updateName(
                    conn, 
                    inserted.id(),
                    inserted.author().getName()
            );

            assertEquals(
                    inserted.author().getName(),
                    repository.fetchById(conn, inserted.id()).getName()
            );
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void updateNameThrowsForMissingAuthor()  throws Exception {
        try(
            Connection conn = Database.getConnection()
        ) {
            assertThrows(EntryNotFoundException.class,
                () -> repository.updateName(
                        conn, 
                        Integer.MAX_VALUE,
                        uniqueAuthorName()
                ));
        }
    }

    @Test
    void updateNameRejectsNonpositiveId()  throws Exception {
        
        try(
            Connection conn = Database.getConnection()
        ) {
            assertThrows(IllegalArgumentException.class,
                () -> repository.updateName(conn, 0, uniqueAuthorName()));

            assertThrows(IllegalArgumentException.class,
                () -> repository.updateName(conn, -1, uniqueAuthorName()));
        }
    }

    @Test
    void updateNameRejectsInvalidName()  throws Exception {
        try(
            Connection conn = Database.getConnection()
        ) {
            assertThrows(IllegalArgumentException.class,
                () -> repository.updateName(conn, 1, null));

            assertThrows(IllegalArgumentException.class,
                () -> repository.updateName(conn, 1, ""));

            assertThrows(IllegalArgumentException.class,
                () -> repository.updateName(conn, 1, "   "));
        }
    }

    // ------------------------------------------------------------
    // deleteById()
    // ------------------------------------------------------------

    @Test
    void deleteByIdRemovesAuthor() throws Exception {
        TestAuthor inserted = insertTestAuthor();

        try(
            Connection conn = Database.getConnection()
        ) {
            repository.deleteById(conn, inserted.id());

            assertFalse(repository.existsById(conn, inserted.id()));
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void deleteByIdRejectsNonpositiveId()  throws Exception {
        try(
            Connection conn = Database.getConnection()
        ) {
            assertThrows(IllegalArgumentException.class,
                () -> repository.deleteById(conn, 0));

            assertThrows(IllegalArgumentException.class,
                () -> repository.deleteById(conn, -1));
        }
    }

    @Test
    void deleteByIdThrowsForMissingAuthor()  throws Exception {
        try(
            Connection conn = Database.getConnection()
        ) {
            assertThrows(EntryNotFoundException.class,
                () -> repository.deleteById(conn, Integer.MAX_VALUE));
        }
    }

    // ------------------------------------------------------------
    // deleteByName()
    // ------------------------------------------------------------

    @Test
    void deleteByNameRemovesAuthor() throws Exception {
        TestAuthor inserted = insertTestAuthor();

        try(
            Connection conn = Database.getConnection()
        ) {
            repository.deleteByName(conn, inserted.author().getName());

            assertFalse(repository.existsById(conn, inserted.id()));
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void deleteByNameRejectsInvalidName()  throws Exception {
        try(
            Connection conn = Database.getConnection()
        ) {
            assertThrows(IllegalArgumentException.class,
                () -> repository.deleteByName(conn, null));

            assertThrows(IllegalArgumentException.class,
                () -> repository.deleteByName(conn, ""));

            assertThrows(IllegalArgumentException.class,
                () -> repository.deleteByName(conn, "   "));
        }
    }

    @Test
    void deleteByNameThrowsForMissingAuthor() throws Exception {
        try(
            Connection conn = Database.getConnection()
        ) {
            Author author = newAuthor();

            assertThrows(EntryNotFoundException.class,
                () -> repository.deleteByName(conn, author.getName()));
        }
    }

    // ------------------------------------------------------------
    // existsById()
    // ------------------------------------------------------------

    @Test
    void existsByIdReturnsTrueForExistingAuthor() throws Exception {
        TestAuthor inserted = insertTestAuthor();

        try(
            Connection conn = Database.getConnection()
        ) {
            assertTrue(repository.existsById(conn, inserted.id()));
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void existsByIdReturnsFalseForMissingAuthor() throws SQLException {
        try(Connection conn = Database.getConnection()) {
            assertFalse(repository.existsById(conn, Integer.MAX_VALUE));
        }
    }

    @Test
    void existsByIdRejectsNonpositiveId()  throws Exception {
        try(Connection conn = Database.getConnection()) {
            assertThrows(IllegalArgumentException.class,
                () -> repository.existsById(conn, 0));

            assertThrows(IllegalArgumentException.class,
                () -> repository.existsById(conn, -1));
        }

    }

    // ------------------------------------------------------------
    // existsByName()
    // ------------------------------------------------------------

    @Test
    void existsByNameReturnsTrueForExistingAuthor() throws Exception {
        TestAuthor inserted = insertTestAuthor();

        try(
            Connection conn = Database.getConnection()
        ) {
            assertTrue(repository.existsByName(conn, inserted.author().getName()));
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void existsByNameReturnsFalseForMissingAuthor() throws Exception {
        try (Connection conn = Database.getConnection()) {
            assertFalse(repository.existsByName(conn, uniqueAuthorName()));
        }
    }

    @Test
    void existsByNameRejectsInvalidName()  throws Exception {
        try(
            Connection conn = Database.getConnection()
        ) {
            assertThrows(IllegalArgumentException.class,
                () -> repository.existsByName(conn, null));

            assertThrows(IllegalArgumentException.class,
                () -> repository.existsByName(conn, ""));

            assertThrows(IllegalArgumentException.class,
                () -> repository.existsByName(conn, "   "));
        }
    }

    // ------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------

    private record TestAuthor(Author author, int id) {}

    private TestAuthor insertTestAuthor()
            throws Exception {

        try(
            Connection conn = Database.getConnection()
        ) {
            Author author = newAuthor();

            return new TestAuthor(author, repository.insert(conn, author));
        }
    }

    private Author newAuthor()  throws Exception {
        return new Author(uniqueAuthorName());
    }

    private String uniqueAuthorName()  throws Exception {
        return "Repository Test Author " + System.currentTimeMillis()
                + "-" + AUTHOR_COUNTER.incrementAndGet();
    }

    private void cleanup(int id) throws SQLException, EntryNotFoundException {
        try(Connection conn = Database.getConnection()) {
            if (repository.existsById(conn, id)) {
                repository.deleteById(conn, id);
            }
        }
    }
}
