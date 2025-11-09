package ro.makore.akrilki_08.model;

import android.os.Parcel;
import android.os.Parcelable;

public class WeatherItem implements Parcelable {
    private String cityName;
    private String country;
    private String description;
    private String iconUrl;
    private double temperature;
    private double feelsLike;
    private double humidity;
    private double pressure;
    private double windSpeed;
    private double visibility;
    private String dateTime;

    // Unparcelable Constructor
    public WeatherItem() {
    }

    // Parcelable constructor - same order as in writeToParcel method
    protected WeatherItem(Parcel in) {
        cityName = in.readString();
        country = in.readString();
        description = in.readString();
        iconUrl = in.readString();
        temperature = in.readDouble();
        feelsLike = in.readDouble();
        humidity = in.readDouble();
        pressure = in.readDouble();
        windSpeed = in.readDouble();
        visibility = in.readDouble();
        dateTime = in.readString();
    }

    // writeToParcel must be in the same order as constructor
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cityName);
        dest.writeString(country);
        dest.writeString(description);
        dest.writeString(iconUrl);
        dest.writeDouble(temperature);
        dest.writeDouble(feelsLike);
        dest.writeDouble(humidity);
        dest.writeDouble(pressure);
        dest.writeDouble(windSpeed);
        dest.writeDouble(visibility);
        dest.writeString(dateTime);
    }

    @Override
    public int describeContents() {
        return 0;  // No special objects inside
    }

    // Parcelable CREATOR to help with deserialization
    public static final Creator<WeatherItem> CREATOR = new Creator<WeatherItem>() {
        @Override
        public WeatherItem createFromParcel(Parcel in) {
            return new WeatherItem(in);
        }

        @Override
        public WeatherItem[] newArray(int size) {
            return new WeatherItem[size];
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getVisibility() {
        return visibility;
    }

    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}

