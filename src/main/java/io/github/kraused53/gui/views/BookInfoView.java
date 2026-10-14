
package io.github.kraused53.gui.views;

import io.github.kraused53.models.Book;
import io.github.kraused53.models.Author;

import io.github.kraused53.service.LibraryService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class BookInfoView extends BorderPane {

    private final Consumer<Book> goToEditBook;

    public BookInfoView(Book book, Runnable onBack, Consumer<Book> goToEditBook) {
        setPadding(new Insets(25));
        setStyle("-fx-background-color: #f4f4f4;");

        this.goToEditBook = goToEditBook;

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> onBack.run());

        // Edit button
        Button editButton = new Button("Edit Book");
        editButton.setOnAction(event -> goToEditBook.accept(book));

        // Book title
        Label title = new Label(book.getTitle());
        title.setWrapText(true);
        title.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;"
        );

        // Book information
        VBox infoBox = new VBox(12);

        infoBox.getChildren().addAll(
                createInfoLabel("ISBN-13", book.getIsbn13()),
                createInfoLabel("ISBN-10", book.getIsbn10()),
                createInfoLabel("Page Count",
                        String.valueOf(book.getPageCount())),
                createInfoLabel("Description", book.getDescription())
        );

        // Authors
        Label authorsHeading = new Label("Authors");
        authorsHeading.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        );

        VBox authorsBox = new VBox(6);

        for (Author author : book.getAuthors()) {
            Label authorLabel = new Label(author.getName());
            authorLabel.setWrapText(true);
            authorsBox.getChildren().add(authorLabel);
        }

        if (book.getAuthors().isEmpty()) {
            authorsBox.getChildren().add(
                    new Label("No authors listed")
            );
        }

        VBox content = new VBox(
                20,
                title,
                infoBox,
                authorsHeading,
                authorsBox
        );
        content.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);

        setTop(backButton);
        setCenter(scrollPane);
        setBottom(editButton);
    }

    private Label createInfoLabel(String heading, String value) {
        Label label = new Label(
                heading + ": " +
                        (value == null || value.isBlank() ? "Not available" : value)
        );

        label.setWrapText(true);
        return label;
    }
}
