package io.github.kraused53.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void bookModelGettersAndSetters() {
        Book book = new Book();
        book.setId(1234);
        book.setTitle("The Hobbit");
        book.setIsbn10("0345339681");
        book.setIsbn13("9780345339683");
        book.setPageCount(320);
        book.setDescription("book description");

        assertEquals("The Hobbit", book.getTitle());
        assertEquals("0345339681", book.getIsbn10());
        assertEquals("9780345339683", book.getIsbn13());
        assertEquals(320, book.getPageCount());
        assertEquals("book description", book.getDescription());
    }
}
