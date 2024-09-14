package com.example.tour;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserDashboardController implements Initializable {

    @FXML
    private Button BookGuide;

    @FXML
    private Label username;

    @FXML
    private ImageView imgView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String user = SessionManager.getInstance().getLoggedInUser();
        username.setText(user);
        Image image = SessionManager.getInstance().getLoggedInUserPic();
        imgView.setImage(image);

    }

    @FXML
    void bookGuide(ActionEvent event) {
        BookGuide.setDisable(true);
        SceneManager.loadScene("booking.fxml", event);
    }

    @FXML
    void bookingHistory(ActionEvent event) {
        SceneManager.loadScene("bookingHistory.fxml", event);
    }

    @FXML
    void logout(ActionEvent event) {
        SessionManager.getInstance().clearSession();
        SceneManager.clearScene(event);
    }

    @FXML
    void profile(ActionEvent event) {
        SceneManager.loadScene("profile.fxml", event);
    }
    @FXML
    void contact(ActionEvent event) {
        SceneManager.loadScene("contact.fxml", event);
    }



}
