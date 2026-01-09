package ro.makore.akrilki_05.ui.address;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import ro.makore.akrilki_05.R;
import ro.makore.akrilki_05.databinding.FragmentAddressBinding;

public class AddressFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "AddressFragment";
    private FragmentAddressBinding binding;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddressBinding.inflate(inflater, container, false);

        // Get MapView and set up the map
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);

        return binding.getRoot();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Enable the 'My Location' button
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Enable compass
        googleMap.getUiSettings().setCompassEnabled(true);

        // Enable map rotation
        googleMap.getUiSettings().setRotateGesturesEnabled(true);

        // Enable zoom gestures
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        String address = "Bucharest, Calea Văcăreşti, nr. 189";
        LatLng addressLatLng = getLocationFromAddress(address);

        if (addressLatLng != null) {
            Log.d(TAG, "Geocoding successful: " + addressLatLng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addressLatLng, 15));
            googleMap.addMarker(new MarkerOptions().position(addressLatLng).title(address));
        } else {
            Log.w(TAG, "Geocoding failed for address: " + address);
            Toast.makeText(requireContext(), "Unable to find location. Using default location.", Toast.LENGTH_SHORT).show();
            // If the address is not found, you can center the map to a default location, e.g., Bucharest
            LatLng defaultLatLng = new LatLng(44.4268, 26.1025); // Bucharest default coordinates
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 12));
        }

        binding.getRoot().findViewById(R.id.satelliteButton).setOnClickListener(v -> googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE));
        binding.getRoot().findViewById(R.id.normalButton).setOnClickListener(v -> googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL));
        binding.getRoot().findViewById(R.id.terrainButton).setOnClickListener(v -> googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private LatLng getLocationFromAddress(String address) {
        Geocoder geocoder = new Geocoder(requireContext());
        
        if (!Geocoder.isPresent()) {
            Log.e(TAG, "Geocoder service is not available on this device");
            Toast.makeText(requireContext(), "Geocoder not available", Toast.LENGTH_SHORT).show();
            return null;
        }
        
        List<Address> addressList;
        try {
            Log.d(TAG, "Attempting to geocode address: " + address);
            addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address location = addressList.get(0);
                Log.d(TAG, "Geocoding result - Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
                return new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                Log.w(TAG, "Geocoder returned empty or null result");
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoding failed with IOException", e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (binding.mapView != null) {
            binding.mapView.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding.mapView != null) {
            binding.mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding.mapView != null) {
            binding.mapView.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (binding.mapView != null) {
            binding.mapView.onStop();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding.mapView != null) {
            binding.mapView.onDestroy();
        }
        binding = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (binding.mapView != null) {
            binding.mapView.onLowMemory();
        }
    }
}