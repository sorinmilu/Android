package ro.makore.simple_weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * MINIMAL WEATHER APP - EVERYTHING IN ONE FILE
 * This is what happens when you put EVERYTHING in MainActivity.
 * Total: ~850 lines in ONE class. Enjoy the chaos! 🔥
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SimpleWeather";
    private static final String PREFS_NAME = "weather_prefs";
    private static final String DEFAULT_CITY = "Bucharest";
    
    private RecyclerView recyclerView;
    private TextView cityNameView;
    private TextView loadingView;
    private WeatherAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        cityNameView = findViewById(R.id.cityName);
        recyclerView = findViewById(R.id.recyclerView);
        loadingView = findViewById(R.id.loadingText);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Load saved city or use default
        String city = getSavedCity();
        cityNameView.setText(city);
        
        // Load weather
        loadWeather(city);
        
        // Refresh button
        findViewById(R.id.btnRefresh).setOnClickListener(v -> {
            loadWeather(getSavedCity());
        });
    }
    
    // ========== PERSISTENCE (INLINE - NO LocationManager CLASS) ==========
    
    private String getSavedCity() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString("city", DEFAULT_CITY);
    }
    
    private void saveCity(String city) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString("city", city).apply();
    }
    
    // ========== API CALL (INLINE - NO WeatherAPI CLASS) ==========
    
    private void loadWeather(String city) {
        loadingView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        
        new Thread(() -> {
            try {
                // Fetch weather from API (inline)
                String json = fetchWeatherFromAPI(city);
                
                // Parse JSON (inline)
                List<DailyWeather> dailyList = parseWeatherJSON(json);
                
                // Update UI
                runOnUiThread(() -> {
                    loadingView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    
                    adapter = new WeatherAdapter(dailyList);
                    recyclerView.setAdapter(adapter);
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading weather", e);
                runOnUiThread(() -> {
                    loadingView.setText("Error: " + e.getMessage());
                    Toast.makeText(this, "Failed to load weather", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    private String fetchWeatherFromAPI(String cityName) throws Exception {
        OkHttpClient client = new OkHttpClient();
        
        // Read API key from assets
        String apiKey = readAPIKey();
        
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your_api_key_here")) {
            throw new Exception("API key not configured in assets/api_key.json");
        }
        
        // Build URL
        String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString());
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + encodedCity + 
                     "&appid=" + apiKey + "&units=metric";
        
        Log.d(TAG, "Fetching weather for: " + cityName);
        
        Request request = new Request.Builder().url(url).get().build();
        
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            
            if (!response.isSuccessful()) {
                throw new IOException("API Error: " + response.code());
            }
            
            return body;
        }
    }
    
    private String readAPIKey() throws Exception {
        try {
            InputStream is = getAssets().open("api_key.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            
            String json = new String(buffer, StandardCharsets.UTF_8);
            JsonObject obj = new Gson().fromJson(json, JsonObject.class);
            return obj.get("apiKey").getAsString().trim();
        } catch (Exception e) {
            Log.e(TAG, "Failed to read API key", e);
            throw new Exception("Failed to read API key from assets");
        }
    }
    
    // ========== JSON PARSING (INLINE - NO WeatherParser CLASS) ==========
    
    private List<DailyWeather> parseWeatherJSON(String json) {
        Gson gson = new Gson();
        JsonObject root = gson.fromJson(json, JsonObject.class);
        
        JsonObject city = root.getAsJsonObject("city");
        String cityName = city.get("name").getAsString();
        String country = city.has("country") ? city.get("country").getAsString() : "";
        
        JsonArray list = root.getAsJsonArray("list");
        
        // Group by day
        Map<String, DailyWeather> dayMap = new HashMap<>();
        
        for (JsonElement element : list) {
            JsonObject item = element.getAsJsonObject();
            
            // Parse timestamp
            long timestamp = item.get("dt").getAsLong() * 1000;
            Date date = new Date(timestamp);
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
            SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);
            
            String dayKey = dayFormat.format(date);
            String timeStr = timeFormat.format(date);
            String displayDate = displayFormat.format(date);
            
            // Parse weather data
            JsonObject main = item.getAsJsonObject("main");
            double temp = main.get("temp").getAsDouble();
            double feelsLike = main.get("feels_like").getAsDouble();
            double humidity = main.get("humidity").getAsDouble();
            double pressure = main.get("pressure").getAsDouble();
            
            JsonArray weatherArray = item.getAsJsonArray("weather");
            String description = "";
            String icon = "";
            if (weatherArray.size() > 0) {
                JsonObject weather = weatherArray.get(0).getAsJsonObject();
                description = weather.get("description").getAsString();
                icon = weather.get("icon").getAsString();
            }
            
            JsonObject wind = item.getAsJsonObject("wind");
            double windSpeed = wind.get("speed").getAsDouble();
            
            double visibility = item.has("visibility") ? item.get("visibility").getAsDouble() / 1000.0 : 0;
            
            // Create weather item
            WeatherItem weatherItem = new WeatherItem();
            weatherItem.cityName = cityName;
            weatherItem.country = country;
            weatherItem.description = description;
            weatherItem.iconUrl = "https://openweathermap.org/img/wn/" + icon + "@2x.png";
            weatherItem.temperature = temp;
            weatherItem.feelsLike = feelsLike;
            weatherItem.humidity = humidity;
            weatherItem.pressure = pressure;
            weatherItem.windSpeed = windSpeed;
            weatherItem.visibility = visibility;
            weatherItem.dateTime = displayDate + " " + timeStr;
            
            // Group by day
            if (!dayMap.containsKey(dayKey)) {
                DailyWeather daily = new DailyWeather();
                daily.date = displayDate;
                daily.items = new ArrayList<>();
                dayMap.put(dayKey, daily);
            }
            dayMap.get(dayKey).items.add(weatherItem);
        }
        
        // Convert to list and calculate averages
        List<DailyWeather> result = new ArrayList<>(dayMap.values());
        for (DailyWeather daily : result) {
            daily.calculateAverages();
        }
        
        return result;
    }
    
    // ========== MODEL CLASSES (INLINE - NO SEPARATE MODEL PACKAGE) ==========
    
    /**
     * WeatherItem - Parcelable model for single weather entry
     * Normally this would be in model/WeatherItem.java
     */
    public static class WeatherItem implements Parcelable {
        public String cityName;
        public String country;
        public String description;
        public String iconUrl;
        public double temperature;
        public double feelsLike;
        public double humidity;
        public double pressure;
        public double windSpeed;
        public double visibility;
        public String dateTime;
        
        public WeatherItem() {}
        
        protected WeatherItem(Parcel in) {
            cityName = in.readString();
            country = in.readString();
            description = in.readString();
            iconUrl = in.readString();
            temperature = in.readDouble();
            feelsLike = in.readDouble();
            humidity = in.readDouble();
            pressure = in.readDouble();
            windSpeed = in.readDouble();
            visibility = in.readDouble();
            dateTime = in.readString();
        }
        
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(cityName);
            dest.writeString(country);
            dest.writeString(description);
            dest.writeString(iconUrl);
            dest.writeDouble(temperature);
            dest.writeDouble(feelsLike);
            dest.writeDouble(humidity);
            dest.writeDouble(pressure);
            dest.writeDouble(windSpeed);
            dest.writeDouble(visibility);
            dest.writeString(dateTime);
        }
        
        @Override
        public int describeContents() {
            return 0;
        }
        
        public static final Creator<WeatherItem> CREATOR = new Creator<WeatherItem>() {
            @Override
            public WeatherItem createFromParcel(Parcel in) {
                return new WeatherItem(in);
            }
            
            @Override
            public WeatherItem[] newArray(int size) {
                return new WeatherItem[size];
            }
        };
    }
    
    /**
     * DailyWeather - Groups weather items by day
     * Normally this would be in model/DailyWeatherItem.java
     */
    public static class DailyWeather {
        public String date;
        public List<WeatherItem> items;
        public double avgTemp;
        public double minTemp;
        public double maxTemp;
        
        public void calculateAverages() {
            if (items == null || items.isEmpty()) return;
            
            double sum = 0;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            
            for (WeatherItem item : items) {
                sum += item.temperature;
                min = Math.min(min, item.temperature);
                max = Math.max(max, item.temperature);
            }
            
            avgTemp = sum / items.size();
            minTemp = min;
            maxTemp = max;
        }
    }
    
    // ========== ADAPTER (INLINE - NO SEPARATE ADAPTER PACKAGE) ==========
    
    /**
     * WeatherAdapter - RecyclerView adapter
     * Normally this would be in adapter/DailyWeatherAdapter.java
     * 
     * Android FORCES us to have this as a separate class because
     * RecyclerView.Adapter is abstract. Can't avoid it!
     */
    private class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
        
        private List<DailyWeather> dailyList;
        
        public WeatherAdapter(List<DailyWeather> dailyList) {
            this.dailyList = dailyList;
        }
        
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weather, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            DailyWeather daily = dailyList.get(position);
            
            holder.dateText.setText(daily.date);
            holder.tempText.setText(String.format(Locale.US, "%.1f°C", daily.avgTemp));
            holder.minMaxText.setText(String.format(Locale.US, "%.1f / %.1f°C", 
                daily.minTemp, daily.maxTemp));
            holder.itemCountText.setText(daily.items.size() + " entries");
            
            // Click to see details
            holder.itemView.setOnClickListener(v -> {
                if (!daily.items.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, WeatherDetailActivity.class);
                    intent.putExtra("weather_item", daily.items.get(0));
                    startActivity(intent);
                }
            });
        }
        
        @Override
        public int getItemCount() {
            return dailyList.size();
        }
        
        /**
         * ViewHolder - Required by RecyclerView
         * Android FORCES this pattern for performance
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView dateText;
            TextView tempText;
            TextView minMaxText;
            TextView itemCountText;
            
            ViewHolder(View itemView) {
                super(itemView);
                dateText = itemView.findViewById(R.id.dateText);
                tempText = itemView.findViewById(R.id.tempText);
                minMaxText = itemView.findViewById(R.id.minMaxText);
                itemCountText = itemView.findViewById(R.id.itemCountText);
            }
        }
    }
}
