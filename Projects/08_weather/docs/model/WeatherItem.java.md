# WeatherItem.java — Documentație detaliată a codului sursă

Acest document prezintă codul complet al clasei `WeatherItem.java` împărțit în fragmente logice, fiecare precedat de explicații detaliate în limba română. Documentația este destinată programatorilor cu cunoștințe de Java dar cu experiență limitată în programarea Android.

## Context general

`WeatherItem` este clasa model fundamentală a aplicației meteo, reprezentând **o singură înregistrare de vreme pentru un moment specific în timp** (o oră). Aceasta este "unitatea atomică" de date meteo din aplicație.

### Caracteristici cheie:

1. **Model class** - Clasa POJO (Plain Old Java Object) pentru date meteo
2. **Parcelable** - Poate fi transmisă între Activity-uri prin Intent-uri (optimizat pentru Android)
3. **11 câmpuri** - Date complete despre vreme (temperatură, umiditate, vânt, etc.)
4. **Utilizare multiplă**:
   - În `DailyWeatherItem` (agregare: 8-24 WeatherItem-uri pentru o zi)
   - În `WeatherAdapter` (RecyclerView pentru lista orară)
   - În `WeatherDetailActivity` (detalii despre o oră specifică)
   - Transmisă prin Intent-uri între Activity-uri

### Relația cu alte componente:

```
JSON (OpenWeatherMap API)
         ↓
    WeatherParser.parseWeather()
         ↓
    List<WeatherItem> (40 ore)
         ↓
    WeatherParser.parseWeatherByDay()
         ↓
    List<DailyWeatherItem> (5 zile)
         ↓ (fiecare conține List<WeatherItem>)
    WeatherAdapter / DailyWeatherAdapter
         ↓
    RecyclerView (afișare)
```

## 1. Declararea pachetului

```java
package ro.makore.akrilki_08.model;
```

**Explicație:**
- Sub-pachetul `model` grupează toate clasele de date (data model)
- Organizare logică: separarea datelor de logica business și UI
- Alte clase din acest pachet: `DailyWeatherItem`

## 2. Import-uri pentru Parcelable

```java
import android.os.Parcel;
import android.os.Parcelable;
```

**Linie cu linie:**
- **`Parcel`** - Container pentru serializarea datelor (scriere/citire binară)
- **`Parcelable`** - Interfață Android pentru obiecte care pot fi transmise prin Intent-uri
- **Scop**: Permite trecerea obiectelor WeatherItem între Activity-uri (MainActivity → WeatherDetailActivity)

### De ce Parcelable și nu Serializable?

| Aspect | Parcelable | Serializable |
|--------|------------|--------------|
| **Performanță** | **10x mai rapid** | Mai lent (reflection) |
| **Memorie** | Optimizat pentru Android | Consuma mai multă memorie |
| **Cod** | Necesită implementare manuală | Automată (doar `implements Serializable`) |
| **Android** | Recomandat oficial | Evitat pentru performanță |
| **Complexitate** | Mai complex | Mai simplu |

**Concluzie**: Pentru Android, Parcelable este alegerea corectă pentru transmitere între componente.

## 3. Declararea clasei și implementarea Parcelable

```java
public class WeatherItem implements Parcelable {
```

**Explicație:**
- Clasa este `public` pentru a putea fi utilizată din orice pachet
- `implements Parcelable` - Obligă clasa să implementeze 3 elemente:
  1. Metoda `writeToParcel()` - Serializare (obiect → Parcel)
  2. Metoda `describeContents()` - Descriptor pentru conținut special
  3. Câmpul static `CREATOR` - Factory pentru deserializare (Parcel → obiect)

## 4. Declararea câmpurilor private - Date meteo complete

### Câmpuri de identificare

```java
    private String cityName;
    private String country;
```

**Linie cu linie:**
- **`cityName`** - Numele orașului (ex: "Bucharest", "Cluj-Napoca")
- **`country`** - Codul țării (ex: "RO", "US", "GB")
- **Private**: Acces doar prin getters/setters (encapsulare)

### Câmpuri de descriere vizuală

```java
    private String description;
    private String iconUrl;
```

**Linie cu linie:**
- **`description`** - Descrierea vremii (ex: "Clear sky", "Light rain", "Scattered clouds")
- **`iconUrl`** - URL complet pentru iconița meteo de la OpenWeatherMap
  - Format: `"https://openweathermap.org/img/wn/10d@2x.png"`
  - Încărcat cu Glide în adapter

### Câmpuri de temperatură

```java
    private double temperature;
    private double feelsLike;
```

**Linie cu linie:**
- **`temperature`** - Temperatura reală în grade Celsius
- **`feelsLike`** - Temperatura resimțită (heat index sau wind chill)
- Tip `double` pentru precizie (ex: 23.45°C)

### Câmpuri de condiții atmosferice

```java
    private double humidity;
    private double pressure;
```

**Linie cu linie:**
- **`humidity`** - Umiditate relativă în procente (0-100)
- **`pressure`** - Presiune atmosferică în hPa (hectopascali)
  - Valoare tipică la nivelul mării: ~1013 hPa

### Câmpuri de vânt și vizibilitate

```java
    private double windSpeed;
    private double visibility;
```

**Linie cu linie:**
- **`windSpeed`** - Viteza vântului în m/s (metri pe secundă)
- **`visibility`** - Vizibilitate în metri (max 10000 = 10 km)

### Câmp temporal

```java
    private String dateTime;
```

**Explicație:**
- **`dateTime`** - Data și ora exactă a prognozei
- Format ISO 8601: `"2024-01-20 15:00:00"`
- Utilizat pentru:
  - Afișare în UI (conversie cu ThreeTenABP)
  - Grupare pe zile (în WeatherParser)
  - Sortare cronologică

### Structura completă a datelor:

```
WeatherItem {
  ┌─ Identificare ────────────┐
  │ cityName: "Bucharest"     │
  │ country: "RO"              │
  ├─ Descriere vizuală ───────┤
  │ description: "Clear sky"   │
  │ iconUrl: "https://..."     │
  ├─ Temperatură ─────────────┤
  │ temperature: 23.5          │
  │ feelsLike: 24.2            │
  ├─ Condiții atmosferice ────┤
  │ humidity: 65.0             │
  │ pressure: 1013.0           │
  ├─ Vânt și vizibilitate ────┤
  │ windSpeed: 3.5             │
  │ visibility: 10000.0        │
  └─ Timp ────────────────────┤
    dateTime: "2024-01-20 15:00"
}
```

## 5. Constructor default (fără parametri)

```java
    // Unparcelable Constructor
    public WeatherItem() {
    }
```

**Explicație:**
- Constructor default gol, fără parametri
- **Scop principal**: Utilizat de WeatherParser pentru crearea obiectelor noi
- Câmpurile rămân la valorile default (null pentru String, 0.0 pentru double)
- Valorile sunt setate ulterior prin setters

### Exemplu de utilizare în WeatherParser:

```java
// În WeatherParser.parseWeather()
WeatherItem item = new WeatherItem();  // Constructor default
item.setCityName(cityName);
item.setCountry(country);
item.setTemperature(main.get("temp").getAsDouble());
// ... etc
```

## 6. Constructor Parcelable - Deserializare din Parcel

### Importanța ordinii de citire

```java
    // Parcelable constructor - same order as in writeToParcel method
    protected WeatherItem(Parcel in) {
```

**Linie cu linie:**
- **`protected`** - Vizibilitate: pachet + subclase (suficient pentru CREATOR)
- **`Parcel in`** - Parametru: Parcel-ul din care citim datele
- **CRITICAL**: Ordinea de citire TREBUIE să fie identică cu ordinea de scriere din `writeToParcel()`

### Citirea câmpurilor String și double

```java
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
```

**Linie cu linie:**
- Fiecare `in.readXXX()` citește următoarea valoare din Parcel
- **String**: `readString()` - Pentru text (cityName, country, description, iconUrl, dateTime)
- **double**: `readDouble()` - Pentru valori numerice cu zecimale (toate măsurătorile)
- **Ordine obligatorie**: Exact ca în `writeToParcel()`

### Tabel de comparație: Ordine critică

| Poziție | writeToParcel() | Constructor(Parcel) | Tip |
|---------|-----------------|---------------------|-----|
| 1 | `writeString(cityName)` | `cityName = readString()` | String |
| 2 | `writeString(country)` | `country = readString()` | String |
| 3 | `writeString(description)` | `description = readString()` | String |
| 4 | `writeString(iconUrl)` | `iconUrl = readString()` | String |
| 5 | `writeDouble(temperature)` | `temperature = readDouble()` | double |
| 6 | `writeDouble(feelsLike)` | `feelsLike = readDouble()` | double |
| 7 | `writeDouble(humidity)` | `humidity = readDouble()` | double |
| 8 | `writeDouble(pressure)` | `pressure = readDouble()` | double |
| 9 | `writeDouble(windSpeed)` | `windSpeed = readDouble()` | double |
| 10 | `writeDouble(visibility)` | `visibility = readDouble()` | double |
| 11 | `writeString(dateTime)` | `dateTime = readString()` | String |

**IMPORTANT**: Dacă ordinea nu coincide, datele vor fi citite greșit (ex: `temperature` va conține valoarea `feelsLike`).

## 7. Metoda writeToParcel() - Serializare în Parcel

```java
    // writeToParcel must be in the same order as constructor
    @Override
    public void writeToParcel(Parcel dest, int flags) {
```

**Linie cu linie:**
- `@Override` - Suprascrie metoda din interfața Parcelable
- **`Parcel dest`** - Parcel-ul destinație unde scriem datele
- **`int flags`** - Flag-uri speciale (de obicei 0, ignorat în majoritatea cazurilor)

### Scrierea tuturor câmpurilor

```java
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
```

**Linie cu linie:**
- Fiecare `dest.writeXXX()` scrie următoarea valoare în Parcel
- **Ordine identică** cu constructor-ul Parcelable
- Valorile sunt scrise secvențial în Parcel (ca într-un buffer binar)

### Flow de serializare prin Intent:

```
MainActivity                    WeatherDetailActivity
     |                                   |
     | intent.putExtra("weather", item)  |
     |         ↓                          |
     |   WeatherItem.writeToParcel()     |
     |   (scriere în Parcel)              |
     |         ↓                          |
     |   [Parcel binar în Intent]         |
     |────────────────────────────────────→
     |                                   |
     |                    CREATOR.createFromParcel()
     |                    WeatherItem(Parcel in)
     |                    (citire din Parcel)
     |                          ↓
     |                    WeatherItem obiect
```

## 8. Metoda describeContents() - Descriptor de conținut

```java
    @Override
    public int describeContents() {
        return 0;  // No special objects inside
    }
```

**Explicație:**
- Metodă din interfața Parcelable
- **Return 0**: Obiectul nu conține FileDescriptors sau obiecte speciale
- **Return CONTENTS_FILE_DESCRIPTOR**: Dacă ar conține FileDescriptor-i (fișiere deschise, socket-uri)
- **Pentru WeatherItem**: Conține doar String-uri și double-uri → return 0

## 9. Câmpul static CREATOR - Factory pentru Parcelable

### Declararea CREATOR-ului

```java
    // Parcelable CREATOR to help with deserialization
    public static final Creator<WeatherItem> CREATOR = new Creator<WeatherItem>() {
```

**Linie cu linie:**
- **`public static final`** - Câmp constant static, vizibil global
- **`Creator<WeatherItem>`** - Interfață generică pentru crearea obiectelor Parcelable
- **OBLIGATORIU**: Fără acest câmp, Parcelable nu funcționează!
- **Nume fix**: Trebuie să se numească exact `CREATOR` (căutat prin reflection)

### Metoda createFromParcel() - Creare obiect din Parcel

```java
        @Override
        public WeatherItem createFromParcel(Parcel in) {
            return new WeatherItem(in);
        }
```

**Explicație:**
- Apelează constructorul Parcelable `WeatherItem(Parcel in)`
- Utilizată automat de sistem când extras-e obiectul din Intent
- **Exemplu**: `intent.getParcelableExtra("weather")` apelează această metodă intern

### Metoda newArray() - Creare array de obiecte

```java
        @Override
        public WeatherItem[] newArray(int size) {
            return new WeatherItem[size];
        }
    };
```

**Explicație:**
- Creează un array de WeatherItem-uri de dimensiune specificată
- Utilizat când transmitem array-uri de obiecte Parcelable
- **Exemplu**: `intent.putParcelableArrayListExtra("list", arrayList)`

### De ce este necesar CREATOR?

```java
// În WeatherDetailActivity - Android folosește CREATOR implicit:
WeatherItem item = getIntent().getParcelableExtra("weather");

// Intern, sistemul face:
// 1. Citește Parcel-ul din Intent
// 2. Găsește clasa WeatherItem
// 3. Accesează WeatherItem.CREATOR (prin reflection)
// 4. Apelează CREATOR.createFromParcel(parcel)
// 5. Returnează obiectul reconstruit
```

## 10. Getters și Setters - Encapsulare câmpurilor

### Pattern standard JavaBeans

Toate câmpurile private au perechi de metode getter/setter. Vom prezenta câteva exemple reprezentative:

### Getter și Setter pentru cityName

```java
    // Getters and Setters
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
```

**Linie cu linie:**
- **`getCityName()`** - Returnează valoarea câmpului private `cityName`
- **`setCityName(String cityName)`** - Setează o nouă valoare
- **`this.cityName`** - Distinge între câmpul clasei și parametrul metodei

### Getter și Setter pentru country

```java
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
```

**Exemplu de utilizare în WeatherParser:**
```java
item.setCountry(sys.get("country").getAsString());
String displayName = item.getCityName() + ", " + item.getCountry();
```

### Getter și Setter pentru description

```java
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
```

**Exemplu de utilizare în WeatherAdapter:**
```java
String desc = item.getDescription();
holder.description.setText(desc.substring(0, 1).toUpperCase() + desc.substring(1));
// "clear sky" → "Clear sky"
```

### Getter și Setter pentru iconUrl

```java
    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
```

**Exemplu de utilizare în WeatherAdapter (Glide):**
```java
Glide.with(holder.itemView.getContext())
     .load(item.getIconUrl())
     .into(holder.weatherIcon);
```

### Getter și Setter pentru temperature

```java
    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
```

**Exemplu de utilizare în WeatherDetailActivity:**
```java
double temp = weatherItem.getTemperature();
tvTemperature.setText(String.format("%.1f°C", temp));
// 23.456 → "23.5°C"
```

### Getter și Setter pentru feelsLike

```java
    public double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }
```

**Explicație:**
- **feelsLike** - Temperatura resimțită (calculată cu umiditate și vânt)
- Poate fi diferită de temperatura reală
- Exemplu: 25°C temperatura, 28°C resimțită (umiditate mare)

### Getter și Setter pentru humidity

```java
    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }
```

**Exemplu de utilizare în WeatherDetailActivity:**
```java
tvHumidity.setText(String.format("%.0f%%", weatherItem.getHumidity()));
// 65.0 → "65%"
```

### Getter și Setter pentru pressure

```java
    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }
```

**Exemplu de utilizare:**
```java
tvPressure.setText(String.format("%.0f hPa", weatherItem.getPressure()));
// 1013.25 → "1013 hPa"
```

### Getter și Setter pentru windSpeed

```java
    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }
```

**Exemplu de utilizare:**
```java
tvWind.setText(String.format("%.1f m/s", weatherItem.getWindSpeed()));
// 3.456 → "3.5 m/s"
```

### Getter și Setter pentru visibility

```java
    public double getVisibility() {
        return visibility;
    }

    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }
```

**Exemplu de utilizare:**
```java
double visKm = weatherItem.getVisibility() / 1000.0;
tvVisibility.setText(String.format("%.1f km", visKm));
// 10000.0 → "10.0 km"
```

### Getter și Setter pentru dateTime

```java
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
```

**Exemplu de utilizare în WeatherParser (ThreeTenABP):**
```java
String dateStr = item.getDateTime();
LocalDate date = LocalDate.parse(dateStr.substring(0, 10));
String dayName = date.format(DateTimeFormatter.ofPattern("EEEE"));
// "2024-01-20 15:00:00" → "Saturday"
```

---

## 11. Avantajele encapsulării prin getters/setters

### De ce nu câmpuri publice?

| Aspect | Câmpuri publice | Getters/Setters |
|--------|-----------------|-----------------|
| **Validare** | Nu se poate face | Posibilă în setter |
| **Notificare** | Nu | Posibilă (ex: Observer) |
| **Read-only** | Imposibil | Doar getter, fără setter |
| **Logging** | Nu | Posibil în setter/getter |
| **Refactoring** | Risc mare | Controlat |
| **Debugging** | Greu de urmărit | Breakpoint în setter |

### Exemple de îmbunătățiri posibile:

#### Validare în setteri:

```java
public void setTemperature(double temperature) {
    if (temperature < -273.15) {  // Sub zero absolut
        throw new IllegalArgumentException("Invalid temperature");
    }
    this.temperature = temperature;
}

public void setHumidity(double humidity) {
    if (humidity < 0 || humidity > 100) {
        throw new IllegalArgumentException("Humidity must be 0-100");
    }
    this.humidity = humidity;
}
```

#### Câmpuri read-only (doar getter):

```java
// Dacă vrem ca dateTime să fie setat doar la crearea obiectului:
private final String dateTime;

public WeatherItem(String dateTime) {
    this.dateTime = dateTime;
}

public String getDateTime() {
    return dateTime;
}

// NU există setDateTime() → câmpul e immutable după creare
```

---

## 12. Utilizarea clasei WeatherItem în aplicație

### 1. Creare în WeatherParser (din JSON)

```java
// În WeatherParser.parseWeather()
JsonArray list = forecast.getAsJsonArray("list");

for (JsonElement element : list) {
    JsonObject item = element.getAsJsonObject();
    
    // Creăm obiect nou
    WeatherItem weatherItem = new WeatherItem();
    
    // Setăm datele din JSON
    weatherItem.setCityName(cityName);
    weatherItem.setCountry(country);
    weatherItem.setDescription(description);
    weatherItem.setIconUrl(iconUrl);
    weatherItem.setTemperature(temperature);
    weatherItem.setFeelsLike(feelsLike);
    weatherItem.setHumidity(humidity);
    weatherItem.setPressure(pressure);
    weatherItem.setWindSpeed(windSpeed);
    weatherItem.setVisibility(visibility);
    weatherItem.setDateTime(dateTime);
    
    items.add(weatherItem);
}
```

### 2. Afișare în WeatherAdapter (RecyclerView)

```java
// În WeatherAdapter.onBindViewHolder()
@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    WeatherItem item = weatherList.get(position);
    
    // Extragem datele și le afișăm
    holder.cityName.setText(item.getCityName() + ", " + item.getCountry());
    holder.temperature.setText(String.format("%.1f°C", item.getTemperature()));
    holder.description.setText(item.getDescription());
    holder.dateTime.setText(formatDateTime(item.getDateTime()));
    
    // Încărcăm iconița cu Glide
    Glide.with(holder.itemView.getContext())
         .load(item.getIconUrl())
         .into(holder.weatherIcon);
}
```

### 3. Transmitere prin Intent (Parcelable)

```java
// În WeatherAdapter (click pe item)
holder.itemView.setOnClickListener(v -> {
    Intent intent = new Intent(context, WeatherDetailActivity.class);
    
    // Punem obiectul în Intent (folosește writeToParcel intern)
    intent.putExtra("weather_item", item);
    
    context.startActivity(intent);
});

// În WeatherDetailActivity.onCreate()
WeatherItem item = getIntent().getParcelableExtra("weather_item");
// Folosește CREATOR.createFromParcel() intern

// Afișăm detaliile
tvTemperature.setText(String.format("%.1f°C", item.getTemperature()));
tvFeelsLike.setText(String.format("%.1f°C", item.getFeelsLike()));
tvHumidity.setText(String.format("%.0f%%", item.getHumidity()));
tvPressure.setText(String.format("%.0f hPa", item.getPressure()));
tvWind.setText(String.format("%.1f m/s", item.getWindSpeed()));
tvVisibility.setText(String.format("%.1f km", item.getVisibility() / 1000));
```

### 4. Agregare în DailyWeatherItem

```java
// În DailyWeatherItem
private List<WeatherItem> hourlyData = new ArrayList<>();

public void addHourlyData(WeatherItem item) {
    if (item != null) {
        hourlyData.add(item);
    }
}

// În WeatherParser.parseWeatherByDay()
for (WeatherItem item : allItems) {
    String date = item.getDateTime().substring(0, 10); // "2024-01-20"
    
    if (!dailyMap.containsKey(date)) {
        DailyWeatherItem dailyItem = new DailyWeatherItem();
        dailyItem.setDate(date);
        dailyMap.put(date, dailyItem);
    }
    
    // Adăugăm WeatherItem-ul în ziua corespunzătoare
    dailyMap.get(date).addHourlyData(item);
}
```

### 5. Utilizare în DailyWeatherAdapter (grafice)

```java
// În DailyWeatherAdapter.onBindViewHolder()
List<WeatherItem> hourlyData = dailyItem.getHourlyData();

// Creăm date pentru grafic
List<Entry> entries = new ArrayList<>();
for (int i = 0; i < hourlyData.size(); i++) {
    WeatherItem item = hourlyData.get(i);
    float temp = (float) item.getTemperature();
    entries.add(new Entry(i, temp));
}

// Configurăm graficul
LineDataSet dataSet = new LineDataSet(entries, "Temperature");
holder.chart.setData(new LineData(dataSet));
```

---

## 13. Diagrama de flux: Viața unui WeatherItem

```
┌─────────────────────────────────────────────────────┐
│ 1. API Request                                       │
│    MainActivity → WeatherAPI.fetchWeather()          │
└─────────────────┬───────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────────────────┐
│ 2. JSON Response (40 ore de prognoză)                │
│    {                                                  │
│      "list": [                                        │
│        {"dt_txt": "2024-01-20 15:00:00",              │
│         "main": {"temp": 23.5, ...},                  │
│         "weather": [{"description": "clear sky"}],    │
│         ...}                                          │
│      ]                                                │
│    }                                                  │
└─────────────────┬───────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────────────────┐
│ 3. WeatherParser.parseWeather()                      │
│    List<WeatherItem> items = new ArrayList<>();      │
│                                                       │
│    for (JsonElement elem : list) {                   │
│        WeatherItem item = new WeatherItem();  ←──────┼── CREARE
│        item.setTemperature(...);                      │
│        item.setDescription(...);                      │
│        items.add(item);                               │
│    }                                                  │
└─────────────────┬───────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────────────────┐
│ 4. WeatherParser.parseWeatherByDay()                 │
│    Grupează 40 WeatherItem-uri → 5 DailyWeatherItem  │
│                                                       │
│    Daily 1: [8 WeatherItem-uri]  ←──────────────────┼── AGREGARE
│    Daily 2: [8 WeatherItem-uri]                      │
│    ...                                                │
└─────────────────┬───────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────────────────┐
│ 5. DailyWeatherAdapter (RecyclerView în MainActivity)│
│    - Afișează grafice cu temperaturi                  │
│    - Overlay cu iconițe meteo                         │
│    - Click pe zi → WeatherDetailActivity              │
└─────────────────┬───────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────────────────┐
│ 6. Intent.putExtra("weather_item", item)             │
│    item.writeToParcel(parcel, 0);  ←─────────────────┼── SERIALIZARE
│    [Parcel binar transmis între Activity-uri]         │
└─────────────────┬───────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────────────────┐
│ 7. WeatherDetailActivity.onCreate()                  │
│    WeatherItem item = getIntent()                    │
│        .getParcelableExtra("weather_item");          │
│                                                       │
│    CREATOR.createFromParcel(parcel);  ←──────────────┼── DESERIALIZARE
│    new WeatherItem(parcel);                          │
└─────────────────┬───────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────────────────┐
│ 8. Afișare detalii în WeatherDetailActivity          │
│    - Temperatură, umiditate, presiune                 │
│    - Vânt, vizibilitate                               │
│    - Iconița și descrierea                            │
└─────────────────────────────────────────────────────┘
```

---

## 14. Structura de date: 40 WeatherItem → 5 DailyWeatherItem

```
API Response (40 ore = 5 zile × 8 ore/zi)
    ↓
List<WeatherItem> (40 obiecte)
    ↓
parseWeatherByDay() - Grupare pe date
    ↓
LinkedHashMap<String, DailyWeatherItem>
    ├─ "2024-01-20" → DailyWeatherItem
    │                   ├─ WeatherItem (00:00)
    │                   ├─ WeatherItem (03:00)
    │                   ├─ WeatherItem (06:00)
    │                   ├─ WeatherItem (09:00)
    │                   ├─ WeatherItem (12:00)
    │                   ├─ WeatherItem (15:00)
    │                   ├─ WeatherItem (18:00)
    │                   └─ WeatherItem (21:00)
    │
    ├─ "2024-01-21" → DailyWeatherItem
    │                   └─ [8 WeatherItem-uri]
    │
    ├─ "2024-01-22" → DailyWeatherItem
    ├─ "2024-01-23" → DailyWeatherItem
    └─ "2024-01-24" → DailyWeatherItem
```

---

## 15. Best practices și îmbunătățiri posibile

### Îmbunătățiri pentru robustețe:

#### 1. Validare în setteri

```java
public void setTemperature(double temperature) {
    if (temperature < -273.15) {
        Log.w("WeatherItem", "Invalid temperature: " + temperature);
        this.temperature = 0;  // Valoare default sigură
    } else {
        this.temperature = temperature;
    }
}

public void setHumidity(double humidity) {
    this.humidity = Math.max(0, Math.min(100, humidity)); // Clamp 0-100
}
```

#### 2. Imutabilitate (pattern final fields + constructor complet)

```java
public class WeatherItem implements Parcelable {
    private final String cityName;
    private final String country;
    // ... toate câmpurile final
    
    // Constructor complet - singura cale de setare
    public WeatherItem(String cityName, String country, String description, ...) {
        this.cityName = cityName;
        this.country = country;
        // ... etc
    }
    
    // Doar getters, fără setters → obiect immutable
    public String getCityName() { return cityName; }
    // ... etc
}

// Avantaje:
// - Thread-safe (nu se poate modifica din multiple thread-uri)
// - Previne modificări accidentale
// - Mai ușor de debugat
```

#### 3. Builder pattern pentru creare mai clară

```java
public class WeatherItem implements Parcelable {
    // ... câmpuri
    
    private WeatherItem(Builder builder) {
        this.cityName = builder.cityName;
        this.temperature = builder.temperature;
        // ... etc
    }
    
    public static class Builder {
        private String cityName;
        private double temperature;
        // ... etc
        
        public Builder cityName(String cityName) {
            this.cityName = cityName;
            return this;
        }
        
        public Builder temperature(double temperature) {
            this.temperature = temperature;
            return this;
        }
        
        public WeatherItem build() {
            return new WeatherItem(this);
        }
    }
}

// Utilizare în WeatherParser:
WeatherItem item = new WeatherItem.Builder()
    .cityName("Bucharest")
    .country("RO")
    .temperature(23.5)
    .humidity(65.0)
    .build();
```

#### 4. Metode helper pentru formatare

```java
public String getFormattedTemperature() {
    return String.format("%.1f°C", temperature);
}

public String getFormattedHumidity() {
    return String.format("%.0f%%", humidity);
}

public String getFormattedWindSpeed() {
    return String.format("%.1f m/s", windSpeed);
}

public String getFormattedVisibility() {
    return String.format("%.1f km", visibility / 1000.0);
}

// Utilizare în adapter:
holder.temperature.setText(item.getFormattedTemperature());
// În loc de: holder.temperature.setText(String.format("%.1f°C", item.getTemperature()));
```

#### 5. Metode helper pentru dată/oră

```java
public LocalDateTime getLocalDateTime() {
    return LocalDateTime.parse(dateTime, 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
}

public String getFormattedTime() {
    return getLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
}

public String getFormattedDate() {
    return getLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
}

// Utilizare:
holder.time.setText(item.getFormattedTime()); // "15:00"
holder.date.setText(item.getFormattedDate()); // "20 Jan 2024"
```

#### 6. Override toString() pentru debugging

```java
@Override
public String toString() {
    return "WeatherItem{" +
           "city='" + cityName + ", " + country + '\'' +
           ", temp=" + temperature + "°C" +
           ", desc='" + description + '\'' +
           ", time='" + dateTime + '\'' +
           '}';
}

// Utilizare în debugging:
Log.d("WeatherParser", "Parsed: " + weatherItem);
// Output: "Parsed: WeatherItem{city='Bucharest, RO', temp=23.5°C, desc='clear sky', time='2024-01-20 15:00:00'}"
```

#### 7. Override equals() și hashCode() pentru comparații

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    
    WeatherItem that = (WeatherItem) o;
    
    return Objects.equals(cityName, that.cityName) &&
           Objects.equals(dateTime, that.dateTime);
    // Două WeatherItem-uri sunt egale dacă au același oraș și același timp
}

@Override
public int hashCode() {
    return Objects.hash(cityName, dateTime);
}

// Utilizare:
if (weatherList.contains(newItem)) {
    // Item-ul există deja, nu-l adăugăm
}
```

---

## 16. Comparație: WeatherItem vs DailyWeatherItem

| Aspect | WeatherItem | DailyWeatherItem |
|--------|-------------|------------------|
| **Scop** | O oră de prognoză | O zi întreagă |
| **Granularitate** | Oră | Zi |
| **Câmpuri** | 11 câmpuri detaliate | 4 câmpuri (+ listă WeatherItem) |
| **Relație** | Atomic | Agregare (8-24 WeatherItem-uri) |
| **Utilizare** | WeatherDetailActivity | MainActivity (grafice) |
| **Adapter** | WeatherAdapter | DailyWeatherAdapter |
| **API source** | Directă (1:1 cu JSON) | Procesată (grupare) |
| **Parcelable** | Da | Da |

---

## Rezumat

Această clasă este modelul de date fundamental pentru vremea orară:

### **Scop principal**
- Reprezintă o înregistrare de vreme pentru un moment specific în timp (o oră)
- Conține toate detaliile necesare: temperatură, umiditate, vânt, presiune, vizibilitate
- Poate fi transmisă între Activity-uri folosind Parcelable

### **Caracteristici cheie:**
1. **11 câmpuri** - Date meteorologice complete
2. **Parcelable** - Optimizat pentru transmitere între componente Android
3. **Encapsulare** - Toate câmpurile private cu getters/setters
4. **Pattern JavaBeans** - Standard pentru clase model
5. **Imutabilitate potențială** - Poate fi ușor transformat în immutable
6. **Thread-safe potential** - Fără state partajat

### **Pattern-uri implementate:**
- **JavaBeans** - Getters/setters pentru toate câmpurile
- **Parcelable** - Serializare optimizată pentru Android
- **Factory** - CREATOR pentru deserializare
- **Encapsulation** - Câmpuri private, acces controlat

### **Metode Parcelable (obligatorii):**
1. **Constructor Parcelable** - `WeatherItem(Parcel in)`
2. **writeToParcel()** - Serializare (obiect → Parcel)
3. **describeContents()** - Return 0 (fără FileDescriptors)
4. **CREATOR** - Factory static pentru deserializare

### **Ordine critică Parcelable:**
```
writeToParcel() ←→ Constructor(Parcel)
   cityName            cityName
   country             country
   description         description
   iconUrl             iconUrl
   temperature         temperature
   feelsLike           feelsLike
   humidity            humidity
   pressure            pressure
   windSpeed           windSpeed
   visibility          visibility
   dateTime            dateTime
```

### **Flow de date:**
1. **JSON** (API) → **WeatherParser** → **WeatherItem** (creare cu setters)
2. **WeatherParser** → **List<WeatherItem>** (40 ore)
3. **WeatherParser** → **parseWeatherByDay()** → **List<DailyWeatherItem>** (agregare)
4. **DailyWeatherAdapter** → **RecyclerView** (afișare grafice)
5. **Click** → **Intent.putExtra()** → **writeToParcel()** (serializare)
6. **WeatherDetailActivity** → **getParcelableExtra()** → **CREATOR.createFromParcel()** (deserializare)
7. **WeatherDetailActivity** → Afișare detalii

### **Utilizare în aplicație:**
- **WeatherParser** - Creare din JSON
- **WeatherAdapter** - Afișare listă orară
- **DailyWeatherItem** - Agregare pentru zi
- **DailyWeatherAdapter** - Grafice și iconițe
- **WeatherDetailActivity** - Detalii pentru o oră
- **Intent** - Transmitere între Activity-uri

### **Îmbunătățiri posibile:**
- Validare în setteri (range checking)
- Imutabilitate (final fields + constructor complet)
- Builder pattern pentru creare mai clară
- Metode helper pentru formatare
- Override toString(), equals(), hashCode()
- Null-safety pentru câmpuri String

Clasa `WeatherItem` este un exemplu clasic de **data model class** în Android, implementând corect pattern-ul Parcelable pentru performanță maximă în transmiterea datelor între componente!
