package io.github.kraused53;

import io.github.kraused53.book_api.BookInfo;
import io.github.kraused53.data_models.author.Author;
import io.github.kraused53.data_models.book.Book;
import io.github.kraused53.database.DatabaseBook;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Program execution begins here.
 */
public class Main {

    /**
     * Program execution begins here.
     */
    static void main() throws SQLException, IOException, InterruptedException {
        System.out.println("Daniel's Book Repository Project:");

        DatabaseBook dbook = new DatabaseBook();
        BookInfo book_info = new BookInfo();

        // Add a new book to the library
        Book book = book_info.fetch_book_info("9780316129084");

        if (book == null || !book.isValid()) {
            System.out.println("Not a valid book!");
        }else {
            dbook.save(book);
        }
        System.out.println("\n");

        // Fetch a list of all books in the database and print them
        List<Book> books = dbook.getAllBooks();

        for (Book b : books) {
            System.out.println("\n"+b.toString(1));
        }

    }
}
