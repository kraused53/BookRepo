package io.github.kraused53;

import java.util.List;

import io.github.kraused53.models.Author;
import io.github.kraused53.models.Book;
import io.github.kraused53.service_api.ServiceApi;

public class BookRepo {
    public static void main(String[] args) {

        ServiceApi api = new ServiceApi();

        try {
            List<Book> books = api.fetchBooksForAuthor(1);

            for(Book b : books) {
                System.out.println("\n---------- Book ----------");
                System.out.println(b);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}