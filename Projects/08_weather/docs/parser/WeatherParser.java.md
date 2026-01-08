# WeatherParser.java — Documentație detaliată a codului sursă

Acest document prezintă codul complet al clasei `WeatherParser.java` împărțit în fragmente logice, fiecare precedat de explicații detaliate în limba română. Documentația este destinată programatorilor cu cunoștințe de Java dar cu experiență limitată în programarea Android.

## Context general

`WeatherParser` este o clasă utilitară responsabilă cu transformarea (parsarea) răspunsului JSON de la OpenWeatherMap API în obiecte Java pe care aplicația le poate folosi. Această clasă:

1. **Transformă JSON → Obiecte** - Convertește string-uri JSON complexe în obiecte `WeatherItem` și `DailyWeatherItem`
2. **Validează date** - Verifică că răspunsul API conține toate câmpurile necesare
3. **Gestionează erori** - Detectează răspunsuri invalide și aruncă excepții descriptive
4. **Grupează date** - Organizează previziunile orare pe zile
5. **Conversii unități** - Transformă vizibilitatea din metri în kilometri
6. **Formatare date** - Parsează și reformatează date și timp

Este piesa centrală care face legătura între API (date raw JSON) și UI (obiecte Java).

## 1. Declararea pachetului

Clasa aparține sub-pachetului `parser` din aplicație.

```java
package ro.makore.akrilki_08.parser;
```

**Explicație:**
- Sub-pachetul `parser` grupează clasele responsabile cu parsarea datelor
- Separare logică: parsarea este o responsabilitate distinctă de UI sau networking

## 2. Import-uri pentru Gson

Biblioteca Gson pentru parsarea JSON:

```java
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
```

**Linie cu linie:**
- **`Gson`** - Clasa principală Gson pentru conversii JSON ↔ Java
- **`JsonArray`** - Reprezentare pentru array-uri JSON (liste)
- **`JsonObject`** - Reprezentare pentru obiecte JSON (dicționare cheie-valoare)
- **Gson vs org.json**: Gson oferă null-safety mai bună și API mai intuitiv

## 3. Import-uri pentru modelele de date

```java
import ro.makore.akrilki_08.model.WeatherItem;
import ro.makore.akrilki_08.model.DailyWeatherItem;
```

**Explicație:**
- **`WeatherItem`** - Model pentru vremea la un moment specific (o oră)
- **`DailyWeatherItem`** - Model pentru vremea dintr-o zi întreagă (conține listă de WeatherItem-uri)

## 4. Import-uri pentru colecții Java

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
```

**Linie cu linie:**
- **`ArrayList`** - Listă dinamică pentru stocarea elementelor
- **`List`** - Interfața pentru liste
- **`Map`** - Interfață pentru dicționare cheie-valoare
- **`LinkedHashMap`** - Implementare de Map care păstrează ordinea de inserție
  - **Important**: Păstrează zilele în ordine cronologică

## 5. Import-uri pentru ThreeTenABP (lucru cu date)

```java
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.ZoneId;
```

**Linie cu linie:**
- **ThreeTenABP** - Backport al Java 8 Time API pentru Android
- **`Instant`** - Moment specific în timp (timestamp)
- **`LocalDateTime`** - Dată și oră fără timezone
- **`DateTimeFormatter`** - Pentru parsarea și formatarea date
- **`ZoneId`** - Zona de timp (nu folosit direct în cod curent)

## 6. Declararea clasei

```java
public class WeatherParser {
```

**Explicație:**
- Clasă publică, accesibilă din orice pachet
- Conține doar metode statice (nu necesită instanțiere)
- Pattern: Utility class pentru operații stateless

## 7. Metoda parseWeatherByDay() - Parsarea și gruparea pe zile

Metoda principală care parsează JSON-ul și grupează previziunile pe zile.

### Semnătura metodei și inițializare

```java
    // Parse weather and group by day
    public static List<DailyWeatherItem> parseWeatherByDay(String jsonResponse) {
        List<DailyWeatherItem> dailyWeatherItems = new ArrayList<>();
        Gson gson = new Gson();
```

**Linie cu linie:**
- **`public static`** - Metodă publică, statică (apelabilă fără instanță)
- **Return type**: `List<DailyWeatherItem>` - Listă de obiecte cu vreme zilnică
- **Parametru**: `String jsonResponse` - JSON raw de la API
- Creăm lista goală pentru rezultate
- Instanțiem Gson pentru parsare

### Blocul try pentru gestionarea erorilor

```java
        try {
            JsonObject rootObject = gson.fromJson(jsonResponse, JsonObject.class);
```

**Linie cu linie:**
- `try` - Încep blocul de gestionare excepții
- `fromJson()` - Parsează string-ul JSON în JsonObject
- `JsonObject.class` - Tipul dorit de returnat
- Dacă JSON-ul este malformat, Gson aruncă excepție

### Verificarea codului de eroare API

```java
            // Check if response has error
            if (rootObject.has("cod")) {
                String cod = rootObject.get("cod").getAsString();
                if (!cod.equals("200")) {
                    String message = rootObject.has("message") ? rootObject.get("message").getAsString() : "Unknown error";
                    throw new Exception("API Error: " + message + " (Code: " + cod + ")");
                }
            }
```

**Linie cu linie:**
- `has("cod")` - Verifică dacă obiectul conține câmpul "cod"
- `get("cod").getAsString()` - Extrage valoarea ca String
- API-ul returnează "200" pentru succes, alte coduri pentru erori
- Operator ternar pentru mesaj de eroare: dacă există câmpul "message", îl folosim, altfel "Unknown error"
- `throw new Exception()` - Aruncă excepție cu mesaj descriptiv
- **Exemple de coduri**: "404" (oraș negăsit), "401" (cheie API invalidă)

### Extragerea informațiilor despre oraș

```java
            // Get city information (forecast API structure)
            if (!rootObject.has("city")) {
                throw new Exception("Invalid API response: missing 'city' field");
            }
            
            JsonObject cityObject = rootObject.getAsJsonObject("city");
            String cityName = cityObject.has("name") ? cityObject.get("name").getAsString() : "Unknown";
            String country = cityObject.has("country") ? cityObject.get("country").getAsString() : "";
```

**Linie cu linie:**
- **Structura JSON**: Forecast API returnează un obiect `city` cu info despre oraș
- Verificăm prezența câmpului (defensive programming)
- `getAsJsonObject("city")` - Extrage obiectul imbricat
- Operatori ternari pentru valori default ("Unknown", "")
- **Null safety**: Verificăm `has()` înainte de `get()`

### Extragerea listei de previziuni

```java
            // Get forecast list
            if (!rootObject.has("list")) {
                throw new Exception("Invalid API response: missing 'list' field");
            }
            
            JsonArray forecastList = rootObject.getAsJsonArray("list");
            
            if (forecastList == null || forecastList.size() == 0) {
                throw new Exception("No forecast data available");
            }
```

**Linie cu linie:**
- `"list"` - Câmpul care conține array-ul cu previziuni (40 elemente = 5 zile × 8 ore)
- `getAsJsonArray()` - Extrage array-ul JSON
- Verificăm că array-ul nu e null și nu e gol
- Dacă nu avem date, aruncăm excepție (nu putem continua)

### Inițializarea Map-ului pentru grupare

```java
            // Map to group data by date (yyyy-MM-dd)
            Map<String, DailyWeatherItem> dailyMap = new LinkedHashMap<>();
```

**Explicație:**
- **Pattern**: Grupare cu Map
- **Cheia**: Data în format "yyyy-MM-dd" (ex: "2026-01-08")
- **Valoarea**: Obiectul `DailyWeatherItem` pentru acea zi
- **LinkedHashMap**: Păstrează ordinea cronologică (important pentru afișare)
- **Exemplu**: 40 de elemente → 5 chei (5 zile) cu câte 8 elemente fiecare

## 8. Loop-ul principal de parsare

### Iterarea prin previziuni

```java
            for (int i = 0; i < forecastList.size(); i++) {
                JsonObject forecastItem = forecastList.get(i).getAsJsonObject();
                WeatherItem item = new WeatherItem();
```

**Linie cu linie:**
- Parcurgem toate elementele din array (de obicei 40)
- `get(i)` - Obține elementul de la index i
- `getAsJsonObject()` - Convertește la JsonObject
- Creăm un obiect nou `WeatherItem` pentru fiecare previziune

### Setarea informațiilor despre oraș

```java
                // Set city and country
                item.setCityName(cityName);
                item.setCountry(country);
```

**Explicație:**
- Setăm orașul și țara pe fiecare item (redundant dar simplifică codul)
- Aceste valori sunt identice pentru toate elementele

### Parsarea descrierii vremii și iconiței

```java
                // Parse weather description and icon
                if (forecastItem.has("weather") && forecastItem.get("weather").isJsonArray()) {
                    JsonArray weatherArray = forecastItem.getAsJsonArray("weather");
                    if (weatherArray.size() > 0) {
                        JsonObject weather = weatherArray.get(0).getAsJsonObject();
```

**Linie cu linie:**
- `"weather"` - Array JSON cu descrieri (de obicei un singur element)
- Verificăm că există și că e array
- `isJsonArray()` - Type checking pentru siguranță
- Luăm primul element din array (index 0)
- **Structură API**: `weather` e array pentru a suporta multiple condiții (rar)

### Extragerea descrierii

```java
                        if (weather.has("description") && !weather.get("description").isJsonNull()) {
                            item.setDescription(weather.get("description").getAsString());
                        } else {
                            item.setDescription("No description");
                        }
```

**Linie cu linie:**
- Verificăm prezența câmpului "description"
- `isJsonNull()` - Verificăm că nu e null explicit în JSON
- `getAsString()` - Extragem ca String
- Valoare default: "No description"
- **Exemple**: "clear sky", "few clouds", "light rain"

### Construirea URL-ului iconiței

```java
                        if (weather.has("icon") && !weather.get("icon").isJsonNull()) {
                            String iconCode = weather.get("icon").getAsString();
                            item.setIconUrl("https://openweathermap.org/img/wn/" + iconCode + "@2x.png");
                        } else {
                            item.setIconUrl("");
                        }
                    }
                }
```

**Linie cu linie:**
- `"icon"` - Cod pentru iconița meteo (ex: "01d", "10n")
- Construim URL-ul complet către imagine
- Format: `https://openweathermap.org/img/wn/{icon}@2x.png`
- `@2x` - Versiune high resolution (2x mai mare)
- **Exemple**: "01d" → soare (day), "01n" → lună (night)

### Parsarea datelor principale (temperatură, umiditate, etc.)

```java
                // Parse main weather data
                if (forecastItem.has("main") && !forecastItem.get("main").isJsonNull()) {
                    JsonObject main = forecastItem.getAsJsonObject("main");
```

**Explicație:**
- `"main"` - Obiect JSON cu datele meteo principale
- Verificări standard: prezență și non-null

### Parsarea temperaturii

```java
                    if (main.has("temp") && !main.get("temp").isJsonNull()) {
                        item.setTemperature(main.get("temp").getAsDouble());
                    }
```

**Linie cu linie:**
- `"temp"` - Temperatura în grade Celsius (am cerut `units=metric`)
- `getAsDouble()` - Extrage ca număr double
- **Exemplu**: 23.5

### Parsarea temperaturii resimțite

```java
                    if (main.has("feels_like") && !main.get("feels_like").isJsonNull()) {
                        item.setFeelsLike(main.get("feels_like").getAsDouble());
                    }
```

**Explicație:**
- `"feels_like"` - Temperatura resimțită (consideră vânt, umiditate)
- **Heat index / Wind chill**: Calculat de API

### Parsarea umidității

```java
                    if (main.has("humidity") && !main.get("humidity").isJsonNull()) {
                        item.setHumidity(main.get("humidity").getAsDouble());
                    }
```

**Explicație:**
- `"humidity"` - Umiditatea relativă în procente (0-100)
- **Exemplu**: 65.0

### Parsarea presiunii

```java
                    if (main.has("pressure") && !main.get("pressure").isJsonNull()) {
                        item.setPressure(main.get("pressure").getAsDouble());
                    }
                }
```

**Explicație:**
- `"pressure"` - Presiunea atmosferică în hPa (hectoPascali)
- **Exemplu**: 1013.0 (presiune normală la nivelul mării)

### Parsarea datelor de vânt

```java
                // Parse wind data
                if (forecastItem.has("wind") && !forecastItem.get("wind").isJsonNull()) {
                    JsonObject wind = forecastItem.getAsJsonObject("wind");
                    if (wind.has("speed") && !wind.get("speed").isJsonNull()) {
                        item.setWindSpeed(wind.get("speed").getAsDouble());
                    }
                }
```

**Linie cu linie:**
- `"wind"` - Obiect JSON cu date despre vânt
- `"speed"` - Viteza vântului în m/s (metri pe secundă)
- **Exemplu**: 3.5 m/s
- **Note**: API oferă și `"deg"` (direcție) dar nu îl folosim

### Parsarea și conversia vizibilității

```java
                // Parse visibility (in meters, convert to km)
                if (forecastItem.has("visibility") && !forecastItem.get("visibility").isJsonNull()) {
                    item.setVisibility(forecastItem.get("visibility").getAsDouble() / 1000.0); // Convert to km
                }
```

**Linie cu linie:**
- `"visibility"` - Vizibilitatea în metri
- Împărțim la 1000 pentru conversie în kilometri
- **Exemplu**: 10000 metri → 10.0 km
- **Note**: Valoare maximă de obicei 10 km

## 9. Parsarea și formatarea datei/timpului

### Extragerea string-ului de dată

```java
                // Parse date/time from dt_txt field (forecast API)
                String dateKey = "";
                if (forecastItem.has("dt_txt") && !forecastItem.get("dt_txt").isJsonNull()) {
                    try {
                        String dateTimeStr = forecastItem.get("dt_txt").getAsString();
```

**Linie cu linie:**
- `dateKey` - Va conține data în format "yyyy-MM-dd" pentru grupare
- `"dt_txt"` - Câmp cu data și ora în format text
- **Format API**: "2024-01-15 12:00:00"

### Parsarea cu DateTimeFormatter

```java
                        // Parse the date string (format: "2024-01-15 12:00:00")
                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, 
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
```

**Linie cu linie:**
- `LocalDateTime.parse()` - Parsează string în obiect LocalDateTime
- `DateTimeFormatter.ofPattern()` - Definește formatul de parsare
- **Pattern**: "yyyy-MM-dd HH:mm:ss" - an, lună, zi, oră, minute, secunde
- **Exemplu**: "2026-01-08 15:00:00" → obiect LocalDateTime

### Reformatarea pentru afișare

```java
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm yyyy-MM-dd");
                        item.setDateTime(dateTime.format(formatter));
```

**Linie cu linie:**
- Creăm un formatter nou pentru output
- **Pattern**: "HH:mm yyyy-MM-dd" - ora:minute an-lună-zi
- `format()` - Convertește LocalDateTime înapoi în String
- **Transformare**: "2026-01-08 15:00:00" → "15:00 2026-01-08"

### Extragerea datei pentru grupare

```java
                        // Extract date part (yyyy-MM-dd) for grouping
                        dateKey = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
```

**Linie cu linie:**
- Formatăm doar partea de dată (fără oră)
- **Scop**: Cheia pentru Map (toate elementele din aceeași zi au aceeași cheie)
- **Exemplu**: "2026-01-08"

### Gestionarea erorilor de parsare

```java
                    } catch (Exception e) {
                        item.setDateTime("Invalid date");
                    }
                } else {
                    item.setDateTime("No date");
                }
```

**Explicație:**
- Dacă parsarea eșuează (format neașteptat), setăm "Invalid date"
- Dacă câmpul lipsește complet, setăm "No date"
- Aplicația nu crashuiește pentru erori de dată

## 10. Gruparea pe zile și adăugarea în Map

```java
                // Group by date
                if (!dateKey.isEmpty()) {
                    DailyWeatherItem dailyItem = dailyMap.get(dateKey);
                    if (dailyItem == null) {
                        dailyItem = new DailyWeatherItem();
                        dailyItem.setCityName(cityName);
                        dailyItem.setCountry(country);
                        dailyItem.setDate(dateKey);
                        dailyMap.put(dateKey, dailyItem);
                    }
                    dailyItem.addHourlyData(item);
                }
            }
```

**Linie cu linie:**
- Verificăm că am reușit să extragem o dată validă
- `get(dateKey)` - Căutăm în Map dacă există deja un item pentru această zi
- Dacă nu există (`null`), creăm unul nou:
  - Instanțiem `DailyWeatherItem`
  - Setăm oraș, țară, dată
  - Adăugăm în Map cu cheia = data
- `addHourlyData(item)` - Adăugăm WeatherItem-ul la lista oră cu oră a zilei
- **Pattern**: Lazy initialization - creăm DailyWeatherItem doar la prima întâlnire a datei

### Convertirea Map în List

```java
            // Convert map to list
            dailyWeatherItems.addAll(dailyMap.values());
```

**Linie cu linie:**
- `values()` - Obține toate valorile din Map (fără chei)
- `addAll()` - Adaugă toate elementele în lista de rezultate
- LinkedHashMap păstrează ordinea → lista va fi cronologică
- **Transformare**: Map<String, DailyWeatherItem> → List<DailyWeatherItem>

### Gestionarea excepțiilor

```java
        } catch (Exception e) {
            android.util.Log.e("WeatherParser", "Error parsing weather data: " + e.getMessage(), e);
            throw new RuntimeException("Failed to parse weather data: " + e.getMessage(), e);
        }

        return dailyWeatherItems;
    }
```

**Linie cu linie:**
- Prinde orice excepție din bloc try
- `Log.e()` - Scrie eroare în logcat pentru debugging
- `throw new RuntimeException()` - Re-aruncă ca RuntimeException
- Wrapping: Excepția originală e păstrată ca `cause`
- **Important**: Nu returnăm listă goală - preferăm să crashuim cu informație clară

## 11. Metoda parseWeather() - Versiune pentru backward compatibility

Metodă similară dar returnează listă plată (fără grupare pe zile).

### Semnătura și structura

```java
    // Keep the old method for backward compatibility if needed
    public static List<WeatherItem> parseWeather(String jsonResponse) {
        List<WeatherItem> weatherItems = new ArrayList<>();
        Gson gson = new Gson();
```

**Explicație:**
- **Scop**: Compatibilitate cu cod vechi care așteaptă `List<WeatherItem>`
- Returnează toate elementele într-o listă plată (40 elemente, nu 5)
- Logica de parsare e identică cu `parseWeatherByDay()`

### Diferențe față de parseWeatherByDay()

```java
                // [... identical parsing logic ...]
                
                weatherItems.add(item);
            }
```

**Explicație:**
- **Diferența principală**: În loc să grupăm în Map, adăugăm direct în listă
- Nu creăm DailyWeatherItem
- Nu formatăm data pentru grupare
- Rezultat: Listă simplă cu toate previziunile orare

### Finalizare

```java
        } catch (Exception e) {
            android.util.Log.e("WeatherParser", "Error parsing weather data: " + e.getMessage(), e);
            throw new RuntimeException("Failed to parse weather data: " + e.getMessage(), e);
        }

        return weatherItems;
    }
}
```

**Explicație:**
- Gestionarea erorilor identică
- Returnează lista plată
- Închide clasa

---

## Rezumat

Această clasă este motorul de parsare JSON pentru aplicația de vreme:

### **Scop principal**
- Transformă răspunsul JSON raw de la OpenWeatherMap în obiecte Java utilizabile
- Face legătura dintre stratul de rețea și stratul de prezentare

### **Funcționalități cheie:**
1. **Parsare JSON robuastă** - Gestionează toate câmpurile API-ului
2. **Validare date** - Verifică structura răspunsului și detectează erori
3. **Null safety** - Verificări defensive pentru toate câmpurile
4. **Grupare inteligentă** - Organizează 40 previziuni în 5 zile
5. **Conversii unități** - Metri → kilometri pentru vizibilitate
6. **Formatare date** - Parsează și reformatează timestamp-uri
7. **Backward compatibility** - Două metode pentru flexibilitate

### **Structura JSON procesată:**

```json
{
  "cod": "200",
  "city": {
    "name": "Bucharest",
    "country": "RO"
  },
  "list": [
    {
      "dt_txt": "2026-01-08 12:00:00",
      "main": {
        "temp": 5.2,
        "feels_like": 3.1,
        "humidity": 65,
        "pressure": 1013
      },
      "weather": [{
        "description": "clear sky",
        "icon": "01d"
      }],
      "wind": {
        "speed": 3.5
      },
      "visibility": 10000
    }
    // ... 39 more items
  ]
}
```

### **Flow-ul de parsare:**

1. **Validare**: Verifică cod răspuns ("200" = OK)
2. **Extragere oraș**: Citește name și country din "city"
3. **Iterare**: Parcurge array-ul "list" (40 elemente)
4. **Pentru fiecare element**:
   - Creează WeatherItem
   - Parsează toate câmpurile cu null checks
   - Convertește vizibilitatea (m → km)
   - Formatează data/ora
   - Extrage data pentru grupare
5. **Grupare**: Adaugă în Map bazat pe dată
6. **Conversie**: Map → List
7. **Return**: Lista de DailyWeatherItem

### **Pattern-uri și tehnici:**

- **Null-safe parsing** - Verificări has() și isJsonNull() peste tot
- **Default values** - Valori implicite pentru câmpuri lipsă
- **Try-catch granular** - Pentru parsarea datelor (nu oprește parsarea celorlalte câmpuri)
- **LinkedHashMap** - Păstrează ordinea cronologică
- **Static utility methods** - Nu necesită instanțiere
- **Defensive programming** - Validări multiple la fiecare pas

### **Transformări de date:**

| Input (JSON) | Output (Java) | Conversie |
|--------------|---------------|-----------|
| "temp": 5.2 | 5.2 | Direct (double) |
| "visibility": 10000 | 10.0 | ÷ 1000 (m → km) |
| "dt_txt": "2026-01-08 12:00:00" | "12:00 2026-01-08" | Parse + reformat |
| "icon": "01d" | "https://...01d@2x.png" | Construire URL |
| 40 items | 5 DailyWeatherItem | Grupare pe zi |

### **Gestionarea erorilor:**

- **Cod API != 200**: Excepție cu mesaj de la server
- **Câmpuri lipsă**: Excepție "missing field"
- **Lista goală**: Excepție "No forecast data"
- **JSON malformat**: Gson aruncă JsonSyntaxException
- **Parsare dată eșuată**: "Invalid date" (nu oprește parsarea)
- **Orice altă eroare**: RuntimeException cu mesaj descriptiv

### **Optimizări și considerații:**

- **LinkedHashMap**: O(1) pentru lookup și inserție, păstrează ordinea
- **Single pass**: Parcurge lista o singură dată
- **Lazy initialization**: DailyWeatherItem creat doar când e necesar
- **Memory efficient**: Nu duplică date, doar referințe
- **Fail-fast**: Excepții imediate pentru erori critice

Această clasă este un exemplu excelent de parsing robust, defensive programming și gestionare complexă a datelor JSON!
