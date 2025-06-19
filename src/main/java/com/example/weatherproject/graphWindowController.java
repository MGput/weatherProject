package com.example.weatherproject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.Chart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ResourceBundle;

public class graphWindowController implements Initializable {
    @FXML private Label testLabel;
    private mainController mainControllerRef;
    @FXML private RadioButton windSpeedCheckBox;
    @FXML private RadioButton rainCheckBox;
    @FXML private RadioButton soilTempCheckBox;
    @FXML private RadioButton pressureCheckBox;
    @FXML private RadioButton airTempCheckBox;
    @FXML private ToggleGroup dataVariableGroup;
    @FXML private Chart windSpeedChart;
    @FXML private Chart rainChart;
    @FXML private Chart soilTempChart;
    @FXML private Chart pressureChart;
    @FXML private Chart airTempChart;
    private String jsonResponse;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataVariableGroup = new ToggleGroup();
        windSpeedCheckBox.setToggleGroup(dataVariableGroup);
        rainCheckBox.setToggleGroup(dataVariableGroup);
        soilTempCheckBox.setToggleGroup(dataVariableGroup);
        pressureCheckBox.setToggleGroup(dataVariableGroup);
        airTempCheckBox.setToggleGroup(dataVariableGroup);
        windSpeedCheckBox.setSelected(true);
    }

    private String getSelectedToggleId() {
        return ((RadioButton) dataVariableGroup.getSelectedToggle()).getId();
    }

    public void forwardJsonResponse(String response){
        jsonResponse = response;
    }

    public void updateSelection(){

    }
}