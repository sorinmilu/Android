# ChuckViewModel.java - Documentație Linie cu Linie

## Prezentare

`ChuckViewModel` este componenta ViewModel asociată cu `ChuckFragment`, responsabilă pentru:
- **Gestionarea datelor** legate de Chuck Norris facts
- **Efectuarea request-urilor HTTP** către API-ul Chuck Norris
- **Expunerea datelor** prin LiveData către fragment
- **Supraviețuirea** rotațiilor ecranului și schimbărilor de configurație

**Rol în arhitectură MVVM:**
- **Model:** Date de la API (Chuck Norris facts în format JSON)
- **View:** ChuckFragment (observă și afișează datele)
- **ViewModel:** ChuckViewModel (gestionează logica de business și comunicarea cu API)

**Caracteristici:**
- Scoped la ChuckFragment (nu shared cu alte fragmente)
- Încarcă automat un fact la inițializare
- Oferă metodă publică pentru refresh on demand
- Folosește OkHttp pentru request-uri asincrone
- Parsează JSON și extrage câmpul "value"

## Analiza Linie cu Linie

### Declararea Pachetului

```java
package ro.makore.akrilki_04.ui.chuck;
```

Declară pachetul în care se află clasa, parte din feature-ul Chuck Norris al aplicației.

---

### Import LiveData

```java
import androidx.lifecycle.LiveData;
```

Importă `LiveData` - clasa read-only pentru expunerea datelor observabile către UI.

**LiveData:** Container pentru date care respectă lifecycle-ul Android, emițând update-uri doar când observatorul este activ.

---

### Import MutableLiveData

```java
import androidx.lifecycle.MutableLiveData;
```

Importă `MutableLiveData` - subclasă de LiveData care permite modificarea valorii.

**MutableLiveData vs LiveData:**
- `MutableLiveData` = poate fi modificat (folosit intern în ViewModel)
- `LiveData` = read-only (expus către fragment pentru observare)

---

### Import ViewModel

```java
import androidx.lifecycle.ViewModel;
```

Importă clasa de bază `ViewModel` care asigură supraviețuirea datelor în timpul schimbărilor de configurație.

---

### Import JSONException

```java
import org.json.JSONException;
```

Importă excepția aruncată când parsarea JSON eșuează.

---

### Import JSONObject

```java
import org.json.JSONObject;
```

Importă clasa folosită pentru parsarea și navigarea în structuri JSON.

---

### Import IOException

```java
import java.io.IOException;
```

Importă excepția aruncată când operațiile I/O (network, disk) eșuează.

---

### Import OkHttp Call

```java
import okhttp3.Call;
```

Importă `Call` - reprezintă un request HTTP pregătit pentru execuție.

---

### Import OkHttp Callback

```java
import okhttp3.Callback;
```

Importă interfața `Callback` pentru gestionarea asincronă a răspunsurilor HTTP.

---

### Import OkHttpClient

```java
import okhttp3.OkHttpClient;
```

Importă `OkHttpClient` - clientul HTTP responsabil pentru efectuarea request-urilor.

---

### Import OkHttp Request

```java
import okhttp3.Request;
```

Importă clasa `Request` folosită pentru construirea request-urilor HTTP.

---

### Import OkHttp Response

```java
import okhttp3.Response;
```

Importă clasa `Response` care reprezintă răspunsul HTTP primit de la server.

---

### Declararea Clasei

```java
public class ChuckViewModel extends ViewModel {
```

Declară clasa `ChuckViewModel` care moștenește din `ViewModel`.

**Moștenire ViewModel:**
- Acces la `onCleared()` pentru cleanup la distrugerea ViewModel-ului
- Supraviețuiește rotațiilor ecranului
- Legat de lifecycle-ul fragmentului prin ViewModelProvider

---

### Declarare MutableLiveData

```java
    private final MutableLiveData<String> mText;
```

Declară LiveData-ul privat care va conține Chuck Norris fact-ul.

**private:** Doar ViewModel-ul poate modifica valoarea.

**final:** Referința nu se schimbă niciodată (obiectul este inițializat o singură dată).

**MutableLiveData<String>:** Tipul generic `<String>` indică că LiveData-ul conține text.

**Nume:** Prefixul `m` sugerează "member variable" (convenție).

---

### Declarare OkHttpClient

```java
    private OkHttpClient client;
```

Declară clientul HTTP folosit pentru efectuarea request-urilor.

**Tip:** Variabilă de instanță, nu final (recreată în `fetchCNFact()`).

---

### Constructor - Semnătura

```java
    public ChuckViewModel() {
```

Constructor public fără parametri, apelat de ViewModelProvider la crearea ViewModel-ului.

**Când se apelează:**
- Prima dată când `ViewModelProvider.get()` este apelat pentru acest fragment
- NU la fiecare recreare a view-ului
- NU la rotații ecran (ViewModel-ul supraviețuiește)

---

### Inițializare MutableLiveData

```java
        mText = new MutableLiveData<>();
```

Creează instanța de MutableLiveData.

**<>:** Diamond operator (Java 7+), tipul `<String>` este dedus din declarație.

**Stare inițială:** Valoarea este null până când primul request se finalizează.

---

### Apel Inițial Fetch

```java
        fetchCNFact();
```

Încarcă imediat un Chuck Norris fact la crearea ViewModel-ului.

**Efect:**
- La prima navigare la ChuckFragment, fact-ul este încărcat automat
- La revenirea la fragment (din back stack), ViewModel-ul există deja, deci nu se reîncarcă
- LiveData va emite ultima valoare către noul observer

---

### Metoda fetchCNFact - Semnătura

```java
    public void fetchCNFact() {
```

Metodă publică pentru încărcarea unui nou Chuck Norris fact de la API.

**Accesibilitate:** Public pentru a putea fi apelată din ChuckFragment (butonul Refresh).

**Return:** void (rezultatul este emis prin LiveData).

---

### Definire URL API

```java
        String url = "https://api.chucknorris.io/jokes/random";
```

URL-ul endpoint-ului API care returnează un Chuck Norris fact aleator.

**API:** Gratuit, fără autentificare, returnează JSON.

**Format răspuns:**
```json
{
  "icon_url": "...",
  "id": "...",
  "url": "...",
  "value": "Chuck Norris can divide by zero."
}
```

---

### Construire Request - Builder

```java
        Request request = new Request.Builder()
```

Creează un builder pentru construirea request-ului HTTP.

**Builder Pattern:** Permite configurarea treptată a request-ului.

---

### Setare URL Request

```java
                .url(url)
```

Setează URL-ul către care se va efectua request-ul.

**Metodă:** GET implicit (dacă nu se specifică altfel).

---

### Finalizare Build Request

```java
                .build();
```

Finalizează construirea request-ului și returnează obiectul `Request` complet.

---

### Creare OkHttpClient

```java
        client = new OkHttpClient();
```

Creează o instanță nouă de OkHttpClient pentru efectuarea request-ului.

**Note:**
- O nouă instanță la fiecare apel (nu reutilizare)
- În producție, ar fi preferabil un singur client reutilizat (connection pooling)

---

### Execuție Request Asincron - newCall

```java
        client.newCall(request).enqueue(new Callback() {
```

Execută request-ul HTTP pe un thread secundar (background).

**client.newCall(request):**
- Creează un `Call` gata de executare cu request-ul specificat

**.enqueue(Callback):**
- Execută request-ul asincron
- Primește un `Callback` pentru gestionarea răspunsului
- Returnează imediat (nu blochează UI thread-ul)

**new Callback():**
- Implementare anonimă a interfeței `Callback`
- Trebuie să implementeze `onFailure()` și `onResponse()`

---

### Override onFailure

```java
            @Override
            public void onFailure(Call call, IOException e) {
```

Metodă apelată automat când request-ul eșuează.

**Parametri:**

**Call call:**
- Request-ul care a eșuat
- Util pentru retry sau logging

**IOException e:**
- Excepția care a cauzat eșecul
- Exemple: timeout, no internet, DNS failure

**Thread:** Rulează pe un thread OkHttp (NU main thread).

---

### Update LiveData cu Eroare

```java
                mText.postValue("Failed to load joke." + e.getMessage());
```

Actualizează LiveData cu mesaj de eroare în caz de eșec.

**postValue():**
- Thread-safe pentru actualizări din background threads
- Postează valoarea pe main thread unde observatorii vor primi update-ul
- Dacă se apelează de mai multe ori rapid, doar ultima valoare este garantată

**"Failed to load joke." + e.getMessage():**
- Concatenare mesaj de eroare cu detalii tehnice
- Afișat în TextView din ChuckFragment

**Alternative:**
- `setValue()` = doar pentru main thread (ar cauza crash aici)

---

### Override onResponse

```java
            @Override
            public void onResponse(Call call, Response response) throws IOException {
```

Metodă apelată automat când se primește răspuns de la server (chiar și pentru coduri de eroare 4xx/5xx).

**Parametri:**

**Call call:**
- Request-ul pentru care s-a primit răspuns

**Response response:**
- Obiect conținând status code, headers, body

**throws IOException:**
- Metoda poate arunca IOException (ex: la citirea body-ului)

**Thread:** Rulează pe un thread OkHttp (NU main thread).

---

### Verificare Succes Response

```java
                if (!response.isSuccessful()) {
```

Verifică dacă răspunsul are cod de succes (2xx).

**response.isSuccessful():**
- `true` = coduri 200-299
- `false` = coduri 3xx, 4xx, 5xx

---

### Update LiveData pentru Response Nereușit

```java
                    mText.postValue("Failed to load joke.");
```

Actualizează LiveData cu mesaj de eroare pentru răspunsuri cu cod de eroare.

**Exemple situații:**
- 404 Not Found
- 500 Internal Server Error
- 503 Service Unavailable

---

### Early Return

```java
                    return;
```

Oprește execuția metodei după setarea erorii, prevenind procesarea ulterioară.

---

### Try Block - Parsare JSON

```java
                try {
```

Începe bloc try pentru gestionarea erorilor de parsare JSON.

**De ce try:** Parsarea JSON poate eșua dacă formatul este invalid sau câmpurile lipsesc.

---

### Citire Body Response

```java
                    String responseData = response.body().string();
```

Extrage corpul răspunsului ca String.

**response.body():**
- Returnează `ResponseBody` (poate fi null)
- În acest caz, garantat non-null pentru răspunsuri reușite

**.string():**
- Citește întregul body ca String
- **Atenție:** Poate fi apelat o singură dată (consumă stream-ul)
- Poate arunca IOException

**responseData:**
```json
{
  "icon_url": "https://...",
  "id": "abc123",
  "url": "https://...",
  "value": "Chuck Norris can compile syntax errors."
}
```

---

### Creare JSONObject

```java
                    JSONObject json = new JSONObject(responseData);
```

Parsează String-ul JSON într-un obiect JSONObject pentru acces la câmpuri.

**Constructor:**
- Primește String JSON
- Poate arunca JSONException dacă JSON-ul este invalid

---

### Extragere Câmp Value

```java
                    String cnfact = json.getString("value");
```

Extrage valoarea câmpului "value" din obiect JSON.

**json.getString("value"):**
- Caută cheia "value" în obiect
- Returnează valoarea ca String
- Aruncă JSONException dacă cheia nu există sau valoarea nu e String

**cnfact:** Conține textul Chuck Norris fact-ului propriu-zis.

---

### Update LiveData cu Fact-ul

```java
                    mText.postValue(cnfact);
```

Actualizează LiveData cu Chuck Norris fact-ul extras.

**Rezultat:**
- Observatorii din ChuckFragment primesc noua valoare
- TextView-ul este actualizat automat prin method reference

---

### Catch JSONException

```java
                } catch (JSONException e) {
```

Prinde excepțiile aruncate în timpul parsării JSON.

**Când se întâmplă:**
- JSON invalid
- Câmpul "value" lipsește
- Valoarea "value" nu e String

---

### Update LiveData pentru Eroare Parsare

```java
                    mText.postValue("Failed to parse joke.");
```

Actualizează LiveData cu mesaj de eroare pentru eșecuri de parsare JSON.

---

### Metoda getText - Semnătura

```java
    public LiveData<String> getText() {
```

Metodă getter care expune LiveData-ul către fragment pentru observare.

**Return tip:** `LiveData<String>` (NU MutableLiveData)

**Encapsulare:**
```
ChuckViewModel:
    private MutableLiveData<String> mText  ◄─ Poate fi modificat
                     │
                     └─── public LiveData<String> getText()
                                        │
                                        └──> ChuckFragment
                                             │
                                             └──> Doar observare (read-only)
```

---

### Return mText

```java
        return mText;
```

Returnează referința la MutableLiveData (upcast automat la LiveData).

**Upcast implicit:**
```java
MutableLiveData<String> → LiveData<String>
```

Fragment-ul primește LiveData (fără acces la `setValue()` sau `postValue()`).

---

## Arhitectură MVVM în ChuckViewModel

```
ChuckFragment (View)
    │
    ├─── onCreate()
    │    └──> ViewModelProvider.get(ChuckViewModel.class)
    │         │
    │         └──> ChuckViewModel constructor ◄─ CREARE
    │              │
    │              ├──> mText = new MutableLiveData<>()
    │              └──> fetchCNFact() ◄─ REQUEST INIȚIAL
    │                   │
    │                   └──> OkHttp async request
    │
    ├─── onCreateView()
    │    └──> chuckViewModel.getText().observe(...)
    │         │
    │         └──> Subscribe la mText LiveData
    │
    │ [ViewModel execută request în background]
    │
    └──> mText.postValue(cnfact) ◄─ UPDATE DE LA BACKGROUND
         │
         └──> LiveData notifică observatori (Main Thread)
              │
              └──> textView::setText(cnfact) ◄─ UI UPDATE


[User apasă butonul Refresh]
    │
    └──> refreshButton.setOnClickListener()
         │
         └──> chuckViewModel.fetchCNFact() ◄─ REQUEST MANUAL
              │
              └──> Procesul se repetă
```

---

## Lifecycle ViewModel vs Fragment

```
Fragment creat prima dată
    │
    ├─── ViewModelProvider.get() ◄─ ChuckViewModel CREAT
    │    │
    │    └──> Constructor apelat
    │         └──> fetchCNFact() ◄─ Request inițial
    │
    ├─── Fragment activ
    │    └──> LiveData observat
    │
    │ [User rotește ecranul]
    │
    ├─── Fragment DISTRUS
    │    ├─── onDestroyView()
    │    └─── onDestroy()
    │
    ├─── Fragment RECREAT
    │    ├─── onCreate()
    │    ├─── onCreateView()
    │    └──> ViewModelProvider.get()
    │         │
    │         └──> ChuckViewModel EXISTENT returnat
    │              │
    │              └──> Constructor NU se apelează din nou
    │                   │
    │                   └──> mText conține ULTIMA VALOARE
    │                        │
    │                        └──> Observer primește valoarea instant
    │
    │ [User navighează la alt fragment]
    │
    ├─── Fragment în back stack
    │    ├─── onDestroyView() ◄─ View distrus
    │    └─── ChuckViewModel SUPRAVIEȚUIEȘTE
    │
    │ [User navighează înapoi cu back]
    │
    ├─── Fragment recreează view
    │    └──> ViewModelProvider.get()
    │         │
    │         └──> ChuckViewModel EXISTENT returnat
    │              └──> Datele intact păstrate
    │
    │ [Fragment eliminat definitiv din back stack]
    │
    └──> ChuckViewModel.onCleared() ◄─ CLEANUP
         └──> ViewModel distrus
```

---

## Flux Request HTTP Complet

```
User interaction (app start sau refresh button)
    │
    └──> fetchCNFact()
         │
         ├──> String url = "https://api.chucknorris.io/jokes/random"
         │
         ├──> Request request = new Request.Builder()
         │                          .url(url)
         │                          .build()
         │
         ├──> client = new OkHttpClient()
         │
         └──> client.newCall(request).enqueue(Callback)
              │
              ├─── [Background Thread] ──────────────────┐
              │                                          │
              │ ┌──────────────────────────────────────┐ │
              │ │ Internet Request                     │ │
              │ │     │                                │ │
              │ │     ├─── DNS Lookup                  │ │
              │ │     ├─── TCP Connection              │ │
              │ │     ├─── HTTPS Handshake             │ │
              │ │     ├─── Send GET Request            │ │
              │ │     └─── Receive Response            │ │
              │ └──────────────────────────────────────┘ │
              │                                          │
              ├──> SUCCESS ────────────────────────────┐ │
              │    │                                   │ │
              │    └──> onResponse(call, response)    │ │
              │         │                              │ │
              │         ├─── response.isSuccessful()  │ │
              │         │    │                         │ │
              │         │    ├─── FALSE ──> postValue("Failed") │
              │         │    │                         │ │
              │         │    └─── TRUE                 │ │
              │         │         │                    │ │
              │         │         ├─── response.body().string() │
              │         │         │                    │ │
              │         │         ├─── new JSONObject(responseData) │
              │         │         │                    │ │
              │         │         ├─── json.getString("value") │
              │         │         │                    │ │
              │         │         └─── mText.postValue(cnfact) │
              │         │              │               │ │
              │         │              └──> [Post to Main Thread] │
              │         │                              │ │
              │         └─── JSONException             │ │
              │              │                         │ │
              │              └──> postValue("Failed to parse") │
              │                                        │ │
              └──> FAILURE ────────────────────────────┘ │
                   │                                     │
                   └──> onFailure(call, e) ──────────────┘
                        │
                        └──> postValue("Failed to load" + e.getMessage())
                             │
                             │
                   ┌─────────▼──────────┐
                   │   Main Thread      │
                   │                    │
                   │  LiveData.setValue │
                   │        │           │
                   │        └──> Notify observers
                   │             │      │
                   └─────────────┼──────┘
                                 │
                                 ▼
                   ChuckFragment Observer
                        │
                        └──> textView::setText
                             │
                             └──> UI Updated
```

---

## Comparație postValue vs setValue

| Aspect | postValue() | setValue() |
|--------|-------------|------------|
| **Thread** | Orice thread (thread-safe) | Doar main thread |
| **Utilizare** | Background threads | Main thread |
| **Comportament** | Postează pe main thread | Setează imediat |
| **Apeluri multiple** | Doar ultima valoare garantată | Toate valorile emise |
| **În acest cod** | ✅ Folosit (callback pe background thread) | ❌ Ar cauza crash |

**Exemplu:**
```java
// CORECT (în onResponse care rulează pe background thread)
mText.postValue(cnfact);

// GREȘIT (ar cauza crash)
mText.setValue(cnfact);  // CalledFromWrongThreadException
```

---

## Tipuri de Erori Gestionate

### 1. Network Failure (onFailure)

```
Cauze:
- No internet connection
- Timeout
- DNS failure
- Server unreachable

Gestionare:
onFailure(call, e)
    └──> postValue("Failed to load joke." + e.getMessage())
```

### 2. HTTP Error Response

```
Cauze:
- 404 Not Found
- 500 Internal Server Error
- 503 Service Unavailable

Gestionare:
if (!response.isSuccessful())
    └──> postValue("Failed to load joke.")
```

### 3. JSON Parse Error

```
Cauze:
- Invalid JSON format
- Missing "value" field
- Wrong data type

Gestionare:
catch (JSONException e)
    └──> postValue("Failed to parse joke.")
```

---

## Encapsulare LiveData

```
┌─────────────────────────────────────────────────┐
│          ChuckViewModel (ViewModel)             │
│                                                 │
│  private final MutableLiveData<String> mText    │
│          │                                      │
│          ├─── setValue() ───────────────────┐   │
│          ├─── postValue() ──────────────────┤   │
│          └─── getValue() ───────────────────┤   │
│                                              │   │
│  public LiveData<String> getText()           │   │
│      return mText; ◄─────────────────────────┘   │
│          │                                       │
└──────────┼───────────────────────────────────────┘
           │ (Upcast to LiveData)
           │
           ▼
┌─────────────────────────────────────────────────┐
│          ChuckFragment (View)                   │
│                                                 │
│  chuckViewModel.getText().observe(...)          │
│          │                                      │
│          └─── Doar citire (read-only)           │
│               │                                 │
│               ├─── observe() ✅                  │
│               ├─── getValue() ✅                 │
│               ├─── setValue() ❌ (nu există)     │
│               └─── postValue() ❌ (nu există)    │
│                                                 │
└─────────────────────────────────────────────────┘
```

**Avantaje:**
- Fragment-ul nu poate modifica accidental datele
- Separare clară a responsabilităților
- ViewModel controlează complet când și cum se actualizează datele

---

## Relația cu ChuckFragment

```
ChuckFragment.java                  ChuckViewModel.java
─────────────────                   ───────────────────

onCreateView()
    │
    ├──> ViewModelProvider.get()
    │         │
    │         └──────────────────────────> Constructor
    │                                           │
    │                                           ├──> mText = new MutableLiveData<>()
    │                                           └──> fetchCNFact()
    │                                                │
    │                                                └──> OkHttp async request
    │
    ├──> getText().observe(getViewLifecycleOwner(), textView::setText)
    │         │
    │         └──────────────────────────> return mText
    │
    └──> refreshButton.setOnClickListener()
              │
              └──────────────────────────> fetchCNFact()
                                               │
                                               └──> New request


onDestroyView()
    │
    └──> binding = null
         (ViewModel SUPRAVIEȚUIEȘTE)
```

---

## Best Practices Demonstrate

### ✅ Encapsulare LiveData
```java
private final MutableLiveData<String> mText;  // Privat
public LiveData<String> getText() {           // Public read-only
    return mText;
}
```

### ✅ postValue pentru Background Threads
```java
// În callback OkHttp (background thread)
mText.postValue(cnfact);  // Thread-safe
```

### ✅ Gestionare Erori
```java
// Network failure
onFailure() → postValue("Failed to load...")

// HTTP error
if (!response.isSuccessful()) → postValue("Failed to load...")

// Parse error
catch (JSONException) → postValue("Failed to parse...")
```

### ✅ Metodă Publică pentru Refresh
```java
public void fetchCNFact() {
    // Poate fi apelată din fragment pentru refresh
}
```

---

