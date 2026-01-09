# MainViewModel.java - Documentație Linie cu Linie

<!-- TOC -->

- [MainViewModel.java - Documentație Linie cu Linie](#mainviewmodeljava---documenta%C8%9Bie-linie-cu-linie)
    - [1. Prezentare](#1-prezentare)
    - [2. Analiza Linie cu Linie](#2-analiza-linie-cu-linie)
        - [2.1. Declararea Pachetului](#21-declararea-pachetului)
        - [2.2. Import-uri Lifecycle](#22-import-uri-lifecycle)
        - [2.3. Import Log](#23-import-log)
        - [2.4. Import-uri JSON](#24-import-uri-json)
        - [2.5. Import IOException](#25-import-ioexception)
        - [2.6. Import-uri OkHttp](#26-import-uri-okhttp)
        - [2.7. Declararea Clasei](#27-declararea-clasei)
        - [2.8. Variabile de Instanță](#28-variabile-de-instan%C8%9B%C4%83)
        - [2.9. Constructor](#29-constructor)
        - [2.10. Inițializare LiveData](#210-ini%C8%9Bializare-livedata)
        - [2.11. Apel Descărcare Inițială](#211-apel-desc%C4%83rcare-ini%C8%9Bial%C4%83)
        - [2.12. Metoda fetchJoke - Semnătura](#212-metoda-fetchjoke---semn%C4%83tura)
        - [2.13. Definire URL API](#213-definire-url-api)
        - [2.14. Construire Request HTTP](#214-construire-request-http)
        - [2.15. Inițializare Client HTTP](#215-ini%C8%9Bializare-client-http)
        - [2.16. Execuție Asincronă Request](#216-execu%C8%9Bie-asincron%C4%83-request)
        - [2.17. Callback onFailure - Semnătura](#217-callback-onfailure---semn%C4%83tura)
        - [2.18. Logging Eroare Rețea](#218-logging-eroare-re%C8%9Bea)
        - [2.19. Actualizare LiveData cu Mesaj Eroare](#219-actualizare-livedata-cu-mesaj-eroare)
        - [2.20. Callback onResponse - Semnătura](#220-callback-onresponse---semn%C4%83tura)
        - [2.21. Verificare Succes HTTP](#221-verificare-succes-http)
        - [2.22. Actualizare LiveData la Eroare HTTP](#222-actualizare-livedata-la-eroare-http)
        - [2.23. Return Timpuriu](#223-return-timpuriu)
        - [2.24. Bloc try - Parsare JSON](#224-bloc-try---parsare-json)
        - [2.25. Extragere Body Răspuns](#225-extragere-body-r%C4%83spuns)
        - [2.26. Parsare JSON Object](#226-parsare-json-object)
        - [2.27. Extragere Câmpuri JSON](#227-extragere-c%C3%A2mpuri-json)
        - [2.28. Compunere Text Glumă](#228-compunere-text-glum%C4%83)
        - [2.29. Actualizare LiveData cu Gluma](#229-actualizare-livedata-cu-gluma)
        - [2.30. Bloc catch - Captarea excepției JSON](#230-bloc-catch---captarea-excep%C8%9Biei-json)
        - [2.31. Logging Eroare Parsare](#231-logging-eroare-parsare)
        - [2.32. Actualizare LiveData la Eroare Parsare](#232-actualizare-livedata-la-eroare-parsare)
        - [2.33. Metoda getText - Semnătura](#233-metoda-gettext---semn%C4%83tura)
        - [2.34. Return LiveData](#234-return-livedata)
    - [3. Gestionarea Thread-urilor](#3-gestionarea-thread-urilor)
        - [3.1. Background Thread OkHttp Worker](#31-background-thread-okhttp-worker)
        - [3.2. Main Thread UI Thread](#32-main-thread-ui-thread)
    - [4. Ciclu de Viață ViewModel vs Activity](#4-ciclu-de-via%C8%9B%C4%83-viewmodel-vs-activity)
    - [5. Comparație postValue vs setValue](#5-compara%C8%9Bie-postvalue-vs-setvalue)
        - [5.1. postValue](#51-postvalue)
        - [5.2. setValue](#52-setvalue)

<!-- /TOC -->

## Prezentare

Clasa `MainViewModel` este componenta **ViewModel** din arhitectura MVVM care:
- **Gestionează logica de business** (descărcare și parsare date de la API)
- **Stochează starea UI-ului** (textul glumei) independent de ciclu de viață activității
- **Expune date prin LiveData** pentru comunicare reactivă cu UI-ul
- **Supraviețuiește schimbărilor de configurare** (rotație ecran, dark mode, etc.)

**Rol în arhitectură:**
- Intermediar între Model (API jokes) și View (MainActivity)
- Nu conține referințe la Context, View sau Activity (previne memory leaks)
- Toate operațiile de rețea se fac aici, nu în Activity

## Analiza Linie cu Linie

### Declararea Pachetului

```java
package ro.makore.akrilki_03;
```

Declară pachetul în care se află clasa.

---

### Import-uri Lifecycle

```java
import androidx.lifecycle.LiveData;
```

Importă interfața `LiveData<T>` care este read-only și folosită pentru a expune date observabile către UI. UI-ul poate doar să observe, nu să modifice direct.

```java
import androidx.lifecycle.MutableLiveData;
```

Importă clasa `MutableLiveData<T>` care extinde LiveData și permite modificarea valorii prin `setValue()` și `postValue()`. Folosită intern în ViewModel pentru actualizarea datelor.

**Diferența:**
- `LiveData` = read-only (pentru expunere publică)
- `MutableLiveData` = read-write (pentru uz intern)

```java
import androidx.lifecycle.ViewModel;
```

Importă clasa de bază `ViewModel` care oferă suport pentru:
- Supraviețuirea schimbărilor de configurare
- Metoda `onCleared()` pentru curățare resurse
- Integrare cu ViewModelProvider

---

### Import Log

```java
import android.util.Log;
```

Importă utilitarul pentru logging în Logcat.

---

### Import-uri JSON

```java
import org.json.JSONException;
```

Importă excepția aruncată la parsare JSON eșuată.

```java
import org.json.JSONObject;
```

Importă clasa pentru parsare și manipulare obiecte JSON.

---

### Import IOException

```java
import java.io.IOException;
```

Importă excepția pentru erori de I/O și rețea.

---

### Import-uri OkHttp

```java
import okhttp3.Call;
```

Importă interfața `Call` care reprezintă un HTTP request pregătit pentru execuție.

```java
import okhttp3.Callback;
```

Importă interfața `Callback` pentru procesare asincronă a răspunsurilor HTTP.

```java
import okhttp3.OkHttpClient;
```

Importă clientul HTTP folosit pentru comunicarea cu API-ul.

```java
import okhttp3.Request;
```

Importă clasa pentru construirea HTTP request-urilor.

```java
import okhttp3.Response;
```

Importă clasa care reprezintă răspunsul HTTP de la server.

---

### Declararea Clasei

```java
public class MainViewModel  extends ViewModel {
```

Declară clasa `MainViewModel` care moștenește din `ViewModel`. Moștenirea oferă:
- Ciclu de viață extins (supraviețuire la rotație)
- Integrare cu ViewModelProvider
- Metoda `onCleared()` pentru cleanup

---

### Variabile de Instanță

```java
    private final MutableLiveData<String> mText;
```

Declară o variabilă `final` de tip `MutableLiveData<String>` care stochează textul glumei.

**Caracteristici:**
- `private` = accesibilă doar în această clasă
- `final` = nu poate fi reasignată (referința rămâne constantă)
- `MutableLiveData` = poate fi modificată intern prin `postValue()`

**De ce final?**
- Previne reasignarea accidentală
- Obiectul LiveData este creat o singură dată și reutilizat
- Doar valoarea internă se schimbă, nu referința

```java
    private OkHttpClient client;
```

Declară variabila pentru clientul HTTP. Este inițializată în `fetchJoke()` (ar putea fi optimizat prin inițializare în constructor).

---

### Constructor

```java
    public MainViewModel () {
```

Constructor public fără parametri. **Obligatoriu** pentru ViewModel-uri create de ViewModelProvider.

**Restricție:** ViewModelProvider necesită constructor fără parametri sau folosirea unui Factory pentru parametri.

---

### Inițializare LiveData

```java
        mText = new MutableLiveData<>();
```

Creează instanța de `MutableLiveData<String>`. Operatorul `<>` (diamond operator) inferă tipul generic din declarație.

**Stare inițială:** LiveData nu conține nicio valoare (valoare `null` implicit).

---

### Apel Descărcare Inițială

```java
        fetchJoke();
```

Apelează `fetchJoke()` imediat în constructor pentru a descărca prima glumă când ViewModel-ul este creat.

**Comportament:**
- Prima lansare aplicație → Constructor apelat → `fetchJoke()` descarcă gluma
- Rotație ecran → Constructor NU este apelat → ViewModel existent refolosit → Fără API call

---

### Metoda fetchJoke - Semnătura

```java
    public void fetchJoke() {
```

Metodă publică care descarcă o glumă nouă de la API. Apelată din:
- Constructor (prima glumă)
- MainActivity când user apasă Refresh

---

### Definire URL API

```java
        String url = "https://official-joke-api.appspot.com/random_joke";
```

URL-ul endpoint-ului API care returnează o glumă aleatoare în format JSON.

---

### Construire Request HTTP

```java
        Request request = new Request.Builder()
                .url(url)
                .build();
```

Construiește un obiect `Request` folosind Builder pattern:
- `.url(url)` = setare URL destinație
- `.build()` = construire obiect final

Metoda HTTP implicită este GET.

---

### Inițializare Client HTTP

```java
        client = new OkHttpClient();        
```

Creează o nouă instanță de `OkHttpClient` pentru fiecare request.

**Observație:** Ineficient - ar trebui creat o singură dată (în constructor) și reutilizat. Crearea repetată consumă resurse inutil.

**Optimizare sugerată:**
```java
// În constructor:
client = new OkHttpClient();

// În fetchJoke():
// client deja existent, nu mai creăm altul
```

---

### Execuție Asincronă Request

```java
        client.newCall(request).enqueue(new Callback() {
```

Execută request-ul **asincron** (pe thread în background):
- `newCall(request)` = creează un Call din Request
- `.enqueue(Callback)` = pune în coadă pentru execuție asincronă
- `new Callback()` = creează implementare anonimă pentru procesare răspuns

---

### Callback onFailure - Semnătura

```java
            @Override
            public void onFailure(Call call, IOException e) {
```

Suprascrie metoda apelată când request-ul eșuează (eroare de rețea, timeout, DNS failure).

**Execuție:** Pe un thread în background (nu Main Thread).

---

### Logging Eroare Rețea

```java
                Log.e("MainActivity", "Failed to fetch joke", e);
```

Scrie mesaj de eroare în Logcat cu stack trace complet.

**Notă:** Tag-ul "MainActivity" ar trebui să fie "MainViewModel" pentru acuratețe.

---

### Actualizare LiveData cu Mesaj Eroare

```java
                mText.postValue("Failed to load joke." + e.getMessage());
```

Actualizează LiveData cu mesaj de eroare care va fi afișat în UI.

**De ce postValue() și nu setValue()?**
- `postValue()` = safe din orice thread (background sau Main)
- `setValue()` = doar din Main Thread
- Callback-ul `onFailure()` rulează pe background thread → trebuie `postValue()`

**Flux:**
```
Background Thread                Main Thread
      │                               │
      │─── onFailure() apelat         │
      │─── postValue(error)            │
      │        │                       │
      │        └──────────────────────>│
      │                                │
      │                          LiveData notifică
      │                          Observer pe Main Thread
      │                                │
      │                          jokeTextView.setText(error)
```

---

### Callback onResponse - Semnătura

```java
            @Override
            public void onResponse(Call call, Response response) throws IOException {
```

Suprascrie metoda apelată când serverul răspunde (chiar dacă cu eroare HTTP).

**Execuție:** Pe un thread în background.

---

### Verificare Succes HTTP

```java
                if (!response.isSuccessful()) {
```

Verifică dacă răspunsul HTTP retunreaza eroare (status code în afara 200-299).

---

### Actualizare LiveData la Eroare HTTP

```java
                    mText.postValue("Failed to load joke.");
```

Actualizează LiveData cu mesaj generic de eroare pentru coduri HTTP ca 404, 500, etc.

**Folosește `postValue()`** pentru că rulează pe background thread.

---

### Return Timpuriu

```java
                    return;
```

Iese din metoda `onResponse()` fără a continua procesarea.

---

### Bloc try - Parsare JSON

```java
                try {
```

Deschide bloc try pentru gestionarea excepțiilor de parsare JSON.

---

### Extragere Body Răspuns

```java
                    String responseData = response.body().string();
```

Extrage conținutul body-ului HTTP ca String. 

**Atenție:** `.string()` poate fi apelat o singură dată (consumă stream-ul).

---

### Parsare JSON Object

```java
                    JSONObject json = new JSONObject(responseData);
```

Parsează String-ul JSON în obiect `JSONObject` pentru acces la câmpuri.

Poate arunca `JSONException` dacă JSON-ul este invalid.

---

### Extragere Câmpuri JSON

```java
                    String setup = json.getString("setup");
                    String punchline = json.getString("punchline");
```

Extrage valorile câmpurilor "setup" și "punchline" din JSON.

Fiecare apel `getString()` poate arunca `JSONException` dacă câmpul lipsește.

---

### Compunere Text Glumă

```java
                    String joke = setup + "\n\n" + punchline;
```

Concatenează setup-ul și punchline-ul cu două linii noi între ele pentru formatare.

---

### Actualizare LiveData cu Gluma

```java
                    mText.postValue(joke);
```

Actualizează LiveData cu gluma completă. LiveData va notifica automat toți observatorii (MainActivity), care vor actualiza UI-ul.

**De ce postValue()?**
- Callback-ul `onResponse()` rulează pe background thread
- `postValue()` transferă actualizarea pe Main Thread în mod safe
- `setValue()` ar arunca excepție pe background thread

**Flux complet:**
```
Background Thread (OkHttp)
    │
    ├─── HTTP Response primit
    ├─── JSON parsat
    ├─── String joke creat
    │
    └─── mText.postValue(joke)
         │
         │ (LiveData procesează intern)
         │
         ▼
Main Thread
    │
    ├─── LiveData.setValue(joke) apelat intern
    ├─── Verificare lifecycle (Activity ACTIVE?)
    │
    └─── Notificare Observer (dacă Activity ACTIVE)
         │
         └─── Lambda: jokeTextView.setText(joke)
```

---

### Bloc catch - Captarea excepției JSON

```java
                } catch (JSONException e) {
```

Capteaza excepția `JSONException` in cazul in care parsingul esueaza.

---

### Logging Eroare Parsare

```java
                    Log.e("MainActivity", "Failed to parse joke JSON", e);
```

Scrie eroare în Logcat cu stack trace.

**Notă:** Tag-ul ar trebui să fie "MainViewModel".

---

### Actualizare LiveData la Eroare Parsare

```java
                    mText.postValue("Failed to parse joke." + e.getMessage());
```

Actualizează LiveData cu mesaj de eroare de parsare care include mesajul excepției.

---

### Metoda getText - Semnătura

```java
    public LiveData<String> getText() {
```

Metodă publică care returnează `LiveData<String>` (nu `MutableLiveData`).

**De ce LiveData în loc de MutableLiveData?**
- **Encapsulare:** Previne modificarea directă din MainActivity
- MainActivity poate doar **observe** (read-only)
- Doar ViewModel-ul poate **modifica** (prin `mText.postValue()`)



---

### Return LiveData

```java
        return mText;
```

Returnează referința la `mText`. Deși `mText` este `MutableLiveData`, este up-cast automat la `LiveData` din cauza tipului de return al metodei.

**Conversie implicită:**
```
MutableLiveData<String> → LiveData<String>
(subclasă)                (superclasă)
```

MainActivity primește o **viziune read-only** a acelorași date.

---

## Gestionarea Thread-urilor

### Background Thread (OkHttp Worker)

```
onFailure() și onResponse() rulează pe thread-uri în background
    │
    ├─── HTTP I/O operations
    ├─── JSON parsing
    └─── mText.postValue()
         │
         │ postValue() este thread-safe
         │ Transferă actualizarea pe Main Thread
         │
         ▼
    Main Thread (automat)
```

### Main Thread (UI Thread)

```
LiveData procesează postValue() pe Main Thread
    │
    ├─── Verifică lifecycle (Activity ACTIVE?)
    │
    └─── Dacă DA: Notifică observatori
         │
         └─── Lambda observer apelat
              │
              └─── jokeTextView.setText()
```

**De ce e safe?**
- `postValue()` pune valoarea într-o coadă thread-safe
- Un Runnable este postat pe Main Thread
- Runnable-ul apelează `setValue()` pe Main Thread
- Observer-ii sunt notificați pe Main Thread
- UI-ul este actualizat pe thread-ul corect

---

## Ciclu de Viață ViewModel vs Activity

```
┌─────────────────────────────────────────────────────────┐
│ Ciclu de Viață Activity                                 │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  onCreate() ─────┐                                      │
│  onStart()       │                                      │
│  onResume()      │  Activity ACTIVE                     │
│                  │                                      │
│  [User rotează]  │                                      │
│                  │                                      │
│  onPause()       │                                      │
│  onStop()        │                                      │
│  onDestroy() ────┘  Activity DESTROYED                  │
│                                                         │
│  onCreate() ─────┐  Activity RECREATĂ                   │
│  onStart()       │                                      │
│  onResume()      │  Activity ACTIVE din nou             │
│                  │                                      │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│ Ciclu de Viață ViewModel                                │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Constructor() ──┐                                      │
│  fetchJoke()     │  ViewModel CREAT                     │
│                  │  (la prima creare Activity)          │
│                  │                                      │
│  [User rotează]  │                                      │
│                  │  ViewModel SUPRAVIEȚUIEȘTE           │
│                  │  (nu este recreat!)                  │
│                  │                                      │
│  [Activity]      │                                      │
│  [recreată]      │  ViewModelProvider returnează        │
│                  │  instanța EXISTENTĂ                  │
│                  │                                      │
│  [User închide]  │                                      │
│  [aplicația]     │                                      │
│                  │                                      │
│  onCleared() ────┘  ViewModel DISTRUS                   │
│                     (curățare resurse)                  │
│                                                         │
└─────────────────────────────────────────────────────────┘

REZULTAT:
- mText.getValue() păstrează gluma după rotație
- Fără API call la rotație
- Date persistente între recreări Activity
```

---

## Comparație postValue() vs setValue()

### postValue()

```java
// Poate fi apelat din ORICE thread
mText.postValue("Hello");

Implementare internă (simplificată):
1. Adaugă valoare în coadă thread-safe
2. Postează Runnable pe Main Thread
3. Runnable apelează setValue() pe Main Thread
4. Observer-ii sunt notificați

SIGURANȚĂ:
✓ Safe din background thread
✓ Safe din Main Thread
✓ Thread-safe complet

PERFORMANȚĂ:
- Ușor mai lent (overhead de thread switching)
- Ideal pentru callback-uri asincrone
```

### setValue()

```java
// DOAR din Main Thread
mText.setValue("Hello");

Implementare internă (simplificată):
1. Verifică că thread-ul curent e Main Thread
2. Setează valoarea direct
3. Notifică observer-ii imediat

SIGURANȚĂ:
✗ Crash din background thread
✓ Safe din Main Thread

PERFORMANȚĂ:
- Foarte rapid (fără overhead)
- Ideal când deja pe Main Thread
```

**Regula:**
- În callback-uri HTTP (background) → **postValue()**
- În event handlers UI (Main Thread) → **setValue()** sau **postValue()**
- Când în dubiu → **postValue()** (întotdeauna safe)

---

