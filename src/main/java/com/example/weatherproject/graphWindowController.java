package com.example.weatherproject;

import com.google.gson.JsonElement;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class graphWindowController implements Initializable {
    @FXML private Label testLabel;
    @FXML private RadioButton windSpeedCheckBox;
    @FXML private RadioButton rainCheckBox;
    @FXML private RadioButton soilTempCheckBox;
    @FXML private RadioButton pressureCheckBox;
    @FXML private RadioButton airTempCheckBox;
    @FXML private ToggleGroup dataVariableGroup;
    @FXML private LineChart<String, Number> windSpeedChart;
    @FXML private LineChart<String, Number> rainChart;
    @FXML private LineChart<String, Number> soilTempChart;
    @FXML private LineChart<String, Number> pressureChart;
    @FXML private LineChart<String, Number> airTempChart;
    private JsonElement jsonResponse;

    private final String[] windSpeedVariables = {
            "wind_speed_10m", "wind_speed_80m", "wind_speed_100m",
            "wind_speed_120m", "wind_speed_180m"
    };

    private final String[] soilTempVariables = {
            "soil_temperature_0cm", "soil_temperature_6cm", "soil_temperature_18cm",
            "soil_temperature_54cm", "soil_temperature_0_to_7cm", "soil_temperature_7_to_28cm",
            "soil_temperature_28_to_100cm", "soil_temperature_100_to_255cm"
    };

    private final Map<String, String> windSpeedColors = new HashMap<>();
    private final Map<String, String> soilTempColors = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataVariableGroup = new ToggleGroup();
        windSpeedCheckBox.setToggleGroup(dataVariableGroup);
        rainCheckBox.setToggleGroup(dataVariableGroup);
        soilTempCheckBox.setToggleGroup(dataVariableGroup);
        pressureCheckBox.setToggleGroup(dataVariableGroup);
        airTempCheckBox.setToggleGroup(dataVariableGroup);
        windSpeedCheckBox.setSelected(true);

        initializeColorMappings();
        initializeCharts();
    }

    private void initializeColorMappings() {
        windSpeedColors.put("wind_speed_10m", "#FF0000");
        windSpeedColors.put("wind_speed_80m", "#00FF00");
        windSpeedColors.put("wind_speed_100m", "#0000FF");
        windSpeedColors.put("wind_speed_120m", "#FF00FF");
        windSpeedColors.put("wind_speed_180m", "#00FFFF");

        soilTempColors.put("soil_temperature_0cm", "#FF0000");
        soilTempColors.put("soil_temperature_6cm", "#FF7F00");
        soilTempColors.put("soil_temperature_18cm", "#FFFF00");
        soilTempColors.put("soil_temperature_54cm", "#00FF00");
        soilTempColors.put("soil_temperature_0_to_7cm", "#0000FF");
        soilTempColors.put("soil_temperature_7_to_28cm", "#4B0082");
        soilTempColors.put("soil_temperature_28_to_100cm", "#9400D3");
        soilTempColors.put("soil_temperature_100_to_255cm", "#FF1493");
    }

    private void initializeCharts() {
        LineChart<String, Number>[] charts = new LineChart[]{
                windSpeedChart, rainChart, soilTempChart, pressureChart, airTempChart
        };

        for (LineChart<String, Number> chart : charts) {
            chart.setAnimated(false);
            chart.setCreateSymbols(false);
            chart.getData().clear();
        }
        configurePressureAxis();
    }

    private void configurePressureAxis() {
        NumberAxis pressureYAxis = (NumberAxis) pressureChart.getYAxis();
        pressureYAxis.setAutoRanging(false);
        pressureYAxis.setLowerBound(990);
        pressureYAxis.setUpperBound(1020);
        pressureYAxis.setTickUnit(20);

        pressureYAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(pressureYAxis) {
            @Override
            public String toString(Number object) {
                return String.format("%.0f", object);
            }
        });
    }

    public void forwardJsonResponse(JsonElement response) {
        jsonResponse = response;
        try {
            updateCharts();
            updateSelection();
        } catch (Exception e) {
            System.out.print("Error parsing weather data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applySeriesColors(LineChart<String, Number> chart, Map<String, String> colorMap) {
        Platform.runLater(() -> {
            for (int i = 0; i < chart.getData().size(); i++) {
                XYChart.Series<String, Number> series = chart.getData().get(i);
                String variableName = getVariableNameFromSeries(series.getName());
                String color = colorMap.get(variableName);

                if (color != null) {
                    Node line = series.getNode().lookup(".chart-series-line");
                    if (line != null) {
                        line.setStyle("-fx-stroke: " + color + "; -fx-stroke-width: 2px;");
                    }

                    Node legendSymbol = chart.lookup(".chart-legend-item-symbol.series" + i);
                    if (legendSymbol != null) {
                        legendSymbol.setStyle("-fx-background-color: " + color + ";");
                    }

                    for (XYChart.Data<String, Number> data : series.getData()) {
                        Node symbol = data.getNode();
                        if (symbol != null) {
                            symbol.setStyle("-fx-background-color: " + color + ", white; -fx-background-insets: 0, 2; -fx-background-radius: 5px; -fx-padding: 5px;");
                        }
                    }
                }
            }
        });
    }

    private String getVariableNameFromSeries(String seriesName) {
        return seriesName.trim().replace(" ", "_").toLowerCase();
    }

    private void updateCharts() {
        if (jsonResponse == null) {
            throw new IllegalArgumentException("No JSON data received");
        }

        JsonObject hourly = jsonResponse.getAsJsonObject().get("hourly").getAsJsonObject();
        if (hourly == null) {
            throw new IllegalArgumentException("Missing 'hourly' data in response");
        }

        JsonArray timeArray = hourly.getAsJsonArray("time");
        if (timeArray == null) {
            throw new IllegalArgumentException("Missing 'time' array in response");
        }

        updateMultiSeriesChart(windSpeedChart, timeArray, hourly, windSpeedVariables, " ");
        updateMultiSeriesChart(soilTempChart, timeArray, hourly, soilTempVariables, " ");

        updateChartIfExists(rainChart, "Rain (mm)", timeArray, hourly, "rain");
        updateChartIfExists(pressureChart, "Surface Pressure (hPa)", timeArray, hourly, "surface_pressure");
        updateChartIfExists(airTempChart, "Air Temperature (°C)", timeArray, hourly, "temperature_2m");
    }

    private void updateMultiSeriesChart(LineChart<String, Number> chart, JsonArray timeArray,
                                        JsonObject hourly, String[] variables, String namePrefix) {
        chart.getData().clear();

        for (String variable : variables) {
            if (hourly.has(variable)) {
                JsonArray dataArray = hourly.getAsJsonArray(variable);
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(namePrefix + variable.replace("_", " "));

                int maxPoints = Math.min(100, Math.min(timeArray.size(), dataArray.size()));
                for (int i = 0; i < maxPoints; i++) {
                    String time = formatTime(timeArray.get(i).getAsString());
                    Number value = dataArray.get(i).getAsNumber();
                    series.getData().add(new XYChart.Data<>(time, value));
                }

                chart.getData().add(series);
            }
        }

        if (chart == windSpeedChart) {
            applySeriesColors(chart, windSpeedColors);
        } else if (chart == soilTempChart) {
            applySeriesColors(chart, soilTempColors);
        }
    }

    private void updateChartIfExists(LineChart<String, Number> chart, String seriesName,
                                     JsonArray timeArray, JsonObject hourly, String dataKey) {
        if (hourly.has(dataKey)) {
            JsonArray dataArray = hourly.getAsJsonArray(dataKey);
            updateSingleSeriesChart(chart, seriesName, timeArray, dataArray);
        }
    }

    private void updateSingleSeriesChart(LineChart<String, Number> chart, String seriesName,
                                         JsonArray timeArray, JsonArray dataArray) {
        chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(seriesName);

        int maxPoints = Math.min(100, Math.min(timeArray.size(), dataArray.size()));
        for (int i = 0; i < maxPoints; i++) {
            String time = formatTime(timeArray.get(i).getAsString());
            Number value = dataArray.get(i).getAsNumber();
            series.getData().add(new XYChart.Data<>(time, value));
        }

        chart.getData().add(series);

        Platform.runLater(() -> {
            Node line = series.getNode().lookup(".chart-series-line");
            Node legendSymbol = chart.lookup(".chart-legend-item-symbol.series0");

            if (line != null && legendSymbol != null) {
                String defaultColor = "#1f77b4";
                line.setStyle("-fx-stroke: " + defaultColor + "; -fx-stroke-width: 2px;");
                legendSymbol.setStyle("-fx-background-color: " + defaultColor + ";");
            }
        });
    }

    private String formatTime(String isoTime) {
        return isoTime.substring(5, 16).replace("T", " ");
    }

    public void updateSelection() {
        String selectedId = ((RadioButton) dataVariableGroup.getSelectedToggle()).getId();

        windSpeedChart.setVisible(false);
        rainChart.setVisible(false);
        soilTempChart.setVisible(false);
        pressureChart.setVisible(false);
        airTempChart.setVisible(false);

        switch (selectedId) {
            case "windSpeedCheckBox":
                windSpeedChart.setVisible(true);
                break;
            case "rainCheckBox":
                rainChart.setVisible(true);
                break;
            case "soilTempCheckBox":
                soilTempChart.setVisible(true);
                break;
            case "pressureCheckBox":
                pressureChart.setVisible(true);
                break;
            case "airTempCheckBox":
                airTempChart.setVisible(true);
                break;
        }
    }
}