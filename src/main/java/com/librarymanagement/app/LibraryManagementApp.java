package com.librarymanagement.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LibraryManagementApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setMaximized(true);
        showLoginScreen();
    }

    public static void showLoginScreen() throws Exception {
        System.out.println(LibraryManagementApp.class.getResource("/GUI/Login.fxml"));
        FXMLLoader loader = new FXMLLoader(LibraryManagementApp.class.getResource("/GUI/Login.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.show();
    }

    public static void showHomeScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(LibraryManagementApp.class.getResource("../../../../resources/GUI/Home.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
    }

    public static void showAddDocumentScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(LibraryManagementApp.class.getResource("../../../../resources/GUI/AddDocument.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
