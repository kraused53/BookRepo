package io.github.kraused53;

import java.sql.SQLException;

import javax.management.RuntimeErrorException;

import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.exceptions.InvalidBookException;
import io.github.kraused53.exceptions.InvalidISBNException;
import io.github.kraused53.models.Book;
import io.github.kraused53.repository.BookRepository;

public class BookRepo {
    public static void main(String[] args) {

        BookRepository bookRepository = new BookRepository();

        Book book;

        // Make book
        try {
            book = new Book(
                "Example Book Title",
                null,
                "9780778387459",
                5,
                "A test book"
            );
        } catch (Exception e) {
            throw new RuntimeException("There was an error making the book!" + e);
        }

        // Search for book before inserting
        try {
            System.out.println("========== Book ==========\n"+bookRepository.fetchByISBN("9780778387459")+"\n");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException("There was an error fetching the book!" + e);
        } catch (EntryNotFoundException | InvalidBookException | InvalidISBNException f) {
            System.out.println(f.getMessage());
        }

        // Insert the book
        try {
            bookRepository.insert(book);
        } catch (DuplicateEntryException de) {
            System.out.println("This book is already in the database");
        } catch (SQLException e) {
            throw new RuntimeException("There was an error inserting the book!" + e);
        }

        // Book should now be in the database
        try {
            System.out.println("========== Book ==========\n"+bookRepository.fetchByISBN("9780778387459")+"\n");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException("There was an error fetching the book!" + e);
        } catch (EntryNotFoundException | InvalidBookException | InvalidISBNException f) {
            System.out.println(f.getMessage());
        }

        // Try inserting a second time
        try {
            bookRepository.insert(book);
        } catch (DuplicateEntryException de) {
            System.out.println("This book is already in the database");
        } catch (SQLException e) {
            throw new RuntimeException("There was an error inserting the book!" + e);
        }

        // Book should still be in database
        try {
            System.out.println("========== Book ==========\n"+bookRepository.fetchByISBN("9780778387459")+"\n");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException("There was an error fetching the book!" + e);
        } catch (EntryNotFoundException | InvalidBookException | InvalidISBNException f) {
            System.out.println(f.getMessage());
        }

        // Next: delete book
    }
}