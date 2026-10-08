package io.github.kraused53.repository.author;

import io.github.kraused53.models.Author;
import io.github.kraused53.database.Database;
import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the middle step between the database and the rest of the program. This is where all of the SQL
 *      magic happens.
 */
public class AuthorRepository {

    /**
     * Nothing to prepare when this class is instantiated.
     */
    public AuthorRepository() {}

    /**
     * Fetch all of the authors in the database and return them as a list of Author objects.
     *
     * @return A list of all authors present in the database.
     * @exception SQLException An SQLException might be raised by the authorRepository if there is an issues
     *      interfacing with the database. Simply pass it down the chain.
     *
     * @see io.github.kraused53.service.AuthorService
     */
    public List<Author> findAll() throws SQLException {
        // Define list of results. If no authors are found, this list wll remain empty.
        List<Author> result = new ArrayList<Author>();

        try(Connection conn = Database.getConnection()) {
            // This query will pull all entries from the authors table
            String query = "SELECT id, name FROM authors";

            // Prepare the sql statement
            PreparedStatement stmt = conn.prepareStatement(query);

            // Execute the query
            ResultSet rs = stmt.executeQuery();

            // Iterate through every item in the result set
            while(rs.next()) {
                // Pull relevant information
                int id = rs.getInt("id");
                String name = rs.getString("name");

                // Create new author object and fill in information
                Author author = new Author();
                author.setId(id);
                author.setName(name);

                // Add this author to the result list
                result.add(author);
            }

        }

        return result;
    }

    /**
     * Fetch an author by their ID
     *
     * @param authorId Requested ID. Use this to search the database
     *
     * @return An author found by the given id. Returns null if no author is found
     *
     * @exception SQLException An SQLException might be raised by the authorRepository if there is an issues
     *      interfacing with the database. Simply pass it down the chain.
     *
     * @exception EntryNotFoundException Thrown if the system fails to find an author with the given id.
     *
     * @see io.github.kraused53.service.AuthorService
     */
    public Author findById(int authorId) throws SQLException, EntryNotFoundException {
        try(Connection conn = Database.getConnection()) {
            // This query will pull the author(s) corresponding with this ID
            String query = "SELECT name FROM authors WHERE id = ?";

            // Prepare the sql statement
            PreparedStatement stmt = conn.prepareStatement(query);

            // Fill in the author ID
            stmt.setInt(1, authorId);

            // Execute the query
            ResultSet rs = stmt.executeQuery();

            // There should only be one author per ID. This layer should not try and validate this
            // Only pull data from the first result.
            if(rs.next()) {
                // Pull relevant information
                String name = rs.getString("name");

                // Fill in the information
                Author author = new Author();
                author.setId(authorId);
                author.setName(name);

                return author;
            }else {
                throw new EntryNotFoundException("No author was found with id: "+authorId);
            }

        }
    }

    /**
     * Insert a new author into the database
     *
     * @param author The author to add to the database
     *
     * @exception SQLException An SQLException might be raised by the authorRepository if there is an issues
     *      interfacing with the database. Simply pass it down the chain.
     *
     * @see io.github.kraused53.service.AuthorService
     */
    public void insert(Author author) throws SQLException, DuplicateEntryException {
        try(Connection conn = Database.getConnection()) {
            // This query will pull the author(s) corresponding with this ID
            String query = "INSERT INTO authors (name) VALUES (?)";

            // Prepare the sql statement
            PreparedStatement stmt = conn.prepareStatement(query);

            // Fill in author name
            stmt.setString(1, author.getName());

            // Execute the query
            stmt.executeUpdate();
        }catch (SQLException e) {
            if(e.getErrorCode() == 1062) {
                throw new DuplicateEntryException("An author with this name already exists in the database!");
            }else {
                throw e;
            }
        }
    }

    /**
     * Update the name of an existing author
     *
     * @param authorId The id of the author to update
     * @param newName The new name to apply to the author with the given ID
     * @throws SQLException if the database operation fails
     * @throws DuplicateEntryException if the new name matches a name already in the authors table
     * @throws EntryNotFoundException if the id does not match the id of an author in the table
     *
     */
    public void update(int authorId, String newName) throws SQLException, DuplicateEntryException, EntryNotFoundException{
        try(Connection conn = Database.getConnection()) {
            // This query will update the name at the given id
            String query = "UPDATE authors SET name = ? WHERE id = ?";

            // Prepare the query
            PreparedStatement stmt = conn.prepareStatement(query);

            // Fill in query variables
            stmt.setString(1, newName);
            stmt.setInt(2, authorId);

            // Execute the query
            int rows = stmt.executeUpdate();

            // If zero rows are effected, then the WHERE statement resulted in zero entries
            if(rows == 0) {
                throw new EntryNotFoundException("An author with this ID was not found in the database!");
            }
        }catch (SQLException e) {
            switch (e.getErrorCode()) {
                case 1062: throw new DuplicateEntryException("An author with this name already exists in the database!");
                case 1048: throw new IllegalArgumentException("An authors name can not be null");
                default: throw e;
            }
        }
    }

    /**
     * Delete an author using their id
     *
     * @param authorId The id of the author to delete
     * @throws SQLException if the database operation fails
     * @throws DuplicateEntryException if the new name matches a name already in the authors table
     * @throws EntryNotFoundException if the id does not match the id of an author in the table
     *
     */
    public void delete(int authorId) throws EntryNotFoundException, SQLException {
        try(Connection conn = Database.getConnection()) {
            // This query will update the name at the given id
            String query = "DELETE FROM authors WHERE id = ?";

            // Prepare the query
            PreparedStatement stmt = conn.prepareStatement(query);

            // Fill in query variables
            stmt.setInt(1, authorId);

            // Execute the query
            int rows = stmt.executeUpdate();

            // If zero rows are effected, then the WHERE statement resulted in zero entries
            if(rows == 0) {
                throw new EntryNotFoundException("An author with this ID was not found in the database!");
            }
        }
    }
}
