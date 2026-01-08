# Arhitectura Aplicației Weather (akrilki_08)

Acest document explică arhitectura aplicației meteo, organizarea claselor în pachete, și motivele din spatele acestei structuri. Documentul este destinat studenților cu cunoștințe de bază în Java, care încep să învețe programarea Android.

## Cuprins

1. [Viziune generală](#viziune-generală)
2. [Structura de pachete](#structura-de-pachete)
3. [De ce atât de multe clase?](#de-ce-atât-de-multe-clase)
4. [Separarea responsabilităților](#separarea-responsabilităților)
5. [Ce impune Java vs. OOP vs. Android](#ce-impune-java-vs-oop-vs-android)
6. [Pattern-uri de design utilizate](#pattern-uri-de-design-utilizate)
7. [Fluxul de date prin aplicație](#fluxul-de-date-prin-aplicație)
8. [Diagrame de arhitectură](#diagrame-de-arhitectură)

---

## Viziune generală

Aplicația meteo este o aplicație Android care afișează prognoza vremii pentru mai multe orașe. 

### Curiozitati:

**Q: "De ce nu am putut scrie totul într-o singură clasă MainActivity?"**

**A:** Teoretic e posibil. Dar acea clasă ar avea peste 2000 de linii de cod, ar fi imposibil de întreținut, imposibil de testat, și ar încălca toate principiile programării orientate pe obiecte. 

**Q: "De ce sunt atât de multe pachete (foldere)?"**

**A:** Pachetele grupează clasele după responsabilitate. Dar în plus, platforma Android forțeaza (sau sugereaza) anumite clase. 

## Clasificarea claselor: OBLIGATORIU vs. ALEGERE

### Tabel complet - Ce te forțează Android/biblioteci vs. ce ai ales tu

| # | Clasă | Linii | OBLIGATORIU? | Motivul real |
|---|-------|-------|--------------|--------------|
| 1 | [`MainActivity`](MainActivity.java.md) | 450 | ✅ **DA - Android** | Android NU permite aplicații fără Activity. Punct. |
| 2 | [`WeatherDetailActivity`](WeatherDetailActivity.java.md) | 180 | ⚠️ **SEMI** | *Poți folosi Fragment, dar Activity e mai simplu pentru un ecran secundar* |
| 3 | [`WeatherApp`](WeatherApp.java.md) | 25 | ✅ **DA - Glide** | Biblioteca Glide TE OBLIGĂ să faci o clasă `AppGlideModule` pentru configurare |
| 4 | [`WeatherAPI`](api/WeatherAPI.java.md) | 120 | ⚠️ **SEMI** | *Poți pune în MainActivity, dar ai 450+120=570 linii → inacceptabil* |
| 5 | [`WeatherParser`](parser/WeatherParser.java.md) | 200 | ⚠️ **SEMI** | *Idem - parsarea JSON în MainActivity ar face 450+200=650 linii* |
| 6 | [`AddLocationDialog`](dialog/AddLocationDialog.java.md) | 280 | ❌ **NU** | *Poți face dialog în MainActivity cu AlertDialog.Builder în 20 linii* |
| 7 | [`DailyWeatherAdapter`](adapter/DailyWeatherAdapter.java.md) | 380 | ✅ **DA - RecyclerView** | RecyclerView TE FORȚEAZĂ să faci o clasă `RecyclerView.Adapter` |
| 8 | [`WeatherAdapter`](adapter/WeatherAdapter.java.md) | 150 | ✅ **DA - RecyclerView** | Al doilea RecyclerView = a doua clasă Adapter obligatorie |
| 9 | [`LocationSpinnerAdapter`](adapter/LocationSpinnerAdapter.java.md) | 80 | ⚠️ **SEMI** | *Poți folosi ArrayAdapter default, dar nu vei avea styling custom* |
| 10 | [`WeatherItem`](model/WeatherItem.java.md) | 250 | ⚠️ **SEMI** | *Poți folosi HashMap, dar Parcelable TE FORȚEAZĂ să faci clasă pentru Intent* |
| 11 | [`DailyWeatherItem`](model/DailyWeatherItem.java.md) | 180 | ❌ **NU** | *Poți grupa WeatherItem direct în ArrayList<List<WeatherItem>>* |
| 12 | [`LocationManager`](util/LocationManager.java.md) | 140 | ❌ **NU** | *Poți accesa SharedPreferences direct din MainActivity* |
| 13 | [`LocationService`](util/LocationService.java.md) | 100 | ❌ **NU** | *Poți folosi FusedLocationProviderClient direct în MainActivity* |



---

## Structura de pachete

Aplicația este organizată în **7 pachete principale**:

```
ro.makore.akrilki_08/
│
├── MainActivity.java              # Activity-ul principal (ecranul principal)
├── WeatherDetailActivity.java    # Activity pentru detalii vreme
├── WeatherApp.java               # Clasa Application
│
├── adapter/                       # Package pentru adaptoare RecyclerView/Spinner
│   ├── DailyWeatherAdapter.java
│   ├── WeatherAdapter.java
│   └── LocationSpinnerAdapter.java
│
├── api/                          # Package pentru comunicare rețea
│   └── WeatherAPI.java
│
├── dialog/                       # Package pentru dialog-uri custom
│   └── AddLocationDialog.java
│
├── model/                        # Package pentru clase de date (POJOs)
│   ├── WeatherItem.java
│   └── DailyWeatherItem.java
│
├── parser/                       # Package pentru parsare JSON
│   └── WeatherParser.java
│
└── util/                         # Package pentru utilitare
    ├── LocationManager.java
    └── LocationService.java
```

### Explicație pachete:

| Pachet | Scop | Motivație |
|--------|------|-----------|
| **root** | Activity-uri și Application | **Android requirement** - Activity-urile trebuie să existe |
| **adapter** | Afișare liste și spinnere | **Android requirement** - RecyclerView și Spinner necesită adaptoare |
| **api** | Comunicare cu serverul | **OOP best practice** - Separare networking de UI |
| **dialog** | Ferestre popup custom | **Android requirement** - Dialog-uri reutilizabile |
| **model** | Clase de date | **OOP best practice** - Separare date de logică |
| **parser** | Parsare JSON | **OOP best practice** - Single Responsibility Principle |
| **util** | Funcții auxiliare | **Java convention** - Cod reutilizabil |

---

## De ce atât de multe clase?

### Total: 13 clase Java

La prima vedere pare mult pentru o aplicație simplă. Să analizăm **de ce fiecare este necesară**:

### 1. Activity-uri (3 clase) - **OBLIGATORIU ANDROID**

| Clasă | Documentație | De ce există? |
|-------|--------------|---------------|
| [MainActivity](MainActivity.java.md) | Main screen | **Android requirement** - Punct de intrare în aplicație |
| [WeatherDetailActivity](WeatherDetailActivity.java.md) | Detail screen | **Android requirement** - Ecran separat pentru detalii |
| [WeatherApp](WeatherApp.java.md) | Application class | **Android best practice** - Configurare globală aplicație |

**De ce nu o singură Activity?**
- Android folosește pattern-ul "un ecran = o Activity"
- Separarea ecranelor facilitează navigarea înapoi cu butonul Back
- Fiecare Activity poate avea lifecycle independent

### 2. Adaptoare (3 clase) - **OBLIGATORIU ANDROID**

| Clasă | Documentație | De ce există? |
|-------|--------------|---------------|
| [DailyWeatherAdapter](adapter/DailyWeatherAdapter.java.md) | RecyclerView pentru zile | **Android requirement** - RecyclerView necesită Adapter |
| [WeatherAdapter](adapter/WeatherAdapter.java.md) | RecyclerView pentru ore | **Android requirement** - RecyclerView necesită Adapter |
| [LocationSpinnerAdapter](adapter/LocationSpinnerAdapter.java.md) | Spinner pentru orașe | **Android requirement** - Spinner custom necesită Adapter |

**De ce nu un singur Adapter universal?**
- Fiecare RecyclerView afișează date diferite (zi vs. oră)
- Layout-urile sunt diferite (grafic vs. listă simplă)
- **Violarea SRP (Single Responsibility Principle)** - un adapter pentru totul ar fi haos

### 3. Model (2 clase) - **OBLIGATORIU OOP**

| Clasă | Documentație | De ce există? |
|-------|--------------|---------------|
| [WeatherItem](model/WeatherItem.java.md) | Vreme pentru 1 oră | **OOP best practice** - Encapsulare date |
| [DailyWeatherItem](model/DailyWeatherItem.java.md) | Vreme pentru 1 zi | **OOP best practice** - Agregare date |

**De ce nu HashMap<String, Object> sau JSON direct?**
- **Type safety** - Compilatorul detectează erori (nu runtime crashes)
- **Encapsulation** - Câmpurile private cu getters/setters
- **Parcelable** - Necesar pentru transmitere între Activity-uri (Android requirement)
- **Code completion** - IDE-ul știe ce câmpuri există

**Exemplu fără clase model (RĂU):**
```java
// RĂU - HashMap generic, fără type safety
HashMap<String, Object> weather = new HashMap<>();
weather.put("temperature", 23.5);
weather.put("city", "Bucharest");

// Eroare de tip detectată doar la runtime!
String temp = (String) weather.get("temperature"); // ClassCastException!
```

**Exemplu cu clase model (BINE):**
```java
// BINE - Clasă model cu type safety
WeatherItem weather = new WeatherItem();
weather.setTemperature(23.5);
weather.setCityName("Bucharest");

// Eroare detectată la compilare!
String temp = weather.getTemperature(); // Compilation error: double cannot be String
```

### 4. Utilitare (2 clase) - **OBLIGATORIU OOP + ANDROID**

| Clasă | Documentație | De ce există? |
|-------|--------------|---------------|
| [LocationManager](util/LocationManager.java.md) | Persistență orașe | **OOP best practice** - Separare persistență de UI |
| [LocationService](util/LocationService.java.md) | GPS și geocoding | **OOP best practice** - Wrapper pentru API complex |

**De ce nu direct în MainActivity?**
- **Reusability** - Pot fi folosite din multiple Activity-uri
- **Testability** - Pot fi testate independent
- **Single Responsibility** - MainActivity nu trebuie să știe despre SharedPreferences sau GPS

### 5. API (1 clasă) - **OBLIGATORIU OOP**

| Clasă | Documentație | De ce există? |
|-------|--------------|---------------|
| [WeatherAPI](api/WeatherAPI.java.md) | HTTP requests | **OOP best practice** - Separare networking |

**De ce nu direct în MainActivity?**
- **Separation of Concerns** - UI nu trebuie să știe despre HTTP
- **Centralizare** - Un singur loc pentru toate API calls
- **Error handling** - Gestionare centralizată a erorilor de rețea

### 6. Parser (1 clasă) - **OBLIGATORIU OOP**

| Clasă | Documentație | De ce există? |
|-------|--------------|---------------|
| [WeatherParser](parser/WeatherParser.java.md) | JSON → Objects | **OOP best practice** - Single Responsibility Principle |

**De ce nu direct în MainActivity sau WeatherAPI?**
- **Single Responsibility** - O clasă face DOAR parsare
- **Complexity management** - Parsarea JSON este complexă (400+ linii)
- **Testability** - Poate fi testată independent cu JSON mock

### 7. Dialog (1 clasă) - **OBLIGATORIU ANDROID**

| Clasă | Documentație | De ce există? |
|-------|--------------|---------------|
| [AddLocationDialog](dialog/AddLocationDialog.java.md) | Dialog pentru adăugare oraș | **Android best practice** - Dialog reutilizabil |

**De ce nu direct în MainActivity?**
- **Reusability** - Poate fi folosit din multiple locații
- **Complexity** - Dialog-ul are logică complexă (debouncing, validare online)
- **Lifecycle management** - Dialog-urile au lifecycle separat

---

## Separarea responsabilităților

### Principiul Single Responsibility (SRP)

Fiecare clasă are **o singură responsabilitate**, un singur motiv să se schimbe.

#### Exemplu: Flow de la API la UI

```
1. MainActivity
   Responsabilitate: Coordonare generală, gestionare UI

2. WeatherAPI
   Responsabilitate: Comunicare HTTP cu serverul

3. WeatherParser
   Responsabilitate: Transformare JSON în obiecte Java

4. WeatherItem / DailyWeatherItem
   Responsabilitate: Reprezentare date

5. DailyWeatherAdapter
   Responsabilitate: Afișare date în RecyclerView

6. LocationManager
   Responsabilitate: Salvare/citire orașe din SharedPreferences
```

**Fără separare (tot în MainActivity):**
```java
// RĂU - MainActivity face TOTUL (anti-pattern "God Object")
public class MainActivity extends AppCompatActivity {
    
    // HTTP request
    private String fetchWeatherFromAPI(String city) { ... }
    
    // JSON parsing
    private List<WeatherItem> parseJSON(String json) { ... }
    
    // SharedPreferences
    private void saveCity(String city) { ... }
    private List<String> loadCities() { ... }
    
    // RecyclerView adapter
    class WeatherAdapter extends RecyclerView.Adapter { ... }
    
    // GPS
    private void getGPSLocation() { ... }
    
    // Dialog
    class AddCityDialog extends Dialog { ... }
    
    // ... 2000+ linii de cod
}
```

**Probleme cu acest approach:**
- **Unmaintainable** - Imposibil de citit și înțeles
- **Untestable** - Nu poți testa individual GPS, API, etc.
- **Code reuse impossible** - Nu poți reutiliza API-ul în altă Activity
- **Merge conflicts** - Dacă 5 developeri lucrează, toți modifică aceeași clasă
- **Violation of SRP** - MainActivity are 10+ responsabilități

---

## Ce impune Java vs. OOP vs. Android

### Tabel comparativ:

| Clasă/Concept | Java Requirement | OOP Best Practice | Android Requirement |
|---------------|------------------|-------------------|---------------------|
| **MainActivity** | ✓ (clasă Java) | — | ✓ (extends Activity) |
| **WeatherDetailActivity** | ✓ | — | ✓ (extends Activity) |
| **WeatherApp** | ✓ | — | ✓ (extends Application) |
| **DailyWeatherAdapter** | ✓ | — | ✓ (extends RecyclerView.Adapter) |
| **WeatherAdapter** | ✓ | — | ✓ (extends RecyclerView.Adapter) |
| **LocationSpinnerAdapter** | ✓ | — | ✓ (extends ArrayAdapter) |
| **WeatherItem** | ✓ | ✓ (POJO, encapsulation) | ✓ (implements Parcelable) |
| **DailyWeatherItem** | ✓ | ✓ (POJO, encapsulation) | ✓ (implements Parcelable) |
| **WeatherAPI** | ✓ | ✓ (SRP, separation) | — |
| **WeatherParser** | ✓ | ✓ (SRP, static utility) | — |
| **LocationManager** | ✓ | ✓ (SRP, persistence) | — |
| **LocationService** | ✓ | ✓ (SRP, wrapper) | — |
| **AddLocationDialog** | ✓ | ✓ (reusability) | ✓ (extends Dialog) |
| **Pachete (7)** | ✓ (organizare) | ✓ (modularity) | ✓ (naming convention) |

### Explicații detaliate:

#### 1. Java Requirements (TOATE clasele)

**Java impune:**
- Fiecare clasă publică trebuie în fișier separat cu același nume
- Organizare în pachete pentru evitarea conflictelor de nume

**Nu poți avea în Java:**
```java
// ILEGAL în Java - două clase publice în același fișier
public class MainActivity { ... }
public class WeatherAPI { ... }  // Compilation error!
```

#### 2. OOP Best Practices

**OOP impune (prin principii SOLID):**

##### S - Single Responsibility Principle
- **WeatherAPI** - doar HTTP requests
- **WeatherParser** - doar parsare JSON
- **LocationManager** - doar persistență orașe

##### O - Open/Closed Principle
- **WeatherItem** - poate fi extins fără modificare (subclase)

##### L - Liskov Substitution Principle
- Toate **Adapter**-urile pot înlocui baza RecyclerView.Adapter

##### I - Interface Segregation Principle
- **LocationCallback** - interfață mică, specifică (nu god interface)
- **OnLocationAddedListener** - interfață cu o singură metodă

##### D - Dependency Inversion Principle
- **MainActivity** depinde de abstracții (LocationManager), nu implementări concrete

#### 3. Android Requirements

**Android Framework impune:**

##### Activity-uri (obligatoriu pentru UI)
```java
// OBLIGATORIU - Trebuie să extinzi Activity pentru ecrane
public class MainActivity extends AppCompatActivity { ... }
```

**De ce?** Android gestionează lifecycle-ul (onCreate, onPause, onDestroy, etc.)

##### Adaptoare (obligatoriu pentru liste)
```java
// OBLIGATORIU - RecyclerView necesită Adapter
public class DailyWeatherAdapter extends RecyclerView.Adapter { ... }
```

**De ce?** RecyclerView nu știe cum să afișeze datele tale custom. Adapter-ul face "traducerea".

##### Parcelable (obligatoriu pentru transmitere date)
```java
// OBLIGATORIU - Pentru a trimite obiecte prin Intent
public class WeatherItem implements Parcelable { ... }
```

**De ce?** Android transmite date între Activity-uri prin serializare. Parcelable este optimizat pentru Android (10x mai rapid decât Serializable).

##### Application class (optional dar best practice)
```java
// BEST PRACTICE - Configurare globală
public class WeatherApp extends Application { ... }
```

**De ce?** Inițializare libraries, configurare logger, etc. - o singură dată la pornirea aplicației.

---

## Pattern-uri de design utilizate

Aplicația folosește mai multe pattern-uri de design consacrate:

### 1. **MVC (Model-View-Controller)** - Arhitectură generală

```
MODEL               VIEW                CONTROLLER
─────               ────                ──────────
WeatherItem    →    XML layouts    ←    MainActivity
DailyWeatherItem    RecyclerView        WeatherDetailActivity
                    
LocationManager                          WeatherAPI
                                        WeatherParser
```

**De ce MVC?**
- **Separation of Concerns** - UI separat de date separat de logică
- **Testability** - Poți testa Model-ul fără UI
- **Reusability** - Același Model poate fi folosit cu UI diferit

### 2. **Adapter Pattern** - Pentru RecyclerView și Spinner

```
RecyclerView (Android)
    ↓
[ADAPTER PATTERN]
    ↓
DailyWeatherAdapter (Custom)
    ↓
List<DailyWeatherItem> (Datele noastre)
```

**De ce Adapter?**
- RecyclerView este generic (nu știe nimic despre vreme)
- Adapter-ul "adaptează" datele noastre la interfața RecyclerView
- **Gang of Four pattern** - Permite comunicarea între interfețe incompatibile

### 3. **Singleton Pattern** - LocationManager (implicit)

```java
// Implicit singleton prin ViewModel sau instanță unică în MainActivity
private LocationManager locationManager;

@Override
protected void onCreate(Bundle savedInstanceState) {
    locationManager = new LocationManager(this); // O singură instanță
}
```

**De ce Singleton (informal)?**
- O singură sursă de adevăr pentru orașe salvate
- Evită conflicte când scriem în SharedPreferences

### 4. **Factory Pattern** - Parcelable CREATOR

```java
// În WeatherItem.java
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

**De ce Factory?**
- Android Framework necesită acest pattern pentru Parcelable
- Centralizează logica de creare obiecte din Parcel

### 5. **Observer Pattern (Callback)** - Pentru operații async

```java
// În LocationService.java
public interface LocationCallback {
    void onLocationReceived(String cityName);
    void onLocationError(String error);
}

// Utilizare:
locationService.getCurrentLocationName(new LocationCallback() {
    @Override
    public void onLocationReceived(String cityName) {
        // Update UI
    }
    
    @Override
    public void onLocationError(String error) {
        // Show error
    }
});
```

**De ce Observer/Callback?**
- GPS și geocoding sunt operații asincrone (durează secunde)
- Nu poți returna direct (ar bloca UI-ul)
- Pattern-ul Observer permite notificare când operația se termină

### 6. **ViewHolder Pattern** - În Adaptoare

```java
// În DailyWeatherAdapter.java
public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView cityName;
    LineChart chart;
    // ... alte view-uri
    
    public ViewHolder(View itemView) {
        super(itemView);
        cityName = itemView.findViewById(R.id.city_name);
        chart = itemView.findViewById(R.id.chart);
    }
}
```

**De ce ViewHolder?**
- **Performance** - findViewById() este lent, se apelează o singură dată
- **Android requirement** - RecyclerView necesită ViewHolder pentru recycling
- Fără ViewHolder, scrolling-ul ar fi laggy

### 7. **Strategy Pattern** - Pentru fallback-uri

```java
// În LocationService.java - fallback strategy pentru geocoding
String cityName = address.getLocality();       // Strategy 1: City
if (cityName == null || cityName.isEmpty()) {
    cityName = address.getAdminArea();         // Strategy 2: Region
}
if (cityName == null || cityName.isEmpty()) {
    cityName = address.getCountryName();       // Strategy 3: Country
}
```

**De ce Strategy?**
- Încercăm multiple strategii până găsim una care funcționează
- Fallback graceful (nu crash-uri)

---

## Fluxul de date prin aplicație

### Scenario 1: Pornirea aplicației

```
┌─────────────────────────────────────────────────────────┐
│ 1. User pornește aplicația                              │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ 2. Android creează WeatherApp                           │
│    [WeatherApp.java](WeatherApp.java.md)               │
│    - Inițializare ThreeTenABP (date/time library)      │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ 3. Android creează MainActivity                         │
│    [MainActivity.java](MainActivity.java.md)            │
│    - onCreate() se apelează                             │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ 4. MainActivity inițializează LocationManager           │
│    [LocationManager.java](util/LocationManager.java.md) │
│    - Citește orașe salvate din SharedPreferences        │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ 5. MainActivity populează Spinner cu orașe              │
│    [LocationSpinnerAdapter.java](adapter/...md)        │
│    - Afișează lista de orașe în dropdown                │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ 6. MainActivity încarcă vremea pentru primul oraș       │
│    - loadWeatherData(cityName)                          │
└─────────────────────────────────────────────────────────┘
```

### Scenario 2: Încărcarea datelor meteo

```
┌─────────────────────────────────────────────────────────┐
│ MainActivity.loadWeatherData("Bucharest")               │
│ [MainActivity.java](MainActivity.java.md)               │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ WeatherAPI.fetchWeather(context, "Bucharest")          │
│ [WeatherAPI.java](api/WeatherAPI.java.md)              │
│ - HTTP GET request către OpenWeatherMap                 │
│ - Returnează JSON string                                │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ WeatherParser.parseWeather(json)                        │
│ [WeatherParser.java](parser/WeatherParser.java.md)     │
│ - Parsează JSON → List<WeatherItem>                    │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ WeatherParser.parseWeatherByDay(items)                 │
│ [WeatherParser.java](parser/WeatherParser.java.md)     │
│ - Grupează 40 ore → 5 zile                             │
│ - Returnează List<DailyWeatherItem>                    │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ DailyWeatherAdapter.setData(dailyItems)                │
│ [DailyWeatherAdapter.java](adapter/...md)              │
│ - Afișează grafice cu temperaturi în RecyclerView      │
└─────────────────────────────────────────────────────────┘
```

### Scenario 3: Adăugarea unui oraș nou

```
┌─────────────────────────────────────────────────────────┐
│ 1. User apasă butonul "+" în MainActivity               │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ 2. MainActivity afișează AddLocationDialog              │
│    [AddLocationDialog.java](dialog/...md)              │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ 3. User tastează "Cluj-Napoca"                         │
│    - TextWatcher detectează schimbarea                  │
│    - Debouncing: Așteaptă 600ms                        │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ 4. AddLocationDialog.checkLocationAvailability()       │
│    - Verificare locală: LocationManager.hasLocation()  │
│    - Verificare online: WeatherAPI.fetchWeather()      │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ 5. Oraș valid → Butonul "Add" devine activ             │
│    User apasă "Add"                                     │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ 6. LocationManager.addLocation("Cluj-Napoca")          │
│    [LocationManager.java](util/LocationManager.java.md)│
│    - Salvează în SharedPreferences (JSON)              │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ 7. Callback: onLocationAdded("Cluj-Napoca")            │
│    MainActivity actualizează Spinner-ul                 │
└─────────────────────────────────────────────────────────┘
```

### Scenario 4: Click pe o zi → Detalii

```
┌─────────────────────────────────────────────────────────┐
│ 1. User click pe o zi în RecyclerView                  │
│    DailyWeatherAdapter.onBindViewHolder()              │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ 2. Click listener creează Intent                       │
│    Intent intent = new Intent(context,                  │
│                   WeatherDetailActivity.class);         │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ 3. Transmitere DailyWeatherItem prin Intent            │
│    intent.putExtra("daily_item", dailyItem);           │
│    - Folosește Parcelable pentru serializare           │
│    - dailyItem.writeToParcel() este apelat             │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ 4. Android creează WeatherDetailActivity               │
│    [WeatherDetailActivity.java](...md)                 │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ 5. WeatherDetailActivity extrage DailyWeatherItem      │
│    DailyWeatherItem item = getIntent()                 │
│         .getParcelableExtra("daily_item");             │
│    - CREATOR.createFromParcel() este apelat            │
└────────────────────┬────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────┐
│ 6. WeatherAdapter afișează lista orară                 │
│    [WeatherAdapter.java](adapter/WeatherAdapter.java.md)│
│    - RecyclerView cu toate orele din zi                │
└─────────────────────────────────────────────────────────┘
```

---

## Diagrame de arhitectură

### Diagrama 1: Organizarea claselor pe straturi (Layered Architecture)

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│                         (UI)                                 │
│  ┌────────────────┐  ┌──────────────────┐  ┌─────────────┐ │
│  │  MainActivity  │  │WeatherDetail     │  │ WeatherApp  │ │
│  │               │  │Activity          │  │             │ │
│  └────────────────┘  └──────────────────┘  └─────────────┘ │
│                                                              │
│  ┌────────────────┐  ┌──────────────────┐  ┌─────────────┐ │
│  │AddLocationDialog│ │DailyWeather      │  │ Weather     │ │
│  │               │  │Adapter           │  │ Adapter     │ │
│  └────────────────┘  └──────────────────┘  └─────────────┘ │
│                                                              │
│  ┌────────────────┐                                         │
│  │LocationSpinner │                                         │
│  │Adapter         │                                         │
│  └────────────────┘                                         │
└──────────────────────────────────────────────────────────────┘
                            ↓ ↑
┌─────────────────────────────────────────────────────────────┐
│                     BUSINESS LOGIC LAYER                     │
│                    (Domain Logic)                            │
│  ┌────────────────┐  ┌──────────────────┐                  │
│  │ WeatherParser  │  │ LocationService  │                  │
│  │               │  │                  │                  │
│  └────────────────┘  └──────────────────┘                  │
└──────────────────────────────────────────────────────────────┘
                            ↓ ↑
┌─────────────────────────────────────────────────────────────┐
│                      DATA LAYER                              │
│                (Data Access & Models)                        │
│  ┌────────────────┐  ┌──────────────────┐  ┌─────────────┐ │
│  │ WeatherItem    │  │DailyWeatherItem  │  │LocationMgr  │ │
│  │ (Model)        │  │(Model)           │  │(Persistence)│ │
│  └────────────────┘  └──────────────────┘  └─────────────┘ │
│                                                              │
│  ┌────────────────┐                                         │
│  │ WeatherAPI     │                                         │
│  │ (Network)      │                                         │
│  └────────────────┘                                         │
└──────────────────────────────────────────────────────────────┘
                            ↓ ↑
┌─────────────────────────────────────────────────────────────┐
│                    EXTERNAL SERVICES                         │
│  ┌────────────────┐  ┌──────────────────┐  ┌─────────────┐ │
│  │OpenWeatherMap  │  │Google Play       │  │SharedPrefs  │ │
│  │API             │  │Services (GPS)    │  │(Storage)    │ │
│  └────────────────┘  └──────────────────┘  └─────────────┘ │
└──────────────────────────────────────────────────────────────┘
```

**Explicație straturi:**

1. **PRESENTATION LAYER** - Tot ce vede utilizatorul
   - Activity-uri, Dialog-uri, Adaptoare
   - Responsabilitate: Afișare și interacțiune

2. **BUSINESS LOGIC LAYER** - Logica aplicației
   - Parser, LocationService
   - Responsabilitate: Procesare date, validări, coordonare

3. **DATA LAYER** - Date și persistență
   - Model classes, LocationManager, WeatherAPI
   - Responsabilitate: Acces date (rețea, storage, memorie)

4. **EXTERNAL SERVICES** - Servicii externe
   - API-uri, GPS, SharedPreferences
   - Responsabilitate: Furnizare date brute

**De ce straturi?**
- **Separation of Concerns** - Fiecare strat are un scop clar
- **Testability** - Poți testa fiecare strat independent
- **Maintainability** - Modificări într-un strat nu afectează altele
- **Scalability** - Poți adăuga funcționalități fără refactoring major

### Diagrama 2: Dependențele între clase

```
                    WeatherApp
                        │
                        ↓
              ┌─────────────────────┐
              │   MainActivity      │
              └─────────────────────┘
                        │
        ┌───────────────┼───────────────┐
        ↓               ↓               ↓
┌──────────────┐ ┌─────────────┐ ┌────────────┐
│LocationMgr   │ │WeatherAPI   │ │DailyWeather│
│              │ │             │ │Adapter     │
└──────────────┘ └─────────────┘ └────────────┘
        │               │               │
        │               ↓               ↓
        │        ┌─────────────┐ ┌────────────┐
        │        │WeatherParser│ │DailyWeather│
        │        │             │ │Item        │
        │        └─────────────┘ └────────────┘
        │               │               │
        │               ↓               ↓
        │        ┌─────────────┐ ┌────────────┐
        │        │WeatherItem  │ │WeatherItem │
        │        │             │ │(List)      │
        │        └─────────────┘ └────────────┘
        │
        ↓
┌──────────────┐
│AddLocation   │
│Dialog        │
└──────────────┘
        │
        ↓
┌──────────────┐
│LocationMgr   │
│              │
└──────────────┘


              WeatherDetailActivity
                        │
        ┌───────────────┴───────────────┐
        ↓                               ↓
┌──────────────┐              ┌────────────┐
│WeatherAdapter│              │DailyWeather│
│              │              │Item        │
└──────────────┘              └────────────┘
        │                               │
        ↓                               ↓
┌──────────────┐              ┌────────────┐
│WeatherItem   │              │WeatherItem │
│              │              │(List)      │
└──────────────┘              └────────────┘
```

**Legendă săgeți:**
- `→` : "folosește" / "depinde de"
- `↓` : "creează instanțe de"

### Diagrama 3: Pattern-uri de comunicare

```
┌─────────────────────────────────────────────────────────┐
│  SYNCHRONOUS COMMUNICATION (Direct calls)                │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  MainActivity ──────→ LocationManager.getSavedLocations()│
│       │                      ↓                           │
│       │              return List<String>                 │
│       │←─────────────────────┘                           │
│                                                          │
│  MainActivity ──────→ WeatherParser.parseWeather(json)  │
│       │                      ↓                           │
│       │              return List<WeatherItem>            │
│       │←─────────────────────┘                           │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│  ASYNCHRONOUS COMMUNICATION (Callbacks)                  │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  MainActivity ──→ LocationService.getCurrentLocationName│
│       │               (callback)                         │
│       │                   ↓                              │
│       │           [GPS în background]                    │
│       │                   ↓                              │
│       │           callback.onLocationReceived(city)      │
│       │←──────────────────┘                              │
│       ↓                                                  │
│  Update UI                                               │
│                                                          │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│  EVENT-DRIVEN COMMUNICATION (Listeners)                  │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  AddLocationDialog ──→ listener.onLocationAdded(city)   │
│                               ↓                          │
│                        MainActivity                      │
│                               ↓                          │
│                        Update Spinner                    │
│                                                          │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│  INTENT-BASED COMMUNICATION (Android IPC)                │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  MainActivity ──→ Intent + Parcelable                   │
│       │               ↓                                  │
│       │       [Android Framework]                        │
│       │               ↓                                  │
│       └──→ WeatherDetailActivity                        │
│                   ↓                                      │
│           getIntent().getParcelableExtra()               │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

---

## Concluzie: De ce această arhitectură?

### Răspunsuri la întrebări frecvente:

#### Q1: "Nu era mai simplu cu mai puține clase?"

**A:** Pe termen scurt, da. Pe termen lung, **categoric NU**.

**Scenarii reale:**

**Scenariul 1: Bug în parsarea JSON**
- **Cu arhitectură separată**: Modifici doar `WeatherParser.java` (1 fișier)
- **Fără separare**: Cauți prin 2000 linii în `MainActivity.java` și riști să strici altceva

**Scenariul 2: Schimbi API-ul de vreme**
- **Cu arhitectură separată**: Modifici doar `WeatherAPI.java` (1 fișier)
- **Fără separare**: Refactorizare completă, risc mare de bug-uri

**Scenariul 3: Test automat pentru parsare**
- **Cu arhitectură separată**: 
  ```java
  @Test
  public void testParsing() {
      String mockJSON = "...";
      List<WeatherItem> items = WeatherParser.parseWeather(mockJSON);
      assertEquals(40, items.size());
  }
  ```
- **Fără separare**: Imposibil de testat fără să rulezi toată aplicația

#### Q2: "De ce atâtea pachete (foldere)?"

**A:** Pentru a găsi rapid ce cauți.

**Exemplu real:**
- Bug în afișarea graficului → Știi că e în `adapter/DailyWeatherAdapter.java`
- Bug la salvarea orașelor → Știi că e în `util/LocationManager.java`
- Bug la parsare JSON → Știi că e în `parser/WeatherParser.java`

**Fără pachete:** Toate clasele în același folder, cauți prin 13 fișiere fără indiciu.

#### Q3: "Ce pattern este cel mai important?"

**A:** **Single Responsibility Principle (SRP)**

Fiecare clasă face **un singur lucru**, bine:
- `WeatherAPI` - doar HTTP requests
- `WeatherParser` - doar parsare JSON
- `LocationManager` - doar persistență orașe
- `DailyWeatherAdapter` - doar afișare RecyclerView

**Beneficii SRP:**
1. **Ușor de înțeles** - O clasă = un concept simplu
2. **Ușor de testat** - Testezi o singură responsabilitate
3. **Ușor de modificat** - Modificări izolate, fără side effects
4. **Ușor de reutilizat** - Poți folosi `WeatherAPI` în alt proiect

#### Q4: "Ce am învățat despre arhitectură Android?"

**A:** Lecții cheie:

1. **Android impune structura**
   - Activity-uri pentru ecrane
   - Adaptoare pentru liste
   - Parcelable pentru transmitere date

2. **Java impune organizarea**
   - O clasă publică = un fișier
   - Pachete pentru evitarea conflictelor

3. **OOP impune separarea**
   - Single Responsibility
   - Separation of Concerns
   - Dependency Inversion

4. **Best practices impun pattern-uri**
   - MVC pentru arhitectură
   - Adapter pentru RecyclerView
   - Callback pentru async operations
   - ViewHolder pentru performance

### Comparație: Aplicație simplă vs. Aplicație profesională

| Aspect | Aplicație simplă (1-2 clase) | Aplicație profesională (13 clase) |
|--------|-------------------------------|-------------------------------------|
| **Linii de cod/clasă** | 2000+ linii | 100-400 linii |
| **Timp pentru bug fix** | Ore (cauți prin tot codul) | Minute (știi unde e bug-ul) |
| **Posibilitate testare** | Imposibil (totul legat) | Ușor (clase independente) |
| **Lucru în echipă** | Conflict permanent (toți modifică aceeași clasă) | Fără conflicte (clase separate) |
| **Reutilizare cod** | Imposibil (totul în MainActivity) | Ușor (WeatherAPI refolosibil) |
| **Citire cod** | 2 ore pentru înțelegere | 15 minute (citești doar ce te interesează) |
| **Schimbare UI** | Risc mare (UI legat de logică) | Fără risc (UI separat) |
| **Schimbare API** | Refactorizare totală | Modifici 1 clasă |

### Lecția finală:

**"Good architecture is not about writing more code. It's about writing code that you (and others) can understand, modify, and maintain 6 months from now without pulling your hair out."**

---

## Resurse suplimentare

### Documentație clase (în ordine alfabetică):

**Activities:**
- [MainActivity.java](MainActivity.java.md) - Ecranul principal
- [WeatherApp.java](WeatherApp.java.md) - Application class
- [WeatherDetailActivity.java](WeatherDetailActivity.java.md) - Ecran detalii

**Adapters:**
- [DailyWeatherAdapter.java](adapter/DailyWeatherAdapter.java.md) - RecyclerView pentru zile
- [LocationSpinnerAdapter.java](adapter/LocationSpinnerAdapter.java.md) - Spinner pentru orașe
- [WeatherAdapter.java](adapter/WeatherAdapter.java.md) - RecyclerView pentru ore

**API:**
- [WeatherAPI.java](api/WeatherAPI.java.md) - HTTP requests către OpenWeatherMap

**Dialog:**
- [AddLocationDialog.java](dialog/AddLocationDialog.java.md) - Dialog adăugare oraș

**Model:**
- [DailyWeatherItem.java](model/DailyWeatherItem.java.md) - Model pentru zi
- [WeatherItem.java](model/WeatherItem.java.md) - Model pentru oră

**Parser:**
- [WeatherParser.java](parser/WeatherParser.java.md) - Parsare JSON

**Util:**
- [LocationManager.java](util/LocationManager.java.md) - Persistență orașe
- [LocationService.java](util/LocationService.java.md) - GPS și geocoding

### Principii de design:

1. **SOLID Principles**
   - Single Responsibility Principle
   - Open/Closed Principle
   - Liskov Substitution Principle
   - Interface Segregation Principle
   - Dependency Inversion Principle

2. **Design Patterns (Gang of Four)**
   - Adapter Pattern
   - Observer Pattern
   - Factory Pattern
   - Strategy Pattern
   - Singleton Pattern (informal)

3. **Android Architecture Components**
   - Activity Lifecycle
   - RecyclerView + Adapter
   - Intent + Parcelable
   - SharedPreferences
   - Google Play Services

4. **Clean Code Principles**
   - DRY (Don't Repeat Yourself)
   - KISS (Keep It Simple, Stupid)
   - YAGNI (You Aren't Gonna Need It)
   - Separation of Concerns

---

**Ultima actualizare:** Ianuarie 2026  
**Autor:** Documentație tehnică pentru studenți  
**Nivel:** Intermediar (cunoștințe Java de bază necesare)
