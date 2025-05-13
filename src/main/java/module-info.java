module com.example.studenturegistracija {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.studenturegistracija to javafx.fxml;
    exports com.example.studenturegistracija;
}