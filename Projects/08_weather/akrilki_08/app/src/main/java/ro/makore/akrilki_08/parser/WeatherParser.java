package ro.makore.akrilki_08.parser;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ro.makore.akrilki_08.model.WeatherItem;
import ro.makore.akrilki_08.model.DailyWeatherItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.ZoneId;

public class WeatherParser {

    // Parse weather and group by day
    public static List<DailyWeatherItem> parseWeatherByDay(String jsonResponse) {
        List<DailyWeatherItem> dailyWeatherItems = new ArrayList<>();
        Gson gson = new Gson();

        try {
            JsonObject rootObject = gson.fromJson(jsonResponse, JsonObject.class);
            
            // Check if response has error
            if (rootObject.has("cod")) {
                String cod = rootObject.get("cod").getAsString();
                if (!cod.equals("200")) {
                    String message = rootObject.has("message") ? rootObject.get("message").getAsString() : "Unknown error";
                    throw new Exception("API Error: " + message + " (Code: " + cod + ")");
                }
            }
            
            // Get city information (forecast API structure)
            if (!rootObject.has("city")) {
                throw new Exception("Invalid API response: missing 'city' field");
            }
            
            JsonObject cityObject = rootObject.getAsJsonObject("city");
            String cityName = cityObject.has("name") ? cityObject.get("name").getAsString() : "Unknown";
            String country = cityObject.has("country") ? cityObject.get("country").getAsString() : "";

            // Get forecast list
            if (!rootObject.has("list")) {
                throw new Exception("Invalid API response: missing 'list' field");
            }
            
            JsonArray forecastList = rootObject.getAsJsonArray("list");
            
            if (forecastList == null || forecastList.size() == 0) {
                throw new Exception("No forecast data available");
            }

            // Map to group data by date (yyyy-MM-dd)
            Map<String, DailyWeatherItem> dailyMap = new LinkedHashMap<>();

            for (int i = 0; i < forecastList.size(); i++) {
                JsonObject forecastItem = forecastList.get(i).getAsJsonObject();
                WeatherItem item = new WeatherItem();

                // Set city and country
                item.setCityName(cityName);
                item.setCountry(country);

                // Parse weather description and icon
                if (forecastItem.has("weather") && forecastItem.get("weather").isJsonArray()) {
                    JsonArray weatherArray = forecastItem.getAsJsonArray("weather");
                    if (weatherArray.size() > 0) {
                        JsonObject weather = weatherArray.get(0).getAsJsonObject();
                        if (weather.has("description") && !weather.get("description").isJsonNull()) {
                            item.setDescription(weather.get("description").getAsString());
                        } else {
                            item.setDescription("No description");
                        }
                        if (weather.has("icon") && !weather.get("icon").isJsonNull()) {
                            String iconCode = weather.get("icon").getAsString();
                            item.setIconUrl("https://openweathermap.org/img/wn/" + iconCode + "@2x.png");
                        } else {
                            item.setIconUrl("");
                        }
                    }
                }

                // Parse main weather data
                if (forecastItem.has("main") && !forecastItem.get("main").isJsonNull()) {
                    JsonObject main = forecastItem.getAsJsonObject("main");
                    if (main.has("temp") && !main.get("temp").isJsonNull()) {
                        item.setTemperature(main.get("temp").getAsDouble());
                    }
                    if (main.has("feels_like") && !main.get("feels_like").isJsonNull()) {
                        item.setFeelsLike(main.get("feels_like").getAsDouble());
                    }
                    if (main.has("humidity") && !main.get("humidity").isJsonNull()) {
                        item.setHumidity(main.get("humidity").getAsDouble());
                    }
                    if (main.has("pressure") && !main.get("pressure").isJsonNull()) {
                        item.setPressure(main.get("pressure").getAsDouble());
                    }
                }

                // Parse wind data
                if (forecastItem.has("wind") && !forecastItem.get("wind").isJsonNull()) {
                    JsonObject wind = forecastItem.getAsJsonObject("wind");
                    if (wind.has("speed") && !wind.get("speed").isJsonNull()) {
                        item.setWindSpeed(wind.get("speed").getAsDouble());
                    }
                }

                // Parse visibility (in meters, convert to km)
                if (forecastItem.has("visibility") && !forecastItem.get("visibility").isJsonNull()) {
                    item.setVisibility(forecastItem.get("visibility").getAsDouble() / 1000.0); // Convert to km
                }

                // Parse date/time from dt_txt field (forecast API)
                String dateKey = "";
                if (forecastItem.has("dt_txt") && !forecastItem.get("dt_txt").isJsonNull()) {
                    try {
                        String dateTimeStr = forecastItem.get("dt_txt").getAsString();
                        // Parse the date string (format: "2024-01-15 12:00:00")
                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, 
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm yyyy-MM-dd");
                        item.setDateTime(dateTime.format(formatter));
                        
                        // Extract date part (yyyy-MM-dd) for grouping
                        dateKey = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    } catch (Exception e) {
                        item.setDateTime("Invalid date");
                    }
                } else {
                    item.setDateTime("No date");
                }

                // Group by date
                if (!dateKey.isEmpty()) {
                    DailyWeatherItem dailyItem = dailyMap.get(dateKey);
                    if (dailyItem == null) {
                        dailyItem = new DailyWeatherItem();
                        dailyItem.setCityName(cityName);
                        dailyItem.setCountry(country);
                        dailyItem.setDate(dateKey);
                        dailyMap.put(dateKey, dailyItem);
                    }
                    dailyItem.addHourlyData(item);
                }
            }

            // Convert map to list
            dailyWeatherItems.addAll(dailyMap.values());
            
        } catch (Exception e) {
            android.util.Log.e("WeatherParser", "Error parsing weather data: " + e.getMessage(), e);
            throw new RuntimeException("Failed to parse weather data: " + e.getMessage(), e);
        }

        return dailyWeatherItems;
    }

    // Keep the old method for backward compatibility if needed
    public static List<WeatherItem> parseWeather(String jsonResponse) {
        List<WeatherItem> weatherItems = new ArrayList<>();
        Gson gson = new Gson();

        try {
            JsonObject rootObject = gson.fromJson(jsonResponse, JsonObject.class);
            
            // Check if response has error
            if (rootObject.has("cod")) {
                String cod = rootObject.get("cod").getAsString();
                if (!cod.equals("200")) {
                    String message = rootObject.has("message") ? rootObject.get("message").getAsString() : "Unknown error";
                    throw new Exception("API Error: " + message + " (Code: " + cod + ")");
                }
            }
            
            // Get city information (forecast API structure)
            if (!rootObject.has("city")) {
                throw new Exception("Invalid API response: missing 'city' field");
            }
            
            JsonObject cityObject = rootObject.getAsJsonObject("city");
            String cityName = cityObject.has("name") ? cityObject.get("name").getAsString() : "Unknown";
            String country = cityObject.has("country") ? cityObject.get("country").getAsString() : "";

            // Get forecast list
            if (!rootObject.has("list")) {
                throw new Exception("Invalid API response: missing 'list' field");
            }
            
            JsonArray forecastList = rootObject.getAsJsonArray("list");
            
            if (forecastList == null || forecastList.size() == 0) {
                throw new Exception("No forecast data available");
            }

            for (int i = 0; i < forecastList.size(); i++) {
                JsonObject forecastItem = forecastList.get(i).getAsJsonObject();
                WeatherItem item = new WeatherItem();

                // Set city and country
                item.setCityName(cityName);
                item.setCountry(country);

                // Parse weather description and icon
                if (forecastItem.has("weather") && forecastItem.get("weather").isJsonArray()) {
                    JsonArray weatherArray = forecastItem.getAsJsonArray("weather");
                    if (weatherArray.size() > 0) {
                        JsonObject weather = weatherArray.get(0).getAsJsonObject();
                        if (weather.has("description") && !weather.get("description").isJsonNull()) {
                            item.setDescription(weather.get("description").getAsString());
                        } else {
                            item.setDescription("No description");
                        }
                        if (weather.has("icon") && !weather.get("icon").isJsonNull()) {
                            String iconCode = weather.get("icon").getAsString();
                            item.setIconUrl("https://openweathermap.org/img/wn/" + iconCode + "@2x.png");
                        } else {
                            item.setIconUrl("");
                        }
                    }
                }

                // Parse main weather data
                if (forecastItem.has("main") && !forecastItem.get("main").isJsonNull()) {
                    JsonObject main = forecastItem.getAsJsonObject("main");
                    if (main.has("temp") && !main.get("temp").isJsonNull()) {
                        item.setTemperature(main.get("temp").getAsDouble());
                    }
                    if (main.has("feels_like") && !main.get("feels_like").isJsonNull()) {
                        item.setFeelsLike(main.get("feels_like").getAsDouble());
                    }
                    if (main.has("humidity") && !main.get("humidity").isJsonNull()) {
                        item.setHumidity(main.get("humidity").getAsDouble());
                    }
                    if (main.has("pressure") && !main.get("pressure").isJsonNull()) {
                        item.setPressure(main.get("pressure").getAsDouble());
                    }
                }

                // Parse wind data
                if (forecastItem.has("wind") && !forecastItem.get("wind").isJsonNull()) {
                    JsonObject wind = forecastItem.getAsJsonObject("wind");
                    if (wind.has("speed") && !wind.get("speed").isJsonNull()) {
                        item.setWindSpeed(wind.get("speed").getAsDouble());
                    }
                }

                // Parse visibility (in meters, convert to km)
                if (forecastItem.has("visibility") && !forecastItem.get("visibility").isJsonNull()) {
                    item.setVisibility(forecastItem.get("visibility").getAsDouble() / 1000.0); // Convert to km
                }

                // Parse date/time from dt_txt field (forecast API)
                if (forecastItem.has("dt_txt") && !forecastItem.get("dt_txt").isJsonNull()) {
                    try {
                        String dateTimeStr = forecastItem.get("dt_txt").getAsString();
                        // Parse the date string (format: "2024-01-15 12:00:00")
                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, 
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm yyyy-MM-dd");
                        item.setDateTime(dateTime.format(formatter));
                    } catch (Exception e) {
                        item.setDateTime("Invalid date");
                    }
                } else {
                    item.setDateTime("No date");
                }

                weatherItems.add(item);
            }
            
        } catch (Exception e) {
            android.util.Log.e("WeatherParser", "Error parsing weather data: " + e.getMessage(), e);
            throw new RuntimeException("Failed to parse weather data: " + e.getMessage(), e);
        }

        return weatherItems;
    }
}

