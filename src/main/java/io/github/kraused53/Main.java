package io.github.kraused53;

import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.models.Author;
import io.github.kraused53.models.Book;
import io.github.kraused53.repository.author.AuthorRepository;
import io.github.kraused53.repository.book.BookRepository;
import io.github.kraused53.repository.book_authors.BookAuthorsRepository;
import io.github.kraused53.service.LibraryService;

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
        printAllBooks(books);

        Author author = new Author();
        author.setName("J.R.R. Tolkien");

        Book book = new Book();

        book.setTitle("The Hobbit");
        book.setIsbn10("0345339681");
        book.setIsbn13("9780345339683");
        book.setDescription("Bilbo Baggins is a hobbit who enjoys a comfortable, unambitious life, rarely traveling any farther than his pantry or cellar. But his contentment is disturbed when the wizard Gandalf and a company of dwarves arrive on his doorstep one day to whisk him away on an adventure. They have launched a plot to raid the treasure hoard guarded by Smaug the Magnificent, a large and very dangerous dragon. Bilbo reluctantly joins their epic quest, unaware that on his journey to the Lonely Mountain he will encounter both a magic ring and a frightening creature known as Gollum.");
        book.setPageCount(320);
        book.addAuthor(author);

        book.isValid();
    }

    private static List<Book> getAllBooks(LibraryService library) {
        List<Book> books;

        try {
            books = library.getAllBooks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return books;
    }

    private static void printAllBooks(List<Book> books) {
        System.out.println("Library:");

        if(books == null || books.isEmpty()) {
            System.out.println("\t<empty>");
            return;
        }

        for(Book b : books) {
            System.out.println("\n==================================================");
            System.out.println(b.toString(1));
        }
    }
}
