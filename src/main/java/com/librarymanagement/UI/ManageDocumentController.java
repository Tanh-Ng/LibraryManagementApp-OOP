package com.librarymanagement.UI;

import com.librarymanagement.app.LibraryManagementApp;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import com.librarymanagement.dao.DocumentDAO;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Document;
import com.librarymanagement.model.Thesis;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;


import java.sql.SQLException;

import java.awt.*;

public class ManageDocumentController {

    public TextField titleField;
    public TextField authorNameField;
    public TextField documentIDField;
    public ChoiceBox<String> documentTypeField;
    public ChoiceBox<Boolean> isAvailableField;
    public TextField isbnField;
    public TextField academicAdvisorField;

    @FXML
    private TableView<Document> documentTableView;

    @FXML
    private TableColumn<Document, Integer> documentIdColumn;

    @FXML
    private TableColumn<Document, String> documentTitleColumn;

    @FXML
    private TableColumn<Document, String> authorColumn;

    @FXML
    private TableColumn<Document, Boolean> isAvailableColumn;

    @FXML
    private TableColumn<Document, String> typeColumn;

    @FXML
    private TableColumn<Document, String> isbnColumn;

    @FXML
    private TableColumn<Document, String> academicAdvisorColumn;




    private DocumentDAO documentDAO = new DocumentDAO();



    private String title;
    private String authorName;
    private String documentID;
    private String documentType;
    private String isbn;
    private String academicAdvisor;


    @FXML
    public void initialize() {
        documentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        documentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
      typeColumn.setCellValueFactory(new PropertyValueFactory<>("documentType"));

        isbnColumn.setCellValueFactory(data -> {
            if (data.getValue() instanceof Book) {
                return new SimpleStringProperty(((Book) data.getValue()).getIsbn());
            }
            return new SimpleStringProperty("");
        });
        academicAdvisorColumn.setCellValueFactory(data -> {
            if (data.getValue() instanceof Thesis) {
                return new SimpleStringProperty(((Thesis) data.getValue()).getAcademicAdvisor());
            }
            return new SimpleStringProperty("");
        });



        // Load dữ liệu từ database lên bảng
        loadDocumentData();
    }
    private void getInfo() {
        title = titleField.getText();
        authorName = authorNameField.getText();
        documentID = documentIDField.getText();
        documentType = documentTypeField.getValue();
        isbn = isbnField.getText();
        academicAdvisor = academicAdvisorField.getText();

    }


    public void handleToHomePageAdmin(ActionEvent actionEvent) throws Exception {
        LibraryManagementApp.showAdminPage();
    }

    public void handleAddDocument(ActionEvent actionEvent) {
        try {
            // Lấy thông tin từ giao diện
            String title = titleField.getText();
            String author = authorNameField.getText();
            String documentType = documentTypeField.getValue();
            boolean isAvailable = isAvailableField.getValue();


             

            // Kiểm tra loại tài liệu
            Document document;
            if ("Book".equalsIgnoreCase(documentType)) {
                String isbn = isbnField.getText(); // Bạn cần thêm trường ISBN vào UI
                document = new Book(title, author, isbn);
            } else if ("Thesis".equalsIgnoreCase(documentType)) {
                String academicAdvisor = academicAdvisorField.getText(); // Thêm trường Academic Advisor vào UI
                document = new Thesis(title, author, academicAdvisor);
            } else {
                // Thông báo lỗi nếu không chọn loại tài liệu
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn loại tài liệu hợp lệ!");
                return;
            }

            document.setIsAvailable(isAvailable);

            // Thêm tài liệu vào cơ sở dữ liệu
            documentDAO.addDocument(document);

            // Thêm tài liệu vào bảng (TableView)
            documentTableView.getItems().add(document);

            // Thông báo thành công
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Tài liệu đã được thêm thành công!");

            // Xóa thông tin trên các trường sau khi thêm
            clearFields();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi khi thêm tài liệu vào cơ sở dữ liệu.");
        }

    }

    // Hàm để hiển thị thông báo
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Hàm xóa thông tin trên các trường nhập liệu
    private void clearFields() {
        titleField.clear();
        authorNameField.clear();
        documentIDField.clear();
        documentTypeField.setValue(null);
        isAvailableField.setValue(null);
        isbnField.clear();
        academicAdvisorField.clear();
    }

    private void loadDocumentData() {
        try {
            // Lấy danh sách tài liệu từ cơ sở dữ liệu
            List<Document> documents = documentDAO.getAllDocuments();

            // Xóa dữ liệu cũ trong bảng (nếu có)
            documentTableView.getItems().clear();

            // Thêm tất cả tài liệu vào bảng
            documentTableView.getItems().addAll(documents);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi khi tải dữ liệu tài liệu từ cơ sở dữ liệu.");
        }
    }

    public void handleUpdateDocument(ActionEvent actionEvent) {
    }

    public void handleDeleteDocument(ActionEvent actionEvent) {
        Document selectedDocument = documentTableView.getSelectionModel().getSelectedItem();

        if (selectedDocument == null) {
            // Hiển thị thông báo nếu không có tài liệu nào được chọn
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một tài liệu để xóa.");
            return;
        }

        try {
            // Xóa tài liệu khỏi cơ sở dữ liệu
            documentDAO.deleteDocument(selectedDocument.getId());

            // Xóa tài liệu khỏi TableView
            documentTableView.getItems().remove(selectedDocument);

            // Hiển thị thông báo thành công
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Tài liệu đã được xóa thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
            // Hiển thị thông báo lỗi
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi khi xóa tài liệu khỏi cơ sở dữ liệu.");
        }
    }
}

