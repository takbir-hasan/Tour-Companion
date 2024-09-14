package com.example.tour;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BookingHistoryController implements Initializable {

    @FXML
    private Label userName;

    @FXML
    private TableView<BookingDetails> HistoryTable;

    @FXML
    private ImageView imgView;
    String username =  SessionManager.getInstance().getLoggedInUser();

    public static boolean ratingUpdate(String guide, int value) {

        Connection connection = DatabaseConnector.getConnection();
        String query = "Update guideInfo set ratings = CAST(tot_rating AS FLOAT) / (services + 1) where name = ?";

        int rowsAffected = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1,guide);
            rowsAffected = preparedStatement.executeUpdate();
        }
        catch (SQLException e) {

            e.printStackTrace();
        }
        String sql ="UPDATE guideInfo SET tot_rating = tot_rating + ? WHERE name = ?";
        int rowsAffected2 = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1,guide);
            rowsAffected2 = preparedStatement.executeUpdate();
        }
        catch (SQLException e) {

            e.printStackTrace();
        }
        return rowsAffected > 0 && rowsAffected2 > 0;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        userName.setText(username);
        Image image = SessionManager.getInstance().getLoggedInUserPic();
        imgView.setImage(image);
        HistoryTable.setRowFactory(tv -> new TableRow<BookingDetails>() {
            @Override
            protected void updateItem(BookingDetails item, boolean empty) {
                super.updateItem(item, empty);
                if (getIndex() % 2 == 0) {
                    setStyle("-fx-background-color:  #A9CEEC;");
                } else {
                    setStyle("-fx-background-color: #c99087;");
                }
            }
        });

        ArrayList<BookingDetails> Details = getDetailsFromDatabase(username);
        HistoryTable.getItems().addAll(Details);
    }

    @FXML
    void BookGuide(ActionEvent event) {
        SceneManager.loadScene("booking.fxml", event);
    }

    @FXML
    void dashboard(ActionEvent event) {
        SceneManager.loadScene("UserDashboard.fxml", event);
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
    void refresh(ActionEvent event) {
        SceneManager.switchScene((Node) event.getSource(), "bookingHistory.fxml", "login", 1024, 650);
    }

    private ArrayList<BookingDetails> getDetailsFromDatabase(String username) {
        ArrayList<BookingDetails> Details = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection()) {

            String query = "SELECT place, guide, date, rated, isReviewed FROM bookingInfo WHERE user = ? order by date asc";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String Place = resultSet.getString("place");
                        String Guide = resultSet.getString("guide");
                        java.sql.Date sqlDate = resultSet.getDate("date");
                        LocalDate date = sqlDate.toLocalDate();
                        Boolean rated = resultSet.getBoolean("rated");
                        Boolean isReviewed = resultSet.getBoolean("isReviewed");
                        BookingDetails details= new BookingDetails(Place, Guide, date, rated, isReviewed );

                        System.out.println(details.getPlace());
                        System.out.println(details.getGuide());
                        System.out.println(details.getDate());
                        System.out.println("kaj Hoye geche");
                        Details.add(details);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("data geing por");
        }

        return Details;
    }
    public static boolean CancelBooking(String guide, LocalDate date) {

        String username = SessionManager.getInstance().getLoggedInUser();
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);

        Connection connection = DatabaseConnector.getConnection();
        String query = "delete from bookingInfo where user = ? and guide = ? and date = ?";

        int rowsAffected = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2,guide);
            preparedStatement.setDate(3,sqlDate);

            rowsAffected = preparedStatement.executeUpdate();

        }
        catch (SQLException e) {

            System.out.println("book a guide a problem");
            e.printStackTrace();
        }
        return rowsAffected > 0;
    }



    public void Back(ActionEvent event) {
       SceneManager.goBack(event);
    }
}
