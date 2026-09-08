package io.github.kraused53.data_models.author;

/**
 * The author class stores all the information needed by the BookRepo database.
 */
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
}
