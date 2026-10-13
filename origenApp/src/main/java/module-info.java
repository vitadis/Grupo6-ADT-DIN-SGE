module com.mycompany.origenapp {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.origenapp to javafx.fxml;
    exports com.mycompany.origenapp;
}
