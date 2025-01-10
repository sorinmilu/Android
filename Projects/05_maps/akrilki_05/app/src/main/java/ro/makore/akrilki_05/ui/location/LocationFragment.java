package ro.makore.akrilki_05.ui.location;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.content.pm.ApplicationInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import ro.makore.akrilki_05.databinding.FragmentLocationBinding;

public class LocationFragment extends Fragment implements OnMapReadyCallback{

    private FragmentLocationBinding binding;
    private GoogleMap placesGoogleMap;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentLocationBinding.inflate(inflater, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Get MapView and set up the map
        binding.placesMapView.onCreate(savedInstanceState);
        binding.placesMapView.getMapAsync((OnMapReadyCallback) this);

        return binding.getRoot();
    }
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        placesGoogleMap = map;

        // Check location permission and fetch location
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        placesGoogleMap.setMyLocationEnabled(true);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                centerMapOnLocation(location);
            }
        });
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
        placesGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
        placesGoogleMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (binding.placesMapView != null) {
            binding.placesMapView.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding.placesMapView != null) {
            binding.placesMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding.placesMapView != null) {
            binding.placesMapView.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (binding.placesMapView != null) {
            binding.placesMapView.onStop();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding.placesMapView != null) {
            binding.placesMapView.onDestroy();
        }
        binding = null;
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (binding.placesMapView != null) {
            binding.placesMapView.onLowMemory();
        }
    }
}