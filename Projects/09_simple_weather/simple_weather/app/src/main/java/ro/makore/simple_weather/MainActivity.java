package ro.makore.simple_weather;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

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
 * SIMPLE WEATHER APP - Still minimal but with GPS and multiple cities
 * Everything is INLINE in MainActivity except what Android FORCES to be separate
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SimpleWeather";
    private static final String PREFS_NAME = "weather_prefs";
    private static final String KEY_CITIES = "cities";
    private static final String KEY_CURRENT_CITY = "current_city";
    private static final String DEFAULT_CITY = "Bucharest";
    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    
    private RecyclerView recyclerView;
    private TextView loadingView;
    private Spinner citySpinner;
    private WeatherAdapter adapter;
    private ArrayAdapter<String> spinnerAdapter;
    private FusedLocationProviderClient fusedLocationClient;
    
    private List<String> cities = new ArrayList<>();
    private String currentCity;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        recyclerView = findViewById(R.id.recyclerView);
        loadingView = findViewById(R.id.loadingText);
        citySpinner = findViewById(R.id.citySpinner);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Initialize GPS
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        // Load saved cities
        loadCities();
        
        // Setup spinner
        setupCitySpinner();
        
        // Setup buttons
        findViewById(R.id.btnRefresh).setOnClickListener(v -> loadWeather(currentCity));
        findViewById(R.id.btnAddCity).setOnClickListener(v -> showAddCityDialog());
        
        // Get GPS location
        requestGPSLocation();
        
        // Load weather for current city
        if (currentCity != null) {
            loadWeather(currentCity);
        }
    }
    
    // ========== PERSISTENCE (INLINE - using SharedPreferences directly) ==========
    
    private void loadCities() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_CITIES, null);
        
        if (json != null) {
            cities = new Gson().fromJson(json, new TypeToken<List<String>>(){}.getType());
        }
        
        if (cities.isEmpty()) {
            cities.add(DEFAULT_CITY);
        }
        
        currentCity = prefs.getString(KEY_CURRENT_CITY, cities.get(0));
    }
    
    private void saveCities() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = new Gson().toJson(cities);
        prefs.edit()
            .putString(KEY_CITIES, json)
            .putString(KEY_CURRENT_CITY, currentCity)
            .apply();
    }
    
    private void addCity(String city) {
        if (!cities.contains(city)) {
            cities.add(city);
            saveCities();
            updateSpinner();
        }
    }
    
    // ========== UI SETUP ==========
    
    private void setupCitySpinner() {
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(spinnerAdapter);
        
        int position = cities.indexOf(currentCity);
        if (position >= 0) {
            citySpinner.setSelection(position);
        }
        
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = cities.get(position);
                if (!selected.equals(currentCity)) {
                    currentCity = selected;
                    saveCities();
                    loadWeather(currentCity);
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    
    private void updateSpinner() {
        spinnerAdapter.clear();
        spinnerAdapter.addAll(cities);
        spinnerAdapter.notifyDataSetChanged();
        
        int position = cities.indexOf(currentCity);
        if (position >= 0) {
            citySpinner.setSelection(position);
        }
    }
    
    // ========== ADD CITY DIALOG (INLINE - simple AlertDialog) ==========
    
    private void showAddCityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add City");
        
        final EditText input = new EditText(this);
        input.setHint("Enter city name");
        builder.setView(input);
        
        builder.setPositiveButton("Add", (dialog, which) -> {
            String city = input.getText().toString().trim();
            if (!city.isEmpty()) {
                // Verify city exists with API
                new Thread(() -> {
                    try {
                        fetchWeatherFromAPI(city); // This will throw if city doesn't exist
                        runOnUiThread(() -> {
                            addCity(city);
                            currentCity = city;
                            saveCities();
                            updateSpinner();
                            loadWeather(city);
                            Toast.makeText(this, "Added: " + city, Toast.LENGTH_SHORT).show();
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "City not found: " + city, Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            }
        });
        
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    
    // ========== GPS LOCATION (INLINE - using FusedLocationProviderClient) ==========
    
    private void requestGPSLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, 
                             Manifest.permission.ACCESS_COARSE_LOCATION}, 
                LOCATION_PERMISSION_REQUEST);
            return;
        }
        
        getGPSLocation();
    }
    
    private void getGPSLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, location -> {
                if (location != null) {
                    // Get city name from coordinates
                    new Thread(() -> {
                        try {
                            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);
                            
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                String cityName = address.getLocality();
                                
                                if (cityName == null || cityName.isEmpty()) {
                                    cityName = address.getAdminArea();
                                }
                                if (cityName == null || cityName.isEmpty()) {
                                    cityName = address.getCountryName();
                                }
                                
                                if (cityName != null && !cityName.isEmpty()) {
                                    String finalCity = cityName;
                                    runOnUiThread(() -> {
                                        Log.d(TAG, "GPS location: " + finalCity);
                                        addCity(finalCity);
                                        currentCity = finalCity;
                                        saveCities();
                                        updateSpinner();
                                        loadWeather(finalCity);
                                    });
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Geocoding error", e);
                        }
                    }).start();
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to get location", e);
            });
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getGPSLocation();
            }
        }
    }
    
    // ========== API CALL (INLINE - NO WeatherAPI CLASS) ==========
    
    private void loadWeather(String city) {
        loadingView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        
        new Thread(() -> {
            try {
                String json = fetchWeatherFromAPI(city);
                List<DailyWeather> dailyList = parseWeatherJSON(json);
                
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
        
        String apiKey = readAPIKey();
        
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your_api_key_here")) {
            throw new Exception("API key not configured in assets/api_key.json");
        }
        
        String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString());
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + encodedCity + 
                     "&appid=" + apiKey + "&units=metric";
        
        Log.d(TAG, "Fetching weather for: " + cityName);
        Log.d(TAG, "API URL: " + url.replace(apiKey, "***"));
        
        Request request = new Request.Builder().url(url).get().build();
        
        try (Response response = client.newCall(request).execute()) {
            int code = response.code();
            String body = response.body() != null ? response.body().string() : "";
            
            Log.d(TAG, "API Response code: " + code);
            
            if (code == 429) {
                Log.e(TAG, "Rate limit exceeded. Response: " + body);
                throw new IOException("API Error: 429 - Too many requests. Wait a few minutes.");
            } else if (code == 401) {
                Log.e(TAG, "Unauthorized. Check API key. Response: " + body);
                throw new IOException("API Error: 401 - Invalid API key");
            } else if (!response.isSuccessful()) {
                Log.e(TAG, "API Error " + code + ": " + body);
                throw new IOException("API Error: " + code + " - " + body);
            }
            
            Log.d(TAG, "API Response received successfully");
            return body;
        }
    }
    
    private String readAPIKey() throws Exception {
        try {
            InputStream is = getAssets().open("api_key.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            int bytesRead = is.read(buffer);
            is.close();
            
            if (bytesRead != size) {
                Log.w(TAG, "Warning: Expected to read " + size + " bytes but read " + bytesRead);
            }
            
            String content = new String(buffer, 0, bytesRead, "UTF-8");
            if (content.length() > 0 && content.charAt(0) == '\uFEFF') {
                content = content.substring(1);
            }
            
            Log.d(TAG, "Raw API key JSON: " + content);
            
            org.json.JSONObject apiKeyObject = new org.json.JSONObject(content);
            String apiKey = apiKeyObject.getString("apiKey");
            
            if (apiKey != null) {
                apiKey = apiKey.trim();
            }
            
            Log.d(TAG, "API Key length: " + apiKey.length());
            Log.d(TAG, "API Key (first 4 chars): " + (apiKey.length() > 4 ? apiKey.substring(0, 4) + "..." : apiKey));
            
            return apiKey;
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
        
        Map<String, DailyWeather> dayMap = new HashMap<>();
        
        for (JsonElement element : list) {
            JsonObject item = element.getAsJsonObject();
            
            long timestamp = item.get("dt").getAsLong() * 1000;
            Date date = new Date(timestamp);
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
            SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);
            
            String dayKey = dayFormat.format(date);
            String timeStr = timeFormat.format(date);
            String displayDate = displayFormat.format(date);
            
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
            
            if (!dayMap.containsKey(dayKey)) {
                DailyWeather daily = new DailyWeather();
                daily.date = displayDate;
                daily.items = new ArrayList<>();
                dayMap.put(dayKey, daily);
            }
            dayMap.get(dayKey).items.add(weatherItem);
        }
        
        List<DailyWeather> result = new ArrayList<>(dayMap.values());
        for (DailyWeather daily : result) {
            daily.calculateAverages();
        }
        
        return result;
    }
    
    // ========== MODEL CLASSES (INLINE - NO SEPARATE MODEL PACKAGE) ==========
    
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
