package io.github.kraused53.service.author;

import io.github.kraused53.repository.author.AuthorRepository;
import io.github.kraused53.models.Author;
import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;

import java.sql.SQLException;
import java.util.List;

/**
 * This class is a middle step between the user interface and the database access. All of the Author related actions
 *      will be enumerated here, and each will use the authorRepository object to access the database.
 */
public class AuthorService {

    /**
     * This AuthorRepository object will be how the AuthorService class interacts with the database
     */
    private final AuthorRepository authorRepository;

    /**
     * The author repository class will be instantiated elsewhere and then connected here
     */
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    /**
     * Fetch all of the authors in the database and return them as a list of Author objects.
     *
     * @return A list of all authors present in the database.
     * @throws SQLException if the database operation fails
     *
     * @see AuthorRepository
     */
    public List<Author> getAllAuthors() throws SQLException {
        return authorRepository.findAll();
    }

    /**
     * Fetch a specific author in the database by their ID and return an Author object.
     *
     * @param id Requested ID. Use this to search the database
     *
     * @return The author with the corresponding ID.
     * @throws SQLException if the database operation fails
     * @throws IllegalArgumentException if the given id is invalid
     *
     * @see AuthorRepository
     */
    public Author findById(int id) throws SQLException, EntryNotFoundException {
        // Input sanitation
        if(id <= 0) {
            throw new IllegalArgumentException("Author id must be greater than zero!");
        }

        return authorRepository.findById(id);
    }

    /**
     * Insert a new author into the database
     *
     * @param author The author to add to the database
     * @throws SQLException if the database operation fails
     * @see AuthorRepository
     */
    public void addAuthor(Author author) throws SQLException, DuplicateEntryException {
        // If the author is valid, no need to continue
        if(!author.isValid()) {
            throw new IllegalArgumentException("This author name is not valid!");
        }

        authorRepository.insert(author);

    }

    /**
     * Update the name of an existing author
     *
     * @param authorId The id of the author to update
     * @param newName The new name to apply to the author with the given ID
     *
     * @see AuthorRepository
     */
    public void updateAuthor(int authorId, String newName) throws SQLException, DuplicateEntryException, EntryNotFoundException {
        if(authorId <= 0) {
            throw new IllegalArgumentException("Author id must be greater than zero!");
        }

        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("An author's name can not be null or empty");
        }
        authorRepository.update(authorId, newName);
    }

    public void deleteAuthor(int authorId) throws SQLException, EntryNotFoundException, DuplicateEntryException {
        if(authorId <= 0) {
            throw new IllegalArgumentException("Author id must be greater than zero!");
        }

        authorRepository.delete(authorId);
    }
}
