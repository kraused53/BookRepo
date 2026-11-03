package io.github.kraused53.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class AuthorTest {
    
    @Test
    void testAuthorModelGettersAndSetters() {
        Author author = makeTestAuthor();

        assertEquals(67890, author.getId());
        assertEquals("Jim Bob", author.getName());
    }

    @Test
    void authorModelIsValidValid() {
        Author author = makeTestAuthor();

        assertTrue(author.isValid());
    }

    @Test
    void authorModelIsValidInvalid() {
        Author author = new Author();

        assertFalse(author.isValid());
    }

    @Test
    void authorModelInvalidIdSetThrow() {
        Author author = new Author();

        assertThrows(IllegalArgumentException.class, () -> {
            author.setId(0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            author.setId(-1);
        });
    }

    @Test
    void authorModelInvalidNameSetThrow() {
        Author author = new Author();

        assertThrows(IllegalArgumentException.class, () -> {
            author.setName("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            author.setName(null);
        });
    }

    @Test
    void authorModelAuthorsValidAfterAddingToBook() {
        Book book = new Book();
        book.setId(12345);
        book.setTitle("Test Book");
        assertDoesNotThrow(() -> {
            book.setIsbn10("0345339681");
        });
        assertDoesNotThrow(() -> {
            book.setIsbn13("9780345339683");
        });
        book.setPageCount(320);
        book.setDescription("book description");

        for(int i = 1; i < 11; i++) {
            Author author = new Author();
            author.setId(i);
            author.setName("Author: "+i);

            book.addAuthor(author);
        }

        List<Author> authors = book.getAuthors();

        for(int i = 1; i < 11; i++) {
            assertEquals(i, authors.get(i-1).getId());
            assertEquals("Author: "+i, authors.get(i-1).getName());
        }
    }

    /*** Utility ***/

    // Make a known test author. This is just to save a few lines of code in each test.
    private Author makeTestAuthor() {
        Author author = new Author();
        author.setId(67890);
        author.setName("Jim Bob");

        return author;
    }
}
