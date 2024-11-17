package com.librarymanagement.UI;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

public class HomePageUserController {
    public TextField searchStringField;

    //Search document after pressed Enter
    public void handleSearchDocument() {
        String searchString = searchStringField.getText();

    }


}
