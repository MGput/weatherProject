package com.example.weatherproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class mainController implements Initializable{
    @FXML private ComboBox<String> locationType;
    @FXML private TextField locationInput;
    @FXML private Label latLabel;
    @FXML private Label longLabel;
    @FXML private RadioButton historicalData;
    @FXML private RadioButton forecastData;
    @FXML private DatePicker datePick;
    @FXML private DatePicker datePick1;
    @FXML private Label historicalDataLabel;
    @FXML private Label historicalDataLabel1;
    @FXML private ComboBox<Integer> forecastDays;
    @FXML private Label forecastDaysLabel;
    @FXML private ToggleGroup dataTypeGroup;
    public LocalDate today = LocalDate.now();
    @FXML private TextField latitude;
    @FXML private TextField longitude;
    @FXML private Label testLabel;
    @FXML private CheckBox windSpeedCheckBox;
    @FXML private CheckBox precipitationCheckBox;
    @FXML private CheckBox soilTempCheckBox;
    @FXML private CheckBox pressureCheckBox;
    @FXML private CheckBox airTempCheckBox;

    private TextFormatter<String> createFormatter() {                                   // Do TextField, zeby mozna bylo tylko wpisac liczbe
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9.]*") && newText.length() <= 18) {
                long periodCount = newText.chars().filter(ch -> ch == '.').count();
                if (periodCount <= 1) {
                    return change;
                }
            }
            return null;
        });
    }

    public boolean isLocationCity() {
        return locationType.getItems().contains("Miasto");
    }

    @Override public void initialize(URL location, ResourceBundle resources){
        locationType.getSelectionModel().selectFirst();
        locationInput.setPromptText("Wyszukaj miasto (po angielsku)...");
        latitude.setPromptText("Szerokość");
        longitude.setPromptText("Wysokość");
        dataTypeGroup = new ToggleGroup();
        historicalData.setToggleGroup(dataTypeGroup);
        forecastData.setToggleGroup(dataTypeGroup);
        historicalData.setSelected(true);
        int[] forecastOptions = {1, 3, 7, 14, 16};
        for(int forecastOption : forecastOptions){
            forecastDays.getItems().add(forecastOption);
        }
        forecastDays.getSelectionModel().selectLast();
        datePick1.setValue(today);
        datePick.setValue(today.minusDays(16));
        latitude.setTextFormatter(createFormatter());
        longitude.setTextFormatter(createFormatter());
    }

    @FXML public void updateLocationType(){
        String location = locationType.getSelectionModel().getSelectedItem();
        if(location.contains("Miasto")){
            locationInput.setVisible(true);
            latitude.setVisible(false);
            longitude.setVisible(false);
            latLabel.setVisible(false);
            longLabel.setVisible(false);
        }
        else{
            locationInput.setVisible(false);
            latitude.setVisible(true);
            longitude.setVisible(true);
            latLabel.setVisible(true);
            longLabel.setVisible(true);
        }
    }

    @FXML public void updateDataType(){
        RadioButton selected = (RadioButton) dataTypeGroup.getSelectedToggle();
        if(selected == historicalData){
            forecastDaysLabel.setVisible(false);
            forecastDays.setVisible(false);
            datePick.setVisible(true);
            datePick1.setVisible(true);
            historicalDataLabel.setVisible(true);
            historicalDataLabel1.setVisible(true);
        }
        else{
            forecastDaysLabel.setVisible(true);
            forecastDays.setVisible(true);
            datePick.setVisible(false);
            datePick1.setVisible(false);
            historicalDataLabel.setVisible(false);
            historicalDataLabel1.setVisible(false);
        }
    }

    @FXML public void checkIfFirstDateCorrect(){
        LocalDate min = LocalDate.of(1940,1,1);
        LocalDate firstPickedDate =  datePick.getValue();
        LocalDate secondPickedDate =  datePick1.getValue();
        if(firstPickedDate.isBefore(min)){
            datePick.setValue(min);
            return;
        }
        if(firstPickedDate.isAfter(secondPickedDate)){
            datePick.setValue(secondPickedDate.minusDays(1));
        }
    }

    @FXML public void checkIfSecondDateCorrect(){
        LocalDate firstPickedDate =  datePick.getValue();
        LocalDate secondPickedDate =  datePick1.getValue();
        if(secondPickedDate.isAfter(today)){
            datePick1.setValue(today);
            return;
        }
        if(secondPickedDate.isBefore(firstPickedDate)){
            datePick1.setValue(firstPickedDate.plusDays(1));
        }
    }

    @FXML public void launchGraphWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(mainApplication.class.getResource("graphWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 350);

        Stage graphStage = new Stage();

        graphWindowController controller = fxmlLoader.getController();
        controller.setMainController(this);

        graphStage.setTitle("Tescik");
        graphStage.setScene(scene);
        graphStage.setResizable(false);
        graphStage.show();
    }

    @FXML public void test(){
        System.out.println("Od: "+datePick.getValue()+" ");
        System.out.println("Do: "+datePick1.getValue());
    }

    @FXML public void testLabel(){
        testLabel.setText("Hello :)");
    }
}