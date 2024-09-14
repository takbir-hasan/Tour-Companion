package com.example.tour;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ContactController {

    @FXML
    void Back(ActionEvent event) {
        SceneManager.goBack(event);
    }

}
