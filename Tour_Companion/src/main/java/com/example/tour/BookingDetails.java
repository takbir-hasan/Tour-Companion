package com.example.tour;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import static javafx.scene.paint.Color.RED;

public class BookingDetails {

    private String place;
    private String guide;
    private LocalDate date;
    private Button actionButton;
    private ChoiceBox<Integer> ratingChoiceBox;

    private Button setReviewButton;
    private boolean rated;

    private boolean reviewed;
    LocalDate today = LocalDate.now();

    public BookingDetails(String place, String guide, LocalDate date, Boolean rated, Boolean reviewed) {
        this.place = place;
        this.guide = guide;
        this.date = date;
        this.rated = rated;
        this.reviewed = reviewed;
        this.actionButton = new Button("Cancel");
        this.setReviewButton = new Button("Write a Review");
        this.ratingChoiceBox = new ChoiceBox<>();
        this.actionButton.setAlignment(Pos.CENTER);
        for (int i = 1; i <= 5; i++) {
            this.ratingChoiceBox.getItems().add(i);
        }

        if (today.isEqual(date) || today.isAfter(date)) {
            this.actionButton.setText("Finished");
            this.actionButton.setDisable(true);
            if(rated){
                this.ratingChoiceBox.setDisable(true);
            }
            else{
                ratingChoiceBox.setStyle("-fx-background-color: white; -fx-border-width: 2px; -fx-border-color: yellow;");
                this.ratingChoiceBox.setOnAction(event -> rating());
            }
            if(reviewed) {
                this.setReviewButton.setText("Reviewed");
                this.setReviewButton.setDisable(true);
            }
            else {
                this.setReviewButton.setOnAction(event -> Review());
            }
        }
        else {
            this.actionButton.setText("Cancel");
            this.actionButton.setTextFill(RED);
            this.actionButton.setOnAction(event -> Cancel());
            this.ratingChoiceBox.setDisable(true);
            this.setReviewButton.setDisable(true);
        }


    }

    private void rating() {

        int value = ratingChoiceBox.getValue();

        if (BookingHistoryController.ratingUpdate(guide, value)) {
            Connection connection = DatabaseConnector.getConnection();
            String sql = "update guideInfo set services = (services + 1) where name = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1,guide);
                int rowAffected = preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }

            String user = SessionManager.getInstance().getLoggedInUser();
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);

            sql = "update bookingInfo set rated = ? where user = ? and guide = ? and date = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setBoolean(1,true);
                preparedStatement.setString(2,user);
                preparedStatement.setString(3,guide);
                preparedStatement.setDate(4, sqlDate);

                int rowAffected = preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
                System.err.println("rated a problem");
            }
            this.ratingChoiceBox.setDisable(true);
        }

    }

    public void Review() {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Write a Review");
        dialog.setHeaderText("Share your feedback");

        // Create a TextArea for the review
        TextArea reviewTextArea = new TextArea();
        int maxLength = 100;
        reviewTextArea.setTextFormatter(new TextFormatter<>(this::limitTextLength));

        // Create a Submit button
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(submitEvent -> {
            String reviewText = reviewTextArea.getText();
            if (reviewText.length() <= maxLength) {
                // Process the submitted review
                updateReview(reviewText);
                System.out.println("Review submitted: " + reviewText);
                dialog.close();
            } else {
                System.out.println("Review exceeds maximum length.");
            }
        });

        VBox vBox = new VBox(reviewTextArea, submitButton);
        vBox.setSpacing(10);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(vBox);
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);

        // Show the dialog and wait for the user's response
        dialog.showAndWait();
    }

    private TextFormatter.Change limitTextLength(TextFormatter.Change change) {
        int maxLength = 100;
        if (change.isDeleted() || change.isContentChange()) {
            int newLength = change.getControlNewText().length();
            if (newLength <= maxLength) {
                return change;
            }
        }
        return null;
    }

    private boolean updateReview(String reviewTxt) {
        boolean flag = true;
        try {
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
            Connection connection = DatabaseConnector.getConnection();
            String sql = "update bookingInfo set Review = ?, isReviewed = ? where user = ? and guide = ? and date = ?";
            String user = SessionManager.getInstance().getLoggedInUser();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, reviewTxt);
            preparedStatement.setBoolean(2, true);
            preparedStatement.setString(3, user);
            preparedStatement.setString(4,guide);
            preparedStatement.setDate(5, sqlDate);
            int rowAffected = 0;
            rowAffected = preparedStatement.executeUpdate();

           if(rowAffected == 0 ) {
               LoginController.infoBox("Error in review", null, "Error");
               flag = false;
           }
           this.setReviewButton.setText("Reviewed");
           this.setReviewButton.setDisable(true);


        }catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }



    private void Cancel() {
        if (BookingHistoryController.CancelBooking(guide, date)) {
            this.actionButton.setText("Canceled");
            this.actionButton.setDisable(true);
        }
    }

    public String getPlace() {
        return place;
    }

    public String getGuide() {
        return guide;
    }

    public LocalDate getDate() {
        return date;
    }

    public Button getActionButton() {
        return actionButton;
    }

    public ChoiceBox<Integer> getRatingChoiceBox() {
        return ratingChoiceBox;
    }

    public Button getSetReviewButton () {
        return  setReviewButton;
    }
}
