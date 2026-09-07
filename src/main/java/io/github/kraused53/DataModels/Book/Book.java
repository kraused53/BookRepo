package io.github.kraused53.DataModels.Book;

import io.github.kraused53.DataModels.Author.Author;

import java.util.ArrayList;
import java.util.List;

public class Book {
    private  int id;
    private String title;
    private String isbn;
    private String description;
    private final List<Author> authors = new ArrayList<>();

    public Book() {}

    public String getTitle() { return title; }
    public String getIsbn() { return isbn; }
    public String getDescription() { return description; }
    public int getId() { return id; }
    public List<Author> getAuthors() { return authors; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setDescription(String description) { this.description = description; }
    public void addAuthor(Author author) { this.authors.add(author); }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int tab) {
        StringBuilder sb = new StringBuilder();

        String indent = "\t".repeat(tab);

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

        /*
        if (isbn.length() == 13) {
            sb.append(indent).append("\t")
                    .append(isbn.substring(0,3)).append("-")
                    .append(isbn.substring(3,4)).append("-")
                    .append(isbn.substring(4,6)).append("-")
                    .append(isbn.substring(6,12)).append("-")
                    .append(isbn.substring(12,13))
                    .append("\n");
        } else if (isbn.length() == 10) {
            sb.append(indent).append("\t")
                    .append(isbn.substring(0,1)).append("-")
                    .append(isbn.substring(1,4)).append("-")
                    .append(isbn.substring(4,9)).append("-")
                    .append(isbn.substring(9,10))
                    .append("\n");
        }else {
        */
        sb.append(indent).append("\t").append(isbn).append("\n");


        if (description!=null && !description.isBlank()) {
            sb.append(indent).append("Description:\n");
            sb.append(indent).append("\t").append(description);
        }

        return sb.toString();
    }
}
