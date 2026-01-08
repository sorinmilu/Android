# LocationService.java — Documentație detaliată a codului sursă

Acest document prezintă codul complet al clasei `LocationService.java` împărțit în fragmente logice, fiecare precedat de explicații detaliate în limba română. Documentația este destinată programatorilor cu cunoștințe de Java dar cu experiență limitată în programarea Android.

## Context general

`LocationService` este clasa utilitar responsabilă pentru **obținerea locației GPS curente** și **conversia coordonatelor în nume de oraș** (reverse geocoding). Aceasta încapsulează complexitatea Google Play Services Location API și oferă o interfață simplă bazată pe callback-uri.

### Responsabilități principale:

1. **Obținere locație GPS** - Folosește Google Play Services pentru coordonate precise
2. **Reverse geocoding** - Transformă coordonate (lat/long) în nume de oraș
3. **Gestionare permisiuni** - Verifică ACCESS_FINE_LOCATION și ACCESS_COARSE_LOCATION
4. **Fallback-uri multiple** - Locality → AdminArea → Country (dacă orașul nu e disponibil)
5. **Callback pattern** - Notificare asincronă pentru succes/eroare
6. **Thread safety** - Google Play Services gestionează threading-ul automat

### De ce este necesară această clasă?

| Problemă | Soluție LocationService |
|----------|-------------------------|
| **GPS complex** | FusedLocationProviderClient (API simplificat) |
| **Coordonate → Oraș** | Geocoder (reverse geocoding automat) |
| **Permisiuni** | Verificare runtime înainte de acces GPS |
| **Operații async** | Callback interface pentru notificare |
| **Erori multiple** | Gestionare granulară (no permission, no location, geocoder fail) |
| **Fallback** | Locality → AdminArea → Country (3 niveluri) |

### Utilizare în aplicație:

```
MainActivity
    ↓
LocationService.getCurrentLocationName()
    ↓
GPS Provider (Google Play Services)
    ↓
Coordonate: (44.4268, 26.1025)
    ↓
Geocoder (reverse geocoding)
    ↓
Nume oraș: "Bucharest"
    ↓
Callback.onLocationReceived("Bucharest")
    ↓
LocationManager.setCurrentLocation("Bucharest")
    ↓
Spinner update → "Bucharest (current)"
```

## 1. Declararea pachetului

```java
package ro.makore.akrilki_08.util;
```

**Explicație:**
- Sub-pachetul `util` grupează toate clasele utilitare
- Alte clase util în aplicație: `LocationManager` (persistență orașe)

## 2. Import-uri pentru permisiuni Android

```java
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
```

**Linie cu linie:**
- **`Manifest`** - Constante pentru permisiuni (ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
- **`Context`** - Necesar pentru accesarea serviciilor sistem
- **`PackageManager`** - Pentru verificarea permisiunilor runtime

### Permisiuni necesare în AndroidManifest.xml:

```xml
<manifest>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
</manifest>
```

### Diferența între FINE și COARSE:

| Permisiune | Precizie | Sursă | Baterie | Utilizare |
|------------|----------|-------|---------|-----------|
| **ACCESS_FINE_LOCATION** | ±5-10m | GPS, Wi-Fi, Cell | Mare | Navigație, tracking precis |
| **ACCESS_COARSE_LOCATION** | ±100-500m | Wi-Fi, Cell | Mică | Meteo, timezone |

**În această aplicație**: Folosim FINE pentru precizie maximă (numele exact al orașului).

## 3. Import-uri pentru geocoding

```java
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
```

**Linie cu linie:**
- **`Address`** - Reprezintă o adresă (oraș, țară, stradă, cod poștal, etc.)
- **`Geocoder`** - Serviciu pentru conversie coordonate ↔ adrese
- **`Location`** - Reprezintă o locație GPS (latitude, longitude, altitude, etc.)
- **`Log`** - Pentru logging

### Ce este Geocoding?

```
GEOCODING (Forward):
"Bucharest, Romania" → (44.4268, 26.1025)

REVERSE GEOCODING (Reverse):
(44.4268, 26.1025) → "Bucharest, Romania"
```

**LocationService folosește REVERSE GEOCODING**: GPS → Oraș

## 4. Import-uri pentru AndroidX

```java
import androidx.core.app.ActivityCompat;
```

**Explicație:**
- **`ActivityCompat`** - Funcții de compatibilitate pentru verificarea permisiunilor
- Funcționează pe toate versiunile Android (backward compatible)

## 5. Import-uri pentru Google Play Services

```java
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.Task;
```

**Linie cu linie:**
- **`FusedLocationProviderClient`** - Client pentru obținerea locației (API modern)
- **`LocationServices`** - Factory pentru crearea clientului
- **`Priority`** - Constante pentru prioritatea locației (HIGH_ACCURACY, BALANCED, LOW_POWER)
- **`Task`** - Reprezintă o operație asincronă (similar cu Future/Promise)

### De ce FusedLocationProviderClient?

| Aspect | LocationManager (vechi) | FusedLocationProviderClient (nou) |
|--------|-------------------------|-----------------------------------|
| **API** | Android framework | Google Play Services |
| **Furnizori** | GPS, Network (separat) | **Fuzionat** (automat cel mai bun) |
| **Baterie** | Consum mare | **Optimizat** (inteligent) |
| **Precizie** | Configurare manuală | Automat bazat pe prioritate |
| **Threading** | Manual | **Automat** (Task API) |
| **Recomandat** | ✗ Deprecated | ✓ Best practice |

**Concluzie**: FusedLocationProviderClient este alegerea corectă pentru Android modern!

## 6. Import-uri pentru excepții și utilitare

```java
import java.io.IOException;
import java.util.List;
import java.util.Locale;
```

**Linie cu linie:**
- **`IOException`** - Excepție pentru erori Geocoder (no internet, service unavailable)
- **`List`** - Pentru lista de adrese returnată de Geocoder
- **`Locale`** - Pentru limba/țara utilizatorului (ex: Locale.getDefault() = română)

## 7. Declararea clasei și câmpurilor membre

### Declararea clasei

```java
public class LocationService {
```

**Explicație:**
- Clasă publică, accesibilă din orice pachet
- Nu extinde nicio clasă (standalone utility)
- Nu este singleton (instanță nouă pentru fiecare Activity)

### Constantă pentru logging

```java
    private static final String TAG = "LocationService";
```

**Explicație:**
- Tag pentru identificarea mesajelor în logcat
- `static final` - Constantă partajată de toate instanțele

### Câmpurile private

```java
    private final FusedLocationProviderClient fusedLocationClient;
    private final Context context;
    private final Geocoder geocoder;
```

**Linie cu linie:**
- **`fusedLocationClient`** - Client pentru obținerea locației GPS
- **`context`** - Context pentru accesarea serviciilor
- **`geocoder`** - Serviciu pentru conversie coordonate → adrese
- **`final`** - Referințele nu se schimbă după inițializare în constructor

## 8. Constructorul - Inițializare servicii

```java
    public LocationService(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.geocoder = new Geocoder(context, Locale.getDefault());
    }
```

**Linie cu linie:**
- **`Context context`** - Necesar pentru toate serviciile
- `this.context = context` - Salvăm referința
- **`LocationServices.getFusedLocationProviderClient(context)`** - Creăm clientul GPS
  - Factory method din Google Play Services
  - Returnează client pre-configurat
- **`new Geocoder(context, Locale.getDefault())`** - Creăm Geocoder
  - `Locale.getDefault()` - Limba utilizatorului (română → "București", engleză → "Bucharest")

### Exemplu de utilizare în MainActivity:

```java
public class MainActivity extends AppCompatActivity {
    private LocationService locationService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Creăm serviciul
        locationService = new LocationService(this);
        
        // Folosim serviciul
        locationService.getCurrentLocationName(new LocationService.LocationCallback() {
            @Override
            public void onLocationReceived(String cityName) {
                // Update UI cu orașul
            }
            
            @Override
            public void onLocationError(String error) {
                // Afișare eroare
            }
        });
    }
}
```

## 9. Interfața LocationCallback - Pattern Callback

```java
    public interface LocationCallback {
        void onLocationReceived(String cityName);
        void onLocationError(String error);
    }
```

**Explicație:**
- **Pattern Callback**: Interfață pentru comunicare asincronă
- **`onLocationReceived(String cityName)`** - Apelată la succes cu numele orașului
- **`onLocationError(String error)`** - Apelată la eroare cu mesajul descriptiv

### De ce callback și nu return?

```java
// RĂU - Blocant (nu funcționează pentru operații async):
public String getCurrentLocationName() {
    // GPS și geocoding durează secunde!
    // UI-ul ar îngheța...
    return cityName;
}

// BINE - Non-blocant (operație asincronă):
public void getCurrentLocationName(LocationCallback callback) {
    // Operația se execută în background
    // Callback-ul e apelat când se termină
    // UI-ul rămâne responsive
}
```

### Flow callback:

```
MainActivity.onCreate()
    ↓
locationService.getCurrentLocationName(callback)
    ↓
[Returnează imediat, nu blochează]
    ↓
GPS în background (1-3 secunde)
    ↓
callback.onLocationReceived("Bucharest")
    ↓
MainActivity actualizează UI
```

## 10. Metoda getCurrentLocationName() - Obținerea numelui orașului

Aceasta este metoda publică principală, punct de intrare pentru obținerea locației.

### Documentația metodei

```java
    /**
     * Get current location and convert to city name
     */
    public void getCurrentLocationName(LocationCallback callback) {
```

**Explicație:**
- Comentariu Javadoc pentru documentare
- **Parametru**: `LocationCallback callback` - Va fi apelat cu rezultatul
- **Return**: void (rezultatul vine prin callback asincron)

### Verificarea permisiunilor runtime

```java
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Location permission not granted");
            callback.onLocationError("Location permission not granted");
            return;
        }
```

**Linie cu linie:**
- **`ActivityCompat.checkSelfPermission()`** - Verifică dacă permisiunea este acordată
- Verificăm FINE ȘI COARSE (AND logic - ambele trebuie să lipsească pentru eroare)
- **`!= PackageManager.PERMISSION_GRANTED`** - Permisiunea NU este acordată
- Dacă nicio permisiune nu e acordată, logăm warning și apelăm callback cu eroare
- **`return`** - Ieșim din metodă, nu continuăm cu GPS

### Sistemul de permisiuni Android (Runtime Permissions):

```
Android 6.0+ (API 23+): Runtime Permissions
    ↓
User instalează aplicația
    ↓
Permisiuni în Manifest, dar NU acordate automat
    ↓
Aplicația cere permisiune (ActivityCompat.requestPermissions)
    ↓
User vede dialog: "Allow location access?"
    ↓
User apasă "Allow" sau "Deny"
    ↓
checkSelfPermission() → GRANTED sau DENIED
```

**IMPORTANT**: Fără permisiune acordată, accesul la GPS aruncă SecurityException!

### Exemplu de cerere permisiune în MainActivity:

```java
private static final int REQUEST_LOCATION = 100;

// Verificare și cerere permisiune:
if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
    != PackageManager.PERMISSION_GRANTED) {
    
    // Cere permisiune utilizatorului
    ActivityCompat.requestPermissions(
        this,
        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
        REQUEST_LOCATION
    );
} else {
    // Permisiune deja acordată, folosim LocationService
    locationService.getCurrentLocationName(callback);
}

// Callback când user răspunde la dialog:
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == REQUEST_LOCATION) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // User a acordat permisiunea
            locationService.getCurrentLocationName(callback);
        } else {
            // User a refuzat permisiunea
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
```

### Obținerea locației curente cu prioritate HIGH_ACCURACY

```java
        Task<Location> locationTask = fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        );
```

**Linie cu linie:**
- **`fusedLocationClient.getCurrentLocation()`** - Obține locația curentă (ONE-TIME, nu continuous)
- **`Priority.PRIORITY_HIGH_ACCURACY`** - Prioritate maximă (folosește GPS pentru precizie)
- **`null`** - CancellationToken (nu e necesar pentru această utilizare)
- **Return**: `Task<Location>` - Operație asincronă care va returna locația

### Prioritățile disponibile:

| Prioritate | Precizie | Sursă | Baterie | Timp | Utilizare |
|------------|----------|-------|---------|------|-----------|
| **PRIORITY_HIGH_ACCURACY** | ±5-10m | GPS | Mare | 1-5s | Navigație, tracking |
| **PRIORITY_BALANCED_POWER_ACCURACY** | ±10-100m | Wi-Fi, Cell | Medie | <1s | Meteo, timezone |
| **PRIORITY_LOW_POWER** | ±1km | Cell | Mică | <1s | Regiune aproximativă |
| **PRIORITY_PASSIVE** | Variabil | Altă aplicație | Zero | Variabil | Opportunistic |

**În această aplicație**: HIGH_ACCURACY pentru numele exact al orașului.

### Listener pentru succes (locație obținută)

```java
        locationTask.addOnSuccessListener(location -> {
            if (location != null) {
                // Convert coordinates to city name
                getCityNameFromLocation(location.getLatitude(), location.getLongitude(), callback);
            } else {
                Log.w(TAG, "Location is null");
                callback.onLocationError("Unable to get current location");
            }
        })
```

**Linie cu linie:**
- **`addOnSuccessListener()`** - Adaugă listener pentru succes (Task-ul s-a terminat cu succes)
- **Lambda expression**: `location -> { ... }`
- **`if (location != null)`** - Verificăm că locația nu e null
  - Poate fi null dacă GPS-ul nu e pornit sau nu are signal
- **`location.getLatitude()`** - Coordonata latitudine (ex: 44.4268)
- **`location.getLongitude()`** - Coordonata longitudine (ex: 26.1025)
- Apelăm `getCityNameFromLocation()` pentru conversie coordonate → oraș
- Dacă location e null, apelăm callback cu eroare

### Listener pentru eșec (locație ne-obținută)

```java
        .addOnFailureListener(e -> {
            Log.e(TAG, "Failed to get location", e);
            callback.onLocationError("Failed to get location: " + e.getMessage());
        });
    }
```

**Linie cu linie:**
- **`addOnFailureListener()`** - Adaugă listener pentru eșec (Task-ul a eșuat)
- **Lambda expression**: `e -> { ... }`
- Logăm eroarea pentru debugging
- Apelăm callback cu mesaj de eroare descriptiv

### Cauze posibile de eșec:

- GPS dezactivat în setări
- Locația nu e disponibilă (indoor, tunel, etc.)
- Google Play Services lipsește sau outdated
- Timeout (locația durează prea mult)
- Permisiuni revocate între timp

### Flow complet al Task API:

```
getCurrentLocationName(callback)
    ↓
Verificare permisiuni → OK
    ↓
fusedLocationClient.getCurrentLocation()
    ↓
[Returnează Task<Location> imediat]
    ↓
GPS lucrează în background (1-5 secunde)
    ↓
┌─────────────────┬─────────────────┐
│ SUCCES          │ EȘEC            │
│ location != null│ Exception       │
│      ↓          │      ↓          │
│ getCityName()   │ onLocationError │
└─────────────────┴─────────────────┘
```

## 11. Metoda getCityNameFromLocation() - Reverse geocoding

Această metodă privată convertește coordonate GPS în numele orașului folosind Geocoder.

### Documentația și signatura metodei

```java
    /**
     * Convert coordinates to city name using Geocoder
     */
    private void getCityNameFromLocation(double latitude, double longitude, LocationCallback callback) {
```

**Linie cu linie:**
- **`private`** - Metodă internă, folosită doar de `getCurrentLocationName()`
- **Parametri**:
  - `double latitude` - Latitudinea (ex: 44.4268)
  - `double longitude` - Longitudinea (ex: 26.1025)
  - `LocationCallback callback` - Pentru returnarea rezultatului

### Apelarea Geocoder pentru reverse geocoding

```java
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
```

**Linie cu linie:**
- **`try`** - Geocoder poate arunca IOException
- **`geocoder.getFromLocation(lat, long, maxResults)`** - Reverse geocoding
  - `latitude, longitude` - Coordonatele de convertit
  - `1` - Numărul maxim de rezultate (vrem doar cel mai bun match)
- **Return**: `List<Address>` - Lista de adrese găsite (de obicei 1 element)

### Ce este un Address?

```java
Address address = addresses.get(0);

// Câmpuri disponibile:
address.getLocality()        → "Bucharest" (oraș)
address.getAdminArea()       → "București" (județ/regiune)
address.getCountryName()     → "Romania" (țară)
address.getThoroughfare()    → "Calea Victoriei" (stradă)
address.getSubThoroughfare() → "10" (număr stradă)
address.getPostalCode()      → "010061" (cod poștal)
address.getLatitude()        → 44.4268
address.getLongitude()       → 26.1025
```

### Verificarea rezultatelor

```java
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
```

**Linie cu linie:**
- Verificăm că lista nu e null ȘI nu e goală
- Extragem primul Address (cel mai relevant)

### Extragerea numelui orașului cu fallback-uri multiple

```java
                String cityName = address.getLocality();
                if (cityName == null || cityName.isEmpty()) {
                    cityName = address.getAdminArea(); // Fallback to admin area
                }
                if (cityName == null || cityName.isEmpty()) {
                    cityName = address.getCountryName(); // Fallback to country
                }
```

**Linie cu linie:**
- **Nivel 1**: `getLocality()` - Orașul (cel mai specific)
  - Exemplu: "Bucharest", "Cluj-Napoca", "Timișoara"
- **Nivel 2 (fallback)**: `getAdminArea()` - Județul/Regiunea
  - Exemplu: "București", "Cluj", "Timiș"
  - Utilizat când localitatea nu e disponibilă (zone rurale)
- **Nivel 3 (fallback)**: `getCountryName()` - Țara
  - Exemplu: "Romania", "Hungary", "Bulgaria"
  - Ultimul resort când nimic altceva nu e disponibil

### De ce fallback-uri multiple?

| Locație | Locality | AdminArea | CountryName | Folosim |
|---------|----------|-----------|-------------|---------|
| București centru | "Bucharest" | "București" | "Romania" | "Bucharest" ✓ |
| Sat mic Sibiu | null | "Sibiu" | "Romania" | "Sibiu" ✓ |
| Mijloc ocean | null | null | null | Error ✗ |
| Frontieră țară | null | null | "Romania" | "Romania" ✓ |

**Strategy**: Încearcă cel mai specific, cade înapoi la cel mai general.

### Apelarea callback-ului cu succes

```java
                if (cityName != null && !cityName.isEmpty()) {
                    Log.d(TAG, "Found city name: " + cityName);
                    callback.onLocationReceived(cityName);
                } else {
                    callback.onLocationError("Could not determine city name");
                }
```

**Linie cu linie:**
- Verificăm că avem un nume de oraș valid (după toate fallback-urile)
- Dacă DA: Logăm succesul și apelăm callback cu orașul
- Dacă NU: Apelăm callback cu eroare (locație în mijlocul oceanului?)

### Gestionarea cazului fără adrese găsite

```java
            } else {
                callback.onLocationError("No address found for location");
            }
```

**Explicație:**
- Geocoder-ul nu a găsit nicio adresă pentru aceste coordonate
- Posibil: coordonate invalide, locație în ocean, serviciu indisponibil

### Gestionarea erorilor Geocoder (IOException)

```java
        } catch (IOException e) {
            Log.e(TAG, "Geocoder error", e);
            callback.onLocationError("Geocoder error: " + e.getMessage());
        }
    }
```

**Linie cu linie:**
- **`catch (IOException e)`** - Prinde erori de rețea/serviciu
- Logăm eroarea pentru debugging
- Apelăm callback cu mesaj de eroare

### Cauze posibile de IOException în Geocoder:

- **No internet** - Geocoder necesită conexiune la server Google
- **Service unavailable** - Serverele Google Maps sunt down
- **Too many requests** - Rate limiting (prea multe request-uri rapid)
- **Timeout** - Request-ul durează prea mult

### Flow complet al reverse geocoding:

```
getCityNameFromLocation(44.4268, 26.1025, callback)
    ↓
geocoder.getFromLocation(44.4268, 26.1025, 1)
    ↓
[Request HTTP la Google Maps API]
    ↓
Response: List<Address>
    ↓
Addresses != null && !empty?
    ↓ YES                    ↓ NO
address = addresses[0]    onLocationError
    ↓
getLocality() → "Bucharest"?
    ↓ YES                    ↓ NO
Return "Bucharest"        getAdminArea() → "București"?
    ↓                         ↓ YES                ↓ NO
onLocationReceived        Return "București"    getCountryName() → "Romania"?
                              ↓                     ↓ YES            ↓ NO
                          onLocationReceived    Return "Romania"  onLocationError
                                                    ↓
                                                onLocationReceived
```

---

## 12. Diagrama completă a fluxului de execuție

```
┌─────────────────────────────────────────────────────────────┐
│                      MainActivity                            │
│  ┌────────────────────────────────────────────────────┐     │
│  │ onCreate()                                          │     │
│  │   locationService = new LocationService(this)      │     │
│  │   locationService.getCurrentLocationName(callback) │     │
│  └────────────────────────────────────────────────────┘     │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│                    LocationService                           │
│  ┌────────────────────────────────────────────────────┐     │
│  │ getCurrentLocationName(callback)                    │     │
│  │   ↓                                                 │     │
│  │ Verificare permisiuni                               │     │
│  │   ↓ OK                        ↓ DENIED              │     │
│  │ GPS request                 onLocationError()       │     │
│  │   ↓                                                 │     │
│  │ fusedLocationClient                                 │     │
│  │   .getCurrentLocation(HIGH_ACCURACY)                │     │
│  └────────────────────────────────────────────────────┘     │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│              Google Play Services (GPS)                      │
│  ┌────────────────────────────────────────────────────┐     │
│  │ GPS Satellite signals                               │     │
│  │ Wi-Fi positioning                                   │     │
│  │ Cell tower triangulation                            │     │
│  │   ↓                                                 │     │
│  │ Fuzed Location: (44.4268, 26.1025)                 │     │
│  └────────────────────────────────────────────────────┘     │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│                    LocationService                           │
│  ┌────────────────────────────────────────────────────┐     │
│  │ onSuccessListener(location)                         │     │
│  │   ↓                                                 │     │
│  │ getCityNameFromLocation(44.4268, 26.1025)          │     │
│  │   ↓                                                 │     │
│  │ geocoder.getFromLocation()                          │     │
│  └────────────────────────────────────────────────────┘     │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│              Google Geocoding API                            │
│  ┌────────────────────────────────────────────────────┐     │
│  │ Reverse geocoding:                                  │     │
│  │ (44.4268, 26.1025) → Address                       │     │
│  │   ↓                                                 │     │
│  │ Address {                                           │     │
│  │   locality: "Bucharest"                             │     │
│  │   adminArea: "București"                            │     │
│  │   country: "Romania"                                │     │
│  │ }                                                   │     │
│  └────────────────────────────────────────────────────┘     │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│                    LocationService                           │
│  ┌────────────────────────────────────────────────────┐     │
│  │ Extragere cityName cu fallback:                     │     │
│  │   1. locality → "Bucharest" ✓                       │     │
│  │   2. adminArea (skip)                               │     │
│  │   3. country (skip)                                 │     │
│  │   ↓                                                 │     │
│  │ callback.onLocationReceived("Bucharest")            │     │
│  └────────────────────────────────────────────────────┘     │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│                      MainActivity                            │
│  ┌────────────────────────────────────────────────────┐     │
│  │ LocationCallback.onLocationReceived("Bucharest")    │     │
│  │   ↓                                                 │     │
│  │ locationManager.setCurrentLocation("Bucharest")     │     │
│  │   ↓                                                 │     │
│  │ spinnerAdapter.notifyDataSetChanged()               │     │
│  │   ↓                                                 │     │
│  │ Spinner shows: "Bucharest (current)"                │     │
│  └────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

---

## 13. Exemplu complet de utilizare în MainActivity

```java
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private LocationService locationService;
    private LocationManager locationManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Inițializare servicii
        locationService = new LocationService(this);
        locationManager = new LocationManager(this);
        
        // Verificare și cerere permisiuni
        checkAndRequestLocationPermission();
    }
    
    private void checkAndRequestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, 
                Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            
            // Cerere permisiune
            ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION_PERMISSION
            );
        } else {
            // Permisiune deja acordată
            getCurrentLocation();
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && 
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permisiune acordată
                getCurrentLocation();
            } else {
                // Permisiune refuzată
                Toast.makeText(this, 
                    "Location permission is required for current location feature", 
                    Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void getCurrentLocation() {
        // Afișăm progress
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Getting current location...");
        progress.show();
        
        // Obținem locația
        locationService.getCurrentLocationName(new LocationService.LocationCallback() {
            @Override
            public void onLocationReceived(String cityName) {
                progress.dismiss();
                
                // Salvăm orașul curent
                locationManager.setCurrentLocation(cityName);
                
                // Adăugăm în listă dacă nu există
                if (!locationManager.hasLocation(cityName)) {
                    locationManager.addLocation(cityName);
                }
                
                // Actualizăm UI
                updateSpinner();
                
                // Încărcăm vremea pentru orașul curent
                loadWeatherForCity(cityName);
                
                Toast.makeText(MainActivity.this, 
                    "Current location: " + cityName, 
                    Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onLocationError(String error) {
                progress.dismiss();
                
                Toast.makeText(MainActivity.this, 
                    "Failed to get location: " + error, 
                    Toast.LENGTH_LONG).show();
                
                Log.e("MainActivity", "Location error: " + error);
            }
        });
    }
    
    private void updateSpinner() {
        List<String> cities = locationManager.getSavedLocations();
        ArrayAdapter<String> adapter = new LocationSpinnerAdapter(
            this, 
            android.R.layout.simple_spinner_item, 
            cities,
            locationManager
        );
        spinner.setAdapter(adapter);
    }
    
    private void loadWeatherForCity(String cityName) {
        // Încărcare date meteo...
    }
}
```

---

## 14. Gestionarea erorilor - Cazuri posibile

### 1. Permisiuni refuzate

```
User → Deny location permission
    ↓
checkSelfPermission() → PERMISSION_DENIED
    ↓
callback.onLocationError("Location permission not granted")
    ↓
MainActivity → Toast: "Permission required"
```

**Soluție**: Explicați utilizatorului de ce e necesară permisiunea și cereți din nou.

### 2. GPS dezactivat

```
User → Location services OFF în Settings
    ↓
fusedLocationClient.getCurrentLocation()
    ↓
Task failure: "Location services disabled"
    ↓
onFailureListener(e)
    ↓
callback.onLocationError("Failed to get location: ...")
```

**Soluție**: Redirecționați utilizatorul la Settings pentru a activa GPS:

```java
Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
startActivity(intent);
```

### 3. Locație indisponibilă (indoor, tunel)

```
fusedLocationClient → location == null
    ↓
onSuccessListener(null)
    ↓
callback.onLocationError("Unable to get current location")
```

**Soluție**: Retry după câteva secunde sau folosiți ultima locație cunoscută.

### 4. Geocoder fără internet

```
geocoder.getFromLocation()
    ↓
IOException: "Network unavailable"
    ↓
catch (IOException e)
    ↓
callback.onLocationError("Geocoder error: Network unavailable")
```

**Soluție**: Verificați conexiunea înainte de geocoding:

```java
ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

if (!isConnected) {
    callback.onLocationError("No internet connection");
    return;
}
```

### 5. Google Play Services lipsește

```
LocationServices.getFusedLocationProviderClient()
    ↓
Exception: "Google Play Services unavailable"
```

**Soluție**: Verificați disponibilitatea Google Play Services:

```java
int resultCode = GoogleApiAvailability.getInstance()
    .isGooglePlayServicesAvailable(context);

if (resultCode != ConnectionResult.SUCCESS) {
    if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
        GoogleApiAvailability.getInstance()
            .getErrorDialog(activity, resultCode, 9000).show();
    } else {
        Toast.makeText(context, "This device is not supported", Toast.LENGTH_LONG).show();
    }
}
```

---

## 15. Best practices și îmbunătățiri posibile

### Îmbunătățiri pentru robustețe:

#### 1. Timeout pentru GPS (evită așteptare infinită)

```java
public void getCurrentLocationName(LocationCallback callback) {
    // ... verificare permisiuni ...
    
    // Timeout de 10 secunde
    Handler timeoutHandler = new Handler(Looper.getMainLooper());
    Runnable timeoutRunnable = () -> {
        callback.onLocationError("Location request timeout");
    };
    
    timeoutHandler.postDelayed(timeoutRunnable, 10000); // 10s
    
    Task<Location> locationTask = fusedLocationClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        null
    );
    
    locationTask.addOnSuccessListener(location -> {
        timeoutHandler.removeCallbacks(timeoutRunnable); // Anulează timeout
        
        if (location != null) {
            getCityNameFromLocation(location.getLatitude(), location.getLongitude(), callback);
        } else {
            callback.onLocationError("Unable to get current location");
        }
    }).addOnFailureListener(e -> {
        timeoutHandler.removeCallbacks(timeoutRunnable); // Anulează timeout
        callback.onLocationError("Failed to get location: " + e.getMessage());
    });
}
```

#### 2. Cache pentru ultima locație cunoscută

```java
public class LocationService {
    private String cachedCityName = null;
    private long cacheTimestamp = 0;
    private static final long CACHE_VALIDITY_MS = 5 * 60 * 1000; // 5 minute
    
    public void getCurrentLocationName(LocationCallback callback) {
        // Verificăm cache-ul
        long now = System.currentTimeMillis();
        if (cachedCityName != null && (now - cacheTimestamp) < CACHE_VALIDITY_MS) {
            Log.d(TAG, "Returning cached location: " + cachedCityName);
            callback.onLocationReceived(cachedCityName);
            return;
        }
        
        // Cache expirat sau inexistent, obținem locație nouă
        // ... rest of code ...
        
        // La succes, actualizăm cache-ul:
        private void getCityNameFromLocation(...) {
            // ... geocoding ...
            if (cityName != null && !cityName.isEmpty()) {
                cachedCityName = cityName;
                cacheTimestamp = System.currentTimeMillis();
                callback.onLocationReceived(cityName);
            }
        }
    }
}
```

#### 3. Fallback la ultima locație cunoscută (fast)

```java
public void getCurrentLocationName(LocationCallback callback) {
    // ... verificare permisiuni ...
    
    // Încearcă mai întâi ultima locație cunoscută (instant)
    fusedLocationClient.getLastLocation()
        .addOnSuccessListener(lastLocation -> {
            if (lastLocation != null) {
                Log.d(TAG, "Using last known location");
                getCityNameFromLocation(
                    lastLocation.getLatitude(), 
                    lastLocation.getLongitude(), 
                    callback
                );
            } else {
                // Nu avem last location, obținem locație curentă (mai lent)
                getCurrentLocationFresh(callback);
            }
        })
        .addOnFailureListener(e -> {
            // Fallback la locație curentă
            getCurrentLocationFresh(callback);
        });
}

private void getCurrentLocationFresh(LocationCallback callback) {
    Task<Location> locationTask = fusedLocationClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        null
    );
    // ... rest of code ...
}
```

#### 4. Retry logic cu backoff

```java
public void getCurrentLocationName(LocationCallback callback) {
    getCurrentLocationWithRetry(callback, 0);
}

private void getCurrentLocationWithRetry(LocationCallback callback, int attemptCount) {
    final int MAX_ATTEMPTS = 3;
    
    if (attemptCount >= MAX_ATTEMPTS) {
        callback.onLocationError("Failed to get location after " + MAX_ATTEMPTS + " attempts");
        return;
    }
    
    // ... GPS request ...
    
    locationTask.addOnFailureListener(e -> {
        Log.w(TAG, "Attempt " + (attemptCount + 1) + " failed, retrying...");
        
        // Exponential backoff: 1s, 2s, 4s
        long delayMs = (long) Math.pow(2, attemptCount) * 1000;
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            getCurrentLocationWithRetry(callback, attemptCount + 1);
        }, delayMs);
    });
}
```

#### 5. Verificare internet înainte de geocoding

```java
private void getCityNameFromLocation(double latitude, double longitude, LocationCallback callback) {
    // Verificare conexiune internet
    if (!isNetworkAvailable()) {
        callback.onLocationError("No internet connection for geocoding");
        return;
    }
    
    try {
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        // ... rest of code ...
    } catch (IOException e) {
        // ...
    }
}

private boolean isNetworkAvailable() {
    ConnectivityManager cm = (ConnectivityManager) 
        context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
}
```

#### 6. Listener pentru updates continue (opțional)

```java
// Pentru tracking în timp real (nu doar one-time):
public void startLocationUpdates(LocationCallback callback) {
    LocationRequest locationRequest = new LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10000 // Update interval: 10 secunde
    )
    .setMinUpdateIntervalMillis(5000) // Fastest: 5 secunde
    .build();
    
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                Location location = locationResult.getLastLocation();
                getCityNameFromLocation(
                    location.getLatitude(), 
                    location.getLongitude(), 
                    callback
                );
            }
        }
    };
    
    fusedLocationClient.requestLocationUpdates(
        locationRequest, 
        locationCallback, 
        Looper.getMainLooper()
    );
}

public void stopLocationUpdates(LocationCallback locationCallback) {
    fusedLocationClient.removeLocationUpdates(locationCallback);
}
```

#### 7. Logging detaliat pentru debugging

```java
private void getCityNameFromLocation(double latitude, double longitude, LocationCallback callback) {
    Log.d(TAG, String.format("Geocoding coordinates: (%.6f, %.6f)", latitude, longitude));
    
    try {
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        
        if (addresses != null && !addresses.isEmpty()) {
            Address address = addresses.get(0);
            
            // Log toate câmpurile disponibile
            Log.d(TAG, "Geocoding result:");
            Log.d(TAG, "  Locality: " + address.getLocality());
            Log.d(TAG, "  AdminArea: " + address.getAdminArea());
            Log.d(TAG, "  Country: " + address.getCountryName());
            Log.d(TAG, "  Full address: " + address.toString());
            
            // ... extract cityName cu fallback ...
        }
    } catch (IOException e) {
        Log.e(TAG, "Geocoder IOException", e);
        callback.onLocationError("Geocoder error: " + e.getMessage());
    }
}
```

---

## 16. Comparație: LocationService vs alternative

### LocationService (Google Play Services) vs LocationManager (Android framework):

| Aspect | LocationService (GPS) | LocationManager (vechi) |
|--------|----------------------|------------------------|
| **API** | Google Play Services | Android framework |
| **Setup** | Simplu (FusedLocationProviderClient) | Complex (LocationManager, Criteria) |
| **Furnizori** | Fuzionat automat | GPS, Network (separat) |
| **Baterie** | Optimizat | Consum mare |
| **Precizie** | Automat cel mai bun | Configurare manuală |
| **Threading** | Task API (async) | Callbacks manual |
| **Recomandat** | ✓ **Best practice** | ✗ Deprecated |

### Geocoder (Android) vs Google Maps Geocoding API:

| Aspect | Geocoder (Android) | Google Maps API |
|--------|-------------------|----------------|
| **Cost** | Gratuit | Pay-per-request (după limită) |
| **Limite** | Niciuna | 40,000 requests/lună gratuit |
| **Internet** | Necesar | Necesar |
| **API Key** | Nu | Da |
| **Precizie** | Bună | Excelentă |
| **Recomandat** | ✓ **Pentru aplicații simple** | Pentru aplicații comerciale |

**Concluzie**: Pentru o aplicație meteo simplă, combinația LocationService + Geocoder este perfectă!

---

## Rezumat

Această clasă oferă acces simplu la locația GPS și geocoding:

### **Scop principal**
- Obținerea numelui orașului din coordonatele GPS
- Interfață simplă bazată pe callback-uri
- Gestionare completă a erorilor și fallback-uri

### **Funcționalități cheie:**
1. **getCurrentLocationName()** - Obține orașul curent (public)
2. **getCityNameFromLocation()** - Reverse geocoding (private)
3. **LocationCallback interface** - Pattern async pentru rezultate

### **Tehnologii utilizate:**
- **FusedLocationProviderClient** - Google Play Services Location API
- **Geocoder** - Android Geocoding Service
- **Task API** - Operații asincrone
- **ActivityCompat** - Verificare permisiuni runtime

### **Pattern-uri implementate:**
- **Callback pattern** - Comunicare asincronă
- **Fallback strategy** - Locality → AdminArea → Country
- **Error handling** - Gestionare granulară pentru toate cazurile
- **Permission checking** - Runtime permissions pentru Android 6.0+

### **Avantaje design:**
✓ **Simplu** - API cu 1 metodă publică  
✓ **Async** - Nu blochează UI-ul  
✓ **Robust** - Fallback-uri multiple pentru geocoding  
✓ **Safe** - Verificare permisiuni, gestionare erori  
✓ **Modern** - Google Play Services (nu LocationManager vechi)  

### **Flow-uri principale:**

**Obținere locație cu succes:**
```
getCurrentLocationName(callback)
    ↓
Verificare permisiuni → OK
    ↓
GPS → (44.4268, 26.1025)
    ↓
Geocoder → "Bucharest"
    ↓
callback.onLocationReceived("Bucharest")
```

**Eroare permisiuni:**
```
getCurrentLocationName(callback)
    ↓
Verificare permisiuni → DENIED
    ↓
callback.onLocationError("Location permission not granted")
```

**Eroare GPS:**
```
GPS request → Failure
    ↓
onFailureListener(e)
    ↓
callback.onLocationError("Failed to get location")
```

**Eroare geocoding:**
```
Geocoder → IOException (no internet)
    ↓
catch (IOException e)
    ↓
callback.onLocationError("Geocoder error")
```

### **Îmbunătățiri posibile:**
- Timeout pentru GPS (10 secunde)
- Cache pentru ultima locație (5 minute validitate)
- Fallback la getLastLocation() (instant)
- Retry logic cu exponential backoff
- Verificare internet înainte de geocoding
- Location updates continue (opțional)
- Logging detaliat pentru debugging

### **Cazuri de utilizare:**
- **MainActivity** - Auto-detect oraș curent la pornire
- **Refresh button** - Update locație la cerere
- **Settings** - Configurare oraș default
- **First run** - Setup inițial automat

Clasa `LocationService` este un exemplu excelent de **wrapper class** bine proiectată: simplifică API-uri complexe (GPS + Geocoding), gestionează cazuri speciale, și oferă interfață intuitivă bazată pe callback-uri!
