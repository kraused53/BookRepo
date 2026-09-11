package io.github.kraused53.book_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kraused53.data_models.author.Author;
import io.github.kraused53.data_models.book.Book;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * This class handles API calls to OpenLibrary that involve book information.
 */
public class BookInfo {

    /**
     * Basic Constructor
     */
    public BookInfo() {}


    public Book fetch_book_info(String isbn) throws IOException, InterruptedException {
        Book book = new Book();

        String url =
                "https://openlibrary.org/search.json?q="+ isbn +"&fields=title,author_name";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url)).GET().build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();

        OpenLibraryResponse api_response = mapper.readValue(
                response.body(),
                OpenLibraryResponse.class
        );

        List<OpenLibraryBook> results = api_response.get_list();

        if (results.isEmpty()) {
            System.out.println("Book not found.");
            return null;
        }

        // Get first book
        OpenLibraryBook apiBook = results.getFirst();

        // Fill in information
        book.setTitle(apiBook.getTitle());
        book.setIsbn(isbn);

        // Fill in authors
        for ( String a : apiBook.getAuthors() ) {
            Author author = new Author();

            author.setName(a);

            book.addAuthor(author);
        }

        return book;
    }
}
