# Aplicație care preia date meteorologice și le afișează cu grafice

Aplicația preia date meteorologice de la OpenWeatherMap API și le afișează organizate pe zile, cu grafice de temperatură și icoane pentru condițiile meteo. Aplicația suportă gestionarea mai multor locații, inclusiv locația curentă determinată prin GPS.

<!-- TOC -->

- [Aplicație care preia date meteorologice și le afișează cu grafice](#aplicație-care-preia-date-meteorologice-și-le-afișează-cu-grafice)
  - [OpenWeatherMap API](#openweathermap-api)
  - [Concepte importante](#concepte-importante)
    - [RecyclerView](#recyclerview)
    - [Parcelable](#parcelable)
    - [MPAndroidChart](#mpandroidchart)
    - [GPS și Geocoding](#gps-și-geocoding)
    - [Structura împărțită pe componente](#structura-împărțită-pe-componente)
  - [Structura aplicației](#structura-aplicatiei)
    - [JSON-ul returnat de API](#json-ul-returnat-de-api)
    - [ro.makore.akrilki\_08.api - clasa WeatherAPI](#romakoreakrilki_08api---clasa-weatherapi)
    - [ro.makore.akrilki\_08.parser - clasa WeatherParser](#romakoreakrilki_08parser---clasa-weatherparser)
    - [ro.makore.akrilki\_08.model - clasele WeatherItem și DailyWeatherItem](#romakoreakrilki_08model---clasele-weatheritem-și-dailyweatheritem)
    - [ro.makore.akrilki\_08.adapter - clasele DailyWeatherAdapter și LocationSpinnerAdapter](#romakoreakrilki_08adapter---clasele-dailyweatheradapter-și-locationspinneradapter)
    - [ro.makore.akrilki\_08.util - clasele LocationManager și LocationService](#romakoreakrilki_08util---clasele-locationmanager-și-locationservice)
    - [ro.makore.akrilki\_08.dialog - clasa AddLocationDialog](#romakoreakrilki_08dialog---clasa-addlocationdialog)
  - [Activități](#activități)
    - [MainActivity](#mainactivity)
    - [WeatherDetailActivity](#weatherdetailactivity)
  - [Layouturi](#layouturi)
    - [activity\_main.xml](#activity_mainxml)
    - [app\_bar\_main.xml](#app_bar_mainxml)
    - [item\_daily\_weather.xml](#item_daily_weatherxml)
    - [dialog\_add\_location.xml](#dialog_add_locationxml)

<!-- /TOC -->

## OpenWeatherMap API

Aplicația preia date meteorologice de la https://openweathermap.org/api. Pentru replicarea funcționalității trebuie să vă înregistrați pe site-ul https://openweathermap.org/ și să obțineți o cheie API gratuită. Cheia API se poziționează într-un fișier numit `api_key.json` în directorul `app/src/main/assets`.

Conținutul fișierului este astfel:

```json
{
  "apiKey": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
}
```

**Notă**: API-ul gratuit de la OpenWeatherMap oferă acces la prognoza pe 5 zile cu date la fiecare 3 ore. După înregistrare, cheia API poate dura câteva minute până devine activă.

## Concepte importante

### RecyclerView

Un component (view) care reprezintă o variantă avansată a vechilor ListView sau GridView. RecyclerView este din punct de vedere al afișării un ListView cu un management diferit al memoriei, optimizat pentru liste mari de elemente.

### Parcelable

Parcelable este o interfață disponibilă în Android care permite o serializare/deserializare eficientă a obiectelor complexe utilizată la transferul de date între activități sau procese. Parcelable este mai eficient decât vechea interfață Serializable.

### MPAndroidChart

MPAndroidChart este o bibliotecă Android pentru crearea de grafice interactive. În această aplicație, este folosită pentru afișarea graficelor de temperatură sub formă de linie, cu aria umplută sub linie și fără axe sau grid.

### GPS și Geocoding

Aplicația folosește Google Play Services Location API pentru a obține locația curentă a utilizatorului prin GPS. Coordonatele GPS sunt apoi convertite în nume de oraș folosind Geocoder API-ul Android. Locația curentă este afișată în listă cu eticheta "(current)".

### Structura împărțită pe componente

Preluarea datelor meteorologice de la un API extern are o serie de subactivități:

- Interacțiunea cu API-ul (HTTP Request)
- Parsarea rezultatelor (transformarea rezultatelor primite de la API, de obicei în format JSON, într-un obiect Java)
- Definirea structurii obiectului Java care va stoca datele meteorologice
- Gruparea datelor pe zile
- Gestionarea locațiilor salvate
- Obținerea locației GPS curente
- Update al interfeței Android care să afișeze informațiile

Fiecare dintre aceste operații a fost implementată într-o clasă separată.

## Structura aplicației

```sh
akrilki_08/
│   build.gradle
│   gradle.properties
│   settings.gradle
│
├───app
│   │   build.gradle
│   │   proguard-rules.pro
│   │
│   └───src
│       └───main
│           │   AndroidManifest.xml
│           │
│           ├───assets
│           │       api_key.json
│           │
│           ├───java
│           │   └───ro
│           │       └───makore
│           │           └───akrilki_08
│           │               │   MainActivity.java
│           │               │   WeatherDetailActivity.java
│           │               │   WeatherApp.java
│           │               │
│           │               ├───adapter
│           │               │       DailyWeatherAdapter.java
│           │               │       LocationSpinnerAdapter.java
│           │               │       WeatherAdapter.java
│           │               │
│           │               ├───api
│           │               │       WeatherAPI.java
│           │               │
│           │               ├───dialog
│           │               │       AddLocationDialog.java
│           │               │
│           │               ├───model
│           │               │       DailyWeatherItem.java
│           │               │       WeatherItem.java
│           │               │
│           │               ├───parser
│           │               │       WeatherParser.java
│           │               │
│           │               └───util
│           │                       LocationManager.java
│           │                       LocationService.java
│           │
│           └───res
│               ├───drawable
│               │       ic_launcher_8.xml
│               │       ic_launcher_round_8.xml
│               │       ic_quit_black_24dp.xml
│               │       ic_refresh_black_24dp.xml
│               │
│               ├───layout
│               │       activity_main.xml
│               │       activity_weather_detail.xml
│               │       app_bar_main.xml
│               │       dialog_add_location.xml
│               │       item_daily_weather.xml
│               │       item_weather.xml
│               │       spinner_dropdown_item_location.xml
│               │       spinner_item_location.xml
│               │
│               └───values
│                       colors.xml
│                       strings.xml
│                       themes.xml
│
```

### JSON-ul returnat de API

API-ul OpenWeatherMap returnează date în format JSON pentru prognoza pe 5 zile cu date la fiecare 3 ore:

```json
{
  "cod": "200",
  "message": 0,
  "cnt": 40,
  "list": [
    {
      "dt": 1704067200,
      "main": {
        "temp": 5.2,
        "feels_like": 2.8,
        "temp_min": 4.5,
        "temp_max": 6.1,
        "pressure": 1018,
        "sea_level": 1018,
        "grnd_level": 1005,
        "humidity": 75,
        "temp_kf": 0.6
      },
      "weather": [
        {
          "id": 500,
          "main": "Rain",
          "description": "light rain",
          "icon": "10d"
        }
      ],
      "clouds": {
        "all": 100
      },
      "wind": {
        "speed": 4.5,
        "deg": 230
      },
      "visibility": 10000,
      "pop": 0.8,
      "rain": {
        "3h": 0.5
      },
      "sys": {
        "pod": "d"
      },
      "dt_txt": "2024-01-01 12:00:00"
    }
  ],
  "city": {
    "id": 683506,
    "name": "Bucharest",
    "coord": {
      "lat": 44.4325,
      "lon": 26.1039
    },
    "country": "RO",
    "population": 1877155,
    "timezone": 7200,
    "sunrise": 1704012345,
    "sunset": 1704045678
  }
}
```

### ro.makore.akrilki_08.api - clasa WeatherAPI

Clasa `WeatherAPI` gestionează comunicarea cu API-ul OpenWeatherMap. Ea folosește OkHttp pentru a face cereri HTTP.

**Metode principale:**
- `fetchWeather(Context context, String cityName)`: Preia datele meteorologice pentru un oraș specificat
- `readJsonFromAssets(Context context, String fileName)`: Citește cheia API din fișierul `api_key.json` din assets

**Caracteristici:**
- URL encoding pentru numele orașelor cu spații sau caractere speciale
- Validare a cheii API
- Gestionare detaliată a erorilor
- Logging pentru debugging

**Exemplu de utilizare:**

```java
String jsonResponse = WeatherAPI.fetchWeather(context, "Bucharest");
```

### ro.makore.akrilki_08.parser - clasa WeatherParser

Clasa `WeatherParser` parsează răspunsul JSON de la API și îl transformă în obiecte Java.

**Metode principale:**
- `parseWeatherByDay(String jsonResponse)`: Parsează răspunsul JSON și grupează datele pe zile, returnând o listă de `DailyWeatherItem`

**Caracteristici:**
- Parsare JSON folosind Gson
- Gruparea datelor pe zile (yyyy-MM-dd)
- Validare a structurii răspunsului
- Conversie timestamp Unix în format de dată/ora
- Construirea URL-urilor pentru icoanele meteo

**Exemplu de utilizare:**

```java
List<DailyWeatherItem> dailyWeatherItems = WeatherParser.parseWeatherByDay(jsonResponse);
```

### ro.makore.akrilki_08.model - clasele WeatherItem și DailyWeatherItem

**WeatherItem** - Reprezintă o intrare de prognoză meteo pentru un interval de 3 ore:
- `cityName`: Numele orașului
- `country`: Codul țării
- `description`: Descrierea condițiilor meteo
- `iconUrl`: URL-ul icoanei meteo
- `temperature`: Temperatura în grade Celsius
- `feelsLike`: Temperatura resimțită
- `humidity`: Umiditatea relativă
- `pressure`: Presiunea atmosferică
- `windSpeed`: Viteza vântului
- `visibility`: Vizibilitatea
- `dateTime`: Data și ora prognozei

**DailyWeatherItem** - Reprezintă datele meteo pentru o zi întreagă:
- `date`: Data (yyyy-MM-dd)
- `cityName`: Numele orașului
- `country`: Codul țării
- `hourlyData`: Lista de `WeatherItem` pentru acea zi (date la fiecare 3 ore)

Ambele clase implementează interfața `Parcelable` pentru transferul eficient de date între activități.

### ro.makore.akrilki_08.adapter - clasele DailyWeatherAdapter și LocationSpinnerAdapter

**DailyWeatherAdapter** - Adapter pentru RecyclerView care afișează datele meteo grupate pe zile:
- Afișează fiecare zi într-un card
- Conține un grafic de temperatură (LineChart) cu aria umplută sub linie
- Afișează temperatura pe fiecare nod al graficului
- Afișează icoane meteo sub grafic, aliniate cu fiecare nod de date
- Fără axe sau grid pe grafic
- Formatare a datelor (ex: "Monday, January 15, 2024")

**LocationSpinnerAdapter** - Adapter pentru Spinner-ul de locații:
- Afișează toate locațiile salvate
- Marchează locația curentă (GPS) cu eticheta "(current)"
- Stilizare personalizată pentru dropdown (fundal alb, text negru)

### ro.makore.akrilki_08.util - clasele LocationManager și LocationService

**LocationManager** - Gestionează locațiile salvate:
- Folosește SharedPreferences pentru stocare persistentă
- Metode: `getSavedLocations()`, `addLocation()`, `removeLocation()`, `hasLocation()`
- Stochează locația curentă (GPS) separat

**LocationService** - Gestionează obținerea locației GPS:
- Folosește Google Play Services Location API (FusedLocationProviderClient)
- Convertește coordonatele GPS în nume de oraș folosind Geocoder
- Gestionează permisiunile de locație
- Callback pentru rezultate: `onLocationReceived()`, `onLocationError()`

### ro.makore.akrilki_08.dialog - clasa AddLocationDialog

Dialog pentru adăugarea de locații noi:
- Câmp de text pentru introducerea numelui locației
- Validare în timp real prin verificarea disponibilității locației pe API
- Afișare status: "Checking...", "Location found", "Location not found", "Already exists"
- Adaugă locația doar dacă este validă și nu există deja

## Activități

### MainActivity

Activitatea principală care afișează lista de prognoze meteo grupate pe zile.

**Funcționalități:**
- App bar cu Spinner pentru selecția locației
- Buton pentru adăugarea de locații noi
- Obținere automată a locației GPS curente
- Afișare date meteo în RecyclerView cu grafice
- Butoane Floating Action Button pentru refresh și quit
- Gestionare stări de loading și erori

**Fluxul de date:**
1. La pornire, obține locația GPS (dacă permisiunea este acordată)
2. Încarcă locațiile salvate din SharedPreferences
3. Afișează locațiile în Spinner
4. Preia datele meteo pentru locația selectată
5. Parsează și grupează datele pe zile
6. Afișează datele în RecyclerView cu grafice

### WeatherDetailActivity

Activitatea pentru afișarea detaliilor unei intrări meteo specifice. Primește un `WeatherItem` prin Intent și afișează toate detaliile.

## Layouturi

### activity_main.xml

Layout principal care conține:
- CoordinatorLayout ca container principal
- AppBarLayout inclus din `app_bar_main.xml`
- ConstraintLayout pentru conținutul principal
- RecyclerView pentru lista de prognoze
- ProgressBar și TextView pentru starea de loading
- Două FloatingActionButton pentru quit și refresh

### app_bar_main.xml

Layout pentru app bar care conține:
- Toolbar cu temă Material Design
- Spinner pentru selecția locației
- ImageButton pentru adăugarea de locații noi

### item_daily_weather.xml

Layout pentru fiecare card de prognoză zilnică:
- CardView ca container
- TextView pentru numele orașului (afișat doar pe primul item)
- TextView pentru data formatată
- FrameLayout care conține:
  - LineChart pentru graficul de temperatură
  - ImageView-uri pentru icoanele meteo (adăugate dinamic)

### dialog_add_location.xml

Layout pentru dialogul de adăugare locație:
- EditText pentru introducerea numelui locației
- TextView pentru status (checking, found, not found)
- Butoane pentru Add și Cancel

### spinner_item_location.xml și spinner_dropdown_item_location.xml

Layouturi personalizate pentru Spinner-ul de locații:
- Fundal alb
- Text negru
- Padding pentru lizibilitate

## Dependențe principale

- **OkHttp**: Pentru cereri HTTP
- **Gson**: Pentru parsarea JSON
- **Glide**: Pentru încărcarea imaginilor (folosit inițial, apoi înlocuit cu OkHttp direct pentru icoane)
- **MPAndroidChart**: Pentru graficele de temperatură
- **Google Play Services Location**: Pentru obținerea locației GPS
- **AndroidThreeTen**: Pentru manipularea datelor (backport pentru API < 26)

## Caracteristici speciale

1. **Gruparea datelor pe zile**: Datele meteo la fiecare 3 ore sunt grupate pe zile pentru o vizualizare mai clară
2. **Grafice interactive**: Fiecare zi are un grafic de temperatură cu aria umplută sub linie
3. **Icoane meteo**: Icoanele sunt afișate sub grafic, aliniate cu fiecare nod de date
4. **Gestionare locații**: Suport pentru mai multe locații salvate
5. **Locație GPS**: Detecție automată a locației curente cu etichetare "(current)"
6. **Validare online**: Locațiile noi sunt validate prin verificarea disponibilității pe API înainte de a fi adăugate

