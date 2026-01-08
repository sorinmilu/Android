package ro.makore.akrilki_08.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
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
     * Get all saved locations in insertion order.
     */
    public List<String> getSavedLocations() {
        List<String> list = new ArrayList<>();
        try {
            String json = prefs.getString(KEY_LOCATIONS, null);
            if (json == null) return list;
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                String v = arr.optString(i, null);
                if (v != null) list.add(v);
            }
            return list;
        } catch (ClassCastException e) {
            // Legacy data may have been saved as a StringSet (unordered). Read it and migrate.
            try {
                Set<String> set = prefs.getStringSet(KEY_LOCATIONS, null);
                if (set == null) return list;
                list.addAll(set);
                // Persist migrated ordered list as JSON string for future runs
                saveLocations(list);
            } catch (Exception ex) {
                Log.w(TAG, "Failed to read legacy saved locations set", ex);
            }
            return list;
        } catch (JSONException e) {
            Log.w(TAG, "Failed to parse saved locations JSON", e);
            return list;
        }
    }
    
    private void saveLocations(List<String> locations) {
        JSONArray arr = new JSONArray();
        for (String s : locations) arr.put(s);
        prefs.edit().putString(KEY_LOCATIONS, arr.toString()).apply();
    }
    
    /**
     * Add a new location (preserves order, append if not present)
     */
    public void addLocation(String location) {
        if (location == null) return;
        List<String> list = getSavedLocations();
        if (!list.contains(location)) {
            list.add(location);
            saveLocations(list);
            Log.d(TAG, "Added location: " + location);
        }
    }
    
    /**
     * Remove a location
     */
    public void removeLocation(String location) {
        List<String> list = getSavedLocations();
        if (list.remove(location)) {
            saveLocations(list);
            Log.d(TAG, "Removed location: " + location);
        }
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

