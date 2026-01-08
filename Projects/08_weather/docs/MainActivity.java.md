# MainActivity.java — structured annotated reference

This document reproduces `MainActivity.java`'s code in exact form in separate, sensible code blocks (package, grouped imports, class fields, methods). Each code block is preceded by a short Markdown explanation describing what the code below implements.

## Package declaration

The package declaration places the class in the application's namespace.

```java
package ro.makore.akrilki_08;
```

## Android framework imports

These imports bring in core Android classes used for permissions, UI widgets and lifecycle management.

```java
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
```

## AndroidX, UI and Recycler imports

Support libraries and RecyclerView-related classes used for the activity and list display.

```java
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
```

## Misc Android imports

Utilities for logging, view, and simple widgets used across the activity.

```java
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView;
```

## App-specific imports

Application model, parser, networking, adapters and utilities referenced by the activity.

```java
import ro.makore.akrilki_08.model.WeatherItem;
import ro.makore.akrilki_08.model.DailyWeatherItem;
import ro.makore.akrilki_08.parser.WeatherParser;
import ro.makore.akrilki_08.api.WeatherAPI;
import ro.makore.akrilki_08.adapter.WeatherAdapter;
import ro.makore.akrilki_08.adapter.DailyWeatherAdapter;
import ro.makore.akrilki_08.adapter.LocationSpinnerAdapter;
import ro.makore.akrilki_08.util.LocationManager;
import ro.makore.akrilki_08.util.LocationService;
import ro.makore.akrilki_08.dialog.AddLocationDialog;
```

## Third-party and Java imports

Third-party libs and Java standard library imports used in the activity.

```java
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
```

## Class declaration and fields

Below is the class signature and all member fields. These are declared at the top of the file and used by methods throughout the activity.

```java
public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private RecyclerView recyclerView;
    private DailyWeatherAdapter dailyWeatherAdapter;
    private ProgressBar progressBar; // ProgressBar for loading indicator
    private TextView loadingText; // TextView for loading message
    private Spinner locationSpinner;
    private LocationManager locationManager;
    private LocationService locationService;
    private LocationSpinnerAdapter locationAdapter;
    private String currentSelectedLocation;
    private String currentGpsLocation; // GPS-based current location

    // Default city for weather forecast
    private static final String DEFAULT_CITY = "Bucharest";
```

## onCreate(Bundle)

`onCreate` wires up the UI, initializes helpers, sets listeners and triggers initial GPS lookup and data load.

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);

        // Initialize location services
        locationManager = new LocationManager(this);
        locationService = new LocationService(this);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup location spinner
        locationSpinner = findViewById(R.id.location_spinner);
        setupLocationSpinner();

        // Setup add location button
        ImageButton btnAddLocation = findViewById(R.id.btn_add_location);
        btnAddLocation.setOnClickListener(v -> showAddLocationDialog());

        FloatingActionButton fabQuit = findViewById(R.id.fab_quit);
        fabQuit.setOnClickListener(v -> finishAffinity());

        FloatingActionButton fabRefresh = findViewById(R.id.fab_refresh);
        fabRefresh.setOnClickListener(v -> refreshWeatherData());

        recyclerView = findViewById(R.id.weatherRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);

        // Get GPS location and update spinner
        getGpsLocation();

        // Load weather for default/selected location
        refreshWeatherData();
    }
```

## setupLocationSpinner()

Creates and configures the `Spinner` adapter, ensures a default city exists, and chooses the initial selection (preferring stored GPS location when available). It also registers the item selection listener.

```java
    private void setupLocationSpinner() {
        List<String> locations = locationManager.getSavedLocations();

        // Try to get previously stored GPS/current location
        currentGpsLocation = locationManager.getCurrentLocation();

        // If no saved locations, add default
        if (locations.isEmpty()) {
            locationManager.addLocation(DEFAULT_CITY);
            locations = locationManager.getSavedLocations();
        }

        // Create adapter with current GPS location
        locationAdapter = new LocationSpinnerAdapter(this, locations, currentGpsLocation);
        locationSpinner.setAdapter(locationAdapter);

        // Choose selection: prefer current GPS if present, otherwise first item
        int selectionIndex = 0;
        if (currentGpsLocation != null && locations.contains(currentGpsLocation)) {
            selectionIndex = locations.indexOf(currentGpsLocation);
        }
        currentSelectedLocation = locations.get(selectionIndex);
        locationSpinner.setSelection(selectionIndex);

        // Handle location selection
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<String> currentLocations = locationManager.getSavedLocations();
                if (position < currentLocations.size()) {
                    String selectedLocation = currentLocations.get(position);
                    if (!selectedLocation.equals(currentSelectedLocation)) {
                        currentSelectedLocation = selectedLocation;
                        refreshWeatherData();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
```

## getGpsLocation()

Performs runtime permission checks for location, then requests a city name from `LocationService`. When a city is received it persists it, ensures it appears first in the spinner and triggers a refresh.

```java
    private void getGpsLocation() {
        // Check location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Get GPS location
        locationService.getCurrentLocationName(new LocationService.LocationCallback() {
            @Override
            public void onLocationReceived(String cityName) {
                currentGpsLocation = cityName;
                locationManager.setCurrentLocation(cityName);
                
                // Update spinner to include current location if not already there
                runOnUiThread(() -> {
                    List<String> locations = locationManager.getSavedLocations();
                    if (!locations.contains(cityName)) {
                        // Add current location to the list (it will appear with "(current)")
                        locationManager.addLocation(cityName);
                        locations = locationManager.getSavedLocations(); // Refresh list
                        // Move current location to the beginning
                        locations.remove(cityName);
                        locations.add(0, cityName);
                    }
                    locationAdapter = new LocationSpinnerAdapter(MainActivity.this, locations, currentGpsLocation);
                    locationSpinner.setAdapter(locationAdapter);
                    locationSpinner.setSelection(0); // Select current location
                    currentSelectedLocation = cityName;
                    refreshWeatherData();
                });
            }

            @Override
            public void onLocationError(String error) {
                Log.w("WEATHER08", "GPS location error: " + error);
                // Continue without GPS location
            }
        });
    }
```

## onRequestPermissionsResult(...)

Handles the result of the runtime location permission dialog; if granted, retries GPS lookup, otherwise shows a Toast.

```java
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getGpsLocation();
            } else {
                Toast.makeText(this, "Location permission denied. GPS location will not be available.", Toast.LENGTH_SHORT).show();
            }
        }
    }
```

## showAddLocationDialog()

Opens `AddLocationDialog`. The provided callback updates the spinner and selects the newly added location, then triggers a data refresh.

```java
    private void showAddLocationDialog() {
        AddLocationDialog dialog = new AddLocationDialog(this, locationManager, locationName -> {
            // Location added, refresh spinner
            List<String> locations = locationManager.getSavedLocations();
            locationAdapter = new LocationSpinnerAdapter(this, locations, currentGpsLocation);
            locationSpinner.setAdapter(locationAdapter);
            
            // Select the newly added location
            int position = locations.indexOf(locationName);
            if (position >= 0) {
                locationSpinner.setSelection(position);
                currentSelectedLocation = locationName;
                refreshWeatherData();
            }
        });
        dialog.show();
    }
```

## refreshWeatherData()

Performs the network call to fetch weather for the selected city off the UI thread, parses results with `WeatherParser`, and updates the `RecyclerView`. Errors are shown in the `loadingText`.

```java
    private void refreshWeatherData() {
        
        // Show loading indicators
        runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            loadingText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        });

        // Use selected location instead of default
        String cityToFetch = currentSelectedLocation != null ? currentSelectedLocation : DEFAULT_CITY;
        
        new Thread(() -> {
            try {
                String jsonResponse = WeatherAPI.fetchWeather(this, cityToFetch);
                List<DailyWeatherItem> dailyWeatherItems = WeatherParser.parseWeatherByDay(jsonResponse);

                int count = dailyWeatherItems.size();
                
                if (count == 0) {
                    throw new Exception("No weather data received");
                }

                Log.d("WEATHER08", "Successfully parsed " + count + " daily weather items");

                // Update UI on the main thread
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    
                    // Scroll to the top (first item)
                    recyclerView.scrollToPosition(0);    
                    if (dailyWeatherAdapter == null) {
                        dailyWeatherAdapter = new DailyWeatherAdapter(MainActivity.this, dailyWeatherItems);
                        recyclerView.setAdapter(dailyWeatherAdapter);
                    } else {
                        dailyWeatherAdapter.updateData(dailyWeatherItems);
                    }
                });
            } catch (IOException e) {
                Log.e("WEATHER08", "Error fetching weather: " + e.getMessage(), e);
                final String errorMessage = e.getMessage() != null ? e.getMessage() : "Network error";
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                    loadingText.setText("Error: " + errorMessage);
                    loadingText.setVisibility(View.VISIBLE);
                });
            } catch (Exception e) {
                // Handle any other exceptions that might occur
                Log.e("WEATHER08", "Unexpected error", e);
                final String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                    loadingText.setText("Error: " + errorMessage);
                    loadingText.setVisibility(View.VISIBLE);
                });
            }
        }).start();
    }
    
}
```

File updated at: `Projects/08_weather/docs/MainActivity.java.md` — I preserved the original code exactly and added descriptive sections above each code block. Let me know if you want the same format for other files.