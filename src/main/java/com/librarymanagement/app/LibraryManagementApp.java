package com.librarymanagement.app;

import com.librarymanagement.UI.BookDetailsController;
import com.librarymanagement.UI.ImageLoader;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

public class LibraryManagementApp extends Application {
    private static Stage primaryStage;
    private static User currentUser;
    private static Stack<Scene> scenesHistory = new Stack<>();

    public static void goBack(){
        primaryStage.setScene(scenesHistory.pop());
        scenesHistory.add(primaryStage.getScene());
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLoginScreen();
    }

    @Override
    public void stop() {
        ImageLoader.shutdown();
        Platform.exit();
        System.out.println("Application is stopping...");
        // Perform cleanup actions here
    }

    public static void showLoginScreen() throws Exception {
        System.out.println(LibraryManagementApp.class.getResource("/FXML/Login.fxml"));
        FXMLLoader loader = new FXMLLoader(LibraryManagementApp.class.getResource("/FXML/Login.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.show();
    }

    //Users' Screens

    public static void showHomeScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(LibraryManagementApp.class.getResource("/FXML/UserFXML/HomeUserPage.fxml"));
        Scene newScene = new Scene(loader.load());
        scenesHistory.add(newScene);
        primaryStage.setScene(newScene);
    }

    public static void showBorrowedDocumentsPage() throws Exception {
        FXMLLoader loader = new FXMLLoader(LibraryManagementApp.class.getResource("/FXML/UserFXML/BorrowedPage.fxml"));
        Scene newScene = new Scene(loader.load());
        scenesHistory.add(newScene);
        primaryStage.setScene(newScene);
    }

    public static void showBookDetailsPage(Scene sence) throws Exception {
        primaryStage.setScene(sence);
    }

    //Admins' Screen

    public static void showAdminPage() throws Exception {
        FXMLLoader loader = new FXMLLoader(LibraryManagementApp.class.getResource("/FXML/AdminFXML/HomeAdminPage.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
    }

    public static void showManageDocumentPage() throws Exception {
        FXMLLoader loader = new FXMLLoader(LibraryManagementApp.class.getResource("/FXML/AdminFXML/ManageDocumentPage.fxml"));
        Scene newScene = new Scene(loader.load());
        scenesHistory.add(newScene);
        primaryStage.setScene(newScene);
    }

    public static void showManageUserPage() throws Exception {
        FXMLLoader loader = new FXMLLoader(LibraryManagementApp.class.getResource("/FXML/AdminFXML/ManageUserPage.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
    }

    //User setter
    public static void setCurrentUser(User loggedInUser) {
        currentUser = loggedInUser;
    }

    //User getter
    public static User getCurrentUser() {
        return currentUser;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
