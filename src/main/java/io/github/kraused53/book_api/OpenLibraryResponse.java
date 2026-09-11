package io.github.kraused53.book_api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * This class is an intermediate step to map The OpenLibrary json response into  a format usable by the rest of the Book system.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLibraryResponse {

    @JsonProperty("docs")
    private List<OpenLibraryBook> book_list;

    private int numFound;

    private int getNumFound() { return numFound; }
    private void setNumFound( int nf ) { numFound = nf; }

    public List<OpenLibraryBook> get_list() { return book_list; }

    public void set_list(List<OpenLibraryBook> blist) { book_list = blist; }
}
