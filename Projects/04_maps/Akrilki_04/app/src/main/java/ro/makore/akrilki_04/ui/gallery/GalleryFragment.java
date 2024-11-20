package ro.makore.akrilki_04.ui.gallery;

import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
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

import ro.makore.akrilki_04.databinding.FragmentGalleryBinding;

public class GalleryFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FragmentGalleryBinding binding;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (!isGranted) {
                        Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView tvLatitude   = binding.tvLatitude;
        final TextView tvLongitude   = binding.tvLongitude;
        final TextView tvAccuracy    = binding.tvAccuracy;
        final TextView tvAltitude    = binding.tvAltitude;
        final TextView tvSpeed    = binding.tvSpeed;
        final TextView tvBearing    = binding.tvBearing;
        final TextView tvProvider    = binding.tvProvider;
        final TextView tvTime = binding.tvTime;

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            fetchLocationAndUpdateUI(tvLatitude, tvLongitude, tvAccuracy, tvAltitude, tvSpeed, tvBearing, tvProvider, tvTime);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchLocationAndUpdateUI(TextView tvLatitude, TextView tvLongitude, TextView tvAccuracy, TextView tvAltitude,
                                                        TextView tvSpeed, TextView tvBearing, TextView tvProvider, TextView tvTime) {
        Log.i("GalleryFragment", "Get Location here");
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
                    tvLatitude.setText("Latitude: " + latitude);
                    tvLongitude.setText("Longitude: " + longitude);
                    tvAccuracy.setText("Accuracy: " + accuracy);
                    tvAltitude.setText("Altitude: " + altitude);
                    tvSpeed.setText("Speed: " + speed);
                    tvBearing.setText("Bearing: " + bearing);
                    tvProvider.setText("Provider: " + provider);
                    tvTime.setText("Time: " + formattedTime);

                } else {
                    Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException e) {
            Log.e("GalleryFragment", "Error getting location", e);
        }
    }


}