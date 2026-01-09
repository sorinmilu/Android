# MainActivity.java - Documentație Completă (01_hello_world)

## Prezentare Generală

Aceasta este **cea mai simplă aplicație Android** posibilă - un "Hello World" care afișează text pe ecran.


Demonstrează cele mai fundamentale concepte Android:
1. Cum se creează o activitate
2. Cum se creează un layout programatic
3. Cum se afișează text pe ecran
---


## Analiza Linie cu Linie

### 1. Declarația Pachetului

```java
package ro.makore.hello_world;
```

**Ce face:**
- Declară pachetul în care se află clasa
- Echivalent cu namespace-ul în alte limbaje

**De ce este important:**
- Identifică aplicația în mod unic pe dispozitiv
- `ro.makore.hello_world` este ID-ul aplicației (Application ID)
- Trebuie să fie unic pe Google Play Store

**Structura:**
- `ro` - domeniu reversed (Romania)
- `makore` - nume companie/dezvoltator
- `hello_world` - nume aplicație

---

### 2. Import pachete

```java
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
```

#### 2.1 `import android.os.Bundle;`

**Ce este Bundle:**
- Container pentru date (key-value pairs)
- Folosit pentru a salva/restaura starea activității

**În acest context:**
- Parametrul `savedInstanceState` din `onCreate()`
- `null` la prima pornire a aplicației
- Conține date dacă activitatea este recreată (rotație ecran, etc.)

#### 2.2 `import android.widget.LinearLayout;`

**Ce este LinearLayout:**
- Container pentru view-uri, pe care le aranjeaza în **linie** (vertical sau orizontal)

#### 2.3 `import android.widget.TextView;`

**TextView:**
- Widget pentru afișarea textului
- Cel mai folosit widget în Android
- **Read-only** pentru utilizator (nu poate fi editat)

#### 2.4 `import androidx.appcompat.app.AppCompatActivity;`

**AppCompatActivity:**
- Clasa de bază pentru toate activitățile moderne Android
- Parte din AndroidX (Android Extension Libraries)
- Oferă compatibilitate cu versiuni vechi de Android

**Ierarhie:**
```
Object
  └─ Context
      └─ ContextWrapper
          └─ ContextThemeWrapper
              └─ Activity
                  └─ FragmentActivity
                      └─ AppCompatActivity (← folosim asta)
                          └─ MainActivity (← clasa noastră)
```

**De ce AppCompatActivity și nu Activity:**
- ✅ Material Design pe Android vechi (4.0+)
- ✅ Toolbar modern pe toate versiunile
- ✅ Teme și stiluri consistente
- ✅ Vector drawables pe API < 21
- ✅ Tint colors pentru widgets

**Exemplu diferență:**
```java
// Activity veche (API 15)
public class MainActivity extends Activity { }  // ❌ UI diferit pe fiecare versiune

// Activity modernă
public class MainActivity extends AppCompatActivity { }  // ✅ UI consistent
```

---

### 3. Declarația Clasei

```java
public class MainActivity extends AppCompatActivity {
```

**Analiza componentelor:**

#### `public`
- **OBLIGATORIU** pentru activități (Android le instanțiază)

#### `class MainActivity`
- Numele clasei
- **Convenție:** Numele fișierului = numele clasei (`MainActivity.java`)
- **Convenție:** PascalCase (fiecare cuvânt cu majusculă)

#### `extends AppCompatActivity`
- MainActivity **este** o AppCompatActivity
- Moștenește toate metodele și proprietățile

**Ce moștenim:**
```java
// De la AppCompatActivity
- onCreate()
- onStart()
- onResume()
- onPause()
- onStop()
- onDestroy()
- setContentView()
- findViewById()
- finish()
- startActivity()
- ... și multe altele
```

Android nu poate porni o clasă simplă ca activitate. Trebuie să fie subclasă a `Activity` sau derivată.

---

### 4. Metoda onCreate()

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
```

#### `@Override`
- **Adnotare** (annotation)
- Spune compilatorului: "Suprascriu o metodă din clasa părinte"
- **Protecție:** Compilatorul verifică că metoda chiar există în părinte

#### `protected`
- Modificator de acces
- Vizibilă în clasa curentă și subclase
- Nu poate fi apelată din afara ierarhiei

#### `void`
- Tip de return
- Metoda nu returnează nimic
- Doar execută cod

#### `onCreate`
- **Cea mai importantă metodă** din Activity
- Apelată automat de Android când activitatea este creată
- Locul unde inițializăm UI-ul

**Android Activity lifecycle:**
```
┌─────────────┐
│ onCreate()  │ ← Inițializare (o singură dată)
├─────────────┤
│ onStart()   │ ← Activitatea devine vizibilă
├─────────────┤
│ onResume()  │ ← Activitatea devine interactivă (utilizatorul poate interacționa)
│             │
│   RUNNING   │ ← Aplicația rulează
│             │
├─────────────┤
│ onPause()   │ ← Activitatea pierde focus (dialog deasupra, etc.)
├─────────────┤
│ onStop()    │ ← Activitatea nu mai este vizibilă
├─────────────┤
│ onDestroy() │ ← Activitatea este distrusă
└─────────────┘
```

**onCreate() este apelat:**
1. Prima dată când aplicația pornește
2. După ce activitatea a fost distrusă și recreată (rotație ecran, low memory)
3. După ce aplicația a fost în background mult timp

#### `Bundle savedInstanceState`
- **Parametru** al metodei
- Conține starea salvată a activității (dacă există)


În această aplicație Nu folosim `savedInstanceState` pentru că Nu avem date de salvat, UI-ul este static ("Hello World" hardcodat), Nu ne interesează dacă este prima pornire sau recreare

---

### 5. Apelul super.onCreate()

```java
        super.onCreate(savedInstanceState);
```
- Apelează metoda `onCreate()` din clasa părinte (`AppCompatActivity`)
- **OBLIGATORIU** - trebuie să fie prima linie în `onCreate()`

**De ce este necesar:**

```java
// În AppCompatActivity.onCreate():
- Inițializează sistemul de teme
- Configurează window-ul activității
- Pregătește FragmentManager
- Inițializează ActionBar/Toolbar support
- Setează configurația pentru rotație ecran
- ... multe alte inițializări critice
```

**Ordinea este importantă:**
```java
// ✅ Corect
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);  // 1. Primul
    setContentView(layout);              // 2. După
}

---

### 6. Crearea Layout-ului (Programatic)

```java

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
```

#### Linia 1: Crearea Layout-ului

```java
LinearLayout layout = new LinearLayout(this);
```

**`LinearLayout`**
- Tipul variabilei
- Container care aranjează view-uri în linie

**`layout`**
- Numele variabilei
- Convenție: camelCase (prima literă mică)
- Nume descriptiv

**`new LinearLayout(this)`**
- Creează o nouă instanță de LinearLayout
- **Constructor** - inițializează obiectul

**`this`**
- Referință la obiectul curent (`MainActivity`)
- În Android, `this` este **Contextul**
- Context = informații despre mediul aplicației

```
Context oferă acces la:
- Resurse (strings, colors, layouts)
- Assets (fișiere din assets/)
- SharedPreferences (persistență date)
- Services (location, notification, etc.)
- PackageManager (info despre aplicație)
- Theme (tema aplicației)
```

**De ce avem nevoie de Context pentru View:**
```java
// View-ul trebuie să știe:
- Ce temă să folosească (culori, stiluri)
- Ce densitate de ecran e (dp → px)
- Ce limbă e setată (pentru text)
- Ce resurse sunt disponibile

// Toate astea vin din Context!
```

#### Linia 2: Setarea Orientării

```java
layout.setOrientation(LinearLayout.VERTICAL);
```
**`layout.setOrientation(...)`**
- Apelează metoda `setOrientation()` pe obiectul `layout`
- Setter method - modifică o proprietate

**`LinearLayout.VERTICAL`**
- Constantă statică din clasa `LinearLayout`
- Valoare: `1` (int)
- Alternativa: `LinearLayout.HORIZONTAL` (valoare `0`)

```java
LinearLayout layout = new LinearLayout(this);
// Fără setOrientation() → HORIZONTAL (default)
```

**În această aplicație:**
- Avem doar UN copil (TextView)
- Orientarea nu afectează vizual (un singur element)
- Este setată pentru claritate/best practice

---

### 7. Crearea TextView-ului

```java
        // Create a TextView with "Hello World"
        TextView textView = new TextView(this);
        textView.setText("Hello World");
        textView.setTextSize(24);
```

#### Linia 1: Instanțierea TextView

```java
TextView textView = new TextView(this);
```

**`TextView`**
- Tipul variabilei
- Widget pentru afișarea textului

**`textView`**
- Numele variabilei
- Convenție: camelCase
- Nume descriptiv (ar fi putut fi `helloText`, `messageView`, etc.)

**`new TextView(this)`**
- Creează un TextView nou
- `this` = Context (MainActivity)

**De ce Context și aici:**
TextView trebuie să știe:
```
- Ce font să folosească (din temă)
- Ce dimensiune de text (dp → sp → px)
- Ce culoare implicită (din temă)
- Ce limbă (pentru text direction: LTR/RTL)
```

**Starea inițială:**
```java
TextView textView = new TextView(this);
// text = ""             (gol)
// textSize = 14sp       (default din temă)
// textColor = #000000   (negru, din temă)
// visibility = VISIBLE
// background = null     (transparent)
```

#### Linia 2: Setarea Textului

```java
textView.setText("Hello World");
```

**`setText(String)`**
- Metodă din clasa TextView
- Setează textul afișat
- **Suprascrie** metoda `setText()` din `View`

**`"Hello World"`**
- String literal
- Text hardcodat în cod

#### Linia 3: Setarea Dimensiunii Textului

```java
textView.setTextSize(24);
```
**`setTextSize(float)`**
- Metodă din TextView
- Setează dimensiunea textului

**`24`**
- Valoare numerică (int, convertit automat la float)
- **Unitate:** SP (Scale-independent Pixels)
- **PROBLEMA:** Lipsa unității explicite!

**Ce sunt SP (Scalable Pixels):**
```
SP = Pixeli care se scalează cu:
1. Densitatea ecranului (ca DP)
2. Preferințele utilizatorului pentru mărimea fontului

Exemplu:
- Utilizator normal: 24sp = 24px (pe mdpi)
- Utilizator cu vedere slabă: 24sp = 36px (setare "Large text" în Settings)
```

**Diferență DP vs SP:**
```
DP (Density-independent Pixels):
- Pentru dimensiuni (width, height, margins)
- NU se scalează cu preferințele utilizatorului
- 48dp button = 48dp mereu

SP (Scale-independent Pixels):
- Pentru text DOAR
- Se scalează cu setările utilizatorului
- 14sp text = 14sp-21sp (depinde de setări)
```

**Varianta corectă (explicită):**
```java
textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);  // ✅ Explicit SP
textView.setTextSize(TypedValue.COMPLEX_UNIT_DP, 24);  // ✅ Explicit DP
textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);  // ✅ Explicit PX
```

**Unități disponibile:**
```java
COMPLEX_UNIT_PX  // Pixels     (1px = 1 pixel fizic)
COMPLEX_UNIT_DP  // DP         (density-independent)
COMPLEX_UNIT_SP  // SP         (scale-independent) ← pentru text
COMPLEX_UNIT_PT  // Points     (1pt = 1/72 inch)
COMPLEX_UNIT_IN  // Inches     (1in = 2.54cm)
COMPLEX_UNIT_MM  // Millimeters
```

**Dimensiuni text standard Android:**
```
12sp - Micro text (footnotes)
14sp - Body text (default)
16sp - Subheading
20sp - Heading
24sp - Large heading  ← folosim asta
34sp - Display text
```

**Comparație vizuală:**
```
12sp: Hello World
14sp: Hello World
16sp: Hello World
20sp: Hello World
24sp: Hello World  ← această aplicație
34sp: Hello World
```
---

### 8. Adăugarea TextView în Layout

```java
        // Add the TextView to the layout
        layout.addView(textView);
```

**`layout.addView(textView)`**
- Metodă din clasa `ViewGroup` (LinearLayout moștenește)
- Adaugă un view în layout


**Ierarhia view-urilor:**
```
LinearLayout (parent)
  └─ TextView (child)
      - text: "Hello World"
      - textSize: 24sp
```

---

### 9. Setarea Layout-ului ca Content View

```java
        // Set the layout as the content view
        setContentView(layout);
    }
}
```

**`setContentView(layout)`**
- Metodă moștenită din `AppCompatActivity`
- Setează UI-ul activității
- **Cea mai importantă linie** pentru UI

**Ce face:**
```
1. Ia layout-ul creat (LinearLayout cu TextView)
2. Îl atașează ferestrei activității
3. Îl face vizibil utilizatorului
4. Începe procesul de măsurare/desenare
```

**Fluxul complet:**
```
onCreate() {
    1. super.onCreate()                  → Inițializare sistem
    2. LinearLayout layout = new...     → Creare container
    3. TextView textView = new...       → Creare widget
    4. textView.setText("Hello World")  → Configurare text
    5. textView.setTextSize(24)         → Configurare dimensiune
    6. layout.addView(textView)         → Adăugare în layout
    7. setContentView(layout)           → Afișare pe ecran ✅
}
```

**Când este apelat setContentView():**
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // ✅ Aici - în onCreate(), după super.onCreate()
    setContentView(layout);
    
    // ❌ NU în alte metode lifecycle
}

// ❌ NU în constructor
public MainActivity() {
    setContentView(layout);  // CRASH - activitatea nu e inițializată
}

// ❌ NU în onStart() sau onResume()
@Override
protected void onStart() {
    super.onStart();
    setContentView(layout);  // Se va apela de fiecare dată când app revine
}
```

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);  // ✅ Mai simplu, mai performant
}
```

---

## Rezumat Fluxul Complet

### Secvența de Execuție

```
1. Utilizatorul deschide aplicația
   ↓
2. Android pornește procesul aplicației
   ↓
3. Android creează instanța MainActivity
   ↓
4. Android apelează onCreate(null)
   ↓
5. super.onCreate(null)
   - Inițializează window
   - Setează tema
   - Pregătește UI
   ↓
6. LinearLayout layout = new LinearLayout(this)
   - Creează container
   - În memorie, nu e vizibil încă
   ↓
7. layout.setOrientation(LinearLayout.VERTICAL)
   - Configurează orientare
   - View-uri vor fi aranjate vertical
   ↓
8. TextView textView = new TextView(this)
   - Creează widget text
   - În memorie, nu e vizibil încă
   ↓
9. textView.setText("Hello World")
   - Setează textul
   - "Hello World" salvat intern
   ↓
10. textView.setTextSize(24)
    - Setează dimensiunea
    - 24sp (scale-independent pixels)
    ↓
11. layout.addView(textView)
    - Adaugă TextView în LinearLayout
    - TextView devine copil al LinearLayout
    - Ierarhie: LinearLayout → TextView
    ↓
12. setContentView(layout)
    - Atașează layout la Window
    - MEASURE: Calculează dimensiuni
    - LAYOUT: Poziționează view-uri
    - DRAW: Desenează pe ecran
    ↓
13. UI VIZIBIL pe ecran
    ✅ "Hello World" (24sp) afișat
```

### Structura Vizuală Finală

```
┌────────────────────────────────────┐
│ MainActivity (Window)              │
│ ┌────────────────────────────────┐ │
│ │ LinearLayout (VERTICAL)        │ │
│ │ ┌────────────────────────────┐ │ │
│ │ │ TextView                   │ │ │
│ │ │   text: "Hello World"      │ │ │
│ │ │   textSize: 24sp           │ │ │
│ │ │   width: WRAP_CONTENT      │ │ │
│ │ │   height: WRAP_CONTENT     │ │ │
│ │ └────────────────────────────┘ │ │
│ └────────────────────────────────┘ │
└────────────────────────────────────┘
```

### Ierarhia Obiectelor

```
MainActivity (extends AppCompatActivity)
  └─ Window
      └─ DecorView (root view al window-ului)
          └─ ContentView
              └─ LinearLayout (layout)
                  └─ TextView (textView)
```

---

