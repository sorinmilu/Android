package ro.makore.akrilki_08.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.Task;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationService {
    private static final String TAG = "LocationService";
    private final FusedLocationProviderClient fusedLocationClient;
    private final Context context;
    private final Geocoder geocoder;
    
    public LocationService(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.geocoder = new Geocoder(context, Locale.getDefault());
    }
    
    /**
     * Get current location and convert to city name
     */
    public void getCurrentLocationName(LocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Location permission not granted");
            callback.onLocationError("Location permission not granted");
            return;
        }
        
        Task<Location> locationTask = fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        );
        
        locationTask.addOnSuccessListener(location -> {
            if (location != null) {
                // Convert coordinates to city name
                getCityNameFromLocation(location.getLatitude(), location.getLongitude(), callback);
            } else {
                Log.w(TAG, "Location is null");
                callback.onLocationError("Unable to get current location");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to get location", e);
            callback.onLocationError("Failed to get location: " + e.getMessage());
        });
    }
    
    /**
     * Convert coordinates to city name using Geocoder
     */
    private void getCityNameFromLocation(double latitude, double longitude, LocationCallback callback) {
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String cityName = address.getLocality();
                if (cityName == null || cityName.isEmpty()) {
                    cityName = address.getAdminArea(); // Fallback to admin area
                }
                if (cityName == null || cityName.isEmpty()) {
                    cityName = address.getCountryName(); // Fallback to country
                }
                if (cityName != null && !cityName.isEmpty()) {
                    Log.d(TAG, "Found city name: " + cityName);
                    callback.onLocationReceived(cityName);
                } else {
                    callback.onLocationError("Could not determine city name");
                }
            } else {
                callback.onLocationError("No address found for location");
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder error", e);
            callback.onLocationError("Geocoder error: " + e.getMessage());
        }
    }
    
    public interface LocationCallback {
        void onLocationReceived(String cityName);
        void onLocationError(String error);
    }
}

