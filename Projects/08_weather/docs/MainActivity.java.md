# MainActivity.java — Documentație detaliată a codului sursă

Acest document prezintă codul complet al clasei `MainActivity.java` împărțit în fragmente logice, fiecare precedat de explicații detaliate în limba română. Documentația este destinată programatorilor cu cunoștințe de Java dar cu experiență limitată în programarea Android.

## 1. Declararea pachetului

În Java și Android, fiecare clasă aparține unui pachet (package). Această linie declară că clasa `MainActivity` face parte din pachetul aplicației noastre, `ro.makore.akrilki_08`. Pachetul reprezintă namespace-ul aplicației și asigură că nu există conflicte de nume cu alte clase.

```java
package ro.makore.akrilki_08;
```

## 2. Import-uri din framework-ul Android

Aceste import-uri aduc în clasă componente fundamentale din Android SDK:

- **`Manifest`** - Conține constante pentru permisiuni (de ex. accesul la locație GPS)
- **`PackageManager`** - Gestionează informații despre pachete și verifică permisiuni
- **`Bundle`** - Container pentru date care poate fi transmis între componente Android
- **`ArrayAdapter`** - Adaptor simplu pentru a afișa liste de date în UI
- **`ImageButton`** - Widget buton cu imagine
- **`Spinner`** - Widget dropdown (listă derulantă)
- **`Toast`** - Mesaje scurte pop-up pentru utilizator

```java
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
```

## 3. Import-uri AndroidX și RecyclerView

AndroidX este biblioteca modernă de suport Android care înlocuiește vechile "support libraries":

- **`AppCompatActivity`** - Clasa de bază pentru activități moderne care suportă backward compatibility
- **`Toolbar`** - Bara de instrumente (action bar modernă) din partea de sus a ecranului
- **`ActivityCompat`** - Utilități pentru a gestiona permisiuni runtime în mod compatibil
- **`ContextCompat`** - Ajută la accesarea resurselor în mod compatibil între versiuni
- **`LinearLayoutManager`** - Aranjează elementele RecyclerView în linie (vertical sau orizontal)
- **`RecyclerView`** - Widget performant pentru afișarea listelor mari de date

```java
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
```

## 4. Alte import-uri Android utile

Clase suplimentare pentru logging, gestiunea view-urilor și widget-uri:

- **`Log`** - Sistem de logging pentru Android (pentru debug și monitorizare)
- **`View`** - Clasa de bază pentru toate componentele UI
- **`ProgressBar`** - Widget pentru afișarea progresului (roată de încărcare)
- **`TextView`** - Widget pentru afișarea textului
- **`AdapterView`** - Clasa de bază pentru view-uri care folosesc adaptoare (Spinner, ListView)

```java
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView;
```

## 5. Import-uri specifice aplicației

Acestea sunt clasele custom create pentru aplicația noastră de vreme:

- **`WeatherItem`** - Model de date pentru un singur element de vreme (o oră specifică)
- **`DailyWeatherItem`** - Model de date pentru vremea dintr-o zi întreagă
- **`WeatherParser`** - Clasă care parsează (transformă) JSON-ul primit de la API în obiecte Java
- **`WeatherAPI`** - Clasă care face request-uri HTTP către API-ul de vreme
- **`WeatherAdapter`** - Adaptor pentru RecyclerView (elementele orare)
- **`DailyWeatherAdapter`** - Adaptor pentru RecyclerView (elementele zilnice)
- **`LocationSpinnerAdapter`** - Adaptor custom pentru Spinner-ul de locații
- **`LocationManager`** - Gestionează salvarea și încărcarea locațiilor utilizatorului
- **`LocationService`** - Obține locația GPS curentă
- **`AddLocationDialog`** - Dialog pentru adăugarea unei locații noi

```java
import ro.makore.akrilki_08.model.WeatherItem;
import ro.makore.akrilki_08.model.DailyWeatherItem;
import ro.makore.akrilki_08.parser.WeatherParser;
import ro.makore.akrilki_08.api.WeatherAPI;
import ro.makore.akrilki_08.adapter.WeatherAdapter;
import ro.makore.akrilki_08.adapter.DailyWeatherAdapter;
import ro.makore.akrilki_08.adapter.LocationSpinnerAdapter;
import ro.makore.akrilki_08.util.LocationManager;
import ro.makore.akrilki_08.util.LocationService;
import ro.makore.akrilki_08.dialog.AddLocationDialog;
```

## 6. Import-uri pentru biblioteci terțe și Java standard

Biblioteci externe și clase din Java standard library:

- **`AndroidThreeTen`** - Biblioteca pentru lucrul cu date și timp în Android (backport al Java 8 Time API)
- **`FloatingActionButton`** - Buton rotund plutitor (FAB) din Material Design
- **`IOException`** - Excepție pentru erori de input/output (network, fișiere)
- **`ArrayList`** - Implementare de listă dinamică din Java
- **`List`** - Interfața pentru liste în Java

```java
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
```

## 7. Declararea clasei și câmpurile membre

### Declararea clasei

Clasa `MainActivity` extinde `AppCompatActivity`, ceea ce înseamnă că moștenește toate funcționalitățile unei activități Android moderne. O activitate reprezintă un singur ecran cu interfață utilizator în Android.

```java
public class MainActivity extends AppCompatActivity {
```

### Constante

Această constantă este un cod unic folosit pentru a identifica request-ul de permisiune de locație. Când Android returnează rezultatul permisiunii, folosim acest cod pentru a ști despre ce permisiune este vorba.

```java
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
```

### Componente UI (interfață utilizator)

Acestea sunt referințe către componentele vizuale din layout:

- **`recyclerView`** - Lista principală care afișează vremea zilnică
- **`dailyWeatherAdapter`** - Adaptorul care populează RecyclerView cu date
- **`progressBar`** - Roata de încărcare afișată când se descarcă datele
- **`loadingText`** - Textul "Loading..." sau mesajele de eroare
- **`locationSpinner`** - Dropdown-ul pentru selectarea orașului

```java
    private RecyclerView recyclerView;
    private DailyWeatherAdapter dailyWeatherAdapter;
    private ProgressBar progressBar; // ProgressBar for loading indicator
    private TextView loadingText; // TextView for loading message
    private Spinner locationSpinner;
```

### Servicii și manageri

Obiecte care gestionează funcționalități specifice:

- **`locationManager`** - Gestionează salvarea și citirea locațiilor din SharedPreferences
- **`locationService`** - Obține locația GPS curentă a dispozitivului
- **`locationAdapter`** - Adaptor custom pentru Spinner-ul de locații

```java
    private LocationManager locationManager;
    private LocationService locationService;
    private LocationSpinnerAdapter locationAdapter;
```

### Variabile de stare

Acestea retin informații despre starea curentă a aplicației:

- **`currentSelectedLocation`** - Orașul selectat momentan în spinner
- **`currentGpsLocation`** - Orașul obținut din coordonatele GPS

```java
    private String currentSelectedLocation;
    private String currentGpsLocation; // GPS-based current location
```

### Orașul implicit

Dacă nu există locații salvate și GPS nu funcționează, aplicația folosește București ca oraș implicit.

```java
    // Default city for weather forecast
    private static final String DEFAULT_CITY = "Bucharest";
```

## 8. Metoda onCreate() - Inițializarea activității

Metoda `onCreate()` este prima metodă apelată când activitatea este creată. Aici se face toată configurarea inițială a interfeței și componentelor.

### Apelarea constructorului părinte și inițializare

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
```

**Linie cu linie:**
- `@Override` - Adnotare care indică că suprascriem o metodă din clasa părinte
- `super.onCreate(savedInstanceState)` - Apelează metoda `onCreate()` din `AppCompatActivity` pentru a face inițializările standard

### Inițializarea bibliotecii de timp și setarea layout-ului

```java
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);
```

**Linie cu linie:**
- `AndroidThreeTen.init(this)` - Inițializează biblioteca ThreeTenABP pentru lucrul cu date și timp
- `setContentView(R.layout.activity_main)` - Încarcă și afișează interfața definită în fișierul XML `activity_main.xml`

### Inițializarea serviciilor de locație

```java
        // Initialize location services
        locationManager = new LocationManager(this);
        locationService = new LocationService(this);
```

**Linie cu linie:**
- Se creează o instanță nouă a `LocationManager` care va gestiona locațiile salvate
- Se creează o instanță nouă a `LocationService` care va obține locația GPS
- `this` reprezintă Context-ul curent (MainActivity)

### Configurarea Toolbar-ului

```java
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
```

**Linie cu linie:**
- `findViewById(R.id.toolbar)` - Găsește componenta Toolbar din layout după ID-ul ei
- `setSupportActionBar(toolbar)` - Setează toolbar-ul găsit ca ActionBar al activității

### Configurarea Spinner-ului de locații

```java
        // Setup location spinner
        locationSpinner = findViewById(R.id.location_spinner);
        setupLocationSpinner();
```

**Linie cu linie:**
- `findViewById(R.id.location_spinner)` - Găsește Spinner-ul din layout
- `setupLocationSpinner()` - Apelează metoda care configurează Spinner-ul (populare cu date, listeners)

### Configurarea butonului de adăugare locație

```java
        // Setup add location button
        ImageButton btnAddLocation = findViewById(R.id.btn_add_location);
        btnAddLocation.setOnClickListener(v -> showAddLocationDialog());
```

**Linie cu linie:**
- `findViewById(R.id.btn_add_location)` - Găsește butonul de adăugare din layout
- `setOnClickListener(...)` - Setează ce se întâmplă când butonul este apăsat
- `v -> showAddLocationDialog()` - Lambda expression (funcție anonimă) care apelează metoda de afișare dialog

### Configurarea butonului FAB de ieșire

```java
        FloatingActionButton fabQuit = findViewById(R.id.fab_quit);
        fabQuit.setOnClickListener(v -> finishAffinity());
```

**Linie cu linie:**
- `findViewById(R.id.fab_quit)` - Găsește butonul plutitor de ieșire
- `finishAffinity()` - Metodă care închide activitatea curentă și toate activitățile părinte din stack

### Configurarea butonului FAB de refresh

```java
        FloatingActionButton fabRefresh = findViewById(R.id.fab_refresh);
        fabRefresh.setOnClickListener(v -> refreshWeatherData());
```

**Linie cu linie:**
- `findViewById(R.id.fab_refresh)` - Găsește butonul plutitor de reîmprospătare
- `refreshWeatherData()` - Apelează metoda care descarcă din nou datele de vreme

### Configurarea RecyclerView

```java
        recyclerView = findViewById(R.id.weatherRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
```

**Linie cu linie:**
- `findViewById(R.id.weatherRecyclerView)` - Găsește RecyclerView-ul principal
- `setLayoutManager(new LinearLayoutManager(this))` - Setează managerul de layout care aranjează elementele vertical în listă

### Configurarea componentelor de încărcare

```java
        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);
```

**Linie cu linie:**
- Se găsesc și se salvează referințele către ProgressBar și TextView-ul de încărcare
- Aceste componente vor fi afișate/ascunse în timpul descărcării datelor

### Obținerea locației GPS și încărcarea datelor

```java
        // Get GPS location and update spinner
        getGpsLocation();

        // Load weather for default/selected location
        refreshWeatherData();
    }
```

**Linie cu linie:**
- `getGpsLocation()` - Inițiază procesul de obținere a locației GPS curente
- `refreshWeatherData()` - Descarcă datele de vreme pentru locația selectată sau implicită
- Aceste metode rulează asincron (în fundal) pentru a nu bloca interfața

## 9. Metoda setupLocationSpinner() - Configurarea listei de orașe

Această metodă configurează Spinner-ul (dropdown-ul) care permite utilizatorului să selecteze orașul pentru care vrea să vadă vremea.

### Încărcarea locațiilor salvate și a locației GPS

```java
    private void setupLocationSpinner() {
        List<String> locations = locationManager.getSavedLocations();

        // Try to get previously stored GPS/current location
        currentGpsLocation = locationManager.getCurrentLocation();
```

**Linie cu linie:**
- `getSavedLocations()` - Returnează o listă de String-uri cu toate orașele salvate de utilizator
- `getCurrentLocation()` - Încearcă să obțină orașul GPS salvat anterior în SharedPreferences
- Această valoare poate fi `null` dacă nu a fost salvată încă nicio locație GPS

### Adăugarea orașului implicit dacă lista este goală

```java
        // If no saved locations, add default
        if (locations.isEmpty()) {
            locationManager.addLocation(DEFAULT_CITY);
            locations = locationManager.getSavedLocations();
        }
```

**Linie cu linie:**
- `isEmpty()` - Verifică dacă lista de locații este goală
- Dacă da, se adaugă orașul implicit ("Bucharest") pentru ca utilizatorul să aibă cel puțin o opțiune
- Lista se reîncarcă pentru a include orașul nou adăugat

### Crearea și setarea adaptorului

```java
        // Create adapter with current GPS location
        locationAdapter = new LocationSpinnerAdapter(this, locations, currentGpsLocation);
        locationSpinner.setAdapter(locationAdapter);
```

**Linie cu linie:**
- Se creează un adaptor custom care primește:
  - `this` - Context-ul curent
  - `locations` - Lista de orașe
  - `currentGpsLocation` - Locația GPS (pentru a o marca vizual în listă)
- `setAdapter()` - Conectează adaptorul la Spinner, astfel încât acesta să afișeze orașele

### Determinarea selecției inițiale

```java
        // Choose selection: prefer current GPS if present, otherwise first item
        int selectionIndex = 0;
        if (currentGpsLocation != null && locations.contains(currentGpsLocation)) {
            selectionIndex = locations.indexOf(currentGpsLocation);
        }
        currentSelectedLocation = locations.get(selectionIndex);
        locationSpinner.setSelection(selectionIndex);
```

**Linie cu linie:**
- `selectionIndex = 0` - Implicit se selectează primul element (index 0)
- Dacă există o locație GPS și ea este în lista salvată, găsim poziția ei cu `indexOf()`
- `get(selectionIndex)` - Obține orașul de la acea poziție
- `setSelection(selectionIndex)` - Setează Spinner-ul să afișeze orașul selectat

### Configurarea listener-ului pentru selecție

```java
        // Handle location selection
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
```

**Explicație:**
- `setOnItemSelectedListener()` - Înregistrează un listener care va fi notificat când utilizatorul selectează un alt oraș
- Se creează o instanță anonimă de `OnItemSelectedListener` care trebuie să implementeze două metode

### Metoda apelată la selecția unui element

```java
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<String> currentLocations = locationManager.getSavedLocations();
                if (position < currentLocations.size()) {
                    String selectedLocation = currentLocations.get(position);
                    if (!selectedLocation.equals(currentSelectedLocation)) {
                        currentSelectedLocation = selectedLocation;
                        refreshWeatherData();
                    }
                }
            }
```

**Linie cu linie:**
- `onItemSelected()` - Metodă apelată când utilizatorul selectează un element din Spinner
- `position` - Indexul elementului selectat (0, 1, 2, etc.)
- Se reîncarcă lista de locații pentru a fi siguri că este actualizată
- Se verifică dacă poziția este validă (în limitele listei)
- Se obține orașul selectat de la acea poziție
- Se verifică dacă orașul selectat este diferit de cel curent (pentru a evita refresh-uri inutile)
- Dacă da, se actualizează selecția și se reîmprospătează datele de vreme

### Metoda apelată când nu este selectat nimic

```java
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
```

**Explicație:**
- `onNothingSelected()` - Metodă apelată când selecția este ștearsă (rar în cazul unui Spinner)
- În cazul nostru nu facem nimic

## 10. Metoda getGpsLocation() - Obținerea locației GPS

Această metodă obține locația GPS curentă a utilizatorului și o convertește într-un nume de oraș. Este un proces în mai mulți pași care necesită permisiuni.

### Verificarea permisiunilor de locație

```java
    private void getGpsLocation() {
        // Check location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
```

**Linie cu linie:**
- `checkSelfPermission()` - Verifică dacă aplicația are o anumită permisiune
- `ACCESS_FINE_LOCATION` - Permisiune pentru locația precisă (GPS)
- `ACCESS_COARSE_LOCATION` - Permisiune pentru locația aproximativă (rețea)
- `!= PackageManager.PERMISSION_GRANTED` - Verifică dacă permisiunea NU a fost acordată
- Operatorul `&&` - Verifică dacă AMBELE permisiuni lipsesc

### Cererea permisiunilor dacă nu există

```java
            // Request permission
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
```

**Linie cu linie:**
- `requestPermissions()` - Afișează dialog-ul sistem pentru a cere permisiuni utilizatorului
- Primul parametru: Context-ul curent
- Al doilea parametru: Array de String-uri cu permisiunile cerute
- Al treilea parametru: Cod de identificare pentru a recunoaște răspunsul
- `return` - Oprește execuția metodei aici; vom continua în `onRequestPermissionsResult()` după ce utilizatorul răspunde

### Obținerea numelui orașului din coordonate GPS

```java
        // Get GPS location
        locationService.getCurrentLocationName(new LocationService.LocationCallback() {
```

**Explicație:**
- `getCurrentLocationName()` - Metodă asincronă care:
  1. Obține coordonatele GPS (latitudine, longitudine)
  2. Face geocoding reverse (convertește coordonatele în adresă)
  3. Extrage numele orașului
- Se trimite un callback (interfață cu două metode) care va fi apelat când operația se termină

### Callback apelat când locația este primită cu succes

```java
            @Override
            public void onLocationReceived(String cityName) {
                currentGpsLocation = cityName;
                locationManager.setCurrentLocation(cityName);
```

**Linie cu linie:**
- `onLocationReceived()` - Metodă apelată când orașul a fost obținut cu succes
- `cityName` - Numele orașului primit (de ex. "București")
- Se salvează în variabila `currentGpsLocation`
- `setCurrentLocation()` - Salvează orașul în SharedPreferences pentru utilizare ulterioară

### Actualizarea UI-ului pe thread-ul principal

```java
                // Update spinner to include current location if not already there
                runOnUiThread(() -> {
                    List<String> locations = locationManager.getSavedLocations();
```

**Explicație:**
- `runOnUiThread()` - Execută codul în thread-ul principal (UI thread)
- Necesar deoarece callback-ul vine dintr-un thread de fundal
- Doar thread-ul principal poate modifica UI-ul
- Lambda expression `() -> { ... }` conține codul de executat

### Adăugarea locației GPS în listă dacă lipsește

```java
                    if (!locations.contains(cityName)) {
                        // Add current location to the list (it will appear with "(current)")
                        locationManager.addLocation(cityName);
                        locations = locationManager.getSavedLocations(); // Refresh list
                        // Move current location to the beginning
                        locations.remove(cityName);
                        locations.add(0, cityName);
                    }
```

**Linie cu linie:**
- `contains(cityName)` - Verifică dacă orașul GPS este deja în listă
- Dacă nu este, îl adăugăm cu `addLocation()` (îl salvează în SharedPreferences)
- Reîncărcăm lista pentru a include orașul nou adăugat
- `remove(cityName)` - Îl scoatem din poziția curentă
- `add(0, cityName)` - Îl adăugăm la început (poziția 0) pentru acces rapid

### Actualizarea Spinner-ului cu noua listă

```java
                    locationAdapter = new LocationSpinnerAdapter(MainActivity.this, locations, currentGpsLocation);
                    locationSpinner.setAdapter(locationAdapter);
                    locationSpinner.setSelection(0); // Select current location
                    currentSelectedLocation = cityName;
                    refreshWeatherData();
                });
            }
```

**Linie cu linie:**
- Se creează un adaptor nou cu lista actualizată
- `MainActivity.this` - Referință explicită la Context (necesar în lambda dintr-un callback)
- `setAdapter()` - Înlocuiește adaptorul vechi cu cel nou
- `setSelection(0)` - Selectează primul element (locația GPS)
- Se salvează orașul GPS ca selecție curentă
- `refreshWeatherData()` - Descarcă vremea pentru orașul GPS

### Callback apelat când apare o eroare

```java
            @Override
            public void onLocationError(String error) {
                Log.w("WEATHER08", "GPS location error: " + error);
                // Continue without GPS location
            }
        });
    }
```

**Linie cu linie:**
- `onLocationError()` - Metodă apelată dacă obținerea locației eșuează
- `Log.w()` - Scrie un mesaj de tip warning în logcat
- `"WEATHER08"` - Tag-ul pentru filtrare în logcat
- Aplicația continuă să funcționeze fără locație GPS (va folosi orașul implicit sau unul salvat)

## 11. Metoda onRequestPermissionsResult() - Procesarea răspunsului la cererea de permisiuni

Această metodă este apelată automat de Android când utilizatorul răspunde la dialog-ul de cerere de permisiuni.

### Verificarea și procesarea răspunsului

```java
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getGpsLocation();
            } else {
                Toast.makeText(this, "Location permission denied. GPS location will not be available.", Toast.LENGTH_SHORT).show();
            }
        }
    }
```

**Linie cu linie:**
- `@Override` - Suprascriem metoda din clasa părinte
- `requestCode` - Codul cu care am cerut permisiunea (1001 în cazul nostru)
- `permissions` - Array cu permisiunile cerute
- `grantResults` - Array cu rezultatele (GRANTED sau DENIED pentru fiecare permisiune)
- `super.onRequestPermissionsResult()` - Apelăm metoda din părinte pentru procesare standard
- `if (requestCode == LOCATION_PERMISSION_REQUEST_CODE)` - Verificăm dacă este răspunsul pentru cererea noastră de locație
- `grantResults.length > 0` - Verificăm dacă avem cel puțin un rezultat
- `grantResults[0] == PackageManager.PERMISSION_GRANTED` - Verificăm dacă prima permisiune a fost acordată
- Dacă da, apelăm din nou `getGpsLocation()` care acum va avea permisiunea și va putea obține locația
- Dacă nu, afișăm un Toast (mesaj temporar) informând utilizatorul că GPS-ul nu va fi disponibil

## 12. Metoda showAddLocationDialog() - Afișarea dialog-ului pentru adăugare oraș

Această metodă creează și afișează un dialog care permite utilizatorului să adauge un oraș nou în listă.

### Crearea și configurarea dialog-ului

```java
    private void showAddLocationDialog() {
        AddLocationDialog dialog = new AddLocationDialog(this, locationManager, locationName -> {
```

**Linie cu linie:**
- Se creează o instanță nouă de `AddLocationDialog`
- Primul parametru: `this` - Context-ul curent (MainActivity)
- Al doilea parametru: `locationManager` - Managerul care va salva noul oraș
- Al treilea parametru: Un callback (lambda) care va fi apelat când utilizatorul adaugă un oraș
- `locationName` - Parametrul lambda-ului, va conține numele orașului adăugat

### Actualizarea Spinner-ului cu noul oraș

```java
            // Location added, refresh spinner
            List<String> locations = locationManager.getSavedLocations();
            locationAdapter = new LocationSpinnerAdapter(this, locations, currentGpsLocation);
            locationSpinner.setAdapter(locationAdapter);
```

**Linie cu linie:**
- Când callback-ul este apelat (orașul a fost adăugat), reîncărcăm lista de locații
- Creăm un adaptor nou cu lista actualizată
- Setăm adaptorul nou pe Spinner pentru a afișa și noul oraș

### Selectarea automată a orașului adăugat

```java
            // Select the newly added location
            int position = locations.indexOf(locationName);
            if (position >= 0) {
                locationSpinner.setSelection(position);
                currentSelectedLocation = locationName;
                refreshWeatherData();
            }
        });
```

**Linie cu linie:**
- `indexOf(locationName)` - Găsește poziția orașului nou adăugat în listă
- `if (position >= 0)` - Verifică că orașul a fost găsit (indexOf returnează -1 dacă nu găsește)
- `setSelection(position)` - Selectează orașul nou adăugat în Spinner
- Actualizează variabila de stare cu noul oraș selectat
- `refreshWeatherData()` - Descarcă datele de vreme pentru noul oraș

### Afișarea dialog-ului

```java
        dialog.show();
    }
```

**Explicație:**
- `show()` - Afișează dialog-ul pe ecran
- Dialog-ul va aștepta input de la utilizator (nume oraș) și va apela callback-ul când utilizatorul confirmă

## 13. Metoda refreshWeatherData() - Descărcarea și afișarea datelor meteo

Aceasta este metoda centrală care descarcă datele de vreme de pe internet, le parsează și le afișează în interfață. Rulează într-un thread separat pentru a nu bloca UI-ul.

### Afișarea indicatorilor de încărcare

```java
    private void refreshWeatherData() {
        
        // Show loading indicators
        runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            loadingText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        });
```

**Linie cu linie:**
- `runOnUiThread()` - Asigură că modificările UI se fac pe thread-ul principal
- `setVisibility(View.VISIBLE)` - Face componenta vizibilă
- `setVisibility(View.GONE)` - Ascunde componenta complet (nu ocupă spațiu în layout)
- Se afișează ProgressBar-ul și textul de încărcare
- Se ascunde RecyclerView-ul (lista cu vreme) până când datele sunt gata

### Determinarea orașului pentru care se descarcă vremea

```java
        // Use selected location instead of default
        String cityToFetch = currentSelectedLocation != null ? currentSelectedLocation : DEFAULT_CITY;
```

**Explicație:**
- Operator ternar: `conditie ? valoare_daca_true : valoare_daca_false`
- Dacă `currentSelectedLocation` nu este `null`, folosim acel oraș
- Altfel folosim orașul implicit ("Bucharest")

### Crearea și pornirea thread-ului de fundal

```java
        new Thread(() -> {
            try {
```

**Explicație:**
- `new Thread()` - Creează un thread nou pentru operațiuni de rețea
- Operațiunile de rețea NU pot rula pe thread-ul principal (Android aruncă excepție)
- Lambda expression `() -> { ... }` conține codul care va rula în thread-ul nou
- `try` - Începe un bloc try-catch pentru a prinde excepțiile

### Descărcarea și parsarea datelor JSON

```java
                String jsonResponse = WeatherAPI.fetchWeather(this, cityToFetch);
                List<DailyWeatherItem> dailyWeatherItems = WeatherParser.parseWeatherByDay(jsonResponse);
```

**Linie cu linie:**
- `fetchWeather()` - Face request HTTP către API-ul meteo și returnează răspunsul ca String JSON
- Operațiune blocantă (sincronă) - așteaptă până primește răspunsul complet
- `parseWeatherByDay()` - Transformă String-ul JSON într-o listă de obiecte `DailyWeatherItem`
- Fiecare `DailyWeatherItem` reprezintă vremea pentru o zi întreagă

### Verificarea validității datelor

```java
                int count = dailyWeatherItems.size();
                
                if (count == 0) {
                    throw new Exception("No weather data received");
                }

                Log.d("WEATHER08", "Successfully parsed " + count + " daily weather items");
```

**Linie cu linie:**
- `size()` - Returnează numărul de elemente din listă
- Dacă lista este goală, aruncăm o excepție manuală
- `throw new Exception()` - Creează și aruncă o excepție, care va fi prinsă de blocul `catch`
- `Log.d()` - Scrie un mesaj de debug în logcat cu numărul de zile primite

### Actualizarea UI-ului cu datele primite (cazul de succes)

```java
                // Update UI on the main thread
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
```

**Explicație:**
- Din nou folosim `runOnUiThread()` pentru că suntem în thread-ul de fundal
- Ascundem indicatorii de încărcare (ProgressBar și text)
- Facem RecyclerView-ul vizibil pentru a afișa datele

### Scroll la început și configurarea adaptorului

```java
                    // Scroll to the top (first item)
                    recyclerView.scrollToPosition(0);    
                    if (dailyWeatherAdapter == null) {
                        dailyWeatherAdapter = new DailyWeatherAdapter(MainActivity.this, dailyWeatherItems);
                        recyclerView.setAdapter(dailyWeatherAdapter);
                    } else {
                        dailyWeatherAdapter.updateData(dailyWeatherItems);
                    }
                });
```

**Linie cu linie:**
- `scrollToPosition(0)` - Derulează lista la primul element (sus)
- Verificăm dacă adaptorul există deja
- Dacă este prima dată (`null`), creăm un adaptor nou și îl setăm pe RecyclerView
- `MainActivity.this` - Referință explicită la Context (necesar în lambda)
- Dacă adaptorul există deja, îl actualizăm doar cu date noi folosind `updateData()`
- Acest lucru este mai eficient decât să recreăm adaptorul de fiecare dată

### Gestionarea erorilor de rețea (IOException)

```java
            } catch (IOException e) {
                Log.e("WEATHER08", "Error fetching weather: " + e.getMessage(), e);
                final String errorMessage = e.getMessage() != null ? e.getMessage() : "Network error";
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                    loadingText.setText("Error: " + errorMessage);
                    loadingText.setVisibility(View.VISIBLE);
                });
```

**Linie cu linie:**
- `catch (IOException e)` - Prinde erori de input/output (probleme de rețea, timeout, etc.)
- `Log.e()` - Scrie un mesaj de eroare în logcat
- `e.getMessage()` - Obține mesajul de eroare din excepție
- Operator ternar pentru a avea un mesaj implicit dacă `getMessage()` returnează `null`
- `final` - Necesar pentru a folosi variabila în lambda
- În UI: ascundem ProgressBar-ul, setăm textul de eroare și îl afișăm

### Gestionarea altor tipuri de excepții

```java
            } catch (Exception e) {
                // Handle any other exceptions that might occur
                Log.e("WEATHER08", "Unexpected error", e);
                final String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                    loadingText.setText("Error: " + errorMessage);
                    loadingText.setVisibility(View.VISIBLE);
                });
            }
        }).start();
    }
```

**Linie cu linie:**
- `catch (Exception e)` - Prinde orice altă excepție care nu este IOException
- De exemplu: erori de parsare JSON, erori de conversie, etc.
- Logica de afișare a erorii este identică cu cazul IOException
- `.start()` - Pornește efectiv execuția thread-ului
- IMPORTANT: Fără `.start()`, thread-ul este doar creat dar nu rulează!

### Închiderea clasei

```java
    
}
```

**Explicație:**
- Acoladă închisă care marchează sfârșitul clasei `MainActivity`

---

## Rezumat

Acest fișier implementează activitatea principală a aplicației de vreme. Componentele cheie sunt:

1. **Gestionarea locațiilor** - Permite utilizatorului să selecteze orașe și să adauge altele noi
2. **GPS** - Obține automat locația curentă cu permisiuni runtime
3. **Networking asincron** - Descarcă datele de vreme fără a bloca UI-ul
4. **RecyclerView** - Afișează vremea zilnică într-o listă scrollabilă
5. **Gestionarea erorilor** - Tratează erori de rețea și de permisiuni în mod elegant