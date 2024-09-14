package com.example.tour;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class bookingController implements Initializable {


    @FXML
    private ComboBox<String> placeBox;

    @FXML
    private TableView<Guide> guideTable;

    @FXML
    private DatePicker datepick;
    @FXML
    private Label label;

    @FXML
    private Button searchButton;
    static LocalDate date = LocalDate.parse("2000-01-01");;

    static String selectedPlace = "NULL";



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        guideTable.setRowFactory(tv -> new TableRow<Guide>() {
            @Override
            protected void updateItem(Guide item, boolean empty) {
                super.updateItem(item, empty);
                if (getIndex() % 2 == 0) {
                    setStyle("-fx-background-color:  #A9CEEC;");
                } else {
                    setStyle("-fx-background-color:  #c99087;");
                }
            }
        });


        String[] items = {"Sylhet", "Bandarban", "Khagrachhari", "Cox's Bazar", "Sundarbans", "Rangamati"};
        placeBox.getItems().addAll(items);

        List<LocalDate> enabledDates = new ArrayList<>();
        enabledDates.add(LocalDate.now());
        for (int i = 1; i <= 15; i++) {
            enabledDates.add(LocalDate.now().plusDays(i));
        }
        datepick.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(true);

                if (date != null) {
                    if (enabledDates.contains(date)) {
                        setDisable(false);
                    }
                }
            }
        });
    }
    @FXML
    void Datepick(ActionEvent event) {
        date = datepick.getValue();
    }

    @FXML
    void placeBoxAction(ActionEvent event) {
    }

    @FXML
    void dashboard(ActionEvent event) {
        SceneManager.loadScene("UserDashboard.fxml", event);
    }

    @FXML
    public void search(ActionEvent event) {

        searchButton.setDisable(true);
        selectedPlace = placeBox.getValue();
        guideTable.getItems().clear();
        ArrayList<Guide> guides = getGuidesFromDatabase(selectedPlace, date);
        guideTable.getItems().addAll(guides);
        searchButton.setDisable(false);
    }

    private ArrayList<Guide> getGuidesFromDatabase(String place, LocalDate date) {
        ArrayList<Guide> guides = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection()) {

            String query = "SELECT g.name, g.services, g.ratings FROM guideInfo as g " +
                            "WHERE place = ? and " +
                            "not exists(select 1 from bookingInfo as b  " +
                                        "where g.name = b.guide and b.place = g.place and b.date = ?)" +
                            "order by ratings desc";
            System.out.println(place);

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, place);
                preparedStatement.setDate(2,  Date.valueOf(date));
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        int successfulServices = resultSet.getInt("services");
                        float ratings = resultSet.getFloat("ratings");
                        Guide guide = new Guide(name, successfulServices, ratings);
                        guides.add(guide);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return guides;
    }

    public static boolean bookGuide(String name) {

        String username = SessionManager.getInstance().getLoggedInUser();
        Connection connection = DatabaseConnector.getConnection();
        String query = "insert into bookingInfo values (?,?,?,?,?,?, ?)";
        int rowsAffected = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2,selectedPlace);
            preparedStatement.setString(3, name);
            preparedStatement.setDate(4,  Date.valueOf(date));
            preparedStatement.setBoolean(5,  false);
            preparedStatement.setBoolean(6,false);
            preparedStatement.setString(7, null);

            rowsAffected = preparedStatement.executeUpdate();

        }
        catch (SQLException e) {

            System.out.println("book a guide a problem");
            e.printStackTrace();
        }
        return rowsAffected > 0;

    }

    public void bookingHistory(ActionEvent event) {
        SceneManager.loadScene("bookingHistory.fxml", event);
    }


    @FXML
    void logout(ActionEvent event) {
        SessionManager.getInstance().clearSession();
        SceneManager.clearScene(event);
    }

    public void Back(ActionEvent event) {
       SceneManager.goBack(event);
    }
}

