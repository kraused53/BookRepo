package io.github.kraused53.models;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.kraused53.exceptions.InvalidISBNException;

public class BookTest {
    @Test
    void bookModelGettersAndSetters() {
        Book book = makeTestBook();

        assertEquals("The Hobbit", book.getTitle());
        assertEquals("0345339681", book.getIsbn10());
        assertEquals("9780345339683", book.getIsbn13());
        assertEquals(320, book.getPageCount());
        assertEquals("book description", book.getDescription());
    }

    @Test
    void bookModelIsValidValid() {
        Book book = makeTestBook();

        assertTrue(book.isValid());
    }

    @Test
    void bookModelIsValidOnlyISBN10() {
        Book book = new Book();
        book.setId(12345);
        book.setTitle("The Hobbit");
        assertDoesNotThrow(() -> {
            book.setIsbn10("0345339681");
        });
        book.setPageCount(320);
        book.setDescription("book description");

        book.addAuthor(makeTestAuthor());

        assertTrue(book.isValid());
    }

    @Test
    void bookModelIsValidOnlyISBN13() {
        Book book = new Book();
        book.setId(12345);
        book.setTitle("The Hobbit");
        assertDoesNotThrow(() -> {
            book.setIsbn13("9780345339683");
        });
        book.setPageCount(320);
        book.setDescription("book description");

        book.addAuthor(makeTestAuthor());

        assertTrue(book.isValid());
    }

    @Test
    void bookModelIsValidInvalidWorksWithNoDescription() {
        Book book = new Book();
        book.setId(12345);
        book.setTitle("The Hobbit");
        assertDoesNotThrow(() -> {
            book.setIsbn10("0345339681");
            book.setIsbn13("9780345339683");
        });
        book.setPageCount(320);

        book.addAuthor(makeTestAuthor());

        assertTrue(book.isValid());
    }

    @Test
    void bookModelIsValidInvalidTitle() {
        Book book = new Book();
        book.setId(12345);
        assertDoesNotThrow(() -> {
            book.setIsbn10("0345339681");
            book.setIsbn13("9780345339683");
        });
        book.setPageCount(320);
        book.setDescription("book description");

        book.addAuthor(makeTestAuthor());

        assertFalse(book.isValid());
    }

    @Test
    void bookModelIsValidInvalidISBNs() {
        Book book = new Book();
        book.setId(12345);
        book.setTitle("The Hobbit");
        book.setPageCount(320);
        book.setDescription("book description");

        book.addAuthor(makeTestAuthor());

        assertFalse(book.isValid());
    }

    @Test
    void bookModelIsValidInvalidPageCount() {
        Book book = new Book();
        book.setId(12345);
        book.setTitle("The Hobbit");
        assertDoesNotThrow(() -> {
            book.setIsbn10("0345339681");
            book.setIsbn13("9780345339683");
        });
        book.setDescription("book description");

        book.addAuthor(makeTestAuthor());

        assertFalse(book.isValid());
    }

    @Test
    void bookModelCatchInvalidId() {
        Book book = new Book();
        
        assertThrows(IllegalArgumentException.class, () -> {
            book.setId(0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            book.setId(-1);
        });
    }

    @Test
    void bookModelCatchInvalidTitle() {
        Book book = new Book();
        
        assertThrows(IllegalArgumentException.class, () -> {
            book.setTitle(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            book.setTitle("");
        });
    }

    @Test
    void bookModelCatchInvalidISBN10() {
        Book book = new Book();
        
        assertThrows(InvalidISBNException.class, () -> {
            book.setIsbn10(null);
        });

        assertThrows(InvalidISBNException.class, () -> {
            book.setIsbn10("");
        });

        assertThrows(InvalidISBNException.class, () -> {
            book.setIsbn10("    ");
        });

        assertThrows(InvalidISBNException.class, () -> {
            book.setIsbn10("1");
        });

        assertThrows(InvalidISBNException.class, () -> {
            book.setIsbn10("12345678901");
        });
    }

    @Test
    void bookModelCatchInvalidISBN13() {
        Book book = new Book();
        
        assertThrows(InvalidISBNException.class, () -> {
            book.setIsbn13(null);
        });

        assertThrows(InvalidISBNException.class, () -> {
            book.setIsbn13("");
        });

        assertThrows(InvalidISBNException.class, () -> {
            book.setIsbn13("    ");
        });

        assertThrows(InvalidISBNException.class, () -> {
            book.setIsbn13("1");
        });

        assertThrows(InvalidISBNException.class, () -> {
            book.setIsbn13("12345678901234");
        });
    }

    /*** Utilities ***/
    private Book makeTestBook() {
        Book book = new Book();
        book.setId(12345);
        book.setTitle("The Hobbit");
        try {
            book.setIsbn10("0345339681");
            book.setIsbn13("9780345339683");
        } catch (Exception e) {
            return null;
        }
        book.setPageCount(320);
        book.setDescription("book description");

        book.addAuthor(makeTestAuthor());

        return book;
    }

    private Author makeTestAuthor() {
        Author author = new Author();
        author.setId(67890);
        author.setName("Jim Bob");

        return author;
    }
}
