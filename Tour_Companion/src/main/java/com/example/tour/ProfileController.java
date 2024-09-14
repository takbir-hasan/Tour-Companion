package com.example.tour;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML
    private Label addressLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label fullnameLabel;

    @FXML
    private Button logout;

    @FXML
    private Label user;

    @FXML
    private Label usernameLable;

    @FXML
    private ImageView imgView;

    String username = SessionManager.getInstance().getLoggedInUser();
    String fullname = SessionManager.getInstance().getFullname();
    String email = SessionManager.getInstance().getEmail();
    String address = SessionManager.getInstance().getAddress();
    Image image = SessionManager.getInstance().getLoggedInUserPic();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        user.setText(username);

        usernameLable.setText(username);
        fullnameLabel.setText(fullname);
        emailLabel.setText(email);
        addressLabel.setText(address);
        imgView.setImage(image);

    }

    @FXML
    void Back(ActionEvent event) {
        SceneManager.goBack(event);
    }

    @FXML
    void logout(ActionEvent event) {
        SessionManager.getInstance().clearSession();
        SceneManager.clearScene(event);
    }

    @FXML
    void settings(ActionEvent event) {
        SceneManager.loadScene("settings.fxml",event);
    }


}
