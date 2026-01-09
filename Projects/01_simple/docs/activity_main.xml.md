# activity_main.xml

<!-- TOC -->

- [activity_main.xml](#activity_mainxml)
    - [1. Prezentare Generală](#1-prezentare-general%C4%83)
    - [2. Analiza Linie cu Linie](#2-analiza-linie-cu-linie)
        - [2.1. Declarația XML](#21-declara%C8%9Bia-xml)
        - [2.2. Root Element: ConstraintLayout](#22-root-element-constraintlayout)
            - [2.2.1. Tag-ul Principal](#221-tag-ul-principal)
            - [2.2.2. Atribute ConstraintLayout](#222-atribute-constraintlayout)
        - [2.3. TextView Element](#23-textview-element)
            - [2.3.1. Tag-ul TextView](#231-tag-ul-textview)
            - [2.3.2. Atribute Dimensiuni](#232-atribute-dimensiuni)
            - [2.3.3. Atribute Text](#233-atribute-text)
            - [2.3.4. Constraint Attributes](#234-constraint-attributes)
        - [2.4. Button Element](#24-button-element)
            - [2.4.1. Tag-ul Button](#241-tag-ul-button)
            - [2.4.2. Atribute ID și Dimensiuni](#242-atribute-id-%C8%99i-dimensiuni)
            - [2.4.3. Atribute Text](#243-atribute-text)
            - [2.4.4. Constraint Attributes](#244-constraint-attributes)
    - [3. Structura Ierarhică Vizuală](#3-structura-ierarhic%C4%83-vizual%C4%83)
        - [3.1. View Hierarchy](#31-view-hierarchy)

<!-- /TOC -->

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

## Analiza Linie cu Linie

### 1. Declarația XML

```xml
<?xml version="1.0" encoding="utf-8"?>
```

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
- ViewGroup (container) pentru view-uri
- Parte din AndroidX (Android Extension Libraries)
- Fully qualified name: `androidx.constraintlayout.widget.ConstraintLayout`

**Ierarhie:**
```
Object
  └─ View
      └─ ViewGroup
          └─ ConstraintLayout
```

**ConstraintLayout:**
Layout manager modern care permite:
- Poziționare relativă complexă
- Flat view hierarchy (performanță mai bună)
- Responsive design
- Design vizual în Layout Editor

#### Atribute ConstraintLayout

**`android:id="@+id/main"`**

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

- Setează lățimea view-ului
- `match_parent` = ocupă toată lățimea părintelui

**`android:layout_height="match_parent"`**

- Setează înălțimea view-ului
- `match_parent` = ocupă toată înălțimea părintelui (ecranul)

**`tools:context=".MainActivity"`**

- Indică Android Studio că acest layout e folosit de MainActivity
- Doar pentru design-time (nu afectează aplicația)
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

#### Atribute Dimensiuni

**`android:layout_width="match_parent"`**
- Lățime = toată lățimea părintelui (ConstraintLayout)
- Text se va întinde pe toată lățimea ecranului

**`android:layout_height="wrap_content"`**
- Înălțime = cât e nevoie pentru text
- Se adaptează automat la dimensiunea textului


#### Atribute Text

**`android:text="Hello World"`**

- Setează textul afișat
- String hardcodat în XML

**`android:textSize="42sp"`**

- Setează dimensiunea textului
- `42sp` = 42 scale-independent pixels

**`android:textAllCaps="true"`**

- Convertește textul în MAJUSCULE
- `"Hello World"` → `"HELLO WORLD"`


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


**Relație:**
```
TextView.bottom = quitButton.top
```

**`app:layout_constraintEnd_toEndOf="parent"`**

**Analiza:**
- `End` = marginea dreaptă (în LTR) / stângă (în RTL)
- `toEndOf` = se aliniază la marginea dreaptă
- `parent` = părintele (ConstraintLayout)



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

- Creează ID unic pentru buton
- ID folosit în:
  - Java: `findViewById(R.id.quitButton)`
  - XML: `@id/quitButton` (în constraints)

**`android:layout_width="match_parent"`**
- Lățime = toată lățimea ecranului
- Buton se întinde pe toată lățimea

**`android:layout_height="87dp"`**

- Înălțime fixă de 87 density-independent pixels
- NU se adaptează la conținut (nu e wrap_content)


#### Atribute Text

**`android:text="Quit"`**

- Setează textul butonului
- Apare ca "Quit" pe buton


#### Constraint Attributes

**`app:layout_constraintBottom_toBottomOf="parent"`**

**Analiza:**
- `Bottom` = marginea de jos a butonului
- `toBottomOf` = se aliniază la marginea de jos
- `parent` = ConstraintLayout (ecran)

**`app:layout_constraintEnd_toEndOf="parent"`**

**Analiza:**
- `End` = marginea dreaptă (LTR)
- `toEndOf` = se aliniază la marginea dreaptă
- `parent` = ConstraintLayout
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

