module com.example.carproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.carproject to javafx.fxml;
    exports com.example.carproject;
}