# WeatherAPI.java — Documentație detaliată a codului sursă

Acest document prezintă codul complet al clasei `WeatherAPI.java` împărțit în fragmente logice, fiecare precedat de explicații detaliate în limba română. Documentația este destinată programatorilor cu cunoștințe de Java dar cu experiență limitată în programarea Android.

## Context general

`WeatherAPI` este o clasă utilitară (helper class) care gestionează comunicarea cu API-ul OpenWeatherMap. Această clasă are responsabilitatea de a:
1. Citi cheia API din fișierul de configurare
2. Construi URL-ul corect pentru request-uri
3. Face request-uri HTTP către serverul meteo
4. Gestiona erorile de rețea și de API
5. Returna răspunsul JSON cu datele meteo

Este o clasă cu metode statice, ceea ce înseamnă că nu trebuie instanțiată - metodele se apelează direct pe clasă (de ex. `WeatherAPI.fetchWeather(...)`).

## 1. Declararea pachetului

Clasa aparține sub-pachetului `api` din aplicație.

```java
package ro.makore.akrilki_08.api;
```

**Explicație:**
- Sub-pachetul `api` grupează clasele responsabile cu comunicarea rețea
- Organizare logică: toate clasele legate de API-uri sunt împreună
- Pattern comun în arhitectura Android: separarea pe layere (UI, API, Model, etc.)

## 2. Import-uri Android

Import pentru acces la resurse:

```java
import android.content.Context;
```

**Explicație:**
- **`Context`** - Necesar pentru a accesa fișierele din directorul `assets`
- Context oferă acces la resurse, preferințe, servicii sistem
- Este transmis ca parametru din activitate către această clasă

## 3. Import-uri pentru OkHttp

Biblioteca pentru comunicare HTTP:

```java
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
```

**Linie cu linie:**
- **`OkHttpClient`** - Client HTTP care face request-uri și primește răspunsuri
- **`Request`** - Reprezintă un request HTTP (URL, method, headers)
- **`Response`** - Reprezintă răspunsul primit de la server (status code, body, headers)

## 4. Import pentru logging

```java
import android.util.Log;
```

**Explicație:**
- **`Log`** - Sistem de logging Android pentru debug
- Folosit pentru a scrie mesaje în logcat (vizibile în Android Studio)
- Ajută la debuggarea problemelor de rețea și API

## 5. Import-uri Java standard

Import-uri pentru I/O, encoding și JSON:

```java
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
```

**Linie cu linie:**
- **`IOException`** - Excepție pentru erori de input/output (rețea, fișiere)
- **`InputStream`** - Pentru citirea fișierelor ca streamuri de bytes
- **`URLEncoder`** - Encodează string-uri pentru a fi safe în URL-uri (spații → %20)
- **`StandardCharsets`** - Constante pentru encodings standard (UTF-8)
- **`JSONObject`** - Clasă pentru parsarea și manipularea JSON

## 6. Declararea clasei și constantelor

### Declararea clasei

```java
public class WeatherAPI {
```

**Explicație:**
- Clasă publică, accesibilă din orice pachet
- Nu extinde nicio altă clasă - este o clasă utilitară simplă
- Conține doar metode statice

### Constante pentru API

```java
    private static final String API_BASE_URL = "https://api.openweathermap.org/data/2.5/forecast";
    private static final String TAG = "WeatherAPI";
```

**Linie cu linie:**
- **`API_BASE_URL`** - URL-ul de bază al API-ului OpenWeatherMap pentru forecast
- **`private static final`** - Constantă privată, shared între toate instanțele, nu poate fi modificată
- **`TAG`** - String folosit pentru identificarea mesajelor în logcat
- Constantele sunt în SCREAMING_SNAKE_CASE (convenție Java)

## 7. Metoda readJsonFromAssets() - Citirea fișierului de configurare

Această metodă privată citește fișierul JSON cu cheia API din directorul `assets`.

### Semnătura metodei

```java
    private static String readJsonFromAssets(Context context, String fileName) throws IOException {
```

**Linie cu linie:**
- **`private`** - Metodă accesibilă doar în interiorul clasei
- **`static`** - Poate fi apelată fără a crea o instanță a clasei
- **`String`** - Returnează conținutul fișierului ca String
- **`Context context`** - Parametru necesar pentru acces la assets
- **`String fileName`** - Numele fișierului de citit (ex: "api_key.json")
- **`throws IOException`** - Poate arunca excepție dacă fișierul nu există sau citirea eșuează

### Deschiderea fișierului din assets

```java
        InputStream is = context.getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
```

**Linie cu linie:**
- `context.getAssets()` - Obține AssetManager-ul aplicației
- `.open(fileName)` - Deschide fișierul și returnează un InputStream
- `available()` - Returnează numărul de bytes disponibili pentru citire (dimensiunea fișierului)
- `new byte[size]` - Creează un buffer (array de bytes) cu dimensiunea exactă a fișierului

### Citirea conținutului fișierului

```java
        int bytesRead = is.read(buffer);
        is.close();
```

**Linie cu linie:**
- `read(buffer)` - Citește bytes din stream și îi pune în buffer
- Returnează numărul de bytes efectiv citiți
- `close()` - Închide stream-ul pentru a elibera resurse
- Important: întotdeauna închideți stream-urile după utilizare

### Verificarea citirii complete

```java
        if (bytesRead != size) {
            Log.w(TAG, "Warning: Expected to read " + size + " bytes but read " + bytesRead);
        }
```

**Explicație:**
- Verificăm că am citit exact câți bytes ne așteptam
- Dacă nu, logăm un warning (posibilă problemă cu fișierul)
- `Log.w()` - Warning level (mai puțin sever decât error)

### Convertirea buffer-ului în String

```java
        String content = new String(buffer, 0, bytesRead, "UTF-8");
```

**Linie cu linie:**
- Constructor `new String()` creează un String din bytes
- **Parametri:**
  - `buffer` - Array-ul de bytes
  - `0` - Poziția de start în buffer
  - `bytesRead` - Numărul de bytes de convertit
  - `"UTF-8"` - Encoding-ul folosit (standard pentru JSON)

### Eliminarea BOM (Byte Order Mark)

```java
        // Remove any BOM if present
        if (content.length() > 0 && content.charAt(0) == '\uFEFF') {
            content = content.substring(1);
        }
        return content;
    }
```

**Linie cu linie:**
- BOM este un caracter special invizibil la începutul unor fișiere text
- `\uFEFF` - Codul Unicode pentru BOM
- `charAt(0)` - Obține primul caracter
- `substring(1)` - Ia tot string-ul începând de la index 1 (omite primul caracter)
- Returnează conținutul fișierului ca String curat, fără BOM

## 8. Metoda fetchWeather() - Descărcarea datelor meteo

Metoda principală publică care face request-ul HTTP către API și returnează răspunsul JSON.

### Semnătura metodei

```java
    public static String fetchWeather(Context context, String cityName) throws Exception {
```

**Linie cu linie:**
- **`public static`** - Metodă publică, statică, apelabilă direct pe clasă
- **`String`** - Returnează răspunsul JSON ca String
- **`Context context`** - Pentru citirea fișierului de configurare
- **`String cityName`** - Orașul pentru care vrem vremea
- **`throws Exception`** - Poate arunca excepții (erori de rețea, API, configurare)

### Crearea clientului HTTP

```java
        OkHttpClient client = new OkHttpClient();
```

**Explicație:**
- Creează o instanță nouă de OkHttpClient
- Client-ul gestionează conexiunile HTTP, timeout-uri, retry-uri
- Configurație implicită (ar putea fi customizată cu timeouts, interceptors)

### Citirea și parsarea cheii API

```java
        // Read the API key from assets
        String apiKeyJson = readJsonFromAssets(context, "api_key.json");
        Log.d(TAG, "Raw API key JSON: " + apiKeyJson);
        
        JSONObject apiKeyObject = new JSONObject(apiKeyJson);
        String apiKey = apiKeyObject.getString("apiKey");
```

**Linie cu linie:**
- Apelează metoda `readJsonFromAssets()` pentru a citi fișierul "api_key.json"
- `Log.d()` - Debug log pentru a vedea JSON-ul raw
- `new JSONObject(apiKeyJson)` - Parsează string-ul JSON într-un obiect JSONObject
- `getString("apiKey")` - Extrage valoarea câmpului "apiKey" din JSON
- Structura JSON așteptată: `{"apiKey": "cheie_aici"}`

### Curățarea cheii API

```java
        // Trim whitespace from API key
        if (apiKey != null) {
            apiKey = apiKey.trim();
        }
```

**Explicație:**
- `trim()` - Elimină spațiile de la început și sfârșit
- Previne erori cauzate de spații accidentale în fișierul de configurare
- Verificăm null-safety înainte de a apela `trim()`

### Validarea cheii API

```java
        // Check if API key is set
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your_openweathermap_api_key_here")) {
            throw new Exception("API key not configured. Please set your OpenWeatherMap API key in assets/api_key.json");
        }
```

**Linie cu linie:**
- Verificăm trei condiții de invaliditate:
  1. `apiKey == null` - Câmpul lipsește din JSON
  2. `isEmpty()` - String gol
  3. `equals("your_openweathermap_api_key_here")` - Placeholder-ul default nu a fost înlocuit
- Dacă oricare este adevărată, aruncăm excepție cu mesaj descriptiv
- Mesajul ghidează dezvoltatorul să configureze cheia corect

### Logging pentru debug

```java
        Log.d(TAG, "API Key length: " + apiKey.length());
        Log.d(TAG, "API Key (first 4 chars): " + (apiKey.length() > 4 ? apiKey.substring(0, 4) + "..." : apiKey));
```

**Linie cu linie:**
- Logăm lungimea cheii (pentru a verifica că e rezonabilă, ~32 caractere)
- Logăm primele 4 caractere urmate de "..." pentru debug
- Operator ternar: dacă lungimea > 4, afișează primele 4 + "...", altfel afișează toată cheia
- **Securitate**: Nu logăm toată cheia API în producție!

### Encodarea numelui orașului pentru URL

```java
        // URL encode the city name to handle spaces and special characters
        String encodedCityName = URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString());
```

**Linie cu linie:**
- `URLEncoder.encode()` - Transformă string-ul într-un format safe pentru URL
- Transformări comune:
  - Spații → `%20` sau `+`
  - "București" → "Bucure%C8%99ti"
  - "New York" → "New+York"
- `StandardCharsets.UTF_8.toString()` - Encoding-ul folosit
- **Necesar** pentru orașe cu spații sau caractere speciale

### Construirea URL-ului complet

```java
        // Build URL with query parameters
        String url = API_BASE_URL + "?q=" + encodedCityName + "&appid=" + apiKey + "&units=metric";
        
        Log.d(TAG, "Fetching weather for: " + cityName);
        Log.d(TAG, "URL: " + url.replace(apiKey, "***"));
```

**Linie cu linie:**
- Concatenăm URL-ul de bază cu parametrii query:
  - `?q=` - Parametru pentru oraș
  - `&appid=` - Cheia API
  - `&units=metric` - Unități metrice (Celsius, m/s)
- Rezultat final: `https://api.openweathermap.org/data/2.5/forecast?q=Bucharest&appid=ABC123&units=metric`
- Logăm orașul original (neencodat) pentru claritate
- Logăm URL-ul cu cheia ascunsă (`***`) pentru securitate

### Construirea request-ului HTTP

```java
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
```

**Linie cu linie:**
- `new Request.Builder()` - Pattern Builder pentru construirea request-ului
- `.url(url)` - Setează URL-ul destinație
- `.get()` - Specifică metoda HTTP GET (default, dar explicit e mai clar)
- `.build()` - Construiește obiectul Request final
- Alte metode posibile: `.post()`, `.header()`, etc.

### Executarea request-ului și procesarea răspunsului

```java
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
```

**Linie cu linie:**
- `try (...)` - Try-with-resources: închide automat resursa la final
- `client.newCall(request)` - Creează un Call (apel) pregătit
- `.execute()` - Execută request-ul sincron (BLOCKING - așteaptă răspunsul)
  - **Important**: Aceasta rulează într-un thread de fundal în MainActivity
- `response.body().string()` - Extrage body-ul răspunsului ca String
- Body-ul conține JSON-ul cu datele meteo

### Verificarea succesului request-ului

```java
            if (!response.isSuccessful()) {
                Log.e(TAG, "API Error - Code: " + response.code());
                Log.e(TAG, "Response: " + responseBody);
```

**Linie cu linie:**
- `isSuccessful()` - Returnează `true` pentru coduri 200-299, `false` altfel
- `!isSuccessful()` - Verificăm dacă a eșuat (cod 4xx sau 5xx)
- `response.code()` - Codul HTTP (200, 404, 500, etc.)
- Logăm codul de eroare și răspunsul complet pentru debugging
- `Log.e()` - Error level logging

### Parsarea și aruncarea erorii API

```java
                // Try to parse error message from response
                try {
                    JSONObject errorJson = new JSONObject(responseBody);
                    String errorMessage = errorJson.optString("message", "Unknown error");
                    throw new IOException("API Error: " + errorMessage + " (Code: " + response.code() + ")");
                } catch (Exception e) {
                    throw new IOException("API Error - Code: " + response.code() + ", Response: " + responseBody);
                }
            }
```

**Linie cu linie:**
- Încercăm să parsăm răspunsul de eroare ca JSON
- `optString("message", "Unknown error")` - Încearcă să obțină câmpul "message", dacă lipsește returnează "Unknown error"
- `throw new IOException(...)` - Aruncăm excepție cu mesajul de eroare din API
- Exemplu: "API Error: city not found (Code: 404)"
- Blocul `catch` interior gestionează cazul când răspunsul nu e JSON valid
- În acest caz, aruncăm excepție cu tot răspunsul raw

### Returnarea răspunsului de succes

```java
            Log.d(TAG, "API Response received successfully");
            return responseBody;
        }
    }
}
```

**Linie cu linie:**
- Dacă request-ul a fost cu succes, logăm confirmarea
- Returnăm body-ul răspunsului (JSON cu datele meteo)
- Acolada închide blocul try-with-resources (Response se închide automat)
- Acolada finală închide metoda și clasa

---

## Rezumat

Această clasă este responsabilă cu comunicarea HTTP către API-ul meteo:

### **Scop principal**
- Încapsulează toată logica de comunicare cu OpenWeatherMap API
- Abstractizează detaliile HTTP de restul aplicației

### **Funcționalități cheie:**
1. **Citire configurare** - Extrage cheia API din fișierul `assets/api_key.json`
2. **Validare** - Verifică că cheia API este configurată corect
3. **Encoding URL** - Gestionează orașe cu spații și caractere speciale
4. **Request HTTP** - Folosește OkHttp pentru comunicare robustă
5. **Error handling** - Detectează și raportează erori de API și rețea
6. **Logging** - Mesaje detaliate pentru debugging

### **Pattern-uri și best practices:**
- **Metode statice** - Nu necesită instanțiere
- **Try-with-resources** - Gestionare automată a resurselor
- **URL encoding** - Previne erori cu caractere speciale
- **Null safety** - Verificări defensive
- **Logging selectiv** - Nu expune cheia API completă
- **Excepții descriptive** - Mesaje clare pentru debugging

### **Flow-ul execuției:**
1. MainActivity apelează `WeatherAPI.fetchWeather(context, "București")`
2. Se citește cheia API din assets
3. Se validează cheia
4. Se encodează numele orașului
5. Se construiește URL-ul complet
6. Se face request-ul HTTP (blocking)
7. Se verifică răspunsul
8. Se returnează JSON-ul sau se aruncă excepție

### **Erori posibile:**
- **IOException** - Probleme de rețea, timeout
- **Exception** - Cheie API lipsă sau invalidă
- **JSONException** - Fișier api_key.json malformat
- **API Errors** - Oraș inexistent (404), cheie invalidă (401), etc.

### **Îmbunătățiri posibile:**
- Cache pentru răspunsuri (evită request-uri duplicate)
- Retry logic pentru erori temporare de rețea
- Timeout-uri configurabile
- Suport pentru mai multe API keys (fallback)
- Singleton OkHttpClient (mai eficient decât a crea instanțe noi)
