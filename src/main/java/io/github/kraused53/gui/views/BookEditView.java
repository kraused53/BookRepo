package io.github.kraused53.gui.views;

import io.github.kraused53.exceptions.DuplicateEntryException;
import io.github.kraused53.exceptions.EntryNotFoundException;
import io.github.kraused53.models.Book;
import io.github.kraused53.service.LibraryService;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.function.Consumer;

public class BookEditView extends BorderPane {

    private final Consumer<Book> onReturnToInfo;
    private final Book book;
    private final LibraryService libraryService;

    // Editable fields
    private final TextField titleField;
    private final TextField isbn13Field;
    private final TextField isbn10Field;
    private final TextField pageCountField;
    private final TextField descriptionField;

    public BookEditView(
            LibraryService libraryService,
            Book book,
            Consumer<Book> onReturnToInfo) {

        this.book = book;
        this.onReturnToInfo = onReturnToInfo;
        this.libraryService = libraryService;

        setPadding(new Insets(20));

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(
                event -> onReturnToInfo.accept(book)
        );

        // Update Button
        Button updateButton = new Button("Update Book Info");
        updateButton.setOnAction(
                event -> updateBook()
        );

        setTop(backButton);

        // Create text fields populated with existing book information
        titleField = new TextField(book.getTitle());
        isbn13Field = new TextField(book.getIsbn13());
        isbn10Field = new TextField(book.getIsbn10());
        pageCountField = new TextField(
                String.valueOf(book.getPageCount())
        );
        descriptionField = new TextField(book.getDescription());

        // Build the form
        VBox form = new VBox(10);
        form.setPadding(new Insets(20));

        form.getChildren().addAll(
                new Label("Title"),
                titleField,

                new Label("ISBN-13"),
                isbn13Field,

                new Label("ISBN-10"),
                isbn10Field,

                new Label("Page Count"),
                pageCountField,

                new Label("Description"),
                descriptionField
        );

        setCenter(form);
        setBottom(updateButton);
    }

    private void updateBook() {
        try {
            book.setTitle(titleField.getText());
            book.setIsbn10(isbn10Field.getText());
            book.setIsbn13(isbn13Field.getText());
            book.setDescription(descriptionField.getText());
            book.setPageCount(
                    Integer.parseInt(pageCountField.getText())
            );
        } catch (NumberFormatException e) {
            System.out.println("Page count must be a valid integer.");
            return;
        }

        try {
            libraryService.updateBookInfo(book);
        } catch (SQLException | EntryNotFoundException | DuplicateEntryException e) {
            throw new RuntimeException(e);
        }
    }
}
