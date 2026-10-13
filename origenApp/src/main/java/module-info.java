module com.mycompany.g5_holamundoporcapas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    //opens model to javafx.base;
    opens com.mycompany.origenapp to javafx.fxml;

    opens controller to javafx.fxml, javafx.graphics;
    exports controller;
    exports com.mycompany.origenapp;
}
