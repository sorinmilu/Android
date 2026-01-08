package ro.makore.simple_weather;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * WeatherDetailActivity - Shows details for one weather entry
 * 
 * Android FORCES us to have a separate Activity class.
 * This is the MINIMUM we can do.
 */
public class WeatherDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);
        
        // Get weather item from intent
        MainActivity.WeatherItem item = getIntent().getParcelableExtra("weather_item");
        
        if (item != null) {
            // Set all the text views
            TextView cityText = findViewById(R.id.cityText);
            TextView tempText = findViewById(R.id.tempText);
            TextView feelsLikeText = findViewById(R.id.feelsLikeText);
            TextView descText = findViewById(R.id.descText);
            TextView humidityText = findViewById(R.id.humidityText);
            TextView pressureText = findViewById(R.id.pressureText);
            TextView windText = findViewById(R.id.windText);
            TextView visibilityText = findViewById(R.id.visibilityText);
            TextView dateTimeText = findViewById(R.id.dateTimeText);
            
            cityText.setText(item.cityName + ", " + item.country);
            tempText.setText(String.format("%.1f°C", item.temperature));
            feelsLikeText.setText(String.format("Feels like: %.1f°C", item.feelsLike));
            descText.setText(item.description);
            humidityText.setText(String.format("Humidity: %.0f%%", item.humidity));
            pressureText.setText(String.format("Pressure: %.0f hPa", item.pressure));
            windText.setText(String.format("Wind: %.1f m/s", item.windSpeed));
            visibilityText.setText(String.format("Visibility: %.1f km", item.visibility));
            dateTimeText.setText(item.dateTime);
        }
        
        // Back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }
}
