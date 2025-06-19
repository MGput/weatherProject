module com.example.weatherproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.net.http;
    requires com.google.gson;
    requires java.desktop;
    requires redis.clients.jedis;

    opens com.example.weatherproject to javafx.fxml;
    exports com.example.weatherproject;
}