module com.origen {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.bson;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;

    opens controller to javafx.fxml, javafx.graphics;
    opens com.origen to javafx.fxml;
    opens model to org.mongodb.bson;

    exports controller;
    exports com.origen;
}
