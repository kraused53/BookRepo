package io.github.kraused53.models;

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
     * The book's ISBN - 10.
     */
    private String isbn10;

    /**
     * The book's ISBN - 13.
     */
    private String isbn13;

    /**
     * The book's page count
     */
    private int pageCount;

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
     * Return the ISBN 10 for this book.
     *
     * @return Book ISBN {@code String}
     */
    public String getIsbn10() { return isbn10; }

    /**
     * Return the ISBN 10 for this book.
     *
     * @return Book ISBN {@code String}
     */
    public String getIsbn13() { return isbn13; }

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
     * Return the page count for this book
     *
     * @return Book page count {@code int}
     */
    public int getPageCount() { return pageCount; }

    /**
     * Return a List of this book's authors
     *
     * @return List of book authors {@code List<Author>}
     */
    public List<Author> getAuthors() { return authors; }

    /**
     * Set this book's page count.
     *
     * @param pageCount New book page count. {@code int}
     */
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    /**
     * Set this book's page count.
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
     * @param isbn_10 New book ISBN. {@code String}
     */
    public void setIsbn10(String isbn_10) { this.isbn10 = isbn_10; }

    /**
     * Set this book's ISBN.
     *
     * @param isbn_13 New book ISBN. {@code String}
     */
    public void setIsbn13(String isbn_13) { this.isbn13 = isbn_13; }

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
     * Check if the book object is valid.
     *
     * @return Return true if the book is valid, and false if not {@code boolean}
     */
    public boolean isValid() {
        // The book is only valid when the title, one of the isbns, and the page count are not empty. Also, page
        //      count must be positive

        // Validate title
        if(title == null || title.isBlank()) {
            return false;
        }

        // Validate isbns. AT LEAST one must be valid. Both is fine
        if(!isIsbn10Valid() && !isIsbn13Valid()) {
            return false;
        }

        if (pageCount <= 0) {
            return false;
        }

        return true;
    }

    /**
     *  Return true if isbn10 is valid
     */
    public boolean isIsbn10Valid() {
        return (isbn10 != null) && (!isbn10.isBlank()) && (isbn10.length() != 10);
    }

    /**
     *  Return true if isbn13 is valid
     */
    public boolean isIsbn13Valid() {
        return (isbn13 != null) && (!isbn13.isBlank()) && (isbn13.length() != 13);
    }

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

        if(isbn10 != null && !isbn10.isBlank() && isbn13 != null && !isbn13.isBlank()) {
            sb.append(indent).append("ISBNS:\n");
        }else {
            sb.append(indent).append("ISBN:\n");
        }

        if(isbn10 != null && !isbn10.isBlank()) {
            sb.append(indent).append("\tISBN-10: ").append(isbn10).append("\n");
        }

        if(isbn13 != null && !isbn13.isBlank()) {
            sb.append(indent).append("\tISBN-13: ").append(isbn13).append("\n");
        }

        if (description!=null && !description.isBlank()) {
            sb.append(indent).append("Description:\n");
            sb.append(indent).append("\t").append(description);
        }

        // Convert string builder to string and return.
        return sb.toString();
    }
}