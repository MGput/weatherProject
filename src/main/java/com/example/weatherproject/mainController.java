package com.example.weatherproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.input.KeyCode;

import static com.example.weatherproject.APIcontroller.getWeatherInfo;

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
    @FXML private Label errorMessage;

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

    private String locationType() {
        return locationType.getValue();
    }

    private String dataType(){
        RadioButton selected = (RadioButton) dataTypeGroup.getSelectedToggle();
        if(selected.equals(historicalData)){
            return "historicalData";
        }
        else if(selected.equals(forecastData)){
            return "forecastData";
        }
        else{
            return null;
        }
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
        datePick1.setValue(today.minusDays(1));
        datePick.setValue(today.minusDays(17));
        latitude.setTextFormatter(createFormatter());
        longitude.setTextFormatter(createFormatter());
        locationInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    initiateApiRequest();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        latitude.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    initiateApiRequest();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        longitude.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    initiateApiRequest();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void updateLocationType(){
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

    public void updateDataType(){
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

    public void checkIfFirstDateCorrect(){
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

    public void checkIfSecondDateCorrect(){
        LocalDate firstPickedDate =  datePick.getValue();
        LocalDate secondPickedDate =  datePick1.getValue();
        if(secondPickedDate.isAfter(today.minusDays(1))){
            datePick1.setValue(today.minusDays(1));
            return;
        }
        if(secondPickedDate.isBefore(firstPickedDate)){
            datePick1.setValue(firstPickedDate.plusDays(1));
        }
    }

    public void initiateApiRequest() throws Exception {
        String reqURL = "", reqURLdate = "", reqURLvariables = "", reqURLloc = "";
        float lat = 0, lon = 0;
        if(dataType().equals("historicalData")){
            reqURL = "https://archive-api.open-meteo.com/v1/archive?";
            String startDate = datePick.getValue().toString();
            String endDate = datePick1.getValue().toString();
            reqURLdate = "&start_date="+startDate+"&end_date="+endDate;
            reqURLvariables = "&hourly=wind_speed_10m,wind_speed_100m";
            reqURLvariables += ",soil_temperature_0_to_7cm,soil_temperature_7_to_28cm,soil_temperature_28_to_100cm,soil_temperature_100_to_255cm";
            reqURLvariables += ",surface_pressure,temperature_2m,rain";
            reqURLvariables += "&timezone=auto";
        }
        else if(dataType().equals("forecastData")){
            reqURL = "https://api.open-meteo.com/v1/forecast?";
            int forecastDaysSelected = forecastDays.getValue();
            reqURLdate = "&forecast_days="+forecastDaysSelected;
            reqURLvariables = "&hourly=wind_speed_180m,wind_speed_10m,wind_speed_120m,wind_speed_80m";
            reqURLvariables += ",soil_temperature_54cm,soil_temperature_18cm,soil_temperature_6cm,soil_temperature_0cm";
            reqURLvariables += ",surface_pressure,rain,temperature_2m";
        }

        if(locationType().equals("Miasto")){
            if(locationInput.getText().isEmpty()){
                errorMessage.setText("Nie wpisano wszystkich danych.");
                return;
            }
            errorMessage.setText("");
            String cityLocation = locationInput.getText().replace(" ","+");

            try {
                JsonElement cityCords = APIcontroller.getCityCoordinates(cityLocation);

                if (cityCords != null && cityCords.isJsonObject()) {
                    JsonObject responseObject = cityCords.getAsJsonObject();
                    JsonArray resultsArray = responseObject.getAsJsonArray("results");

                    if (resultsArray != null && !resultsArray.isEmpty()) {
                        JsonObject firstResult = resultsArray.get(0).getAsJsonObject();
                        lat = firstResult.get("latitude").getAsFloat();
                        lon = firstResult.get("longitude").getAsFloat();
                    } else {
                        errorMessage.setText("Nie znaleziono miasta: " + cityLocation);
                        return;
                    }
                } else {
                    errorMessage.setText("Błąd podczas pobierania współrzędnych.");
                    return;
                }
            } catch (Exception e) {
                errorMessage.setText("Błąd podczas pobierania współrzędnych: " + e.getMessage());
                return;
            }
        }
        else {
            if(latitude.getText().isEmpty() || longitude.getText().isEmpty()){
                errorMessage.setText("Nie wpisano wszystkich danych.");
                return;
            }
            errorMessage.setText("");

            try {
                lat = Float.parseFloat(latitude.getText());
                lon = Float.parseFloat(longitude.getText());
            } catch (NumberFormatException e) {
                errorMessage.setText("Nieprawidłowy format współrzędnych.");
                return;
            }
        }

        lat = Math.round(lat * 100000.0f) / 100000.0f;  // zaokraglenie do 5 miejsc po przecinku
        lon = Math.round(lon * 100000.0f) / 100000.0f;
        reqURLloc = "latitude="+lat+"&longitude="+lon;

        reqURL += reqURLloc + reqURLdate + reqURLvariables;

        System.out.println(reqURL);
        JsonElement reqResponse = getWeatherInfo(reqURL);
        launchGraphWindow(reqResponse);
    }

    private void launchGraphWindow(JsonElement apiResponse) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(mainApplication.class.getResource("graphWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 745, 449);
        Stage graphStage = new Stage();
        graphWindowController controller = fxmlLoader.getController();
        controller.forwardJsonResponse(apiResponse);
        graphStage.setTitle("Test");
        graphStage.setScene(scene);
        graphStage.setResizable(false);
        graphStage.show();
    }

    public void test(){
        System.out.println("Od: "+datePick.getValue()+" ");
        System.out.println("Do: "+datePick1.getValue());
    }
}