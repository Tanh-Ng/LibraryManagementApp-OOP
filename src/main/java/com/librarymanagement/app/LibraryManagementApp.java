package com.librarymanagement.app;

import com.librarymanagement.UI.General.ImageLoader;
import com.librarymanagement.UI.General.ManageBorrowController;
import com.librarymanagement.UI.UserUI.HomePageUserController;
import com.librarymanagement.model.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Stack;

public class LibraryManagementApp extends Application {
    private static Stage primaryStage;
    private static User currentUser;
    private static Stack<Scene> scenesHistory = new Stack<>();

    public static void goBack(){
        primaryStage.setScene(scenesHistory.pop());
    }
  
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLoginScreen();

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            confirmExit();
        });
    }

    @Override
    public void stop() {
        ImageLoader.shutdown();
        Platform.exit();
        System.out.println("Application is stopping...");
        // Perform cleanup actions here
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ManageBorrowController.stopExpirationCheck();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
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
        Scene scene = new Scene(loader.load());
        HomePageUserController controller = loader.getController();
        controller.setIsRefresh(true);
        primaryStage.setScene(scene);
    }

    public static void showBorrowedDocumentsPage() throws Exception {
        FXMLLoader loader = new FXMLLoader(LibraryManagementApp.class.getResource("/FXML/UserFXML/BorrowedPage.fxml"));
        Scene newScene = new Scene(loader.load());
        scenesHistory.add(newScene);
        primaryStage.setScene(newScene);
    }

    public static void showBookDetailsPage(Scene scene) throws Exception {
        scenesHistory.add(primaryStage.getScene());
        primaryStage.setScene(scene);
    }

    public static void showBookByTypePage(Scene scene) throws Exception {
        primaryStage.setScene(scene);
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

    public static void showManageBorrowPage() throws Exception {
        FXMLLoader loader = new FXMLLoader(LibraryManagementApp.class.getResource("/FXML/AdminFXML/ManageBorrowPage.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
    }

    private void confirmExit() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit Confirmation");
            alert.setHeaderText("Are you sure you want to exit?");


            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                ImageLoader.shutdown();
                Platform.exit();
            }
        });
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
