# LocationManager.java — Documentație detaliată a codului sursă

Acest document prezintă codul complet al clasei `LocationManager.java` împărțit în fragmente logice, fiecare precedat de explicații detaliate în limba română. Documentația este destinată programatorilor cu cunoștințe de Java dar cu experiență limitată în programarea Android.

## Context general

`LocationManager` este clasa utilitar responsabilă pentru **persistența (salvarea) și gestionarea orașelor** utilizate în aplicația meteo. Aceasta oferă o interfață simplă pentru lucrul cu orașele salvate, ascunzând complexitatea SharedPreferences și gestionând migrarea datelor legacy.

### Responsabilități principale:

1. **Salvare persistentă** - Orașe salvate rămân disponibile după închiderea aplicației
2. **Ordine garantată** - Orașele sunt păstrate în ordinea în care au fost adăugate
3. **Migrare automată** - Convertește date vechi (StringSet neordonat) în format nou (JSON Array ordonat)
4. **Locație curentă GPS** - Gestionează orașul detectat prin GPS
5. **Verificare duplicate** - Previne adăugarea aceluiași oraș de două ori
6. **API simplu** - 7 metode publice pentru toate operațiunile necesare

### De ce este necesară această clasă?

| Problemă | Soluție LocationManager |
|----------|-------------------------|
| **Persistență** | SharedPreferences (storage permanent) |
| **Ordine** | JSONArray (ordinea de inserție garantată) |
| **Duplicate** | Verificare `contains()` înainte de adăugare |
| **Acces global** | Instanță partajată prin Context |
| **Migrare date** | Detectare StringSet legacy + conversie automată |
| **GPS location** | Câmp separat pentru oraș curent |

### Utilizare în aplicație:

```
MainActivity
    ↓
LocationManager.getSavedLocations()
    ↓
Spinner (dropdown listă orașe)
    ↓
User selectează oraș
    ↓
WeatherAPI.fetchWeather(cityName)
```

```
AddLocationDialog
    ↓
LocationManager.addLocation("Bucharest")
    ↓
SharedPreferences (salvare permanentă)
    ↓
MainActivity.onLocationAdded() (callback)
    ↓
Spinner.notifyDataSetChanged() (refresh UI)
```

## 1. Declararea pachetului

```java
package ro.makore.akrilki_08.util;
```

**Explicație:**
- Sub-pachetul `util` grupează toate clasele utilitare
- Clasele util sunt reutilizabile și nu depind de logica specifică aplicației
- Alte posibile clase util: DateUtils, FormatUtils, NetworkUtils

## 2. Import-uri pentru Android

```java
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
```

**Linie cu linie:**
- **`Context`** - Necesar pentru accesarea SharedPreferences
- **`SharedPreferences`** - Mecanism Android pentru salvare key-value persistentă
- **`Log`** - Pentru logging-ul operațiunilor (debug)

### Ce este SharedPreferences?

SharedPreferences este un mecanism Android pentru salvarea datelor simple în format key-value:

```
Fișier XML pe disc:
/data/data/ro.makore.akrilki_08/shared_prefs/weather_locations.xml

<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
    <string name="saved_locations">["Bucharest","Cluj-Napoca","Timișoara"]</string>
    <string name="current_location">Bucharest</string>
</map>
```

**Caracteristici SharedPreferences:**
- Salvare automată pe disc (persistentă)
- Acces rapid (cache în memorie)
- Thread-safe (accesibil din multiple thread-uri)
- Limitare: doar tipuri simple (String, int, boolean, float, long, Set<String>)

## 3. Import-uri pentru JSON

```java
import org.json.JSONArray;
import org.json.JSONException;
```

**Linie cu linie:**
- **`JSONArray`** - Pentru reprezentarea listei de orașe ca array JSON
- **`JSONException`** - Excepție pentru erori de parsare JSON

### De ce JSON și nu StringSet?

| Aspect | StringSet (legacy) | JSONArray (actual) |
|--------|--------------------|--------------------|
| **Ordine** | NU este garantată | DA, garantată |
| **Tip Android** | Set<String> | String (JSON) |
| **Modificare** | Creează copie nouă | Modificare directă |
| **Performance** | Rapid pentru verificări | Puțin mai lent |
| **Compatibilitate** | Android 3.0+ | Toate versiunile |

**Problema StringSet:**
```java
Set<String> cities = new HashSet<>();
cities.add("Bucharest");
cities.add("Cluj");
cities.add("Timișoara");

// La citire, ordinea poate fi:
// ["Cluj", "Timișoara", "Bucharest"] - DIFERIT de ordinea adăugării!
```

**Soluția JSONArray:**
```java
JSONArray cities = new JSONArray();
cities.put("Bucharest");
cities.put("Cluj");
cities.put("Timișoara");

// La citire, ordinea este ÎNTOTDEAUNA:
// ["Bucharest", "Cluj", "Timișoara"] - IDENTICĂ cu ordinea adăugării!
```

## 4. Import-uri pentru colecții Java

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
```

**Linie cu linie:**
- **`ArrayList`** - Implementare de listă ordonată (utilizat pentru orașe)
- **`List`** - Interfață pentru liste (tip returnat de metode)
- **`Set`** - Utilizat doar pentru citirea datelor legacy (StringSet vechi)

## 5. Declararea clasei și constantelor

### Declararea clasei

```java
public class LocationManager {
```

**Explicație:**
- Clasă publică, accesibilă din orice pachet
- Nu extinde nicio clasă (standalone utility)
- Nu este singleton (o instanță nouă pentru fiecare Context/Activity)

### Constante pentru SharedPreferences

```java
    private static final String PREFS_NAME = "weather_locations";
    private static final String KEY_LOCATIONS = "saved_locations";
    private static final String KEY_CURRENT_LOCATION = "current_location";
    private static final String TAG = "LocationManager";
```

**Linie cu linie:**
- **`PREFS_NAME`** - Numele fișierului SharedPreferences
  - Creează fișierul: `weather_locations.xml`
  - Mai multe aplicații pot avea SharedPreferences diferite
- **`KEY_LOCATIONS`** - Cheia pentru lista orașelor salvate
  - În XML: `<string name="saved_locations">...</string>`
- **`KEY_CURRENT_LOCATION`** - Cheia pentru orașul curent GPS
  - În XML: `<string name="current_location">Bucharest</string>`
- **`TAG`** - Tag pentru logging în logcat
- **`private static final`** - Constante private, partajate de toate instanțele

### De ce constante și nu string-uri hardcoded?

```java
// RĂU - Hardcoded strings (risc de typo):
prefs.getString("saved_locations", null);
prefs.getString("savd_locations", null);  // TYPO! Nu se detectează la compilare

// BINE - Constante (typo detectat la compilare):
prefs.getString(KEY_LOCATIONS, null);
prefs.getString(KEY_LOCATINS, null);  // EROARE DE COMPILARE!
```

### Câmpul SharedPreferences

```java
    private final SharedPreferences prefs;
```

**Explicație:**
- Referință către instanța SharedPreferences
- `final` - Nu se poate reatribui după inițializare în constructor
- `private` - Accesibil doar din interiorul clasei

## 6. Constructorul - Inițializare SharedPreferences

```java
    public LocationManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
```

**Linie cu linie:**
- **`public`** - Constructor public, apelabil din orice clasă
- **`Context context`** - Necesar pentru accesarea SharedPreferences
- `context.getSharedPreferences()` - Obține sau creează SharedPreferences
- **`PREFS_NAME`** - "weather_locations" (numele fișierului XML)
- **`Context.MODE_PRIVATE`** - Fișierul este accesibil DOAR de această aplicație

### Moduri de acces SharedPreferences:

| Mod | Descriere | Utilizare |
|-----|-----------|-----------|
| **MODE_PRIVATE** | Doar această aplicație | ✓ Recomandat (default) |
| MODE_WORLD_READABLE | Alte aplicații pot citi | ✗ Deprecated (risc securitate) |
| MODE_WORLD_WRITEABLE | Alte aplicații pot scrie | ✗ Deprecated (risc securitate) |
| MODE_MULTI_PROCESS | Acces din procese multiple | ✗ Deprecated (buggy) |

### Exemplu de utilizare în MainActivity:

```java
public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Creăm instanță LocationManager
        locationManager = new LocationManager(this);
        
        // Folosim metodele
        List<String> cities = locationManager.getSavedLocations();
    }
}
```

## 7. Metoda getSavedLocations() - Citirea orașelor salvate

Aceasta este metoda cea mai complexă, gestionând citirea, migrarea datelor legacy și parsarea JSON.

### Documentația metodei

```java
    /**
     * Get all saved locations in insertion order.
     */
    public List<String> getSavedLocations() {
```

**Explicație:**
- Comentariu Javadoc pentru documentare
- **Return**: `List<String>` - Listă de orașe în ordinea inserției
- **Garanție**: Ordinea este întotdeauna aceeași (primul adăugat = primul în listă)

### Inițializare listă rezultat

```java
        List<String> list = new ArrayList<>();
        try {
```

**Linie cu linie:**
- Creăm o listă goală care va fi returnată
- `try` - Începem bloc pentru gestionarea excepțiilor JSON

### Citirea datelor JSON

```java
            String json = prefs.getString(KEY_LOCATIONS, null);
            if (json == null) return list;
```

**Linie cu linie:**
- Citim string-ul JSON din SharedPreferences
- **`KEY_LOCATIONS`** - "saved_locations"
- **`null`** - Valoare default dacă cheia nu există (prima rulare)
- Dacă nu există date salvate, returnăm listă goală

**Exemplu de valoare JSON:**
```json
["Bucharest", "Cluj-Napoca", "Timișoara", "Brașov"]
```

### Parsarea JSON Array

```java
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                String v = arr.optString(i, null);
                if (v != null) list.add(v);
            }
            return list;
```

**Linie cu linie:**
- `new JSONArray(json)` - Parsează string-ul în JSONArray
- Iterăm prin toate elementele din array (0 până la length-1)
- **`arr.optString(i, null)`** - Obține string-ul de la index i
  - **opt**String = optional (returnează null dacă nu e string, în loc să arunce excepție)
  - Al doilea parametru (null) = valoarea default
- Verificăm că string-ul nu e null înainte de a-l adăuga
- Returnăm lista completă

### Diferența între `get()` și `opt()`:

```java
// getString() - Aruncă excepție dacă elementul nu e string:
String city = arr.getString(0);  // JSONException dacă arr[0] nu e string

// optString() - Returnează default dacă elementul nu e string:
String city = arr.optString(0, "default");  // Returnează "default" dacă arr[0] nu e string
```

### Gestionarea datelor legacy (StringSet)

```java
        } catch (ClassCastException e) {
            // Legacy data may have been saved as a StringSet (unordered). Read it and migrate.
            try {
```

**Explicație:**
- `catch (ClassCastException e)` - Prinde eroarea când datele sunt StringSet în loc de String
- Comentariul explică: datele vechi pot fi StringSet (format neordonat)
- **Migrare automată**: Convertim StringSet → JSONArray

### Citirea StringSet legacy

```java
                Set<String> set = prefs.getStringSet(KEY_LOCATIONS, null);
                if (set == null) return list;
                list.addAll(set);
```

**Linie cu linie:**
- Citim datele ca StringSet (format vechi)
- Dacă nu există, returnăm listă goală
- `addAll(set)` - Adăugăm toate orașele din Set în List
- **ATENȚIE**: Ordinea nu este garantată pentru date legacy!

### Migrarea în format nou

```java
                // Persist migrated ordered list as JSON string for future runs
                saveLocations(list);
```

**Explicație:**
- Apelăm `saveLocations()` pentru a salva în format nou (JSON)
- **Efect**: La următoarea rulare, datele vor fi citite ca JSON (ordonat)
- **Migrare one-time**: Se întâmplă o singură dată, la prima rulare după update

### Gestionarea erorilor de migrare

```java
            } catch (Exception ex) {
                Log.w(TAG, "Failed to read legacy saved locations set", ex);
            }
            return list;
        }
```

**Linie cu linie:**
- Prindem orice altă excepție din procesul de migrare
- **`Log.w()`** - Warning (nu e critică, dar e importantă)
- Returnăm lista (poate fi goală dacă migrarea a eșuat)

### Gestionarea erorilor de parsare JSON

```java
        } catch (JSONException e) {
            Log.w(TAG, "Failed to parse saved locations JSON", e);
            return list;
        }
    }
```

**Linie cu linie:**
- Prindem erori de parsare JSON (JSON corupt/invalid)
- Logăm warning-ul pentru debugging
- Returnăm listă goală (safe fallback)

### Flow-ul complet al metodei:

```
getSavedLocations()
    ↓
Citește string din SharedPreferences
    ↓
String există? ──NO──→ Return listă goală
    ↓ YES
Parsează ca JSONArray?
    ↓ YES                    ↓ NO (ClassCastException)
Extract orașe              Citește ca StringSet (legacy)
    ↓                           ↓
Return listă               Convertește în listă
                                ↓
                           Salvează ca JSON (migrare)
                                ↓
                           Return listă
```

## 8. Metoda saveLocations() - Salvarea orașelor

### Declararea metodei private

```java
    private void saveLocations(List<String> locations) {
```

**Explicație:**
- **`private`** - Metodă internă, folosită doar de `addLocation()` și `removeLocation()`
- **Parametru**: `List<String> locations` - Lista completă de orașe de salvat

### Crearea JSON Array

```java
        JSONArray arr = new JSONArray();
        for (String s : locations) arr.put(s);
```

**Linie cu linie:**
- Creăm un JSONArray gol
- Iterăm prin toate orașele din listă
- `arr.put(s)` - Adăugăm fiecare oraș în array
- **Ordine**: Ordinea din listă este păstrată în JSON

**Exemplu:**
```java
List<String> cities = Arrays.asList("Bucharest", "Cluj", "Timișoara");
// locations = ["Bucharest", "Cluj", "Timișoara"]

JSONArray arr = new JSONArray();
for (String s : cities) arr.put(s);
// arr = ["Bucharest", "Cluj", "Timișoara"]
```

### Salvarea în SharedPreferences

```java
        prefs.edit().putString(KEY_LOCATIONS, arr.toString()).apply();
    }
```

**Linie cu linie:**
- **`prefs.edit()`** - Începe o sesiune de editare
- **`putString(KEY_LOCATIONS, arr.toString())`** - Salvează JSON-ul ca String
  - `arr.toString()` convertește JSONArray în String: `["Bucharest","Cluj"]`
- **`apply()`** - Aplică modificările asincron (în background)

### Diferența între `apply()` și `commit()`:

| Aspect | apply() | commit() |
|--------|---------|----------|
| **Execuție** | Asincron (background) | Sincron (blochează) |
| **Return** | void | boolean (success/fail) |
| **Viteză** | Mai rapid (UI responsive) | Mai lent (UI freeze) |
| **Recomandat** | ✓ DA (în 99% cazuri) | Doar dacă trebuie să știi dacă a avut succes |

**Exemplu diferență:**

```java
// apply() - Recomandat (nu blochează UI):
prefs.edit().putString("key", "value").apply();
// Cod continuă imediat, salvarea se face în background

// commit() - Blochează thread-ul curent:
boolean success = prefs.edit().putString("key", "value").commit();
// Cod așteaptă până când salvarea se termină
if (success) {
    Log.d(TAG, "Saved successfully");
}
```

## 9. Metoda addLocation() - Adăugarea unui oraș

### Documentația și signatura metodei

```java
    /**
     * Add a new location (preserves order, append if not present)
     */
    public void addLocation(String location) {
```

**Explicație:**
- Comentariu explică: păstrează ordinea, adaugă la final doar dacă nu există
- **Parametru**: `String location` - Numele orașului de adăugat
- **Return**: void (nu returnează nimic)

### Validarea input-ului

```java
        if (location == null) return;
```

**Explicație:**
- **Guard clause**: Ieșim imediat dacă parametrul e null
- Previne NullPointerException în operațiunile următoare

### Citirea listei curente

```java
        List<String> list = getSavedLocations();
```

**Explicație:**
- Obținem lista completă de orașe salvate
- Poate fi goală (prima adăugare) sau cu orașe existente

### Verificare duplicate și adăugare

```java
        if (!list.contains(location)) {
            list.add(location);
            saveLocations(list);
            Log.d(TAG, "Added location: " + location);
        }
    }
```

**Linie cu linie:**
- **`!list.contains(location)`** - Verificăm dacă orașul NU există deja
- `list.add(location)` - Adăugăm orașul la finalul listei
- `saveLocations(list)` - Salvăm lista completă în SharedPreferences
- `Log.d()` - Debug log pentru confirmare

### Flow vizual:

```
addLocation("Bucharest")
    ↓
location != null? ──NO──→ Return (guard clause)
    ↓ YES
getSavedLocations()
    ↓
Lista curentă: ["Cluj", "Timișoara"]
    ↓
"Bucharest" în listă? ──YES──→ Return (duplicate, nu adăugăm)
    ↓ NO
Adaugă "Bucharest"
    ↓
Listă nouă: ["Cluj", "Timișoara", "Bucharest"]
    ↓
saveLocations() → SharedPreferences
    ↓
Log: "Added location: Bucharest"
```

### Exemplu de utilizare în AddLocationDialog:

```java
// În AddLocationDialog.addLocation():
private void addLocation(String locationName) {
    locationManager.addLocation(locationName);  // Salvare persistentă
    Toast.makeText(getContext(), "Location added", Toast.LENGTH_SHORT).show();
    if (listener != null) {
        listener.onLocationAdded(locationName);  // Notificare MainActivity
    }
    dismiss();
}
```

## 10. Metoda removeLocation() - Ștergerea unui oraș

### Documentația și signatura metodei

```java
    /**
     * Remove a location
     */
    public void removeLocation(String location) {
```

**Explicație:**
- **Parametru**: `String location` - Numele orașului de șters
- **Return**: void

### Citirea, ștergerea și salvarea

```java
        List<String> list = getSavedLocations();
        if (list.remove(location)) {
            saveLocations(list);
            Log.d(TAG, "Removed location: " + location);
        }
    }
```

**Linie cu linie:**
- Obținem lista curentă de orașe
- **`list.remove(location)`** - Șterge orașul dacă există
  - Returnează `true` dacă orașul a fost găsit și șters
  - Returnează `false` dacă orașul nu exista în listă
- Salvăm lista actualizată doar dacă ștergerea a avut succes
- Logăm confirmarea

### Comportamentul metodei `remove()`:

```java
List<String> cities = new ArrayList<>(Arrays.asList("Bucharest", "Cluj", "Timișoara"));

// Ștergere existentă:
boolean removed = cities.remove("Cluj");  
// removed = true
// cities = ["Bucharest", "Timișoara"]

// Ștergere inexistentă:
boolean removed2 = cities.remove("Paris");
// removed2 = false
// cities = ["Bucharest", "Timișoara"] (neschimbată)
```

### Flow vizual:

```
removeLocation("Cluj")
    ↓
getSavedLocations()
    ↓
Listă curentă: ["Bucharest", "Cluj", "Timișoara"]
    ↓
remove("Cluj") → success?
    ↓ YES                    ↓ NO
Listă nouă:            Return (nu există)
["Bucharest", "Timișoara"]
    ↓
saveLocations()
    ↓
Log: "Removed location: Cluj"
```

### Exemplu de utilizare în MainActivity:

```java
// Long-click pe un oraș în Spinner pentru ștergere:
spinnerLocation.setOnItemLongClickListener((parent, view, position, id) -> {
    String cityToRemove = locationList.get(position);
    
    // Confirmare utilizator
    new AlertDialog.Builder(this)
        .setTitle("Delete Location")
        .setMessage("Delete " + cityToRemove + "?")
        .setPositiveButton("Delete", (dialog, which) -> {
            locationManager.removeLocation(cityToRemove);  // Ștergere
            locationList.remove(position);  // Update listă UI
            adapter.notifyDataSetChanged();  // Refresh Spinner
        })
        .setNegativeButton("Cancel", null)
        .show();
    
    return true;
});
```

## 11. Metoda getCurrentLocation() - Obținere oraș curent GPS

### Documentația și signatura metodei

```java
    /**
     * Get current location (GPS-based)
     */
    public String getCurrentLocation() {
        return prefs.getString(KEY_CURRENT_LOCATION, null);
    }
```

**Linie cu linie:**
- Citește orașul curent din SharedPreferences
- **`KEY_CURRENT_LOCATION`** - "current_location"
- **`null`** - Default dacă nu a fost setat niciodată
- **Return**: String (numele orașului) sau null

### Scop și utilizare:

```java
// În MainActivity - Marcarea orașului curent în Spinner:
String currentCity = locationManager.getCurrentLocation();

// În LocationSpinnerAdapter - Afișare "(current)":
@Override
public View getView(int position, View convertView, ViewGroup parent) {
    // ... setup view ...
    
    String city = getItem(position);
    String currentCity = locationManager.getCurrentLocation();
    
    if (city.equals(currentCity)) {
        textView.setText(city + " (current)");  // Marcare vizuală
        textView.setTypeface(null, Typeface.BOLD);
    } else {
        textView.setText(city);
    }
    
    return convertView;
}
```

### Exemplu valori în SharedPreferences:

```xml
<!-- weather_locations.xml -->
<map>
    <string name="saved_locations">["Bucharest","Cluj-Napoca","Timișoara"]</string>
    <string name="current_location">Bucharest</string>
</map>
```

## 12. Metoda setCurrentLocation() - Setare oraș curent GPS

### Documentația și signatura metodei

```java
    /**
     * Set current location (GPS-based)
     */
    public void setCurrentLocation(String location) {
        prefs.edit().putString(KEY_CURRENT_LOCATION, location).apply();
        Log.d(TAG, "Set current location: " + location);
    }
```

**Linie cu linie:**
- Salvează orașul curent în SharedPreferences
- `apply()` - Salvare asincronă
- Logăm confirmarea

### Exemplu de utilizare cu GPS:

```java
// În MainActivity - Când se obține locația GPS:
private void onLocationReceived(Location gpsLocation) {
    // Geocoding invers: Coordonate GPS → Nume oraș
    Geocoder geocoder = new Geocoder(this);
    try {
        List<Address> addresses = geocoder.getFromLocation(
            gpsLocation.getLatitude(), 
            gpsLocation.getLongitude(), 
            1
        );
        
        if (!addresses.isEmpty()) {
            String cityName = addresses.get(0).getLocality();  // "Bucharest"
            
            // Salvăm orașul curent
            locationManager.setCurrentLocation(cityName);
            
            // Actualizăm UI
            spinnerAdapter.notifyDataSetChanged();  // Refresh "(current)"
        }
    } catch (IOException e) {
        Log.e(TAG, "Geocoding failed", e);
    }
}
```

### Flow GPS → Oraș curent:

```
User pornește aplicația
    ↓
MainActivity.onCreate()
    ↓
Request GPS location
    ↓
GPS Provider → Coordonate (44.4268, 26.1025)
    ↓
Geocoder (reverse geocoding)
    ↓
Coordonate → "Bucharest"
    ↓
setCurrentLocation("Bucharest")
    ↓
SharedPreferences salvat
    ↓
Spinner refresh → "Bucharest (current)"
```

## 13. Metoda hasLocation() - Verificare existență oraș

### Documentația și signatura metodei

```java
    /**
     * Check if location exists in saved locations
     */
    public boolean hasLocation(String location) {
        return getSavedLocations().contains(location);
    }
}
```

**Linie cu linie:**
- Verifică dacă orașul există în lista salvată
- **Return**: `true` dacă orașul există, `false` altfel
- Implementare simplă: obține lista și verifică cu `contains()`

### Exemplu de utilizare în AddLocationDialog:

```java
// În AddLocationDialog.checkLocationAvailability():
private void checkLocationAvailability(String locationName) {
    isChecking = true;
    // ... set UI state ...
    
    // Verificare duplicat LOCAL (rapid, fără request HTTP)
    if (locationManager.hasLocation(locationName)) {
        locationStatus.setText("Location already exists");
        locationStatus.setTextColor(Color.RED);
        btnAdd.setEnabled(false);
        isChecking = false;
        
        if (pendingAdd) {
            pendingAdd = false;  // Anulează adăugarea
        }
        return;  // Nu continuăm cu verificarea online
    }
    
    // Dacă nu e duplicat, continuăm cu verificarea online...
    new Thread(() -> {
        // WeatherAPI.fetchWeather()...
    }).start();
}
```

### Avantaje verificare locală înainte de verificare online:

| Aspect | Verificare locală | Verificare online |
|--------|-------------------|-------------------|
| **Viteză** | Instantanee (<1ms) | 100-500ms (HTTP request) |
| **Cost** | Zero | Bandwidth, API quota |
| **Necesită internet** | NU | DA |
| **Erori posibile** | Zero | Timeout, no connection, API down |

**Optimizare**: Verificăm local înainte de online → salvăm timp și resurse!

---

## 14. Diagrama relațiilor între componente

```
┌─────────────────────────────────────────────────────────────┐
│                        MainActivity                          │
│  ┌────────────────────────────────────────────────────┐     │
│  │ onCreate()                                          │     │
│  │   locationManager = new LocationManager(this)      │     │
│  │   List<String> cities = locationManager            │     │
│  │                        .getSavedLocations()         │     │
│  │   setupSpinner(cities)                              │     │
│  └────────────────────────────────────────────────────┘     │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│                    LocationManager                           │
│  ┌────────────────────────────────────────────────────┐     │
│  │ prefs: SharedPreferences                            │     │
│  │                                                      │     │
│  │ getSavedLocations() → List<String>                 │     │
│  │ addLocation(String)                                 │     │
│  │ removeLocation(String)                              │     │
│  │ getCurrentLocation() → String                       │     │
│  │ setCurrentLocation(String)                          │     │
│  │ hasLocation(String) → boolean                       │     │
│  └────────────────────────────────────────────────────┘     │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│                   SharedPreferences                          │
│                 (weather_locations.xml)                      │
│  ┌────────────────────────────────────────────────────┐     │
│  │ saved_locations:                                    │     │
│  │   ["Bucharest", "Cluj-Napoca", "Timișoara"]        │     │
│  │                                                      │     │
│  │ current_location:                                   │     │
│  │   "Bucharest"                                       │     │
│  └────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                   AddLocationDialog                          │
│  ┌────────────────────────────────────────────────────┐     │
│  │ addLocation()                                       │     │
│  │   if (!locationManager.hasLocation(city)) {        │     │
│  │       // Verificare online...                      │     │
│  │       locationManager.addLocation(city)            │     │
│  │       listener.onLocationAdded(city)               │     │
│  │   }                                                 │     │
│  └────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

---

## 15. Flow complet: De la adăugare la afișare

```
┌──────────────────────────────────────────────────────────────┐
│ User click pe "+" în MainActivity                             │
└──────────────────┬───────────────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────────────┐
│ AddLocationDialog.show()                                      │
│   - User tastează "Bucharest"                                 │
│   - Debouncing (600ms delay)                                  │
│   - checkLocationAvailability("Bucharest")                    │
└──────────────────┬───────────────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────────────┐
│ Verificare duplicat LOCAL                                     │
│   locationManager.hasLocation("Bucharest")                    │
│     → getSavedLocations().contains("Bucharest")               │
│     → false (nu există)                                       │
└──────────────────┬───────────────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────────────┐
│ Verificare online (Thread nou)                                │
│   WeatherAPI.fetchWeather(context, "Bucharest")               │
│     → Success (oraș valid)                                    │
└──────────────────┬───────────────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────────────┐
│ User click pe "Add"                                           │
│   addLocation("Bucharest")                                    │
└──────────────────┬───────────────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────────────┐
│ LocationManager.addLocation("Bucharest")                      │
│   list = getSavedLocations()                                  │
│     → ["Cluj-Napoca", "Timișoara"]                            │
│   list.add("Bucharest")                                       │
│     → ["Cluj-Napoca", "Timișoara", "Bucharest"]               │
│   saveLocations(list)                                         │
└──────────────────┬───────────────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────────────┐
│ saveLocations(list)                                           │
│   JSONArray arr = new JSONArray()                             │
│   for (city : list) arr.put(city)                             │
│   arr.toString() → '["Cluj-Napoca","Timișoara","Bucharest"]' │
│   prefs.edit()                                                │
│     .putString("saved_locations", arr.toString())             │
│     .apply()                                                  │
└──────────────────┬───────────────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────────────┐
│ SharedPreferences (disc)                                      │
│   /data/data/.../shared_prefs/weather_locations.xml           │
│   <string name="saved_locations">                             │
│     ["Cluj-Napoca","Timișoara","Bucharest"]                   │
│   </string>                                                   │
└──────────────────┬───────────────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────────────┐
│ Callback la MainActivity                                      │
│   listener.onLocationAdded("Bucharest")                       │
│     → locationList.add("Bucharest")                           │
│     → spinnerAdapter.notifyDataSetChanged()                   │
└──────────────────┬───────────────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────────────┐
│ UI Update                                                     │
│   Spinner acum afișează:                                      │
│     - Cluj-Napoca                                             │
│     - Timișoara                                               │
│     - Bucharest (NOU!)                                        │
└──────────────────────────────────────────────────────────────┘
```

---

## 16. Migrarea datelor: StringSet → JSONArray

### Problema cu datele legacy:

În versiunile vechi ale aplicației, orașele erau salvate ca `Set<String>`:

```java
// Cod vechi (înainte de migrare):
Set<String> cities = new HashSet<>();
cities.add("Bucharest");
cities.add("Cluj");
cities.add("Timișoara");

prefs.edit().putStringSet("saved_locations", cities).apply();

// SharedPreferences XML (ordine ALEATORIE):
<set name="saved_locations">
    <string>Timișoara</string>
    <string>Bucharest</string>
    <string>Cluj</string>
</set>
```

**Problemă**: La fiecare citire, ordinea poate fi diferită!

### Soluția: Migrare automată în getSavedLocations()

```java
// În getSavedLocations():
try {
    String json = prefs.getString(KEY_LOCATIONS, null);
    // Parsare JSON...
} catch (ClassCastException e) {
    // Datele sunt StringSet (vechi), nu String (nou)
    
    // Citim StringSet-ul vechi:
    Set<String> set = prefs.getStringSet(KEY_LOCATIONS, null);
    list.addAll(set);  // Convertim în List
    
    // MIGRARE: Salvăm în format nou (JSON):
    saveLocations(list);
    
    // La următoarea rulare, datele vor fi citite ca JSON!
}
```

### Timeline-ul migrării:

```
Versiune 1.0 (vechi)
    ↓
User adaugă orașe → Salvare ca StringSet
    ↓
SharedPreferences: <set>...</set>
    ↓
──────── UPDATE LA VERSIUNE 2.0 ────────
    ↓
Prima rulare v2.0:
    ↓
getSavedLocations() → ClassCastException
    ↓
Citire StringSet vechi → Conversie în List
    ↓
saveLocations(list) → Salvare ca JSON
    ↓
SharedPreferences: <string>["Buc","Cluj"]</string>
    ↓
A doua rulare v2.0:
    ↓
getSavedLocations() → Parsare JSON (SUCCESS!)
    ↓
Ordinea este acum garantată!
```

### Avantajele migrării automate:

✓ **Transparentă** - Utilizatorul nu observă nimic  
✓ **One-time** - Se întâmplă o singură dată  
✓ **Fallback safe** - Dacă eșuează, returnează listă goală  
✓ **Backward compatible** - Nu necesită ștergere date vechi manual

---

## 17. Best practices și îmbunătățiri posibile

### Îmbunătățiri pentru robustețe:

#### 1. Singleton pattern pentru instanță globală

```java
public class LocationManager {
    private static LocationManager instance;
    private final SharedPreferences prefs;
    
    private LocationManager(Context context) {
        prefs = context.getApplicationContext()
                      .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized LocationManager getInstance(Context context) {
        if (instance == null) {
            instance = new LocationManager(context);
        }
        return instance;
    }
}

// Utilizare:
LocationManager manager = LocationManager.getInstance(this);
// Același manager în toată aplicația!
```

**Avantaje Singleton:**
- O singură instanță în toată aplicația
- Acces global simplu
- Nu se recrează la fiecare Activity

#### 2. LiveData / Observable pentru notificări automate

```java
public class LocationManager {
    private final MutableLiveData<List<String>> locationsLiveData = new MutableLiveData<>();
    
    public LiveData<List<String>> getLocationsLiveData() {
        return locationsLiveData;
    }
    
    public void addLocation(String location) {
        if (location == null) return;
        List<String> list = getSavedLocations();
        if (!list.contains(location)) {
            list.add(location);
            saveLocations(list);
            locationsLiveData.postValue(list);  // Notificare automată!
        }
    }
}

// În MainActivity:
locationManager.getLocationsLiveData().observe(this, cities -> {
    // Update UI automat când lista se schimbă!
    spinnerAdapter.clear();
    spinnerAdapter.addAll(cities);
    spinnerAdapter.notifyDataSetChanged();
});
```

#### 3. Validare și sanitizare input

```java
public void addLocation(String location) {
    if (location == null || location.trim().isEmpty()) {
        Log.w(TAG, "Cannot add null or empty location");
        return;
    }
    
    // Sanitizare: trim și capitalize
    String sanitized = location.trim();
    sanitized = sanitized.substring(0, 1).toUpperCase() + 
                sanitized.substring(1).toLowerCase();
    
    List<String> list = getSavedLocations();
    if (!list.contains(sanitized)) {
        list.add(sanitized);
        saveLocations(list);
        Log.d(TAG, "Added location: " + sanitized);
    }
}

// "  bucharest  " → "Bucharest"
// "CLUJ-napoca" → "Cluj-napoca"
```

#### 4. Limite pentru numărul de orașe

```java
private static final int MAX_LOCATIONS = 20;

public boolean addLocation(String location) {
    if (location == null) return false;
    List<String> list = getSavedLocations();
    
    if (list.size() >= MAX_LOCATIONS) {
        Log.w(TAG, "Maximum number of locations reached: " + MAX_LOCATIONS);
        return false;  // Prea multe orașe
    }
    
    if (!list.contains(location)) {
        list.add(location);
        saveLocations(list);
        return true;
    }
    return false;  // Duplicat
}

// În AddLocationDialog:
boolean added = locationManager.addLocation(cityName);
if (!added) {
    Toast.makeText(context, "Cannot add more cities (max 20)", Toast.LENGTH_SHORT).show();
}
```

#### 5. Backup și restore

```java
public String exportLocations() {
    JSONArray arr = new JSONArray();
    for (String s : getSavedLocations()) arr.put(s);
    return arr.toString();
}

public void importLocations(String json) {
    try {
        JSONArray arr = new JSONArray(json);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            String v = arr.optString(i, null);
            if (v != null) list.add(v);
        }
        saveLocations(list);
        Log.d(TAG, "Imported " + list.size() + " locations");
    } catch (JSONException e) {
        Log.e(TAG, "Failed to import locations", e);
    }
}

// Utilizare - Export:
String backup = locationManager.exportLocations();
// Salvare în fișier sau cloud...

// Utilizare - Import:
String backup = // Citire din fișier sau cloud...
locationManager.importLocations(backup);
```

#### 6. Listener pentru schimbări

```java
public interface LocationChangeListener {
    void onLocationAdded(String location);
    void onLocationRemoved(String location);
    void onLocationsChanged(List<String> locations);
}

public class LocationManager {
    private final List<LocationChangeListener> listeners = new ArrayList<>();
    
    public void addListener(LocationChangeListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(LocationChangeListener listener) {
        listeners.remove(listener);
    }
    
    public void addLocation(String location) {
        if (location == null) return;
        List<String> list = getSavedLocations();
        if (!list.contains(location)) {
            list.add(location);
            saveLocations(list);
            
            // Notificare listeners
            for (LocationChangeListener l : listeners) {
                l.onLocationAdded(location);
                l.onLocationsChanged(list);
            }
        }
    }
}

// În MainActivity:
locationManager.addListener(new LocationChangeListener() {
    @Override
    public void onLocationAdded(String location) {
        Toast.makeText(MainActivity.this, "Added: " + location, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onLocationRemoved(String location) {
        Toast.makeText(MainActivity.this, "Removed: " + location, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onLocationsChanged(List<String> locations) {
        updateSpinner(locations);
    }
});
```

#### 7. Caching pentru performanță

```java
public class LocationManager {
    private List<String> cachedLocations = null;
    
    public List<String> getSavedLocations() {
        if (cachedLocations != null) {
            return new ArrayList<>(cachedLocations);  // Return copy
        }
        
        // Citire din SharedPreferences (doar prima dată)...
        List<String> list = // ... parsare JSON ...
        
        cachedLocations = list;  // Cache
        return new ArrayList<>(list);
    }
    
    private void saveLocations(List<String> locations) {
        JSONArray arr = new JSONArray();
        for (String s : locations) arr.put(s);
        prefs.edit().putString(KEY_LOCATIONS, arr.toString()).apply();
        
        cachedLocations = new ArrayList<>(locations);  // Update cache
    }
}

// Prima apelare: Citește din disc (lent)
// Apelări următoare: Returnează din cache (foarte rapid)
```

---

## 18. Comparație: LocationManager vs alte soluții

### LocationManager (SharedPreferences) vs Room Database:

| Aspect | SharedPreferences | Room Database |
|--------|-------------------|---------------|
| **Complexitate** | Simplu (3 linii cod) | Complex (Entity, DAO, Database) |
| **Setup** | Zero | Gradle dependencies, @Entity, @Dao |
| **Viteză** | Foarte rapid | Rapid (dar mai lent) |
| **Query-uri** | Nu suportă | SQL complet |
| **Relații** | Nu | Da (1-to-many, etc.) |
| **Migrări** | Manuale | Automate (cu Migration) |
| **Ideal pentru** | **Date simple, liste mici** | Date complexe, relații |

**Concluzie**: Pentru o listă simplă de orașe (10-20 item-uri), SharedPreferences este alegerea perfectă!

### LocationManager vs Firebase Firestore:

| Aspect | SharedPreferences | Firestore |
|--------|-------------------|-----------|
| **Storage** | Local (device) | Cloud |
| **Sync** | Nu | Da (toate device-urile) |
| **Offline** | Întotdeauna | Cache local |
| **Autentificare** | Nu | Da (Firebase Auth) |
| **Cost** | Zero | Pay-per-read/write |
| **Latență** | <1ms | 50-200ms |
| **Ideal pentru** | **Date locale private** | Date partajate între device-uri |

**Concluzie**: Pentru listă personală de orașe (nu partajată), SharedPreferences este suficient!

---

## Rezumat

Această clasă oferă management complet pentru orașele salvate în aplicație:

### **Scop principal**
- Persistența orașelor salvate (supraviețuiesc închiderii aplicației)
- Gestionarea orașului curent (detectat prin GPS)
- API simplu pentru operațiuni CRUD (Create, Read, Update, Delete)

### **Funcționalități cheie:**
1. **getSavedLocations()** - Citire orașe salvate (ordonat)
2. **addLocation()** - Adăugare oraș nou (fără duplicate)
3. **removeLocation()** - Ștergere oraș
4. **getCurrentLocation()** - Obținere oraș curent GPS
5. **setCurrentLocation()** - Setare oraș curent GPS
6. **hasLocation()** - Verificare existență oraș
7. **saveLocations()** - Salvare listă (private, internă)

### **Tehnologii utilizate:**
- **SharedPreferences** - Persistență key-value în XML
- **JSONArray** - Reprezentare ordonată a listei de orașe
- **Migrare automată** - StringSet (legacy) → JSONArray (nou)
- **apply() asincron** - Salvare fără blocare UI

### **Pattern-uri implementate:**
- **Guard clauses** - Validare early return (null check)
- **Migration pattern** - Conversie automată date legacy
- **Safe fallback** - Returnare listă goală la erori
- **Logging** - Debug info pentru toate operațiunile

### **Avantaje design:**
✓ **Simplu** - API clar cu 7 metode publice  
✓ **Robust** - Gestionare excepții și migrare automată  
✓ **Eficient** - SharedPreferences (cache în memorie)  
✓ **Ordonat** - JSONArray păstrează ordinea de inserție  
✓ **Safe** - Verificări null, duplicate, erori parsare  

### **Flow-uri principale:**

**Adăugare oraș:**
```
AddLocationDialog → addLocation("Bucharest")
    ↓
getSavedLocations() → Lista curentă
    ↓
Verificare duplicate
    ↓
list.add() → Listă actualizată
    ↓
saveLocations() → JSONArray → SharedPreferences
    ↓
Callback → MainActivity → Update Spinner
```

**Citire orașe:**
```
MainActivity.onCreate()
    ↓
getSavedLocations()
    ↓
prefs.getString("saved_locations") → JSON string
    ↓
JSONArray parse → ["Buc", "Cluj", "Tim"]
    ↓
Return List<String> → Spinner
```

**Migrare automată:**
```
Update aplicație (StringSet → JSON)
    ↓
Prima rulare: getSavedLocations()
    ↓
ClassCastException (nu e String, e StringSet)
    ↓
Citire StringSet legacy
    ↓
saveLocations() → Conversie în JSON
    ↓
A doua rulare: Citire JSON (SUCCESS!)
```

### **Îmbunătățiri posibile:**
- Singleton pattern (instanță unică globală)
- LiveData/Observable (update UI automat)
- Validare și sanitizare input (trim, capitalize)
- Limite (max 20 orașe)
- Backup/restore (export/import JSON)
- Listeners pentru schimbări
- Caching (evită citiri repetate)

### **Cazuri de utilizare:**
- **MainActivity** - Încărcare orașe în Spinner
- **AddLocationDialog** - Adăugare oraș nou (cu verificare duplicate)
- **LocationSpinnerAdapter** - Marcare oraș curent "(current)"
- **GPS Service** - Setare oraș curent la schimbare locație

Clasa `LocationManager` este un exemplu excelent de **utility class** bine proiectată: simplă, robustă, eficientă și ușor de utilizat!
