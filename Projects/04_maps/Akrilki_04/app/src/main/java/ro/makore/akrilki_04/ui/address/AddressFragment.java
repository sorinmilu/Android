package ro.makore.akrilki_04.ui.address;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.content.pm.ApplicationInfo;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import ro.makore.akrilki_04.R;
import ro.makore.akrilki_04.databinding.FragmentAddressBinding;

public class AddressFragment extends Fragment implements OnMapReadyCallback {

    private FragmentAddressBinding binding;
    private GoogleMap googleMap;

    private FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddressBinding.inflate(inflater, container, false);

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

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
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addressLatLng, 15));
            googleMap.addMarker(new MarkerOptions().position(addressLatLng).title(address));
        } else {
            // If the address is not found, you can center the map to a default location, e.g., Bucharest
            LatLng defaultLatLng = new LatLng(44.4268, 26.1025); // Bucharest default coordinates
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 12));
        }

        Button satelliteButton = binding.getRoot().findViewById(R.id.satelliteButton);
        Button normalButton = binding.getRoot().findViewById(R.id.normalButton);
        Button terrainButton = binding.getRoot().findViewById(R.id.terrainButton);

        satelliteButton.setOnClickListener(v -> googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE));
        normalButton.setOnClickListener(v -> googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL));
        terrainButton.setOnClickListener(v -> googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private LatLng getLocationFromAddress(String address) {
        Geocoder geocoder = new Geocoder(requireContext());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address location = addressList.get(0);
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private String getApiKeyFromManifest() {
        try {
            ApplicationInfo applicationInfo = requireContext().getPackageManager()
                    .getApplicationInfo(requireContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = applicationInfo.metaData;
            return metaData.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            throw new RuntimeException("Failed to load meta-data, NullPointer: " + e.getMessage());
        }
    }
    private void centerMapOnLocation(@NonNull Location location) {
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
        googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
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