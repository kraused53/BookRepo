package io.github.kraused53;


import io.github.kraused53.models.Author;
import io.github.kraused53.repository.author.AuthorRepository;
import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.service.AuthorService;

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

        // Database access related to Authors
        AuthorRepository authorRepository = new AuthorRepository();

        // Business actions related to authors
        AuthorService authorService = new AuthorService(authorRepository);

        // Fetch and print all authors
        List<Author> allAuthors = null;

        // Existing authors
        printAllAuthors(authorService);

        // Add new author
        Author newAuthor = new Author();
        newAuthor.setName("Jim Bob");

        System.out.println("Test add author");
        try {
            authorService.addAuthor(newAuthor);
        } catch (SQLException add_error) {
            throw new RuntimeException(add_error);
        } catch (DuplicateEntryException add_error) {
            System.out.println(add_error.getMessage());;
        }

        printAllAuthors(authorService);

        try {
            allAuthors = authorService.getAllAuthors();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        int updateId = 0;
        for (Author a : allAuthors) {
            if (a.getName().equals("Jim Bob")) {
                updateId = a.getId();
                break;
            }
        }


        System.out.println("Test update author");
        if(updateId == 0) {
            System.out.println("Error!");
        }else {
            // Update Jim Bob to Tommy boy
            try {
                authorService.updateAuthor(updateId, "Tommy boy");
            } catch (SQLException update_error) {
                throw new RuntimeException(update_error);
            } catch (DuplicateEntryException | EntryNotFoundException update_error) {
                System.out.println(update_error.getMessage());
            }

            printAllAuthors(authorService);
        }


        System.out.println("Test delete author");
        // Delete Tommy boy
        if(updateId == 0) {
            System.out.println("Error!");
        }else {
            // Update Jim Bob to Tommy boy
            try {
                authorService.deleteAuthor(updateId);
            } catch (SQLException update_error) {
                throw new RuntimeException(update_error);
            } catch (DuplicateEntryException | EntryNotFoundException update_error) {
                System.out.println(update_error.getMessage());
            }

            printAllAuthors(authorService);
        }


    }

    private static void printAllAuthors(AuthorService authorService) {
        List<Author> allAuthors;
        try {
            allAuthors = authorService.getAllAuthors();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("All Database Authors:");
        if(allAuthors.isEmpty()) {
            System.out.println("\t<empty>");
        }else {
            for(Author a : allAuthors) {
                System.out.println(a.toString(1));
            }
        }

        System.out.println("\n");
    }

}
