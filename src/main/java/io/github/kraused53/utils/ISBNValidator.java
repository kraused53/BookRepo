package io.github.kraused53.utils;

import io.github.kraused53.exceptions.InvalidISBNException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ISBNValidator {

    private static final Logger logger =
            LoggerFactory.getLogger(ISBNValidator.class);

    // Prevent instantiation of this utility class.
    private ISBNValidator() {
        throw new UnsupportedOperationException(
                "ISBNValidator is a utility class"
        );
    }

    /**
     * Sanitizes and validates an ISBN-10.
     *
     * @param isbn ISBN-10 string to validate
     * @return Sanitized, validated ISBN-10
     * @throws InvalidISBNException if the ISBN is invalid
     */
    public static String validateISBN10(String isbn)
            throws InvalidISBNException {

        logger.debug("Validating ISBN-10");

        if (isbn == null) {
            logger.warn("ISBN-10 validation failed: input is null");
            throw new InvalidISBNException("ISBN-10 cannot be null");
        }

        // Remove formatting characters while preserving X/x.
        String sanitized = isbn.replaceAll("[^0-9Xx]", "")
                .toUpperCase();

        logger.debug("Sanitized ISBN-10: {}", sanitized);

        // Enforce length.
        if (sanitized.length() != 10) {
            logger.warn(
                    "ISBN-10 validation failed: expected 10 characters, got {}",
                    sanitized.length()
            );
            throw new InvalidISBNException(
                    "ISBN-10 must contain exactly 10 characters"
            );
        }

        // X is permitted only as the final check digit.
        if (!sanitized.matches("[0-9]{9}[0-9X]")) {
            logger.warn(
                    "ISBN-10 validation failed: invalid characters or X position"
            );
            throw new InvalidISBNException(
                    "ISBN-10 must contain 9 digits followed by a digit or X"
            );
        }

        // Calculate the ISBN-10 checksum.
        int checksum = 0;

        for (int i = 0; i < 10; i++) {
            int digit;

            if (i == 9 && sanitized.charAt(i) == 'X') {
                digit = 10;
            } else {
                digit = sanitized.charAt(i) - '0';
            }

            checksum += digit * (10 - i);
        }

        if (checksum % 11 != 0) {
            logger.warn("ISBN-10 validation failed: checksum mismatch");
            throw new InvalidISBNException(
                    "ISBN-10 checksum is invalid"
            );
        }

        logger.debug("ISBN-10 validation successful");
        return sanitized;
    }

    /**
     * Sanitizes and validates an ISBN-13.
     *
     * @param isbn ISBN-13 string to validate
     * @return Sanitized, validated ISBN-13
     * @throws InvalidISBNException if the ISBN is invalid
     */
    public static String validateISBN13(String isbn)
            throws InvalidISBNException {

        logger.debug("Validating ISBN-13");

        if (isbn == null) {
            logger.warn("ISBN-13 validation failed: input is null");
            throw new InvalidISBNException("ISBN-13 cannot be null");
        }

        // Remove whitespace, hyphens, and other non-numeric characters.
        String sanitized = isbn.replaceAll("[^0-9]", "");

        logger.debug("Sanitized ISBN-13: {}", sanitized);

        // Enforce length.
        if (sanitized.length() != 13) {
            logger.warn(
                    "ISBN-13 validation failed: expected 13 digits, got {}",
                    sanitized.length()
            );
            throw new InvalidISBNException(
                    "ISBN-13 must contain exactly 13 digits"
            );
        }

        // Calculate the ISBN-13 checksum.
        int checksum = 0;

        for (int i = 0; i < 13; i++) {
            int digit = sanitized.charAt(i) - '0';

            checksum += digit * (i % 2 == 0 ? 1 : 3);
        }

        if (checksum % 10 != 0) {
            logger.warn("ISBN-13 validation failed: checksum mismatch");
            throw new InvalidISBNException(
                    "ISBN-13 checksum is invalid"
            );
        }

        logger.debug("ISBN-13 validation successful");
        return sanitized;
    }
}