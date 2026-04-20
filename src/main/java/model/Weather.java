package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Weather
 * <p>
 * Description:
 *  Pull weather information from openweathermap.org
 * @author rcwav
 * @since 4/19/2026
 */
public class Weather {
    public static final String apiKey = "dc6ba153c1eee40a0d7d75dc0a0e9f1e";
    public static final String defaultCity = "Monterey,CA,USA";

    public static void main(String[] args) {
        double[] latlon = getLatLon(defaultCity);
        if (latlon != null) {
            System.out.printf("City:%s\nLat:%f\nLon:%f\n", defaultCity, latlon[0], latlon[1]);
            System.out.println(getCurrentWeather(latlon[0],latlon[1]));
            System.out.println(getWeatherComments());
        }
        String altCity = "Carlsbad,CA,USA";
        latlon = getLatLon(altCity);
        if (latlon != null) {
            System.out.printf("City:%s\nLat:%f\nLon:%f\n", altCity, latlon[0], latlon[1]);
            System.out.println(getCurrentWeather(latlon[0],latlon[1]));
            System.out.println(getWeatherComments(altCity));
        }
    }

    public static double[] getLatLon(String city) {
        try {
            city = city == null ? defaultCity : city;
            city = city.replace(" ","");
            String urlStr = "http://api.openweathermap.org/geo/1.0/direct?q=" + city + "&limit=1&appid=" + apiKey;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            String json = reader.lines().reduce("", String::concat);
            reader.close();

            // Thanks ChatGPT
            double lat = Double.parseDouble(json.split("\"lat\":")[1].split(",")[0]);
            double lon = Double.parseDouble(json.split("\"lon\":")[1].split(",")[0]);

            return new double[]{lat, lon};

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get current weather
     */
    public static String getCurrentWeather() {
        double[] latlon = getLatLon(defaultCity);
        return getCurrentWeather(latlon[0], latlon[1]);
    }
    public static String getCurrentWeather(double lat, double lon) {
        try {
            String urlStr = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat
                    + "&lon=" + lon + "&appid=" + apiKey + "&units=imperial";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );
            String json = reader.lines().reduce("",String::concat);
            reader.close();

            String temp = json.split("\"temp\":")[1].split(",")[0];
            String condition = json.split("\"main\":\"")[1].split("\"")[0];
            String iconCode = json.split("\"icon\":\"")[1].split("\"")[0];

            String icon = mapIcon(iconCode);

            return icon + " " + temp + "°F";

        } catch (Exception e) {
            e.printStackTrace();
            return "Weather unavailable";
        }
    }

    /**
     * Map the weather iconCode to an image
     * As documented on openweathermap.org
     */
    private static String mapIcon(String iconCode) {
        // day icons end with "d", night with "n"
        // thanks ChatGPT
        return switch (iconCode) {
            case "01d" -> "☀";   // no emoji variation
            case "01n" -> "☾";
            case "02d", "02n" -> "⛅";
            case "03d", "03n", "04d", "04n" -> "☁";
            case "09d", "09n", "10d", "10n" -> "☂";
            case "11d", "11n" -> "⚡";
            case "13d", "13n" -> "❄";
            case "50d", "50n" -> "〰";
            default -> "°";
        };
    }

    /**
     * getWeatherComments
     * Use the AI endpoint to get commentary about the weather using
     * simple prompt: What's the weather like in <city></city>
     */
    public static String getWeatherComments() {
        return getWeatherComments(defaultCity);
    }
    public static String getWeatherComments(String city) {
        city = city == null ? defaultCity : city;
        city = city.replace(" ","");
        try {
            String urlStr = "https://api.openweathermap.org/assistant/session";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-Api-Key", apiKey);
            conn.setDoOutput(true);

            // Request body
            String jsonInput = "{ \"prompt\": \"What’s weather like in " + city + "?\" }";

            // Send the request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line.trim());
                }
            }

            // Parse JSON
            String responseStr = response.toString();

            int start = responseStr.indexOf("\"answer\":") + 10;
            int end = responseStr.indexOf("\",", start);

            String text = responseStr.substring(start,end);
            // Convert escaped newlines into real ones
            text = text.replace("\\n", "\n");

            // Optional: collapse excessive blank lines
            text = text.replaceAll("\n{3,}", "\n\n");

            return text;

        } catch (Exception e) {
            e.printStackTrace();
            return "Weather unavailable";
        }

    }
}
