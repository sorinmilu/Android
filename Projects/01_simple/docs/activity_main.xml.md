# activity_main.xml - Documentație Completă (01_simple)

## Prezentare Generală

Acesta este fișierul de layout XML care definește interfața utilizatorului pentru MainActivity.

**Caracteristici:**
- ✅ **30 linii XML**
- ✅ **ConstraintLayout** - Layout modern și flexibil
- ✅ **2 view-uri** - TextView și Button
- ✅ **Responsive design** - Se adaptează la diferite ecrane
- ✅ **Constraint-based positioning** - Poziționare relativă

**Conținut:**
1. ConstraintLayout (root)
2. TextView - "HELLO WORLD" (42sp, uppercase)
3. Button - "Quit" (87dp înălțime)

---

## Codul Complet

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Hello World"
        android:textSize="42sp"
        android:textAllCaps="true"
        app:layout_constraintBottom_toTopOf="@+id/quitButton"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/quitButton"
        android:layout_width="match_parent"
        android:layout_height="87dp"
        android:layout_marginBottom="20dp"
        android:text="Quit"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

---

## Analiza Linie cu Linie

### 1. Declarația XML

```xml
<?xml version="1.0" encoding="utf-8"?>
```

**Analiza:**

**`<?xml version="1.0" encoding="utf-8"?>`**
- Declarație XML standard
- Obligatorie în orice fișier XML

**`version="1.0"`**
- Versiunea specificației XML folosită
- Întotdeauna 1.0 în Android

**`encoding="utf-8"`**
- Encoding-ul caracterelor
- UTF-8 suportă caractere internaționale (română: ă, â, î, ș, ț)

---

### 2. Root Element: ConstraintLayout

```xml
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
```

#### Tag-ul Principal

**`<androidx.constraintlayout.widget.ConstraintLayout`**
- ViewGroup (container) pentru view-uri copil
- Parte din AndroidX (Android Extension Libraries)
- Fully qualified name: `androidx.constraintlayout.widget.ConstraintLayout`

**Ierarhie:**
```
Object
  └─ View
      └─ ViewGroup
          └─ ConstraintLayout
```

**Ce este ConstraintLayout:**
Layout manager modern care permite:
- Poziționare relativă complexă
- Flat view hierarchy (performanță mai bună)
- Responsive design
- Design vizual în Layout Editor

**Alternative:**
```xml
<!-- LinearLayout - aranjare liniară -->
<LinearLayout android:orientation="vertical">

<!-- RelativeLayout - poziționare relativă (deprecated) -->
<RelativeLayout>

<!-- FrameLayout - overlay views -->
<FrameLayout>

<!-- ConstraintLayout - modern, recomandat -->
<androidx.constraintlayout.widget.ConstraintLayout>
```

#### Namespace-uri XML

**`xmlns:android="http://schemas.android.com/apk/res/android"`**

**xmlns** = XML Namespace

**Prefix: `android:`**
- Atribute standard Android SDK
- Built-in în platform

**Exemple de atribute android:**
```xml
android:id
android:layout_width
android:layout_height
android:text
android:textSize
android:background
```

**`xmlns:app="http://schemas.android.com/apk/res-auto"`**

**Prefix: `app:`**
- Atribute din biblioteci custom (AndroidX, Material, etc.)
- "res-auto" = rezolvă automat pachetul bibliotecii

**Exemple de atribute app:**
```xml
app:layout_constraintTop_toTopOf
app:layout_constraintBottom_toBottomOf
app:layout_constraintStart_toStartOf
app:layout_constraintEnd_toEndOf
```

**De ce `app:` și nu `android:`:**
```
android: - Atribute din Android SDK (API Level specific)
app:     - Atribute din biblioteci externe (backward compatible)

ConstraintLayout e bibliotecă AndroidX → folosește app:
```

**`xmlns:tools="http://schemas.android.com/tools"`**

**Prefix: `tools:`**
- Atribute doar pentru design-time (în Android Studio)
- Nu afectează aplicația runtime
- Folosite pentru preview, warnings, optimizări IDE

**Exemple de atribute tools:**
```xml
tools:context=".MainActivity"     ← Asociază cu Activity
tools:text="Preview Text"         ← Text doar în preview
tools:visibility="visible"        ← Vizibilitate în preview
tools:layout_editor_absoluteX     ← Poziție absolută (doar editor)
tools:ignore="HardcodedText"      ← Ignoră warning
```

**Diferență runtime vs design-time:**
```xml
<TextView
    android:text="Actual Text"      ← Apare în aplicație
    tools:text="Preview Text" />    ← Apare doar în Android Studio preview
```

#### Atribute ConstraintLayout

**`android:id="@+id/main"`**

**Ce este ID-ul:**
- Identificator unic pentru view
- Folosit pentru referințiere în Java/Kotlin și în alte view-uri

**Sintaxa:**
```
@+id/name
│ │  └── Numele ID-ului (camelCase)
│ └────── Tipul resursei (id)
└──────── @ = referință la resursă
          + = creează ID nou dacă nu există
```

**Fără `+`:**
```xml
<!-- Creează ID nou -->
<View android:id="@+id/myView" />

<!-- Referențiază ID existent -->
<View app:layout_constraintTop_toTopOf="@id/myView" />
```

**Utilizare în Java:**
```java
ConstraintLayout mainLayout = findViewById(R.id.main);
```

**În acest caz:**
ID-ul `main` identifică ConstraintLayout-ul root, dar nu este folosit în cod deoarece este setat automat prin `setContentView()`.

**`android:layout_width="match_parent"`**

**Ce face:**
- Setează lățimea view-ului
- `match_parent` = ocupă toată lățimea părintelui

**Valori posibile:**
```xml
android:layout_width="match_parent"  ← Toată lățimea părintelui
android:layout_width="wrap_content"  ← Cât conținutul (text, imagine)
android:layout_width="0dp"           ← În ConstraintLayout: match constraints
android:layout_width="100dp"         ← Dimensiune fixă (density-independent pixels)
```

**Vizualizare:**
```
Parent (ecran):
┌──────────────────────────────┐
│ match_parent (100% lățime)   │
│ ┌──────────────────────────┐ │
│ └──────────────────────────┘ │
└──────────────────────────────┘

┌──────────────────────────────┐
│ wrap_content (cât textul)    │
│ ┌───────┐                    │
│ └───────┘                    │
└──────────────────────────────┘

┌──────────────────────────────┐
│ 100dp (dimensiune fixă)      │
│ ┌───────┐                    │
│ └───────┘                    │
└──────────────────────────────┘
```

**`android:layout_height="match_parent"`**

**Ce face:**
- Setează înălțimea view-ului
- `match_parent` = ocupă toată înălțimea părintelui (ecranul)

**În acest context:**
```
ConstraintLayout ocupă tot ecranul:
- Lățime: match_parent (toată lățimea ecranului)
- Înălțime: match_parent (toată înălțimea ecranului)
```

**`tools:context=".MainActivity"`**

**Ce face:**
- Indică Android Studio că acest layout e folosit de MainActivity
- Doar pentru design-time (nu afectează aplicația)

**Beneficii:**
```
1. Acces la tema activității în preview
2. Click dreapta → "Go to Java/Kotlin class"
3. Auto-complete pentru databinding
4. Validări specifice activității
```

**Sintaxa:**
```
.MainActivity
└── Relative la package-ul aplicației (ro.makore.akrilki_01.MainActivity)

Echivalent cu:
tools:context="ro.makore.akrilki_01.MainActivity"
```

---

### 3. TextView Element

```xml
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Hello World"
        android:textSize="42sp"
        android:textAllCaps="true"
        app:layout_constraintBottom_toTopOf="@+id/quitButton"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
```

#### Tag-ul TextView

**`<TextView`**
- Widget pentru afișarea textului
- Nu poate fi editat de utilizator (read-only)

**Ierarhie:**
```
View → TextView
```

**Pentru text editabil:**
```xml
<EditText />  ← Permite input utilizator
```

#### Atribute Dimensiuni

**`android:layout_width="match_parent"`**
- Lățime = toată lățimea părintelui (ConstraintLayout)
- Text se va întinde pe toată lățimea ecranului

**`android:layout_height="wrap_content"`**
- Înălțime = cât e nevoie pentru text
- Se adaptează automat la dimensiunea textului

**Comportament:**
```
Text mic (14sp):
┌──────────────────┐
│  Hello World     │ ← wrap_content (înălțime mică)
└──────────────────┘

Text mare (42sp):
┌──────────────────┐
│                  │
│  HELLO WORLD     │ ← wrap_content (înălțime mai mare)
│                  │
└──────────────────┘
```

#### Atribute Text

**`android:text="Hello World"`**

**Ce face:**
- Setează textul afișat
- String hardcodat în XML

**⚠️ Anti-pattern:**
Textul ar trebui în `strings.xml` pentru:
- Internaționalização (traduceri)
- Reutilizare
- Mentenabilitate

**Best practice:**
```xml
<!-- res/values/strings.xml -->
<resources>
    <string name="hello_world">Hello World</string>
</resources>

<!-- res/layout/activity_main.xml -->
<TextView
    android:text="@string/hello_world" />
```

**Pentru multilingv:**
```
res/values/strings.xml           (English - default)
res/values-ro/strings.xml        (Romanian)
res/values-es/strings.xml        (Spanish)
```

**`android:textSize="42sp"`**

**Ce face:**
- Setează dimensiunea textului
- `42sp` = 42 scale-independent pixels

**Unități:**
```
sp (scale-independent pixels) - pentru TEXT
- Se scalează cu densitatea ecranului
- Se scalează cu preferințele utilizatorului
- Recomandat pentru textSize

dp (density-independent pixels) - pentru DIMENSIUNI
- Se scalează cu densitatea ecranului
- NU se scalează cu preferințele utilizatorului
- Recomandat pentru width, height, margins

px (pixels) - EVITĂ
- Pixels fizici
- NU se scalează
- Arată diferit pe fiecare dispozitiv
```

**Dimensiuni text standard Android:**
```
12sp - Micro
14sp - Body (default)
16sp - Subheading
20sp - Heading
24sp - Large heading
34sp - Display
42sp - Extra large ← folosit aici
```

**Scalare cu setările utilizatorului:**
```
User normal:       42sp = 42px (pe mdpi)
User "Large text": 42sp = 63px (150% scaling)
User "Huge text":  42sp = 84px (200% scaling)
```

**`android:textAllCaps="true"`**

**Ce face:**
- Convertește textul în MAJUSCULE
- `"Hello World"` → `"HELLO WORLD"`

**Valori:**
```xml
android:textAllCaps="true"   ← MAJUSCULE
android:textAllCaps="false"  ← Text original
```

**Rezultat vizual:**
```
textAllCaps="false":  Hello World
textAllCaps="true":   HELLO WORLD
```

**Implementare:**
```
TextView aplică:
text.toUpperCase(locale)
```

**Alternative:**
```xml
<!-- În strings.xml -->
<string name="hello">HELLO WORLD</string>  ← Deja majuscule

<!-- Sau în Java -->
textView.setText(text.toUpperCase());
```

#### Constraint Attributes

**Ce sunt Constraints:**
Relații de poziționare între view-uri în ConstraintLayout.

**Conceptul:**
```
Fiecare view trebuie să aibă:
- Constraint orizonta (left/right SAU start/end)
- Constraint vertical (top/bottom)

Minim 2 constraints (unul orizontal + unul vertical)
```

**`app:layout_constraintBottom_toTopOf="@+id/quitButton"`**

**Analiza:**
- `layout_constraint` = constraint pentru poziționare
- `Bottom` = marginea de jos a TextView-ului
- `toTopOf` = se aliniază la marginea de sus
- `@+id/quitButton` = a butonului quitButton

**Vizualizare:**
```
┌────────────────────┐
│   HELLO WORLD      │ ← TextView
└────────────────────┘
        ↓ (bottom)
        │
        ↓ (toTopOf)
┌────────────────────┐
│       Quit         │ ← quitButton (top)
└────────────────────┘
```

**Relație:**
```
TextView.bottom = quitButton.top
```

**`app:layout_constraintEnd_toEndOf="parent"`**

**Analiza:**
- `End` = marginea dreaptă (în LTR) / stângă (în RTL)
- `toEndOf` = se aliniază la marginea dreaptă
- `parent` = părintele (ConstraintLayout)

**Vizualizare:**
```
┌─────────────────────────────┐ ← parent
│   ┌──────────────────────┐  │
│   │   HELLO WORLD        │  │ ← TextView
│   └──────────────────────┘  │
│                            │←┘ (end aligns with parent end)
└─────────────────────────────┘
```

**Start vs Left:**
```
LTR (Left-to-Right) languages (English, Romanian):
start = left
end = right

RTL (Right-to-Left) languages (Arabic, Hebrew):
start = right
end = left

Recomandat: folosește start/end (suport multilingv)
```

**`app:layout_constraintStart_toStartOf="parent"`**

**Analiza:**
- `Start` = marginea stângă (în LTR)
- `toStartOf` = se aliniază la marginea stângă
- `parent` = ConstraintLayout

**Vizualizare:**
```
┌─────────────────────────────┐ ← parent
│  ┌──────────────────────┐   │
│  │   HELLO WORLD        │   │ ← TextView
│  └──────────────────────┘   │
│ │                            │
└─┘────────────────────────────┘
  (start aligns with parent start)
```

**Combinat cu End:**
```
Start + End constraints → TextView se întinde pe toată lățimea
(confirmă layout_width="match_parent")
```

**`app:layout_constraintTop_toTopOf="parent"`**

**Analiza:**
- `Top` = marginea de sus a TextView-ului
- `toTopOf` = se aliniază la marginea de sus
- `parent` = ConstraintLayout

**Vizualizare:**
```
┌─────────────────────────────┐ ← parent (top)
├─────────────────────────────┤ ← TextView (top) aligned here
│   HELLO WORLD               │
│                             │
└─────────────────────────────┘
```

**Toate Constraints Împreună:**
```
TextView are 4 constraints:
1. Top    → parent.top          (sus: marginea ecranului)
2. Bottom → quitButton.top      (jos: deasupra butonului)
3. Start  → parent.start        (stânga: marginea ecranului)
4. End    → parent.end          (dreapta: marginea ecranului)

Rezultat: TextView centrat vertical între top ecran și buton,
          întins orizontal pe toată lățimea
```

**Poziționare finală:**
```
┌──────────────────────────────────┐ ← parent.top
│                                  │
│        HELLO WORLD               │ ← TextView (centrat vertical)
│                                  │
├──────────────────────────────────┤ ← quitButton.top
│           Quit                   │
└──────────────────────────────────┘ ← parent.bottom
```

---

### 4. Button Element

```xml
    <Button
        android:id="@+id/quitButton"
        android:layout_width="match_parent"
        android:layout_height="87dp"
        android:layout_marginBottom="20dp"
        android:text="Quit"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />
```

#### Tag-ul Button

**`<Button`**
- Widget interactiv pentru acțiuni utilizator
- Răspunde la click/touch events

**Ierarhie:**
```
View → TextView → Button
```

**De ce moștenește TextView:**
Button poate avea text (și TextView are deja implementare pentru text display).

#### Atribute ID și Dimensiuni

**`android:id="@+id/quitButton"`**

**Ce face:**
- Creează ID unic pentru buton
- ID folosit în:
  - Java: `findViewById(R.id.quitButton)`
  - XML: `@id/quitButton` (în constraints)

**Naming convention:**
```
camelCase pentru ID-uri:
quitButton, saveButton, loginButton, cancelButton
```

**`android:layout_width="match_parent"`**
- Lățime = toată lățimea ecranului
- Buton se întinde pe toată lățimea

**`android:layout_height="87dp"`**

**Ce face:**
- Înălțime fixă de 87 density-independent pixels
- NU se adaptează la conținut (nu e wrap_content)

**De ce 87dp:**
```
Material Design recommendations:
- Minimum touch target: 48dp
- Standard button height: 36dp - 56dp
- Large button: 60dp - 100dp

87dp = buton foarte mare (probabil pentru vizibilitate)
```

**Comparație:**
```
Standard button (48dp):
┌────────────┐
│    Quit    │
└────────────┘

Acest buton (87dp):
┌────────────┐
│            │
│    Quit    │
│            │
└────────────┘
```

#### Atribute Margin

**`android:layout_marginBottom="20dp"`**

**Ce face:**
- Adaugă spațiu de 20dp sub buton
- Distanță între buton și marginea de jos a ecranului

**Margin vs Padding:**
```
Margin:
┌─────────────────┐
│     Button      │
└─────────────────┘
       ↓ (20dp margin)
───────────────────── (screen bottom)

Padding:
┌─────────────────┐
│  ┌───────────┐  │ ← 20dp padding
│  │   Quit    │  │
│  └───────────┘  │
└─────────────────┘
```

**Tipuri de margin:**
```xml
android:layout_margin="16dp"           ← Toate marginile
android:layout_marginTop="8dp"         ← Sus
android:layout_marginBottom="20dp"     ← Jos (folosit aici)
android:layout_marginStart="12dp"      ← Stânga (LTR)
android:layout_marginEnd="12dp"        ← Dreapta (LTR)
android:layout_marginLeft="12dp"       ← Stânga (deprecated)
android:layout_marginRight="12dp"      ← Dreapta (deprecated)
```

**Vizualizare cu margin:**
```
┌──────────────────────────────────┐
│        HELLO WORLD               │
│                                  │
├──────────────────────────────────┤
│           Quit                   │ ← Button
└──────────────────────────────────┘
              ↓ 20dp margin
═════════════════════════════════════ (ecran bottom)
```

#### Atribute Text

**`android:text="Quit"`**

**Ce face:**
- Setează textul butonului
- Apare ca "Quit" pe buton

**⚠️ Hardcoded text warning:**
Android Studio va afișa warning: "Hardcoded text "Quit", should use @string resource"

**Best practice:**
```xml
<!-- res/values/strings.xml -->
<string name="quit">Quit</string>

<!-- activity_main.xml -->
<Button
    android:text="@string/quit" />
```

#### Constraint Attributes

**`app:layout_constraintBottom_toBottomOf="parent"`**

**Analiza:**
- `Bottom` = marginea de jos a butonului
- `toBottomOf` = se aliniază la marginea de jos
- `parent` = ConstraintLayout (ecran)

**Vizualizare:**
```
┌──────────────────────────────────┐
│        HELLO WORLD               │
│                                  │
│                                  │
│           Quit                   │ ← Button
└──────────────────────────────────┘
                                   │
                    (button.bottom = parent.bottom - 20dp margin)
```

**Cu margin:**
```
Button.bottom = parent.bottom - marginBottom(20dp)
```

**`app:layout_constraintEnd_toEndOf="parent"`**

**Analiza:**
- `End` = marginea dreaptă (LTR)
- `toEndOf` = se aliniază la marginea dreaptă
- `parent` = ConstraintLayout

**De ce lipsește Start constraint:**
```
Button are:
- layout_width="match_parent"  ← Se întinde automat
- End constraint               ← Ancorează dreapta

Rezultat: Butonul se întinde pe toată lățimea
(Start constraint implicit la parent.start)
```

**Constraints Button:**
```
1. Bottom → parent.bottom (jos ecran - 20dp)
2. End    → parent.end    (dreapta ecran)
3. (implicit Start → parent.start din match_parent)

Rezultat: Buton jos, toată lățimea, 20dp margin bottom
```

---

### 5. Închiderea Tag-urilor

```xml
    </Button>

</androidx.constraintlayout.widget.ConstraintLayout>
```

**`</Button>`**
- Închide tag-ul Button
- Button nu are copii (self-closing ar fi fost posibil: `<Button ... />`)

**`</androidx.constraintlayout.widget.ConstraintLayout>`**
- Închide tag-ul root
- ConstraintLayout conține 2 copii (TextView și Button)

**Self-closing vs paired tags:**
```xml
<!-- Self-closing (când nu are copii) -->
<TextView ... />

<!-- Paired tags (când are copii) -->
<LinearLayout ...>
    <TextView ... />
    <Button ... />
</LinearLayout>

<!-- Ambele variante valide pentru views fără copii -->
<Button ... />           ← Self-closing
<Button ...></Button>    ← Paired (redundant dar valid)
```

---

## Structura Ierarhică Vizuală

### View Hierarchy

```
ConstraintLayout (id: main)
├─ TextView (no ID)
│  └─ text: "Hello World" → "HELLO WORLD"
│     size: 42sp
│     allCaps: true
│     constraints:
│       - top → parent.top
│       - bottom → quitButton.top
│       - start → parent.start
│       - end → parent.end
│
└─ Button (id: quitButton)
   └─ text: "Quit"
      height: 87dp
      marginBottom: 20dp
      constraints:
        - bottom → parent.bottom
        - end → parent.end
```

### Layout pe Ecran

```
┌────────────────────────────────────┐ 0dp (top)
│                                    │
│                                    │
│         HELLO WORLD                │ ← TextView (centrat)
│                                    │
│                                    │
├────────────────────────────────────┤
│          Quit                      │ ← Button (87dp height)
└────────────────────────────────────┘
              20dp margin
════════════════════════════════════════ screen bottom
```

### Constraint Graph

```
        parent.top
            ↓
    ┌───────────────┐
    │  TextView     │
    └───────────────┘
            ↓
    ┌───────────────┐
    │    Button     │
    └───────────────┘
            ↓
        parent.bottom
         (- 20dp)
```

---

## Concepte Cheie ConstraintLayout

### 1. Constraints Obligatorii

Fiecare view trebuie să aibă:
- **Minim 1 constraint orizontal** (start/end SAU left/right)
- **Minim 1 constraint vertical** (top/bottom)

**Exemplu minim valid:**
```xml
<TextView
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

### 2. Tipuri de Constraints

**Relative la părinte:**
```xml
app:layout_constraintTop_toTopOf="parent"
app:layout_constraintBottom_toBottomOf="parent"
app:layout_constraintStart_toStartOf="parent"
app:layout_constraintEnd_toEndOf="parent"
```

**Relative la alt view:**
```xml
app:layout_constraintTop_toBottomOf="@id/otherView"
app:layout_constraintStart_toEndOf="@id/otherView"
```

**Aliniere:**
```xml
app:layout_constraintBaseline_toBaselineOf="@id/otherView"  ← Text baseline
```

### 3. Poziționare Centrală

**Centrat orizontal:**
```xml
app:layout_constraintStart_toStartOf="parent"
app:layout_constraintEnd_toEndOf="parent"
```

**Centrat vertical:**
```xml
app:layout_constraintTop_toTopOf="parent"
app:layout_constraintBottom_toBottomOf="parent"
```

**Ambele (centrat complet):**
```xml
app:layout_constraintTop_toTopOf="parent"
app:layout_constraintBottom_toBottomOf="parent"
app:layout_constraintStart_toStartOf="parent"
app:layout_constraintEnd_toEndOf="parent"
```

### 4. Bias (Ajustare Poziție)

**Când ai 2 constraints opuse:**
```xml
<!-- Centrat (50/50) -->
app:layout_constraintHorizontal_bias="0.5"  ← default

<!-- Mai spre stânga (30/70) -->
app:layout_constraintHorizontal_bias="0.3"

<!-- Mai spre dreapta (70/30) -->
app:layout_constraintHorizontal_bias="0.7"
```

**Vizualizare bias:**
```
bias=0.0    bias=0.5    bias=1.0
┌─┐         ┌────┐      ┌──────┐
│█│         │ █  │      │     █│
└─┘         └────┘      └──────┘
```

### 5. Match Constraints (0dp)

**În ConstraintLayout, `0dp` = match constraints:**
```xml
<TextView
    android:layout_width="0dp"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent" />
```

**Comportament:**
```
0dp + 2 constraints → Se întinde între cele 2 constraints
Match parent → Se întinde până la margini
```

**Diferența:**
```xml
<!-- Clasic (în LinearLayout, RelativeLayout) -->
android:layout_width="match_parent"  ← Întinde la părinte

<!-- ConstraintLayout modern -->
android:layout_width="0dp"           ← Întinde între constraints
app:layout_constraintStart_toStartOf="parent"
app:layout_constraintEnd_toEndOf="parent"
```

### 6. Chains (Lanțuri)

**Ce sunt:**
Grupuri de view-uri legate bidirectional.

```xml
<!-- View A -->
<View
    app:layout_constraintEnd_toStartOf="@id/viewB" />

<!-- View B -->
<View
    app:layout_constraintStart_toEndOf="@id/viewA"
    app:layout_constraintEnd_toStartOf="@id/viewC" />

<!-- View C -->
<View
    app:layout_constraintStart_toEndOf="@id/viewB" />
```

**Tipuri chain:**
```
spread:       [A]  [B]  [C]     ← Distribuite uniform
spread_inside: [A] [B] [C]      ← Spațiu doar între
packed:       [A][B][C]         ← Lipite împreună
```

### 7. Guidelines (Linii Ghid)

**Linii invizibile pentru aliniere:**
```xml
<androidx.constraintlayout.widget.Guideline
    android:id="@+id/guideline"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    app:layout_constraintGuide_percent="0.5" />  ← 50% din lățime
```

**Utilizare:**
```xml
<TextView
    app:layout_constraintStart_toStartOf="@id/guideline" />
```

### 8. Barriers (Bariere)

**Linie dinamică bazată pe cel mai mare/mic view:**
```xml
<androidx.constraintlayout.widget.Barrier
    android:id="@+id/barrier"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:barrierDirection="end"
    app:constraint_referenced_ids="view1,view2,view3" />
```

**Comportament:**
```
View1: [Text]
View2: [Longer Text]  ← Cel mai lung
View3: [Hi]
         │
         └─ Barrier → Aliniază la cel mai lung
```

---

## Avantaje ConstraintLayout

### 1. Flat Hierarchy
```xml
<!-- RelativeLayout (nested) -->
<RelativeLayout>
    <LinearLayout>
        <LinearLayout>
            <TextView />
            <Button />
        </LinearLayout>
    </LinearLayout>
</RelativeLayout>

<!-- ConstraintLayout (flat) -->
<ConstraintLayout>
    <TextView />
    <Button />
</ConstraintLayout>
```

**Performanță:**
```
Nested layouts → Multiple measure/layout passes → Slow
Flat layout → Single pass → Fast
```

### 2. Responsive Design

```xml
<!-- Se adaptează automat la: -->
- Diferite dimensiuni ecran (phone, tablet)
- Orientare (portrait, landscape)
- Split screen / multi-window
- Foldable devices
```

### 3. Visual Editor

Android Studio Layout Editor:
- Drag & drop views
- Visual constraint creation
- Real-time preview
- Device preview (multiple sizes)

### 4. Reducere Cod

```xml
<!-- LinearLayout (weighted distribution) -->
<LinearLayout android:orientation="horizontal" android:weightSum="3">
    <View android:layout_weight="1" />
    <View android:layout_weight="1" />
    <View android:layout_weight="1" />
</LinearLayout>

<!-- ConstraintLayout (chain) -->
<ConstraintLayout>
    <View app:layout_constraintHorizontal_chainStyle="spread" />
    <View />
    <View />
</ConstraintLayout>
```

---

## Comparație cu Alte Layouts

### LinearLayout

**Pros:**
- Simplu de înțeles
- Bun pentru liste simple
- Vertical/horizontal straightforward

**Cons:**
- Necesită nesting pentru layouts complexe
- Weight calculations slow
- Nu e responsive

### RelativeLayout

**Pros:**
- Poziționare relativă
- Mai puțin nesting decât LinearLayout

**Cons:**
- Deprecated (înlocuit de ConstraintLayout)
- Mai lent la measure/layout
- Mai puțin flexibil

### FrameLayout

**Pros:**
- Foarte simplu
- Bun pentru overlay
- Performant

**Cons:**
- Limitări severe de poziționare
- Doar stacking

### ConstraintLayout (Recomandat)

**Pros:**
- Flat hierarchy (performanță)
- Flexibil (orice poziționare)
- Responsive
- Visual editor support
- Modern

**Cons:**
- Curba de învățare mai mare
- XML verbose pentru layouts simple
