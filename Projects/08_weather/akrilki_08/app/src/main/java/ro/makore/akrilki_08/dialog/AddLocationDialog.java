package ro.makore.akrilki_08.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingCheck;
    private static final int CHECK_DELAY_MS = 600;
    private boolean lastValid = false;
    private String lastCheckedName = null;
    private boolean pendingAdd = false;

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

        // Add debounced text watcher to check location availability (avoid firing on every keystroke)
        editLocationName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                final String locationName = s.toString().trim();
                // Cancel any pending check
                if (pendingCheck != null) {
                    debounceHandler.removeCallbacks(pendingCheck);
                    pendingCheck = null;
                }

                if (locationName.isEmpty()) {
                    locationStatus.setVisibility(View.GONE);
                    btnAdd.setEnabled(false);
                    return;
                }

                if (locationName.length() > 2 && !isChecking) {
                    locationStatus.setVisibility(View.VISIBLE);
                    locationStatus.setText(getContext().getString(R.string.checking_location));
                    locationStatus.setTextColor(getContext().getResources().getColor(android.R.color.darker_gray));
                    btnAdd.setEnabled(false);

                    // Schedule a debounced availability check
                    pendingCheck = () -> {
                        String toCheck = editLocationName.getText().toString().trim();
                        if (toCheck.length() > 2) {
                            checkLocationAvailability(toCheck);
                        }
                    };
                    debounceHandler.postDelayed(pendingCheck, CHECK_DELAY_MS);
                }
            }
        });

        btnAdd.setOnClickListener(v -> {
            String locationName = editLocationName.getText().toString().trim();
            if (locationName.isEmpty()) return;

            // If we already validated this exact name and it's valid, add immediately
            if (lastValid && locationName.equals(lastCheckedName)) {
                addLocation(locationName);
                return;
            }

            // If a check is currently running, mark that we want to add when it finishes
            if (isChecking) {
                pendingAdd = true;
                Toast.makeText(getContext(), getContext().getString(R.string.checking_location), Toast.LENGTH_SHORT).show();
                return;
            }

            // Otherwise, trigger an immediate verification and add after success
            if (pendingCheck != null) {
                debounceHandler.removeCallbacks(pendingCheck);
                pendingCheck = null;
            }
            pendingAdd = true;
            checkLocationAvailability(locationName);
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
            // If user tried to Add while this check was triggered, cancel pendingAdd
            if (pendingAdd) {
                pendingAdd = false;
            }
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
                    locationStatus.setText(getContext().getString(R.string.location_valid));
                    locationStatus.setTextColor(getContext().getResources().getColor(android.R.color.holo_green_dark));
                    btnAdd.setEnabled(true);
                    isChecking = false;
                    lastValid = true;
                    lastCheckedName = locationName;
                    if (pendingAdd) {
                        pendingAdd = false;
                        addLocation(locationName);
                    }
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
                    // If user had pressed Add and we attempted to pending-add, inform and keep dialog open
                    if (pendingAdd) {
                        pendingAdd = false;
                        Toast.makeText(getContext(), getContext().getString(R.string.location_not_found), Toast.LENGTH_SHORT).show();
                    }
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

