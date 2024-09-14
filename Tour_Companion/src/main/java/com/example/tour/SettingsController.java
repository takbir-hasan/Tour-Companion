package com.example.tour;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static com.example.tour.LoginController.infoBox;

public class SettingsController implements Initializable {

    @FXML
    private Button addressButton;

    @FXML
    private TextField addressField;

    @FXML
    private Button emailButton;

    @FXML
    private TextField emailField;

    @FXML
    private Button nameButton;

    @FXML
    private TextField nameFIeld;

    @FXML
    private Button passwordButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private AnchorPane txtemailaddress;

    @FXML
    private Label passwordLabel;
    @FXML
    private Label emailLabel;

    @FXML
    private ImageView imgView;

    @FXML
    Label ProfilePicLabel;

    @FXML
    Button pictureButton;

    File selectedFile = null;

    String username = SessionManager.getInstance().getLoggedInUser();

    Image image = SessionManager.getInstance().getLoggedInUserPic();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       imgView.setImage(image);
    }

    @FXML
    void AddressUpdate(ActionEvent event) {

        String address = addressField.getText();
        if(address != null) {
            Connection connection = DatabaseConnector.getConnection();
            String sql = "update userInfo set address = ? where username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, address);
                preparedStatement.setString(2, username);

                int rowAffected = preparedStatement.executeUpdate();
                if (rowAffected > 0) {
                    SessionManager.getInstance().setAddress(address);
                    addressButton.setText("updated");
                    addressButton.setDisable(true);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    void Back(ActionEvent event) {
        SceneManager.goBack(event);
    }

    @FXML
    void EmaiUpdate(ActionEvent event) {

        String email = emailField.getText();

        if(email != null && isValidEmail(email) ) {
            Connection connection = DatabaseConnector.getConnection();
            String sql = "update userInfo set email = ? where username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1,email);
                preparedStatement.setString(2, username);

                int rowAffected = preparedStatement.executeUpdate();
                if (rowAffected > 0) {
                    SessionManager.getInstance().setEmail(email);
                    emailButton.setText("updated");
                    emailButton.setDisable(true);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            emailLabel.setText("! Invalid email");
        }
    }

    @FXML
    void Save(ActionEvent event) {

        SceneManager.goBack(event);

    }

    @FXML
    void nameUpdate(ActionEvent event) {
        String name = nameFIeld.getText();
        if(name != null) {
            Connection connection = DatabaseConnector.getConnection();
            String sql = "update userInfo set fullname = ? where username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, name);
                preparedStatement.setString(2, username);

                int rowAffected = preparedStatement.executeUpdate();
                if (rowAffected > 0) {
                    SessionManager.getInstance().setFullname(name);
                    nameButton.setText("updated");
                    nameButton.setDisable(true);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void passwordUpdate(ActionEvent event) {

        String password = passwordField.getText();
        if(password.length() >= 6) {
            Connection connection = DatabaseConnector.getConnection();
            String sql = "update userInfo set password = ? where username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1,password);
                preparedStatement.setString(2, username);

                int rowAffected = preparedStatement.executeUpdate();
                if (rowAffected > 0) {
                    passwordButton.setText("updated");
                    passwordButton.setDisable(true);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            passwordLabel.setText("! Password must be at least six characters");
        }
    }
    @FXML
    void Browse(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpeg","*.png", "*.jpg", "*.gif")
        );

        File selectedF = fileChooser.showOpenDialog(new Stage());

        if (selectedF != null) {
            try {
                FileInputStream input = new FileInputStream(selectedF);
                image = new Image(input);

                if (image.isError()) {
                    infoBox("! Unsupported image", null, "! Error");
                } else {
                    selectedFile = selectedF;
                    //Circle clip = new Circle(imgView.getFitWidth() / 2, imgView.getFitHeight()/2, imgView.getFitWidth() / 2);
                    // imgView.setClip(clip);
                    imgView.setImage(image);
                    ProfilePicLabel.setText(selectedFile.getName());
                    pictureButton.setText("update");
                    pictureButton.setDisable(false);

                }
            } catch (IOException e) {
                infoBox("Select image properly", null,"Error");
                System.err.println("pic nai");
            }
        } else {

        }
    }
    @FXML
    void PictureUpdate(ActionEvent event) {

        //System.out.println(selectedFile.getAbsolutePath());
        if(selectedFile != null) {
            System.out.println("coming");
            byte[] profilePictureData = readProfilePictureBytes(selectedFile);
            Connection connection = DatabaseConnector.getConnection();
            String sql = "update userInfo set picture = ? where username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setBytes(1,profilePictureData);
                preparedStatement.setString(2,username);
                int rowAffected = preparedStatement.executeUpdate();
                if (rowAffected > 0) {
                    SessionManager.getInstance().setLoggedInUserPic(image);
                    pictureButton.setText("updated");
                    pictureButton.setDisable(true);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        else {
            infoBox("Select a image", null, "Error");
        }
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private byte[] readProfilePictureBytes(File profilePictureFile) {
        try {
            // Read the file into a byte array
            byte[] fileBytes = Files.readAllBytes(profilePictureFile.toPath());
            return fileBytes;
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }


}
