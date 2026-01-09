# GeodataFragment.java - Documentație Diferențe față de AddressFragment

## Prezentare

`GeodataFragment` este un fragment **fără hartă** care afișează date brute despre locația curentă a device-ului în **TextViews**. Spre deosebire de `AddressFragment` care folosește Google Maps și geocoding, acest fragment:
- **NU folosește MapView** - interfață text-only
- **Afișează date GPS raw** - latitude, longitude, accuracy, altitude, speed, bearing, provider, time
- **Request permission modern** - folosește ActivityResultLauncher (API nou) în loc de requestPermissions (deprecated)
- **getLastLocation()** - obține ultima locație cunoscută (mai rapid decât location updates)
- **SimpleDateFormat** - formatează timestamp-ul locației
- **NU are lifecycle MapView** - nu necesită sincronizare onStart/onResume/onPause/onStop

**Diferențe cheie față de AddressFragment:**
- Fără Google Maps integration
- Fără Geocoding
- Fără butoane tip hartă (satellite/normal/terrain)
- Request permission în onCreate() cu ActivityResultLauncher
- Afișare date în TextViews în loc de hartă
- Mult mai simplu - fără MapView lifecycle management

## Analiza Diferențelor - Linie cu Linie

### Import SimpleDateFormat

```java
import java.text.SimpleDateFormat;
```

**DIFERENȚĂ:** AddressFragment nu folosește formatare dată.

Importă `SimpleDateFormat` pentru formatarea timestamp-ului locației în format citibil.

---

### Import Log

```java
import android.util.Log;
```

**DIFERENȚĂ:** AddressFragment nu folosește logging explicit.

Importă `Log` pentru logging debug în Logcat.

---

### Import TextView

```java
import android.widget.TextView;
```

**DIFERENȚĂ:** AddressFragment nu folosește TextViews pentru afișare date (folosește MapView).

Importă `TextView` pentru afișarea datelor de locație.

---

### Import Toast

```java
import android.widget.Toast;
```

**DIFERENȚĂ:** AddressFragment nu folosește Toast pentru mesaje user.

Importă `Toast` pentru afișarea mesajelor scurte (permission denied, unable to get location).

---

### Import ActivityResultLauncher

```java
import androidx.activity.result.ActivityResultLauncher;
```

**DIFERENȚĂ:** AddressFragment nu request permission (sau o face diferit).

Importă `ActivityResultLauncher` - mecanism modern pentru gestionarea rezultatelor de la activități și permission-uri.

**Nou în Android:** Înlocuiește `requestPermissions()` deprecated.

---

### Import ActivityResultContracts

```java
import androidx.activity.result.contract.ActivityResultContracts;
```

**DIFERENȚĂ:** AddressFragment nu folosește contracte pentru permissions.

Importă contractele predefinite pentru tipuri comune de requests (permissions, camera, content picker, etc.).

---

### Import Nullable Annotation

```java
import androidx.annotation.Nullable;
```

**DIFERENȚĂ:** AddressFragment nu folosește @Nullable explicit.

Importă adnotarea `@Nullable` pentru parametri care pot fi null.

---

### Import ViewModelProvider

```java
import androidx.lifecycle.ViewModelProvider;
```

**DIFERENȚĂ:** Importat dar **NU folosit** în acest cod (probabil rămas din template).

AddressFragment de asemenea nu folosește ViewModel.

---

### Import Date

```java
import java.util.Date;
```

**DIFERENȚĂ:** AddressFragment nu lucrează cu date.

Importă `Date` pentru conversie timestamp → obiect Date.

---

### Import Locale

```java
import java.util.Locale;
```

**DIFERENȚĂ:** AddressFragment nu folosește Locale.

Importă `Locale` pentru formatarea datei conform setărilor locale ale device-ului.

---

### Import View Binding

```java
import ro.makore.akrilki_05.databinding.FragmentGeodataBinding;
```

**DIFERENȚĂ:** Pachet diferit (`geodata` vs `address`), binding diferit (`FragmentGeodataBinding` vs `FragmentAddressBinding`).

---

### Declararea Clasei

```java
public class GeodataFragment extends Fragment {
```

**DIFERENȚĂ MAJORĂ:** NU implementează `OnMapReadyCallback` (nu are hartă).

AddressFragment: `extends Fragment implements OnMapReadyCallback`

---

### Constantă Permission Request Code

```java
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
```

**DIFERENȚĂ:** AddressFragment nu are constantă pentru request code (probabil nu verifică permissions explicit sau folosește my location button care le gestionează automat).

Definește codul pentru identificarea request-ului de permission.

**Note:** În acest cod, constanta este definită dar **NU este folosită** (ActivityResultLauncher nu necesită request code).

---

### Variabilă Binding

```java
    private FragmentGeodataBinding binding;
```

**SIMILARITATE:** Ambele fragmente au binding, dar tipuri diferite.

---

### Variabilă ActivityResultLauncher

```java
    private ActivityResultLauncher<String> requestPermissionLauncher;
```

**DIFERENȚĂ MAJORĂ:** AddressFragment nu are ActivityResultLauncher.

Declară launcher-ul pentru request de permission modern.

**Tip generic `<String>`:** Tipul input-ului (numele permission-ului, ex: "android.permission.ACCESS_FINE_LOCATION").

---

### Metoda onCreate - Semnătura

```java
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
```

**DIFERENȚĂ MAJORĂ:** GeodataFragment **ARE** metodă `onCreate()`, AddressFragment **NU are** (nu override onCreate).

Metodă de lifecycle apelată când fragmentul este creat (înainte de onCreateView).

**Rol:** Inițializare ActivityResultLauncher care TREBUIE făcută înainte de onCreateView.

---

### Apel Super onCreate

```java
        super.onCreate(savedInstanceState);
```

Apelează implementarea din clasa părinte.

---

### Inițializare Permission Launcher

```java
        requestPermissionLauncher = registerForActivityResult(
```

**DIFERENȚĂ MAJORĂ:** Mecanism modern de request permission.

Înregistrează launcher-ul pentru request de permission.

**registerForActivityResult():** Metodă Fragment/Activity care returnează ActivityResultLauncher.

**Timing:** TREBUIE apelat în onCreate (înainte de fragment STARTED).

---

### Contract Request Permission

```java
                new ActivityResultContracts.RequestPermission(),
```

Specifică contractul: request pentru UN SINGUR permission.

**ActivityResultContracts.RequestPermission():**
- Input: String (numele permission-ului)
- Output: Boolean (granted sau denied)

**Alternative contracte:**
- `RequestMultiplePermissions()` = pentru mai multe permissions
- `TakePicture()` = pentru camera
- `GetContent()` = pentru file picker

---

### Lambda Callback Permission Result

```java
                isGranted -> {
                    if (isGranted) {
                        // Permission granted, fetch location
                        fetchLocationAndUpdateUI();
                    } else {
                        Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
```

**Explicație detaliată:**

Lambda expression care primește rezultatul permission request-ului.

**isGranted:** Boolean = `true` dacă user a acceptat permission, `false` dacă a refuzat.

**Apelare:** Callback apelat AUTOMAT după ce user răspunde la dialog-ul de permission.

**Logică:**
- **Dacă isGranted = true**: Apelează `fetchLocationAndUpdateUI()` pentru a obține și afișa locația imediat
- **Dacă isGranted = false**: Afișează Toast cu mesaj "Location permission denied"

**DIFERENȚĂ CRITICĂ față de versiunea inițială:** Acum apelează `fetchLocationAndUpdateUI()` când permission e granted, astfel încât datele de locație sunt afișate imediat după acordarea permission-ului. Versiunea veche avea bug - nu făcea nimic când permission era granted.

**Toast.LENGTH_SHORT:** Afișare ~2 secunde.

---

### Metoda onCreateView - Semnătura

```java
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
```

**SIMILARITATE:** Ambele fragmente au onCreateView cu aceeași semnătură.

---

### Inflare View Binding

```java
        binding = FragmentGeodataBinding.inflate(inflater, container, false);
```

**SIMILARITATE:** Pattern identic, binding diferit.

---

### Extragere Root View

```java
        View root = binding.getRoot();
```

**SIMILARITATE:** Pattern identic.

---

### Găsire TextView Latitude

```java
        final TextView tvLatitude   = binding.tvLatitude;
```

**DIFERENȚĂ MAJORĂ:** GeodataFragment folosește TextViews pentru afișare, AddressFragment folosește MapView.

Obține referința la TextView pentru afișarea latitude-ului.

**final:** Variabila folosită în lambda mai jos, trebuie final sau effectively final.

---

### Găsire TextView Longitude

```java
        final TextView tvLongitude   = binding.tvLongitude;
```

Obține referința la TextView pentru afișarea longitude-ului.

---

### Găsire TextView Accuracy

```java
        final TextView tvAccuracy    = binding.tvAccuracy;
```

**DIFERENȚĂ:** AddressFragment nu afișează accuracy (precizia locației).

Obține referința la TextView pentru afișarea acurateței locației în metri.

---

### Găsire TextView Altitude

```java
        final TextView tvAltitude    = binding.tvAltitude;
```

**DIFERENȚĂ:** AddressFragment nu afișează altitude.

Obține referința la TextView pentru afișarea altitudinii în metri.

---

### Găsire TextView Speed

```java
        final TextView tvSpeed    = binding.tvSpeed;
```

**DIFERENȚĂ:** AddressFragment nu afișează viteză.

Obține referința la TextView pentru afișarea vitezei în metri/secundă.

---

### Găsire TextView Bearing

```java
        final TextView tvBearing    = binding.tvBearing;
```

**DIFERENȚĂ:** AddressFragment nu afișează bearing (direcția de mișcare).

Obține referința la TextView pentru afișarea bearing-ului în grade (0-360).

---

### Găsire TextView Provider

```java
        final TextView tvProvider    = binding.tvProvider;
```

**DIFERENȚĂ:** AddressFragment nu afișează provider-ul locației.

Obține referința la TextView pentru afișarea provider-ului (GPS, Network, Fused).

---

### Găsire TextView Time

```java
        final TextView tvTime = binding.tvTime;
```

**DIFERENȚĂ:** AddressFragment nu afișează timestamp-ul locației.

Obține referința la TextView pentru afișarea timpului când locația a fost determinată.

---

### Verificare Permission

```java
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
```

**DIFERENȚĂ MAJORĂ:** GeodataFragment verifică explicit permission-ul în onCreateView, AddressFragment nu verifică (se bazează pe my location button sau nu folosește location).

Verifică dacă permission-ul de locație este acordat.

**ActivityCompat.checkSelfPermission():** Verificare compatibilă cu versiuni vechi Android.

**ACCESS_FINE_LOCATION:** Permission pentru GPS high-accuracy.

---

### Launch Permission Request

```java
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
```

**DIFERENȚĂ:** Mecanism modern de request.

Lansează dialog-ul pentru cerere permission dacă nu e acordat.

**.launch(permission):** Declanșează permission request, rezultatul va fi primit în callback-ul definit în onCreate.

---

### Branch Else - Permission Granted

```java
        } else {
```

Branch executat când permission-ul este deja acordat.

---

### Fetch Location și Update UI

```java
            fetchLocationAndUpdateUI(tvLatitude, tvLongitude, tvAccuracy, tvAltitude, tvSpeed, tvBearing, tvProvider, tvTime);
```

**DIFERENȚĂ MAJORĂ:** GeodataFragment apelează metodă custom pentru fetch location, AddressFragment inițializează MapView.

Apelează metoda helper care obține locația și actualizează TextViews.

**Parametri:** Toate cele 8 TextViews pentru update.

---

### Return Root View

```java
        return root;
```

**SIMILARITATE:** Pattern identic.

---

### Metoda onDestroyView

```java
    @Override
    public void onDestroyView() {
```

**SIMILARITATE:** Ambele fragmente au onDestroyView.

---

### Apel Super onDestroyView

```java
        super.onDestroyView();
```

**SIMILARITATE:** Pattern identic.

---

### Cleanup Binding

```java
        binding = null;
```

**DIFERENȚĂ:** GeodataFragment are DOAR binding cleanup, AddressFragment are și MapView.onDestroy() înainte.

AddressFragment:
```java
if (binding.mapView != null) {
    binding.mapView.onDestroy();
}
binding = null;
```

GeodataFragment: Doar `binding = null` (fără MapView).

---

### Metoda fetchLocationAndUpdateUI - Semnătura

```java
    private void fetchLocationAndUpdateUI() {
```

**DIFERENȚĂ MAJORĂ:** Metodă complet nouă, nu există în AddressFragment.

Metodă helper care obține locația curentă și actualizează TextViews folosind binding.

**Parametri:** NICIUN parametru - folosește `binding` direct pentru accesarea TextViews.

**SIMPLIFICARE față de versiunea inițială:** Versiunea veche primea toate cele 8 TextViews ca parametri. Versiunea nouă folosește binding direct, cod mai curat și mai simplu.

---

### Log Info

```java
        Log.i("GeodataFragment", "Get Location here");
```

**DIFERENȚĂ:** AddressFragment nu loggează explicit.

Loggează mesaj informativ în Logcat.

**Log.i():** Info level (pentru debugging).

---

### Creare FusedLocationProviderClient

```java
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
```

**DIFERENȚĂ:** GeodataFragment creează client în metodă custom, AddressFragment îl creează în onCreateView dar nu îl folosește.

Creează clientul pentru obținerea locației.

---

### Try Block - Get Location

```java
        try {
```

Începe bloc try pentru gestionarea SecurityException.

**De ce try:** `getLastLocation()` necesită permission, poate arunca SecurityException dacă permission lipsește.

---

### Get Last Location

```java
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
```

**DIFERENȚĂ MAJORĂ:** GeodataFragment folosește `getLastLocation()`, AddressFragment nu folosește location activ.

**Explicație detaliată:**

Obține ultima locație cunoscută a device-ului asincron.

**getLastLocation():** 
- Returnează `Task<Location>` (asincron)
- Locația cached (ultima cunoscută)
- **NU declanșează update nou** (foarte rapid, battery-efficient)
- Poate returna null dacă nicio locație cached

**.addOnSuccessListener(listener):**
- Callback apelat când Task-ul se finalizează cu succes
- **location** = poate fi null chiar și la success

**De ce cached:**
- Foarte rapid (milisecunde vs secunde)
- Nu consumă baterie
- Suficient pentru afișare date statice

---

### Verificare Location Non-Null

```java
                if (location != null) {
```

Verifică dacă există o locație cached disponibilă.

**Când e null:**
- Device-ul nu a avut niciodată locație (first boot)
- Location services disabled
- Cache expirat

---

### Extragere Latitude

```java
                    double latitude = location.getLatitude();
```

**DIFERENȚĂ:** GeodataFragment extrage date raw din Location, AddressFragment folosește LatLng pentru hartă.

Extrage latitude-ul din obiectul Location.

**Tip:** double (precizie înaltă pentru coordonate geografice).

---

### Extragere Longitude

```java
                    double longitude = location.getLongitude();
```

Extrage longitude-ul din obiectul Location.

---

### Extragere Accuracy

```java
                    float accuracy = location.getAccuracy();
```

**DIFERENȚĂ:** Date suplimentare pe care AddressFragment nu le afișează.

Extrage acuratețea locației în metri.

**Exemplu:** accuracy = 10.5 → locația reală e într-un cerc de 10.5m rază.

---

### Extragere Speed

```java
                    float speed = location.getSpeed();
```

**DIFERENȚĂ:** Date suplimentare.

Extrage viteza device-ului în metri/secundă.

**Exemplu:** speed = 5.0 → 5 m/s = 18 km/h.

---

### Extragere Bearing

```java
                    float bearing = location.getBearing();
```

**DIFERENȚĂ:** Date suplimentare.

Extrage bearing-ul (direcția de mișcare) în grade.

**Range:** 0-360 grade (0 = Nord, 90 = Est, 180 = Sud, 270 = Vest).

---

### Extragere Time

```java
                    long time = location.getTime();
```

**DIFERENȚĂ:** Date suplimentare.

Extrage timestamp-ul când locația a fost determinată.

**Tip:** long = milisecunde de la Unix epoch (1 ianuarie 1970).

---

### Extragere Provider

```java
                    String provider = location.getProvider();
```

**DIFERENȚĂ:** Date suplimentare.

Extrage numele provider-ului de locație.

**Valori posibile:**
- "gps" = GPS satellites
- "network" = WiFi/cell towers
- "fused" = FusedLocationProvider (combinație optimă)

---

### Extragere Altitude

```java
                    float altitude = (float) location.getAltitude();
```

**DIFERENȚĂ:** Date suplimentare.

Extrage altitudinea în metri deasupra nivelului mării.

**Cast:** `getAltitude()` returnează double, cast la float pentru consistency.

---

### Creare Date Object

```java
                    Date date = new Date(time);
```

**DIFERENȚĂ:** GeodataFragment formatează timpul, AddressFragment nu lucrează cu time.

Convertește timestamp-ul (milisecunde) în obiect Date.

---

### Creare SimpleDateFormat

```java
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
```

**DIFERENȚĂ:** Formatare dată specific pentru GeodataFragment.

Creează formatter pentru conversie Date → String citibil.

**Pattern "yyyy-MM-dd HH:mm:ss":**
- yyyy = an (4 cifre)
- MM = lună (01-12)
- dd = zi (01-31)
- HH = oră (00-23)
- mm = minute (00-59)
- ss = secunde (00-59)

**Locale.getDefault():** Folosește setările locale ale device-ului.

**Exemplu output:** "2026-01-09 14:30:45"

---

### Formatare Time

```java
                    String formattedTime = formatter.format(date);
```

Convertește Date în String formatat.

**Rezultat:** String citibil de tipul "2026-01-09 14:30:45".

---

### Update TextView Latitude

```java
                    tvLatitude.setText("Latitude: " + latitude);
```

**DIFERENȚĂ MAJORĂ:** GeodataFragment actualizează TextViews, AddressFragment actualizează hartă cu markere.

Actualizează TextView-ul cu valoarea latitude-ului.

**Exemplu:** "Latitude: 44.4268"

---

### Update TextView Longitude

```java
                    tvLongitude.setText("Longitude: " + longitude);
```

Actualizează TextView-ul cu valoarea longitude-ului.

**Exemplu:** "Longitude: 26.1025"

---

### Update TextView Accuracy

```java
                    tvAccuracy.setText("Accuracy: " + accuracy);
```

Actualizează TextView-ul cu acuratețea în metri.

**Exemplu:** "Accuracy: 10.5"

---

### Update TextView Altitude

```java
                    tvAltitude.setText("Altitude: " + altitude);
```

Actualizează TextView-ul cu altitudinea.

**Exemplu:** "Altitude: 85.3"

---

### Update TextView Speed

```java
                    tvSpeed.setText("Speed: " + speed);
```

Actualizează TextView-ul cu viteza.

**Exemplu:** "Speed: 0.0" (device static) sau "Speed: 5.2"

---

### Update TextView Bearing

```java
                    tvBearing.setText("Bearing: " + bearing);
```

Actualizează TextView-ul cu bearing-ul.

**Exemplu:** "Bearing: 45.0" (Nord-Est)

---

### Update TextView Provider

```java
                    tvProvider.setText("Provider: " + provider);
```

Actualizează TextView-ul cu numele provider-ului.

**Exemplu:** "Provider: fused"

---

### Update TextView Time

```java
                    tvTime.setText("Time: " + formattedTime);
```

Actualizează TextView-ul cu timpul formatat.

**Exemplu:** "Time: 2026-01-09 14:30:45"

---

### Branch Else - Location Null

```java
                } else {
```

Branch executat când location e null (nicio locație cached disponibilă).

---

### Toast Unable to Get Location

```java
                    Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show();
```

**DIFERENȚĂ:** GeodataFragment afișează Toast pentru erori, AddressFragment nu.

Afișează mesaj scurt că locația nu poate fi obținută.

---

### Catch SecurityException

```java
        } catch (SecurityException e) {
```

Prinde excepția aruncată când se încearcă accesarea locației fără permission.

**Când apare:** Teoretic nu ar trebui (verificăm permission înainte), dar safety net.

---

### Log Error

```java
            Log.e("GeodataFragment", "Error getting location", e);
```

Loggează eroarea în Logcat cu stack trace.

**Log.e():** Error level (pentru erori serioase).

---



## Flux Complet GeodataFragment

```
Fragment creat
    │
    └──> onCreate()
         │
         └──> registerForActivityResult(RequestPermission)
              │
              └──> Callback: isGranted → Toast dacă denied


Fragment view creat
    │
    └──> onCreateView()
         │
         ├──> Binding inflate
         │
         ├──> Găsire 8× TextViews
         │
         └──> checkSelfPermission(ACCESS_FINE_LOCATION)
              │
              ├──> Permission DENIED ───────────────────────┐
              │    │                                        │
              │    └──> requestPermissionLauncher.launch() │
              │         │                                   │
              │         └──> [User vede dialog permission] │
              │              │                              │
              │              ├──> ACCEPT ───────────────────┤
              │              │   (next time va merge direct │
              │              │    în branch GRANTED)        │
              │              │                              │
              │              └──> DENY                      │
              │                   └──> Toast "denied" ◄─────┘
              │
              └──> Permission GRANTED
                   │
                   └──> fetchLocationAndUpdateUI(8× TextView)
                        │
                        ├──> FusedLocationProviderClient.getLastLocation()
                        │    │
                        │    └──> [Async] Task<Location>
                        │         │
                        │         └──> addOnSuccessListener(location)
                        │              │
                        │              ├──> location != null
                        │              │    │
                        │              │    ├──> Extragere date (lat, lng, accuracy, etc.)
                        │              │    │
                        │              │    ├──> Formatare time (SimpleDateFormat)
                        │              │    │
                        │              │    └──> Update 8× TextViews
                        │              │         ├──> "Latitude: 44.4268"
                        │              │         ├──> "Longitude: 26.1025"
                        │              │         ├──> "Accuracy: 10.5"
                        │              │         ├──> "Altitude: 85.3"
                        │              │         ├──> "Speed: 0.0"
                        │              │         ├──> "Bearing: 0.0"
                        │              │         ├──> "Provider: fused"
                        │              │         └──> "Time: 2026-01-09 14:30:45"
                        │              │
                        │              └──> location == null
                        │                   └──> Toast "Unable to get location"
                        │
                        └──> SecurityException (safety net)
                             └──> Log.e("Error getting location")


Fragment view distrus
    │
    └──> onDestroyView()
         │
         └──> binding = null
```

**Note:** Fără MapView lifecycle complexity, fără geocoding, fără butoane - mult mai simplu decât AddressFragment.

---

## ActivityResultLauncher vs requestPermissions (deprecated)

### Modul Vechi (requestPermissions - deprecated)

```java
// Request
requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

// Result în metodă separată
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == REQUEST_CODE) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Granted
        } else {
            // Denied
        }
    }
}
```

### Modul Nou (ActivityResultLauncher - folosit în GeodataFragment)

```java
// Setup în onCreate
requestPermissionLauncher = registerForActivityResult(
    new ActivityResultContracts.RequestPermission(),
    isGranted -> {
        if (isGranted) {
            // Granted
        } else {
            // Denied
        }
    }
);

// Request
requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
```

**Avantaje ActivityResultLauncher:**
- Callback inline (nu metodă separată)
- Type-safe (Boolean vs int[] array)
- Mai clar și mai concis
- Separat request de handling

---

## getLastLocation() vs requestLocationUpdates()

### getLastLocation() (folosit în GeodataFragment)

```java
fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
    // location = cached, instant, poate fi null
});
```

**Caracteristici:**
- Returnează ultima locație CACHED
- Foarte rapid (milisecunde)
- Battery-efficient (nu activează GPS)
- Poate returna null sau locație veche

**Utilizare:** Date statice, one-time read

### requestLocationUpdates() (NU folosit)

```java
fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
```

**Caracteristici:**
- Primește update-uri continue
- Activează GPS/Network activ
- Consumă baterie
- Garantat fresh data

**Utilizare:** Tracking real-time, navigație

---

