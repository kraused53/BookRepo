package io.github.kraused53.book_api;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBookVolumeInfo {

    private String title;
    public String getTitle() { return title; }
    public void setTitle( String t ) { title = t; }

    private List<String> authors;
    public List<String> getAuthors() { return authors; }
    public void setAuthors(List<String> author_names) { authors = author_names; }

    private List<GoogleBooksIndustryIdentifiers> industryIdentifiers;
    public List<GoogleBooksIndustryIdentifiers> getIndustryIdentifiers() { return industryIdentifiers; }
    public void setIndustryIdentifiers(List<GoogleBooksIndustryIdentifiers> ii) { industryIdentifiers = ii; }

    public String getIsbn() {
        if (industryIdentifiers == null || industryIdentifiers.isEmpty()) {
            return null;
        }

        // Promote isbn 13
        for ( GoogleBooksIndustryIdentifiers isbn : industryIdentifiers ) {
            if("ISBN_13".equals(isbn.getType())) {
                return isbn.getIdentifier();
            }
        }

        // No isbn 13, look for isbn 10
        for ( GoogleBooksIndustryIdentifiers isbn : industryIdentifiers ) {
            if("ISBN_10".equals(isbn.getType())) {
                return isbn.getIdentifier();
            }
        }

        return null;
    }
}
