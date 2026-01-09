# MainActivity.java - Documentație Completă (01_simple)

## Prezentare Generală

Aceasta este prima aplicație Android care folosește **XML pentru layout** în loc de crearea programatică a UI-ului.

**Caracteristici:**
- ✅ **26 linii de cod** (inclusiv import-uri și spații)
- ✅ **UI definit în XML** - `res/layout/activity_main.xml`
- ✅ **Un buton funcțional** - "Quit" pentru ieșirea din aplicație
- ✅ **findViewById()** - Conectarea între Java și XML
- ✅ **Event listener** - Gestionarea click-ului pe buton

**Diferențe față de 01_hello_world:**
- ❌ **Fără UI programatic** - XML înlocuiește LinearLayout/TextView
- ✅ **Separare UI/logică** - XML pentru design, Java pentru funcționalitate
- ✅ **Interactivitate** - Buton care răspunde la click
- ✅ **finishAffinity()** - Închidere completă aplicație

---

## Analiza

### 1. Declarația Pachetului

```java
package ro.makore.akrilki_01;
```
Acest ID trebuie să fie unic pe dispozitiv și pe Google Play Store.

---

### 2. Import-uri

```java
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import android.widget.Button;
import androidx.core.view.WindowInsetsCompat;
```

#### 2.1 `import android.os.Bundle;`

**Bundle:**
Container pentru date (perechi key-value) folosit pentru salvarea/restaurarea stării activității.

**În acest context:**
Parametrul `savedInstanceState` din `onCreate()`.

#### 2.2 `import androidx.appcompat.app.AppCompatActivity;`

**AppCompatActivity:**
Clasa de bază pentru activități moderne Android cu suport pentru:
- Material Design pe versiuni vechi Android
- Compatibilitate cross-version
- Toolbar modern
- Teme consistente

**Ierarhie:**
```
Object → Context → ContextWrapper → ContextThemeWrapper 
  → Activity → FragmentActivity → AppCompatActivity → MainActivity
```


#### 2.6 `import android.widget.Button;`

**Button:**
Widget interactiv care răspunde la click-uri utilizatorului.

**Ierarhie:**
```
View → TextView → Button
```

**Caracteristici:**
- Derivă din TextView (poate avea text)
- Răspunde la touch events
- Visual feedback la apăsare (ripple effect)
- Poate avea icoane și stiluri

**În această aplicație:**
Folosit pentru butonul "Quit" care închide aplicația.

---

### 3. Declarația Clasei

```java
public class MainActivity extends AppCompatActivity {
```

**Componentele:**

**`public`**
- Modificator de acces
- Clasa accesibilă din orice pachet
- Obligatoriu pentru activități (Android le instanțiază)

**`class MainActivity`**
- Numele clasei
- Convenție: Numele fișierului = numele clasei
- PascalCase

**`extends AppCompatActivity`**
- Moștenire
- MainActivity moștenește toate metodele AppCompatActivity

**Metode moștenite importante:**
```java
- onCreate(), onStart(), onResume(), onPause(), onStop(), onDestroy()
- setContentView(int layoutResID)
- findViewById(int id)
- finish(), finishAffinity()
- startActivity(Intent)
- runOnUiThread(Runnable)
```

---

### 4. Metoda onCreate()

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
```

#### `@Override`
- Adnotare care indică suprascrierea unei metode din clasa părinte
- Protecție la erori de scriere (compilatorul verifică existența metodei)

#### `protected void onCreate(Bundle savedInstanceState)`

**`protected`**
- Vizibilă în clasa curentă și subclase
- Nu poate fi apelată manual din exterior

**`void`**
- Nu returnează nimic

**`onCreate`**
- Prima metodă apelată când activitatea este creată
- Locul pentru inițializarea UI-ului

**`Bundle savedInstanceState`**
- Conține starea salvată (dacă există)
- `null` la prima pornire
- Conține date după recreare (rotație ecran)

#### `super.onCreate(savedInstanceState);`

**Obligatoriu:**
- Trebuie să fie prima linie din `onCreate()`
- Inițializează infrastructura activității în AppCompatActivity

**Ce face super.onCreate():**
```java
- Inițializează window-ul
- Setează tema aplicației
- Configurează FragmentManager
- Pregătește ActionBar support
- Inițializează componentele de bază
```

**Fără super.onCreate() → CRASH:**
```java
java.lang.IllegalStateException: 
You need to use a Theme.AppCompat theme (or descendant) with this activity.
```

---

### 5. Setarea Layout-ului

```java
        setContentView(R.layout.activity_main);
```

**Analiza:**

**`setContentView(...)`**
- Metodă moștenită din AppCompatActivity
- Setează UI-ul activității
- Încarcă layout-ul XML

**`R.layout.activity_main`**
- Referință către fișierul XML de layout
- `R` = clasa auto-generată care conține ID-uri ale resurselor R vine de la Resources (R.layout.activity_main → res/layout/activity_main.xml)
- `layout` = categoria de resurse (layouts)
- `activity_main` = numele fișierului (fără extensia .xml)


**Procesul de creare a resurselor din XML:**

```java
setContentView(R.layout.activity_main) {

}
```

    1. Citește activity_main.xml
    2. Parsează XML-ul
    3. Creează obiectele View din XML
       - ConstraintLayout (root)
       - TextView ("Hello World")
       - Button (quitButton)
    4. Stabilește ierarhia view-urilor
    5. Aplică constraint-uri (poziționare)
    6. Măsoară și poziționează view-urile
    7. Desenează pe ecran

---

### 6. Găsirea View-ului (findViewById)

```java
        // Find the quit button
        Button quitButton = findViewById(R.id.quitButton);
```

**Comentariu:**

Explică că vom căuta butonul definit în XML.

**`Button quitButton`**
- Declarație de variabilă
- Tip: `Button`
- Nume: `quitButton` (camelCase)

**`findViewById(R.id.quitButton)`**
- Metodă moștenită din Activity
- Caută un view după ID-ul său
- Returnează un `View` generic (trebuie făcut cast)

**`R.id.quitButton`**
- Referință către ID-ul butonului din XML
- Definit în XML cu `android:id="@+id/quitButton"`

**Mapare XML → Java:**
```xml
<!-- În activity_main.xml -->
<Button
    android:id="@+id/quitButton"  ← Definire ID
    ... />
```

```java
// În MainActivity.java
Button quitButton = findViewById(R.id.quitButton);  ← Căutare după ID
```

**Ce face findViewById():**
```java
findViewById(R.id.quitButton) {
}
```

    1. Începe de la root view (ConstraintLayout)
    2. Parcurge ierarhia view-urilor
    3. Verifică fiecare view dacă are ID-ul căutat
    4. Găsește Button cu ID = quitButton
    5. Returnează referința către acel Button

**Tipuri de return:**
```java
// findViewById() returnează View generic
View view = findViewById(R.id.quitButton);

// Cast explicit (Java clasic)
Button quitButton = (Button) findViewById(R.id.quitButton);

// Cast implicit (Java 8+, folosit aici)
Button quitButton = findViewById(R.id.quitButton);  // Compilatorul deduce tipul
```

**Performanță:**
```java
// ✅ Bun - găsește o singură dată
Button quitButton = findViewById(R.id.quitButton);
quitButton.setText("Exit");

// ❌ Rău - caută de fiecare dată
findViewById(R.id.quitButton).setText("Exit");
findViewById(R.id.quitButton).setEnabled(false);  // Caută din nou!
```

**Când returnează null:**
```java
Button button = findViewById(R.id.nonexistentButton);
// button == null dacă ID-ul nu există în layout

// Verificare
if (button != null) {
    button.setOnClickListener(...);
}
```

**Alternative moderne:**

**View Binding (recomandat):**
```java
// build.gradle
android {
    viewBinding {
        enabled = true
    }
}

// MainActivity.java
private ActivityMainBinding binding;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    
    binding.quitButton.setOnClickListener(v -> finishAffinity());  // Nu mai e nevoie de findViewById!
}
```

**Data Binding:**
```xml
<layout>
    <data>
        <variable name="handler" type="...ClickHandler" />
    </data>
    <Button android:onClick="@{handler::onQuitClick}" />
</layout>
```

**Kotlin (findViewById opțional):**
```kotlin
// Kotlin synthetics (deprecated)
import kotlinx.android.synthetic.main.activity_main.*

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    quitButton.setOnClickListener { finishAffinity() }  // Direct by ID!
}
```
---

### 7. Event Listener (Click Handler)

```java
        // Add the quit button action
        quitButton.setOnClickListener(v -> finishAffinity());
```

**`quitButton.setOnClickListener(...)`**
- Metodă din clasa View (moștenită de Button)
- Înregistrează un listener pentru evenimentul de click

**Lambda Expression: `v -> finishAffinity()`**

**Sintaxa lambda (Java 8+):**
```java
// Lambda (folosit aici)
quitButton.setOnClickListener(v -> finishAffinity());

// Echivalent cu anonymous inner class
quitButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        finishAffinity();
    }
});


**Ce este un Listener:**

Listener = Callback care ascultă evenimente

Event: Utilizatorul apasă butonul
    ↓
Android detectează touch event
    ↓
Android apelează onClick(View v)
    ↓
Codul nostru se execută: finishAffinity()
```


---

### 8. Metoda finishAffinity()

**finishAffinity():**
- Metodă moștenită din Activity
- Închide activitatea curentă și toate activitățile din același task
- Elimină aplicația din lista de aplicații recente

## Rezumat Fluxul Complet

### Secvența de Execuție

```
1. Utilizator deschide aplicația
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
   - Pregătește FragmentManager
   ↓
6. setContentView(R.layout.activity_main)
   - Citește activity_main.xml
   - Parsează XML
   - Creează ConstraintLayout
   - Creează TextView cu "Hello World"
   - Creează Button cu id=quitButton
   - Aplică constraint-uri (poziționare)
   - MEASURE: Calculează dimensiuni
   - LAYOUT: Poziționează view-uri
   - DRAW: Desenează pe ecran
   ↓
7. findViewById(R.id.quitButton)
   - Parcurge ierarhia view-urilor
   - Găsește Button cu ID quitButton
   - Returnează referința
   ↓
8. quitButton.setOnClickListener(v -> finishAffinity())
   - Înregistrează listener pentru click
   - Când utilizatorul apasă butonul:
     → onClick() este apelat
     → finishAffinity() se execută
     → Aplicația se închide
   ↓
9. UI VIZIBIL
   ✅ TextView: "HELLO WORLD" (42sp, majuscule)
   ✅ Button: "Quit" (87dp înălțime)
   ✅ Layout: ConstraintLayout (responsive)
```

### Ierarhia View-urilor

```
MainActivity
  └─ Window
      └─ DecorView
          └─ ContentView
              └─ ConstraintLayout (android:id="@+id/main")
                  ├─ TextView
                  │   - text: "Hello World"
                  │   - textSize: 42sp
                  │   - textAllCaps: true
                  │   - constraints: centered, above button
                  │
                  └─ Button (android:id="@+id/quitButton")
                      - text: "Quit"
                      - height: 87dp
                      - constraints: bottom of screen
                      - onClick: finishAffinity()
```

