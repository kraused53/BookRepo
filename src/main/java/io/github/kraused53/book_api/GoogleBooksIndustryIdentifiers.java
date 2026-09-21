package io.github.kraused53.book_api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBooksIndustryIdentifiers {

    private String type;

    public String getType() { return type; }
    public void setType(String t) { type = t; }

    private String identifier;

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String i) { identifier = i; }
}
