package ro.makore.akrilki_08.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ro.makore.akrilki_08.R;
import java.util.List;

public class LocationSpinnerAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> locations;
    private final String currentLocation;

    public LocationSpinnerAdapter(@NonNull Context context, @NonNull List<String> locations, String currentLocation) {
        super(context, R.layout.spinner_item_location, locations);
        this.context = context;
        this.locations = locations;
        this.currentLocation = currentLocation;
        setDropDownViewResource(R.layout.spinner_dropdown_item_location);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item_location, parent, false);
        }
        TextView textView = (TextView) convertView;
        String location = locations.get(position);
        if (location != null && location.equals(currentLocation)) {
            textView.setText(location + " (current)");
        } else {
            textView.setText(location);
        }
        textView.setTextColor(context.getResources().getColor(android.R.color.black));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_dropdown_item_location, parent, false);
        }
        TextView textView = (TextView) convertView;
        String location = locations.get(position);
        if (location != null && location.equals(currentLocation)) {
            textView.setText(location + " (current)");
        } else {
            textView.setText(location);
        }
        textView.setTextColor(context.getResources().getColor(android.R.color.black));
        textView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        return convertView;
    }
}

