package com.example.weatherproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class mainApplication extends Application {

    private FXMLLoader fxmlLoader;

    @Override
    public void start(Stage stage) throws IOException {
        fxmlLoader = new FXMLLoader(mainApplication.class.getResource("mainGUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Aplikacja Pogodowa");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        shutdown();
        super.stop();
    }

    private void shutdown() {
        if (fxmlLoader != null) {
            graphWindowController controller = fxmlLoader.getController();
            if (controller != null) {
                controller.shutdown();
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}