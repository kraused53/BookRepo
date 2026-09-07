package io.github.kraused53;

import io.github.kraused53.DataModels.Book.Book;
import io.github.kraused53.Database.DatabaseBook;

import java.sql.SQLException;
import java.util.List;

public class Main {
    static void main() throws SQLException {
        System.out.println("Daniel's Book Repository Project:");

        DatabaseBook dbook = new DatabaseBook();

        List<Book> books = dbook.getAllBooks();

        for (Book book : books) {
            System.out.println("\n"+book.toString(1));
        }


    }
}
