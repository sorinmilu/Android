# WeatherDetailActivity.java — Documentație detaliată a codului sursă

Acest document prezintă codul complet al clasei `WeatherDetailActivity.java` împărțit în fragmente logice, fiecare precedat de explicații detaliate în limba română. Documentația este destinată programatorilor cu cunoștințe de Java dar cu experiență limitată în programarea Android.

## Context general

`WeatherDetailActivity` este o activitate secundară (ecran) care afișează detalii complete despre vremea pentru un moment specific de timp. Utilizatorul ajunge pe acest ecran atunci când apasă pe un element din lista principală. Activitatea primește un obiect `WeatherItem` prin `Intent` și afișează toate informațiile meteo disponibile: temperatură, umiditate, presiune, viteză vânt, vizibilitate, etc.

## 1. Declararea pachetului

Clasa aparține aceluiași pachet cu celelalte clase ale aplicației.

```java
package ro.makore.akrilki_08;
```

**Explicație:**
- Toate clasele aplicației de vreme sunt în același namespace
- Permite accesul direct la alte clase din aplicație fără import explicit

## 2. Import-uri pentru componente Android de bază

Import-uri pentru widget-uri și funcționalități fundamentale:

```java
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.util.Log;
```

**Linie cu linie:**
- **`Bundle`** - Container pentru transmiterea datelor între componente (salvare/restaurare stare)
- **`TextView`** - Widget pentru afișarea textului (temperatură, descriere, etc.)
- **`ImageView`** - Widget pentru afișarea imaginilor (iconița meteo)
- **`Button`** - Buton pentru acțiuni (butonul "Back")
- **`LinearLayout`** - Layout manager care aranjează view-uri în linie
- **`Log`** - Sistem de logging pentru debug (erori de încărcare imagini)

## 3. Import-uri pentru modele de date

```java
import ro.makore.akrilki_08.model.WeatherItem;
```

**Explicație:**
- **`WeatherItem`** - Clasa model care conține toate datele meteo pentru un moment specific
- Este un obiect `Parcelable` care poate fi transmis prin Intent-uri între activități

## 4. Import-uri pentru biblioteca Glide

Glide și clase asociate pentru încărcarea imaginilor:

```java
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.load.engine.GlideException;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.load.DataSource;
```

**Linie cu linie:**
- **`Glide`** - Clasa principală pentru încărcarea imaginilor
- **`RequestListener`** - Interfață pentru callback-uri când imaginea se încarcă (succes/eșec)
- **`GlideException`** - Excepție aruncată când încărcarea imaginii eșuează
- **`Drawable`** - Clasă Android care reprezintă o imagine/grafică
- **`@Nullable`** - Adnotare care indică că un parametru poate fi null
- **`Target`** - Destinația unde se va încărca imaginea
- **`DataSource`** - Sursa de unde a venit imaginea (cache, rețea, etc.)

## 5. Import pentru AppCompatActivity

```java
import androidx.appcompat.app.AppCompatActivity;
```

**Explicație:**
- **`AppCompatActivity`** - Clasa de bază pentru activități moderne cu backward compatibility
- Oferă suport pentru Material Design și funcționalități moderne pe versiuni vechi de Android

## 6. Declararea clasei și câmpurile membre

### Declararea clasei

```java
public class WeatherDetailActivity extends AppCompatActivity {
```

**Explicație:**
- Clasa extinde `AppCompatActivity` pentru funcționalitate completă de activitate
- Este activitatea secundară care afișează detaliile vremii

### Referințe către componentele UI

Declararea tuturor câmpurilor pentru widget-urile din interfață:

```java
    private TextView cityTextView;
    private TextView temperatureTextView;
    private TextView feelsLikeTextView;
    private TextView descriptionTextView;
    private TextView humidityTextView;
    private TextView pressureTextView;
    private TextView windSpeedTextView;
    private TextView visibilityTextView;
    private TextView dateTimeTextView;
    private ImageView weatherIconImageView;
    private Button backButton;
```

**Linie cu linie:**
- **`cityTextView`** - Afișează numele orașului și țara
- **`temperatureTextView`** - Afișează temperatura curentă în grade Celsius
- **`feelsLikeTextView`** - Afișează temperatura resimțită ("feels like")
- **`descriptionTextView`** - Afișează descrierea vremii (senin, înnorat, ploaie, etc.)
- **`humidityTextView`** - Afișează umiditatea în procente
- **`pressureTextView`** - Afișează presiunea atmosferică în hPa
- **`windSpeedTextView`** - Afișează viteza vântului în m/s
- **`visibilityTextView`** - Afișează vizibilitatea în kilometri
- **`dateTimeTextView`** - Afișează data și ora pentru care sunt afișate datele
- **`weatherIconImageView`** - Afișează iconița meteo corespunzătoare
- **`backButton`** - Buton pentru întoarcerea la ecranul principal
- Toate sunt `private` deoarece sunt folosite doar în interiorul acestei clase

## 7. Metoda onCreate() - Inițializarea activității

Metoda principală care configurează interfața și afișează datele meteo.

### Apelarea constructorului părinte și setarea layout-ului

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);
```

**Linie cu linie:**
- `@Override` - Suprascriem metoda din clasa părinte
- `super.onCreate(savedInstanceState)` - Apelăm metoda părinte pentru inițializare standard
- `setContentView(R.layout.activity_weather_detail)` - Încarcă layout-ul XML pentru acest ecran

### Găsirea și inițializarea tuturor componentelor UI

```java
        cityTextView = findViewById(R.id.cityTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        feelsLikeTextView = findViewById(R.id.feelsLikeTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        pressureTextView = findViewById(R.id.pressureTextView);
        windSpeedTextView = findViewById(R.id.windSpeedTextView);
        visibilityTextView = findViewById(R.id.visibilityTextView);
        dateTimeTextView = findViewById(R.id.dateTimeTextView);
        weatherIconImageView = findViewById(R.id.weatherIconImageView);
        backButton = findViewById(R.id.backButton);
```

**Explicație:**
- `findViewById()` - Căutăm fiecare componentă în layout după ID-ul ei unic
- Salvăm referințele în câmpurile membre pentru a le putea accesa ulterior
- Fiecare linie conectează un câmp Java la un element XML din layout

### Extragerea obiectului WeatherItem din Intent

```java
        WeatherItem weatherItem = getIntent().getParcelableExtra("weather_item");
```

**Linie cu linie:**
- `getIntent()` - Obține Intent-ul care a pornit această activitate
- `getParcelableExtra("weather_item")` - Extrage obiectul Parcelable cu cheia "weather_item"
- Intent-ul a fost trimis de `MainActivity` când utilizatorul a apăsat pe un element
- `weatherItem` conține toate datele meteo pentru momentul selectat

### Verificarea dacă datele sunt valide

```java
        if (weatherItem != null) {
```

**Explicație:**
- Verificăm că obiectul primit nu este `null`
- Dacă este `null`, afișăm un mesaj de eroare (vezi secțiunea `else` de mai jos)
- Best practice: întotdeauna verificăm datele primite prin Intent-uri

## 8. Popularea UI-ului cu datele meteo

Următoarea secțiune populează fiecare TextView cu informațiile corespunzătoare.

### Afișarea numelui orașului și țării

```java
            // Set city and country
            String cityCountry = weatherItem.getCityName();
            if (weatherItem.getCountry() != null && !weatherItem.getCountry().isEmpty()) {
                cityCountry += ", " + weatherItem.getCountry();
            }
            cityTextView.setText(cityCountry);
```

**Linie cu linie:**
- Obținem numele orașului cu `getCityName()`
- Verificăm dacă există și țara (`getCountry()` nu este null și nu este string gol)
- Dacă da, adăugăm țara după oraș, separată de virgulă (ex: "București, RO")
- Operatorul `+=` concatenează stringul țării la stringul orașului
- `setText()` - Setează textul final pe TextView

### Afișarea temperaturii

```java
            // Set temperature
            temperatureTextView.setText(String.format("%.1f°C", weatherItem.getTemperature()));
```

**Linie cu linie:**
- `String.format("%.1f°C", ...)` - Formatează temperatura cu o zecimală
- `%.1f` - Format specifier: număr în virgulă mobilă cu 1 zecimală
- `°C` - Simbolul gradelor Celsius
- Rezultat: "23.5°C"

### Afișarea temperaturii resimțite

```java
            // Set feels like
            feelsLikeTextView.setText(String.format("Feels like: %.1f°C", weatherItem.getFeelsLike()));
```

**Explicație:**
- Similar cu temperatura, dar afișează și un label "Feels like:"
- Temperatura resimțită ține cont de umiditate și vânt
- Rezultat: "Feels like: 21.3°C"

### Afișarea și formatarea descrierii

```java
            // Set description
            if (weatherItem.getDescription() != null && !weatherItem.getDescription().isEmpty()) {
                String description = weatherItem.getDescription();
                description = description.substring(0, 1).toUpperCase() + description.substring(1);
                descriptionTextView.setText(description);
            } else {
                descriptionTextView.setText("No description");
            }
```

**Linie cu linie:**
- Verificăm că descrierea există și nu este goală
- `substring(0, 1)` - Extrage primul caracter (index 0)
- `toUpperCase()` - Transformă primul caracter în majusculă
- `substring(1)` - Extrage restul string-ului (de la index 1 până la final)
- Concatenăm: prima literă mare + restul string-ului
- Transformă "scattered clouds" în "Scattered clouds"
- Dacă descrierea lipsește, afișăm "No description"

### Afișarea umidității

```java
            // Set humidity
            humidityTextView.setText(String.format("Humidity: %.0f%%", weatherItem.getHumidity()));
```

**Linie cu linie:**
- `%.0f` - Format pentru număr fără zecimale
- `%%` - Două procente pentru a afișa un singur simbol % (escape în format string)
- Rezultat: "Humidity: 65%"

### Afișarea presiunii atmosferice

```java
            // Set pressure
            pressureTextView.setText(String.format("Pressure: %.0f hPa", weatherItem.getPressure()));
```

**Explicație:**
- Presiunea în hectoPascali (hPa), fără zecimale
- Rezultat: "Pressure: 1013 hPa"

### Afișarea vitezei vântului

```java
            // Set wind speed
            windSpeedTextView.setText(String.format("Wind Speed: %.1f m/s", weatherItem.getWindSpeed()));
```

**Explicație:**
- Viteza vântului în metri pe secundă, cu o zecimală
- Rezultat: "Wind Speed: 3.5 m/s"

### Afișarea vizibilității

```java
            // Set visibility
            visibilityTextView.setText(String.format("Visibility: %.1f km", weatherItem.getVisibility()));
```

**Explicație:**
- Vizibilitatea în kilometri, cu o zecimală
- Rezultat: "Visibility: 10.0 km"

### Afișarea datei și orei

```java
            // Set date/time
            dateTimeTextView.setText(weatherItem.getDateTime());
```

**Explicație:**
- Afișează string-ul cu data și ora deja formatat în obiectul `WeatherItem`
- Format probabil: "2026-01-08 14:00" sau similar

## 9. Încărcarea și afișarea iconiței meteo cu Glide

Această secțiune încarcă imaginea meteo de pe internet și o afișează proporțional.

### Verificarea URL-ului iconiței

```java
            // Load weather icon using Glide
            if (weatherItem.getIconUrl() != null && !weatherItem.getIconUrl().isEmpty()) {
```

**Explicație:**
- Verificăm că URL-ul iconiței există și nu este gol
- Dacă lipsește, nu încărcăm nimic (ImageView rămâne gol)

### Inițierea încărcării cu Glide

```java
                Glide.with(WeatherDetailActivity.this)
                    .load(weatherItem.getIconUrl())
                    .listener(new RequestListener<Drawable>() {
```

**Linie cu linie:**
- `Glide.with(WeatherDetailActivity.this)` - Inițializează Glide cu context-ul activității
- `.load(weatherItem.getIconUrl())` - Specifică URL-ul imaginii de încărcat
- `.listener(...)` - Atașează un listener pentru a monitoriza încărcarea
- `new RequestListener<Drawable>()` - Crează instanță anonimă a listener-ului
- `<Drawable>` - Tipul generic: rezultatul va fi un Drawable (imagine)

### Callback pentru eșecul încărcării

```java
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("Glide", "Image load failed", e);
                            return false; // Allow Glide to handle the error
                        }
```

**Linie cu linie:**
- `onLoadFailed()` - Metodă apelată când încărcarea eșuează
- `@Nullable GlideException e` - Excepția care descrie eroarea (poate fi null)
- `Log.e("Glide", "Image load failed", e)` - Scrie eroarea în logcat pentru debug
- `return false` - Permite Glide să gestioneze eroarea (afișează placeholder sau lasă gol)
- `return true` ar însemna că noi am gestionat eroarea complet

### Callback pentru succesul încărcării

```java
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
```

**Explicație:**
- `onResourceReady()` - Metodă apelată când imaginea a fost încărcată cu succes
- `Drawable resource` - Imaginea încărcată
- `DataSource dataSource` - De unde a venit imaginea (cache, rețea)

### Ajustarea proporțiilor ImageView-ului

```java
                            weatherIconImageView.post(() -> {
```

**Explicație:**
- `post()` - Adaugă un Runnable în coada de mesaje a view-ului
- Asigură că operația se execută după ce view-ul este complet măsurat și desenat
- Necesar pentru a avea acces la dimensiunile reale ale view-ului

### Calcularea dimensiunilor proporționale

```java
                                // Get the intrinsic dimensions of the loaded image
                                int intrinsicWidth = resource.getIntrinsicWidth();
                                int intrinsicHeight = resource.getIntrinsicHeight();
                
                                // Get the width of the ImageView
                                int viewWidth = weatherIconImageView.getWidth();
                
                                // Calculate the proportional height based on the image aspect ratio
                                int viewHeight = (int) ((float) intrinsicHeight / intrinsicWidth * viewWidth);
```

**Linie cu linie:**
- `getIntrinsicWidth()` - Obține lățimea originală a imaginii în pixeli
- `getIntrinsicHeight()` - Obține înălțimea originală a imaginii în pixeli
- `getWidth()` - Obține lățimea curentă a ImageView-ului (setată în layout)
- **Calculul proporțional:**
  - `intrinsicHeight / intrinsicWidth` - Raportul aspect al imaginii (de ex. 0.75 pentru 4:3)
  - `* viewWidth` - Înmulțim cu lățimea view-ului pentru a obține înălțimea proporțională
  - `(float)` - Cast la float pentru calcul cu zecimale precis
  - `(int)` - Cast final la int pentru parametrii de layout
- Rezultat: imaginea păstrează proporțiile originale, fără distorsiune

### Aplicarea noilor dimensiuni

```java
                                // Update the ImageView layout parameters
                                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) weatherIconImageView.getLayoutParams();
                                params.height = viewHeight;
                                weatherIconImageView.setLayoutParams(params);
                            });
```

**Linie cu linie:**
- `getLayoutParams()` - Obține parametrii de layout actuali ai ImageView-ului
- Cast la `LinearLayout.LayoutParams` - Tipul specific de parametri (părintele este LinearLayout)
- `params.height = viewHeight` - Setează înălțimea calculată
- `setLayoutParams(params)` - Aplică parametrii modificați pe ImageView
- ImageView-ul va fi redesenat cu noile dimensiuni

### Finalizarea încărcării Glide

```java
                            return false; // Allow Glide to set the resource on the ImageView
                        }
                    })
                    .into(weatherIconImageView);
            }
```

**Linie cu linie:**
- `return false` - Permite Glide să seteze imaginea pe ImageView automat
- `return true` ar însemna că am setat imaginea manual
- `.into(weatherIconImageView)` - Specifică destinația finală (unde să afișeze imaginea)
- Fără `.into()`, Glide nu va încărca efectiv imaginea!

## 10. Gestionarea cazului când datele lipsesc

```java
        } else {
            cityTextView.setText("No weather data available");
            descriptionTextView.setText("Please try again");
        }
```

**Explicație:**
- Blocul `else` se execută dacă `weatherItem` este `null`
- Afișăm mesaje de eroare în loc de date
- UI-ul rămâne funcțional chiar și cu date lipsă

## 11. Configurarea butonului Back

```java
        backButton.setOnClickListener(v -> finish());
    }
```

**Linie cu linie:**
- `setOnClickListener()` - Setează ce se întâmplă la click pe buton
- Lambda expression `v -> finish()` - Funcție anonimă cu un parametru (View-ul apăsat)
- `finish()` - Închide activitatea curentă și se întoarce la activitatea anterioară (MainActivity)
- Android gestionează automat stack-ul de activități

### Închiderea clasei

```java
}
```

**Explicație:**
- Acoladă închisă care marchează sfârșitul clasei `WeatherDetailActivity`

---

## Rezumat

Această activitate oferă o vizualizare detaliată a datelor meteo:

### **Scop principal**
- Afișarea tuturor detaliilor meteo pentru un moment specific de timp
- Ecran secundar accesat prin click pe un element din lista principală

### **Funcționalități cheie:**
1. **Primește date prin Intent** - Obiectul `WeatherItem` Parcelable
2. **Populează 11 câmpuri UI** - Oraș, temperatură, umiditate, presiune, vânt, etc.
3. **Formatare inteligentă** - Capitalizare, zecimale controlate, unități de măsură
4. **Încărcare imagine cu Glide** - Cu ajustare proporțională automată
5. **Gestionare erori** - Mesaje de fallback când datele lipsesc
6. **Navigare simplă** - Buton Back pentru întoarcere

### **Detalii tehnice importante:**
- **Aspect ratio preservation** - Imaginea nu se distorsionează
- **Null safety** - Verificări pentru toate câmpurile care pot lipsi
- **Thread safety** - Folosește `post()` pentru modificări UI după încărcare asincronă
- **Listener pattern** - Glide RequestListener pentru monitorizarea încărcării

### **Flow-ul utilizatorului:**
1. Utilizatorul apasă pe un element în MainActivity
2. MainActivity creează Intent cu WeatherItem și pornește WeatherDetailActivity
3. WeatherDetailActivity extrage datele și populează UI-ul
4. Utilizatorul vede toate detaliile meteo
5. Apasă Back și se întoarce la MainActivity
