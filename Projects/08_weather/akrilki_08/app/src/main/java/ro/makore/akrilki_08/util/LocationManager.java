package ro.makore.akrilki_08.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocationManager {
    private static final String PREFS_NAME = "weather_locations";
    private static final String KEY_LOCATIONS = "saved_locations";
    private static final String KEY_CURRENT_LOCATION = "current_location";
    private static final String TAG = "LocationManager";
    
    private final SharedPreferences prefs;
    
    public LocationManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Get all saved locations
     */
    public List<String> getSavedLocations() {
        Set<String> locationsSet = prefs.getStringSet(KEY_LOCATIONS, new HashSet<>());
        return new ArrayList<>(locationsSet);
    }
    
    /**
     * Add a new location
     */
    public void addLocation(String location) {
        Set<String> locationsSet = new HashSet<>(getSavedLocations());
        locationsSet.add(location);
        prefs.edit().putStringSet(KEY_LOCATIONS, locationsSet).apply();
        Log.d(TAG, "Added location: " + location);
    }
    
    /**
     * Remove a location
     */
    public void removeLocation(String location) {
        Set<String> locationsSet = new HashSet<>(getSavedLocations());
        locationsSet.remove(location);
        prefs.edit().putStringSet(KEY_LOCATIONS, locationsSet).apply();
        Log.d(TAG, "Removed location: " + location);
    }
    
    /**
     * Get current location (GPS-based)
     */
    public String getCurrentLocation() {
        return prefs.getString(KEY_CURRENT_LOCATION, null);
    }
    
    /**
     * Set current location (GPS-based)
     */
    public void setCurrentLocation(String location) {
        prefs.edit().putString(KEY_CURRENT_LOCATION, location).apply();
        Log.d(TAG, "Set current location: " + location);
    }
    
    /**
     * Check if location exists in saved locations
     */
    public boolean hasLocation(String location) {
        return getSavedLocations().contains(location);
    }
}

