package com.example.weatherproject;

import com.google.gson.JsonObject;
import javafx.application.Application;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class APIcontroller {
    private static JsonElement apiRequest(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return JsonParser.parseString(response.body());
    }

    public static JsonElement getCityCoordinates(String cityLocation) throws Exception {
        JsonElement response = apiRequest("https://geocoding-api.open-meteo.com/v1/search?name="+cityLocation+"&count=1&language=en&format=json");
        //System.out.println(response.toString());
        return response;
    }

    public static JsonElement getWeatherInfo(String URL) throws Exception {
        JsonElement response = apiRequest(URL);
        //System.out.println(response.toString());
        return response;
    }

}
