package io.github.kraused53.book_api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * This class stores information about books found using the google books api
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBook {
    @JsonProperty("volumeInfo")
    GoogleBookVolumeInfo volumeInfo = new GoogleBookVolumeInfo();

    public String getTitle() { return volumeInfo.getTitle(); }
    public void setTitle(String title) { volumeInfo.setTitle(title); }

    public List<String> getAuthors() { return volumeInfo.getAuthors(); }
    public void setAuthors( List<String> a ) { volumeInfo.setAuthors( a ); }

    public String getIsbn() { return volumeInfo.getIsbn(); }
}
