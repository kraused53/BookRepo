package io.github.kraused53.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {

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
    void bookModelISBN10BlankIsNull() {
        Book book = makeTestBook();
        book.setIsbn10("");

        assertEquals("The Hobbit", book.getTitle());
        assertNull(book.getIsbn10());
        assertEquals("9780345339683", book.getIsbn13());
        assertEquals(320, book.getPageCount());
        assertEquals("book description", book.getDescription());
    }

    @Test
    void bookModelISBN13BlankIsNull() {
        Book book = makeTestBook();
        book.setIsbn13("");

        assertEquals("The Hobbit", book.getTitle());
        assertEquals("0345339681", book.getIsbn10());
        assertNull(book.getIsbn13());
        assertEquals(320, book.getPageCount());
        assertEquals("book description", book.getDescription());
    }

    @Test
    void bookIsValidWithValidBook() {
        Book book = makeTestBook();

        assertTrue(book.isValid());
    }

    @Test
    void bookIsValidNullTitle() {
        Book book = makeTestBook();
        book.setTitle(null);

        assertFalse(book.isValid());
    }

    @Test
    void bookIsValidBlankTitle() {
        Book book = makeTestBook();
        book.setTitle("");

        assertFalse(book.isValid());
    }

    @Test
    void bookIsValidNullISBNs() {
        Book book = makeTestBook();
        book.setIsbn10(null);
        book.setIsbn13(null);

        assertFalse(book.isValid());
    }

    @Test
    void bookIsValidBlankISBNs() {
        Book book = makeTestBook();
        book.setIsbn10("");
        book.setIsbn13("");

        assertFalse(book.isValid());
    }

    @Test
    void bookIsValidNegativePageCount() {
        Book book = makeTestBook();
        book.setPageCount(-1);

        assertFalse(book.isValid());
    }

    @Test
    void bookIsValidZeroPageCount() {
        Book book = makeTestBook();
        book.setPageCount(0);

        assertFalse(book.isValid());
    }

    @Test
    void bookIsValidNoAuthors() {
        Book book = new Book();
        book.setId(12345);
        book.setTitle("The Hobbit");
        book.setIsbn10("0345339681");
        book.setIsbn13("9780345339683");
        book.setPageCount(320);
        book.setDescription("book description");

        assertFalse(book.isValid());
    }

    // Utilities
    private Book makeTestBook() {
        Book book = new Book();
        book.setId(12345);
        book.setTitle("The Hobbit");
        book.setIsbn10("0345339681");
        book.setIsbn13("9780345339683");
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
