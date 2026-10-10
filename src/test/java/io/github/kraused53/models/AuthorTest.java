package io.github.kraused53.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuthorTest {

    @Test
    void authorModelGettersAndSetters() {
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
    void authorModelIsValidInvalidNullName() {
        Author author = makeTestAuthor();
        author.setName(null);

        assertFalse(author.isValid());
    }

    @Test
    void authorModelIsValidInvalidBlankName() {
        Author author = makeTestAuthor();
        author.setName("");

        assertFalse(author.isValid());
    }

    @Test
    void authorModelIsValidInvalidZeroID() {
        Author author = makeTestAuthor();
        author.setId(0);

        assertFalse(author.isValid());
    }

    @Test
    void authorModelIsValidInvalidNegativeID() {
        Author author = makeTestAuthor();
        author.setId(-1);

        assertFalse(author.isValid());
    }

    @Test
    void authorModelAuthorsValidAfterAddingToBook() {
        Book book = new Book();
        book.setId(12345);
        book.setTitle("Test Book");
        book.setIsbn10("0345339681");
        book.setIsbn13("9780345339683");
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

    private Author makeTestAuthor() {
        Author author = new Author();
        author.setId(67890);
        author.setName("Jim Bob");

        return author;
    }
}
