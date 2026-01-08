# MainActivity.java — Documentație completă ("God Object" pattern)

Acest document prezintă **întreaga aplicație Simple Weather într-un singur fișier** de 1039 linii. Spre deosebire de 08_weather care împarte logica în 13 clase separate, aici **totul este inline** pentru a demonstra că Android nu ne OBLIGĂ la complexitate excesivă.

## Cuprins

1. [Context general](#context-general)
2. [Structura fișierului](#structura-fisierului)
3. [Import-uri (65 linii)](#import-uri)
4. [Declararea clasei și constante](#declararea-clasei)
5. [onCreate() - Punctul de intrare](#oncreate)
6. [Persistență (SharedPreferences inline)](#persistenta)
7. [UI Setup (Spinner pentru orașe)](#ui-setup)
8. [Add City Dialog (cu validare real-time)](#add-city-dialog)
9. [GPS Location (FusedLocationProviderClient inline)](#gps-location)
10. [API Call (OkHttp inline, NO WeatherAPI class)](#api-call)
11. [JSON Parsing (Gson inline, NO WeatherParser class)](#json-parsing)
12. [Model Classes (Parcelable inline)](#model-classes)
13. [Chart Setup (MPAndroidChart cu Glide icons)](#chart-setup)
14. [RecyclerView Adapter (inline)](#recyclerview-adapter)
15. [Comparație cu 08_weather](#comparatie)

---

## Context general

### Filozofia "God Object"

În arhitectura software, un **"God Object"** este o anti-pattern unde o singură clasă conține toată logica aplicației. Profesionist, acest lucru este considerat:
- ❌ **BAD** pentru aplicații mari (10+ ecrane)
- ❌ **BAD** pentru echipe mari (5+ developeri)
- ❌ **BAD** pentru maintainability (modificări frecvente)

Dar pentru aplicații mici educaționale:
- ✅ **GOOD** pentru învățare (vezi tot într-un loc)
- ✅ **GOOD** pentru debugging (no navigare între 13 fișiere)
- ✅ **GOOD** pentru understanding (transparent flow)

### Ce conține MainActivity.java

| Responsabilitate | Linii cod | În 08_weather |
|------------------|-----------|---------------|
| UI setup + lifecycle | 150 | Activity (MainActivity.java) |
| Persistență | 40 | Repository (WeatherRepository.java) |
| GPS location | 200 | LocationProvider (GPSLocationProvider.java) |
| API calls | 80 | API (WeatherAPI.java) |
| JSON parsing | 120 | Parser (WeatherParser.java) |
| Model classes | 140 | Model (WeatherItem.java, DailyWeather.java) |
| Chart setup | 120 | Util (ChartHelper.java) |
| RecyclerView adapter | 80 | Adapter (WeatherAdapter.java) |
| **TOTAL** | **1039** | **8 fișiere separate** |

---

## Structura fișierului

```
MainActivity.java (1039 linii)
│
├── Package + Imports (1-77)
│   ├── Android SDK imports (40 import-uri)
│   ├── AndroidX imports (4 import-uri)
│   ├── External libraries (21 import-uri)
│   └── Java standard (9 import-uri)
│
├── Class declaration + Fields (78-100)
│   ├── TAG, constants
│   ├── UI components (RecyclerView, Spinner, etc.)
│   └── Data (cities list, currentCity)
│
├── onCreate() (101-134)
│   ├── setContentView
│   ├── findViewById × 3
│   ├── RecyclerView setup
│   ├── GPS client init
│   ├── Load cities
│   ├── Setup spinner
│   ├── Button listeners
│   └── GPS request (NO weather load!)
│
├── PERSISTENCE (136-174)
│   ├── loadCities() - SharedPreferences + Gson
│   ├── saveCities() - SharedPreferences + Gson
│   └── addCity() - Add + save + update UI
│
├── UI SETUP (176-212)
│   ├── setupCitySpinner() - ArrayAdapter + listener
│   └── updateSpinner() - Recreate adapter
│
├── ADD CITY DIALOG (214-322)
│   ├── showAddCityDialog() - AlertDialog builder
│   ├── TextWatcher - Real-time validation
│   ├── Handler debouncing - 600ms delay
│   └── API validation - fetchWeatherFromAPI
│
├── GPS LOCATION (324-572)
│   ├── requestGPSLocation() - Permission check
│   ├── loadDefaultCity() - Fallback
│   ├── getGPSLocation() - getLastLocation + timeout
│   ├── requestCurrentLocation() - Fresh GPS request
│   └── onRequestPermissionsResult() - Permission result
│
├── API CALL (574-670)
│   ├── loadWeather() - Thread + UI updates
│   ├── fetchWeatherFromAPI() - OkHttp + error handling
│   └── readAPIKey() - Assets + org.json.JSONObject
│
├── JSON PARSING (672-765)
│   ├── parseWeatherJSON() - Gson parsing
│   ├── Grouping by day - HashMap
│   └── calculateAverages() - min/max/avg temperatures
│
├── MODEL CLASSES (767-845)
│   ├── WeatherItem - Parcelable implementation
│   └── DailyWeather - Grouping class
│
├── CHART SETUP (847-970)
│   ├── setupTemperatureChart() - MPAndroidChart config
│   └── overlayIconsOnChart() - Glide image loading
│
└── RECYCLERVIEW ADAPTER (972-1039)
    ├── WeatherAdapter - Inner class
    ├── ViewHolder - Inner class
    ├── onCreateViewHolder() - Inflate layout
    ├── onBindViewHolder() - Bind data + setup chart
    └── getItemCount() - Size
```

---

## Import-uri

### Android SDK (40 import-uri)

```java
package ro.makore.simple_weather;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
```

**Linie cu linie:**

| Import | Scop | Folosit în |
|--------|------|------------|
| `Manifest` | Constante pentru permisiuni | `ACCESS_FINE_LOCATION` |
| `Context` | Bază pentru Activity | Moștenit de AppCompatActivity |
| `DialogInterface` | Interface pentru AlertDialog | showAddCityDialog() |
| `Intent` | Navigare între Activities | startActivity(WeatherDetailActivity) |
| `SharedPreferences` | Persistență key-value | loadCities(), saveCities() |
| `PackageManager` | Info despre permisiuni | `PERMISSION_GRANTED` |
| `Bitmap`, `BitmapFactory`, `Drawable` | Imagini (Glide) | overlayIconsOnChart() |
| `Color` | Culori pentru grafic | Chart setup |
| `Address`, `Geocoder` | GPS → city name | getGPSLocation() |
| `Bundle` | Salvare stare Activity | onCreate(Bundle) |
| `Handler` | Delayed execution | Validation debouncing, GPS timeout |
| `Parcel`, `Parcelable` | Serialization | WeatherItem |
| `Editable`, `TextWatcher` | Text input monitoring | showAddCityDialog() |
| `Log` | Logging | Log.d(TAG, ...) |
| `LayoutInflater` | XML → View | Adapter |
| `View`, `ViewGroup` | UI hierarchy | findViewById() |
| `AdapterView` | Spinner selection | onItemSelected() |
| `ArrayAdapter` | Spinner adapter | setupCitySpinner() |
| `Button`, `EditText`, `FrameLayout`, `ImageView`, `Spinner`, `TextView` | UI components | UI setup |
| `Toast` | Short messages | Error notifications |

### AndroidX (4 import-uri)

```java
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
```

| Import | Scop |
|--------|------|
| `AlertDialog` | Dialog modern (AndroidX) |
| `AppCompatActivity` | Base class pentru Activity |
| `ActivityCompat` | Permission requests (runtime) |
| `ContextCompat` | Permission checks |
| `LinearLayoutManager` | RecyclerView layout vertical |
| `RecyclerView` | Listă eficientă |

### External Libraries (21 import-uri)

```java
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.Transformer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
```

| Library | Import-uri | Scop |
|---------|------------|------|
| **Glide** | 3 | Încărcare imagini (weather icons) |
| **MPAndroidChart** | 8 | Temperature line charts |
| **Google Play Services** | 2 | GPS location (FusedLocationProviderClient) |
| **Gson** | 5 | JSON parsing |
| **OkHttp** | 3 | HTTP requests |

### Java Standard (9 import-uri)

```java
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
```

| Import | Scop |
|--------|------|
| `IOException` | Exception handling pentru API |
| `InputStream` | Citire api_key.json din assets |
| `URLEncoder` | Encode city name pentru URL |
| `StandardCharsets` | UTF-8 encoding |
| `SimpleDateFormat` | Date formatting |
| `ArrayList`, `HashMap`, `List`, `Map` | Collections |
| `Date` | Unix timestamp → Date |
| `Locale` | US locale pentru date |
| `OkHttpClient`, `Request`, `Response` | HTTP client |

**Comparație import-uri:**

| Categorie | 08_weather (per fișier) | 09_simple (total) |
|-----------|-------------------------|-------------------|
| Android SDK | 5-15 | 40 |
| AndroidX | 2-5 | 6 |
| External libs | 1-5 | 21 |
| Java standard | 3-8 | 12 |
| **TOTAL per fișier** | **10-30** | **79** |
| **FILES** | **8 files** | **1 file** |

**De ce atât de multe import-uri?**
- În 08_weather, fiecare clasă are 10-30 import-uri ⇒ total ~120-150 (cu duplicate)
- În 09_simple, **toate într-un singur loc** ⇒ 79 import-uri (fără duplicate)
- Actually, **09_simple are MAI PUȚINE import-uri** în total!

---

## Declararea clasei și constante

```java
/**
 * SIMPLE WEATHER APP - Still minimal but with GPS and multiple cities
 * Everything is INLINE in MainActivity except what Android FORCES to be separate
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SimpleWeather";
    private static final String PREFS_NAME = "weather_prefs";
    private static final String KEY_CITIES = "cities";
    private static final String KEY_CURRENT_CITY = "current_city";
    private static final String DEFAULT_CITY = "Bucharest";
    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    
    private RecyclerView recyclerView;
    private TextView loadingView;
    private Spinner citySpinner;
    private WeatherAdapter adapter;
    private ArrayAdapter<String> spinnerAdapter;
    private FusedLocationProviderClient fusedLocationClient;
    
    private List<String> cities = new ArrayList<>();
    private String currentCity;
```

**Linie cu linie:**

### JavaDoc comment
- "Everything is INLINE" - filozofia aplicației
- "except what Android FORCES" - doar WeatherDetailActivity e separat

### Constante (private static final)

| Constantă | Valoare | Scop |
|-----------|---------|------|
| `TAG` | "SimpleWeather" | Log.d(TAG, ...) - filter în Logcat |
| `PREFS_NAME` | "weather_prefs" | Numele fișierului SharedPreferences |
| `KEY_CITIES` | "cities" | Cheie pentru lista de orașe |
| `KEY_CURRENT_CITY` | "current_city" | Cheie pentru orașul selectat |
| `DEFAULT_CITY` | "Bucharest" | Oraș implicit |
| `LOCATION_PERMISSION_REQUEST` | 1001 | Request code pentru GPS permission |

**Best Practice:**
- ✅ `static final` = constantă (nu se modifică)
- ✅ `private` = accesibilă doar în MainActivity
- ✅ Nume descriptive (KEY_CITIES vs "c")
- ❌ Magic numbers/strings în cod (ar trebui constante)

### Field-uri (state al Activity-ului)

| Field | Tip | Scop |
|-------|-----|------|
| `recyclerView` | RecyclerView | Lista cu weather items |
| `loadingView` | TextView | "Loading..." text |
| `citySpinner` | Spinner | Dropdown pentru orașe |
| `adapter` | WeatherAdapter | Adapter pentru RecyclerView |
| `spinnerAdapter` | ArrayAdapter<String> | Adapter pentru Spinner |
| `fusedLocationClient` | FusedLocationProviderClient | GPS provider |
| `cities` | List<String> | Lista orașelor salvate |
| `currentCity` | String | Orașul selectat curent |

**Lifecycle:** Aceste field-uri sunt create în `onCreate()` și distruse când Activity-ul este distrus.

---

## onCreate() - Punctul de intrare

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        recyclerView = findViewById(R.id.recyclerView);
        loadingView = findViewById(R.id.loadingText);
        citySpinner = findViewById(R.id.citySpinner);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Initialize GPS
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        // Load saved cities
        loadCities();
        
        // Setup spinner
        setupCitySpinner();
        
        // Setup buttons
        findViewById(R.id.btnRefresh).setOnClickListener(v -> loadWeather(currentCity));
        findViewById(R.id.btnAddCity).setOnClickListener(v -> showAddCityDialog());
        
        // Get GPS location - this will load weather when ready
        // If GPS fails or permission denied, fallback to default city
        requestGPSLocation();
        
        // DO NOT load weather here - wait for GPS first!
    }
```

**Linie cu linie:**

### 1. Inflating layout
```java
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_main);
```
- **OBLIGATORIU** să apelăm `super.onCreate()` primul
- `setContentView()` - încarcă XML-ul în memorie și creează ierarhia de View-uri

### 2. Finding views
```java
recyclerView = findViewById(R.id.recyclerView);
loadingView = findViewById(R.id.loadingText);
citySpinner = findViewById(R.id.citySpinner);
```
- `findViewById()` - caută view-ul după ID în layout-ul încărcat
- Returnează `null` dacă ID-ul nu există (crashează la runtime dacă folosim view-ul null)

### 3. RecyclerView setup
```java
recyclerView.setLayoutManager(new LinearLayoutManager(this));
```
- **OBLIGATORIU** pentru RecyclerView
- `LinearLayoutManager` - afișare verticală (listă)
- Alternative: `GridLayoutManager` (grilă), `StaggeredGridLayoutManager` (Pinterest style)

### 4. GPS client init
```java
fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
```
- **Google Play Services** - cel mai precis provider de locație
- Alternative: `LocationManager` (deprecated, mai puțin precis)

### 5. Load saved cities
```java
loadCities();
```
- Citește lista de orașe din SharedPreferences
- Dacă lista e goală, adaugă DEFAULT_CITY ("Bucharest")

### 6. Setup spinner
```java
setupCitySpinner();
```
- Creează ArrayAdapter cu lista de orașe
- Setează listener pentru selecție oraș

### 7. Button listeners
```java
findViewById(R.id.btnRefresh).setOnClickListener(v -> loadWeather(currentCity));
findViewById(R.id.btnAddCity).setOnClickListener(v -> showAddCityDialog());
```
- Lambda expressions (Java 8)
- Refresh button - reîncarcă vremea pentru orașul curent
- Add City button - afișează dialog pentru adăugare oraș nou

### 8. GPS request (CRITICAL!)
```java
requestGPSLocation();

// DO NOT load weather here - wait for GPS first!
```

**IMPORTANT:** În versiunea inițială, aici era:
```java
loadWeather(currentCity); // WRONG - loads before GPS!
```

Problema:
1. User deschide app
2. `onCreate()` apelează `loadWeather("Bucharest")` IMEDIAT
3. APOI `requestGPSLocation()` este apelat
4. GPS-ul găsește "Mountain View", dar vremea pentru Bucharest e deja afișată
5. User vede GREȘIT orașul

**Soluție corectă:**
```java
requestGPSLocation(); // Get GPS FIRST
// GPS success → loadWeather(gps_city)
// GPS fail/timeout → loadDefaultCity() → loadWeather(currentCity)
```

Flow corect:
```
onCreate()
    ↓
requestGPSLocation()
    ↓
[Permission granted?]
    ↓ YES
getGPSLocation()
    ↓
[Location found?]
    ↓ YES
Geocoder.getFromLocation()
    ↓
[City name found?]
    ↓ YES
addCity(gps_city)
currentCity = gps_city
loadWeather(gps_city) ← PRIMUL weather load!
    ↓
Display weather for GPS location
```

Fallback flow:
```
onCreate()
    ↓
requestGPSLocation()
    ↓
[Permission denied OR GPS timeout OR Geocoder fails]
    ↓
loadDefaultCity()
    ↓
loadWeather(currentCity) ← Folosim orașul salvat
```

**De ce e important?**
- ✅ GPS location are prioritate (user experience mai bun)
- ✅ Default city doar dacă GPS FAILS (fallback corect)
- ✅ NO duplicate weather loads (eficiență)

---

## Persistență (SharedPreferences inline)

În 08_weather, persistența este într-o clasă separată `WeatherRepository.java`. În 09_simple, **totul e inline în MainActivity**.

### loadCities()

```java
    private void loadCities() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_CITIES, null);
        
        if (json != null) {
            cities = new Gson().fromJson(json, new TypeToken<List<String>>(){}.getType());
        }
        
        if (cities.isEmpty()) {
            cities.add(DEFAULT_CITY);
        }
        
        currentCity = prefs.getString(KEY_CURRENT_CITY, cities.get(0));
    }
```

**Linie cu linie:**

1. **Get SharedPreferences instance**
```java
SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
```
- `PREFS_NAME` = "weather_prefs" - nume fișier XML în `/data/data/ro.makore.simple_weather/shared_prefs/`
- `MODE_PRIVATE` - accesibil doar acestei aplicații (NOT world-readable)

2. **Read JSON string**
```java
String json = prefs.getString(KEY_CITIES, null);
```
- `KEY_CITIES` = "cities"
- Al doilea parametru (`null`) = valoare default dacă cheia nu există

3. **Parse JSON → List**
```java
if (json != null) {
    cities = new Gson().fromJson(json, new TypeToken<List<String>>(){}.getType());
}
```
- **Gson** - Google JSON library
- `new TypeToken<List<String>>(){}` - workaround pentru Java generics erasure
  - La runtime, Java ȘTerge tipurile generice: `List<String>` devine `List`
  - TypeToken păstrează informația de tip pentru Gson

**Exemplu JSON salvat:**
```json
["Bucharest", "London", "Mountain View"]
```

4. **Ensure non-empty list**
```java
if (cities.isEmpty()) {
    cities.add(DEFAULT_CITY);
}
```
- Dacă JSON era `null` sau `[]`, adăugăm "Bucharest"
- Garantăm că lista ÎNTOTDEAUNA are cel puțin un oraș

5. **Load current city**
```java
currentCity = prefs.getString(KEY_CURRENT_CITY, cities.get(0));
```
- Citește orașul selectat ultima dată
- Default: primul oraș din listă

### saveCities()

```java
    private void saveCities() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = new Gson().toJson(cities);
        prefs.edit()
            .putString(KEY_CITIES, json)
            .putString(KEY_CURRENT_CITY, currentCity)
            .apply();
    }
```

**Linie cu linie:**

1. **Convert List → JSON**
```java
String json = new Gson().toJson(cities);
```
- Gson convertește automat `List<String>` → JSON array
- Exemplu: `["Bucharest", "London"]` → `"[\"Bucharest\",\"London\"]"`

2. **Save to SharedPreferences**
```java
prefs.edit()
    .putString(KEY_CITIES, json)
    .putString(KEY_CURRENT_CITY, currentCity)
    .apply();
```
- `edit()` - creează Editor (builder pattern)
- `putString()` × 2 - salvează 2 valori
- `apply()` - **async** write pe disk
  - Alternative: `commit()` - **sync** write (blochează thread-ul)

**Când se apelează saveCities()?**
- După adăugare oraș (addCity dialog)
- După selecție oraș din Spinner
- După GPS location găsit

### addCity()

```java
    private void addCity(String city) {
        if (!cities.contains(city)) {
            cities.add(city);
            saveCities();
            updateSpinner();
        }
    }
```

**Linie cu linie:**

1. **Duplicate check**
```java
if (!cities.contains(city)) {
```
- Evităm duplicate în listă
- `contains()` - O(n) search (linear), OK pentru liste mici

2. **Add + Save + Update UI**
```java
cities.add(city);
saveCities();
updateSpinner();
```
- Add la listă
- Save on disk
- Recreate Spinner adapter (refresh UI)

**Comparație cu 08_weather:**

| Aspect | 08_weather | 09_simple |
|--------|------------|-----------|
| **Locație cod** | WeatherRepository.java (80 linii) | MainActivity.java (40 linii inline) |
| **Singleton pattern** | DA (getInstance()) | NU (direct în Activity) |
| **Dependency injection** | Repository injectat în Activity | Direct access |
| **Testability** | Ușor de testat (mock repository) | Greu de testat (tied to Activity) |
| **Reusability** | Refolosibil în alte Activities | Nu se poate refolosi |
| **Complexity** | Mai complex (pattern overhead) | Mai simplu (direct) |

**De ce inline?**
- ✅ Cod mai puțin (40 vs 80 linii)
- ✅ Mai transparent (vezi exact ce se întâmplă)
- ✅ No dependency injection setup
- ❌ Nu se poate refolosi în alte Activities
- ❌ Mai greu de testat (mock SharedPreferences)

---

## UI Setup (Spinner pentru orașe)

### setupCitySpinner()

```java
    private void setupCitySpinner() {
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(spinnerAdapter);
        
        int position = cities.indexOf(currentCity);
        if (position >= 0) {
            citySpinner.setSelection(position);
        }
        
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = cities.get(position);
                if (!selected.equals(currentCity)) {
                    currentCity = selected;
                    saveCities();
                    loadWeather(currentCity);
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
```

**Linie cu linie:**

### 1. Create ArrayAdapter
```java
spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
```
- `ArrayAdapter` - adapter pentru Spinner (simplu, pentru liste de strings)
- `this` - Context (MainActivity)
- `android.R.layout.simple_spinner_item` - Layout Android default pentru item în Spinner
- `cities` - Lista de orașe (`List<String>`)

### 2. Set dropdown layout
```java
spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
```
- Layout pentru dropdown când Spinner e deschis
- `simple_spinner_dropdown_item` - layout Android default (text cu padding)

### 3. Set adapter
```java
citySpinner.setAdapter(spinnerAdapter);
```
- Conectează adapter-ul la Spinner
- Spinner-ul afișează automat primul item

### 4. Select current city
```java
int position = cities.indexOf(currentCity);
if (position >= 0) {
    citySpinner.setSelection(position);
}
```
- `indexOf()` - găsește poziția orașului curent în listă
- Returnează `-1` dacă nu găsește (de asta check `>= 0`)
- `setSelection()` - setează selecția programatic

### 5. Selection listener
```java
citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = cities.get(position);
        if (!selected.equals(currentCity)) {
            currentCity = selected;
            saveCities();
            loadWeather(currentCity);
        }
    }
    
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
});
```

**onItemSelected():**
- Apelat când user selectează un oraș din dropdown
- `position` - indexul orașului în listă
- `cities.get(position)` - numele orașului

**Logic:**
1. Get selected city
2. Check dacă e diferit de currentCity (evităm reload inutil)
3. Update currentCity
4. Save to SharedPreferences
5. Load weather pentru noul oraș

**onNothingSelected():**
- Apelat când selecția e cleared (rar)
- În cazul nostru, nu facem nimic

### updateSpinner()

```java
    private void updateSpinner() {
        runOnUiThread(() -> {
            // Recreate the adapter to avoid issues
            spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            citySpinner.setAdapter(spinnerAdapter);
            
            int position = cities.indexOf(currentCity);
            if (position >= 0) {
                citySpinner.setSelection(position);
            }
        });
    }
```

**Linie cu linie:**

### runOnUiThread()
```java
runOnUiThread(() -> {
```
- **CRITICAL** - UI updates TREBUIE pe Main Thread
- Dacă `updateSpinner()` e apelat din background thread (ex: după GPS location), Android crashează cu:
  ```
  android.view.ViewRootImpl$CalledFromWrongThreadException: 
  Only the original thread that created a view hierarchy can touch its views.
  ```
- `runOnUiThread()` - garantează că lambda-ul se execută pe Main Thread

### Recreate adapter
```java
spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
citySpinner.setAdapter(spinnerAdapter);
```

**De ce recreate?**
- În loc de `spinnerAdapter.notifyDataSetChanged()` (care uneori nu funcționează)
- Recreăm adapter-ul complet cu noua listă
- Mai safe, mai predictibil

**Alternative (care NU funcționau consistent):**
```java
// ❌ BROKEN - sometimes Spinner shows empty items
spinnerAdapter.clear();
spinnerAdapter.addAll(cities);
spinnerAdapter.notifyDataSetChanged();
```

**Comparație UI setup:**

| Aspect | 08_weather | 09_simple |
|--------|------------|-----------|
| **Spinner** | NU (single city) | DA (multiple cities) |
| **RecyclerView** | DA | DA |
| **ViewBinding** | DA | NU (findViewById) |
| **Custom layouts** | DA (complex item layout) | DA (cu chart) |
| **Selection handling** | N/A | OnItemSelectedListener |

---

## Add City Dialog (cu validare real-time)

Cea mai complexă parte a UI-ului: dialog cu **validare asincronă** și **debouncing**.

```java
    private android.os.Handler validationHandler = new android.os.Handler();
    private Runnable validationRunnable;
    
    private void showAddCityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add City");
        
        // Create custom layout with EditText and feedback TextView
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        
        final EditText input = new EditText(this);
        input.setHint("Enter city name");
        layout.addView(input);
        
        final TextView feedback = new TextView(this);
        feedback.setPadding(0, 20, 0, 0);
        feedback.setTextSize(14);
        layout.addView(feedback);
        
        builder.setView(layout);
        
        builder.setPositiveButton("Add", null); // Set to null, we'll override later
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        
        AlertDialog dialog = builder.create();
        dialog.show();
        
        // Get the Add button and disable it initially
        Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        addButton.setEnabled(false);
        
        // Set up real-time validation with debouncing
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                String city = s.toString().trim();
                
                // Cancel previous validation
                if (validationRunnable != null) {
                    validationHandler.removeCallbacks(validationRunnable);
                }
                
                if (city.isEmpty()) {
                    feedback.setText("");
                    addButton.setEnabled(false);
                    return;
                }
                
                // Check if city already exists
                if (cities.contains(city)) {
                    feedback.setText("City already in list");
                    feedback.setTextColor(0xFFFF5722); // Orange
                    addButton.setEnabled(false);
                    return;
                }
                
                feedback.setText("Checking...");
                feedback.setTextColor(0xFF9E9E9E); // Gray
                addButton.setEnabled(false);
                
                // Debounce: wait 600ms before validating
                validationRunnable = () -> {
                    new Thread(() -> {
                        try {
                            fetchWeatherFromAPI(city); // Throws if city not found
                            runOnUiThread(() -> {
                                feedback.setText("✓ City found");
                                feedback.setTextColor(0xFF4CAF50); // Green
                                addButton.setEnabled(true);
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                feedback.setText("✗ City not found");
                                feedback.setTextColor(0xFFF44336); // Red
                                addButton.setEnabled(false);
                            });
                        }
                    }).start();
                };
                
                validationHandler.postDelayed(validationRunnable, 600);
            }
        });
        
        // Override Add button click
        addButton.setOnClickListener(v -> {
            String city = input.getText().toString().trim();
            if (!city.isEmpty() && !cities.contains(city)) {
                addCity(city);
                currentCity = city;
                saveCities();
                updateSpinner();
                loadWeather(city);
                Toast.makeText(this, "Added: " + city, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
```

**Linie cu linie:**

### 1. Handler pentru debouncing
```java
private android.os.Handler validationHandler = new android.os.Handler();
private Runnable validationRunnable;
```
- **Handler** - permite delayed execution pe Main Thread
- **Runnable** - task care va fi executat după delay
- Field-uri la nivel de clasă (pot fi accesate din inner classes)

### 2. Create AlertDialog
```java
AlertDialog.Builder builder = new AlertDialog.Builder(this);
builder.setTitle("Add City");
```
- Builder pattern pentru AlertDialog
- Titlu: "Add City"

### 3. Custom layout (LinearLayout vertical)
```java
android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
layout.setOrientation(android.widget.LinearLayout.VERTICAL);
layout.setPadding(50, 40, 50, 10);
```
- `LinearLayout` - container vertical (EditText + feedback TextView)
- Padding în pixels (50, 40, 50, 10) = (left, top, right, bottom)

**Best Practice:** Ar trebui folosit DP (density-independent pixels) în loc de pixels:
```java
int padding = (int) (50 * getResources().getDisplayMetrics().density);
```

### 4. EditText pentru input
```java
final EditText input = new EditText(this);
input.setHint("Enter city name");
layout.addView(input);
```
- Hint: text care dispare când user începe să scrie
- `final` - necesar pentru a fi accesat în inner classes

### 5. TextView pentru feedback
```java
final TextView feedback = new TextView(this);
feedback.setPadding(0, 20, 0, 0);
feedback.setTextSize(14);
layout.addView(feedback);
```
- Afișează status validare: "Checking...", "✓ City found", "✗ City not found"
- Top padding 20px pentru spacing

### 6. Set layout + buttons
```java
builder.setView(layout);

builder.setPositiveButton("Add", null); // null = disable default behavior
builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

AlertDialog dialog = builder.create();
dialog.show();
```

**CRITICAL:** `setPositiveButton("Add", null)`
- `null` = NU avem listener
- De ce? Dacă setăm listener aici, butonul va închide dialog-ul AUTOMAT când e apăsat
- Vrem să controlăm noi când se închide (doar dacă validarea e OK)

### 7. Get button reference + disable
```java
Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
addButton.setEnabled(false);
```
- **AFTER** `dialog.show()` - altfel butonul e `null`
- Disabled by default - user trebuie să introducă oraș valid

### 8. TextWatcher cu debouncing

```java
input.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}
    
    @Override
    public void afterTextChanged(Editable s) {
        // Validation logic here
    }
});
```

**TextWatcher interface:**
- `beforeTextChanged()` - înainte de modificare (rar folosit)
- `onTextChanged()` - în timpul modificării (folosit pentru real-time search)
- `afterTextChanged()` - după modificare (**folosim noi aici**)

**De ce afterTextChanged?**
- Avem acces la `Editable` (text finalizat)
- Putem modifica textul (ex: uppercase)
- Mai safe pentru validation

### 9. Validation logic

#### a) Cancel previous validation
```java
String city = s.toString().trim();

if (validationRunnable != null) {
    validationHandler.removeCallbacks(validationRunnable);
}
```

**De ce?**
User tastează "Lond" → delay 600ms → API call
User continuă "London" → CANCEL primul API call → delay 600ms → API call pentru "London"

Fără cancel:
```
User: "L"     → API call după 600ms pentru "L" (GREȘIT)
User: "Lo"    → API call după 600ms pentru "Lo" (GREȘIT)
User: "Lon"   → API call după 600ms pentru "Lon" (GREȘIT)
User: "Lond"  → API call după 600ms pentru "Lond" (GREȘIT)
User: "London" → API call după 600ms pentru "London" (CORECT)
```
= 5 API calls! Rate limit exceeded!

Cu cancel (debouncing):
```
User: "L"     → delay 600ms
User: "Lo"    → CANCEL delay → delay 600ms
User: "Lon"   → CANCEL delay → delay 600ms
User: "Lond"  → CANCEL delay → delay 600ms
User: "London" → CANCEL delay → delay 600ms
[User stops typing]
→ API call pentru "London" (1 singur call!)
```

#### b) Empty check
```java
if (city.isEmpty()) {
    feedback.setText("");
    addButton.setEnabled(false);
    return;
}
```
- Dacă input e gol, disable button
- Clear feedback

#### c) Duplicate check
```java
if (cities.contains(city)) {
    feedback.setText("City already in list");
    feedback.setTextColor(0xFFFF5722); // Orange
    addButton.setEnabled(false);
    return;
}
```
- Check local list (instant, no API call)
- Orange color = warning (not error)

#### d) Show "Checking..."
```java
feedback.setText("Checking...");
feedback.setTextColor(0xFF9E9E9E); // Gray
addButton.setEnabled(false);
```
- User vede că se validează
- Gray = neutral

#### e) Debounced API validation
```java
validationRunnable = () -> {
    new Thread(() -> {
        try {
            fetchWeatherFromAPI(city); // Throws if city not found
            runOnUiThread(() -> {
                feedback.setText("✓ City found");
                feedback.setTextColor(0xFF4CAF50); // Green
                addButton.setEnabled(true);
            });
        } catch (Exception e) {
            runOnUiThread(() -> {
                feedback.setText("✗ City not found");
                feedback.setTextColor(0xFFF44336); // Red
                addButton.setEnabled(false);
            });
        }
    }).start();
};

validationHandler.postDelayed(validationRunnable, 600);
```

**Flow:**
1. Create Runnable (lambda)
2. Inside: new Thread (background)
3. Try API call (fetchWeatherFromAPI)
   - Success → green "✓ City found" + enable button
   - Failure → red "✗ City not found" + disable button
4. Schedule cu 600ms delay

**De ce 600ms?**
- 300ms = prea rapid (API call la fiecare literă dacă user tastează încet)
- 1000ms = prea lent (user așteaptă prea mult)
- 600ms = sweet spot (echilibru între rapiditate și eficiență)

### 10. Override Add button
```java
addButton.setOnClickListener(v -> {
    String city = input.getText().toString().trim();
    if (!city.isEmpty() && !cities.contains(city)) {
        addCity(city);
        currentCity = city;
        saveCities();
        updateSpinner();
        loadWeather(city);
        Toast.makeText(this, "Added: " + city, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
});
```

**Linie cu linie:**
1. Get city name
2. Double-check (!empty && !duplicate)
3. `addCity()` - add to list + save + update spinner
4. Set as current city
5. Load weather
6. Show toast
7. Dismiss dialog

**Comparație validation:**

| Feature | 08_weather | 09_simple |
|---------|------------|-----------|
| **Add city** | NU | DA |
| **Real-time validation** | N/A | DA (TextWatcher) |
| **Debouncing** | N/A | DA (600ms) |
| **API validation** | N/A | DA (fetchWeatherFromAPI) |
| **Visual feedback** | N/A | DA (colored text) |
| **Button state** | N/A | DA (enabled/disabled) |

---

## GPS Location (FusedLocationProviderClient inline)

Cea mai complexă parte a aplicației: **200 linii de cod GPS** care gestionează permisiuni, timeout-uri, fallback-uri și geocoding.

### Structura GPS flow

```
requestGPSLocation()
    ↓
[Check permission]
    ↓ DENIED
    ActivityCompat.requestPermissions()
    ↓ User grants/denies
    onRequestPermissionsResult()
    ↓
[GRANTED]
getGPSLocation()
    ↓
Set 10s timeout (Handler)
    ↓
fusedLocationClient.getLastLocation()
    ↓
[Location != null?]
    ↓ YES
    Geocoder.getFromLocation() [Background Thread]
    ↓
[City name found?]
    ↓ YES
    addCity(gpsCity)
    loadWeather(gpsCity)
    ↓ NO (null location)
    requestCurrentLocation() [Fresh GPS request]
    ↓
[Fresh location found?]
    ↓ YES
    Same geocoding flow
    ↓ NO (timeout or error)
    loadDefaultCity()
```

### requestGPSLocation()

```java
private void requestGPSLocation() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, 
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, 
                         Manifest.permission.ACCESS_COARSE_LOCATION}, 
            LOCATION_PERMISSION_REQUEST);
        return;
    }
    
    getGPSLocation();
}
```

**Linie cu linie:**

#### 1. Check permission
```java
if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
        != PackageManager.PERMISSION_GRANTED)
```
- **Runtime permissions** (Android 6.0+)
- `ContextCompat.checkSelfPermission()` - returnează `PERMISSION_GRANTED` sau `PERMISSION_DENIED`
- `ACCESS_FINE_LOCATION` - GPS precis (necesită și `ACCESS_COARSE_LOCATION`)

#### 2. Request permission
```java
ActivityCompat.requestPermissions(this, 
    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, 
                 Manifest.permission.ACCESS_COARSE_LOCATION}, 
    LOCATION_PERMISSION_REQUEST);
return;
```
- **Afișează dialog sistem** "Allow [app] to access this device's location?"
- Array cu 2 permisiuni (fine + coarse)
- Request code `1001` - folosit în `onRequestPermissionsResult()`
- `return` - STOP aici, continuăm în callback

#### 3. Get GPS location
```java
getGPSLocation();
```
- Dacă permisiunea e deja granted, apelăm direct

---

### loadDefaultCity()

```java
private void loadDefaultCity() {
    Log.d(TAG, "Loading default city: " + currentCity);
    runOnUiThread(() -> {
        if (currentCity != null && !currentCity.isEmpty()) {
            loadWeather(currentCity);
        }
    });
}
```

**Linie cu linie:**

#### Fallback logic
- Apelat când GPS FAILS (timeout, error, permission denied)
- Folosește `currentCity` din SharedPreferences (ultimul oraș salvat)
- `runOnUiThread()` - poate fi apelat din background thread
- `loadWeather(currentCity)` - încarcă vremea pentru orașul salvat

**Când se apelează:**
1. GPS timeout (10s)
2. GPS permission denied
3. GPS location null și fresh request timeout (8s)
4. Geocoder fails (no city name)

---

### getGPSLocation()

```java
private void getGPSLocation() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
        return;
    }
    
    // Set a timeout for GPS - if it takes too long, fallback to default
    final Handler timeoutHandler = new Handler();
    final Runnable timeoutRunnable = () -> {
        Log.w(TAG, "GPS timeout, using default city");
        loadDefaultCity();
    };
    
    // 10 second timeout
    timeoutHandler.postDelayed(timeoutRunnable, 10000);
    
    fusedLocationClient.getLastLocation()
        .addOnSuccessListener(this, location -> {
            if (location != null) {
                Log.d(TAG, "Got last location: lat=" + location.getLatitude() + ", lon=" + location.getLongitude());
                
                // Get city name from coordinates
                new Thread(() -> {
                    try {
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                        
                        if (addresses != null && !addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            String cityName = address.getLocality();
                            
                            Log.d(TAG, "Geocoded locality: " + cityName);
                            
                            if (cityName == null || cityName.isEmpty()) {
                                cityName = address.getAdminArea();
                                Log.d(TAG, "Using adminArea: " + cityName);
                            }
                            if (cityName == null || cityName.isEmpty()) {
                                cityName = address.getCountryName();
                                Log.d(TAG, "Using country: " + cityName);
                            }
                            
                            if (cityName != null && !cityName.isEmpty()) {
                                final String finalCity = cityName;
                                runOnUiThread(() -> {
                                    timeoutHandler.removeCallbacks(timeoutRunnable);
                                    
                                    Log.d(TAG, "GPS location: " + finalCity);
                                    
                                    // Add city if not already in list
                                    if (!cities.contains(finalCity)) {
                                        cities.add(0, finalCity); // Add at beginning
                                        saveCities();
                                    }
                                    
                                    currentCity = finalCity;
                                    saveCities();
                                    updateSpinner();
                                    loadWeather(finalCity);
                                    
                                    Toast.makeText(this, "Current location: " + finalCity, Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                Log.w(TAG, "Could not determine city name, using default");
                                runOnUiThread(() -> {
                                    timeoutHandler.removeCallbacks(timeoutRunnable);
                                    loadDefaultCity();
                                });
                            }
                        } else {
                            Log.w(TAG, "Geocoder returned no addresses, using default");
                            runOnUiThread(() -> {
                                timeoutHandler.removeCallbacks(timeoutRunnable);
                                loadDefaultCity();
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Geocoding error, using default", e);
                        runOnUiThread(() -> {
                            timeoutHandler.removeCallbacks(timeoutRunnable);
                            loadDefaultCity();
                        });
                    }
                }).start();
            } else {
                Log.w(TAG, "GPS location is null, trying to get fresh location");
                // Last location is null, try to get current location
                requestCurrentLocation();
            }
        })
        .addOnFailureListener(e -> {
            timeoutHandler.removeCallbacks(timeoutRunnable);
            Log.e(TAG, "Failed to get location, using default", e);
            loadDefaultCity();
        });
}
```

**Linie cu linie:**

#### 1. Permission re-check
```java
if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
        != PackageManager.PERMISSION_GRANTED) {
    return;
}
```
- **Safety check** - permisiunea poate fi revocată oricând
- Early return dacă nu avem permisiune

#### 2. Timeout setup
```java
final Handler timeoutHandler = new Handler();
final Runnable timeoutRunnable = () -> {
    Log.w(TAG, "GPS timeout, using default city");
    loadDefaultCity();
};

timeoutHandler.postDelayed(timeoutRunnable, 10000);
```

**De ce timeout?**
- GPS poate dura FOARTE mult (10-30s în interior)
- User nu vrea să aștepte atât
- 10s = suficient pentru GPS outdoor, rezonabil pentru indoor

#### 3. Get last known location
```java
fusedLocationClient.getLastLocation()
    .addOnSuccessListener(this, location -> {
```

**FusedLocationProviderClient:**
- **Google Play Services** API
- Mai precis decât LocationManager (deprecated)
- `getLastLocation()` - returnează ultima locație cunoscută (FAST)
  - Poate fi NULL (GPS nu a fost folosit recent)
  - Poate fi veche (de ieri, de săptămâna trecută)
- Returns `Task<Location>` - async operation

**addOnSuccessListener:**
- Apelat când Task-ul se completează cu succes
- `this` = LifecycleOwner (Activity) - callback se anulează când Activity-ul e distrus
- `location` - poate fi `null`!

#### 4. Location != null path
```java
if (location != null) {
    Log.d(TAG, "Got last location: lat=" + location.getLatitude() + ", lon=" + location.getLongitude());
    
    // Get city name from coordinates
    new Thread(() -> {
```

**De ce new Thread?**
- `Geocoder.getFromLocation()` - **BLOCKING** operation (network call)
- Poate dura 1-5 secunde
- NU poate fi apelat pe Main Thread (crash: `NetworkOnMainThreadException`)

#### 5. Geocoder - coordonate → oraș
```java
Geocoder geocoder = new Geocoder(this, Locale.getDefault());
List<Address> addresses = geocoder.getFromLocation(
    location.getLatitude(), location.getLongitude(), 1);
```

**Geocoder:**
- Android API pentru reverse geocoding
- Input: lat/lon
- Output: `List<Address>` (adrese posibile)
- Parametru `1` = returnează doar 1 adresă (cea mai apropiată)

**IMPORTANT:** Geocoder poate returna:
- `null` - serviciu indisponibil
- Empty list - coordonate în ocean/deșert
- List cu Address - SUCCESS

#### 6. Extract city name (cu fallback)
```java
Address address = addresses.get(0);
String cityName = address.getLocality();

Log.d(TAG, "Geocoded locality: " + cityName);

if (cityName == null || cityName.isEmpty()) {
    cityName = address.getAdminArea();
    Log.d(TAG, "Using adminArea: " + cityName);
}
if (cityName == null || cityName.isEmpty()) {
    cityName = address.getCountryName();
    Log.d(TAG, "Using country: " + cityName);
}
```

**Address fields (fallback hierarchy):**

| Field | Description | Example | Availability |
|-------|-------------|---------|--------------|
| `getLocality()` | City name | "Bucharest" | **BEST** (dar poate fi null) |
| `getAdminArea()` | State/Province | "București" | Fallback 1 |
| `getCountryName()` | Country | "Romania" | Fallback 2 (ultimă șansă) |

**De ce fallback?**
- În zone rurale, `getLocality()` poate returna `null`
- În deșerturi, doar `getCountryName()` e disponibil
- Preferăm oraș > regiune > țară

#### 7. Success path - update UI
```java
if (cityName != null && !cityName.isEmpty()) {
    final String finalCity = cityName;
    runOnUiThread(() -> {
        timeoutHandler.removeCallbacks(timeoutRunnable);
        
        Log.d(TAG, "GPS location: " + finalCity);
        
        // Add city if not already in list
        if (!cities.contains(finalCity)) {
            cities.add(0, finalCity); // Add at beginning
            saveCities();
        }
        
        currentCity = finalCity;
        saveCities();
        updateSpinner();
        loadWeather(finalCity);
        
        Toast.makeText(this, "Current location: " + finalCity, Toast.LENGTH_SHORT).show();
    });
}
```

**Linie cu linie:**

- `final String finalCity` - necesar pentru lambda (captured variable)
- `runOnUiThread()` - suntem în background thread, UI updates TREBUIE pe Main Thread
- `timeoutHandler.removeCallbacks()` - **CRITICAL** - anulăm timeout-ul (am avut succes!)
- `cities.add(0, finalCity)` - add la **început** (poziția 0) - orașul curent e primul în listă
- `saveCities()` - salvăm lista + currentCity
- `updateSpinner()` - refresh UI cu noua listă
- `loadWeather(finalCity)` - **PRIMUL weather load** al aplicației!
- `Toast` - feedback vizual pentru user

#### 8. Failure paths

**No city name:**
```java
} else {
    Log.w(TAG, "Could not determine city name, using default");
    runOnUiThread(() -> {
        timeoutHandler.removeCallbacks(timeoutRunnable);
        loadDefaultCity();
    });
}
```

**No addresses:**
```java
} else {
    Log.w(TAG, "Geocoder returned no addresses, using default");
    runOnUiThread(() -> {
        timeoutHandler.removeCallbacks(timeoutRunnable);
        loadDefaultCity();
    });
}
```

**Exception:**
```java
} catch (Exception e) {
    Log.e(TAG, "Geocoding error, using default", e);
    runOnUiThread(() -> {
        timeoutHandler.removeCallbacks(timeoutRunnable);
        loadDefaultCity();
    });
}
```

**Pattern comun:**
- Log error
- Cancel timeout
- Fallback to default city

#### 9. Location == null path
```java
} else {
    Log.w(TAG, "GPS location is null, trying to get fresh location");
    // Last location is null, try to get current location
    requestCurrentLocation();
}
```

**De ce null?**
- GPS nu a fost folosit recent
- Device-ul tocmai a pornit
- Location services disabled apoi enabled

**Soluție:**
- Request **fresh** GPS location (mai lent, dar garantat recent)

#### 10. Failure listener
```java
.addOnFailureListener(e -> {
    timeoutHandler.removeCallbacks(timeoutRunnable);
    Log.e(TAG, "Failed to get location, using default", e);
    loadDefaultCity();
});
```

**Când se apelează:**
- GPS disabled
- Play Services nu e instalat
- Internal error în FusedLocationProviderClient

---

### requestCurrentLocation()

```java
private void requestCurrentLocation() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
        return;
    }
    
    Log.d(TAG, "Requesting fresh GPS location...");
    
    // Set a timeout for fresh GPS request
    final Handler timeoutHandler = new Handler();
    final Runnable timeoutRunnable = () -> {
        Log.w(TAG, "Fresh GPS request timeout, using default city");
        loadDefaultCity();
    };
    
    // 8 second timeout for fresh location
    timeoutHandler.postDelayed(timeoutRunnable, 8000);
    
    com.google.android.gms.location.LocationRequest locationRequest = 
        com.google.android.gms.location.LocationRequest.create();
    locationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);
    locationRequest.setInterval(5000); // 5 seconds
    locationRequest.setFastestInterval(2000); // 2 seconds
    locationRequest.setNumUpdates(1);
    
    fusedLocationClient.requestLocationUpdates(locationRequest, 
        new com.google.android.gms.location.LocationCallback() {
            @Override
            public void onLocationResult(com.google.android.gms.location.LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    android.location.Location loc = locationResult.getLastLocation();
                    
                    Log.d(TAG, "Got location: lat=" + loc.getLatitude() + ", lon=" + loc.getLongitude());
                    
                    // Process this location same as getLastLocation
                    new Thread(() -> {
                        // ... same geocoding logic as getGPSLocation() ...
                    }).start();
                } else {
                    Log.w(TAG, "LocationResult is null or has no location");
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    loadDefaultCity();
                }
            }
        }, null);
}
```

**Linie cu linie:**

#### 1. Timeout (8s vs 10s)
```java
timeoutHandler.postDelayed(timeoutRunnable, 8000);
```
- **8 secunde** (mai scurt ca `getGPSLocation()`)
- Deja am așteptat 10s pentru last location
- Total: până la 18s pentru GPS (10s + 8s)

#### 2. LocationRequest configuration
```java
LocationRequest locationRequest = LocationRequest.create();
locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
locationRequest.setInterval(5000); // 5 seconds
locationRequest.setFastestInterval(2000); // 2 seconds
locationRequest.setNumUpdates(1);
```

| Method | Value | Meaning |
|--------|-------|---------|
| `setPriority()` | `PRIORITY_HIGH_ACCURACY` | Folosește GPS (nu WiFi/cell towers) |
| `setInterval()` | 5000ms | Încearcă să obțină locația la fiecare 5s |
| `setFastestInterval()` | 2000ms | Dacă locația e disponibilă mai repede, acceptă după 2s |
| `setNumUpdates()` | 1 | Oprește după 1 update (nu vrem continuous tracking) |

#### 3. LocationCallback (anonymous class)
```java
new LocationCallback() {
    @Override
    public void onLocationResult(LocationResult locationResult) {
```

**De ce callback nou?**
- `getLastLocation()` returnează `Task<Location>` (one-time)
- `requestLocationUpdates()` returnează updates prin callback (continuous)
- Setăm `setNumUpdates(1)` pentru a primi doar 1 update

#### 4. Same geocoding logic
- Code DUPLICAT (~100 linii)
- În 08_weather, acest cod ar fi într-o metodă separată
- În 09_simple, îl duplicăm (anti-DRY, dar transparent)

**De ce duplicat?**
- ✅ Transparent (vezi exact ce se întâmplă)
- ✅ No abstraction (no metode helper)
- ❌ Code duplication (DRY violation)
- ❌ Harder to maintain (modificări în 2 locuri)

---

### onRequestPermissionsResult()

```java
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == LOCATION_PERMISSION_REQUEST) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted - now get GPS location
            Toast.makeText(this, "Getting your location...", Toast.LENGTH_SHORT).show();
            getGPSLocation();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            // Permission denied, fallback to default city
            loadDefaultCity();
        }
    }
}
```

**Linie cu linie:**

#### Callback pentru permission request
- Apelat când user răspunde la dialog-ul de permisiuni
- `requestCode` - `1001` (LOCATION_PERMISSION_REQUEST)
- `permissions` - array cu permisiunile cerute
- `grantResults` - array cu rezultate (GRANTED/DENIED)

#### Check grant result
```java
if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
```
- `grantResults.length > 0` - safety check (array nu e gol)
- `grantResults[0]` - rezultatul pentru prima permisiune (ACCESS_FINE_LOCATION)

#### Success path
```java
Toast.makeText(this, "Getting your location...", Toast.LENGTH_SHORT).show();
getGPSLocation();
```
- Informează user
- Continuă cu GPS flow

#### Failure path
```java
Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
loadDefaultCity();
```
- Informează user
- Fallback la oraș default

**User flow:**
```
App starts
    ↓
onCreate()
    ↓
requestGPSLocation()
    ↓
[Permission not granted]
    ↓
Dialog: "Allow SimpleWeather to access location?"
    ↓
[User taps "Allow"]
    ↓
onRequestPermissionsResult() called
    ↓
getGPSLocation()
    ↓
... GPS flow continues ...
```

---

## API Call (OkHttp inline, NO WeatherAPI class)

### loadWeather()

```java
private void loadWeather(String city) {
    loadingView.setVisibility(View.VISIBLE);
    recyclerView.setVisibility(View.GONE);
    
    new Thread(() -> {
        try {
            String json = fetchWeatherFromAPI(city);
            List<DailyWeather> dailyList = parseWeatherJSON(json);
            
            runOnUiThread(() -> {
                loadingView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                
                adapter = new WeatherAdapter(dailyList);
                recyclerView.setAdapter(adapter);
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading weather", e);
            runOnUiThread(() -> {
                loadingView.setText("Error: " + e.getMessage());
                Toast.makeText(this, "Failed to load weather", Toast.LENGTH_SHORT).show();
            });
        }
    }).start();
}
```

**Linie cu linie:**

#### 1. Show loading
```java
loadingView.setVisibility(View.VISIBLE);
recyclerView.setVisibility(View.GONE);
```
- Hide RecyclerView (poate avea date vechi)
- Show "Loading..." TextView

#### 2. Background thread
```java
new Thread(() -> {
```
- Network calls TREBUIE pe background thread
- `new Thread()` - simplu, direct (no AsyncTask, no Coroutines)

#### 3. Fetch + Parse
```java
String json = fetchWeatherFromAPI(city);
List<DailyWeather> dailyList = parseWeatherJSON(json);
```
- **Blocking** calls (sync)
- Throws Exception dacă API fails

#### 4. Success - update UI
```java
runOnUiThread(() -> {
    loadingView.setVisibility(View.GONE);
    recyclerView.setVisibility(View.VISIBLE);
    
    adapter = new WeatherAdapter(dailyList);
    recyclerView.setAdapter(adapter);
});
```
- Switch back la Main Thread
- Hide loading, show RecyclerView
- Create new adapter cu date noi
- Set adapter (RecyclerView se refreshează automat)

#### 5. Error handling
```java
} catch (Exception e) {
    Log.e(TAG, "Error loading weather", e);
    runOnUiThread(() -> {
        loadingView.setText("Error: " + e.getMessage());
        Toast.makeText(this, "Failed to load weather", Toast.LENGTH_SHORT).show();
    });
}
```
- Catch ALL exceptions (API, parsing, etc.)
- Show error în loading TextView
- Show Toast

---

### fetchWeatherFromAPI()

```java
private String fetchWeatherFromAPI(String cityName) throws Exception {
    OkHttpClient client = new OkHttpClient();
    
    String apiKey = readAPIKey();
    
    if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your_api_key_here")) {
        throw new Exception("API key not configured in assets/api_key.json");
    }
    
    String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString());
    String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + encodedCity + 
                 "&appid=" + apiKey + "&units=metric";
    
    Log.d(TAG, "Fetching weather for: " + cityName);
    Log.d(TAG, "API URL: " + url.replace(apiKey, "***"));
    
    Request request = new Request.Builder().url(url).get().build();
    
    try (Response response = client.newCall(request).execute()) {
        int code = response.code();
        String body = response.body() != null ? response.body().string() : "";
        
        Log.d(TAG, "API Response code: " + code);
        
        if (code == 429) {
            Log.e(TAG, "Rate limit exceeded. Response: " + body);
            throw new IOException("API Error: 429 - Too many requests. Wait a few minutes.");
        } else if (code == 401) {
            Log.e(TAG, "Unauthorized. Check API key. Response: " + body);
            throw new IOException("API Error: 401 - Invalid API key");
        } else if (!response.isSuccessful()) {
            Log.e(TAG, "API Error " + code + ": " + body);
            throw new IOException("API Error: " + code + " - " + body);
        }
        
        Log.d(TAG, "API Response received successfully");
        return body;
    }
}
```

**Linie cu linie:**

#### 1. OkHttp client
```java
OkHttpClient client = new OkHttpClient();
```
- **OkHttp** - HTTP client modern (de Square)
- Alternative: HttpURLConnection (deprecated), Volley, Retrofit
- NO reuse - creăm client nou la fiecare call
  - În 08_weather, client-ul e refolosit (Singleton)
  - În 09_simple, prefer simplitate (recreate)

#### 2. Read API key
```java
String apiKey = readAPIKey();

if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your_api_key_here")) {
    throw new Exception("API key not configured in assets/api_key.json");
}
```
- Citește din `assets/api_key.json`
- Validează (nu e null, nu e empty, nu e placeholder)
- Throw exception dacă invalid

#### 3. URL encoding
```java
String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString());
String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + encodedCity + 
             "&appid=" + apiKey + "&units=metric";
```

**URLEncoder:**
- Convertește spații → `%20`
- Convertește caractere speciale → encoded
- Exemplu: "New York" → "New%20York"

**URL format:**
- `forecast` - 5-day forecast endpoint
- `q=` - query parameter (city name)
- `appid=` - API key
- `units=metric` - Celsius (default e Kelvin)

#### 4. Logging (debug)
```java
Log.d(TAG, "Fetching weather for: " + cityName);
Log.d(TAG, "API URL: " + url.replace(apiKey, "***"));
```
- **IMPORTANT:** `replace(apiKey, "***")` - NU logăm API key-ul complet (security)
- Logcat: `API URL: https://...&appid=***&units=metric`

#### 5. Build request
```java
Request request = new Request.Builder().url(url).get().build();
```
- Builder pattern (OkHttp)
- `.url()` - set URL
- `.get()` - HTTP GET (default)
- `.build()` - construct Request object

#### 6. Execute request (try-with-resources)
```java
try (Response response = client.newCall(request).execute()) {
```
- **try-with-resources** - auto-close Response (prevent memory leaks)
- `newCall(request)` - create Call object
- `.execute()` - **BLOCKING** - wait for response

#### 7. Extract response
```java
int code = response.code();
String body = response.body() != null ? response.body().string() : "";

Log.d(TAG, "API Response code: " + code);
```
- `code()` - HTTP status code (200, 404, 429, etc.)
- `body().string()` - read entire body as String
  - **IMPORTANT:** `body().string()` can only be called ONCE
  - After calling, body is consumed
- Null check pentru body (safety)

#### 8. Error handling (detailed)

**429 - Rate Limit:**
```java
if (code == 429) {
    Log.e(TAG, "Rate limit exceeded. Response: " + body);
    throw new IOException("API Error: 429 - Too many requests. Wait a few minutes.");
}
```
- OpenWeatherMap free tier: 60 calls/minute
- Dacă depășim, API returnează 429
- User-friendly message

**401 - Unauthorized:**
```java
} else if (code == 401) {
    Log.e(TAG, "Unauthorized. Check API key. Response: " + body);
    throw new IOException("API Error: 401 - Invalid API key");
}
```
- API key invalid sau expirat
- Debug hint: "Check API key"

**Other errors:**
```java
} else if (!response.isSuccessful()) {
    Log.e(TAG, "API Error " + code + ": " + body);
    throw new IOException("API Error: " + code + " - " + body);
}
```
- `isSuccessful()` - true dacă code în range 200-299
- Catch-all pentru alte erori (404, 500, etc.)

#### 9. Success
```java
Log.d(TAG, "API Response received successfully");
return body;
```
- Return JSON string
- Va fi pasat la `parseWeatherJSON()`

---

### readAPIKey()

```java
private String readAPIKey() throws Exception {
    try {
        InputStream is = getAssets().open("api_key.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        int bytesRead = is.read(buffer);
        is.close();
        
        if (bytesRead != size) {
            Log.w(TAG, "Warning: Expected to read " + size + " bytes but read " + bytesRead);
        }
        
        String content = new String(buffer, 0, bytesRead, "UTF-8");
        if (content.length() > 0 && content.charAt(0) == '\uFEFF') {
            content = content.substring(1);
        }
        
        Log.d(TAG, "Raw API key JSON: " + content);
        
        org.json.JSONObject apiKeyObject = new org.json.JSONObject(content);
        String apiKey = apiKeyObject.getString("apiKey");
        
        if (apiKey != null) {
            apiKey = apiKey.trim();
        }
        
        Log.d(TAG, "API Key length: " + apiKey.length());
        Log.d(TAG, "API Key (first 4 chars): " + (apiKey.length() > 4 ? apiKey.substring(0, 4) + "..." : apiKey));
        
        return apiKey;
    } catch (Exception e) {
        Log.e(TAG, "Failed to read API key", e);
        throw new Exception("Failed to read API key from assets");
    }
}
```

**Linie cu linie:**

#### 1. Read from assets
```java
InputStream is = getAssets().open("api_key.json");
int size = is.available();
byte[] buffer = new byte[size];
int bytesRead = is.read(buffer);
is.close();
```
- `getAssets()` - access la `app/src/main/assets/` folder
- `open()` - deschide fișier ca InputStream
- `available()` - size în bytes
- `read(buffer)` - citește tot conținutul în buffer
- `close()` - închide stream (prevent leaks)

#### 2. Safety check
```java
if (bytesRead != size) {
    Log.w(TAG, "Warning: Expected to read " + size + " bytes but read " + bytesRead);
}
```
- Verifică dacă am citit tot fișierul
- Edge case: fișier corrupt/incomplet

#### 3. Convert to String
```java
String content = new String(buffer, 0, bytesRead, "UTF-8");
```
- Bytes → String cu encoding UTF-8
- `bytesRead` - use actual bytes read (nu `size`)

#### 4. Remove BOM (Byte Order Mark)
```java
if (content.length() > 0 && content.charAt(0) == '\uFEFF') {
    content = content.substring(1);
}
```
- **BOM** - invisible character la începutul fișierelor UTF-8 (Windows)
- `\uFEFF` - UTF-8 BOM
- Remove dacă există (altfel JSON parsing fails)

#### 5. Parse JSON (org.json, NOT Gson!)
```java
org.json.JSONObject apiKeyObject = new org.json.JSONObject(content);
String apiKey = apiKeyObject.getString("apiKey");
```

**CRITICAL:** Folosim `org.json.JSONObject` (Android built-in), NU Gson!

**De ce?**
- În versiunea inițială, folosisem Gson
- Problema: Gson citește fișierul diferit
- Rezultat: API key avea whitespace/newlines invisible
- Consecință: API returnează 429 (rate limit) din cauza key invalid
- Soluție: Switch la org.json.JSONObject

**api_key.json format:**
```json
{
  "apiKey": "your_actual_key_here"
}
```

#### 6. Trim whitespace
```java
if (apiKey != null) {
    apiKey = apiKey.trim();
}
```
- Remove spaces, newlines, tabs
- Safety measure

#### 7. Debug logging
```java
Log.d(TAG, "API Key length: " + apiKey.length());
Log.d(TAG, "API Key (first 4 chars): " + (apiKey.length() > 4 ? apiKey.substring(0, 4) + "..." : apiKey));
```
- Logăm lungimea (should be 32 chars pentru OpenWeatherMap)
- Logăm primele 4 chars (pentru debug, fără a expune key-ul complet)

---

*[Continuare cu JSON Parsing, Model Classes, Charts, RecyclerView Adapter și Comparație în următorul răspuns...]*
