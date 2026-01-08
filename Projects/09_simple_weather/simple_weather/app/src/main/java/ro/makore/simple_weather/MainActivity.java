package ro.makore.simple_weather;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.Transformer;
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
        
        // Get GPS location - this will load weather when ready
        // If GPS fails or permission denied, fallback to default city
        requestGPSLocation();
        
        // DO NOT load weather here - wait for GPS first!
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
        runOnUiThread(() -> {
            // Recreate the adapter to avoid issues
            spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            citySpinner.setAdapter(spinnerAdapter);
            
            int position = cities.indexOf(currentCity);
            if (position >= 0) {
                citySpinner.setSelection(position);
            }
        });
    }
    
    // ========== ADD CITY DIALOG (INLINE - with real-time validation) ==========
    
    private android.os.Handler validationHandler = new android.os.Handler();
    private Runnable validationRunnable;
    
    private void showAddCityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add City");
        
        // Create custom layout with EditText and feedback TextView
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        
        final EditText input = new EditText(this);
        input.setHint("Enter city name");
        layout.addView(input);
        
        final TextView feedback = new TextView(this);
        feedback.setPadding(0, 20, 0, 0);
        feedback.setTextSize(14);
        layout.addView(feedback);
        
        builder.setView(layout);
        
        builder.setPositiveButton("Add", null); // Set to null, we'll override later
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        
        AlertDialog dialog = builder.create();
        dialog.show();
        
        // Get the Add button and disable it initially
        Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        addButton.setEnabled(false);
        
        // Set up real-time validation with debouncing
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                String city = s.toString().trim();
                
                // Cancel previous validation
                if (validationRunnable != null) {
                    validationHandler.removeCallbacks(validationRunnable);
                }
                
                if (city.isEmpty()) {
                    feedback.setText("");
                    addButton.setEnabled(false);
                    return;
                }
                
                // Check if city already exists
                if (cities.contains(city)) {
                    feedback.setText("City already in list");
                    feedback.setTextColor(0xFFFF5722); // Orange
                    addButton.setEnabled(false);
                    return;
                }
                
                feedback.setText("Checking...");
                feedback.setTextColor(0xFF9E9E9E); // Gray
                addButton.setEnabled(false);
                
                // Debounce: wait 600ms before validating
                validationRunnable = () -> {
                    new Thread(() -> {
                        try {
                            fetchWeatherFromAPI(city); // Throws if city not found
                            runOnUiThread(() -> {
                                feedback.setText("✓ City found");
                                feedback.setTextColor(0xFF4CAF50); // Green
                                addButton.setEnabled(true);
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                feedback.setText("✗ City not found");
                                feedback.setTextColor(0xFFF44336); // Red
                                addButton.setEnabled(false);
                            });
                        }
                    }).start();
                };
                
                validationHandler.postDelayed(validationRunnable, 600);
            }
        });
        
        // Override Add button click
        addButton.setOnClickListener(v -> {
            String city = input.getText().toString().trim();
            if (!city.isEmpty() && !cities.contains(city)) {
                addCity(city);
                currentCity = city;
                saveCities();
                updateSpinner();
                loadWeather(city);
                Toast.makeText(this, "Added: " + city, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
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
    
    private void loadDefaultCity() {
        Log.d(TAG, "Loading default city: " + currentCity);
        runOnUiThread(() -> {
            if (currentCity != null && !currentCity.isEmpty()) {
                loadWeather(currentCity);
            }
        });
    }
    
    private void getGPSLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        // Set a timeout for GPS - if it takes too long, fallback to default
        final Handler timeoutHandler = new Handler();
        final Runnable timeoutRunnable = () -> {
            Log.w(TAG, "GPS timeout, using default city");
            loadDefaultCity();
        };
        
        // 10 second timeout
        timeoutHandler.postDelayed(timeoutRunnable, 10000);
        
        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, location -> {
                if (location != null) {
                    Log.d(TAG, "Got last location: lat=" + location.getLatitude() + ", lon=" + location.getLongitude());
                    
                    // Get city name from coordinates
                    new Thread(() -> {
                        try {
                            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);
                            
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                String cityName = address.getLocality();
                                
                                Log.d(TAG, "Geocoded locality: " + cityName);
                                
                                if (cityName == null || cityName.isEmpty()) {
                                    cityName = address.getAdminArea();
                                    Log.d(TAG, "Using adminArea: " + cityName);
                                }
                                if (cityName == null || cityName.isEmpty()) {
                                    cityName = address.getCountryName();
                                    Log.d(TAG, "Using country: " + cityName);
                                }
                                
                                if (cityName != null && !cityName.isEmpty()) {
                                    final String finalCity = cityName;
                                    runOnUiThread(() -> {
                                        timeoutHandler.removeCallbacks(timeoutRunnable);
                                        
                                        Log.d(TAG, "GPS location: " + finalCity);
                                        
                                        // Add city if not already in list
                                        if (!cities.contains(finalCity)) {
                                            cities.add(0, finalCity); // Add at beginning
                                            saveCities();
                                        }
                                        
                                        currentCity = finalCity;
                                        saveCities();
                                        updateSpinner();
                                        loadWeather(finalCity);
                                        
                                        Toast.makeText(this, "Current location: " + finalCity, Toast.LENGTH_SHORT).show();
                                    });
                                } else {
                                    Log.w(TAG, "Could not determine city name, using default");
                                    runOnUiThread(() -> {
                                        timeoutHandler.removeCallbacks(timeoutRunnable);
                                        loadDefaultCity();
                                    });
                                }
                            } else {
                                Log.w(TAG, "Geocoder returned no addresses, using default");
                                runOnUiThread(() -> {
                                    timeoutHandler.removeCallbacks(timeoutRunnable);
                                    loadDefaultCity();
                                });
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Geocoding error, using default", e);
                            runOnUiThread(() -> {
                                timeoutHandler.removeCallbacks(timeoutRunnable);
                                loadDefaultCity();
                            });
                        }
                    }).start();
                } else {
                    Log.w(TAG, "GPS location is null, trying to get fresh location");
                    // Last location is null, try to get current location
                    requestCurrentLocation();
                }
            })
            .addOnFailureListener(e -> {
                timeoutHandler.removeCallbacks(timeoutRunnable);
                Log.e(TAG, "Failed to get location, using default", e);
                loadDefaultCity();
            });
    }
    
    private void requestCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        Log.d(TAG, "Requesting fresh GPS location...");
        
        // Set a timeout for fresh GPS request
        final Handler timeoutHandler = new Handler();
        final Runnable timeoutRunnable = () -> {
            Log.w(TAG, "Fresh GPS request timeout, using default city");
            loadDefaultCity();
        };
        
        // 8 second timeout for fresh location
        timeoutHandler.postDelayed(timeoutRunnable, 8000);
        
        com.google.android.gms.location.LocationRequest locationRequest = 
            com.google.android.gms.location.LocationRequest.create();
        locationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000); // 5 seconds
        locationRequest.setFastestInterval(2000); // 2 seconds
        locationRequest.setNumUpdates(1);
        
        fusedLocationClient.requestLocationUpdates(locationRequest, 
            new com.google.android.gms.location.LocationCallback() {
                @Override
                public void onLocationResult(com.google.android.gms.location.LocationResult locationResult) {
                    if (locationResult != null && locationResult.getLastLocation() != null) {
                        android.location.Location loc = locationResult.getLastLocation();
                        
                        Log.d(TAG, "Got location: lat=" + loc.getLatitude() + ", lon=" + loc.getLongitude());
                        
                        // Process this location same as getLastLocation
                        new Thread(() -> {
                            try {
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(
                                    loc.getLatitude(), loc.getLongitude(), 1);
                                
                                if (addresses != null && !addresses.isEmpty()) {
                                    Address address = addresses.get(0);
                                    String cityName = address.getLocality();
                                    
                                    Log.d(TAG, "Geocoded locality: " + cityName);
                                    
                                    if (cityName == null || cityName.isEmpty()) {
                                        cityName = address.getAdminArea();
                                        Log.d(TAG, "Using adminArea: " + cityName);
                                    }
                                    if (cityName == null || cityName.isEmpty()) {
                                        cityName = address.getCountryName();
                                        Log.d(TAG, "Using country: " + cityName);
                                    }
                                    
                                    if (cityName != null && !cityName.isEmpty()) {
                                        final String finalCity = cityName;
                                        runOnUiThread(() -> {
                                            timeoutHandler.removeCallbacks(timeoutRunnable);
                                            
                                            Log.d(TAG, "Current GPS location: " + finalCity);
                                            
                                            if (!cities.contains(finalCity)) {
                                                cities.add(0, finalCity);
                                                saveCities();
                                            }
                                            
                                            currentCity = finalCity;
                                            saveCities();
                                            updateSpinner();
                                            loadWeather(finalCity);
                                            
                                            Toast.makeText(MainActivity.this, "Current location: " + finalCity, Toast.LENGTH_SHORT).show();
                                        });
                                    } else {
                                        Log.w(TAG, "Could not determine city name from location");
                                        runOnUiThread(() -> {
                                            timeoutHandler.removeCallbacks(timeoutRunnable);
                                            loadDefaultCity();
                                        });
                                    }
                                } else {
                                    Log.w(TAG, "Geocoder returned no addresses");
                                    runOnUiThread(() -> {
                                        timeoutHandler.removeCallbacks(timeoutRunnable);
                                        loadDefaultCity();
                                    });
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Geocoding error", e);
                                runOnUiThread(() -> {
                                    timeoutHandler.removeCallbacks(timeoutRunnable);
                                    loadDefaultCity();
                                });
                            }
                        }).start();
                    } else {
                        Log.w(TAG, "LocationResult is null or has no location");
                        timeoutHandler.removeCallbacks(timeoutRunnable);
                        loadDefaultCity();
                    }
                }
            }, null);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted - now get GPS location
                Toast.makeText(this, "Getting your location...", Toast.LENGTH_SHORT).show();
                getGPSLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                // Permission denied, fallback to default city
                loadDefaultCity();
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
    
    // ========== CHART SETUP (INLINE - like original app but all in one place) ==========
    
    private void setupTemperatureChart(LineChart lineChart, FrameLayout chartFrame, List<WeatherItem> hourlyData) {
        Log.d(TAG, "setupTemperatureChart called");
        
        if (hourlyData == null || hourlyData.isEmpty()) {
            Log.w(TAG, "hourlyData is null or empty");
            return;
        }
        
        Log.d(TAG, "Setting up chart with " + hourlyData.size() + " items");
        
        // Prepare data for chart
        List<Entry> entries = new ArrayList<>();
        List<String> iconUrls = new ArrayList<>();
        
        for (int i = 0; i < hourlyData.size(); i++) {
            WeatherItem item = hourlyData.get(i);
            entries.add(new Entry(i, (float) item.temperature));
            iconUrls.add(item.iconUrl != null ? item.iconUrl : "");
        }
        
        Log.d(TAG, "Created " + entries.size() + " entries");
        
        // Create dataset with fill under the line
        LineDataSet dataSet = new LineDataSet(entries, "Temperature");
        dataSet.setColor(Color.parseColor("#2196F3"));
        dataSet.setLineWidth(3f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);
        
        // Format temperature values to show 1 decimal
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.US, "%.0f°", value);
            }
        });
        
        // Fill under line
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#2196F3"));
        dataSet.setFillAlpha(128);
        
        // Create LineData and set to chart
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        
        Log.d(TAG, "LineData set to chart");
        
        // Disable axes
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(false);
        
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setEnabled(false);
        
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
        
        // Configure chart
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setTouchEnabled(false);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChart.setExtraOffsets(20f, 20f, 20f, 60f); // Bottom padding for icons
        
        Log.d(TAG, "Chart configured, calling invalidate");
        
        // Refresh chart and overlay icons
        lineChart.invalidate();
        lineChart.postDelayed(() -> {
            Log.d(TAG, "Calling overlayIconsOnChart");
            overlayIconsOnChart(lineChart, chartFrame, entries, iconUrls);
        }, 100);
    }
    
    private void overlayIconsOnChart(LineChart lineChart, FrameLayout chartFrame, 
                                      List<Entry> entries, List<String> iconUrls) {
        // Clear previous icons
        for (int i = chartFrame.getChildCount() - 1; i > 0; i--) {
            View child = chartFrame.getChildAt(i);
            if (child instanceof ImageView) {
                chartFrame.removeView(child);
            }
        }
        
        if (entries.isEmpty() || iconUrls.isEmpty()) {
            return;
        }
        
        Transformer transformer = lineChart.getTransformer(YAxis.AxisDependency.LEFT);
        int iconSize = 60; // 60dp icon size
        int iconSpacing = 10;
        
        // Overlay icons at each data point
        for (int i = 0; i < entries.size() && i < iconUrls.size(); i++) {
            Entry entry = entries.get(i);
            String iconUrl = iconUrls.get(i);
            
            if (iconUrl == null || iconUrl.isEmpty()) {
                continue;
            }
            
            // Transform entry X coordinate to pixel position
            float[] point = new float[] { entry.getX(), entry.getY() };
            transformer.pointValuesToPixel(point);
            
            float xPos = point[0];
            float yPos = lineChart.getHeight() - iconSize - iconSpacing;
            
            // Ensure icons are within bounds
            if (yPos + iconSize > chartFrame.getHeight()) {
                yPos = chartFrame.getHeight() - iconSize - iconSpacing;
            }
            
            // Create ImageView for icon
            ImageView iconView = new ImageView(MainActivity.this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(iconSize, iconSize);
            params.leftMargin = (int) (xPos - iconSize / 2f);
            params.topMargin = (int) yPos;
            iconView.setLayoutParams(params);
            
            // Load icon with Glide
            Glide.with(MainActivity.this)
                .load(iconUrl)
                .into(iconView);
            
            chartFrame.addView(iconView);
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
            holder.minMaxText.setText(String.format(Locale.US, "%.1f / %.1f°C", 
                daily.minTemp, daily.maxTemp));
            holder.itemCountText.setText(daily.items.size() + " entries");
            
            Log.d(TAG, "onBindViewHolder position=" + position + ", items=" + daily.items.size());
            Log.d(TAG, "Chart view: " + holder.temperatureChart);
            Log.d(TAG, "Frame view: " + holder.chartFrame);
            
            // Setup temperature chart with icons
            if (holder.temperatureChart != null && holder.chartFrame != null) {
                setupTemperatureChart(holder.temperatureChart, holder.chartFrame, daily.items);
            } else {
                Log.e(TAG, "Chart or frame is NULL!");
            }
            
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
            TextView minMaxText;
            TextView itemCountText;
            FrameLayout chartFrame;
            com.github.mikephil.charting.charts.LineChart temperatureChart;
            
            ViewHolder(View itemView) {
                super(itemView);
                dateText = itemView.findViewById(R.id.dateText);
                minMaxText = itemView.findViewById(R.id.minMaxText);
                itemCountText = itemView.findViewById(R.id.itemCountText);
                chartFrame = itemView.findViewById(R.id.chartFrame);
                temperatureChart = itemView.findViewById(R.id.temperatureChart);
            }
        }
    }
}
