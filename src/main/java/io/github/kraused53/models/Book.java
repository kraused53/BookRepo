package io.github.kraused53.models;

import java.util.ArrayList;
import java.util.List;

import io.github.kraused53.exceptions.InvalidISBNException;
import io.github.kraused53.utils.ISBNValidator;

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
     * Initialize id = -1.
     */
    public Book() {
        // Set id to -1 because this is an invalid database ID. This will act as a flag that this book is client created, and not from the database.
        this.id = -1;
    }

    /*** Getters ***/

    /**
     * Return the database ID for this book
     *
     * @return Book ID {@code int}
     */
    public int getId() { return id; }

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
     * Return the page count for this book
     *
     * @return Book page count {@code int}
     */
    public int getPageCount() { return pageCount; }

    /**
     * Return the description of this book.
     *
     * @return Book description {@code String}
     */
    public String getDescription() { return description; }

    /**
     * Return a List of this book's authors
     *
     * @return List of book authors {@code List<Author>}
     */
    public List<Author> getAuthors() { return authors; }

    /*** Setters ***/

    /**
     * Set this book's id.
     *
     * @param id New book ID. {@code int}
     * @throws IllegalArgumentException If id is not valid for the database.
     */
    public void setId(int id) {
        if(id <= 0) {
            throw new IllegalArgumentException("A book's ID must be greater than zero.");
        }
        this.id = id;
    }

    /**
     * Set this book's title.
     *
     * @param title New book title. {@code String}
     * @throws IllegalArgumentException If title is null or blank.
     */
    public void setTitle(String title) { 
        if(title == null || title.isBlank()) {
            throw new IllegalArgumentException("A book's title can not be null or blank.");
        }
        this.title = title;
    }

    /**
     * Set this book's ISBN.
     *
     * @param isbn10 New book ISBN. {@code String}
     * @throws InvalidISBNException If ISBN10 is not valid.
     */
    public void setIsbn10(String isbn10) throws InvalidISBNException {
        // Check to see if the given isbn is valid. Return the isbn striped of whitespace and spacers
        // Will throw InvalidISBNException if it is not valid
        String validatedISBN = ISBNValidator.validateISBN10(isbn10);
        this.isbn10 = validatedISBN;
    }

    /**
     * Set this book's ISBN.
     *
     * @param isbn13 New book ISBN. {@code String}
     * @throws InvalidISBNException If ISBN13 is not valid.
     */
    public void setIsbn13(String isbn13) throws InvalidISBNException {
        // Check to see if the given isbn is valid. Return the isbn striped of whitespace and spacers
        // Will throw InvalidISBNException if it is not valid
        String validatedISBN = ISBNValidator.validateISBN13(isbn13);
        this.isbn13 = validatedISBN;
    }

    /**
     * Set this book's page count.
     *
     * @param pageCount New book page count. {@code int}
     * @throws IllegalArgumentException If pageCount is lessthan or equal to zero.
     */
    public void setPageCount(int pageCount) { 
        if(pageCount <= 0) {
            throw new IllegalArgumentException("A book's page count must be more than zero");
        }
        this.pageCount = pageCount;
    }

    /**
     * Set this book's description. It is optional, so there is no validation needed.
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

    /*** Utility ***/

    /**
     * Check if the book object is valid.
     *
     * @return Return true if the book is valid, and false if not {@code boolean}
     */
    public boolean isValid() {
        // The book is only valid when the title, one of the isbns, and the page count are not empty. Also, page
        //      count must be positive.

        // Validate title
        if(title == null || title.isBlank()) {
            return false;
        }

        // Validate isbns. AT LEAST one must be valid. Both is fine
        if((isbn10 == null) && (isbn13 == null)) {
            return false;
        }

        // Validate Page count
        if (pageCount <= 0) {
            return false;
        }

        return true;
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
