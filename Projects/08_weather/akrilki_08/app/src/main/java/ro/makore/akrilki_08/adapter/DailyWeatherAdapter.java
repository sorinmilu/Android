package ro.makore.akrilki_08.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import ro.makore.akrilki_08.R;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;
import ro.makore.akrilki_08.model.DailyWeatherItem;
import ro.makore.akrilki_08.model.WeatherItem;
import ro.makore.akrilki_08.WeatherDetailActivity;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherAdapter.DailyWeatherViewHolder> {

    private final Context context;
    private final List<DailyWeatherItem> dailyWeatherItemList;

    public DailyWeatherAdapter(Context context, List<DailyWeatherItem> dailyWeatherItemList) {
        this.context = context;
        this.dailyWeatherItemList = dailyWeatherItemList;
    }

    public void updateData(List<DailyWeatherItem> dailyWeatherItemList) {
        Log.v("WEATHER08", "Updating daily weather data");
        this.dailyWeatherItemList.clear();
        this.dailyWeatherItemList.addAll(dailyWeatherItemList);
        notifyDataSetChanged(); // Refresh the RecyclerView
    }

    @NonNull
    @Override
    public DailyWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_daily_weather, parent, false);
        return new DailyWeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyWeatherViewHolder holder, int position) {
        DailyWeatherItem dailyItem = dailyWeatherItemList.get(position);
        List<WeatherItem> hourlyData = dailyItem.getHourlyData();

        if (hourlyData == null || hourlyData.isEmpty()) {
            return;
        }

        // Set city name and country (only on first item)
        if (position == 0) {
            String cityCountry = dailyItem.getCityName();
            if (dailyItem.getCountry() != null && !dailyItem.getCountry().isEmpty()) {
                cityCountry += ", " + dailyItem.getCountry();
            }
            holder.cityTextView.setText(cityCountry);
            holder.cityTextView.setVisibility(View.VISIBLE);
        } else {
            holder.cityTextView.setVisibility(View.GONE);
        }

        // Format and set date
        String formattedDate = formatDate(dailyItem.getDate());
        holder.dateTextView.setText(formattedDate);

        // Setup line chart
        setupLineChart(holder.lineChart, hourlyData);

        // Handle click event to pass the first WeatherItem to WeatherDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WeatherDetailActivity.class);
            if (!hourlyData.isEmpty()) {
                intent.putExtra("weather_item", hourlyData.get(0)); // Pass the first item
            }
            context.startActivity(intent);
        });
    }

    private void setupLineChart(LineChart lineChart, List<WeatherItem> hourlyData) {
        // Prepare data for chart
        List<Entry> entries = new ArrayList<>();
        List<String> iconUrls = new ArrayList<>();

        for (int i = 0; i < hourlyData.size(); i++) {
            WeatherItem item = hourlyData.get(i);
            entries.add(new Entry(i, (float) item.getTemperature()));
            
            // Store icon URL for each entry
            if (item.getIconUrl() != null && !item.getIconUrl().isEmpty()) {
                iconUrls.add(item.getIconUrl());
            } else {
                iconUrls.add("");
            }
        }

        // Create dataset with fill under the line
        LineDataSet dataSet = new LineDataSet(entries, "Temperature");
        dataSet.setColor(Color.parseColor("#2196F3"));
        dataSet.setLineWidth(3f);
        dataSet.setDrawCircles(false); // Don't draw circles
        dataSet.setDrawValues(true); // Show temperature values on nodes
        dataSet.setValueTextColor(Color.parseColor("#000000"));
        dataSet.setValueTextSize(12f);
        dataSet.setValueTypeface(null); // Use default typeface
        // Remove any text stroke/background
        dataSet.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f°", value);
            }
        });
        dataSet.setDrawFilled(true); // Fill area under the line
        dataSet.setFillColor(Color.parseColor("#2196F3"));
        dataSet.setFillAlpha(128); // Semi-transparent fill (0-255, 128 = 50% opacity)

        // Create line data
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);

        // Disable all axes
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(false); // Completely disable X axis

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setEnabled(false); // Completely disable left Y axis

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false); // Completely disable right Y axis

        // Configure chart
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setTouchEnabled(false); // Disable touch interactions
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChart.setExtraOffsets(20f, 20f, 20f, 60f); // Add bottom padding for icons below chart

        // Refresh chart first to get dimensions
        lineChart.invalidate();
        
        // After chart is drawn, overlay icons at data points
        // Use a small delay to ensure chart is fully rendered
        lineChart.postDelayed(() -> {
            overlayIconsOnChart(lineChart, entries, iconUrls);
        }, 100);
    }

    private void overlayIconsOnChart(LineChart lineChart, List<Entry> entries, List<String> iconUrls) {
        // Get the parent view (should be a FrameLayout)
        ViewGroup parent = (ViewGroup) lineChart.getParent();
        if (parent == null || !(parent instanceof FrameLayout)) {
            Log.e("WEATHER08", "Parent is not a FrameLayout: " + (parent != null ? parent.getClass().getName() : "null"));
            return;
        }

        FrameLayout frameLayout = (FrameLayout) parent;

        // Remove existing icon views
        for (int i = frameLayout.getChildCount() - 1; i >= 0; i--) {
            View child = frameLayout.getChildAt(i);
            if (child.getTag() != null && child.getTag().equals("weather_icon")) {
                frameLayout.removeView(child);
            }
        }

        // Get chart's data set
        com.github.mikephil.charting.interfaces.datasets.ILineDataSet dataSet = lineChart.getData().getDataSetByIndex(0);
        if (dataSet == null) {
            Log.e("WEATHER08", "DataSet is null");
            return;
        }

        // Get transformer to convert values to pixel positions
        com.github.mikephil.charting.utils.Transformer transformer = lineChart.getTransformer(dataSet.getAxisDependency());

        // Get chart's content rect (the actual drawing area)
        android.graphics.RectF contentRect = lineChart.getViewPortHandler().getContentRect();
        float contentBottom = contentRect.bottom;
        float contentHeight = lineChart.getHeight();
        float contentTop = contentRect.top;
        float contentLeft = contentRect.left;
        float contentWidth = contentRect.width();

        int iconSize = (int) (context.getResources().getDisplayMetrics().density * 40); // 40dp
        int iconSpacing = (int) (context.getResources().getDisplayMetrics().density * 8); // 8dp spacing

        Log.d("WEATHER08", "Overlaying " + entries.size() + " icons below chart");
        Log.d("WEATHER08", "Content rect: " + contentRect.toString());
        Log.d("WEATHER08", "Chart height: " + contentHeight);
        Log.d("WEATHER08", "FrameLayout height: " + frameLayout.getHeight());
        Log.d("WEATHER08", "FrameLayout width: " + frameLayout.getWidth());

        // Calculate positions for each icon - align horizontally below chart
        for (int i = 0; i < entries.size() && i < iconUrls.size(); i++) {
            Entry entry = entries.get(i);
            String iconUrl = iconUrls.get(i);
            if (iconUrl == null || iconUrl.isEmpty()) {
                Log.w("WEATHER08", "Icon URL is empty for entry " + i);
                continue;
            }

            // Transform entry X coordinate to pixel position
            // We only need X position to align with the node
            float[] point = new float[] { entry.getX(), entry.getY() };
            transformer.pointValuesToPixel(point);
            
            // X position aligns with the data node
            float xPos = point[0];
            
            // Y position is fixed - below the chart on a horizontal line
            // Position icons at a fixed Y position below the chart content area
            // Use chart height minus some padding for icons
            float yPos = lineChart.getHeight() - iconSize - iconSpacing;
            
            // Ensure icons are within FrameLayout bounds
            if (yPos + iconSize > frameLayout.getHeight()) {
                yPos = frameLayout.getHeight() - iconSize - iconSpacing;
            }
            if (yPos < 0) {
                yPos = lineChart.getHeight() + iconSpacing;
            }
            
            // Ensure X position is within bounds
            if (xPos < iconSize / 2) {
                xPos = iconSize / 2;
            } else if (xPos > frameLayout.getWidth() - iconSize / 2) {
                xPos = frameLayout.getWidth() - iconSize / 2;
            }
            
            Log.d("WEATHER08", "Icon position: xPos=" + xPos + ", yPos=" + yPos + ", frameLayout size=" + frameLayout.getWidth() + "x" + frameLayout.getHeight());

            Log.d("WEATHER08", "Entry " + i + ": temp=" + entry.getY() + ", xPos=" + xPos + ", yPos=" + yPos + ", iconUrl=" + iconUrl);

            // Create and position icon
            ImageView iconView = new ImageView(context);
            iconView.setTag("weather_icon");
            
            // Position icon centered horizontally on the data node, below the chart
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(iconSize, iconSize);
            params.leftMargin = (int) (xPos - iconSize / 2);
            params.topMargin = (int) yPos;
            iconView.setLayoutParams(params);
            iconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iconView.setBackgroundColor(Color.TRANSPARENT); // Remove debug background
            iconView.setElevation(10f); // Make sure icons are on top
            iconView.setVisibility(View.VISIBLE); // Explicitly set visibility
            iconView.setAdjustViewBounds(true); // Allow image to adjust bounds
            
            // Add icon view to FrameLayout FIRST, then load image
            // This ensures the ImageView is properly attached before Glide tries to load
            frameLayout.addView(iconView);
            
            // Load icon using OkHttp (which we know works) then load into ImageView
            Log.d("WEATHER08", "Loading icon from URL: " + iconUrl);
            Log.d("WEATHER08", "Icon position: leftMargin=" + params.leftMargin + ", topMargin=" + params.topMargin);
            
            // Download image using OkHttp in background thread - use same client config as WeatherAPI
            final String finalIconUrl = iconUrl; // Make final for lambda
            new Thread(() -> {
                try {
                    // Use the same OkHttpClient configuration that works for API calls
                    okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
                    
                    okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(finalIconUrl)
                        .header("User-Agent", "Mozilla/5.0")
                        .build();
                    
                    try (okhttp3.Response response = client.newCall(request).execute()) {
                        if (response.isSuccessful() && response.body() != null) {
                            byte[] imageBytes = response.body().bytes();
                            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            
                            // Load bitmap into ImageView on UI thread
                            android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                            mainHandler.post(() -> {
                                if (bitmap != null && iconView.getParent() != null) {
                                    android.graphics.Bitmap scaledBitmap = android.graphics.Bitmap.createScaledBitmap(bitmap, iconSize, iconSize, true);
                                    iconView.setImageBitmap(scaledBitmap);
                                    Log.d("WEATHER08", "SUCCESS loaded icon from: " + finalIconUrl);
                                } else {
                                    Log.e("WEATHER08", "Failed to set bitmap - bitmap null or view detached");
                                }
                            });
                        } else {
                            Log.e("WEATHER08", "HTTP error loading icon: " + response.code() + " from: " + finalIconUrl);
                        }
                    }
                } catch (java.net.UnknownHostException e) {
                    Log.e("WEATHER08", "Network error - cannot resolve host: " + finalIconUrl, e);
                } catch (javax.net.ssl.SSLException e) {
                    Log.e("WEATHER08", "SSL error loading icon: " + finalIconUrl, e);
                    // Try HTTP instead of HTTPS as fallback
                    String httpUrl = finalIconUrl.replace("https://", "http://");
                    try {
                        Log.d("WEATHER08", "Trying HTTP fallback: " + httpUrl);
                        okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient();
                        okhttp3.Request httpRequest = new okhttp3.Request.Builder()
                            .url(httpUrl)
                            .build();
                        try (okhttp3.Response httpResponse = httpClient.newCall(httpRequest).execute()) {
                            if (httpResponse.isSuccessful() && httpResponse.body() != null) {
                                byte[] imageBytes = httpResponse.body().bytes();
                                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                if (bitmap != null) {
                                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                                    mainHandler.post(() -> {
                                        if (iconView.getParent() != null) {
                                            iconView.setImageBitmap(android.graphics.Bitmap.createScaledBitmap(bitmap, iconSize, iconSize, true));
                                            Log.d("WEATHER08", "SUCCESS loaded icon via HTTP fallback: " + httpUrl);
                                        }
                                    });
                                }
                            }
                        }
                    } catch (Exception fallbackException) {
                        Log.e("WEATHER08", "HTTP fallback also failed: " + httpUrl, fallbackException);
                    }
                } catch (Exception e) {
                    Log.e("WEATHER08", "Exception loading icon from: " + finalIconUrl, e);
                }
            }).start();
            Log.d("WEATHER08", "Added icon view at position " + params.leftMargin + ", " + params.topMargin + " for URL: " + iconUrl);
            Log.d("WEATHER08", "Icon view bounds: width=" + iconView.getWidth() + ", height=" + iconView.getHeight());
            Log.d("WEATHER08", "FrameLayout child count: " + frameLayout.getChildCount());
            
            // Force layout to ensure icon is positioned
            iconView.post(() -> {
                Log.d("WEATHER08", "Icon view actual position: left=" + iconView.getLeft() + ", top=" + iconView.getTop() + ", right=" + iconView.getRight() + ", bottom=" + iconView.getBottom());
                Log.d("WEATHER08", "Icon view visibility: " + iconView.getVisibility() + ", alpha: " + iconView.getAlpha());
            });
        }
    }

    private String formatDate(String dateStr) {
        try {
            // Parse date string (format: "yyyy-MM-dd")
            org.threeten.bp.LocalDate date = org.threeten.bp.LocalDate.parse(dateStr);
            org.threeten.bp.format.DateTimeFormatter formatter = org.threeten.bp.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
            return date.format(formatter);
        } catch (Exception e) {
            return dateStr; // Return original if parsing fails
        }
    }

    @Override
    public int getItemCount() {
        return dailyWeatherItemList.size();
    }

    // ViewHolder for each daily weather item
    public static class DailyWeatherViewHolder extends RecyclerView.ViewHolder {

        private final TextView cityTextView;
        private final TextView dateTextView;
        private final LineChart lineChart;

        public DailyWeatherViewHolder(View itemView) {
            super(itemView);

            // Initialize views
            cityTextView = itemView.findViewById(R.id.city_name);
            dateTextView = itemView.findViewById(R.id.date_text);
            lineChart = itemView.findViewById(R.id.temperature_chart);
        }
    }

}

