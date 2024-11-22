package com.librarymanagement.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LibraryManagementApp extends Application {
    private static Stage primaryStage;



    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLoginScreen();
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
        primaryStage.setScene(new Scene(loader.load()));
    }

    public static void showBorrowedDocumentsPage () throws  Exception {
        FXMLLoader loader = new FXMLLoader(LibraryManagementApp.class.getResource("/FXML/UserFXML/BorrowedPage.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
    }

    public static void showAccountSetting() throws Exception {
        FXMLLoader loader = new FXMLLoader(LibraryManagementApp.class.getResource("/FXML/UserFXML/AccountSetting.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
    }

    //Admins' Screen

    public static void showAdminPage() throws Exception {
        FXMLLoader loader = new FXMLLoader(LibraryManagementApp.class.getResource("/FXML/AdminFXML/HomeAdminPage.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
    }

    public static void showManageDocumentPage() throws Exception {
        FXMLLoader loader = new FXMLLoader(LibraryManagementApp.class.getResource("/FXML/AdminFXML/ManageDocumentPage.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
    }

    public static void showManageUserPage() throws Exception {
        FXMLLoader loader = new FXMLLoader(LibraryManagementApp.class.getResource("/FXML/AdminFXML/ManageUserPage.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
    }



    public static void main(String[] args) {
        launch(args);
    }
}
