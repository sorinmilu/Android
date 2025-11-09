package ro.makore.akrilki_08;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.util.Log;

import ro.makore.akrilki_08.model.WeatherItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.load.engine.GlideException;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.load.DataSource;

import androidx.appcompat.app.AppCompatActivity;

public class WeatherDetailActivity extends AppCompatActivity {

    private TextView cityTextView;
    private TextView temperatureTextView;
    private TextView feelsLikeTextView;
    private TextView descriptionTextView;
    private TextView humidityTextView;
    private TextView pressureTextView;
    private TextView windSpeedTextView;
    private TextView visibilityTextView;
    private TextView dateTimeTextView;
    private ImageView weatherIconImageView;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);
        
        cityTextView = findViewById(R.id.cityTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        feelsLikeTextView = findViewById(R.id.feelsLikeTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        pressureTextView = findViewById(R.id.pressureTextView);
        windSpeedTextView = findViewById(R.id.windSpeedTextView);
        visibilityTextView = findViewById(R.id.visibilityTextView);
        dateTimeTextView = findViewById(R.id.dateTimeTextView);
        weatherIconImageView = findViewById(R.id.weatherIconImageView);
        backButton = findViewById(R.id.backButton);

        WeatherItem weatherItem = getIntent().getParcelableExtra("weather_item");

        if (weatherItem != null) {
            // Set city and country
            String cityCountry = weatherItem.getCityName();
            if (weatherItem.getCountry() != null && !weatherItem.getCountry().isEmpty()) {
                cityCountry += ", " + weatherItem.getCountry();
            }
            cityTextView.setText(cityCountry);

            // Set temperature
            temperatureTextView.setText(String.format("%.1f°C", weatherItem.getTemperature()));

            // Set feels like
            feelsLikeTextView.setText(String.format("Feels like: %.1f°C", weatherItem.getFeelsLike()));

            // Set description
            if (weatherItem.getDescription() != null && !weatherItem.getDescription().isEmpty()) {
                String description = weatherItem.getDescription();
                description = description.substring(0, 1).toUpperCase() + description.substring(1);
                descriptionTextView.setText(description);
            } else {
                descriptionTextView.setText("No description");
            }

            // Set humidity
            humidityTextView.setText(String.format("Humidity: %.0f%%", weatherItem.getHumidity()));

            // Set pressure
            pressureTextView.setText(String.format("Pressure: %.0f hPa", weatherItem.getPressure()));

            // Set wind speed
            windSpeedTextView.setText(String.format("Wind Speed: %.1f m/s", weatherItem.getWindSpeed()));

            // Set visibility
            visibilityTextView.setText(String.format("Visibility: %.1f km", weatherItem.getVisibility()));

            // Set date/time
            dateTimeTextView.setText(weatherItem.getDateTime());

            // Load weather icon using Glide
            if (weatherItem.getIconUrl() != null && !weatherItem.getIconUrl().isEmpty()) {
                Glide.with(WeatherDetailActivity.this)
                    .load(weatherItem.getIconUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("Glide", "Image load failed", e);
                            return false; // Allow Glide to handle the error
                        }
                
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            weatherIconImageView.post(() -> {
                                // Get the intrinsic dimensions of the loaded image
                                int intrinsicWidth = resource.getIntrinsicWidth();
                                int intrinsicHeight = resource.getIntrinsicHeight();
                
                                // Get the width of the ImageView
                                int viewWidth = weatherIconImageView.getWidth();
                
                                // Calculate the proportional height based on the image aspect ratio
                                int viewHeight = (int) ((float) intrinsicHeight / intrinsicWidth * viewWidth);
                
                                // Update the ImageView layout parameters
                                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) weatherIconImageView.getLayoutParams();
                                params.height = viewHeight;
                                weatherIconImageView.setLayoutParams(params);
                            });
                            return false; // Allow Glide to set the resource on the ImageView
                        }
                    })
                    .into(weatherIconImageView);
            }

        } else {
            cityTextView.setText("No weather data available");
            descriptionTextView.setText("Please try again");
        }

        backButton.setOnClickListener(v -> finish());
    }
}

