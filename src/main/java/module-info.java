module com.example.otodu {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.otodu.Controller to javafx.fxml;
    opens com.example.otodu.Model to javafx.base;

    opens com.example.otodu to javafx.fxml;
    exports com.example.otodu;
}