package io.github.kraused53.data_models.book;

import io.github.kraused53.data_models.author.Author;

import java.util.ArrayList;
import java.util.List;

/**
 * The Book class holds all of the data that the BookRepo database stores about books. Methods are provided to store,
 *      update, and fetch attributes.
 *
 */
public class Book {
    /**
     * The book's database ID. Used for database actions
     */
    private int id;

    /**
     * The title of the book.
     */
    private String title;

    /**
     * The books ISBN.
     */
    private String isbn;

    /**
     * An optional description of the book.
     */
    private String description;

    /**
     * A list of author objects.
     *
     * @see Author
     */
    private final List<Author> authors = new ArrayList<>();

    /**
     * No final variables, so on initialization to default values.
     */
    public Book() {}

    /**
     * Return the title of this book.
     *
     * @return Book title {@code String}
     */
    public String getTitle() { return title; }

    /**
     * Return the ISBN for this book.
     *
     * @return Book ISBN {@code String}
     */
    public String getIsbn() { return isbn; }

    /**
     * Return the description of this book.
     *
     * @return Book description {@code String}
     */
    public String getDescription() { return description; }

    /**
     * Return the database ID for this book
     *
     * @return Book ID {@code int}
     */
    public int getId() { return id; }

    /**
     * Return a List of this book's authors
     *
     * @return List of book authors {@code List<Author>}
     */
    public List<Author> getAuthors() { return authors; }

    /**
     * Set this book's ID.
     *
     * @param id New book ID. {@code int}
     */
    public void setId(int id) { this.id = id; }

    /**
     * Set this book's title.
     *
     * @param title New book title. {@code String}
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Set this book's ISBN.
     *
     * @param isbn New book ISBN. {@code String}
     */
    public void setIsbn(String isbn) { this.isbn = isbn; }

    /**
     * Set this book's description.
     *
     * @param description New book description. {@code String}
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Add an author to this books author list.
     *
     * @param author New book author. {@code Author}
     */
    public void addAuthor(Author author) { this.authors.add(author); }

    /**
     * Override the String method for this class.
     */
    @Override
    public String toString() {
        // Forward to custom toString, indent 0
        return toString(0);
    }

    /**
     * This custom toString method allows a Book object to generate a string representation of its contents. Each line
     *     is prepended by the given number of tabs.
     *
     * @param tab The number of tabs to prepend each line {@code int}
     *
     * @return Return a string representation of this book's information {@code String}
     */
    public String toString(int tab) {
        // Define a string builder
        StringBuilder sb = new StringBuilder();

        // Generate each lines indentation
        String indent = "\t".repeat(tab);

        // Add book's information to the stringbuilder
        sb.append(indent).append("Title:\n");
        sb.append(indent).append("\t").append(title).append("\n");
        if(authors.size()==1) {
            sb.append(indent).append("Author:\n");
        }else {
            sb.append(indent).append("Authors:\n");
        }

        for (Author author : authors) {
            sb.append(indent).append("\t").append(author).append("\n");
        }

        sb.append(indent).append("ISBN:\n");

        sb.append(indent).append("\t").append(isbn).append("\n");

        if (description!=null && !description.isBlank()) {
            sb.append(indent).append("Description:\n");
            sb.append(indent).append("\t").append(description);
        }

        // Convert string builder to string and return.
        return sb.toString();
    }
}
