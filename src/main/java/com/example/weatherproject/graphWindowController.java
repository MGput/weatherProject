package com.example.weatherproject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class graphWindowController implements Initializable {
    @FXML private Label testLabel;
    private mainController mainControllerRef;
    private boolean searchTypeIsCity;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setMainController(mainController mainController) {
        this.mainControllerRef = mainController;
        initializeWithMainController(); // Call after setting the reference
    }

    private void initializeWithMainController() {
        if (mainControllerRef != null) {
            searchTypeIsCity = mainControllerRef.isLocationCity();
        }
    }

    @FXML
    public void testLabel(){
        testLabel.setText("Hello :)");
    }
}