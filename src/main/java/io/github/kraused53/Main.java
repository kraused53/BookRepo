package io.github.kraused53;

import io.github.kraused53.models.Author;
import io.github.kraused53.models.Book;
import io.github.kraused53.repository.author.AuthorRepository;
import io.github.kraused53.repository.book.BookRepository;
import io.github.kraused53.repository.book_authors.BookAuthorsRepository;
import io.github.kraused53.service.library.LibraryService;

import java.sql.SQLException;
import java.util.List;

/**
 * Program execution begins here.
 */
public class Main {

    /**
     * Program execution begins here.
     */
    static void main() {

        // Database access
        AuthorRepository authorRepository = new AuthorRepository();
        BookRepository bookRepository = new BookRepository();
        BookAuthorsRepository bookAuthorsRepository = new BookAuthorsRepository();

        // LibraryService API manager
        LibraryService library = new LibraryService(bookRepository, authorRepository, bookAuthorsRepository);

        List<Book> books = getAllBooks(library);

        System.out.println("Books in library:");
        for(Book b : books) {
            System.out.println("\n==============================");
            System.out.println(b.toString(1));
        }
    }

    private static List<Book> getAllBooks(LibraryService library) {
        List<Book> books;

        try {
            books = library.fetchAllBooksWithAuthors();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return books;
    }

}
