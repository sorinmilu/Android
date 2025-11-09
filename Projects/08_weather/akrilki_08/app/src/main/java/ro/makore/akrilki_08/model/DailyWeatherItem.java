package ro.makore.akrilki_08.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;
import java.util.ArrayList;

public class DailyWeatherItem implements Parcelable {
    private String cityName;
    private String country;
    private String date; // Date in format "yyyy-MM-dd"
    private List<WeatherItem> hourlyData; // List of 3-hourly data points for this day

    // Unparcelable Constructor
    public DailyWeatherItem() {
        hourlyData = new ArrayList<>();
    }

    // Parcelable constructor - same order as in writeToParcel method
    protected DailyWeatherItem(Parcel in) {
        cityName = in.readString();
        country = in.readString();
        date = in.readString();
        hourlyData = in.createTypedArrayList(WeatherItem.CREATOR);
    }

    // writeToParcel must be in the same order as constructor
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cityName);
        dest.writeString(country);
        dest.writeString(date);
        dest.writeTypedList(hourlyData);
    }

    @Override
    public int describeContents() {
        return 0;  // No special objects inside
    }

    // Parcelable CREATOR to help with deserialization
    public static final Creator<DailyWeatherItem> CREATOR = new Creator<DailyWeatherItem>() {
        @Override
        public DailyWeatherItem createFromParcel(Parcel in) {
            return new DailyWeatherItem(in);
        }

        @Override
        public DailyWeatherItem[] newArray(int size) {
            return new DailyWeatherItem[size];
        }
    };

    // Getters and Setters
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<WeatherItem> getHourlyData() {
        return hourlyData;
    }

    public void setHourlyData(List<WeatherItem> hourlyData) {
        this.hourlyData = hourlyData;
    }

    public void addHourlyData(WeatherItem item) {
        if (hourlyData == null) {
            hourlyData = new ArrayList<>();
        }
        hourlyData.add(item);
    }
}

