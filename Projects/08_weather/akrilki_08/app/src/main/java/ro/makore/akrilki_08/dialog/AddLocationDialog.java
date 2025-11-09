package ro.makore.akrilki_08.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import ro.makore.akrilki_08.R;
import ro.makore.akrilki_08.api.WeatherAPI;
import ro.makore.akrilki_08.util.LocationManager;
import android.util.Log;
import java.io.IOException;

public class AddLocationDialog extends Dialog {
    private static final String TAG = "AddLocationDialog";
    private final LocationManager locationManager;
    private final OnLocationAddedListener listener;
    private EditText editLocationName;
    private TextView locationStatus;
    private Button btnAdd;
    private Button btnCancel;
    private boolean isChecking = false;

    public interface OnLocationAddedListener {
        void onLocationAdded(String location);
    }

    public AddLocationDialog(@NonNull Context context, LocationManager locationManager, OnLocationAddedListener listener) {
        super(context);
        this.locationManager = locationManager;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_location);

        editLocationName = findViewById(R.id.edit_location_name);
        locationStatus = findViewById(R.id.location_status);
        btnAdd = findViewById(R.id.btn_add);
        btnCancel = findViewById(R.id.btn_cancel);

        // Add text watcher to check location availability
        editLocationName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String locationName = s.toString().trim();
                if (locationName.length() > 2 && !isChecking) {
                    checkLocationAvailability(locationName);
                } else if (locationName.isEmpty()) {
                    locationStatus.setVisibility(View.GONE);
                    btnAdd.setEnabled(false);
                }
            }
        });

        btnAdd.setOnClickListener(v -> {
            String locationName = editLocationName.getText().toString().trim();
            if (!locationName.isEmpty()) {
                addLocation(locationName);
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());

        btnAdd.setEnabled(false);
    }

    private void checkLocationAvailability(String locationName) {
        isChecking = true;
        locationStatus.setVisibility(View.VISIBLE);
        locationStatus.setText(getContext().getString(R.string.checking_location));
        locationStatus.setTextColor(getContext().getResources().getColor(android.R.color.darker_gray));
        btnAdd.setEnabled(false);

        // Check if location already exists
        if (locationManager.hasLocation(locationName)) {
            locationStatus.setText(getContext().getString(R.string.location_already_exists));
            locationStatus.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark));
            btnAdd.setEnabled(false);
            isChecking = false;
            return;
        }

        // Check location availability online
        new Thread(() -> {
            try {
                // Try to fetch weather for this location to verify it exists
                String jsonResponse = WeatherAPI.fetchWeather(getContext(), locationName);
                
                // If we get here, the location is valid
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> {
                    locationStatus.setText(getContext().getString(R.string.location_added));
                    locationStatus.setTextColor(getContext().getResources().getColor(android.R.color.holo_green_dark));
                    btnAdd.setEnabled(true);
                    isChecking = false;
                });
            } catch (IOException e) {
                // Check if it's a 404 or similar error (location not found)
                String errorMsg = e.getMessage();
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> {
                    if (errorMsg != null && (errorMsg.contains("404") || errorMsg.contains("not found") || errorMsg.contains("city not found"))) {
                        locationStatus.setText(getContext().getString(R.string.location_not_found));
                        locationStatus.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark));
                    } else {
                        locationStatus.setText(getContext().getString(R.string.error_adding_location) + ": " + errorMsg);
                        locationStatus.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark));
                    }
                    btnAdd.setEnabled(false);
                    isChecking = false;
                });
            } catch (Exception e) {
                Log.e(TAG, "Error checking location", e);
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> {
                    locationStatus.setText(getContext().getString(R.string.error_adding_location));
                    locationStatus.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark));
                    btnAdd.setEnabled(false);
                    isChecking = false;
                });
            }
        }).start();
    }

    private void addLocation(String locationName) {
        locationManager.addLocation(locationName);
        Toast.makeText(getContext(), getContext().getString(R.string.location_added), Toast.LENGTH_SHORT).show();
        if (listener != null) {
            listener.onLocationAdded(locationName);
        }
        dismiss();
    }
}

