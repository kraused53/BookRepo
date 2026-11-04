package io.github.kraused53;

import java.sql.SQLException;
import java.util.DuplicateFormatFlagsException;
import java.util.List;

import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.InvalidAuthorException;
import io.github.kraused53.exceptions.InvalidBookException;
import io.github.kraused53.exceptions.InvalidISBNException;
import io.github.kraused53.models.Author;
import io.github.kraused53.models.Book;
import io.github.kraused53.service_api.ServiceApi;

public class BookRepo {
    public static void main(String[] args) throws InvalidISBNException {

        ServiceApi api = new ServiceApi();

        Book book = new Book(
            "The Disaster Gay Detective Agency",
            null,
            "9781464252853",
            400,
            """
Brandon, a nighttime hotel concierge, is a hopeless romantic.
So when a handsome stranger named Jon checks in and invites Brandon to his room, Brandon ignores the advice of his crew (the lovable but chaotic self-declared “Disaster Gays”: Ollie, Nicole, and Ian) and accepts. 
What follows is a tale as old as time: they hook up, Jon promises to text, Brandon falls in love, and Jon ghosts. Case closed—or is it? 

When Jon checks out early, leaving behind a bag of belongings, including his cellphone, Brandon takes the phone and sets out to find him. 
But he gets more than he bargained for when he witnesses a murder—and sees Jon fleeing the scene. 
Now, relying on their distinct strengths and eccentricities, Brandon, Ollie, Nicole, and Ian must work together to solve the mystery of the murder and uncover Jon’s true identity, before a target falls on their own backs.
            """    
        );

        Author author = new Author("Lev AC Rosen");
        book.addAuthor(author);

        try {
            api.insertBook(book);
        }catch (DuplicateFormatFlagsException e) {
            System.out.println("This book is already in the database");
        } catch(DuplicateEntryException e) {
            System.out.println("This book already exists in the database");
        } catch (SQLException | InvalidAuthorException | InvalidISBNException | InvalidBookException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            List<Book> books = api.fetchAllBooks(true);

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