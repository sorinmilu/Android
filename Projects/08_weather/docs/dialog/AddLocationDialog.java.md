# AddLocationDialog.java — Documentație detaliată a codului sursă

Acest document prezintă codul complet al clasei `AddLocationDialog.java` împărțit în fragmente logice, fiecare precedat de explicații detaliate în limba română. Documentația este destinată programatorilor cu cunoștințe de Java dar cu experiență limitată în programarea Android.

## Context general

`AddLocationDialog` este un dialog custom (fereastră popup) care permite utilizatorului să adauge un oraș nou în lista de locații salvate. Dialog-ul oferă următoarele funcționalități avansate:

1. **Validare în timp real** - Pe măsură ce utilizatorul tastează, verifică dacă orașul există
2. **Debouncing** - Așteaptă 600ms după ultima tastare înainte de a face verificarea (evită request-uri excesive)
3. **Feedback vizual** - Afișează starea verificării cu culori (gri=verificare, verde=valid, roșu=invalid)
4. **Verificare online** - Face un request real către API pentru a confirma că orașul există
5. **Prevenție duplicate** - Nu permite adăugarea aceluiași oraș de două ori
6. **UX optim** - Butonul Add este dezactivat până când orașul este validat

Este un exemplu excelent de UX design responsiv și robust.

## 1. Declararea pachetului

Clasa aparține sub-pachetului `dialog` din aplicație.

```java
package ro.makore.akrilki_08.dialog;
```

**Explicație:**
- Sub-pachetul `dialog` grupează toate clasele de dialog-uri custom
- Organizare logică: separarea componentelor UI pe tipuri

## 2. Import-uri pentru componente Dialog

Import-uri pentru funcționalitatea de bază a dialog-urilor:

```java
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
```

**Linie cu linie:**
- **`Dialog`** - Clasa de bază pentru ferestre popup în Android
- **`Context`** - Necesar pentru accesarea resurselor și crearea dialog-ului
- **`Bundle`** - Container pentru salvarea/restaurarea stării
- **`Handler`** - Permite programarea executării de cod pe un thread specific
- **`Looper`** - Gestionează coada de mesaje a unui thread (Main thread are un Looper)

## 3. Import-uri pentru text și input

```java
import android.text.Editable;
import android.text.TextWatcher;
```

**Linie cu linie:**
- **`Editable`** - Interfață pentru text care poate fi modificat (conținutul unui EditText)
- **`TextWatcher`** - Interfață pentru a asculta modificările textului în timp real

## 4. Import-uri pentru componente UI

```java
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
```

**Linie cu linie:**
- **`View`** - Clasa de bază pentru toate componentele UI
- **`Window`** - Reprezintă fereastra de nivel superior
- **`Button`** - Buton pentru acțiuni (Add, Cancel)
- **`EditText`** - Câmp de text editabil pentru input utilizator
- **`TextView`** - Pentru afișarea stării (checking, valid, error)
- **`Toast`** - Mesaje temporare popup
- **`@NonNull`** - Adnotare care specifică că un parametru nu poate fi null

## 5. Import-uri pentru resurse și clase aplicație

```java
import ro.makore.akrilki_08.R;
import ro.makore.akrilki_08.api.WeatherAPI;
import ro.makore.akrilki_08.util.LocationManager;
import android.util.Log;
import java.io.IOException;
```

**Linie cu linie:**
- **`R`** - Clasa generată automat care conține ID-uri pentru toate resursele (layouts, strings, colors)
- **`WeatherAPI`** - Pentru verificarea online dacă orașul există
- **`LocationManager`** - Pentru salvarea și gestionarea orașelor
- **`Log`** - Pentru logging
- **`IOException`** - Excepție pentru erori de rețea

## 6. Declararea clasei și câmpurilor membre

### Declararea clasei

```java
public class AddLocationDialog extends Dialog {
```

**Explicație:**
- Extinde `Dialog` pentru a crea un dialog custom
- Moștenește funcționalitate de bază (afișare, ascundere, lifecycle)

### Constantă pentru logging

```java
    private static final String TAG = "AddLocationDialog";
```

**Explicație:**
- Tag pentru identificarea mesajelor în logcat

### Referințe la manageri și listeners

```java
    private final LocationManager locationManager;
    private final OnLocationAddedListener listener;
```

**Linie cu linie:**
- **`locationManager`** - Pentru salvarea orașelor și verificarea duplicatelor
- **`listener`** - Callback care va fi apelat când orașul este adăugat cu succes
- `final` - Aceste referințe nu se schimbă după inițializare

### Referințe către componentele UI

```java
    private EditText editLocationName;
    private TextView locationStatus;
    private Button btnAdd;
    private Button btnCancel;
```

**Linie cu linie:**
- **`editLocationName`** - Câmpul unde utilizatorul tastează numele orașului
- **`locationStatus`** - Afișează starea verificării (checking/valid/error)
- **`btnAdd`** - Butonul pentru adăugarea orașului
- **`btnCancel`** - Butonul pentru anularea operației

### Variabile de stare pentru debouncing

```java
    private boolean isChecking = false;
    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingCheck;
    private static final int CHECK_DELAY_MS = 600;
```

**Linie cu linie:**
- **`isChecking`** - Flag care indică dacă o verificare este în curs
- **`debounceHandler`** - Handler pe main thread pentru debouncing
- **`Looper.getMainLooper()`** - Looper-ul thread-ului principal UI
- **`pendingCheck`** - Runnable programat pentru verificare (poate fi anulat)
- **`CHECK_DELAY_MS`** - Delay de 600 milisecunde înainte de verificare
- **Debouncing**: Dacă utilizatorul tastează rapid "Bucharest", nu face 9 request-uri, doar unul după 600ms de la ultima tastare

### Variabile pentru cache și optimizare

```java
    private boolean lastValid = false;
    private String lastCheckedName = null;
    private boolean pendingAdd = false;
```

**Linie cu linie:**
- **`lastValid`** - Dacă ultima verificare a fost validă
- **`lastCheckedName`** - Ultimul oraș verificat (cache)
- **`pendingAdd`** - Dacă utilizatorul a apăsat Add în timp ce verificarea era în curs

## 7. Interfața OnLocationAddedListener - Pattern Callback

```java
    public interface OnLocationAddedListener {
        void onLocationAdded(String location);
    }
```

**Explicație:**
- **Pattern Callback**: Interfață pentru comunicare inversă
- Dialog-ul apelează această metodă când orașul este adăugat
- MainActivity implementează această interfață și actualizează UI-ul
- **Parametru:** `location` - Numele orașului adăugat

## 8. Constructorul dialog-ului

```java
    public AddLocationDialog(@NonNull Context context, LocationManager locationManager, OnLocationAddedListener listener) {
        super(context);
        this.locationManager = locationManager;
        this.listener = listener;
    }
```

**Linie cu linie:**
- **`@NonNull Context context`** - Context-ul nu poate fi null (crash la runtime dacă este)
- `super(context)` - Apelează constructorul clasei părinte Dialog
- `this.locationManager = locationManager` - Salvează referința pentru utilizare ulterioară
- `this.listener = listener` - Salvează callback-ul pentru a fi apelat la succes

## 9. Metoda onCreate() - Inițializarea dialog-ului

### Configurare inițială și încărcare layout

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_location);
```

**Linie cu linie:**
- `super.onCreate(savedInstanceState)` - Apelează onCreate din Dialog
- `requestWindowFeature(Window.FEATURE_NO_TITLE)` - Elimină bara de titlu default
- Trebuie apelat ÎNAINTE de `setContentView()`
- `setContentView(R.layout.dialog_add_location)` - Încarcă layout-ul XML al dialog-ului

### Inițializarea referințelor UI

```java
        editLocationName = findViewById(R.id.edit_location_name);
        locationStatus = findViewById(R.id.location_status);
        btnAdd = findViewById(R.id.btn_add);
        btnCancel = findViewById(R.id.btn_cancel);
```

**Explicație:**
- Găsim și salvăm referințe către toate componentele UI din layout
- Similar cu findViewById în Activity, dar se aplică pe layout-ul dialog-ului

## 10. TextWatcher - Monitorizarea input-ului în timp real

### Configurarea TextWatcher-ului

```java
        // Add debounced text watcher to check location availability (avoid firing on every keystroke)
        editLocationName.addTextChangedListener(new TextWatcher() {
```

**Explicație:**
- `addTextChangedListener()` - Înregistrează un ascultător pentru modificări text
- `new TextWatcher()` - Instanță anonimă (trebuie să implementeze 3 metode)
- Comentariul explică strategia de debouncing

### Metode callback nevalorificate

```java
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
```

**Explicație:**
- `beforeTextChanged()` - Apelată înainte ca textul să fie modificat (nu ne interesează)
- `onTextChanged()` - Apelată în timpul modificării (nu ne interesează)
- Corpuri goale: implementăm doar pentru a satisface interfața

### Logica principală: afterTextChanged

```java
            @Override
            public void afterTextChanged(Editable s) {
                final String locationName = s.toString().trim();
```

**Linie cu linie:**
- `afterTextChanged()` - Apelată după ce textul a fost modificat
- `s.toString()` - Convertește Editable în String
- `trim()` - Elimină spațiile de la început și sfârșit
- `final` - Necesar pentru a folosi variabila în Runnable (inner class)

### Anularea verificării anterioare (Debouncing)

```java
                // Cancel any pending check
                if (pendingCheck != null) {
                    debounceHandler.removeCallbacks(pendingCheck);
                    pendingCheck = null;
                }
```

**Linie cu linie:**
- Verificăm dacă există o verificare programată
- `removeCallbacks(pendingCheck)` - Anulează verificarea programată anterior
- Dacă utilizatorul continuă să tasteze, verificarea anterioară nu se mai execută
- Setăm la null pentru a marca că nu mai există verificare pending

### Gestionarea input-ului gol

```java
                if (locationName.isEmpty()) {
                    locationStatus.setVisibility(View.GONE);
                    btnAdd.setEnabled(false);
                    return;
                }
```

**Linie cu linie:**
- Dacă câmpul este gol, ascundem mesajul de status
- `View.GONE` - Componenta devine invizibilă și nu ocupă spațiu în layout
- Dezactivăm butonul Add
- `return` - Ieșim din metodă, nu continuăm verificarea

### Inițierea verificării debounced

```java
                if (locationName.length() > 2 && !isChecking) {
                    locationStatus.setVisibility(View.VISIBLE);
                    locationStatus.setText(getContext().getString(R.string.checking_location));
                    locationStatus.setTextColor(getContext().getResources().getColor(android.R.color.darker_gray));
                    btnAdd.setEnabled(false);
```

**Linie cu linie:**
- Verificăm: minimum 3 caractere ȘI nu e deja o verificare în curs
- Facem status-ul vizibil
- `getString(R.string.checking_location)` - Obține string tradus din resurse (ex: "Verificare...")
- Setăm culoarea gri (indicând procesare în curs)
- Dezactivăm butonul Add până la finalizarea verificării

### Programarea verificării după delay

```java
                    // Schedule a debounced availability check
                    pendingCheck = () -> {
                        String toCheck = editLocationName.getText().toString().trim();
                        if (toCheck.length() > 2) {
                            checkLocationAvailability(toCheck);
                        }
                    };
                    debounceHandler.postDelayed(pendingCheck, CHECK_DELAY_MS);
                }
            }
        });
```

**Linie cu linie:**
- Creăm un Runnable (lambda expression) care va fi executat mai târziu
- Re-citim textul din EditText (ar fi putut să se schimbe în cei 600ms)
- Verificăm din nou lungimea (pentru siguranță)
- Apelăm `checkLocationAvailability()` pentru verificarea efectivă
- `postDelayed(pendingCheck, CHECK_DELAY_MS)` - Programează execuția după 600ms
- Dacă utilizatorul tastează ceva nou în acești 600ms, Runnable-ul este anulat și unul nou este programat

## 11. Listener pentru butonul Add

```java
        btnAdd.setOnClickListener(v -> {
            String locationName = editLocationName.getText().toString().trim();
            if (locationName.isEmpty()) return;
```

**Linie cu linie:**
- Lambda expression pentru click listener
- Re-citim textul curent (poate fi diferit față de ultima verificare)
- Dacă e gol, ieșim (guard clause)

### Cazul 1: Orașul a fost deja validat

```java
            // If we already validated this exact name and it's valid, add immediately
            if (lastValid && locationName.equals(lastCheckedName)) {
                addLocation(locationName);
                return;
            }
```

**Explicație:**
- **Optimizare**: Dacă același oraș a fost deja verificat și validat, adăugăm direct
- Nu mai facem un request HTTP inutil
- Exemplu: utilizatorul a tastat "Bucharest", a așteptat validarea (verde), apoi a apăsat Add

### Cazul 2: Verificare în curs

```java
            // If a check is currently running, mark that we want to add when it finishes
            if (isChecking) {
                pendingAdd = true;
                Toast.makeText(getContext(), getContext().getString(R.string.checking_location), Toast.LENGTH_SHORT).show();
                return;
            }
```

**Linie cu linie:**
- Dacă verificarea este deja în curs (utilizatorul a apăsat Add repede)
- Setăm `pendingAdd = true` - marcare că vrem să adăugăm la finalizare
- Afișăm Toast informativ
- Când verificarea se termină cu succes, orașul va fi adăugat automat

### Cazul 3: Verificare imediată

```java
            // Otherwise, trigger an immediate verification and add after success
            if (pendingCheck != null) {
                debounceHandler.removeCallbacks(pendingCheck);
                pendingCheck = null;
            }
            pendingAdd = true;
            checkLocationAvailability(locationName);
        });
```

**Linie cu linie:**
- Dacă există o verificare programată (dar nu începută), o anulăm
- Setăm `pendingAdd = true`
- Apelăm imediat `checkLocationAvailability()` fără delay
- Orașul va fi adăugat automat dacă verificarea are succes

## 12. Listener pentru butonul Cancel

```java
        btnCancel.setOnClickListener(v -> dismiss());
```

**Explicație:**
- Lambda simplă: la click, închide dialog-ul
- `dismiss()` - Metodă moștenită din Dialog care închide fereastra

### Starea inițială a butonului Add

```java
        btnAdd.setEnabled(false);
    }
```

**Explicație:**
- La deschiderea dialog-ului, butonul Add este dezactivat
- Se activează doar după validarea cu succes a unui oraș

## 13. Metoda checkLocationAvailability() - Verificarea online a orașului

Această metodă face request HTTP real către API pentru a verifica dacă orașul există.

### Setarea stării UI pentru verificare

```java
    private void checkLocationAvailability(String locationName) {
        isChecking = true;
        locationStatus.setVisibility(View.VISIBLE);
        locationStatus.setText(getContext().getString(R.string.checking_location));
        locationStatus.setTextColor(getContext().getResources().getColor(android.R.color.darker_gray));
        btnAdd.setEnabled(false);
```

**Linie cu linie:**
- Setăm flag-ul `isChecking = true`
- Facem status-ul vizibil cu mesaj "Verificare..." în gri
- Dezactivăm butonul Add (nu permitem adăugare în timpul verificării)

### Verificarea duplicatelor locale

```java
        // Check if location already exists
        if (locationManager.hasLocation(locationName)) {
            locationStatus.setText(getContext().getString(R.string.location_already_exists));
            locationStatus.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark));
            btnAdd.setEnabled(false);
            isChecking = false;
```

**Linie cu linie:**
- **Verificare rapidă locală**: Înainte de request HTTP, verificăm dacă orașul există deja în listă
- Dacă da, afișăm mesaj roșu "Orașul există deja"
- Butonul rămâne dezactivat
- Setăm `isChecking = false` (verificarea s-a terminat)

### Anulare pendingAdd pentru duplicate

```java
            // If user tried to Add while this check was triggered, cancel pendingAdd
            if (pendingAdd) {
                pendingAdd = false;
            }
            return;
        }
```

**Explicație:**
- Dacă utilizatorul apăsase Add și orașul e duplicat, anulăm `pendingAdd`
- Nu vrem să adăugăm orașul duplicat
- `return` - Ieșim din metodă, nu continuăm cu verificarea online

### Verificarea online în thread separat

```java
        // Check location availability online
        new Thread(() -> {
            try {
                // Try to fetch weather for this location to verify it exists
                String jsonResponse = WeatherAPI.fetchWeather(getContext(), locationName);
```

**Linie cu linie:**
- `new Thread(() -> { ... })` - Creăm și pornim un thread nou pentru operațiuni de rețea
- **NECESAR**: Operațiunile de rețea NU pot rula pe main thread în Android
- `WeatherAPI.fetchWeather()` - Face request HTTP către OpenWeatherMap
- Dacă orașul există, returnează JSON; dacă nu, aruncă IOException
- **Strategy**: Folosim API-ul de vreme ca mecanism de validare

### Succes: Orașul există

```java
                // If we get here, the location is valid
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> {
```

**Linie cu linie:**
- Dacă ajungem aici, `fetchWeather()` a reușit → orașul există
- Creăm un Handler pentru main thread (UI thread)
- **IMPORTANT**: Modificările UI trebuie făcute pe main thread
- `post()` - Pune un Runnable în coada main thread-ului

### Actualizare UI pentru succes

```java
                    locationStatus.setText(getContext().getString(R.string.location_valid));
                    locationStatus.setTextColor(getContext().getResources().getColor(android.R.color.holo_green_dark));
                    btnAdd.setEnabled(true);
                    isChecking = false;
                    lastValid = true;
                    lastCheckedName = locationName;
```

**Linie cu linie:**
- Status devine verde cu mesaj "Oraș valid"
- Activăm butonul Add (utilizatorul poate adăuga acum)
- `isChecking = false` - Verificarea s-a terminat
- Salvăm în cache: orașul este valid
- Salvăm numele verificat pentru comparație ulterioară

### Adăugare automată dacă era pendingAdd

```java
                    if (pendingAdd) {
                        pendingAdd = false;
                        addLocation(locationName);
                    }
                });
```

**Linie cu linie:**
- Dacă utilizatorul apăsase Add în timpul verificării
- Adăugăm automat orașul acum (verificarea a avut succes)
- Resetăm flag-ul
- UX excelent: utilizatorul nu trebuie să apese Add a doua oară

### Gestionarea erorilor de rețea (IOException)

```java
            } catch (IOException e) {
                // Check if it's a 404 or similar error (location not found)
                String errorMsg = e.getMessage();
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> {
```

**Linie cu linie:**
- `catch (IOException e)` - Prinde erori HTTP, timeout, etc.
- Extragem mesajul de eroare
- Creăm Handler pentru main thread (din nou, pentru update UI)

### Detectarea orașelor inexistente

```java
                    if (errorMsg != null && (errorMsg.contains("404") || errorMsg.contains("not found") || errorMsg.contains("city not found"))) {
                        locationStatus.setText(getContext().getString(R.string.location_not_found));
                        locationStatus.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark));
                    } else {
```

**Linie cu linie:**
- Analizăm mesajul de eroare pentru cod 404 sau "not found"
- Dacă e eroare de oraș inexistent, afișăm mesaj specific roșu
- Oferă feedback clar utilizatorului: "Oraș negăsit"

### Alte tipuri de erori de rețea

```java
                        locationStatus.setText(getContext().getString(R.string.error_adding_location) + ": " + errorMsg);
                        locationStatus.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark));
                    }
                    btnAdd.setEnabled(false);
                    isChecking = false;
```

**Linie cu linie:**
- Pentru alte erori (timeout, no internet), afișăm mesajul complet
- Culoare roșie pentru eroare
- Butonul rămâne dezactivat
- Marcăm verificarea ca terminată

### Gestionare pendingAdd pentru erori

```java
                    // If user had pressed Add and we attempted to pending-add, inform and keep dialog open
                    if (pendingAdd) {
                        pendingAdd = false;
                        Toast.makeText(getContext(), getContext().getString(R.string.location_not_found), Toast.LENGTH_SHORT).show();
                    }
                });
```

**Explicație:**
- Dacă utilizatorul apăsase Add și verificarea a eșuat
- Afișăm Toast cu eroarea
- Dialog-ul rămâne deschis (utilizatorul poate corecta numele)
- Resetăm `pendingAdd`

### Gestionarea altor excepții

```java
            } catch (Exception e) {
                Log.e(TAG, "Error checking location", e);
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> {
                    locationStatus.setText(getContext().getString(R.string.error_adding_location));
                    locationStatus.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark));
                    btnAdd.setEnabled(false);
                    isChecking = false;
                });
```

**Linie cu linie:**
- Prinde orice altă excepție (JSON parse error, etc.)
- Logăm eroarea pentru debugging
- Afișăm mesaj generic de eroare în roșu
- Dezactivăm butonul și marcăm verificarea ca terminată

### Pornirea thread-ului

```java
            }
        }).start();
    }
```

**Explicație:**
- `.start()` - Pornește efectiv thread-ul
- IMPORTANT: Fără `.start()`, codul nu se execută!

## 14. Metoda addLocation() - Adăugarea efectivă a orașului

```java
    private void addLocation(String locationName) {
        locationManager.addLocation(locationName);
        Toast.makeText(getContext(), getContext().getString(R.string.location_added), Toast.LENGTH_SHORT).show();
        if (listener != null) {
            listener.onLocationAdded(locationName);
        }
        dismiss();
    }
}
```

**Linie cu linie:**
- `locationManager.addLocation()` - Salvează orașul în SharedPreferences
- Afișăm Toast de confirmare "Oraș adăugat"
- Verificăm că listener-ul nu e null (defensive programming)
- `listener.onLocationAdded(locationName)` - Apelăm callback-ul
  - MainActivity primește notificarea și actualizează Spinner-ul
- `dismiss()` - Închidem dialog-ul
- **Flow complet**: Salvare → Confirmare → Notificare → Închidere

---

## Rezumat

Această clasă implementează un dialog avansat cu validare în timp real:

### **Scop principal**
- Permite adăugarea orașelor noi cu validare online
- Oferă feedback instant și previne erori (duplicate, orașe inexistente)

### **Funcționalități cheie:**
1. **TextWatcher cu debouncing** - Verificare eficientă în timp real
2. **Validare online** - Request HTTP real către API
3. **Feedback vizual** - Culori și mesaje descriptive
4. **Prevenție duplicate** - Verificare locală înainte de verificare online
5. **UX optimizat** - pendingAdd pentru adăugare automată
6. **Thread safety** - Handler pentru comunicare main thread ↔ background thread
7. **Callback pattern** - Notifică Activity-ul când orașul e adăugat

### **Pattern-uri și tehnici avansate:**
- **Debouncing** - Reduce request-urile HTTP (600ms delay)
- **Caching** - Memorează ultima verificare validă
- **State management** - isChecking, pendingAdd, lastValid
- **Thread management** - Background thread pentru rețea + Handler pentru UI
- **Callback interface** - OnLocationAddedListener pentru comunicare inversă
- **Defensive programming** - Verificări null, guard clauses

### **Flow-ul utilizatorului:**
1. Utilizatorul deschide dialog-ul (click pe + în MainActivity)
2. Începe să tasteze un oraș (ex: "Bucha...")
3. După 600ms de la ultima tastare → verificare automată
4. Status devine verde: "Oraș valid" + butonul Add se activează
5. Utilizatorul apasă Add
6. Orașul e salvat, MainActivity e notificat, dialog-ul se închide
7. Spinner-ul din MainActivity se actualizează cu noul oraș

### **Cazuri speciale gestionate:**
- **Utilizator rapid**: Apasă Add înainte de finalizarea verificării → pendingAdd
- **Oraș duplicat**: Detectat local, fără request HTTP
- **Oraș inexistent**: API returnează 404 → mesaj "Oraș negăsit"
- **Probleme rețea**: Timeout, no internet → mesaj de eroare descriptiv
- **Input gol**: Buton dezactivat, fără verificare

### **Optimizări:**
- Cache pentru evitarea verificărilor duplicate
- Debouncing pentru reducerea request-urilor
- Verificare locală înainte de verificare online
- Adăugare automată când verificarea se termină (pendingAdd)

Acest dialog este un exemplu excelent de UX design profesionist și arhitectură robustă!
