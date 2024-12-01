package com.librarymanagement.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javafx.scene.image.Image;

import org.json.JSONObject;

public class ApiClient {

    public static JSONObject fetchBookDetailsByIsbn(String isbn) {
        String apiUrl = "https://openlibrary.org/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data";
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Failed to fetch details. HTTP response code: " + responseCode);
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.optJSONObject("ISBN:" + isbn);

        } catch (Exception e) {
            System.out.println("An error occurred while fetching book details: " + e.getMessage());
            return null;
        }
    }

    public static String getQRCodeURL(String data) {

        // QR Code API URL
        String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + data;

        // Load the QR code image
        return qrCodeUrl;

    }
}
