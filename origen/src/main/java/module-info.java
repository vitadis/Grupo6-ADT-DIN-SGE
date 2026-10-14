module com.origen {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    //requires com.gluonhq.charm.glisten;

    opens controller to javafx.fxml, javafx.graphics;
    exports controller;
    opens com.origen to javafx.fxml;
    
    exports com.origen;
}
