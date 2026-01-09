# activity_main.xml - Documentație Linie cu Linie

## Prezentare

Acest fișier XML definește layout-ul interfaței utilizatorului pentru aplicația de glume. Layout-ul conține:
- **Un TextView** pentru afișarea glumei
- **Un buton Refresh** pentru descărcarea unei noi glume
- **Un buton Quit** pentru închiderea aplicației

Layout-ul folosește **ConstraintLayout** care permite pozitionarea elementelor prin relații (constraints) între ele, oferind flexibilitate și performanță superioară.

## Cod Complet

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <!-- TextView to display the joke -->
    <TextView
        android:id="@+id/jokeTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Joke will appear here"
        android:textSize="24sp"
        android:textAlignment="center"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@+id/refreshButton"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:padding="16dp"
        android:gravity="center"
        android:layout_marginTop="16dp"
        android:layout_marginBottom="16dp"/>

    <!-- Refresh Joke Button -->

    <!-- Quit Button -->

    <Button
        android:id="@+id/refreshButton"
        android:layout_width="407dp"
        android:layout_height="97dp"
        android:text="Refresh Joke"
        android:textSize="18sp"
        app:layout_constraintBottom_toTopOf="@+id/quitButton"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

    <Button
        android:id="@+id/quitButton"
        android:layout_width="404dp"
        android:layout_height="98dp"
        android:text="Quit"
        android:textSize="18sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.0"
        app:layout_constraintStart_toStartOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

## Analiza Linie cu Linie

### Declarația XML

```xml
<?xml version="1.0" encoding="utf-8"?>
```

**Explicație:** Declarația standard XML care specifică:
- `version="1.0"` = versiunea XML folosită
- `encoding="utf-8"` = codificarea caracterelor (UTF-8 suportă toate limbile)

Această linie trebuie să fie **prima** din orice fișier XML.

---

### Element Root ConstraintLayout - Deschidere Tag

```xml
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
```

**Explicație:** Deschide elementul root (rădăcină) al layout-ului - un `ConstraintLayout`:
- `androidx.constraintlayout.widget.ConstraintLayout` = numele complet calificat al clasei
- `xmlns:android="http://schemas.android.com/apk/res/android"` = declară namespace-ul Android pentru atribute standard

**ConstraintLayout** este un layout manager modern care poziționează elementele prin relații (constraints) între ele.

---

### Namespace app

```xml
    xmlns:app="http://schemas.android.com/apk/res-auto"
```

**Explicație:** Declară namespace-ul `app` folosit pentru atribute personalizate și din biblioteci externe:
- `app:layout_constraint*` = atribute de poziționare ConstraintLayout
- Prefix `app:` în loc de `android:`

---

### Namespace tools

```xml
    xmlns:tools="http://schemas.android.com/tools"
```

**Explicație:** Declară namespace-ul `tools` folosit pentru atribute vizibile **doar în Android Studio** (nu în aplicația rulată):
- Design-time helpers
- Preview configurations
- Lint hints

---

### ID ConstraintLayout

```xml
    android:id="@+id/main"
```

**Explicație:** Atribuie un ID layout-ului root:
- `@+id/main` = creează un nou ID numit `main`
- Permite referirea la acest layout din cod Java

**Sintaxă:**
- `@+id/` = creează ID nou
- `@id/` = referă ID existent (folosit în constraints)

---

### Dimensiuni Layout Root

```xml
    android:layout_width="match_parent"
    android:layout_height="match_parent"
```

**Explicație:** Setează dimensiunile layout-ului root:
- `match_parent` = ocupă toată lățimea părintelui (ecranul)
- `match_parent` = ocupă toată înălțimea părintelui (ecranul)

Layout-ul va umple întregul ecran disponibil.

**Valori posibile:**
- `match_parent` = dimensiunea părintelui
- `wrap_content` = dimensiunea conținutului
- Valoare fixă: `100dp`, `200dp`, etc.

---

### Context Tools

```xml
    tools:context=".MainActivity">
```

**Explicație:** Specifică clasa de activitate asociată cu acest layout (doar pentru Android Studio):
- `.MainActivity` = numele clasei (punct prefix = același pachet)
- Activează auto-complete pentru resurse specifice activității
- **Nu afectează** aplicația rulată

---

### Comentariu TextView

```xml
    <!-- TextView to display the joke -->
```

**Explicație:** Comentariu XML care descrie scopul următorului element. Comentariile în XML folosesc sintaxa `<!-- comentariu -->`.

---

### TextView - Deschidere Tag

```xml
    <TextView
```

**Explicație:** Deschide definirea unui `TextView` - un widget care afișează text pe ecran. În această aplicație, TextView-ul va afișa gluma descărcată de la API.

---

### ID TextView

```xml
        android:id="@+id/jokeTextView"
```

**Explicație:** Creează un ID nou pentru TextView:
- `jokeTextView` = numele ID-ului
- Folosit în Java: `findViewById(R.id.jokeTextView)`
- Folosit în XML: `@id/jokeTextView` (pentru constraints)

---

### Lățime TextView

```xml
        android:layout_width="0dp"
```

**Explicație:** Setează lățimea TextView-ului la `0dp`, care în ConstraintLayout înseamnă **"MATCH_CONSTRAINT"**:
- Lățimea va fi determinată de constraints-uri (relații cu alte View-uri)
- În acest caz: de la `Start` la `End` al părintelui (toată lățimea)

**Valoare specială 0dp în ConstraintLayout:**
```
0dp + constraints pe Start și End = ocupă spațiul între constraints
```

**Alternativa (fără ConstraintLayout):**
- `match_parent` = ocupă toată lățimea părintelui
- `wrap_content` = se ajustează la conținut

---

### Înălțime TextView

```xml
        android:layout_height="wrap_content"
```

**Explicație:** Setează înălțimea TextView-ului să se ajusteze automat la conținutul textului:
- Dacă textul e scurt = înălțime mică
- Dacă textul e lung (mai multe linii) = înălțime mai mare

---

### Text Implicit

```xml
        android:text="Joke will appear here"
```

**Explicație:** Setează textul inițial afișat în TextView:
- `"Joke will appear here"` = mesaj placeholder
- Acest text va fi înlocuit de gluma descărcată din `MainActivity.java` cu `setText()`

**Design-time vs Runtime:**
- La design (Android Studio): se vede acest text
- La rulare: se vede glumea (după ce API răspunde)

---

### Dimensiune Text

```xml
        android:textSize="24sp"
```

**Explicație:** Setează dimensiunea textului la 24 Scale-independent Pixels (sp):
- `sp` = unitate care se scalează cu preferințele de accesibilitate ale utilizatorului
- `24sp` = text mare, lizibil
- Recomandare: folosiți **sp** pentru text, **dp** pentru alte dimensiuni

**Scalare accesibilitate:**
```
Setări utilizator: Text Normal → 24sp afișat ca 24sp
Setări utilizator: Text Mare   → 24sp afișat ca 32sp (exemplu)
```

---

### Aliniere Text

```xml
        android:textAlignment="center"
```

**Explicație:** Aliniază textul **orizontal** în centru:
- Liniile de text sunt centrate în cadrul TextView-ului
- Alternativă modernă la `android:gravity`

**Valori posibile:**
- `center` = centru
- `textStart` = stânga (RTL: dreapta)
- `textEnd` = dreapta (RTL: stânga)
- `viewStart`, `viewEnd` = relativ la View

---

### Constraint Top

```xml
        app:layout_constraintTop_toTopOf="parent"
```

**Explicație:** Creează un constraint (relație) între marginea **de sus** a TextView-ului și marginea **de sus** a părintelui (ConstraintLayout):

```
┌─────────────────────────────┐
│ ConstraintLayout (parent)   │ ← Top of parent
├─────────────────────────────┤
│ TextView                    │ ← Top of TextView
│ (aligned to parent top)     │
```

**Pattern:** `layout_constraint[MargineaProprie]_to[MargineaȚintă]Of="[ElementȚintă]"`

---

### Constraint Bottom

```xml
        app:layout_constraintBottom_toTopOf="@+id/refreshButton"
```

**Explicație:** Creează un constraint între marginea **de jos** a TextView-ului și marginea **de sus** a butonului Refresh:

```
┌─────────────────────────────┐
│ TextView                    │
├─────────────────────────────┤ ← Bottom of TextView
│                             │ ← Top of refreshButton
├─────────────────────────────┤
│ refreshButton               │
```

Acest constraint poziționează TextView-ul **deasupra** butonului Refresh.

---

### Constraint Start

```xml
        app:layout_constraintStart_toStartOf="parent"
```

**Explicație:** Aliniază marginea **de start** (stânga în LTR, dreapta în RTL) a TextView-ului cu marginea de start a părintelui:

```
┌─────────────────────────────┐
│ ConstraintLayout            │
│                             │
│ ┌───────────────────────┐   │
│ │ TextView              │   │
│ └───────────────────────┘   │
│ ^                           │
│ └── Aligned to parent start │
└─────────────────────────────┘
```

**Start vs Left:**
- `Start` = stânga (LTR), dreapta (RTL) - suportă limbi RTL
- `Left` = întotdeauna stânga - nu suportă RTL

---

### Constraint End

```xml
        app:layout_constraintEnd_toEndOf="parent"
```

**Explicație:** Aliniază marginea **de end** (dreapta în LTR, stânga în RTL) a TextView-ului cu marginea de end a părintelui:

```
┌─────────────────────────────┐
│ ConstraintLayout            │
│                             │
│   ┌───────────────────────┐ │
│   │ TextView              │ │
│   └───────────────────────┘ │
│                           ^ │
│        Aligned to parent end┘
└─────────────────────────────┘
```

**Efectul combinat Start + End + width="0dp":**
- TextView-ul se întinde de la marginea stângă la marginea dreaptă
- Ocupă toată lățimea disponibilă

---

### Padding

```xml
        android:padding="16dp"
```

**Explicație:** Adaugă spațiu intern (padding) de 16 Density-independent Pixels pe **toate** laturile TextView-ului:
- Textul nu atinge marginile TextView-ului
- Spațiu uniform pe toate laturile

```
┌─────────────────────────────┐
│ TextView                    │
│ ┌─────────────────────────┐ │
│ │ 16dp padding            │ │
│ │                         │ │
│ │  Text content here      │ │
│ │                         │ │
│ │ 16dp padding            │ │
│ └─────────────────────────┘ │
└─────────────────────────────┘
```

**Padding specific (alternativ):**
- `paddingStart`, `paddingEnd` = orizontal (LTR/RTL aware)
- `paddingTop`, `paddingBottom` = vertical
- `paddingLeft`, `paddingRight` = orizontal (nu suportă RTL)

---

### Gravity

```xml
        android:gravity="center"
```

**Explicație:** Centrarea conținutului (textului) **în interiorul** TextView-ului:
- Text centrat vertical și orizontal
- Similar cu `textAlignment="center"`, dar afectează și alinierea verticală

**Gravity vs TextAlignment:**
- `gravity` = poziționează conținutul în interiorul View-ului
- `textAlignment` = aliniază doar textul orizontal (mai modern)

```
┌─────────────────────────────┐
│ TextView                    │
│                             │
│      Text centered here     │ ← gravity="center"
│                             │
└─────────────────────────────┘
```

---

### Margin Top

```xml
        android:layout_marginTop="16dp"
```

**Explicație:** Adaugă spațiu extern (margin) de 16dp deasupra TextView-ului:
- Spațiu între marginea de sus a părintelui și TextView
- Împinge TextView-ul în jos cu 16dp

```
┌─────────────────────────────┐
│ ConstraintLayout            │
│ ▼ 16dp margin top           │
├─────────────────────────────┤
│ TextView                    │
```

**Margin vs Padding:**
- **Margin** = spațiu **în afara** View-ului (între View-uri)
- **Padding** = spațiu **în interiorul** View-ului (între margine și conținut)

---

### Margin Bottom

```xml
        android:layout_marginBottom="16dp"/>
```

**Explicație:** Adaugă spațiu extern de 16dp sub TextView:
- Spațiu între TextView și butonul Refresh de dedesubt
- Împinge butonul Refresh în jos cu 16dp

```
┌─────────────────────────────┐
│ TextView                    │
├─────────────────────────────┤
│ ▼ 16dp margin bottom        │
├─────────────────────────────┤
│ refreshButton               │
```

**Închidere Tag:** `/>` închide tag-ul `<TextView>` (self-closing tag fără conținut).

---

### Comentarii Butoane

```xml
    <!-- Refresh Joke Button -->

    <!-- Quit Button -->
```

**Explicație:** Comentarii care descriu cele două butoane care urmează. Observăm că comentariile sunt goale (doar titluri), sugerând că butoanele sunt auto-explicative.

---

### Button Refresh - Deschidere Tag

```xml
    <Button
```

**Explicație:** Deschide definirea unui `Button` - un widget clickable care va declanșa descărcarea unei noi glume.

---

### ID Button Refresh

```xml
        android:id="@+id/refreshButton"
```

**Explicație:** Creează ID-ul `refreshButton` folosit pentru:
- Găsire în Java: `findViewById(R.id.refreshButton)`
- Referire în constraints: `@id/refreshButton`

---

### Dimensiuni Button Refresh

```xml
        android:layout_width="407dp"
        android:layout_height="97dp"
```

**Explicație:** Setează dimensiuni fixe pentru butonul Refresh:
- Lățime: 407dp (aproape toată lățimea ecranului)
- Înălțime: 97dp (buton mare, ușor de apăsat)

**Observație:** Dimensiunile fixe (407dp) nu sunt ideale pentru responsive design. O abordare mai bună ar fi:
```xml
android:layout_width="0dp"
app:layout_constraintStart_toStartOf="parent"
app:layout_constraintEnd_toEndOf="parent"
```

---

### Text Button Refresh

```xml
        android:text="Refresh Joke"
```

**Explicație:** Setează textul afișat pe buton:
- `"Refresh Joke"` = instrucțiune clară pentru utilizator
- La click, se va descărca o glumă nouă

---

### Dimensiune Text Button Refresh

```xml
        android:textSize="18sp"
```

**Explicație:** Setează dimensiunea textului la 18sp:
- Mai mic decât textul glumei (24sp)
- Dar suficient de mare pentru lizibilitate

---

### Constraint Bottom Button Refresh

```xml
        app:layout_constraintBottom_toTopOf="@+id/quitButton"
```

**Explicație:** Poziționează butonul Refresh **deasupra** butonului Quit:
- Marginea de jos a Refresh = marginea de sus a Quit
- Cele două butoane sunt lipite vertical

```
┌─────────────────────────────┐
│ refreshButton               │
├─────────────────────────────┤ ← Bottom of refresh = Top of quit
│ quitButton                  │
└─────────────────────────────┘
```

---

### Constraints Orizontale Button Refresh

```xml
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />
```

**Explicație:** Centrarea orizontală a butonului Refresh:
- `End` aliniat cu `End` părinte
- `Start` aliniat cu `Start` părinte

Cu `width="407dp"` fix, butonul este centrat orizontal.

**Închidere Tag:** `/>` închide tag-ul `<Button>`.

---

### Button Quit - Deschidere Tag

```xml
    <Button
```

**Explicație:** Deschide definirea butonului Quit care va închide aplicația.

---

### ID Button Quit

```xml
        android:id="@+id/quitButton"
```

**Explicație:** Creează ID-ul `quitButton` folosit în Java pentru atașarea listener-ului de click.

---

### Dimensiuni Button Quit

```xml
        android:layout_width="404dp"
        android:layout_height="98dp"
```

**Explicație:** Setează dimensiuni fixe pentru butonul Quit:
- Lățime: 404dp (similar cu Refresh: 407dp)
- Înălțime: 98dp (similar cu Refresh: 97dp)

**Observație:** Diferențele mici (3dp lățime, 1dp înălțime) par neintenționate și creează inconsistență vizuală.

---

### Text Button Quit

```xml
        android:text="Quit"
```

**Explicație:** Setează textul afișat pe buton:
- `"Quit"` = instrucțiune clară de ieșire din aplicație

---

### Dimensiune Text Button Quit

```xml
        android:textSize="18sp"
```

**Explicație:** Setează dimensiunea textului la 18sp, identică cu butonul Refresh pentru consistență vizuală.

---

### Constraint Bottom Button Quit

```xml
        app:layout_constraintBottom_toBottomOf="parent"
```

**Explicație:** Aliniază marginea de jos a butonului Quit cu marginea de jos a părintelui:
- Butonul Quit este "lipit" de partea de jos a ecranului
- Poziție finală în ierarhia verticală

```
┌─────────────────────────────┐
│ TextView                    │
├─────────────────────────────┤
│ refreshButton               │
├─────────────────────────────┤
│ quitButton                  │
└─────────────────────────────┘
  ↑ Bottom of parent
```

---

### Constraint End Button Quit

```xml
        app:layout_constraintEnd_toEndOf="parent"
```

**Explicație:** Aliniază marginea de end (dreapta) a butonului cu marginea de end a părintelui.

---

### Horizontal Bias

```xml
        app:layout_constraintHorizontal_bias="0.0"
```

**Explicație:** Controlează poziționarea orizontală când există constraints pe ambele margini (Start și End):
- `0.0` = complet la stânga (0%)
- `0.5` = centru (50%) - valoare implicită
- `1.0` = complet la dreapta (100%)

**Observație:** Cu `bias="0.0"` și `width="404dp"` fix, butonul este aliniat la stânga. Totuși, având constraints pe Start și End, bias-ul nu are efect semnificativ cu dimensiune fixă.

**Diagrama Bias:**
```
bias = 0.0          bias = 0.5          bias = 1.0
┌───────────┐       ┌───────────┐       ┌───────────┐
│Button     │       │  Button   │       │     Button│
└───────────┘       └───────────┘       └───────────┘
```

---

### Constraint Start Button Quit

```xml
        app:layout_constraintStart_toStartOf="parent" />
```

**Explicație:** Aliniază marginea de start (stânga) a butonului cu marginea de start a părintelui.

**Închidere Tag:** `/>` închide tag-ul `<Button>` pentru butonul Quit.

---

### Închidere ConstraintLayout

```xml
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Explicație:** Închide elementul root `ConstraintLayout`. Aceasta marchează sfârșitul definiției layout-ului.

---

## Diagrama Structurii Layout

```
┌─────────────────────────────────────────────┐
│ ConstraintLayout (match_parent × match)     │
│ android:id="@+id/main"                      │
│                                             │
│  ┌─────────────────────────────────────┐   │
│  │ TextView (0dp × wrap_content)       │   │ ← marginTop: 16dp
│  │ android:id="@+id/jokeTextView"      │   │
│  │ "Joke will appear here"             │   │
│  │ textSize: 24sp, centered            │   │
│  │ padding: 16dp                       │   │
│  └─────────────────────────────────────┘   │
│         ↓ marginBottom: 16dp               │
│  ┌─────────────────────────────────────┐   │
│  │ Button (407dp × 97dp)               │   │
│  │ android:id="@+id/refreshButton"     │   │
│  │ "Refresh Joke"                      │   │
│  │ textSize: 18sp                      │   │
│  └─────────────────────────────────────┘   │
│                                             │
│  ┌─────────────────────────────────────┐   │
│  │ Button (404dp × 98dp)               │   │
│  │ android:id="@+id/quitButton"        │   │
│  │ "Quit"                              │   │
│  │ textSize: 18sp                      │   │
│  └─────────────────────────────────────┘   │
│                                             │
└─────────────────────────────────────────────┘
```

---

## Diagrama Constraints

### TextView Constraints

```
        Top → parent.Top
         │
    ┌────┴────┐
    │TextView │
    └────┬────┘
         │
      Bottom → refreshButton.Top
         │
    Start → parent.Start
      End → parent.End
```

### Button Refresh Constraints

```
      Bottom → quitButton.Top
         │
    ┌────┴────┐
    │ Refresh │
    └─────────┘
         │
    Start → parent.Start
      End → parent.End
```

### Button Quit Constraints

```
      Bottom → parent.Bottom
         │
    ┌────┴────┐
    │  Quit   │
    └─────────┘
         │
    Start → parent.Start
      End → parent.End
```

---

## Diagrama Margin vs Padding

```
┌─────────────────────────────────────────┐
│ ConstraintLayout (parent)               │
│                                         │
│ ▼ layout_marginTop = 16dp              │
│ ┌─────────────────────────────────────┐ │
│ │ TextView                            │ │
│ │ ┌─────────────────────────────────┐ │ │
│ │ │ padding = 16dp                  │ │ │
│ │ │                                 │ │ │
│ │ │  "Joke will appear here"        │ │ │
│ │ │                                 │ │ │
│ │ │ padding = 16dp                  │ │ │
│ │ └─────────────────────────────────┘ │ │
│ └─────────────────────────────────────┘ │
│ ▼ layout_marginBottom = 16dp           │
│ ┌─────────────────────────────────────┐ │
│ │ Button Refresh                      │ │
│ └─────────────────────────────────────┘ │
│                                         │

Legend:
┌─┐ = View bounds
│ │ = Margin (spațiu exterior)
│││ = Padding (spațiu interior)
```

---

## Fluxul de Pozitionare ConstraintLayout

### 1. Rezolvare Constraints Verticale

```
1. TextView.Top → parent.Top (+ 16dp margin)
2. TextView.Bottom → refreshButton.Top (+ 16dp margin)
3. refreshButton.Bottom → quitButton.Top
4. quitButton.Bottom → parent.Bottom

Rezultat:
┌──────────┐
│ TextView │ ← Top fixed, Bottom constrained
├──────────┤
│ Refresh  │ ← Top și Bottom constrained
├──────────┤
│ Quit     │ ← Bottom fixed, Top constrained
└──────────┘
```

### 2. Rezolvare Constraints Orizontale

```
Toate View-urile:
- Start → parent.Start
- End → parent.End
- Width = fix (407dp, 404dp) sau 0dp (TextView)

Rezultat: Centrare orizontală sau întindere completă
```

### 3. Aplicare Margin și Padding

```
- Margins: Spațiu între View-uri
- Padding: Spațiu în interiorul TextView
```

---

## Rezumat Ierarhie

```
activity_main.xml
└── ConstraintLayout (root)
    ├── TextView (jokeTextView)
    │   ├── Text: "Joke will appear here"
    │   ├── Size: 24sp
    │   └── Constraints: Top↔Parent, Bottom↔Refresh, Start/End↔Parent
    │
    ├── Button (refreshButton)
    │   ├── Text: "Refresh Joke"
    │   ├── Size: 18sp
    │   └── Constraints: Bottom↔Quit, Start/End↔Parent
    │
    └── Button (quitButton)
        ├── Text: "Quit"
        ├── Size: 18sp
        └── Constraints: Bottom↔Parent, Start/End↔Parent
```

---

## Concepte Cheie

### 1. ConstraintLayout
- Layout manager modern și flexibil
- Poziționare prin relații (constraints) între View-uri
- Performanță superioară față de LinearLayout nested
- `0dp` width/height = **MATCH_CONSTRAINT** (dimensiune calculată din constraints)

### 2. Constraints (Relații)
- Pattern: `layout_constraint[MargineaProprie]_to[MargineaȚintă]Of="[ElementȚintă]"`
- Exemple:
  - `Top_toTopOf="parent"` = sus aliniat cu sus părinte
  - `Bottom_toTopOf="@id/button"` = jos aliniat cu sus buton
  - `Start_toStartOf` / `End_toEndOf` = aliniere orizontală (RTL-aware)

### 3. Margin vs Padding
- **Margin** = spațiu **exterior** (între View-uri)
- **Padding** = spațiu **interior** (între margine și conținut)
- Margin: `layout_margin*`
- Padding: `padding*`

### 4. Unități de Măsură
- **dp** (Density-independent Pixels) = pentru dimensiuni layout, margin, padding
- **sp** (Scale-independent Pixels) = pentru text (respectă setări accesibilitate)
- **px** (Pixels) = pixeli fizici (NU recomandați - nu scalează)

### 5. Match Parent vs Wrap Content vs 0dp
- `match_parent` = ocupă toată dimensiunea părintelui
- `wrap_content` = se ajustează la conținut
- `0dp` (în ConstraintLayout) = **MATCH_CONSTRAINT** (calculat din constraints)

### 6. Start/End vs Left/Right
- **Start/End** = stânga/dreapta în LTR, dreapta/stânga în RTL
- **Left/Right** = întotdeauna stânga/dreapta (nu suportă RTL)
- **Recomandare:** Folosiți Start/End pentru suport RTL (arabe, ebraice)

### 7. Gravity vs TextAlignment
- `android:gravity` = poziționează conținutul în interiorul View-ului
- `android:textAlignment` = aliniază doar textul (mai modern, API 17+)
- Ambele pot fi folosite împreună

### 8. Horizontal Bias
- Controlează poziționarea când există constraints pe ambele margini
- Valori: 0.0 (stânga) → 0.5 (centru, implicit) → 1.0 (dreapta)
- Funcționează doar cu width/height = `0dp` (MATCH_CONSTRAINT)

### 9. ID-uri Resurse
- `@+id/name` = creează ID nou
- `@id/name` = referă ID existent
- Folosite pentru:
  - findViewById() în Java
  - Constraints în XML
  - Accesare din alte resurse

### 10. Namespace-uri XML
- `android:` = atribute standard Android
- `app:` = atribute personalizate și biblioteci (ConstraintLayout, Material)
- `tools:` = atribute doar pentru Android Studio (preview, lint)
