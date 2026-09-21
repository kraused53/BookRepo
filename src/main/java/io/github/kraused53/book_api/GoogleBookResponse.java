package io.github.kraused53.book_api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * This class is an intermediate sstage between the google books api response and the individual google book api entries
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBookResponse {
    @JsonProperty("items")
    private List<GoogleBook> book_list;

    public List<GoogleBook> get_list() { return book_list; }
    public void set_list(List<GoogleBook> blist) { book_list = blist; }

    private int totalItems;

    int getTotalItems() { return totalItems; }
    void setTotalItems( int ti ) { totalItems = ti; }
}
