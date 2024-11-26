package com.example.wsmapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Conteneur principal
        BorderPane root = new BorderPane();

        // Gestionnaire de navigation
        NavigationController navigationController = new NavigationController(root);

        // Charger la première étape
        navigationController.loadStep1();

        // Configurer la scène
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Méthode des Sommes Pondérées (WSM)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
