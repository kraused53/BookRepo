package io.github.kraused53;

import java.sql.SQLException;
import java.util.List;

import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.models.Book;
import io.github.kraused53.repository.BookRepository;

public class BookRepo {
    public static void main(String[] args) {

        BookRepository bookRepository = new BookRepository();
        List<Book> books;

        try {
            books = bookRepository.fetchAll();
        } catch (SQLException e) {
            throw new RuntimeException("There was an error accessing the database!"+e);
        }

        for(Book b : books) {
            System.out.println(b+"\n");
        }

        try {
            System.out.println(bookRepository.fetchById(1)+"\n");
            System.out.println(bookRepository.fetchById(2)+"\n");
            System.out.println(bookRepository.fetchById(3)+"\n");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException("There was an error fetching the book!" + e);
        } catch (EntryNotFoundException f) {
            System.out.println(f.getMessage());
        }
    }
}