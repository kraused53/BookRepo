package io.github.kraused53.gui;

import io.github.kraused53.gui.views.BookEditView;
import io.github.kraused53.gui.views.BookGridView;
import io.github.kraused53.gui.views.MainMenuView;
import io.github.kraused53.gui.views.BookInfoView;
import io.github.kraused53.models.Book;
import io.github.kraused53.repository.author.AuthorRepository;
import io.github.kraused53.repository.book.BookRepository;
import io.github.kraused53.repository.book_authors.BookAuthorsRepository;
import io.github.kraused53.service.LibraryService;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LibraryApp extends Application {

    // Database access
    AuthorRepository authorRepository = new AuthorRepository();
    BookRepository bookRepository = new BookRepository();
    BookAuthorsRepository bookAuthorsRepository = new BookAuthorsRepository();

    // Service API
    private LibraryService libraryService;

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        libraryService = new LibraryService(bookRepository, authorRepository, bookAuthorsRepository);

        primaryStage.setTitle("My Book Library");

        showMainMenu();
        primaryStage.show();
    }

    private void showMainMenu() {
        MainMenuView mainMenu = new MainMenuView(
                this::showBookGrid
        );

        primaryStage.setScene(
                new Scene(mainMenu, 900, 650)
        );
    }

    private void showBookGrid() {
        BookGridView bookGrid = new BookGridView(
                libraryService,
                this::showMainMenu,
                this::showBookInfo
        );

        primaryStage.setScene(
                new Scene(bookGrid, 900, 650)
        );
    }

    private void showEditBook(Book book) {
        BookEditView bookEdit = new BookEditView(
                libraryService,
                book,
                this::showBookInfo
        );

        primaryStage.setScene(
                new Scene(bookEdit, 900, 650)
        );
    }

    private void showBookInfo(Book book) {
        BookInfoView bookInfo = new BookInfoView(
                book,
                this::showBookGrid,
                this::showEditBook
        );

        primaryStage.setScene(
                new Scene(bookInfo, 900, 650)
        );
    }


    public static void main(String[] args) {
        launch(args);
    }
}