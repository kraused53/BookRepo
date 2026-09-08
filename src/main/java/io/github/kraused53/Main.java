package io.github.kraused53;

import io.github.kraused53.data_models.book.Book;
import io.github.kraused53.database.DatabaseBook;

import java.sql.SQLException;
import java.util.List;

/**
 * Program execution begins here.
 */
public class Main {

    /**
     * Program execution begins here.
     */
    static void main() throws SQLException {
        System.out.println("Daniel's Book Repository Project:");

        DatabaseBook dbook = new DatabaseBook();

        List<Book> books = dbook.getAllBooks();

        for (Book book : books) {
            System.out.println("\n"+book.toString(1));
        }


    }
}
