package io.github.kraused53.book_api;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The APIConfig class provides a safe, reliable way for the program class to access
 *     sensitive credentials in a way that does not leak secrets to version control.
 *
 * @author Daniel Krause
 * @version 1.0
 * @since 9/7/2026
 * @see BookInfo
 */
public class APIConfig {

    /**
     * Default constructor
     *
     */
    public APIConfig(){}

    /**
     * Make space to store parsed config data.
     */
    private static final Properties properties = new Properties();

    static {
        try (FileInputStream input =
                     new FileInputStream("config/api.properties")) {

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Unable to load api.properties", e);
        }
    }

    /**
     * This method returns the api URL stored in the api.properties file.
     *
     * @return Returns the stored api URL. {@code String}
     */
    public static String getUrl() {
        return properties.getProperty("api.url");
    }

    /**
     * This method returns the api key for the google books account stored in the api.properties file.
     *
     * @return Returns the stored key for the gogle books api. {@code String}
     */
    public static String getKey() {
        return properties.getProperty("api.key");
    }
}