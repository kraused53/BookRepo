package io.github.kraused53.book_api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * This class stores information for the OpenLibraryResponse class
 */
public class OpenLibraryBook {

    private String title;

    @JsonProperty("author_name")
    private List<String> author_names;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getAuthors() { return author_names; }

    public void setAuthors(List<String> author_names) { this.author_names = author_names; }
}
