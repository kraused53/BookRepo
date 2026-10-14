package io.github.kraused53.gui.views;

import io.github.kraused53.models.Author;
import io.github.kraused53.models.Book;
import io.github.kraused53.service.LibraryService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;

import java.sql.SQLException;
import java.util.List;

public class BookGridView extends BorderPane {

    private final LibraryService libraryService;
    private final Consumer<Book> onBookSelected;

    public BookGridView(
            LibraryService libraryService,
            Runnable onBackToMenu,
            Consumer<Book> onBookSelected) {

        this.libraryService = libraryService;
        this.onBookSelected = onBookSelected;

        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f4f4f4;");

        Label heading = new Label("My Book Library");
        heading.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;"
        );

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(
                event -> onBackToMenu.run()
        );

        HBox toolbar = new HBox(15, backButton, heading);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(0, 0, 15, 0));

        FlowPane bookGrid = new FlowPane();
        bookGrid.setHgap(16);
        bookGrid.setVgap(16);
        bookGrid.setAlignment(Pos.TOP_CENTER);
        bookGrid.setPadding(new Insets(5));

        ScrollPane scrollPane = new ScrollPane(bookGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        setTop(toolbar);
        setCenter(scrollPane);

        loadBooks(bookGrid);
    }

    private void loadBooks(FlowPane bookGrid) {
        List<Book> books;

        try {
             books = libraryService.getAllBooks();
        } catch (SQLException e) {
            bookGrid.getChildren().add(
                new Label("Your library is empty.")
            );
            return;
        }

        if (books.isEmpty()) {
            bookGrid.getChildren().add(
                new Label("Your library is empty.")
            );
            return;
        }

        for (Book book : books) {
            bookGrid.getChildren().add(createBookCard(book));
        }
    }

    private VBox createBookCard(Book book) {
        Label title = new Label(book.getTitle());
        title.setWrapText(true);
        title.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;"
        );

        VBox authorsBox = new VBox(5);

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

        VBox card = new VBox(12, title, authorsBox);
        card.setPadding(new Insets(16));
        card.setPrefWidth(240);
        card.setMinHeight(130);
        card.setAlignment(Pos.TOP_CENTER);

        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: #dddddd;" +
            "-fx-border-radius: 8px;"
        );

        card.setOnMouseClicked(event -> {
            card.setOnMouseClicked(returnToGridView -> onBookSelected.accept(book));
        });

        return card;
    }
}