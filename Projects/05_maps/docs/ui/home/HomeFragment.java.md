# HomeFragment.java - Documentație Diferențe față de AddressFragment

## Prezentare

`HomeFragment` este un fragment similar cu `AddressFragment` care folosește **Google Maps**, dar cu câteva diferențe importante:
- **Folosește locația curentă** în loc de geocoding pentru o adresă hardcoded
- **setMyLocationEnabled(true)** - activează layer-ul "My Location" pe hartă
- **setMapStyle()** - aplică stil custom JSON pentru ascunderea POI-urilor (Points of Interest)
- **requestPermissions vechi** - folosește metoda deprecated `requestPermissions()` în loc de ActivityResultLauncher
- **centerMapOnLocation() FOLOSIT** - metodă helper care centrează harta pe locația curentă (definită dar neutilizată în AddressFragment)
- **NU are butoane tip hartă** - fără satellite/normal/terrain
- **NU are geocoding** - nu convertește adrese text în coordonate

**Similarități cu AddressFragment:**
- Structură MapView identică
- Lifecycle management MapView complet (onStart/Resume/Pause/Stop/Destroy/LowMemory)
- OnMapReadyCallback implementation
- FusedLocationProviderClient pentru locație
- getApiKeyFromManifest() metodă identică (neutilizată)

**Diferențe cheie:**
- My Location layer activat
- Map style custom (ascunde POI-uri)
- Request permissions în onMapReady (deprecated method)
- Folosește efectiv FusedLocationProviderClient
- Centrează automat pe locația user-ului

## Analiza Diferențelor - Linie cu Linie

### Import-uri Absente

**DIFERENȚĂ:** HomeFragment NU importă:
```java
// NU importă:
import android.location.Address;           // Nu face geocoding
import android.location.Geocoder;          // Nu convertește adrese
import android.widget.Button;              // Fără butoane tip hartă
import java.io.IOException;                // Nu face geocoding care aruncă IOException
import java.util.List;                     // Nu primește liste de Address
```

AddressFragment importă toate acestea pentru geocoding și butoane.

---

### Import MapStyleOptions

```java
import com.google.android.gms.maps.model.MapStyleOptions;
```

**DIFERENȚĂ MAJORĂ:** HomeFragment importă `MapStyleOptions`, AddressFragment NU.

Importă `MapStyleOptions` pentru aplicarea stilurilor custom JSON pe hartă.

**Utilizare:** Customizare vizuală hartă (culori, vizibilitate features, etc.).

---

### Import View Binding

```java
import ro.makore.akrilki_05.databinding.FragmentHomeBinding;
```

**DIFERENȚĂ:** Pachet diferit (`home` vs `address`), binding diferit (`FragmentHomeBinding` vs `FragmentAddressBinding`).

---

### Declararea Clasei

```java
public class HomeFragment extends Fragment implements OnMapReadyCallback {
```

**SIMILARITATE:** Ambele implementează `OnMapReadyCallback`.

---

### Variabile de Instanță

```java
    private FragmentHomeBinding binding;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
```

**SIMILARITATE:** Variabile identice (tipuri diferite pentru binding).

**DIFERENȚĂ:** HomeFragment FOLOSEȘTE efectiv `fusedLocationProviderClient`, AddressFragment îl inițializează dar nu îl folosește.

---

### Metoda onCreateView

```java
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);

        return binding.getRoot();
    }
```

**DIFERENȚĂ MAJORĂ:** HomeFragment NU are cod pentru:
- Găsirea butoanelor (satelliteButton, normalButton, terrainButton)
- Listener-uri pentru butoane

**SIMILARITATE:** 
- Pattern identic pentru binding inflate
- FusedLocationProviderClient initialization
- MapView onCreate și getMapAsync

AddressFragment are în plus găsirea și setup-ul celor 3 butoane în `onMapReady()`, nu în `onCreateView()`.

---

### Metoda onMapReady - Semnătura

```java
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
```

**SIMILARITATE:** Semnătură identică.

---

### Salvare Referință GoogleMap

```java
        googleMap = map;
```

**SIMILARITATE:** Pattern identic.

---

### Verificare Permission - Double Check

```java
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
```

**DIFERENȚĂ MAJORĂ:** HomeFragment verifică DOUĂ permissions (FINE și COARSE) cu AND logic.

**Logică:**
```
if (FINE not granted AND COARSE not granted)
    → request permission
```

**Problematic:** AND logic înseamnă că dacă ORICARE permission e granted, verificarea e false → nu se request.

**Intent probabil:** OR logic (`||`) pentru a request dacă ORICARE lipsește.

AddressFragment: Nu verifică permissions explicit (se bazează pe My Location button implicit).

---

### Request Permissions - Metoda Veche

```java
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
```

**DIFERENȚĂ MAJORĂ:** HomeFragment folosește metoda **DEPRECATED** `requestPermissions()`.

Request permission pentru ACCESS_FINE_LOCATION folosind metoda veche.

**Parametri:**
- `requireActivity()` = Activity context
- `new String[]{...}` = array cu permissions
- `1` = request code pentru identificare în callback

**Deprecated:** Ar trebui folosit ActivityResultLauncher (ca în GeodataFragment).

**Note:** Nu există override `onRequestPermissionsResult()` în acest cod → callback-ul lipsește!

AddressFragment: Nu request permissions explicit.

---

### Early Return

```java
            return;
```

**DIFERENȚĂ:** Oprește execuția `onMapReady()` dacă permissions lipsesc.

Previne configurarea hărții fără permissions.

---

### Activare My Location Layer

```java
        googleMap.setMyLocationEnabled(true);
```

**DIFERENȚĂ MAJORĂ:** HomeFragment activează layer-ul "My Location", AddressFragment NU.

**Explicație detaliată:**

Activează layer-ul "My Location" pe hartă.

**Efecte:**
- Afișează **blue dot** pentru locația curentă a user-ului
- Buton "My Location" activat automat (dacă `setMyLocationButtonEnabled(true)` setat)
- Urmărește locația în timp real (dacă user se mișcă, dot-ul se actualizează)

**Necesită:** Permission ACCESS_FINE_LOCATION sau ACCESS_COARSE_LOCATION.

**SecurityException:** Poate arunca SecurityException dacă permission lipsește (de aceea verificare înainte).

AddressFragment: Activează doar `setMyLocationButtonEnabled(true)` în UI settings, dar NU `setMyLocationEnabled(true)` → butonul apare dar nu funcționează complet.

---

### Setare Map Style

```java
        googleMap.setMapStyle(new MapStyleOptions("[{\"featureType\":\"poi\",\"stylers\":[{\"visibility\":\"off\"}]}]"));
```

**DIFERENȚĂ MAJORĂ:** HomeFragment aplică stil custom JSON, AddressFragment NU.

**Explicație detaliată:**

Aplică un stil custom JSON care ascunde toate POI-urile (Points of Interest) de pe hartă.

**MapStyleOptions:** Acceptă string JSON cu reguli de stilizare.

**JSON decodificat:**
```json
[
  {
    "featureType": "poi",
    "stylers": [
      {
        "visibility": "off"
      }
    ]
  }
]
```

**Efect:**
- `"featureType": "poi"` = toate Points of Interest (restaurante, magazine, benzinării, etc.)
- `"visibility": "off"` = ascunde complet

**De ce:** Hartă mai curată, focus pe locația user-ului fără distrageri.

**Alte stilizări posibile:**
- Schimbare culori străzi, apă, parcuri
- Schimbare density etichete
- Night mode
- Retro style

AddressFragment: Nu aplică stiluri custom → hartă standard Google Maps.

---

### Get Last Location și Center Map

```java
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                centerMapOnLocation(location);
            }
        });
```

**DIFERENȚĂ MAJORĂ:** HomeFragment FOLOSEȘTE efectiv FusedLocationProviderClient.

Obține ultima locație cunoscută și centrează harta pe ea.

**Diferențe față de GeodataFragment:**
- GeodataFragment: Extrage toate datele (lat, lng, accuracy, speed, etc.) și afișează în TextViews
- HomeFragment: Folosește doar coordonatele pentru centrare hartă + marker

**Diferențe față de AddressFragment:**
- AddressFragment: FusedLocationProviderClient inițializat dar NEUTILIZAT
- HomeFragment: FusedLocationProviderClient FOLOSIT activ

---

### Metoda getApiKeyFromManifest

```java
    private String getApiKeyFromManifest() {
        try {
            ApplicationInfo applicationInfo = requireContext().getPackageManager()
                    .getApplicationInfo(requireContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = applicationInfo.metaData;
            return metaData.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            throw new RuntimeException("Failed to load meta-data, NullPointer: " + e.getMessage());
        }
    }
```

**SIMILARITATE COMPLETĂ:** Cod IDENTIC cu AddressFragment.

Metodă definită dar **NU folosită** în ambele fragmente.

---

### Metoda centerMapOnLocation - FOLOSITĂ

```java
    private void centerMapOnLocation(@NonNull Location location) {
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
        googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
    }
```

**DIFERENȚĂ MAJORĂ:** HomeFragment FOLOSEȘTE această metodă, AddressFragment o definește dar NU o folosește.

**SIMILARITATE:** Cod identic.

Convertește Location în LatLng, mută camera la locație cu zoom 15, adaugă marker "You are here".

**Comparație:**
- AddressFragment: Metodă definită, neutilizată (dead code)
- HomeFragment: Metodă definită, APELATĂ din `onMapReady()` callback
- GeodataFragment: Nu are această metodă (nu folosește hartă)

---

### Metode Lifecycle MapView

```java
    @Override
    public void onStart() {
        super.onStart();
        if (binding.mapView != null) {
            binding.mapView.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding.mapView != null) {
            binding.mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding.mapView != null) {
            binding.mapView.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (binding.mapView != null) {
            binding.mapView.onStop();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding.mapView != null) {
            binding.mapView.onDestroy();
        }
        binding = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (binding.mapView != null) {
            binding.mapView.onLowMemory();
        }
    }
```

**SIMILARITATE COMPLETĂ:** Cod IDENTIC cu AddressFragment.

Sincronizare lifecycle MapView cu Fragment lifecycle.

**Diferență față de GeodataFragment:** GeodataFragment NU are aceste metode (fără MapView).

---

## Rezumat Diferențe Majore

### 1. Funcționalitate

| Aspect | AddressFragment | HomeFragment |
|--------|-----------------|--------------|
| **Locație** | Geocoding adresă hardcoded | Locație curentă user |
| **Geocoding** | DA (adresă → coordonate) | NU |
| **My Location Layer** | NU (doar button enabled) | DA (`setMyLocationEnabled(true)`) |
| **Map Style** | Nu (standard) | DA (ascunde POI-uri) |
| **Butoane tip hartă** | DA (3 butoane) | NU |
| **FusedLocationClient** | Inițializat, neutilizat | Inițializat, FOLOSIT |
| **centerMapOnLocation()** | Definit, neutilizat | Definit, FOLOSIT |

### 2. Permission Handling

| Aspect | AddressFragment | HomeFragment |
|--------|-----------------|--------------|
| **Verificare** | Nu explicit | DA (în onMapReady) |
| **Metoda** | - | requestPermissions() deprecated |
| **Callback** | - | Lipsește (onRequestPermissionsResult absent) |
| **Logică** | - | AND (probabil greșită, ar trebui OR) |

### 3. UI Components

| Aspect | AddressFragment | HomeFragment |
|--------|-----------------|--------------|
| **MapView** | DA | DA |
| **Butoane** | 3 (satellite/normal/terrain) | 0 |
| **TextViews** | 0 | 0 |
| **My Location dot** | NU | DA (blue dot) |

### 4. Imports Unice

**HomeFragment specific:**
- MapStyleOptions (stiluri custom hartă)

**AddressFragment specific:**
- Address, Geocoder (geocoding)
- Button (butoane)
- IOException, List (pentru geocoding)

**GeodataFragment specific:**
- TextView, Toast, Log (afișare text și mesaje)
- ActivityResultLauncher (permissions modern)
- SimpleDateFormat, Date, Locale (formatare dată)

### 5. Complexitate

| Aspect | AddressFragment | HomeFragment |
|--------|-----------------|--------------|
| **Linii cod** | ~190 | ~140 |
| **Helper methods** | 3 (toate neutilizate) | 2 (1 folosit, 1 neutilizat) |
| **Features** | Geocoding, 3 butoane, MapView | My Location, Map Style, MapView |
| **Complexitate** | Medie (geocoding logic) | Mică-Medie (mai simplu) |

---

## Flux Complet HomeFragment

```
Fragment view creat
    │
    └──> onCreateView()
         │
         ├──> Binding inflate
         ├──> FusedLocationProviderClient initialize
         ├──> MapView.onCreate(savedInstanceState)
         └──> MapView.getMapAsync(this)
              │
              └──> [Background] Inițializare hartă
                   │
                   └──> onMapReady(GoogleMap)
                        │
                        ├──> Verificare permissions (FINE AND COARSE)
                        │    │
                        │    ├──> NOT GRANTED (ambele lipsesc)
                        │    │    │
                        │    │    ├──> requestPermissions() [deprecated]
                        │    │    └──> return (stop aici)
                        │    │
                        │    └──> GRANTED (cel puțin una)
                        │         │
                        │         ├──> setMyLocationEnabled(true)
                        │         │    └──> Blue dot apare pe hartă
                        │         │
                        │         ├──> setMapStyle(JSON)
                        │         │    └──> Ascunde POI-uri
                        │         │
                        │         └──> getLastLocation()
                        │              │
                        │              └──> [Async] Task<Location>
                        │                   │
                        │                   └──> addOnSuccessListener(location)
                        │                        │
                        │                        ├──> location != null
                        │                        │    │
                        │                        │    └──> centerMapOnLocation()
                        │                        │         ├──> new LatLng(lat, lng)
                        │                        │         ├──> moveCamera(zoom=15)
                        │                        │         └──> addMarker("You are here")
                        │                        │
                        │                        └──> location == null
                        │                             └──> Nimic (silent fail)


[Fragment lifecycle - identic cu AddressFragment]
    │
    ├──> onStart() → mapView.onStart()
    ├──> onResume() → mapView.onResume()
    ├──> onPause() → mapView.onPause()
    ├──> onStop() → mapView.onStop()
    ├──> onDestroyView() → mapView.onDestroy() + binding = null
    └──> onLowMemory() → mapView.onLowMemory()
```

---

## setMyLocationEnabled() - Detalii

### HomeFragment (FOLOSIT)

```java
googleMap.setMyLocationEnabled(true);
```

**Efecte:**
- Blue dot pentru locația user pe hartă
- Dot-ul urmărește mișcarea user-ului în timp real
- Integrare cu My Location button (dacă enabled)

### AddressFragment (NU folosit)

```java
// NU apelează setMyLocationEnabled()
// Doar UI button enabled:
googleMap.getUiSettings().setMyLocationButtonEnabled(true);
```

**Rezultat:** Buton afișat dar NU funcțional (nu apare blue dot).

**Fix necesar:** Ar trebui să adauge `setMyLocationEnabled(true)` înainte de button enable.

---

## Map Style Options - JSON Syntax

### Folosit în HomeFragment

```json
[
  {
    "featureType": "poi",
    "stylers": [
      {
        "visibility": "off"
      }
    ]
  }
]
```

**Ascunde:** Toate POI-urile (restaurante, magazine, etc.)

### Exemple Alte Stiluri (NU folosite)

**Night Mode:**
```json
[
  {
    "elementType": "geometry",
    "stylers": [{"color": "#242f3e"}]
  },
  {
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#746855"}]
  }
]
```

**Ascunde Etichete:**
```json
[
  {
    "featureType": "all",
    "elementType": "labels",
    "stylers": [{"visibility": "off"}]
  }
]
```

**Retro Style:**
```json
[
  {
    "featureType": "road",
    "stylers": [{"hue": "#5e00ff"}, {"saturation": -79}]
  }
]
```

---

## Permission Logic Bug în HomeFragment

### Cod Actual (PROBABIL GREȘIT)

```java
if (ACCESS_FINE not granted AND ACCESS_COARSE not granted) {
    request permission
}
```

**Problema:** Request doar dacă AMBELE lipsesc. Dacă COARSE e granted dar FINE lipsește → nu request.

### Logică Corectă (AR TREBUI)

```java
if (ACCESS_FINE not granted AND ACCESS_COARSE not granted) {
    request permission
}
```

SAU

```java
if (ACCESS_FINE not granted) {
    request permission
}
```

**Fix sugerat:**
```java
if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(requireActivity(),
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    return;
}
```

---

## requestPermissions (deprecated) - Problemă Lipsă Callback

### HomeFragment Request

```java
ActivityCompat.requestPermissions(requireActivity(),
        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
```

**Problemă:** Nu există `onRequestPermissionsResult()` override!

### Callback Lipsă (AR TREBUI)

```java
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == 1) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted - reinitialize map
            if (googleMap != null) {
                onMapReady(googleMap); // Re-call setup
            }
        } else {
            // Permission denied - show message
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
```

**Impact:** User acceptă permission → nimic nu se întâmplă automat → trebuie să navigheze away și înapoi.

**Soluție modernă:** Folosește ActivityResultLauncher ca GeodataFragment.

---

## Comparație Cele 3 Fragmente Maps

### AddressFragment

**Focus:** Geocoding adresă statică
- Geocoding adresă hardcoded
- 3 butoane tip hartă
- FusedLocationClient neutilizat
- Fără My Location layer
- Fără map style

### HomeFragment

**Focus:** Locație curentă user
- My Location layer activ (blue dot)
- Map style custom (fără POI-uri)
- FusedLocationClient folosit
- Fără butoane
- Permission request (deprecated, callback lipsă)

### GeodataFragment

**Focus:** Date GPS raw în text
- Fără hartă (TextViews)
- 8 proprietăți Location afișate
- ActivityResultLauncher modern
- SimpleDateFormat pentru timestamp
- Fără MapView lifecycle

---

## Rezumat Concepte Cheie HomeFragment

**My Location Layer:**
- setMyLocationEnabled(true) pentru blue dot
- Urmărire locație real-time
- Necesită permissions

**Map Style Custom:**
- MapStyleOptions cu JSON
- Ascundere POI-uri pentru hartă curată
- Customizare vizuală completă posibilă

**FusedLocationClient Activ:**
- getLastLocation() pentru cached location
- centerMapOnLocation() cu marker "You are here"
- Centrare automată la deschidere

**Permission Issues:**
- requestPermissions deprecated folosit
- onRequestPermissionsResult lipsește
- Logică verificare posibil greșită (AND vs OR)

**Lifecycle:**
- MapView lifecycle identic cu AddressFragment
- Toate metodele necesare prezente
- Gestionare corectă memory leaks
