# AddressFragment.java - Documentație Linie cu Linie

## Prezentare

`AddressFragment` este un fragment care demonstrează utilizarea **Google Maps** cu **Geocoding** pentru:
- **Afișarea unei hărți** interactive în fragment
- **Geocoding** - conversie din adresă text în coordonate geografice (lat/lng)
- **Personalizarea hărții** - zoom controls, compass, rotation, my location button
- **Tipuri de hartă** - Normal, Satellite, Terrain cu butoane de comutare
- **Marker placement** - afișarea unui marker la adresa geocoded
- **MapView lifecycle** - sincronizare corectă cu lifecycle-ul fragmentului

**Caracteristici:**
- Geocoding pentru adresa "Bucharest, Calea Văcăreşti, nr. 189"
- Butoane pentru schimbarea tipului de hartă (Normal/Satellite/Terrain)
- Controale UI activate (zoom, compass, rotation)
- Gestionare lifecycle MapView (onStart, onResume, onPause, onStop, onDestroy, onLowMemory)
- Fallback la coordonate default dacă geocoding eșuează

**Diferențe față de alte fragmente:**
- Implementează `OnMapReadyCallback` pentru inițializare asincronă hartă
- Gestionează lifecycle MapView explicit în toate metodele lifecycle
- Folosește Geocoder pentru conversie adresă → coordonate
- Nu folosește ViewModel (UI-focused fragment)

## Analiza Linie cu Linie

### Declararea Pachetului

```java
package ro.makore.akrilki_05.ui.address;
```

Declară pachetul pentru feature-ul Address din aplicația de hărți.

---

### Import Address

```java
import android.location.Address;
```

Importă clasa `Address` care reprezintă o adresă geografică cu detalii (lat/lng, strada, orașul, țara, etc.).

---

### Import Geocoder

```java
import android.location.Geocoder;
```

Importă `Geocoder` - serviciu pentru conversie între adrese text și coordonate geografice.

**Funcționalități:**
- `getFromLocationName()` = adresă text → coordonate (geocoding)
- `getFromLocation()` = coordonate → adresă text (reverse geocoding)

---

### Import Bundle

```java
import android.os.Bundle;
```

Importă `Bundle` pentru primirea argumentelor fragmentului și salvarea stării.

---

### Import-uri Layout Inflation

```java
import android.view.LayoutInflater;
```

Importă `LayoutInflater` pentru transformarea XML în obiecte View.

```java
import android.view.View;
```

Importă clasa de bază `View`.

```java
import android.view.ViewGroup;
```

Importă `ViewGroup` - container-ul părinte unde fragmentul va fi atașat.

---

### Import NonNull Annotation

```java
import androidx.annotation.NonNull;
```

Importă adnotarea `@NonNull` pentru validare compile-time.

---

### Import Fragment

```java
import androidx.fragment.app.Fragment;
```

Importă clasa de bază `Fragment`.

---

### Import CameraUpdateFactory

```java
import com.google.android.gms.maps.CameraUpdateFactory;
```

Importă `CameraUpdateFactory` pentru crearea update-urilor de cameră (zoom, pan, tilt).

**Metode:**
- `newLatLngZoom()` = mută camera la coordonate specifice cu zoom level
- `newLatLng()` = mută camera la coordonate fără schimbare zoom
- `zoomIn()`, `zoomOut()` = zoom in/out cu o unitate

---

### Import GoogleMap

```java
import com.google.android.gms.maps.GoogleMap;
```

Importă clasa `GoogleMap` - reprezentarea hărții Google cu toate funcționalitățile.

**Funcționalități:**
- Adăugare markere, polilinii, poligoane
- Schimbare tip hartă (normal, satellite, terrain, hybrid)
- Controale UI (zoom, compass, my location button)
- Gestionare gesturi (zoom, rotate, tilt)

---

### Import OnMapReadyCallback

```java
import com.google.android.gms.maps.OnMapReadyCallback;
```

Importă interfața `OnMapReadyCallback` implementată de fragment pentru a primi notificare când harta este inițializată.

**Metodă:** `onMapReady(GoogleMap map)` apelată asincron când harta este gata de utilizare.

---

### Import LatLng

```java
import com.google.android.gms.maps.model.LatLng;
```

Importă clasa `LatLng` - reprezentare imutabilă a coordonatelor geografice (latitude, longitude).

**Exemplu:** `new LatLng(44.4268, 26.1025)` = București

---

### Import MarkerOptions

```java
import com.google.android.gms.maps.model.MarkerOptions;
```

Importă `MarkerOptions` - builder pentru configurarea markerelor pe hartă.

**Configurații:**
- `position()` = coordonate marker
- `title()` = text afișat la click
- `snippet()` = text suplimentar
- `icon()` = icon personalizat
- `draggable()` = marker poate fi mutat

---

### Import IOException

```java
import java.io.IOException;
```

Importă excepția pentru erori I/O (de exemplu, în geocoding când rețeaua eșuează).

---

### Import List

```java
import java.util.List;
```

Importă interfața `List` folosită pentru rezultatele geocoding-ului.

---

### Import R

```java
import ro.makore.akrilki_05.R;
```

Importă clasa `R` pentru accesarea resurselor (layout, ID-uri, strings).

---

### Import View Binding

```java
import ro.makore.akrilki_05.databinding.FragmentAddressBinding;
```

Importă clasa de binding generată din `fragment_address.xml`.

---

### Declararea Clasei

```java
public class AddressFragment extends Fragment implements OnMapReadyCallback {
```

Declară clasa care moștenește `Fragment` și implementează `OnMapReadyCallback`.

**implements OnMapReadyCallback:**
- Fragmentul trebuie să implementeze `onMapReady(GoogleMap)` 
- Apelată asincron când harta e gata
- Permite configurarea hărții după inițializare

---

### Variabilă Binding

```java
    private FragmentAddressBinding binding;
```

Declară variabila pentru binding-ul layout-ului fragmentului.

---

### Variabilă GoogleMap

```java
    private GoogleMap googleMap;
```

Declară variabila care va reține referința la obiectul GoogleMap după inițializare.

**Lifecycle:**
- `null` înainte de `onMapReady()`
- Setată în `onMapReady()`
- Folosită pentru manipularea hărții (zoom, markere, tip hartă)

---

### Metoda onCreateView - Semnătura

```java
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
```

Metodă de lifecycle apelată când fragmentul trebuie să-și creeze view-ul.

---

### Inflare View Binding

```java
        binding = FragmentAddressBinding.inflate(inflater, container, false);
```

Inflează layout-ul fragmentului folosind View Binding.

**false:** NU atașa view-ul la container (FragmentManager face asta automat).

---

### MapView onCreate

```java
        binding.mapView.onCreate(savedInstanceState);
```

**Explicație detaliată:**

Apelează metoda `onCreate()` a MapView-ului, necesară pentru inițializarea corectă.

**MapView lifecycle:**
- MapView are propriul lifecycle care TREBUIE sincronizat cu lifecycle-ul fragmentului
- Fiecare metodă lifecycle a fragmentului trebuie să apeleze metoda corespunzătoare a MapView

**savedInstanceState:**
- Transmis către MapView pentru restaurarea stării (zoom level, poziție cameră)
- Permite MapView să-și păstreze starea la rotații ecran

**De ce este necesar:**
```
Fragment.onCreate() → Fragment.onCreateView() → MapView.onCreate()
                                                │
                                                └─── Inițializare internă MapView
                                                     └─── Pregătire pentru getMapAsync()
```

---

### MapView getMapAsync

```java
        binding.mapView.getMapAsync(this);
```

**Explicație detaliată:**

Inițializează harta asincron și notifică fragmentul când este gata.

**getMapAsync(OnMapReadyCallback):**
- Încarcă harta pe un thread secundar
- Când harta e gata, apelează `onMapReady()` pe main thread
- **this** = AddressFragment implementează OnMapReadyCallback

**De ce asincron:**
- Încărcarea hărții Google necesită timp (download tile-uri, inițializare GL)
- Evită blocarea UI thread-ului
- Permite afișarea unui loading indicator

**Flux:**
```
getMapAsync(this) → [Background] Inițializare hartă
                          │
                          └─── [Main Thread] onMapReady(GoogleMap)
                               │
                               └─── Hartă gata de utilizare
```

---

### Return Root View

```java
        return binding.getRoot();
```

Returnează view-ul root (care conține MapView-ul și butoanele).

---

### Metoda onMapReady - Semnătura

```java
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
```

**Explicație detaliată:**

Metodă callback apelată când harta Google este complet inițializată și gata de utilizare.

**@NonNull GoogleMap map:** Obiectul GoogleMap complet funcțional (garantat non-null).

**Timing:**
- Apelată asincron după `getMapAsync()`
- Pe main thread
- O singură dată (la prima inițializare)

**Rol:** Configurarea hărții (UI settings, tip hartă, markere, listeners).

---

### Salvare Referință GoogleMap

```java
        googleMap = map;
```

Salvează referința la obiectul GoogleMap în variabila de instanță pentru utilizare ulterioară.

---

### Activare Zoom Controls

```java
        googleMap.getUiSettings().setZoomControlsEnabled(true);
```

Activează butoanele de zoom (+/-) pe hartă.

**getUiSettings():** Returnează obiectul pentru configurarea elementelor UI ale hărții.

**setZoomControlsEnabled(true):** Afișează butoanele +/- în colțul hărții.

---

### Activare My Location Button

```java
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
```

Activează butonul "My Location" care centrează harta pe locația curentă a device-ului.

**Note:** Necesită permission `ACCESS_FINE_LOCATION` sau `ACCESS_COARSE_LOCATION`.

---

### Activare Compass

```java
        googleMap.getUiSettings().setCompassEnabled(true);
```

Activează compasul care apare când harta este rotită.

**Comportament:** Compasul apare doar când harta NU este orientată spre nord.

---

### Activare Rotate Gestures

```java
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
```

Permite rotirea hărții prin gesture cu două degete (twist).

---

### Activare Zoom Gestures

```java
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
```

Permite zoom prin pinch gesture sau double-tap.

---

### Definire Adresă Target

```java
        String address = "Bucharest, Calea Văcăreşti, nr. 189";
```

Definește adresa text care va fi convertită în coordonate geografice.

**Format:** Adresă liberă în format text (nu necesită format strict).

---

### Geocoding - Conversie Adresă în Coordonate

```java
        LatLng addressLatLng = getLocationFromAddress(address);
```

Apelează metoda helper pentru conversie adresă text → coordonate LatLng.

**Rezultat:**
- `LatLng` cu coordonatele adresei dacă geocoding reușește
- `null` dacă adresa nu e găsită sau geocoding eșuează

---

### Verificare Rezultat Geocoding

```java
        if (addressLatLng != null) {
```

Verifică dacă geocoding-ul a reușit (adresa a fost găsită).

---

### Mutare Cameră la Adresă

```java
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addressLatLng, 15));
```

Mută camera hărții la coordonatele adresei cu zoom level 15.

**moveCamera():** Mută camera instant (fără animație).

**CameraUpdateFactory.newLatLngZoom(LatLng, zoom):**
- Primul parametru: coordonatele destinație
- Al doilea parametru: zoom level (1 = world, 20 = buildings)
- 15 = zoom potrivit pentru vizualizare stradă/clădire

**Alternative:**
- `animateCamera()` = mută cu animație smoothly
- `newLatLng()` = mută fără schimbare zoom

---

### Adăugare Marker la Adresă

```java
            googleMap.addMarker(new MarkerOptions().position(addressLatLng).title(address));
```

Adaugă un marker roșu pe hartă la coordonatele adresei.

**new MarkerOptions():** Builder pentru configurare marker.

**.position(addressLatLng):** Setează coordonatele marker-ului.

**.title(address):** Setează titlul afișat când user-ul tapează marker-ul.

**Rezultat:** Marker roșu clasic Google Maps cu info window conținând adresa.

---

### Branch Else - Geocoding Eșuat

```java
        } else {
```

Branch executat când geocoding-ul eșuează (adresa nu e găsită).

---

### Coordonate Default București

```java
            LatLng defaultLatLng = new LatLng(44.4268, 26.1025);
```

Creează coordonate default pentru București (centru oraș).

**44.4268** = latitude (Nord)

**26.1025** = longitude (Est)

---

### Mutare Cameră la Coordonate Default

```java
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 12));
```

Mută camera la coordonatele default București cu zoom 12 (vedere oraș).

**Zoom 12:** Mai mare decât zoom 15, arată o zonă mai mare (oraș întreg vs cartier).

---

### Listener Buton Satellite

```java
        binding.getRoot().findViewById(R.id.satelliteButton).setOnClickListener(v -> googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE));
```

Găsește butonul satelit și setează listener care schimbă tipul hărții la satelit când butonul e apăsat.

**binding.getRoot().findViewById():** Caută butonul în view-ul root al binding-ului.

**Lambda expression:** `v ->` (parameter view ignorat).

**GoogleMap.MAP_TYPE_SATELLITE:** Imagini satelit fără etichete străzi.

---

### Listener Buton Normal

```java
        binding.getRoot().findViewById(R.id.normalButton).setOnClickListener(v -> googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL));
```

Găsește butonul normal și setează listener care schimbă tipul hărții la normal (hartă stradală standard).

**GoogleMap.MAP_TYPE_NORMAL:** Hartă stradală clasică cu străzi, etichete, culori.

---

### Listener Buton Terrain

```java
        binding.getRoot().findViewById(R.id.terrainButton).setOnClickListener(v -> googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN));
```

Găsește butonul terrain și setează listener care schimbă tipul hărții la terrain (relief topografic).

**GoogleMap.MAP_TYPE_TERRAIN:** Hartă cu relief, munți, dealuri, șes.

---

### Setare Tip Hartă Inițial

```java
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
```

Setează tipul de hartă inițial la normal (hartă stradală).

---

### Metoda getLocationFromAddress - Semnătura

```java
    private LatLng getLocationFromAddress(String address) {
```

**Explicație detaliată:**

Metodă helper privată care convertește o adresă text în coordonate geografice (geocoding).

**Parametru address:** String cu adresa în format liber.

**Return:** `LatLng` cu coordonatele sau `null` dacă conversiunea eșuează.

**Proces:** Folosește serviciul Geocoder de la Android pentru căutarea adresei.

---

### Creare Geocoder

```java
        Geocoder geocoder = new Geocoder(requireContext());
```

Creează instanță Geocoder pentru conversii adresă ↔ coordonate.

**requireContext():** Context-ul fragmentului (necesar pentru accesarea serviciilor de sistem).

**Geocoder:** Serviciu Android care folosește backend-ul Google pentru geocoding.

---

### Declarare Lista Rezultate

```java
        List<Address> addressList;
```

Declară variabila care va conține lista de rezultate de la geocoding.

**List<Address>:** Geocoder poate returna multiple rezultate pentru o adresă (de exemplu, mai multe "Main Street" în orașe diferite).

---

### Try Block - Geocoding

```java
        try {
```

Începe bloc try pentru gestionarea erorilor de geocoding (IOException).

---

### Geocoding - getFromLocationName

```java
            addressList = geocoder.getFromLocationName(address, 1);
```

**Explicație detaliată:**

Efectuează geocoding-ul propriu-zis: conversie adresă text → coordonate.

**getFromLocationName(String locationName, int maxResults):**
- Primul parametru: adresa de căutat
- Al doilea parametru: numărul maxim de rezultate dorite (1 = doar primul rezultat)

**Rezultat:** Listă de obiecte `Address`, fiecare conținând:
- Latitude, longitude
- Numele străzii, orașului, țării
- Postal code
- Alte detalii geografice

**Thread:** Rulează sincron pe thread-ul curent (poate cauza ANR pe main thread pentru multe căutări).

---

### Verificare Rezultate Non-Null și Non-Empty

```java
            if (addressList != null && !addressList.isEmpty()) {
```

Verifică dacă geocoding-ul a returnat cel puțin un rezultat.

**addressList != null:** Verifică că lista nu e null (Geocoder poate returna null în anumite condiții).

**!addressList.isEmpty():** Verifică că lista conține cel puțin un rezultat.

---

### Extragere Primul Rezultat

```java
                Address location = addressList.get(0);
```

Extrage primul (și singurul, din cauza maxResults=1) rezultat din listă.

**get(0):** Index 0 = primul element.

---

### Return Coordonate

```java
                return new LatLng(location.getLatitude(), location.getLongitude());
```

Creează și returnează obiect LatLng cu coordonatele extrase din rezultat.

**location.getLatitude():** Latitude-ul adresei.

**location.getLongitude():** Longitude-ul adresei.

---

### Catch IOException

```java
        } catch (IOException e) {
```

Prinde excepțiile I/O aruncate de geocoding (de exemplu, erori de rețea).

**Cauze:**
- Lipsă conexiune internet
- Timeout la serviciul de geocoding
- Erori server Google

---

### Print Stack Trace

```java
            e.printStackTrace();
```

Printează stack trace-ul erorii în logcat pentru debugging.

---

### Return Null pentru Eroare

```java
        return null;
```

Returnează `null` când geocoding-ul eșuează (listă goală sau IOException).

---

### Metoda onStart

```java
    @Override
    public void onStart() {
```

Metodă lifecycle apelată când fragmentul devine vizibil.

---

### Apel Super onStart

```java
        super.onStart();
```

Apelează implementarea din clasa părinte.

---

### MapView onStart

```java
        if (binding.mapView != null) {
            binding.mapView.onStart();
        }
```

**Explicație detaliată:**

Propagă evenimentul `onStart()` către MapView.

**De ce este necesar:**
- MapView trebuie să știe când fragmentul devine vizibil
- Pornește rendering-ul hărții și actualizarea tile-urilor
- Sincronizare lifecycle pentru resource management corect

**Verificare null:** Protecție împotriva crash-urilor dacă binding-ul nu e inițializat.

---

### Metoda onResume

```java
    @Override
    public void onResume() {
```

Metodă lifecycle apelată când fragmentul devine activ și interactiv.

---

### Apel Super onResume

```java
        super.onResume();
```

Apelează implementarea din clasa părinte.

---

### MapView onResume

```java
        if (binding.mapView != null) {
            binding.mapView.onResume();
        }
```

**Explicație detaliată:**

Propagă evenimentul `onResume()` către MapView.

**Efecte:**
- MapView începe rendering activ
- Actualizare continuă a poziției (dacă "My Location" e activat)
- Procesare gesturi user

---

### Metoda onPause

```java
    @Override
    public void onPause() {
```

Metodă lifecycle apelată când fragmentul nu mai este în foreground.

---

### Apel Super onPause

```java
        super.onPause();
```

Apelează implementarea din clasa părinte.

---

### MapView onPause

```java
        if (binding.mapView != null) {
            binding.mapView.onPause();
        }
```

**Explicație detaliată:**

Propagă evenimentul `onPause()` către MapView.

**Efecte:**
- MapView oprește rendering activ
- Oprește actualizări locație
- Economisește baterie și resurse

---

### Metoda onStop

```java
    @Override
    public void onStop() {
```

Metodă lifecycle apelată când fragmentul nu mai este vizibil.

---

### Apel Super onStop

```java
        super.onStop();
```

Apelează implementarea din clasa părinte.

---

### MapView onStop

```java
        if (binding.mapView != null) {
            binding.mapView.onStop();
        }
```

**Explicație detaliată:**

Propagă evenimentul `onStop()` către MapView.

**Efecte:**
- MapView oprește complet rendering-ul
- Eliberează resurse GPU
- Pregătire pentru posibil process death

---

### Metoda onDestroyView

```java
    @Override
    public void onDestroyView() {
```

Metodă lifecycle apelată când view-ul fragmentului este distrus.

---

### Apel Super onDestroyView

```java
        super.onDestroyView();
```

Apelează implementarea din clasa părinte.

---

### MapView onDestroy

```java
        if (binding.mapView != null) {
            binding.mapView.onDestroy();
        }
```

**Explicație detaliată:**

Propagă evenimentul destroy către MapView pentru cleanup complet.

**Efecte:**
- MapView eliberează toate resursele (memorie, GPU, conexiuni)
- Cleanup complet pentru a preveni memory leaks
- **CRITIC:** Trebuie apelat pentru a evita memory leaks

**Diferență față de onStop:**
- `onStop()` = suspend temporar
- `onDestroy()` = cleanup definitiv

---

### Cleanup Binding

```java
        binding = null;
```

Setează binding-ul la null pentru a preveni memory leaks.

---

### Metoda onLowMemory

```java
    @Override
    public void onLowMemory() {
```

**Explicație detaliată:**

Metodă lifecycle apelată când sistemul Android rulează cu memorie insuficientă.

**Când se apelează:**
- Sistemul Android e sub presiune de memorie
- Risc de terminare procese în background
- Înainte de a termina app-ul curent

**Rol:** Permite app-ului să elibereze resurse non-esențiale.

---

### Apel Super onLowMemory

```java
        super.onLowMemory();
```

Apelează implementarea din clasa părinte.

---

### MapView onLowMemory

```java
        if (binding.mapView != null) {
            binding.mapView.onLowMemory();
        }
```

**Explicație detaliată:**

Propagă evenimentul low memory către MapView pentru eliberarea memoriei cache.

**Efecte:**
- MapView eliberează tile-uri cached
- Eliberează resurse GPU non-esențiale
- Reduce memory footprint pentru a evita process death

**Importanță:** Poate preveni terminarea app-ului de către sistem.

---

## Lifecycle MapView - Sincronizare Completă

```
Fragment Lifecycle          MapView Lifecycle
──────────────────          ─────────────────

onCreate()
    │
onCreateView()
    │
    └──> binding.mapView.onCreate(savedInstanceState)
         │
         └──> binding.mapView.getMapAsync(this)
              │
              └──> [Async] Map initialization
                   │
                   └──> onMapReady(GoogleMap) ◄─ Hartă gata
                        │
                        └──> Configurare hartă
                             ├─── UI settings
                             ├─── Tip hartă
                             ├─── Geocoding
                             ├─── Markere
                             └─── Button listeners

onStart()
    │
    └──> binding.mapView.onStart()
         │
         └──> Pornire rendering

onResume()
    │
    └──> binding.mapView.onResume()
         │
         └──> Rendering activ

[Fragment activ, user interacționează]

onPause()
    │
    └──> binding.mapView.onPause()
         │
         └──> Stop rendering activ

onStop()
    │
    └──> binding.mapView.onStop()
         │
         └──> Stop complet rendering

onDestroyView()
    │
    ├──> binding.mapView.onDestroy()
    │    │
    │    └──> Cleanup complet resurse
    │
    └──> binding = null

[Memorie insuficientă în sistem]
    │
    └──> onLowMemory()
         │
         └──> binding.mapView.onLowMemory()
              │
              └──> Eliberare cache tile-uri
```

**CRITIC:** Toate metodele MapView TREBUIE apelate pentru funcționare corectă și evitare memory leaks.

---

## Flux Geocoding

```
User navighează la AddressFragment
    │
    └──> onCreateView()
         │
         └──> binding.mapView.getMapAsync(this)
              │
              └──> [Background] Inițializare hartă
                   │
                   └──> onMapReady(GoogleMap)
                        │
                        ├──> UI settings activate
                        │
                        └──> String address = "Bucharest, Calea Văcăreşti, nr. 189"
                             │
                             └──> LatLng addressLatLng = getLocationFromAddress(address)
                                  │
                                  ├──> Geocoder geocoder = new Geocoder()
                                  │
                                  └──> addressList = geocoder.getFromLocationName(address, 1)
                                       │
                                       ├──> [Network Request la Google]
                                       │    │
                                       │    └──> Căutare adresă în database Google
                                       │
                                       ├──> SUCCESS ────────────────────────┐
                                       │    │                               │
                                       │    └──> List<Address> cu rezultate │
                                       │         │                          │
                                       │         └──> Address location = addressList.get(0)
                                       │              │                     │
                                       │              └──> return new LatLng(lat, lng)
                                       │                   │                │
                                       │                   └──> addressLatLng != null
                                       │                        │           │
                                       │                        ├──> moveCamera(lat/lng, zoom=15)
                                       │                        │           │
                                       │                        └──> addMarker(position, title)
                                       │                                    │
                                       └──> FAILURE ─────────────────────┐  │
                                            │                            │  │
                                            ├──> addressList empty       │  │
                                            │    └──> return null ───────┼──┘
                                            │                            │
                                            └──> IOException             │
                                                 └──> catch              │
                                                      └──> return null ──┘
                                                           │
                                                           └──> addressLatLng == null
                                                                │
                                                                └──> LatLng defaultLatLng = București
                                                                     │
                                                                     └──> moveCamera(default, zoom=12)


[User apasă buton Satellite/Normal/Terrain]
    │
    └──> setOnClickListener()
         │
         └──> googleMap.setMapType(MAP_TYPE_XXX)
              │
              └──> Hartă actualizată cu tipul selectat
```

---

## Tipuri de Hartă Google Maps

| Tip | Constantă | Descriere | Utilizare |
|-----|-----------|-----------|-----------|
| **Normal** | `MAP_TYPE_NORMAL` | Hartă stradală standard | Default, navigare urbană |
| **Satellite** | `MAP_TYPE_SATELLITE` | Imagini satelit fără etichete | Vedere aeriană reală |
| **Terrain** | `MAP_TYPE_TERRAIN` | Relief topografic | Zone montane, dealuri |
| **Hybrid** | `MAP_TYPE_HYBRID` | Satelit + etichete străzi | Combinație (NU folosit aici) |
| **None** | `MAP_TYPE_NONE` | Fără tile-uri | Overlay custom complet |

---

## UI Settings Google Maps

```java
googleMap.getUiSettings() - Returnează UiSettings
    │
    ├──> setZoomControlsEnabled(true)
    │    └──> Butoane +/- pe hartă
    │
    ├──> setMyLocationButtonEnabled(true)
    │    └──> Buton pentru centrare pe locația curentă
    │         (necesită permission location)
    │
    ├──> setCompassEnabled(true)
    │    └──> Compas când harta e rotită
    │         (apare doar când NU e orientată spre nord)
    │
    ├──> setRotateGesturesEnabled(true)
    │    └──> Permite rotire cu două degete (twist)
    │
    └──> setZoomGesturesEnabled(true)
         └──> Permite zoom cu pinch sau double-tap
```

**Alte setări disponibile (NU folosite aici):**
- `setTiltGesturesEnabled()` = permite tilt (perspectivă 3D)
- `setScrollGesturesEnabled()` = permite pan (mutare hartă)
- `setMapToolbarEnabled()` = toolbar cu acțiuni rapide
- `setIndoorLevelPickerEnabled()` = selector etaje pentru clădiri

---

## Geocoding vs Reverse Geocoding

### Geocoding (folosit în acest cod)

```
Adresă Text → Coordonate Geografice

"Bucharest, Calea Văcăreşti, nr. 189"
    │
    └──> Geocoder.getFromLocationName(address, maxResults)
         │
         └──> List<Address>
              │
              └──> LatLng(44.4268, 26.1025)
```

### Reverse Geocoding (NU folosit aici)

```
Coordonate Geografice → Adresă Text

LatLng(44.4268, 26.1025)
    │
    └──> Geocoder.getFromLocation(latitude, longitude, maxResults)
         │
         └──> List<Address>
              │
              └──> "Bucharest, Calea Văcăreşti, nr. 189"
```

---

## Camera Updates - Metode Disponibile

### Folosite în acest cod

```java
// Mutare instant la coordonate cu zoom
CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel)

// Aplicare update
googleMap.moveCamera(cameraUpdate)  // Instant, fără animație
```

### Alternative disponibile (NU folosite aici)

```java
// Mutare cu animație
googleMap.animateCamera(cameraUpdate)  // Smooth animation

// Alte tipuri de updates
CameraUpdateFactory.newLatLng(latLng)        // Fără schimbare zoom
CameraUpdateFactory.zoomIn()                  // Zoom +1
CameraUpdateFactory.zoomOut()                 // Zoom -1
CameraUpdateFactory.zoomTo(zoomLevel)         // Zoom specific
CameraUpdateFactory.scrollBy(xPixel, yPixel)  // Pan cu pixels
```

---

## Marker Options - Configurări

### Folosite în acest cod

```java
new MarkerOptions()
    .position(latLng)    // Coordonate marker
    .title(address)      // Text afișat la click
```

### Opțiuni disponibile (NU folosite aici)

```java
.snippet(String)         // Text suplimentar sub titlu
.icon(BitmapDescriptor)  // Icon personalizat
.draggable(boolean)      // Marker poate fi mutat
.visible(boolean)        // Vizibilitate marker
.alpha(float)            // Transparență (0.0 - 1.0)
.rotation(float)         // Rotație în grade
.anchor(float, float)    // Punct de ancorare
.flat(boolean)           // Marker plat vs 3D billboard
.zIndex(float)           // Ordine în stivă (z-order)
```

---

