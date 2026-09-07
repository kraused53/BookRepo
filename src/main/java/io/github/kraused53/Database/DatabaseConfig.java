package io.github.kraused53.Database;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConfig {

    private static final Properties properties = new Properties();

    static {
        try (FileInputStream input =
                     new FileInputStream("config/database.properties")) {

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Unable to load database.properties", e);
        }
    }

    public static String getUrl() {
        return properties.getProperty("db.url");
    }

    public static String getUsername() {
        return properties.getProperty("db.username");
    }

    public static String getPassword() {
        return properties.getProperty("db.password");
    }
}