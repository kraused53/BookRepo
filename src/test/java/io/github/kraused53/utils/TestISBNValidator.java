package io.github.kraused53.utils;

import io.github.kraused53.exceptions.InvalidISBNException;
import org.junit.jupiter.api.Test;

import static io.github.kraused53.utils.ISBNValidator.validateISBN10;
import static io.github.kraused53.utils.ISBNValidator.validateISBN13;

import static org.junit.jupiter.api.Assertions.*;

public class TestISBNValidator {

    /* Test ISBN 10 Validation */

    @Test
    public void testISBN10Valid() {
        String isbn10 = "0345339703";

        String result = assertDoesNotThrow(() -> validateISBN10(isbn10));

        assertEquals("0345339703", result);
    }

    @Test
    public void testISBN10ValidExtraWhiteSpace() {
        String isbn10 = "\t0345   339703     \n";

        String result = assertDoesNotThrow(() -> validateISBN10(isbn10));

        assertEquals("0345339703", result);
    }

    @Test
    public void testISBN10ValidNonNumeric() {
        String isbn10 = "My isbn: 0345-3-3-9-703!!!!!";

        String result = assertDoesNotThrow(() -> validateISBN10(isbn10));

        assertEquals("0345339703", result);
    }

    @Test
    public void testISBN10WithXUpper() {
        String isbn10 = "080442957X";

        String result = assertDoesNotThrow(() -> validateISBN10(isbn10));

        assertEquals("080442957X", result);
    }

    @Test
    public void testISBN10WithXLower() {
        String isbn10 = "080442957x";

        String result = assertDoesNotThrow(() -> validateISBN10(isbn10));

        assertEquals("080442957X", result);
    }

    @Test
    public void testISBN10InvalidTooLong() {
        String isbn10 = "0345339703000";

        assertThrows(InvalidISBNException.class, () -> {
            validateISBN10(isbn10);
        });
    }

    @Test
    public void testISBN10InvalidTooShort() {
        String isbn10 = "03453397";

        assertThrows(InvalidISBNException.class, () -> {
            validateISBN10(isbn10);
        });
    }

    @Test
    public void testISBN10InvalidChecksumError() {
        String isbn10 = "1234567899";

        assertThrows(InvalidISBNException.class, () -> {
            validateISBN10(isbn10);
        });
    }

    @Test
    public void testISBN10InvalidXLocation() {
        String isbn10 = "08X442957X";

        assertThrows(InvalidISBNException.class, () -> {
            validateISBN10(isbn10);
        });
    }

    /* Test ISBN 13 Validation */


    @Test
    public void testISBN13Valid() {
        String isbn13 = "9780671741921";

        String result = assertDoesNotThrow(() -> validateISBN13(isbn13));

        assertEquals("9780671741921", result);
    }

    @Test
    public void testISBN13ValidExtraWhiteSpace() {
        String isbn13 = "\t9 78 067 17   419 21     \n";

        String result = assertDoesNotThrow(() -> validateISBN13(isbn13));

        assertEquals("9780671741921", result);
    }

    @Test
    public void testISBN13ValidNonNumeric() {
        String isbn13 = "978-0-671-74192-1";

        String result = assertDoesNotThrow(() -> validateISBN13(isbn13));

        assertEquals("9780671741921", result);
    }

    @Test
    public void testISBN13InvalidX() {
        String isbn13 = "978067174192X";

        assertThrows(InvalidISBNException.class, () -> {
            validateISBN13(isbn13);
        });
    }

    @Test
    public void testISBN13InvalidTooLong() {
        String isbn13 = "9780671741921000";

        assertThrows(InvalidISBNException.class, () -> {
            validateISBN13(isbn13);
        });
    }

    @Test
    public void testISBN13InvalidTooShort() {
        String isbn13 = "9780671741";

        assertThrows(InvalidISBNException.class, () -> {
            validateISBN13(isbn13);
        });
    }

    @Test
    public void testISBN13InvalidChecksumError() {
        String isbn13 = "9780671741920";

        assertThrows(InvalidISBNException.class, () -> {
            validateISBN13(isbn13);
        });
    }

}
