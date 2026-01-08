# WeatherAdapter.java — Documentație detaliată a codului sursă

Acest document prezintă codul complet al clasei `WeatherAdapter.java` împărțit în fragmente logice, fiecare precedat de explicații detaliate în limba română. Documentația este destinată programatorilor cu cunoștințe de Java dar cu experiență limitată în programarea Android.

## Context general

`WeatherAdapter` este un adapter simplu pentru RecyclerView care afișează o listă plată de previziuni meteo orare. Spre deosebire de `DailyWeatherAdapter` care grupează datele pe zile și afișează grafice, acest adapter:

1. **Afișare simplă liniară** - O listă scrollabilă de previziuni individuale
2. **Layout item compact** - Iconiță, oraș, temperatură, descriere, dată/oră
3. **Glide pentru imagini** - Încărcare eficientă a iconiței meteo
4. **Capitalizare text** - Prima literă mare pentru descriere
5. **Click navigation** - Deschide WeatherDetailActivity la click
6. **Update data** - Actualizare liste cu notifyDataSetChanged()

Este un exemplu clasic de RecyclerView adapter pentru liste simple.

## 1. Declararea pachetului

Clasa aparține sub-pachetului `adapter` din aplicație.

```java
package ro.makore.akrilki_08.adapter;
```

**Explicație:**
- Sub-pachetul `adapter` grupează toate adaptoarele pentru RecyclerView
- Organizare logică: separare responsabilități (adapters vs models vs activities)

## 2. Import-uri pentru componente Android de bază

```java
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import ro.makore.akrilki_08.R;
import android.util.Log;
```

**Linie cu linie:**
- **`Context`** - Pentru acces la resurse și pornirea Activity-urilor
- **`Intent`** - Pentru navigare către WeatherDetailActivity
- **`LayoutInflater`** - Pentru transformarea XML în obiecte View
- **`View`** - Clasa de bază pentru toate componentele UI
- **`ViewGroup`** - Container pentru View-uri (RecyclerView este un ViewGroup)
- **`ImageView`** - Pentru afișarea iconiței meteo
- **`TextView`** - Pentru afișarea textului (oraș, temperatură, descriere, dată)
- **`@NonNull`** - Adnotare pentru parametri care nu pot fi null
- **`R`** - Clasa generată automat cu ID-uri de resurse
- **`Log`** - Pentru logging/debugging

## 3. Import-uri pentru RecyclerView și model

```java
import androidx.recyclerview.widget.RecyclerView;
import ro.makore.akrilki_08.model.WeatherItem;
import ro.makore.akrilki_08.WeatherDetailActivity;
```

**Linie cu linie:**
- **`RecyclerView`** - Componentă pentru liste scrollabile eficiente
- **`WeatherItem`** - Model de date pentru vremea la un moment specific
- **`WeatherDetailActivity`** - Activity-ul pentru afișarea detaliilor

## 4. Import pentru Glide - Library de încărcare imagini

```java
import com.bumptech.glide.Glide;
```

**Explicație:**
- **Glide** - Bibliotecă populară pentru încărcarea și cache-uirea imaginilor
- Alternativă la download manual cu OkHttp (folosit în DailyWeatherAdapter)
- Avantaje: Cache automat, gestionare lifecycle, transformări

## 5. Import pentru colecții

```java
import java.util.List;
```

**Explicație:**
- **`List`** - Interfața pentru liste (ArrayList, LinkedList, etc.)

## 6. Declararea clasei și câmpurilor membre

### Declararea clasei

```java
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
```

**Explicație:**
- Extinde `RecyclerView.Adapter` - Clasa de bază pentru adaptoare
- **Parametru generic**: `<WeatherViewHolder>` - Tipul de ViewHolder folosit
- Pattern standard Android pentru afișare liste

### Câmpuri membre

```java
    private final Context context;
    private final List<WeatherItem> weatherItemList;
```

**Linie cu linie:**
- **`context`** - Context pentru acces la resurse, pornire Activity-uri, Glide
- **`weatherItemList`** - Lista de previziuni meteo care va fi afișată
- `final` - Referințele nu se schimbă (dar conținutul listei da)

## 7. Constructorul

```java
    public WeatherAdapter(Context context, List<WeatherItem> weatherItemList) {
        this.context = context;
        this.weatherItemList = weatherItemList;
    }
```

**Explicație:**
- Primește și salvează context-ul și lista de date
- **Important**: Lista trebuie să fie aceeași referință pentru `updateData()` să funcționeze

## 8. Metoda updateData() - Actualizarea datelor

```java
    public void updateData(List<WeatherItem> weatherItemList) {
        Log.v("WEATHER08", "Updating data");
        this.weatherItemList.clear();
        this.weatherItemList.addAll(weatherItemList);
        notifyDataSetChanged(); // Refresh the RecyclerView
    }
```

**Linie cu linie:**
- Metodă apelată când avem date noi (după refresh sau schimbare oraș)
- `Log.v()` - Verbose logging pentru debugging
- `clear()` - Golește lista existentă
- `addAll()` - Adaugă toate elementele noi din lista primită
- **Pattern**: Modificăm lista existentă, nu înlocuim referința
- `notifyDataSetChanged()` - Notifică RecyclerView-ul să se redeseneze complet
- **Note**: Pentru performance mai bună, ar trebui folosit DiffUtil, dar pentru liste mici e OK

## 9. Metoda onCreateViewHolder() - Crearea ViewHolder-ului

```java
    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_weather, parent, false);
        return new WeatherViewHolder(view);
    }
```

**Linie cu linie:**
- `@Override` - Suprascriem metoda din RecyclerView.Adapter
- `@NonNull` - Garantăm că nu returnăm null
- **Parametri**: 
  - `parent` = RecyclerView-ul (pentru parametri corecți de layout)
  - `viewType` = Tip de item (nu folosim, avem un singur tip)
- `LayoutInflater.from(context)` - Obținem un inflater din context
- `inflate(R.layout.item_weather, parent, false)` - Transformă XML în View
  - **Layout**: `item_weather.xml` - Layout-ul unui item individual
  - **parent**: RecyclerView-ul (pentru width/height corecte)
  - **false**: Nu atașa View-ul la parent încă (RecyclerView o face singur)
- Creăm și returnăm un ViewHolder nou cu view-ul inflated
- **Apelat**: Când RecyclerView are nevoie de un ViewHolder nou (nu există unul disponibil pentru recycling)

## 10. Metoda onBindViewHolder() - Popularea UI-ului cu date

### Semnătura metodei și extragerea datelor

```java
    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        WeatherItem weatherItem = weatherItemList.get(position);
```

**Linie cu linie:**
- `@Override` - Suprascriem metoda din Adapter
- **Parametri**: 
  - `holder` = ViewHolder-ul de populat
  - `position` = Index-ul în listă (0, 1, 2, ...)
- Extragem item-ul de vreme de la poziția curentă
- **Apelat**: Pentru fiecare item vizibil și când un item devine vizibil prin scroll

### Încărcarea iconiței cu Glide

```java
        // Load weather icon using Glide
        if (weatherItem.getIconUrl() != null && !weatherItem.getIconUrl().isEmpty()) {
            Glide.with(context)
                .load(weatherItem.getIconUrl())
                .into(holder.iconImageView);
        }
```

**Linie cu linie:**
- Verificăm că URL-ul iconiței există și nu e gol
- **`Glide.with(context)`** - Creăm un RequestManager legat de lifecycle-ul context-ului
  - Dacă context-ul e Activity și e destroyed, Glide anulează automat request-ul
- **`load(iconUrl)`** - Specifică URL-ul de încărcat
  - **Exemplu**: "https://openweathermap.org/img/wn/01d@2x.png"
- **`into(holder.iconImageView)`** - Specifică ImageView-ul destinație
- **Magie Glide**: 
  - Download în background thread
  - Decode bitmap
  - Cache pe disk și în memorie
  - Set bitmap pe main thread
  - Placeholder și error handling automat (dacă sunt configurate)
- **Avantaj**: Un singur lanț de apeluri face tot ce făcea OkHttp manual în DailyWeatherAdapter

### Setarea numelui orașului și țării

```java
        // Set city name and country
        String cityCountry = weatherItem.getCityName();
        if (weatherItem.getCountry() != null && !weatherItem.getCountry().isEmpty()) {
            cityCountry += ", " + weatherItem.getCountry();
        }
        holder.cityTextView.setText(cityCountry);
```

**Linie cu linie:**
- Începem cu numele orașului
- Dacă există și țara, o adăugăm cu virgulă
- **Rezultat**: "Bucharest, RO" sau doar "Bucharest" dacă țara lipsește
- Setăm textul în TextView
- **Design**: Fiecare item afișează orașul (spre deosebire de DailyWeatherAdapter care îl afișa doar odată)

### Setarea temperaturii

```java
        // Set temperature
        holder.temperatureTextView.setText(String.format("%.1f°C", weatherItem.getTemperature()));
```

**Linie cu linie:**
- `String.format("%.1f°C", temperature)` - Formatare temperatura
  - **%.1f**: Număr cu o zecimală
  - **°C**: Simbolul gradelor Celsius
  - **Exemplu**: 23.456 → "23.5°C"
- Setăm textul formatat în TextView

### Capitalizarea și setarea descrierii

```java
        // Set description
        if (weatherItem.getDescription() != null && !weatherItem.getDescription().isEmpty()) {
            String description = weatherItem.getDescription();
            // Capitalize first letter
            description = description.substring(0, 1).toUpperCase() + description.substring(1);
            holder.descriptionTextView.setText(description);
        } else {
            holder.descriptionTextView.setText("No description");
        }
```

**Linie cu linie:**
- Verificăm că descrierea există și nu e goală
- **Capitalizare**: Prima literă mare pentru aspect mai profesional
  - `substring(0, 1)` - Primul caracter
  - `toUpperCase()` - Transformă în majusculă
  - `substring(1)` - Restul string-ului (de la index 1 până la sfârșit)
  - Concatenare: "C" + "lear sky" = "Clear sky"
- **Transformare**: "clear sky" → "Clear sky"
- Dacă descrierea lipsește, afișăm "No description"
- **Note**: API-ul returnează descrieri cu literă mică ("clear sky", "few clouds")

### Setarea datei/orei

```java
        // Set date/time
        holder.dateTimeTextView.setText(weatherItem.getDateTime());
```

**Explicație:**
- Setăm data/ora direct din model
- **Format**: De obicei "HH:mm yyyy-MM-dd" (ex: "15:00 2026-01-08")
- Formatarea e făcută deja în parser (WeatherParser.java)

### Click listener pentru navigare la detalii

```java
        // Handle click event to pass the WeatherItem to WeatherDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WeatherDetailActivity.class);
            if (weatherItem != null) {
                intent.putExtra("weather_item", weatherItem); // Pass the WeatherItem to the next activity
            }
            context.startActivity(intent);
        });
    }
```

**Linie cu linie:**
- `holder.itemView` - Întregul item (View-ul rădăcină din layout)
- Lambda expression pentru click listener
- Creăm Intent pentru WeatherDetailActivity
- Verificăm că weatherItem nu e null (defensive programming)
- `putExtra("weather_item", weatherItem)` - Adăugăm WeatherItem la Intent
  - **Necesar**: WeatherItem trebuie să implementeze Parcelable
  - Cheia "weather_item" va fi folosită în WeatherDetailActivity pentru extragere
- `startActivity(intent)` - Pornim Activity-ul de detalii
- **Flow**: Click → Intent cu date → WeatherDetailActivity afișează detalii complete

## 11. Metoda getItemCount() - Numărul de elemente

```java
    @Override
    public int getItemCount() {
        return weatherItemList.size();
    }
```

**Explicație:**
- Metodă obligatorie din RecyclerView.Adapter
- Returnează numărul total de elemente din listă
- RecyclerView folosește acest număr pentru:
  - Calcularea înălțimii totale (pentru scrollbar)
  - Determinarea când să oprească scrolling-ul
  - Optimizarea recycling-ului

## 12. Clasa internă WeatherViewHolder - ViewHolder pattern

### Declararea clasei și câmpurilor

```java
    // ViewHolder for each weather item
    public static class WeatherViewHolder extends RecyclerView.ViewHolder {

        private final ImageView iconImageView;
        private final TextView cityTextView;
        private final TextView temperatureTextView;
        private final TextView descriptionTextView;
        private final TextView dateTimeTextView;
```

**Linie cu linie:**
- `public static` - Clasă statică internă (nu necesită instanță a clasei externe)
- Extinde `RecyclerView.ViewHolder` - Pattern standard
- **Scop**: Păstrează referințe la toate View-urile din item layout
- Cinci câmpuri pentru cele cinci componente UI:
  1. **iconImageView** - Iconița meteo
  2. **cityTextView** - Orașul și țara
  3. **temperatureTextView** - Temperatura
  4. **descriptionTextView** - Descrierea vremii
  5. **dateTimeTextView** - Data și ora

### Constructorul ViewHolder-ului

```java
        public WeatherViewHolder(View itemView) {
            super(itemView);

            // Initialize views
            iconImageView = itemView.findViewById(R.id.weather_icon);
            cityTextView = itemView.findViewById(R.id.city_name);
            temperatureTextView = itemView.findViewById(R.id.temperature);
            descriptionTextView = itemView.findViewById(R.id.description);
            dateTimeTextView = itemView.findViewById(R.id.datetime);
        }
    }

}
```

**Linie cu linie:**
- Primește View-ul inflated (item_weather din XML)
- `super(itemView)` - Apelează constructorul ViewHolder (păstrează referința la itemView)
- `findViewById()` pentru fiecare componentă din layout
- **ID-uri**: Definite în item_weather.xml (R.id.weather_icon, etc.)
- **Pattern**: Inițializare o singură dată (la creare ViewHolder)
- **Performance**: 
  - findViewById e costisitor (parcurge tree-ul de View-uri)
  - Facem o singură dată per ViewHolder
  - ViewHolder-ul e recycled → findViewById nu se mai apelează la scroll
- **Exemplu**: Pentru 100 de elemente cu 10 vizibile, findViewById se apelează ~12 ori (nu 100!)

---

## Rezumat

Acest adapter este un exemplu clasic și simplu de RecyclerView adapter:

### **Scop principal**
- Afișează o listă scrollabilă de previziuni meteo individuale
- Binding simplu date → UI pentru fiecare item

### **Funcționalități cheie:**
1. **RecyclerView pattern** - Adapter cu ViewHolder pentru eficiență
2. **Glide integration** - Încărcare simplă și eficientă a imaginilor
3. **Text formatting** - Capitalizare descriere, formatare temperatură
4. **Click navigation** - Intent către WeatherDetailActivity
5. **Update data** - Actualizare listă cu notifyDataSetChanged()

### **Diferențe față de DailyWeatherAdapter:**

| Aspect | WeatherAdapter | DailyWeatherAdapter |
|--------|----------------|---------------------|
| **Date** | List\<WeatherItem\> (plată) | List\<DailyWeatherItem\> (grupată) |
| **Afișare** | Listă simplă | Grafice + overlay iconiți |
| **Imagini** | Glide | OkHttp manual |
| **Complexitate** | Simplă (~115 linii) | Complexă (~394 linii) |
| **Layout** | TextView-uri simple | FrameLayout + LineChart |
| **Oraș** | Pe fiecare item | Doar pe primul item |

### **Pattern-uri implementate:**

**RecyclerView Adapter Pattern:**
- `onCreateViewHolder()` - Creează ViewHolder-i (inflatează layout)
- `onBindViewHolder()` - Populează ViewHolder cu date
- `getItemCount()` - Returnează număr total de elemente
- `updateData()` - Actualizare date cu notificare

**ViewHolder Pattern:**
- Păstrează referințe la View-uri pentru refolosire
- Evită findViewById() repetat (optimization majore)
- Static inner class pentru memory efficiency
- findViewById doar la creare, nu la scroll

**Glide Pattern:**
- Fluent API: `Glide.with().load().into()`
- Lifecycle-aware (anulare automată la destroy)
- Cache automat (memorie + disk)
- Background threading automat

### **Flow-ul de afișare:**

1. **RecyclerView** → are nevoie de un ViewHolder nou
2. **onCreateViewHolder()** → inflatează item_weather.xml → creează WeatherViewHolder
3. **ViewHolder** → salvează referințe la toate View-urile (findViewById × 5)
4. **RecyclerView** → apelează onBindViewHolder() pentru fiecare item vizibil
5. **Pentru fiecare item**:
   - Încarcă iconița cu Glide
   - Setează orașul și țara
   - Formatează și setează temperatura
   - Capitalizează și setează descrierea
   - Setează data/ora
   - Atașează click listener pentru navigare
6. **La scroll**: RecyclerView refolosește ViewHolder-i existenți (recycling)
7. **Click pe item** → Intent cu WeatherItem → WeatherDetailActivity

### **Formatarea datelor:**

| Câmp | Input (din model) | Output (afișat) | Transformare |
|------|-------------------|-----------------|--------------|
| Icon | "https://.../01d@2x.png" | [imagine] | Glide download |
| City | "Bucharest" + "RO" | "Bucharest, RO" | Concatenare |
| Temperature | 23.456 | "23.5°C" | String.format("%.1f°C") |
| Description | "clear sky" | "Clear sky" | Capitalizare primă literă |
| DateTime | "15:00 2026-01-08" | "15:00 2026-01-08" | Direct |

### **Glide vs OkHttp manual:**

**Avantaje Glide (folosit aici):**
- ✅ Cod minimal (3 linii vs ~80 linii)
- ✅ Cache automat (memorie + disk)
- ✅ Lifecycle management (nu memory leaks)
- ✅ Placeholder & error handling configurable
- ✅ Transformări (crop, resize, filters) simple
- ✅ Request priority & throttling

**OkHttp manual (DailyWeatherAdapter):**
- ✅ Control total (SSL fallback, custom headers)
- ✅ Refolosire client existent (WeatherAPI)
- ✅ Debugging mai ușor (log-uri custom)
- ❌ Mai mult cod boilerplate
- ❌ Thread management manual
- ❌ Cache manual dacă e necesar

### **Performance & Optimizări:**

**ViewHolder Pattern:**
- findViewById apelat doar la creare (~10-12 ori pentru ecran)
- Recycling: Pentru 1000 elemente, doar ~15 ViewHolder-i creați
- Scroll ultra-smooth (60fps) chiar pentru liste mari

**Glide Optimizations:**
- Downsampling automat (nu încarcă imagine de 2MB pentru 48×48px)
- Cache multi-nivel (memorie → disk → network)
- Request deduplication (aceeași imagine cerută de 10 ori = 1 download)
- Lifecycle integration (anulare automată când Activity se închide)

**Data Update:**
- `notifyDataSetChanged()` - Simplu dar re-bind toate item-urile vizibile
- **Alternativa**: DiffUtil pentru update selective (mai eficient pentru liste mari)
- Pentru liste de 10-50 elemente, `notifyDataSetChanged()` e perfect OK

### **Click Navigation Pattern:**

```
User tap pe item
    ↓
Click listener (setOnClickListener)
    ↓
Creează Intent cu WeatherItem ca extra (Parcelable)
    ↓
startActivity(intent)
    ↓
WeatherDetailActivity.onCreate()
    ↓
getIntent().getParcelableExtra("weather_item")
    ↓
Populează UI cu detalii complete
```

### **Layout Structure (item_weather.xml):**

```
[LinearLayout] - vertical
    [ImageView] - weather_icon
    [TextView] - city_name
    [TextView] - temperature
    [TextView] - description
    [TextView] - datetime
```

### **Use Cases:**

1. **MainActivity cu lista plată** - Folosește WeatherAdapter
   - Afișare simplă, rapidă
   - Toate previziunile într-o listă
   - Scroll vertical prin toate orele

2. **MainActivity cu grupare pe zile** - Folosește DailyWeatherAdapter
   - Afișare cu grafice
   - Mai vizual, mai mult spațiu
   - Grouping logic (zile)

Această clasă demonstrează implementarea simplă și eficientă a pattern-ului RecyclerView cu Glide pentru o experiență UX fluidă și responsive!
