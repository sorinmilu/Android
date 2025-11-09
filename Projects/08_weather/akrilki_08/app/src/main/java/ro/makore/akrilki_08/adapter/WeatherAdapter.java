package ro.makore.akrilki_08.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import ro.makore.akrilki_08.R;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;
import ro.makore.akrilki_08.model.WeatherItem;
import ro.makore.akrilki_08.WeatherDetailActivity;

import com.bumptech.glide.Glide;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private final Context context;
    private final List<WeatherItem> weatherItemList;

    public WeatherAdapter(Context context, List<WeatherItem> weatherItemList) {
        this.context = context;
        this.weatherItemList = weatherItemList;
    }

    public void updateData(List<WeatherItem> weatherItemList) {
        Log.v("WEATHER08", "Updating data");
        this.weatherItemList.clear();
        this.weatherItemList.addAll(weatherItemList);
        notifyDataSetChanged(); // Refresh the RecyclerView
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_weather, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        WeatherItem weatherItem = weatherItemList.get(position);

        // Load weather icon using Glide
        if (weatherItem.getIconUrl() != null && !weatherItem.getIconUrl().isEmpty()) {
            Glide.with(context)
                .load(weatherItem.getIconUrl())
                .into(holder.iconImageView);
        }

        // Set city name and country
        String cityCountry = weatherItem.getCityName();
        if (weatherItem.getCountry() != null && !weatherItem.getCountry().isEmpty()) {
            cityCountry += ", " + weatherItem.getCountry();
        }
        holder.cityTextView.setText(cityCountry);

        // Set temperature
        holder.temperatureTextView.setText(String.format("%.1f°C", weatherItem.getTemperature()));

        // Set description
        if (weatherItem.getDescription() != null && !weatherItem.getDescription().isEmpty()) {
            String description = weatherItem.getDescription();
            // Capitalize first letter
            description = description.substring(0, 1).toUpperCase() + description.substring(1);
            holder.descriptionTextView.setText(description);
        } else {
            holder.descriptionTextView.setText("No description");
        }

        // Set date/time
        holder.dateTimeTextView.setText(weatherItem.getDateTime());

        // Handle click event to pass the WeatherItem to WeatherDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WeatherDetailActivity.class);
            if (weatherItem != null) {
                intent.putExtra("weather_item", weatherItem); // Pass the WeatherItem to the next activity
            }
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return weatherItemList.size();
    }

    // ViewHolder for each weather item
    public static class WeatherViewHolder extends RecyclerView.ViewHolder {

        private final ImageView iconImageView;
        private final TextView cityTextView;
        private final TextView temperatureTextView;
        private final TextView descriptionTextView;
        private final TextView dateTimeTextView;

        public WeatherViewHolder(View itemView) {
            super(itemView);

            // Initialize views
            iconImageView = itemView.findViewById(R.id.weather_icon);
            cityTextView = itemView.findViewById(R.id.city_name);
            temperatureTextView = itemView.findViewById(R.id.temperature);
            descriptionTextView = itemView.findViewById(R.id.description);
            dateTimeTextView = itemView.findViewById(R.id.datetime);
        }
    }
}

