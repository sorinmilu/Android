# MainActivity.java — Part 2: JSON Parsing, Models, Charts & Adapter

*Continuare de la [MainActivity.java.md](MainActivity.java.md)*

---

## JSON Parsing (Gson inline, NO WeatherParser class)

### parseWeatherJSON() - Monolithic parsing (120 linii)

```java
private List<DailyWeather> parseWeatherJSON(String json) {
    Gson gson = new Gson();
    JsonObject root = gson.fromJson(json, JsonObject.class);
    
    JsonObject city = root.getAsJsonObject("city");
    String cityName = city.get("name").getAsString();
    String country = city.has("country") ? city.get("country").getAsString() : "";
    
    JsonArray list = root.getAsJsonArray("list");
    
    Map<String, DailyWeather> dayMap = new HashMap<>();
    
    for (JsonElement element : list) {
        JsonObject item = element.getAsJsonObject();
        
        long timestamp = item.get("dt").getAsLong() * 1000;
        Date date = new Date(timestamp);
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
        SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);
        
        String dayKey = dayFormat.format(date);
        String timeStr = timeFormat.format(date);
        String displayDate = displayFormat.format(date);
        
        JsonObject main = item.getAsJsonObject("main");
        double temp = main.get("temp").getAsDouble();
        double feelsLike = main.get("feels_like").getAsDouble();
        double humidity = main.get("humidity").getAsDouble();
        double pressure = main.get("pressure").getAsDouble();
        
        JsonArray weatherArray = item.getAsJsonArray("weather");
        String description = "";
        String icon = "";
        if (weatherArray.size() > 0) {
            JsonObject weather = weatherArray.get(0).getAsJsonObject();
            description = weather.get("description").getAsString();
            icon = weather.get("icon").getAsString();
        }
        
        JsonObject wind = item.getAsJsonObject("wind");
        double windSpeed = wind.get("speed").getAsDouble();
        
        double visibility = item.has("visibility") ? item.get("visibility").getAsDouble() / 1000.0 : 0;
        
        WeatherItem weatherItem = new WeatherItem();
        weatherItem.cityName = cityName;
        weatherItem.country = country;
        weatherItem.description = description;
        weatherItem.iconUrl = "https://openweathermap.org/img/wn/" + icon + "@2x.png";
        weatherItem.temperature = temp;
        weatherItem.feelsLike = feelsLike;
        weatherItem.humidity = humidity;
        weatherItem.pressure = pressure;
        weatherItem.windSpeed = windSpeed;
        weatherItem.visibility = visibility;
        weatherItem.dateTime = displayDate + " " + timeStr;
        
        if (!dayMap.containsKey(dayKey)) {
            DailyWeather daily = new DailyWeather();
            daily.date = displayDate;
            daily.items = new ArrayList<>();
            dayMap.put(dayKey, daily);
        }
        dayMap.get(dayKey).items.add(weatherItem);
    }
    
    List<DailyWeather> result = new ArrayList<>(dayMap.values());
    for (DailyWeather daily : result) {
        daily.calculateAverages();
    }
    
    return result;
}
```

**Linie cu linie:**

### 1. Create Gson instance
```java
Gson gson = new Gson();
JsonObject root = gson.fromJson(json, JsonObject.class);
```
- **Gson** - Google JSON library
- `fromJson()` - String → JsonObject
- Alternative: org.json.JSONObject (ce am folosit pentru API key)

### 2. Extract city info
```java
JsonObject city = root.getAsJsonObject("city");
String cityName = city.get("name").getAsString();
String country = city.has("country") ? city.get("country").getAsString() : "";
```

**JSON Structure:**
```json
{
  "city": {
    "name": "Bucharest",
    "country": "RO"
  },
  "list": [ ... ]
}
```

### 3. Get forecast list
```java
JsonArray list = root.getAsJsonArray("list");
```
- Array cu 40 obiecte (8 pe zi × 5 zile)
- Fiecare obiect = weather la o oră anume

### 4. Group by day (HashMap)
```java
Map<String, DailyWeather> dayMap = new HashMap<>();
```
- Key: "2026-01-09" (day key)
- Value: DailyWeather object (conține toate weather items pentru ziua respectivă)

**De ce HashMap?**
- API returnează 40 items în ordine cronologică
- Vrem să grupăm pe zile: Jan 09, Jan 10, Jan 11, etc.
- HashMap permite lookup rapid: `O(1)`

### 5. Loop prin forecast items
```java
for (JsonElement element : list) {
    JsonObject item = element.getAsJsonObject();
```

### 6. Parse timestamp
```java
long timestamp = item.get("dt").getAsLong() * 1000;
Date date = new Date(timestamp);
```
- `dt` - Unix timestamp în **secunde**
- Multiply × 1000 pentru **millisecunde** (Java Date format)

### 7. Format date (3 formatters)
```java
SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);

String dayKey = dayFormat.format(date);         // "2026-01-09"
String timeStr = timeFormat.format(date);        // "15:00"
String displayDate = displayFormat.format(date); // "Wed, Jan 09"
```

| Formatter | Pattern | Output | Usage |
|-----------|---------|--------|-------|
| `dayFormat` | "yyyy-MM-dd" | "2026-01-09" | HashMap key (grouping) |
| `timeFormat` | "HH:mm" | "15:00" | Hour display |
| `displayFormat` | "EEE, MMM dd" | "Wed, Jan 09" | Day display |

**Date format patterns:**
- `yyyy` - year (4 digits)
- `MM` - month (2 digits)
- `dd` - day (2 digits)
- `EEE` - day of week (abbreviated: Mon, Tue, etc.)
- `MMM` - month (abbreviated: Jan, Feb, etc.)
- `HH` - hour 24-format
- `mm` - minute

### 8. Parse main weather data
```java
JsonObject main = item.getAsJsonObject("main");
double temp = main.get("temp").getAsDouble();
double feelsLike = main.get("feels_like").getAsDouble();
double humidity = main.get("humidity").getAsDouble();
double pressure = main.get("pressure").getAsDouble();
```

**API JSON structure:**
```json
{
  "dt": 1704729600,
  "main": {
    "temp": 23.5,
    "feels_like": 21.2,
    "humidity": 65,
    "pressure": 1013
  }
}
```

### 9. Parse weather description + icon
```java
JsonArray weatherArray = item.getAsJsonArray("weather");
String description = "";
String icon = "";
if (weatherArray.size() > 0) {
    JsonObject weather = weatherArray.get(0).getAsJsonObject();
    description = weather.get("description").getAsString();
    icon = weather.get("icon").getAsString();
}
```

**JSON:**
```json
{
  "weather": [
    {
      "description": "clear sky",
      "icon": "01d"
    }
  ]
}
```

**Icon codes:**
- "01d" - clear sky (day)
- "01n" - clear sky (night)
- "02d" - few clouds
- "09d" - shower rain
- "10d" - rain
- "13d" - snow

### 10. Parse wind
```java
JsonObject wind = item.getAsJsonObject("wind");
double windSpeed = wind.get("speed").getAsDouble();
```

### 11. Parse visibility (optional field)
```java
double visibility = item.has("visibility") ? item.get("visibility").getAsDouble() / 1000.0 : 0;
```
- API returnează vizibilitatea în **metri**
- Convertim în **kilometri** (÷ 1000)
- Field-ul poate lipsi (uneori API nu returnează)
- Default: 0 km

### 12. Create WeatherItem (direct field access)
```java
WeatherItem weatherItem = new WeatherItem();
weatherItem.cityName = cityName;
weatherItem.country = country;
weatherItem.description = description;
weatherItem.iconUrl = "https://openweathermap.org/img/wn/" + icon + "@2x.png";
weatherItem.temperature = temp;
weatherItem.feelsLike = feelsLike;
weatherItem.humidity = humidity;
weatherItem.pressure = pressure;
weatherItem.windSpeed = windSpeed;
weatherItem.visibility = visibility;
weatherItem.dateTime = displayDate + " " + timeStr;
```

**Icon URL:**
- Base: `https://openweathermap.org/img/wn/`
- Icon code: `01d`
- Size: `@2x` (2× resolution)
- Full: `https://openweathermap.org/img/wn/01d@2x.png`

**Public fields:**
- NO setters (direct access)
- Faster to write
- Less safe (no validation)

### 13. Group by day
```java
if (!dayMap.containsKey(dayKey)) {
    DailyWeather daily = new DailyWeather();
    daily.date = displayDate;
    daily.items = new ArrayList<>();
    dayMap.put(dayKey, daily);
}
dayMap.get(dayKey).items.add(weatherItem);
```

**Logic:**
1. Check dacă ziua există în HashMap
2. Dacă NU → creează DailyWeather nou cu lista goală
3. Add weatherItem la lista zilei

**Exemplu:**
```
Input: 40 weather items
- Jan 09 00:00
- Jan 09 03:00
- Jan 09 06:00
- ...
- Jan 09 21:00 (8 items pentru Jan 09)
- Jan 10 00:00
- ...

HashMap după loop:
{
  "2026-01-09": DailyWeather { date: "Wed, Jan 09", items: [8 WeatherItems] },
  "2026-01-10": DailyWeather { date: "Thu, Jan 10", items: [8 WeatherItems] },
  ...
}
```

### 14. Convert HashMap → List + calculate averages
```java
List<DailyWeather> result = new ArrayList<>(dayMap.values());
for (DailyWeather daily : result) {
    daily.calculateAverages();
}

return result;
```

**calculateAverages():**
- Loop prin toate weather items ale zilei
- Calculate: `avgTemp`, `minTemp`, `maxTemp`
- Folosit pentru display în RecyclerView

**Comparație cu 08_weather:**

| Aspect | 08_weather | 09_simple |
|--------|------------|-----------|
| **Locație** | WeatherParser.java (392 linii) | MainActivity.parseWeatherJSON() (120 linii) |
| **Metode** | 4 separate (parse, parseItem, groupByDay, format) | 1 monolithic |
| **Reusability** | High | None |
| **Encapsulation** | Setters | Direct field access |
| **Code duplication** | No | Formatters created în loop (INEFFICIENT!) |

**Bug în cod:**
```java
for (JsonElement element : list) {
    // ...
    SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
    SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);
    // ...
}
```

**Problema:**
- Creăm 3 formatters NOI la **fiecare iterație** (40×)
- Total: 120 SimpleDateFormat objects created!
- **INEFFICIENT** - ar trebui create ONCE înainte de loop

**Fix corect:**
```java
SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);

for (JsonElement element : list) {
    // Use formatters here
}
```

**De ce nu am fixat?**
- Demonstrează că "inline everything" poate duce la ineficiențe
- În 08_weather, acest bug ar fi fost evident (formatters ar fi field-uri)
- În 09_simple, e ascuns în cod monolitic

---

## Model Classes (Parcelable inline)

### WeatherItem (inner static class)

```java
public static class WeatherItem implements Parcelable {
    public String cityName;
    public String country;
    public String description;
    public String iconUrl;
    public double temperature;
    public double feelsLike;
    public double humidity;
    public double pressure;
    public double windSpeed;
    public double visibility;
    public String dateTime;
    
    public WeatherItem() {}
    
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
        return 0;
    }
    
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
}
```

**Linie cu linie:**

#### 1. Inner static class
```java
public static class WeatherItem implements Parcelable {
```
- **static** - nu necesită instanță de MainActivity
- **public** - accesibilă din WeatherDetailActivity
- **implements Parcelable** - poate fi transmisă prin Intent

#### 2. Public fields (NO encapsulation)
```java
public String cityName;
public String country;
// ... etc
```
- Direct access (no getters/setters)
- Faster to write
- **Anti-OOP** (breaks encapsulation)

#### 3. Default constructor
```java
public WeatherItem() {}
```
- Necesar pentru crearea obiectelor noi
- Empty body

#### 4. Parcel constructor
```java
protected WeatherItem(Parcel in) {
    cityName = in.readString();
    country = in.readString();
    // ... etc
}
```
- Reconstruct object din Parcel
- **CRITICAL:** Ordinea TREBUIE să fie identică cu `writeToParcel()`
- `protected` - accesat doar de CREATOR

#### 5. writeToParcel() - Serialization
```java
@Override
public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(cityName);
    dest.writeString(country);
    // ... etc
}
```
- Scrie toate field-urile în Parcel
- Ordinea: cityName, country, description, iconUrl, temperature, etc.
- **Strings** → `writeString()`
- **Doubles** → `writeDouble()`

#### 6. describeContents()
```java
@Override
public int describeContents() {
    return 0;
}
```
- Returnează bitmask cu info suplimentară
- `0` = no special objects (FileDescriptor, etc.)
- 99% din cazuri returnează 0

#### 7. CREATOR field
```java
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
```

**CREATOR:**
- **MUST** be named exactly `CREATOR`
- **MUST** be `public static final`
- **MUST** implement `Parcelable.Creator<T>`

**Methods:**
- `createFromParcel()` - creează obiect din Parcel
- `newArray()` - creează array de obiecte (rar folosit)

**Parcelable flow:**

**Write (MainActivity → Intent):**
```
WeatherItem item = new WeatherItem();
item.cityName = "Bucharest";
// ... set fields

Intent intent = new Intent(this, WeatherDetailActivity.class);
intent.putExtra("weather_item", item);
    ↓
Android calls: item.writeToParcel(parcel, 0)
    ↓
Parcel: ["Bucharest", "RO", "clear sky", "url", 23.5, ...]
    ↓
Intent carries Parcel
```

**Read (WeatherDetailActivity):**
```
Intent intent = getIntent();
WeatherItem item = intent.getParcelableExtra("weather_item");
    ↓
Android calls: WeatherItem.CREATOR.createFromParcel(parcel)
    ↓
new WeatherItem(parcel)
    ↓
WeatherItem object reconstructed!
```

---

### DailyWeather (inner static class)

```java
public static class DailyWeather {
    public String date;
    public List<WeatherItem> items;
    public double avgTemp;
    public double minTemp;
    public double maxTemp;
    
    public void calculateAverages() {
        if (items == null || items.isEmpty()) return;
        
        double sum = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        for (WeatherItem item : items) {
            sum += item.temperature;
            min = Math.min(min, item.temperature);
            max = Math.max(max, item.temperature);
        }
        
        avgTemp = sum / items.size();
        minTemp = min;
        maxTemp = max;
    }
}
```

**Linie cu linie:**

#### 1. Public fields
```java
public String date;              // "Wed, Jan 09"
public List<WeatherItem> items;  // 8 weather items pentru ziua respectivă
public double avgTemp;           // Average temperature
public double minTemp;           // Minimum temperature
public double maxTemp;           // Maximum temperature
```

#### 2. calculateAverages()
```java
public void calculateAverages() {
    if (items == null || items.isEmpty()) return;
```
- Safety check
- Early return dacă nu avem items

#### 3. Initialize min/max
```java
double sum = 0;
double min = Double.MAX_VALUE;
double max = Double.MIN_VALUE;
```
- `Double.MAX_VALUE` - cea mai mare valoare posibilă (1.7976931348623157E308)
- `Double.MIN_VALUE` - cea mai mică valoare POZITIVĂ (4.9E-324)
  - **ATENȚIE:** NU e cea mai mică valoare negativă!
  - Pentru negativă, folosim `Double.NEGATIVE_INFINITY` sau `-Double.MAX_VALUE`

**De ce MAX_VALUE pentru min?**
- Orice temperatură va fi < MAX_VALUE
- La prima comparație, min devine temperatura reală

#### 4. Loop + calculate
```java
for (WeatherItem item : items) {
    sum += item.temperature;
    min = Math.min(min, item.temperature);
    max = Math.max(max, item.temperature);
}
```
- `sum` - total pentru average
- `Math.min()` - găsește minimul
- `Math.max()` - găsește maximul

#### 5. Set results
```java
avgTemp = sum / items.size();
minTemp = min;
maxTemp = max;
```

**Exemplu:**
```
items: [23.5, 25.1, 22.3, 21.8, 20.5, 19.2, 18.7, 17.9]
sum: 168.0
items.size(): 8
avgTemp: 21.0
minTemp: 17.9
maxTemp: 25.1
```

**NO Parcelable:**
- DailyWeather NU implementează Parcelable
- Nu e transmis prin Intent
- Folosit doar în MainActivity (RecyclerView)

---

## Chart Setup (MPAndroidChart cu Glide icons)

### setupTemperatureChart()

```java
private void setupTemperatureChart(LineChart lineChart, FrameLayout chartFrame, List<WeatherItem> hourlyData) {
    Log.d(TAG, "setupTemperatureChart called");
    
    if (hourlyData == null || hourlyData.isEmpty()) {
        Log.w(TAG, "hourlyData is null or empty");
        return;
    }
    
    Log.d(TAG, "Setting up chart with " + hourlyData.size() + " items");
    
    // Prepare data for chart
    List<Entry> entries = new ArrayList<>();
    List<String> iconUrls = new ArrayList<>();
    
    for (int i = 0; i < hourlyData.size(); i++) {
        WeatherItem item = hourlyData.get(i);
        entries.add(new Entry(i, (float) item.temperature));
        iconUrls.add(item.iconUrl != null ? item.iconUrl : "");
    }
    
    Log.d(TAG, "Created " + entries.size() + " entries");
    
    // Create dataset with fill under the line
    LineDataSet dataSet = new LineDataSet(entries, "Temperature");
    dataSet.setColor(Color.parseColor("#2196F3"));
    dataSet.setLineWidth(3f);
    dataSet.setDrawCircles(false);
    dataSet.setDrawValues(true);
    dataSet.setValueTextSize(12f);
    dataSet.setValueTextColor(Color.BLACK);
    
    // Format temperature values to show 1 decimal
    dataSet.setValueFormatter(new ValueFormatter() {
        @Override
        public String getFormattedValue(float value) {
            return String.format(Locale.US, "%.0f°", value);
        }
    });
    
    // Fill under line
    dataSet.setDrawFilled(true);
    dataSet.setFillColor(Color.parseColor("#2196F3"));
    dataSet.setFillAlpha(128);
    
    // Create LineData and set to chart
    LineData lineData = new LineData(dataSet);
    lineChart.setData(lineData);
    
    Log.d(TAG, "LineData set to chart");
    
    // Disable axes
    XAxis xAxis = lineChart.getXAxis();
    xAxis.setEnabled(false);
    
    YAxis leftAxis = lineChart.getAxisLeft();
    leftAxis.setEnabled(false);
    
    YAxis rightAxis = lineChart.getAxisRight();
    rightAxis.setEnabled(false);
    
    // Configure chart
    lineChart.getDescription().setEnabled(false);
    lineChart.getLegend().setEnabled(false);
    lineChart.setTouchEnabled(false);
    lineChart.setDragEnabled(false);
    lineChart.setScaleEnabled(false);
    lineChart.setPinchZoom(false);
    lineChart.setDrawGridBackground(false);
    lineChart.setBackgroundColor(Color.TRANSPARENT);
    lineChart.setExtraOffsets(20f, 20f, 20f, 60f); // Bottom padding for icons
    
    Log.d(TAG, "Chart configured, calling invalidate");
    
    // Refresh chart and overlay icons
    lineChart.invalidate();
    lineChart.postDelayed(() -> {
        Log.d(TAG, "Calling overlayIconsOnChart");
        overlayIconsOnChart(lineChart, chartFrame, entries, iconUrls);
    }, 100);
}
```

**Linie cu linie:**

#### 1. Prepare data
```java
List<Entry> entries = new ArrayList<>();
List<String> iconUrls = new ArrayList<>();

for (int i = 0; i < hourlyData.size(); i++) {
    WeatherItem item = hourlyData.get(i);
    entries.add(new Entry(i, (float) item.temperature));
    iconUrls.add(item.iconUrl != null ? item.iconUrl : "");
}
```

**Entry:**
- MPAndroidChart data point
- `new Entry(x, y)`
  - `x` = index (0, 1, 2, ...)
  - `y` = temperature (23.5, 25.1, ...)

#### 2. Create LineDataSet
```java
LineDataSet dataSet = new LineDataSet(entries, "Temperature");
dataSet.setColor(Color.parseColor("#2196F3"));  // Material Blue
dataSet.setLineWidth(3f);
dataSet.setDrawCircles(false);
dataSet.setDrawValues(true);
dataSet.setValueTextSize(12f);
dataSet.setValueTextColor(Color.BLACK);
```

| Method | Value | Effect |
|--------|-------|--------|
| `setColor()` | #2196F3 (Material Blue) | Line color |
| `setLineWidth()` | 3f | Line thickness (3dp) |
| `setDrawCircles()` | false | No dots at data points |
| `setDrawValues()` | true | Show temperature values above line |
| `setValueTextSize()` | 12f | Text size for values |
| `setValueTextColor()` | BLACK | Text color |

#### 3. Format values
```java
dataSet.setValueFormatter(new ValueFormatter() {
    @Override
    public String getFormattedValue(float value) {
        return String.format(Locale.US, "%.0f°", value);
    }
});
```
- Default: "23.5"
- With formatter: "24°" (rounded, with degree symbol)

#### 4. Fill under line
```java
dataSet.setDrawFilled(true);
dataSet.setFillColor(Color.parseColor("#2196F3"));
dataSet.setFillAlpha(128);  // 50% transparent
```
- Creates gradient fill from line to bottom
- 128 = 50% alpha (0-255 range)

#### 5. Disable axes
```java
XAxis xAxis = lineChart.getXAxis();
xAxis.setEnabled(false);

YAxis leftAxis = lineChart.getAxisLeft();
leftAxis.setEnabled(false);

YAxis rightAxis = lineChart.getAxisRight();
rightAxis.setEnabled(false);
```
- Clean look (no grid, no labels)
- Only line + values visible

#### 6. Configure chart
```java
lineChart.getDescription().setEnabled(false);   // No "Description label" text
lineChart.getLegend().setEnabled(false);        // No legend ("Temperature")
lineChart.setTouchEnabled(false);               // No touch interaction
lineChart.setDragEnabled(false);                // No drag to pan
lineChart.setScaleEnabled(false);               // No pinch to zoom
lineChart.setPinchZoom(false);                  // No pinch zoom
lineChart.setDrawGridBackground(false);         // No grid background
lineChart.setBackgroundColor(Color.TRANSPARENT);// Transparent background
lineChart.setExtraOffsets(20f, 20f, 20f, 60f); // Padding: left, top, right, BOTTOM (60 for icons)
```

**Extra offsets:**
- Bottom: 60f - space pentru weather icons (60dp height)
- Top/Left/Right: 20f - breathing room

#### 7. Refresh + overlay icons (delayed)
```java
lineChart.invalidate();  // Force redraw
lineChart.postDelayed(() -> {
    overlayIconsOnChart(lineChart, chartFrame, entries, iconUrls);
}, 100);
```

**De ce delay 100ms?**
- Chart needs time to layout
- Transformer (coordinates conversion) e ready după layout
- Fără delay, icon positions ar fi greșite (0,0)

---

### overlayIconsOnChart()

```java
private void overlayIconsOnChart(LineChart lineChart, FrameLayout chartFrame, 
                                  List<Entry> entries, List<String> iconUrls) {
    // Clear previous icons
    for (int i = chartFrame.getChildCount() - 1; i > 0; i--) {
        View child = chartFrame.getChildAt(i);
        if (child instanceof ImageView) {
            chartFrame.removeView(child);
        }
    }
    
    if (entries.isEmpty() || iconUrls.isEmpty()) {
        return;
    }
    
    Transformer transformer = lineChart.getTransformer(YAxis.AxisDependency.LEFT);
    int iconSize = 60; // 60dp icon size
    int iconSpacing = 10;
    
    // Overlay icons at each data point
    for (int i = 0; i < entries.size() && i < iconUrls.size(); i++) {
        Entry entry = entries.get(i);
        String iconUrl = iconUrls.get(i);
        
        if (iconUrl == null || iconUrl.isEmpty()) {
            continue;
        }
        
        // Transform entry X coordinate to pixel position
        float[] point = new float[] { entry.getX(), entry.getY() };
        transformer.pointValuesToPixel(point);
        
        float xPos = point[0];
        float yPos = lineChart.getHeight() - iconSize - iconSpacing;
        
        // Ensure icons are within bounds
        if (yPos + iconSize > chartFrame.getHeight()) {
            yPos = chartFrame.getHeight() - iconSize - iconSpacing;
        }
        
        // Create ImageView for icon
        ImageView iconView = new ImageView(MainActivity.this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(iconSize, iconSize);
        params.leftMargin = (int) (xPos - iconSize / 2f);
        params.topMargin = (int) yPos;
        iconView.setLayoutParams(params);
        
        // Load icon with Glide
        Glide.with(MainActivity.this)
            .load(iconUrl)
            .into(iconView);
        
        chartFrame.addView(iconView);
    }
}
```

**Linie cu linie:**

#### 1. Clear previous icons
```java
for (int i = chartFrame.getChildCount() - 1; i > 0; i--) {
    View child = chartFrame.getChildAt(i);
    if (child instanceof ImageView) {
        chartFrame.removeView(child);
    }
}
```
- Loop backwards (când removăm views, indexurile se schimbă)
- `i > 0` - NU removăm child 0 (LineChart itself!)
- Only remove ImageViews (previous icons)

#### 2. Get Transformer
```java
Transformer transformer = lineChart.getTransformer(YAxis.AxisDependency.LEFT);
```

**Transformer:**
- Converts chart values (0-7) to pixel coordinates (50px, 150px, etc.)
- Necessary pentru positioning icons

#### 3. Transform coordinates
```java
float[] point = new float[] { entry.getX(), entry.getY() };
transformer.pointValuesToPixel(point);

float xPos = point[0];  // X position în pixels
float yPos = lineChart.getHeight() - iconSize - iconSpacing;  // Bottom of chart
```

**Y positioning:**
- Icons la BOTTOM of chart (sub linie)
- `lineChart.getHeight()` - total height
- Subtract `iconSize` (60) + `iconSpacing` (10)
- Result: icons 10px from bottom

#### 4. Create ImageView
```java
ImageView iconView = new ImageView(MainActivity.this);
FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(iconSize, iconSize);
params.leftMargin = (int) (xPos - iconSize / 2f);  // Center icon on X position
params.topMargin = (int) yPos;
iconView.setLayoutParams(params);
```

**Centering:**
- `xPos` - center of data point
- Subtract `iconSize / 2` - shift left by half icon width
- Result: icon centered on data point

#### 5. Load with Glide
```java
Glide.with(MainActivity.this)
    .load(iconUrl)
    .into(iconView);
```

**Glide:**
- Image loading library (by Bumptech)
- Async loading (no blocking)
- Caching (memory + disk)
- Auto resize/scale

#### 6. Add to frame
```java
chartFrame.addView(iconView);
```
- FrameLayout allows overlapping views
- LineChart = child 0
- Icons = children 1, 2, 3, ...

**Visual result:**
```
┌────────────────────────────────┐
│  24°   25°   23°   22°   21°   │
│   ●─────●─────●─────●─────●    │ ← Temperature line
│  /                         \   │
│ /                           \  │
│                                │
│ ☀️   ☀️   ⛅   🌧️   ⛈️          │ ← Weather icons (overlayed)
└────────────────────────────────┘
```

---

## RecyclerView Adapter (inline)

### WeatherAdapter (inner class)

```java
private class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    
    private List<DailyWeather> dailyList;
    
    public WeatherAdapter(List<DailyWeather> dailyList) {
        this.dailyList = dailyList;
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_weather, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DailyWeather daily = dailyList.get(position);
        
        holder.dateText.setText(daily.date);
        holder.minMaxText.setText(String.format(Locale.US, "%.1f / %.1f°C", 
            daily.minTemp, daily.maxTemp));
        holder.itemCountText.setText(daily.items.size() + " entries");
        
        Log.d(TAG, "onBindViewHolder position=" + position + ", items=" + daily.items.size());
        
        // Setup temperature chart with icons
        if (holder.temperatureChart != null && holder.chartFrame != null) {
            setupTemperatureChart(holder.temperatureChart, holder.chartFrame, daily.items);
        } else {
            Log.e(TAG, "Chart or frame is NULL!");
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (!daily.items.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, WeatherDetailActivity.class);
                intent.putExtra("weather_item", daily.items.get(0));
                startActivity(intent);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return dailyList.size();
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        TextView minMaxText;
        TextView itemCountText;
        FrameLayout chartFrame;
        LineChart temperatureChart;
        
        ViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            minMaxText = itemView.findViewById(R.id.minMaxText);
            itemCountText = itemView.findViewById(R.id.itemCountText);
            chartFrame = itemView.findViewById(R.id.chartFrame);
            temperatureChart = itemView.findViewById(R.id.temperatureChart);
        }
    }
}
```

**Linie cu linie:**

#### 1. Inner class (private)
```java
private class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
```
- **NOT static** - are acces la MainActivity fields/methods
- `<WeatherAdapter.ViewHolder>` - generic type pentru ViewHolder

#### 2. Constructor
```java
private List<DailyWeather> dailyList;

public WeatherAdapter(List<DailyWeather> dailyList) {
    this.dailyList = dailyList;
}
```
- Store reference la date
- NO defensive copy (share reference)

#### 3. onCreateViewHolder()
```java
@Override
public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_weather, parent, false);
    return new ViewHolder(view);
}
```

**When called:**
- RecyclerView needs new item view (scrolling off-screen)
- Only creates views needed for screen + buffer

**Inflate:**
- `R.layout.item_weather` - XML → View object
- `parent` - RecyclerView (for layout params)
- `false` - don't attach (RecyclerView will attach)

#### 4. onBindViewHolder()
```java
@Override
public void onBindViewHolder(ViewHolder holder, int position) {
    DailyWeather daily = dailyList.get(position);
```

**When called:**
- View scrolls on screen
- Data needs to be bound to view

**Binding:**
```java
holder.dateText.setText(daily.date);
holder.minMaxText.setText(String.format(Locale.US, "%.1f / %.1f°C", 
    daily.minTemp, daily.maxTemp));
holder.itemCountText.setText(daily.items.size() + " entries");
```
- Set TextViews
- Format: "18.5 / 25.3°C" (min / max)

**Chart setup:**
```java
if (holder.temperatureChart != null && holder.chartFrame != null) {
    setupTemperatureChart(holder.temperatureChart, holder.chartFrame, daily.items);
}
```
- **CRITICAL:** Setup chart pentru fiecare item
- Chart e recreat când view e recycled

**Click listener:**
```java
holder.itemView.setOnClickListener(v -> {
    if (!daily.items.isEmpty()) {
        Intent intent = new Intent(MainActivity.this, WeatherDetailActivity.class);
        intent.putExtra("weather_item", daily.items.get(0));  // First item of day
        startActivity(intent);
    }
});
```
- Tap pe item → open WeatherDetailActivity
- Pass first WeatherItem of day (00:00)

#### 5. getItemCount()
```java
@Override
public int getItemCount() {
    return dailyList.size();
}
```
- Returnează numărul de items (5 days)

#### 6. ViewHolder (inner class)
```java
class ViewHolder extends RecyclerView.ViewHolder {
    TextView dateText;
    TextView minMaxText;
    TextView itemCountText;
    FrameLayout chartFrame;
    LineChart temperatureChart;
    
    ViewHolder(View itemView) {
        super(itemView);
        dateText = itemView.findViewById(R.id.dateText);
        minMaxText = itemView.findViewById(R.id.minMaxText);
        itemCountText = itemView.findViewById(R.id.itemCountText);
        chartFrame = itemView.findViewById(R.id.chartFrame);
        temperatureChart = itemView.findViewById(R.id.temperatureChart);
    }
}
```

**ViewHolder pattern:**
- Caches findViewById results
- findViewById e SLOW (tree traversal)
- ViewHolder = call once, reuse forever

**Without ViewHolder (OLD Android):**
```java
TextView dateText = itemView.findViewById(R.id.dateText);  // Every bind!
dateText.setText(daily.date);
```

**With ViewHolder:**
```java
// Once in constructor:
holder.dateText = itemView.findViewById(R.id.dateText);

// Reuse in bind:
holder.dateText.setText(daily.date);  // FAST!
```

---

## Comparație cu 08_weather

### Final metrics

| Metric | 08_weather | 09_simple | Winner |
|--------|------------|-----------|--------|
| **Files** | 13 | 2 | 09_simple (-85%) |
| **Lines** | 2535 | 1091 | 09_simple (-57%) |
| **MainActivity** | 420 | 1039 | 08_weather (smaller) |
| **Imports** | ~150 (total) | 79 | 09_simple (no duplicates) |
| **Methods** | 45 (across classes) | 30 (in MainActivity) | 09_simple |
| **Inner classes** | 0 | 3 | 08_weather (separate files) |
| **Public fields** | 0 (all private + getters) | 22 (WeatherItem + DailyWeather) | 08_weather (encapsulation) |

### Code organization

**08_weather:**
```
To understand GPS flow:
1. Open MainActivity.java
2. Find GPSLocationProvider instantiation
3. Open GPSLocationProvider.java
4. Find requestLocation() method
5. Follow callback to MainActivity
6. Find onLocationFound() implementation
= Navigate 2 files, 5 jumps
```

**09_simple:**
```
To understand GPS flow:
1. Open MainActivity.java
2. Find requestGPSLocation() method
3. Read sequentially down
= 1 file, linear reading
```

### Maintainability

**Scenario: Add caching for API responses**

**08_weather:**
```java
// WeatherAPI.java
private Map<String, CachedResponse> cache = new HashMap<>();

public String fetchForecast(String cityName) throws IOException {
    if (cache.containsKey(cityName)) {
        CachedResponse cached = cache.get(cityName);
        if (!cached.isExpired()) {
            return cached.data;
        }
    }
    
    String response = actualFetch(cityName);
    cache.put(cityName, new CachedResponse(response));
    return response;
}
```
- Change 1 file (WeatherAPI.java)
- Add 1 class (CachedResponse.java)
- NO changes în MainActivity

**09_simple:**
```java
// MainActivity.java
private Map<String, CachedResponse> cache = new HashMap<>();  // Add field

private String fetchWeatherFromAPI(String cityName) throws Exception {
    // Add 10 lines caching logic BEFORE existing code
    if (cache.containsKey(cityName)) {
        CachedResponse cached = cache.get(cityName);
        if (!cached.isExpired()) {
            return cached.data;
        }
    }
    
    // Existing 70 lines...
    OkHttpClient client = new OkHttpClient();
    // ...
}

// Add inner class at end of MainActivity
public static class CachedResponse {
    String data;
    long timestamp;
    // ...
}
```
- Change 1 file (MainActivity.java)
- Add ~30 lines
- MainActivity grows to 1069 lines

**Winner:** 08_weather (isolated change)

---

## Rezumat final

### Ce am învățat

1. **God Object pattern** - tot într-un singur fișier
2. **Inline everything** - no abstraction, direct access
3. **Trade-offs** - simplicity vs maintainability
4. **Android APIs** - FusedLocationProviderClient, Geocoder, Parcelable, RecyclerView
5. **Libraries** - OkHttp, Gson, MPAndroidChart, Glide
6. **Performance** - SimpleDateFormat created în loop (bug!)

### Când să folosești 09_simple

✅ **Learning** - perfect pentru învățare
✅ **Prototyping** - rapid development
✅ **Small apps** - < 5 ecrane
✅ **Solo developer** - no team conflicts

### Când să folosești 08_weather

✅ **Production** - real apps
✅ **Teams** - multiple developers
✅ **Large apps** - 10+ ecrane
✅ **Testing** - unit tests required

### The Paradox

**09_simple are:**
- **MAI MULTE features** (GPS, multiple cities, charts, validation)
- **MAI PUȚIN cod** (1091 vs 2535 lines)
- **MAI PUȚINE files** (2 vs 13)

**Dar:**
- **MAI GREU de maintain** (1039 lines în 1 file)
- **MAI GREU de test** (no mocking)
- **MAI GREU de reuse** (tied to MainActivity)

### The Lesson

**Android NU te obligă la complexitate.** Poți construi aplicații feature-rich cu structură minimă dacă înțelegi trade-off-urile.

**Best Practice:** Start simple (09_simple) → refactor când crește (08_weather).

---

**FIN Part 2**

*[Revino la Part 1: MainActivity.java.md](MainActivity.java.md)*
