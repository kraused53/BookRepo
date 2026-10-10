package io.github.kraused53.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The author class stores all the information needed by the BookRepo database.
 */
public class Author {

    // Define logging system
    private static final Logger logger = LoggerFactory.getLogger(Author.class);

    /**
     * The author's database ID. Used for database actions.
     */
    private int id;

    /**
     * The author's database name.
     */
    private String name;

    /**
     * No final variables, so on initialization to default values.
     */
    public Author() {}

    /**
     * Return the name of this author.
     *
     * @return Author name {@code String}
     */
    public String getName() { return name; }

    /**
     * Set this author's name.
     *
     * @param name New author name. {@code String}
     */
    public void setName(String name) { this.name = name; }

    /**
     * Return the ID of this author.
     *
     * @return Author ID {@code int}
     */
    public int getId() { return id; }

    /**
     * Set this author's ID.
     *
     * @param id New author ID. {@code int}
     */
    public void setId(int id) { this.id = id; }

    /**
     * Override the String method for this class.
     */
    @Override
    public String toString() {
        return toString(0);
    }

    /**
     * This custom toString method allows an Author object to generate a string representation of its contents. Each line
     *     is prepended by the given number of tabs.
     *
     * @param tab The number of tabs to prepend each line {@code int}
     *
     * @return Return a string representation of this author's information {@code String}
     */
    public String toString(int tab) {
        // Define new string builds
        StringBuilder sb = new StringBuilder();

        // Generate tab to prepend to each line
        String indent = "\t".repeat(tab);

        // Author information
        sb.append(indent).append(name);

        // Convert string builder to string and return
        return sb.toString();
    }

    /**
     * Check if the author object is valid.
     *
     * @return Return true if the author is valid, and false if not {@code boolean}
     */
    public boolean isValid() {

        if(name == null || name.isBlank()) {
            logger.warn("Author.isValid: Author name is not valid.");
            return false;
        }

        if(id < 1) {
            logger.warn("Author.isValid: Author id is not valid.");
            return false;
        }

        logger.debug("Author.isValid: Author is valid.");
        // The author is only valid if the name field is not null or blank
        return true;
    }
}