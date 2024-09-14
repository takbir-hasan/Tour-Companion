package com.example.tour;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController  {

    @FXML
    private Hyperlink Create;

    @FXML
    private Button Login;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField email;

    @FXML
    private TextField fullName;

    @FXML
    private TextField address;

    @FXML
    private Label loginActionMessage;

    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label pictureLabel;

    @FXML
    private ImageView imgView;

    URL url = LoginController.class.getResource("/profile.png");
    Image image = new Image(LoginController.class.getResource("/profile.png").toExternalForm());

    File selectedFile = null;

    private Stage stage;
    private Scene scene;

    @FXML
    void signup(ActionEvent event) {

        usernameLabel.setText("");
        passwordLabel.setText("");
        emailLabel.setText("");

        String username = usernameField.getText();
        String password = passwordField.getText();
        String mail = email.getText();
        String name = fullName.getText();
        String Address = address.getText();

        boolean flag = true;

        if (!isValidEmail(mail)) {
            emailLabel.setText("! Invalid email");
            flag = false;
        }

        if (password.length() < 6) {
            passwordLabel.setText("! Password must be at least six characters");
            flag = false;
        }

        if (name == null || name.isEmpty() || Address == null || Address.isEmpty() || !flag) {
            infoBox(" Please check that you have entered all the information correctly ! ", null, "Failed");
            flag = false;
        }

        if (isUserDuplicate(username)) {
           usernameLabel.setText("! this username is already in use");
            flag = false;
        }
        if(!flag) {
            return;
        }
        byte[] profilePictureData = new byte[0];
        if(selectedFile != null) profilePictureData = readProfilePictureBytes(selectedFile);


        if (insertUser(username, password, mail, name, Address, profilePictureData)) {

            SessionManager.getInstance().setLoggedInUser(username);
            SessionManager.getInstance().setLoggedInUserPic(image);
            SessionManager.getInstance().setFullname(name);
            SessionManager.getInstance().setEmail(mail);
            SessionManager.getInstance().setAddress(Address);
            SceneManager.loadScene("UserDashboard.fxml", event);


        } else {

        }
    }


    @FXML
    void back(ActionEvent event) {
       SceneManager.clearScene(event);
    }


    @FXML
    void create(ActionEvent event) {
        SceneManager.loadScene("signup.fxml", event);
    }

    @FXML
    void login(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if(username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
            Login.setDisable(true); // Disable the button
            loginActionMessage.setText("Please Wait!...");

            Platform.runLater(() -> {
                boolean flag = validateCredentials(username, password);

                if (!flag) {
                    infoBox("Please enter correct Username and Password", null, "Failed");
                } else {
                    Image image = null;
                    String sql = "select picture , email, address, fullname from userInfo where username = ?";
                    try {
                        Connection connection = DatabaseConnector.getConnection();
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setString(1,username);
                        ResultSet resultSet = preparedStatement.executeQuery();
                        if(resultSet.next()){
                            try {
                                InputStream input = resultSet.getBinaryStream("picture");
                                if(input != null) {
                                    image = new Image(input);
                                    SessionManager.getInstance().setLoggedInUserPic(image);
                                }
                                else {
                                    SessionManager.getInstance().setLoggedInUserPic(image);
                                    System.out.println("pic payna");
                                }
                            }catch (Exception e) {
                                SessionManager.getInstance().setLoggedInUserPic(image);
                                System.out.println("inputstream a problem");
                            }
                            String email = resultSet.getString("email");
                            String fullname = resultSet.getString("fullname");
                            String address = resultSet.getString("address");
                            SessionManager.getInstance().setEmail(email);
                            SessionManager.getInstance().setAddress(address);
                            SessionManager.getInstance().setFullname(fullname);

                        }
                    }catch (SQLException e){
                        e.printStackTrace();
                    }
                    SessionManager.getInstance().setLoggedInUser(username);
                    SceneManager.loadScene("UserDashboard.fxml", event);
                }

                Login.setDisable(false);
                loginActionMessage.setText("");
            });
        }
        else{
            infoBox("Please enter correct Username and Password", null, "Failed");
        }
    }

    @FXML
    void Browse(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpeg","*.png", "*.jpg", "*.gif")
        );

        // Show open file dialog
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
                    pictureLabel.setText(selectedFile.getName());
                }
            } catch (IOException e) {
                System.out.println("pic nai");
                e.printStackTrace();
            }
        } else {

        }
    }

    private boolean insertUser(String username, String password, String Email, String fullname, String address, byte[] profilePictureData) {
        String sql = "INSERT INTO userInfo (username, password, email, fullname, address, picture) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, Email);
            preparedStatement.setString(4, fullname);
            preparedStatement.setString(5, address);
            if(selectedFile != null) preparedStatement.setBytes(6, profilePictureData);
            else preparedStatement.setNull(6, java.sql.Types.BLOB);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean validateCredentials(String username, String password) {
        Connection connection = DatabaseConnector.getConnection();
        final String SELECT_QUERY = "SELECT * FROM userInfo WHERE username = ? and password = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
    public static void infoBox(String infoMessage, String headerText, String title) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(infoMessage);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        Image iconImage = new Image(LoginController.class.getResource("/alertIcon.jpg").toExternalForm());
        ImageView iconImageView = new ImageView(iconImage);
        iconImageView.setFitWidth(48);
        iconImageView.setFitHeight(48);
        alert.setGraphic(iconImageView);
        alert.showAndWait();
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    private boolean isUserDuplicate(String username) {
        String sql = "SELECT COUNT(*) FROM userInfo WHERE username = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
             preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int rowCount = resultSet.getInt(1);
                    return rowCount > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
