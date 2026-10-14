package io.github.kraused53.gui.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MainMenuView extends VBox {

    public MainMenuView(Runnable onOpenLibrary) {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setStyle("-fx-background-color: #f4f4f4;");

        Label heading = new Label("My Book Library");
        heading.setStyle(
            "-fx-font-size: 30px;" +
            "-fx-font-weight: bold;"
        );

        Button libraryButton = new Button("View My Books");
        libraryButton.setPrefWidth(200);
        libraryButton.setPrefHeight(45);

        libraryButton.setOnAction(event -> onOpenLibrary.run());

        getChildren().addAll(heading, libraryButton);
    }
}