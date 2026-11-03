
package io.github.kraused53.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

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
        int id = repository.insert(author);

        try {
            assertTrue(id > 0);

            Author loaded = repository.fetchById(id);

            assertEquals(id, loaded.getId());
            assertEquals(author.getName(), loaded.getName());
        } finally {
            cleanup(id);
        }
    }

    @Test
    void insertRejectsDuplicateAuthorName() throws Exception {
        TestAuthor inserted = insertTestAuthor();

        try {
            Author duplicate = new Author(inserted.author().getName());

            assertThrows(DuplicateEntryException.class,
                    () -> repository.insert(duplicate));
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void insertRejectsNullAuthor() {
        assertThrows(InvalidAuthorException.class,
                () -> repository.insert(null));
    }

    @Test
    void insertRejectsInvalidAuthor() {
        Author author = new Author("");

        assertThrows(InvalidAuthorException.class,
                () -> repository.insert(author));
    }

    // ------------------------------------------------------------
    // fetchAll()
    // ------------------------------------------------------------

    @Test
    void fetchAllIncludesInsertedAuthorAndItsId() throws Exception {
        TestAuthor inserted = insertTestAuthor();

        try {
            List<Author> authors = repository.fetchAll();

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

        try {
            Author result = repository.fetchById(inserted.id());

            assertEquals(inserted.id(), result.getId());
            assertEquals(inserted.author().getName(), result.getName());
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void fetchByIdThrowsWhenAuthorDoesNotExist() {
        assertThrows(EntryNotFoundException.class,
                () -> repository.fetchById(Integer.MAX_VALUE));
    }

    @Test
    void fetchByIdRejectsNonpositiveId() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.fetchById(0));

        assertThrows(IllegalArgumentException.class,
                () -> repository.fetchById(-1));
    }

    // ------------------------------------------------------------
    // fetchByName()
    // ------------------------------------------------------------

    @Test
    void fetchByNameReturnsMatchingAuthor() throws Exception {
        TestAuthor inserted = insertTestAuthor();

        try {
            Author result = repository.fetchByName(inserted.author().getName());

            assertEquals(inserted.id(), result.getId());
            assertEquals(inserted.author().getName(), result.getName());
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void fetchByNameThrowsWhenAuthorDoesNotExist() throws Exception {
        Author author = newAuthor();

        assertThrows(EntryNotFoundException.class,
                () -> repository.fetchByName(author.getName()));
    }

    @Test
    void fetchByNameRejectsInvalidName() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.fetchByName(null));

        assertThrows(IllegalArgumentException.class,
                () -> repository.fetchByName(""));

        assertThrows(IllegalArgumentException.class,
                () -> repository.fetchByName("   "));
    }

    // ------------------------------------------------------------
    // updateName()
    // ------------------------------------------------------------

    @Test
    void updateNameChangesAuthorName() throws Exception {
        TestAuthor inserted = insertTestAuthor();
        String updatedName = uniqueAuthorName();

        try {
            repository.updateName(inserted.id(), updatedName);

            Author result = repository.fetchById(inserted.id());

            assertEquals(updatedName, result.getName());
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void updateNameAllowsExistingName() throws Exception {
        TestAuthor inserted = insertTestAuthor();

        try {
            repository.updateName(
                    inserted.id(),
                    inserted.author().getName()
            );

            assertEquals(
                    inserted.author().getName(),
                    repository.fetchById(inserted.id()).getName()
            );
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void updateNameThrowsForMissingAuthor() {
        assertThrows(EntryNotFoundException.class,
                () -> repository.updateName(
                        Integer.MAX_VALUE,
                        uniqueAuthorName()
                ));
    }

    @Test
    void updateNameRejectsNonpositiveId() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.updateName(0, uniqueAuthorName()));

        assertThrows(IllegalArgumentException.class,
                () -> repository.updateName(-1, uniqueAuthorName()));
    }

    @Test
    void updateNameRejectsInvalidName() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.updateName(1, null));

        assertThrows(IllegalArgumentException.class,
                () -> repository.updateName(1, ""));

        assertThrows(IllegalArgumentException.class,
                () -> repository.updateName(1, "   "));
    }

    // ------------------------------------------------------------
    // deleteById()
    // ------------------------------------------------------------

    @Test
    void deleteByIdRemovesAuthor() throws Exception {
        TestAuthor inserted = insertTestAuthor();

        try {
            repository.deleteById(inserted.id());

            assertFalse(repository.existsById(inserted.id()));
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void deleteByIdRejectsNonpositiveId() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.deleteById(0));

        assertThrows(IllegalArgumentException.class,
                () -> repository.deleteById(-1));
    }

    @Test
    void deleteByIdThrowsForMissingAuthor() {
        assertThrows(EntryNotFoundException.class,
                () -> repository.deleteById(Integer.MAX_VALUE));
    }

    // ------------------------------------------------------------
    // deleteByName()
    // ------------------------------------------------------------

    @Test
    void deleteByNameRemovesAuthor() throws Exception {
        TestAuthor inserted = insertTestAuthor();

        try {
            repository.deleteByName(inserted.author().getName());

            assertFalse(repository.existsById(inserted.id()));
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void deleteByNameRejectsInvalidName() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.deleteByName(null));

        assertThrows(IllegalArgumentException.class,
                () -> repository.deleteByName(""));

        assertThrows(IllegalArgumentException.class,
                () -> repository.deleteByName("   "));
    }

    @Test
    void deleteByNameThrowsForMissingAuthor() throws Exception {
        Author author = newAuthor();

        assertThrows(EntryNotFoundException.class,
                () -> repository.deleteByName(author.getName()));
    }

    // ------------------------------------------------------------
    // existsById()
    // ------------------------------------------------------------

    @Test
    void existsByIdReturnsTrueForExistingAuthor() throws Exception {
        TestAuthor inserted = insertTestAuthor();

        try {
            assertTrue(repository.existsById(inserted.id()));
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void existsByIdReturnsFalseForMissingAuthor() throws SQLException {
        assertFalse(repository.existsById(Integer.MAX_VALUE));
    }

    @Test
    void existsByIdRejectsNonpositiveId() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.existsById(0));

        assertThrows(IllegalArgumentException.class,
                () -> repository.existsById(-1));
    }

    // ------------------------------------------------------------
    // existsByName()
    // ------------------------------------------------------------

    @Test
    void existsByNameReturnsTrueForExistingAuthor() throws Exception {
        TestAuthor inserted = insertTestAuthor();

        try {
            assertTrue(repository.existsByName(inserted.author().getName()));
        } finally {
            cleanup(inserted.id());
        }
    }

    @Test
    void existsByNameReturnsFalseForMissingAuthor() throws SQLException {
        assertFalse(repository.existsByName(uniqueAuthorName()));
    }

    @Test
    void existsByNameRejectsInvalidName() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.existsByName(null));

        assertThrows(IllegalArgumentException.class,
                () -> repository.existsByName(""));

        assertThrows(IllegalArgumentException.class,
                () -> repository.existsByName("   "));
    }

    // ------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------

    private record TestAuthor(Author author, int id) {}

    private TestAuthor insertTestAuthor()
            throws SQLException, DuplicateEntryException, InvalidAuthorException {

        Author author = newAuthor();

        return new TestAuthor(author, repository.insert(author));
    }

    private Author newAuthor() {
        return new Author(uniqueAuthorName());
    }

    private String uniqueAuthorName() {
        return "Repository Test Author " + System.currentTimeMillis()
                + "-" + AUTHOR_COUNTER.incrementAndGet();
    }

    private void cleanup(int id) throws SQLException, EntryNotFoundException {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        }
    }
}
