package io.github.kraused53.models;

public class Author {

    /**
     * The author's database ID. Used for database actions.
     */
    private int id;

    /**
     * The author's database name.
     */
    private String name;

    /**
     * Initialize id = -1.
     */
    public Author() {
        // Set id to -1 because this is an invalid database ID. This will act as a flag that this author is client created, and not from the database.
        this.id = -1;
    }

    /**
     *  Setup an author with a name
     *
     * @param name The name of the author
     */
    public Author(String name) {
        // Set id to -1 because this is an invalid database ID. This will act as a flag that this author is client created, and not from the database.
        this.id = -1;

        this.name = name;
    }

    /*** Getters ***/

    /**
     * Return the ID of this author.
     *
     * @return Author ID {@code int}
     */
    public int getId() { return id; }

    /**
     * Return the name of this author.
     *
     * @return Author name {@code String}
     */
    public String getName() { return name; }

    /*** Setters ***/

    /**
     * Set this author's id.
     *
     * @param id New author ID. {@code int}
     * @throws IllegalArgumentException If id is not valid for the database.
     */
    public void setId(int id) {
        if(id <= 0) {
            throw new IllegalArgumentException("An author's ID must be greater than zero.");
        }
        this.id = id;
    }

    /**
     * Set this author's name.
     *
     * @param name New author name. {@code String}
     * @throws IllegalArgumentException If name is null or blank.
     */
    public void setName(String name) { 
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("An author's name can not be null or blank.");
        }
        this.name = name;
    }

    /*** Utility ***/

    /**
     * Check if the author object is valid.
     *
     * @return Return true if the author is valid, and false if not {@code boolean}
     */
    public boolean isValid() {

        if(name == null || name.isBlank()) {
            return false;
        }
        
        // The author is only valid if the name field is not null or blank
        return true;
    }

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
}
