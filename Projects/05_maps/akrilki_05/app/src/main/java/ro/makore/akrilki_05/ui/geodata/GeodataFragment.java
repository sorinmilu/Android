package ro.makore.akrilki_05.ui.geodata;

import android.content.pm.PackageManager;

import java.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.Date;
import java.util.Locale;

import ro.makore.akrilki_05.databinding.FragmentGeodataBinding;

public class GeodataFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FragmentGeodataBinding binding;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission granted, fetch location
                        fetchLocationAndUpdateUI();
                    } else {
                        Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGeodataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            fetchLocationAndUpdateUI();
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchLocationAndUpdateUI() {
        Log.i("GeodataFragment", "Get Location here");
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    float accuracy = location.getAccuracy();
                    float speed = location.getSpeed();
                    float bearing = location.getBearing();
                    long time = location.getTime();
                    String provider = location.getProvider();
                    float altitude = (float) location.getAltitude();

                    Date date = new Date(time);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String formattedTime = formatter.format(date);

                    // Update TextViews
                    binding.tvLatitude.setText("Latitude: " + latitude);
                    binding.tvLongitude.setText("Longitude: " + longitude);
                    binding.tvAccuracy.setText("Accuracy: " + accuracy);
                    binding.tvAltitude.setText("Altitude: " + altitude);
                    binding.tvSpeed.setText("Speed: " + speed);
                    binding.tvBearing.setText("Bearing: " + bearing);
                    binding.tvProvider.setText("Provider: " + provider);
                    binding.tvTime.setText("Time: " + formattedTime);

                } else {
                    Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException e) {
            Log.e("GeodataFragment", "Error getting location", e);
        }
    }


}