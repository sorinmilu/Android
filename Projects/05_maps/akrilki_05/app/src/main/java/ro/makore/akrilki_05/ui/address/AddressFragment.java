package ro.makore.akrilki_05.ui.address;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addressLatLng, 15));
        googleMap.addMarker(new MarkerOptions().position(addressLatLng).title(address));

        binding.getRoot().findViewById(R.id.satelliteButton).setOnClickListener(v -> googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE));
        binding.getRoot().findViewById(R.id.normalButton).setOnClickListener(v -> googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL));
        binding.getRoot().findViewById(R.id.terrainButton).setOnClickListener(v -> googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN));
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
        // Fallback to hardcoded coordinates if geocoding fails
        return new LatLng(44.3967, 26.1242);
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