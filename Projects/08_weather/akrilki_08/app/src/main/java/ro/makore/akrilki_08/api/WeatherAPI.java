package ro.makore.akrilki_08.api;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class WeatherAPI {

    private static final String API_BASE_URL = "https://api.openweathermap.org/data/2.5/forecast";
    private static final String TAG = "WeatherAPI";

    private static String readJsonFromAssets(Context context, String fileName) throws IOException {
        InputStream is = context.getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        int bytesRead = is.read(buffer);
        is.close();
        
        if (bytesRead != size) {
            Log.w(TAG, "Warning: Expected to read " + size + " bytes but read " + bytesRead);
        }
        
        String content = new String(buffer, 0, bytesRead, "UTF-8");
        // Remove any BOM if present
        if (content.length() > 0 && content.charAt(0) == '\uFEFF') {
            content = content.substring(1);
        }
        return content;
    }

    public static String fetchWeather(Context context, String cityName) throws Exception {
        OkHttpClient client = new OkHttpClient();

        // Read the API key from assets
        String apiKeyJson = readJsonFromAssets(context, "api_key.json");
        Log.d(TAG, "Raw API key JSON: " + apiKeyJson);
        
        JSONObject apiKeyObject = new JSONObject(apiKeyJson);
        String apiKey = apiKeyObject.getString("apiKey");
        
        // Trim whitespace from API key
        if (apiKey != null) {
            apiKey = apiKey.trim();
        }

        // Check if API key is set
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your_openweathermap_api_key_here")) {
            throw new Exception("API key not configured. Please set your OpenWeatherMap API key in assets/api_key.json");
        }
        
        Log.d(TAG, "API Key length: " + apiKey.length());
        Log.d(TAG, "API Key (first 4 chars): " + (apiKey.length() > 4 ? apiKey.substring(0, 4) + "..." : apiKey));

        // URL encode the city name to handle spaces and special characters
        String encodedCityName = URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString());

        // Build URL with query parameters
        String url = API_BASE_URL + "?q=" + encodedCityName + "&appid=" + apiKey + "&units=metric";
        
        Log.d(TAG, "Fetching weather for: " + cityName);
        Log.d(TAG, "URL: " + url.replace(apiKey, "***"));

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            
            if (!response.isSuccessful()) {
                Log.e(TAG, "API Error - Code: " + response.code());
                Log.e(TAG, "Response: " + responseBody);
                
                // Try to parse error message from response
                try {
                    JSONObject errorJson = new JSONObject(responseBody);
                    String errorMessage = errorJson.optString("message", "Unknown error");
                    throw new IOException("API Error: " + errorMessage + " (Code: " + response.code() + ")");
                } catch (Exception e) {
                    throw new IOException("API Error - Code: " + response.code() + ", Response: " + responseBody);
                }
            }
            
            Log.d(TAG, "API Response received successfully");
            return responseBody;
        }
    }
}

