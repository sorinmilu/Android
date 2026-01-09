# MainActivity.java - Documentație Linie cu Linie

## Prezentare

Această aplicație Android demonstrează:
- **Comunicare HTTP asincronă** cu biblioteca OkHttp
- **Parsare JSON** pentru a extrage date dintr-un răspuns de la server
- **Actualizare UI dintr-un thread secundar** folosind `runOnUiThread()`
- **Gestionarea erorilor** pentru operații de rețea
- **Interacțiune utilizator** prin două butoane (Refresh și Quit)

Aplicația descarcă glume aleatorii de la API-ul `https://official-joke-api.appspot.com/random_joke` și le afișează pe ecran. Utilizatorul poate reîmprospăta gluma sau ieși din aplicație.


## Analiza Linie cu Linie

### Declararea Pachetului

```java
package ro.makore.akrilki_02;
```

Declară pachetul în care se află clasa. Pachetul `ro.makore.akrilki_02` organizează codul aplicației și oferă un identificator unic în cadrul sistemului Android.

---

### Import-uri Android Standard

```java
import android.os.Bundle;
```

Importă clasa `Bundle` care este folosită pentru a salva și restaura starea activității între diferite cicluri de viață (ex: când utilizatorul rotește ecranul).

```java
import android.util.Log;
```

Importă utilitarul `Log` pentru a scrie mesaje în consola Logcat. Folosit pentru debugging și raportarea erorilor.

---

### Import-uri AndroidX

```java
import androidx.appcompat.app.AppCompatActivity;
```

Importă clasa de bază `AppCompatActivity` care oferă compatibilitate înapoi pentru funcționalități moderne de UI pe versiuni vechi de Android.


### Import-uri Widget-uri

```java
import android.widget.Button;
```

Importă clasa `Button` care reprezintă un buton pe care utilizatorul poate face click.

```java
import android.widget.TextView;
```

Importă clasa `TextView` care afișează text pe ecran. În această aplicație este folosită pentru a afișa gluma.

---

### Import-uri JSON

```java
import org.json.JSONException;
```

Importă excepția `JSONException` care este aruncată când parsarea JSON eșuează (de exemplu, când formatul JSON este invalid).

```java
import org.json.JSONObject;
```

Importă clasa `JSONObject` care permite parsarea și manipularea datelor în format JSON. JSON (JavaScript Object Notation) este un format text pentru schimb de date.

---

### Import-uri I/O

```java
import java.io.IOException;
```

Importă excepția `IOException` care este aruncată când operațiile de rețea sau I/O eșuează (ex: nu există conexiune la internet).

---

### Import-uri OkHttp

```java
import okhttp3.Call;
```

Importă interfața `Call` care reprezintă un request HTTP pregătit pentru execuție. Poate fi executat sincron sau asincron.

```java
import okhttp3.Callback;
```

Importă interfața `Callback` care trebuie implementată pentru a procesa răspunsurile asincrone de la server (succes sau eșec).

```java
import okhttp3.OkHttpClient;
```

Importă clasa `OkHttpClient` care este clientul HTTP folosit pentru a face request-uri către server. Este o bibliotecă populară pentru comunicare HTTP în Android.

```java
import okhttp3.Request;
```

Importă clasa `Request` care reprezintă un HTTP request (cerere către server) cu URL, metodă (GET/POST), headers, etc.

```java
import okhttp3.Response;
```

Importă clasa `Response` care reprezintă răspunsul primit de la server după executarea unui request HTTP.

---

### Declararea Clasei

```java
public class MainActivity extends AppCompatActivity {
```

Declară clasa `MainActivity` care este publică și moștenește din `AppCompatActivity`. Aceasta este activitatea principală a aplicației care va fi lansată când utilizatorul deschide aplicația.

---

### Variabile de Instanță

```java
    private TextView jokeTextView;
```

Declară o variabilă privată de tip `TextView` care va reține referința către TextView-ul din layout unde se afișează gluma. Este `private` pentru că este folosită doar în interiorul acestei clase.

```java
    private Button refreshButton;
```

Declară o variabilă privată de tip `Button` care va reține referința către butonul "Refresh Joke". Este folosită pentru a atașa un listener de click.

```java
    private OkHttpClient client;
```

Declară o variabilă privată de tip `OkHttpClient` care va fi clientul HTTP folosit pentru toate request-urile către API. Se creează o singură instanță și se reutilizează pentru eficiență.

---

### Metoda onCreate - Semnătura

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
```

Suprascrie metoda `onCreate()` moștenită de la `AppCompatActivity`. Această metodă este apelată când activitatea este creată pentru prima dată. `Bundle savedInstanceState` conține starea salvată anterior (sau `null` la prima lansare).

---

### Apel Constructor Părinte

```java
        super.onCreate(savedInstanceState);
```

Apelează metoda `onCreate()` din clasa părinte (`AppCompatActivity`) pentru a efectua inițializarea standard a activității. Acest apel este **obligatoriu** și trebuie să fie prima linie din `onCreate()`.

---

### Setare Layout

```java
        setContentView(R.layout.activity_main);
```

Încarcă layout-ul XML definit în `res/layout/activity_main.xml` și îl setează ca interfață vizuală a activității. După acest apel, toate View-urile din XML sunt create în memorie și pot fi accesate cu `findViewById()`.

---

### Inițializare Client HTTP

```java
        client = new OkHttpClient();
```

Creează o nouă instanță de `OkHttpClient` care va fi folosită pentru a face request-uri HTTP către API. Clientul gestionează conexiunile, cache-ul, timeout-urile și alte aspecte ale comunicării HTTP.

---

### Găsirea View-urilor (Prima Versiune)

```java
        jokeTextView = findViewById(R.id.jokeTextView);
```

Caută în layout-ul încărcat elementul cu ID-ul `jokeTextView` și returnează o referință către acesta. Referința este salvată în variabila de instanță `jokeTextView` pentru a putea actualiza textul ulterior.

```java
        refreshButton = findViewById(R.id.refreshButton);
```

Caută în layout butonul cu ID-ul `refreshButton` și salvează referința în variabila de instanță `refreshButton`.

---

### Găsirea View-urilor (A Doua Versiune - Duplicat)

```java
        Button quitButton = findViewById(R.id.quitButton);
```

Caută butonul cu ID-ul `quitButton` și creează o variabilă locală de tip `Button`. Această variabilă este folosită doar în metoda `onCreate()`.

```java
        TextView jokeTextView = findViewById(R.id.jokeTextView);
```

**Atenție - Cod Duplicat:** Această linie creează o nouă variabilă **locală** cu același nume ca variabila de instanță `jokeTextView`. Aceasta **ascunde** (shadowing) variabila de instanță în restul metodei `onCreate()`. Totuși, în metodele `fetchJoke()` și callback-uri se va folosi variabila de instanță (care a fost inițializată corect mai sus), deci codul funcționează corect.

**Diagrama Shadowing:**
```
┌─────────────────────────────────────┐
│ Variabila de Instanță               │
│ private TextView jokeTextView;      │ ← Declarată la nivel de clasă
└─────────────────────────────────────┘
           │
           │ Inițializată:
           │ jokeTextView = findViewById(R.id.jokeTextView);
           ▼
┌─────────────────────────────────────┐
│ Variabila Locală (Shadowing)        │
│ TextView jokeTextView = ...;        │ ← Creată local în onCreate()
└─────────────────────────────────────┘
           │
           │ Ascunde variabila de instanță
           │ doar în restul metodei onCreate()
           ▼
┌─────────────────────────────────────┐
│ În fetchJoke() și callbacks         │
│ se folosește variabila de instanță  │ ← Funcționează corect
└─────────────────────────────────────┘
```

---

### Comentariu Preluare Prima Glumă

```java
        // fetch the first joke    
```

Comentariu care indică că următoarea linie va descărca prima glumă imediat după pornirea aplicației.

---

### Apel Descărcare Glumă Inițială

```java
        fetchJoke();
```

Apelează metoda `fetchJoke()` pentru a descărca și afișa prima glumă imediat ce aplicația pornește. Aceasta face un request HTTP asincron către API.

---

### Comentariu Acțiune Buton Quit

```java
        // Add the quit button action
```

Comentariu care indică că următoarea linie va atașa funcționalitatea butonului de quit.

---

### Listener Buton Quit

```java
        quitButton.setOnClickListener(v -> finishAffinity());
```

Atașează un listener la butonul `quitButton` folosind o expresie lambda. Când utilizatorul apasă butonul, se apelează `finishAffinity()` care închide activitatea curentă și toate activitățile părinte din task, terminând complet aplicația.

**Detalii Lambda:**
- `v` = parametrul (View-ul care a fost apăsat, adică `quitButton`)
- `->` = operator lambda (separă parametrii de corp)
- `finishAffinity()` = metoda apelată la click

---

### Listener Buton Refresh

```java
        refreshButton.setOnClickListener(v -> fetchJoke());
```

Atașează un listener la butonul `refreshButton`. Când utilizatorul apasă acest buton, se apelează metoda `fetchJoke()` care descarcă o nouă glumă de la API și actualizează interfața.

---

### Închidere Metodă onCreate

```java
    }
```

Închide metoda `onCreate()`. La acest punct, activitatea este complet inițializată: layout-ul este încărcat, clientul HTTP este creat, toate View-urile sunt găsite, listener-ii sunt atașați, și prima glumă este în curs de descărcare.

---

### Metoda fetchJoke - Semnătura

```java
    private void fetchJoke() {
```

Declară metoda privată `fetchJoke()` care nu returnează nimic (`void`). Această metodă este responsabilă pentru descărcarea unei glume de la API și actualizarea UI-ului. Este `private` pentru că este folosită doar în interiorul acestei clase.

---

### Definire URL API

```java
        String url = "https://official-joke-api.appspot.com/random_joke";
```

Creează o variabilă locală `url` care conține adresa API-ului de unde se descarcă glumele. API-ul returnează o glumă aleatorie în format JSON la fiecare request.

**Format răspuns JSON așteptat:**
```json
{
  "type": "general",
  "setup": "Why did the chicken cross the road?",
  "punchline": "To get to the other side!",
  "id": 123
}
```

---

### Construire Request HTTP

```java
        Request request = new Request.Builder()
                .url(url)
                .build();
```

Creează un obiect `Request` folosind pattern-ul Builder:
1. `new Request.Builder()` - creează un builder pentru request
2. `.url(url)` - setează URL-ul destinației
3. `.build()` - construiește obiectul `Request` final

Acest request va fi un **GET request** implicit (metoda HTTP implicită).

**Diagrama Request Builder:**
```
Request.Builder()
    │
    ├─── .url("https://...")     ← Setare URL
    │
    └─── .build()                ← Construire obiect Request
           │
           ▼
        Request gata de execuție
```

---

### Execuție Asincronă Request

```java
        client.newCall(request).enqueue(new Callback() {
```

Execută request-ul **asincron** (în background, pe un thread separat):
1. `client.newCall(request)` - creează un `Call` (apel) din request
2. `.enqueue(...)` - pune request-ul în coadă pentru execuție asincronă
3. `new Callback() { ... }` - creează un callback anonim pentru a procesa răspunsul

Execuția asincronă înseamnă că UI-ul nu se blochează în timp ce se așteaptă răspunsul de la server.

**Diagrama Execuție Asincronă:**
```
Main Thread (UI)              Background Thread
     │                              │
     │─── client.newCall()          │
     │                              │
     │─── .enqueue() ──────────────>│
     │                              │
     │    (continuă execuție)       │─── Trimitere HTTP Request
     │    (UI rămâne responsive)    │
     │                              │─── Așteptare răspuns server
     │                              │
     │<──── onResponse/onFailure ───│
     │                              │
     │─── runOnUiThread()           │
     │    (actualizare UI)          │
     │                              │
```

---

### Override Metodă onFailure

```java
            @Override
            public void onFailure(Call call, IOException e) {
```

Suprascrie metoda `onFailure()` din interfața `Callback`. Această metodă este apelată **automat** pe un thread în background când request-ul HTTP eșuează (ex: nu există internet, timeout, server down).

Parametri:
- `call` = obiectul Call care a eșuat
- `e` = excepția IOException care descrie eroarea

---

### Logare Eroare

```java
                Log.e("MainActivity", "Failed to fetch joke", e);
```

Scrie un mesaj de eroare în Logcat cu nivelul ERROR:
- `"MainActivity"` = tag-ul (identifică sursa mesajului)
- `"Failed to fetch joke"` = mesajul descriptiv
- `e` = excepția care va fi afișată cu stack trace complet

Acest log ajută la debugging și identificarea problemelor.

---

### Actualizare UI la Eroare

```java
                runOnUiThread(() -> jokeTextView.setText("Failed to load joke."));
```

Execută codul din lambda **pe Main Thread (UI Thread)** pentru că:
- Callback-ul `onFailure()` rulează pe un thread în background
- Android **nu permite** modificarea UI-ului din alte thread-uri decât Main Thread
- `runOnUiThread()` asigură că `setText()` se execută pe thread-ul corect

Lambda-ul actualizează textul din `jokeTextView` cu mesajul de eroare pentru utilizator.

**De ce este necesar runOnUiThread:**
```
Background Thread                Main Thread (UI)
      │                               │
      │─── onFailure() apelat         │
      │                               │
      │─── Log.e()                    │
      │    (OK pe background)         │
      │                               │
      │─── runOnUiThread() ──────────>│
      │                               │─── setText()
      │                               │    (SAFE pe Main Thread)
      │                               │
```

---

### Închidere Metodă onFailure

```java
            }
```

Închide metoda `onFailure()`.

---

### Override Metodă onResponse

```java
            @Override
            public void onResponse(Call call, Response response) throws IOException {
```

Suprascrie metoda `onResponse()` din interfața `Callback`. Această metodă este apelată **automat** pe un thread în background când serverul răspunde (chiar dacă răspunsul este o eroare HTTP, ex: 404, 500).

Parametri:
- `call` = obiectul Call care a primit răspuns
- `response` = răspunsul HTTP de la server (conține status code, headers, body)

**Important:** Această metodă poate arunca `IOException`.

---

### Verificare Succes HTTP

```java
                if (!response.isSuccessful()) {
```

Verifică dacă răspunsul HTTP este **nesucces** (status code în afara intervalului 200-299). `isSuccessful()` returnează `true` doar pentru coduri de succes (200, 201, 204, etc.).

Coduri HTTP comune:
- 200 OK = succes
- 404 Not Found = resursa nu există
- 500 Internal Server Error = eroare pe server

---

### UI Update la Răspuns Nesucces

```java
                    runOnUiThread(() -> jokeTextView.setText("Failed to load joke."));
```

Execută pe Main Thread actualizarea UI-ului cu mesaj de eroare. Aceasta se întâmplă când serverul răspunde dar cu un cod de eroare HTTP (ex: 404, 500).

---

### Return Timpuriu

```java
                    return;
```

Iese din metoda `onResponse()` fără a continua procesarea. Aceasta previne executarea codului de parsare JSON pentru că nu există date valide.

---

### Închidere Bloc if Nesucces

```java
                }
```

Închide blocul `if` pentru răspunsuri HTTP nesuccessful. Dacă execuția ajunge după această acoladă, înseamnă că răspunsul este **succes** (status code 200-299).

---

### Bloc try - Parsare JSON

```java
                try {
```

Deschide un bloc `try` pentru a gestiona excepțiile care pot apărea în timpul parsării JSON. Dacă orice linie din bloc aruncă o excepție, execuția sare la blocul `catch`.

---

### Extragere Text Răspuns

```java
                    String responseData = response.body().string();
```

Extrage conținutul body-ului răspunsului HTTP ca String:
1. `response.body()` - obține obiectul ResponseBody
2. `.string()` - convertește body-ul în String (citește tot conținutul)

**Important:** `.string()` poate fi apelat **o singură dată** pentru că consumă stream-ul.

Exemplu `responseData`:
```
{"type":"general","setup":"Why did...","punchline":"To get...","id":123}
```

---

### Parsare JSON

```java
                    JSONObject json = new JSONObject(responseData);
```

Creează un obiect `JSONObject` din String-ul JSON. Aceasta parsează textul și creează o structură de date pe care o putem interoga.

Dacă JSON-ul este invalid (ex: lipsesc ghilimele, virgule), se va arunca `JSONException`.

**Transformare:**
```
String: {"setup":"Why...","punchline":"To..."}
   │
   │ new JSONObject()
   ▼
JSONObject: {setup → "Why...", punchline → "To..."}
```

---

### Extragere Setup

```java
                    String setup = json.getString("setup");
```

Extrage valoarea câmpului `"setup"` din obiectul JSON ca String. Aceasta este prima parte a glumei (întrebarea sau setup-ul).

Exemplu: `"Why did the chicken cross the road?"`

Dacă câmpul `"setup"` nu există, se va arunca `JSONException`.

---

### Extragere Punchline

```java
                    String punchline = json.getString("punchline");
```

Extrage valoarea câmpului `"punchline"` din obiectul JSON ca String. Aceasta este a doua parte a glumei (răspunsul sau poanta).

Exemplu: `"To get to the other side!"`

---

### Compunere Text Glumă

```java
                    String joke = setup + "\n\n" + punchline;
```

Concatenează cele două părți ale glumei cu două linii noi între ele:
- `setup` = prima parte
- `"\n\n"` = două caractere newline (linie goală între părți)
- `punchline` = a doua parte

Rezultat exemplu:
```
Why did the chicken cross the road?

To get to the other side!
```

---

### Comentariu Actualizare UI

```java
                    // Update the UI
```

Comentariu care indică că următoarea linie va actualiza interfața utilizatorului cu gluma descărcată.

---

### Actualizare UI cu Gluma

```java
                    runOnUiThread(() -> jokeTextView.setText(joke));
```

Execută pe Main Thread actualizarea TextView-ului cu textul glumei:
- Callback-ul `onResponse()` rulează pe background thread
- `runOnUiThread()` transferă execuția pe Main Thread
- `jokeTextView.setText(joke)` afișează gluma pe ecran

**Flux complet de actualizare UI:**
```
Background Thread              Main Thread
      │                             │
      │─── Parse JSON               │
      │─── Creare String joke       │
      │                             │
      │─── runOnUiThread() ────────>│
      │                             │─── setText(joke)
      │                             │    [UI se actualizează]
      │                             │
```

---

### Bloc catch - Prindere Excepție JSON

```java
                } catch (JSONException e) {
```

Prinde excepția `JSONException` care este aruncată când:
- JSON-ul este malformat (sintaxă invalidă)
- Câmpurile `"setup"` sau `"punchline"` lipsesc din răspuns
- Tipul de date nu corespunde (ex: câmpul este număr, nu String)

---

### Logare Eroare Parsare

```java
                    Log.e("MainActivity", "Failed to parse joke JSON", e);
```

Scrie în Logcat un mesaj de eroare la nivel ERROR:
- Tag: `"MainActivity"`
- Mesaj: `"Failed to parse joke JSON"`
- Excepție: `e` (include stack trace pentru debugging)

---

### UI Update la Eroare Parsare

```java
                    runOnUiThread(() -> jokeTextView.setText("Failed to load joke."));
```

Execută pe Main Thread actualizarea UI-ului cu mesaj de eroare. Utilizatorul vede că s-a produs o problemă la încărcarea glumei, chiar dacă serverul a răspuns cu succes dar datele erau invalide.

---

### Închidere Bloc catch

```java
                }
```

Închide blocul `catch` care gestionează excepția `JSONException`.

---

### Închidere Metodă onResponse

```java
            }
```

Închide metoda `onResponse()`.

---

### Închidere Callback Anonim

```java
        });
```

Închide clasa anonimă `Callback` și apelul metodei `.enqueue()`. Acest punct marchează sfârșitul definirii callback-ului care procesează răspunsurile HTTP.

---

### Închidere Metodă fetchJoke

```java
    }
```

Închide metoda `fetchJoke()`. Metoda a lansat request-ul HTTP asincron și a definit callback-urile care vor procesa răspunsul. Metoda se termină imediat, iar request-ul continuă în background.

**Flux temporal:**
```
fetchJoke() apelată
    │
    ├─── Creare Request
    ├─── Lansare .enqueue() (asincron)
    └─── Return imediat
         │
         │ (request-ul continuă în background)
         │
         ▼
    După câteva secunde:
         │
         ├─── onResponse() sau onFailure()
         │
         └─── runOnUiThread() actualizează UI
```

---

### Închidere Clasă MainActivity

```java
}
```

Închide clasa `MainActivity`. Aceasta este sfârșitul definiției clasei care conține toată logica aplicației de glume.

---

## Rezumat Flux de Execuție

### La Pornirea Aplicației

```
1. onCreate() apelat
   │
   ├─── setContentView() → Încarcă layout XML
   ├─── new OkHttpClient() → Creează client HTTP
   ├─── findViewById() × 4 → Găsește toate View-urile
   ├─── fetchJoke() → Descarcă prima glumă
   │    │
   │    └─── Request asincron lansat în background
   │
   ├─── setOnClickListener(quit) → Atașare listener
   └─── setOnClickListener(refresh) → Atașare listener
```

### La Apăsare Buton Refresh

```
1. refreshButton apăsat
   │
   └─── fetchJoke() apelat
        │
        ├─── Creare Request HTTP
        ├─── client.enqueue() → Lansare asincron
        │
        └─── Return imediat (UI rămâne responsive)

2. După primire răspuns (în background):
   │
   ├─── onResponse() / onFailure() apelat
   │
   ├─── Parsare JSON (dacă succes)
   │
   └─── runOnUiThread() → Actualizare TextView
```

### La Apăsare Buton Quit

```
1. quitButton apăsat
   │
   └─── finishAffinity() → Închide aplicația complet
```

---

## Diagrama Arhitecturii Request HTTP

```
MainActivity
    │
    ├─── OkHttpClient client
    │    │
    │    └─── Gestionare conexiuni HTTP
    │         - Connection pooling
    │         - Timeout management
    │         - Cache (dacă configurat)
    │
    ├─── fetchJoke()
    │    │
    │    ├─── Request.Builder
    │    │    │
    │    │    └─── Construire obiect Request
    │    │         - URL
    │    │         - Metodă (GET implicit)
    │    │         - Headers (implicit)
    │    │
    │    └─── client.newCall(request).enqueue()
    │         │
    │         ├─── Background Thread
    │         │    │
    │         │    ├─── Trimitere HTTP Request
    │         │    ├─── Așteptare răspuns
    │         │    └─── onResponse() / onFailure()
    │         │
    │         └─── Main Thread (UI)
    │              │
    │              └─── runOnUiThread()
    │                   │
    │                   └─── jokeTextView.setText()
    │
    └─── Listeners
         │
         ├─── refreshButton → fetchJoke()
         └─── quitButton → finishAffinity()
```

---

## Gestionarea Thread-urilor

### Main Thread (UI Thread)
- Rulează `onCreate()`
- Procesează evenimente de click
- Actualizează UI-ul prin `setText()`
- **Nu face operații blocante** (ex: request-uri HTTP sincrone)

### Background Thread (OkHttp Worker)
- Rulează `onResponse()` și `onFailure()`
- Face operații I/O (network requests)
- Parsează JSON
- **Nu poate modifica UI-ul direct**

### Comunicare între Thread-uri
- `runOnUiThread()` transferă execuția de pe background pe Main Thread
- Permite actualizarea sigură a UI-ului din callback-uri

```
┌─────────────────────────┐         ┌─────────────────────────┐
│   Main Thread (UI)      │         │  Background Thread      │
│                         │         │                         │
│  - onCreate()           │         │  - HTTP Request         │
│  - onClick()            │         │  - onResponse()         │
│  - setText()            │◄────────│  - runOnUiThread()      │
│                         │         │                         │
└─────────────────────────┘         └─────────────────────────┘
         │                                     │
         │                                     │
         └─────── Comunicare sigură ──────────┘
                 prin runOnUiThread()
```

---

## Gestionarea Erorilor

### Nivel 1: Erori de Rețea
- **Captare:** `onFailure(Call, IOException)`
- **Cauze:** Lipsă internet, timeout, DNS failure
- **Acțiune:** Log.e() + afișare mesaj eroare utilizator

### Nivel 2: Erori HTTP
- **Captare:** `!response.isSuccessful()`
- **Cauze:** 404 Not Found, 500 Server Error, etc.
- **Acțiune:** Afișare mesaj eroare utilizator + return

### Nivel 3: Erori de Parsare
- **Captare:** `catch (JSONException e)`
- **Cauze:** JSON invalid, câmpuri lipsă, tipuri greșite
- **Acțiune:** Log.e() + afișare mesaj eroare utilizator

```
Request HTTP
    │
    ├─── Succes Rețea?
    │    │
    │    NO ──> onFailure() ──> Eroare nivel 1
    │    │
    │    YES
    │    │
    ├─── Succes HTTP (200-299)?
    │    │
    │    NO ──> !isSuccessful() ──> Eroare nivel 2
    │    │
    │    YES
    │    │
    ├─── Parsare JSON reușită?
    │    │
    │    NO ──> JSONException ──> Eroare nivel 3
    │    │
    │    YES
    │    │
    └─── Afișare glumă în UI ──> SUCCES COMPLET
```

---

## Concepte Cheie

### 1. Comunicare HTTP Asincronă
- Request-urile HTTP se fac **în background** (pe thread separat)
- UI-ul rămâne **responsive** în timp ce se așteaptă răspunsul
- Folosește pattern-ul **Callback** pentru procesare asincronă
- `enqueue()` = asincron vs `execute()` = sincron (blocant)

### 2. Parsare JSON
- JSON = format text pentru schimb de date structurate
- `JSONObject` = parsează String-ul JSON în obiect Java
- `.getString(key)` = extrage valoare String din JSON
- Poate arunca `JSONException` dacă formatul este invalid

### 3. Actualizare UI din Background Thread
- Android permite modificarea UI **doar pe Main Thread**
- `runOnUiThread(Runnable)` = execută cod pe Main Thread
- Necesară pentru actualizarea View-urilor din callback-uri HTTP

### 4. Expresii Lambda (Java 8+)
- Sintaxă scurtă pentru interfețe funcționale (cu o singură metodă)
- `v -> fetchJoke()` echivalent cu:
  ```java
  new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          fetchJoke();
      }
  }
  ```

### 5. findViewById() și Referințe View
- `findViewById(R.id.viewId)` = găsește View în layout după ID
- Returnează referință către obiectul View din memorie
- Permite modificarea proprietăților View-ului (text, culoare, etc.)

### 6. Lifecycle Android
- `onCreate()` = apelată când activitatea este creată
- `finishAffinity()` = închide activitatea și task-ul complet
- Activitatea poate fi recreată (ex: rotație ecran)

### 7. Pattern Builder (OkHttp)
- Construiește obiecte complexe pas cu pas
- `new Request.Builder().url(...).build()`
- Permite configurare fluentă și citibilă

### 8. Dependency Injection Manual
- `OkHttpClient` creat în `onCreate()` și salvat ca variabilă de instanță
- Reutilizat pentru toate request-urile (eficient)
- Alternativa: crearea unui client nou pentru fiecare request (ineficient)

### 9. Variable Shadowing
- Variabila locală `TextView jokeTextView` ascunde variabila de instanță
- Codul funcționează pentru că metodele folosesc variabila de instanță
- Este o **greșeală de stil**, dar nu o eroare funcțională

### 10. Exception Handling
- `try-catch` pentru operații care pot eșua (parsare JSON)
- `throws IOException` în semnătura metodei `onResponse()`
- Logging pentru debugging (`Log.e()`)
