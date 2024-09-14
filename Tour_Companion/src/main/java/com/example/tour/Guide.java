package com.example.tour;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.sql.*;
import java.time.LocalDate;

import static com.example.tour.bookingController.bookGuide;

public class Guide {
    private String name;
    private int successfulService;
    private float ratings;
    private Button button;
    private boolean booked;

    private Button Reviews;
    @FXML
    public static DatePicker date;

    public Guide(String name, int successfulService, float ratings) {
        this.name = name;
        this.successfulService = successfulService;
        float rating = Math.round(ratings * 100.0f) / 100.0f;
        this.ratings = rating;
        this.button = new Button("Book");
        this.button.setOnAction(event -> book());
        this.Reviews = new Button("Reviews");
        this.Reviews.setOnAction(event -> showReviews());
    }

    private void showReviews() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Reviews");
        dialog.setHeaderText("Customer Reviews");
        ScrollPane scrollPane = new ScrollPane(createReviewsVBox());
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(600, 450); // Set a fixed size for the ScrollPane
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(scrollPane);

        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().add(closeButton);
        Button closeButtonNode = (Button) dialogPane.lookupButton(closeButton);
        closeButtonNode.setOnAction(event -> {
            dialog.close();
        });

        dialog.showAndWait();
    }

    private VBox createReviewsVBox() {

        VBox vBox = new VBox();
        try(Connection connection = DatabaseConnector.getConnection()) {
            String sql = "select user, date, Review from bookingInfo where guide = ? and Review IS NOT NULL";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,name);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String user = resultSet.getString("user");
                java.sql.Date sqlDate = resultSet.getDate("date");
                LocalDate date = sqlDate.toLocalDate();
                String review = resultSet.getString("Review");
                Label title = new Label("User: " + user +"         date: "+ date);
                Label header = new Label("Review:");
                Label blank = new Label(" ");
                Label reviewLabel = new Label(review);
                reviewLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-padding: 5px;");
                vBox.getChildren().add(title);
                vBox.getChildren().add(header);
                vBox.getChildren().add(reviewLabel);
                vBox.getChildren().add(blank);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return vBox;
    }
    private void book() {
        if(bookGuide(name)){
            this.button.setText("Booked");
            this.button.setDisable(true);
        }
    }


    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public String getName() {
        return name;
    }

    public int getSuccessfulService() {
        return successfulService;
    }

    public float getRatings() {
        return ratings;
    }
    public void setReviews() {this.Reviews = Reviews;};
    public Button getReviews() {return Reviews;};
}
