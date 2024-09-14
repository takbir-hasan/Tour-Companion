module com.example.tour {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires net.coobird.thumbnailator;


    opens com.example.tour to javafx.fxml;
    exports com.example.tour;
}