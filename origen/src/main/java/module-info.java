module com.origen {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens controller to javafx.fxml, javafx.graphics;
    exports controller;
    opens com.origen to javafx.fxml;
    
    exports com.origen;
    requires org.mongodb.driver.sync.client;
}
