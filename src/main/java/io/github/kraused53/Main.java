package io.github.kraused53;

import io.github.kraused53.data_models.author.Author;
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

        Author author1 = new Author();
        author1.setName("Dominik Parisien");

        Author author2 = new Author();
        author2.setName("Navah Wolfe");

        Book book = new Book();
        book.setTitle("Robots vs. Fairies");
        book.setIsbn("9781481462358");
        book.addAuthor(author1);
        book.addAuthor(author2);
        book.setDescription("Rampaging robots! Tricksy fairies! Facing off for the first time in an epic genre death match! People love pitting two awesome things against each other. Robots vs. Fairies is an anthology that pitches genre against genre, science fiction against fantasy, through an epic battle of two icons. On one side, robots continue to be the classic sci-fi phenomenon in literature and media, from Asimov to WALL-E, from Philip K. Dick to Terminator. On the other, fairies are the beloved icons and unquestionable rulers of fantastic fiction, from Tinkerbell to Tam Lin, from True Blood to Once Upon a Time. Both have proven to be infinitely fun, flexible, and challenging. But when you pit them against each other, which side will triumph as the greatest genre symbol of all time? There can only be one…or can there? Featuring an incredible line-up of authors including John Scalzi, Catherynne M. Valente, Ken Liu, Max Gladstone, Alyssa Wong, Jonathan Maberry, and many more, Robots vs. Fairies will take you on a glitterbombed journey of a techno-fantasy mash-up across genres.");

        dbook.save(book);

    }
}
