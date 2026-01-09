# Script Live Coding - Simple Hello World cu Buton Quit

## Prezentare Aplicație

Această aplicație Android extinde aplicația Hello World de bază prin introducerea unui buton "Quit" care permite utilizatorului să închidă aplicația. Diferența arhitecturală principală față de aplicația `01_hello_world` este separarea interfeței utilizatorului de logica aplicației: interfața este definită într-un fișier XML de layout (`activity_main.xml`) în loc să fie creată programatic în Java. Aplicația folosește `ConstraintLayout` pentru o poziționare flexibilă a elementelor pe ecran, permițând constrângeri complexe între componente. Codul Java folosește metoda `findViewById()` pentru a obține referințe la elementele UI definite în XML și atașează un listener de evenimente la buton folosind lambda expressions. Această abordare demonstrează pattern-ul Model-View-Controller în Android, unde XML-ul reprezintă View-ul, iar Java gestionează logica. Aplicația nu folosește Parcelable și nu are navigare între activități multiple.

## Structura Directorului Aplicației

```
akrilki_01/
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/
│           │   └── ro/
│           │       └── makore/
│           │           └── akrilki_01/
│           │               └── MainActivity.java
│           └── res/
│               └── layout/
│                   └── activity_main.xml
├── build.gradle
├── gradle.properties
└── settings.gradle
```

## Pași Live Coding

### Pasul 1: Crearea structurii de directoare

**Ce fac:** Creez structura completă de directoare pentru aplicația Android, inclusiv directorul pentru layout-uri XML.

**Ce scriu în terminal:**
```bash
mkdir -p akrilki_01/app/src/main/java/ro/makore/akrilki_01
mkdir -p akrilki_01/app/src/main/res/layout
```

**Ce spun:** "Vom începe prin a crea structura de directoare pentru aplicația noastră. Observați că am adăugat directorul `res/layout` care va conține fișierele XML pentru interfața utilizatorului. Aceasta este diferența principală față de aplicația Hello World anterioară - vom separa definirea UI-ului în fișiere XML."

**Checkpoint:** Structura de directoare este creată, inclusiv directorul pentru layout-uri.

---

### Pasul 2: Crearea fișierului settings.gradle

**Ce fac:** Creez fișierul `settings.gradle` la rădăcina proiectului pentru configurarea modulelor Gradle.

**Ce scriu:**
```groovy
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "akrilki_01"
include ':app'
```

**Ce spun:** "Creez fișierul `settings.gradle` cu configurația standard pentru plugin-uri și dependențe. Observați că numele proiectului este `akrilki_01`, care corespunde cu structura pachetului aplicației."

**Checkpoint:** Fișierul `settings.gradle` este creat și configurează corect proiectul.

---

### Pasul 3: Crearea fișierului build.gradle la rădăcină

**Ce fac:** Creez fișierul `build.gradle` la nivelul rădăcină care configurează plugin-ul Android pentru Gradle.

**Ce scriu:**
```groovy
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.3.0'
    }
}
```

**Ce spun:** "Creez fișierul `build.gradle` la rădăcină cu configurația pentru plugin-ul Gradle Android, versiunea 8.3.0."

**Checkpoint:** Fișierul `build.gradle` rădăcină este creat.

---

### Pasul 4: Crearea fișierului gradle.properties

**Ce fac:** Creez fișierul `gradle.properties` pentru configurații globale Gradle.

**Ce scriu:**
```
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.nonTransitiveRClass=true
```

**Ce spun:** "Creez fișierul `gradle.properties` cu setările standard: memorie JVM, utilizarea AndroidX și generarea unui fișier R non-transitiv."

**Checkpoint:** Fișierul `gradle.properties` este creat.

---

### Pasul 5: Crearea fișierului app/build.gradle

**Ce fac:** Creez fișierul `build.gradle` pentru modulul `app` care definește configurația aplicației Android.

**Ce scriu:**
```groovy
apply plugin: 'com.android.application'

android {
    namespace 'ro.makore.akrilki_01'
    compileSdk 34

    defaultConfig {
        applicationId "ro.makore.akrilki_01"
        minSdkVersion 21
        targetSdkVersion 34
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
```

**Ce spun:** "Creez fișierul `app/build.gradle`. Setez namespace-ul la `ro.makore.akrilki_01` și configurez versiunile SDK. Observați că `minSdkVersion` este 21, permițând rularea pe dispozitive mai vechi decât în aplicația anterioară."

**Ce scriu (continuare):**
```groovy
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
}
```

**Ce spun:** "Configurez tipurile de build și opțiunile de compilare Java la versiunea 11."

**Ce scriu (continuare):**
```groovy
dependencies {
    implementation 'androidx.appcompat:appcompat:1.5.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
}
```

**Ce spun:** "În blocul `dependencies` adaug două biblioteci: `appcompat` pentru `AppCompatActivity` și `constraintlayout` pentru `ConstraintLayout`. Această ultimă dependență este nouă față de aplicația Hello World - este necesară pentru a folosi `ConstraintLayout` în fișierul XML de layout."

**Checkpoint:** Fișierul `app/build.gradle` este complet configurat, inclusiv dependența pentru ConstraintLayout.

---

### Pasul 6: Crearea fișierului AndroidManifest.xml

**Ce fac:** Creez fișierul `AndroidManifest.xml` care declară componentele aplicației.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:label="Alkrilki01"
        android:theme="@style/Theme.AppCompat.Light.NoActionBar">
```

**Ce spun:** "Creez fișierul `AndroidManifest.xml` în `app/src/main/`. Declar tag-ul `manifest` cu namespace-urile necesare. În tag-ul `application` setez label-ul aplicației la 'Alkrilki01' și tema la `Theme.AppCompat.Light.NoActionBar` pentru un fundal clar fără bara de acțiuni."

**Ce scriu (continuare):**
```xml
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

**Ce spun:** "Declar activitatea `MainActivity` cu `android:exported="true"` pentru a permite sistemului să o lanseze. `intent-filter` cu acțiunea `MAIN` și categoria `LAUNCHER` face ca această activitate să fie punctul de intrare al aplicației."

**Checkpoint:** Fișierul `AndroidManifest.xml` este creat și declară corect activitatea principală.

---

### Pasul 7: Crearea fișierului activity_main.xml

**Ce fac:** Creez fișierul XML de layout care definește interfața utilizatorului. Aceasta este diferența principală față de aplicația Hello World anterioară.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
```

**Ce spun:** "Creez fișierul `activity_main.xml` în directorul `app/src/main/res/layout/`. Aceasta este prima dată când definim interfața într-un fișier XML separat, nu programatic. Folosesc `ConstraintLayout` ca layout principal, care permite constrângeri flexibile între elemente. Declar trei namespace-uri: `android` pentru atribute standard, `app` pentru atribute din biblioteci externe, și `tools` pentru atribute folosite doar în timpul dezvoltării."

**Ce scriu (continuare):**
```xml
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
```

**Ce spun:** "Setez ID-ul layout-ului la `@+id/main` - simbolul `@+id/` creează un nou ID în clasa R generată automat. `match_parent` pentru lățime și înălțime face ca layout-ul să ocupe tot ecranul. `tools:context` specifică activitatea asociată, folosită de instrumentele de dezvoltare."

**Ce scriu (continuare):**
```xml
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Hello World"
        android:textSize="42sp"
        android:textAllCaps="true"
```

**Ce spun:** "Adaug un `TextView` pentru textul 'Hello World'. `layout_width="match_parent"` face ca textul să ocupe toată lățimea ecranului. `wrap_content` pentru înălțime înseamnă că se ajustează la conținut. Setez dimensiunea textului la 42sp și activez majusculele cu `textAllCaps`."

**Ce scriu (continuare):**
```xml
        app:layout_constraintBottom_toTopOf="@+id/quitButton"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
```

**Ce spun:** "Acum definesc constrângerile pentru `TextView`. `layout_constraintTop_toTopOf="parent"` fixează partea de sus la partea de sus a părinte. `layout_constraintStart_toStartOf` și `layout_constraintEnd_toEndOf` centrează orizontal. `layout_constraintBottom_toTopOf="@+id/quitButton"` plasează partea de jos deasupra butonului - observați că referim un ID care va fi definit mai jos, ceea ce demonstrează flexibilitatea ConstraintLayout."

**Ce scriu (continuare):**
```xml
    <Button
        android:id="@+id/quitButton"
        android:layout_width="match_parent"
        android:layout_height="87dp"
        android:layout_marginBottom="20dp"
        android:text="Quit"
```

**Ce spun:** "Adaug un `Button` cu ID-ul `quitButton` - acest ID va fi folosit în Java pentru a obține referința la buton. Lățimea este `match_parent` pentru a ocupa toată lățimea, înălțimea este fixă la 87dp. Adaug un margin de 20dp în partea de jos pentru spațiere."

**Ce scriu (continuare):**
```xml
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />
```

**Ce spun:** "Setez constrângerile pentru buton: `layout_constraintBottom_toBottomOf="parent"` fixează butonul în partea de jos a ecranului, iar `layout_constraintEnd_toEndOf` aliniază la dreapta. ConstraintLayout va calcula automat poziționarea corectă."

**Ce scriu (continuare):**
```xml
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Ce spun:** "Închid tag-ul `ConstraintLayout`. Layout-ul este complet - avem un TextView centrat sus și un buton fixat jos, cu constrângeri care leagă elementele între ele."

**Checkpoint:** Fișierul `activity_main.xml` este creat și definește interfața cu TextView și Button folosind ConstraintLayout.

---

### Pasul 8: Crearea clasei MainActivity.java

**Ce fac:** Creez clasa Java principală care folosește layout-ul XML și gestionează evenimentele.

**Ce scriu:**
```java
package ro.makore.akrilki_01;
```

**Ce spun:** "Creez fișierul `MainActivity.java` în directorul `app/src/main/java/ro/makore/akrilki_01/`. Declar pachetul care corespunde cu structura de directoare."

**Ce scriu (continuare):**
```java
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
```

**Ce spun:** "Adaug import-urile necesare: `Bundle` pentru starea activității, `AppCompatActivity` ca clasă de bază, și `Button` pentru gestionarea butonului. Observați că nu mai importăm `LinearLayout` sau `TextView` - acestea sunt definite în XML, nu în Java."

**Ce scriu (continuare):**
```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
```

**Ce spun:** "Definesc clasa `MainActivity` care extinde `AppCompatActivity` și suprascriu metoda `onCreate`. Apelez `super.onCreate` pentru inițializarea corectă."

**Ce scriu (continuare):**
```java
        setContentView(R.layout.activity_main);
```

**Ce spun:** "Această linie este diferită față de aplicația Hello World anterioară! În loc să creez layout-ul programatic, folosesc `setContentView(R.layout.activity_main)` pentru a încărca layout-ul din fișierul XML. `R.layout.activity_main` este o referință generată automat de Android către fișierul `activity_main.xml` din directorul `res/layout/`. Aceasta demonstrează separarea interfeței de logică."

**Ce scriu (continuare):**
```java
        // Find the quit button
        Button quitButton = findViewById(R.id.quitButton);
```

**Ce spun:** "Folosesc metoda `findViewById()` pentru a obține referința la butonul definit în XML. `R.id.quitButton` corespunde cu `android:id="@+id/quitButton"` din fișierul XML. Aceasta este pattern-ul standard în Android pentru conectarea codului Java cu elementele UI definite în XML."

**Ce scriu (continuare):**
```java
        // Add the quit button action
        quitButton.setOnClickListener(v -> finishAffinity());
```

**Ce spun:** "Atașez un listener de evenimente la buton folosind o expresie lambda. Când butonul este apăsat, se execută `finishAffinity()` care închide toate activitățile din aplicație și iese complet. Lambda-ul `v ->` primește view-ul care a declanșat evenimentul, dar nu îl folosim în acest caz."

**Ce scriu (continuare):**
```java
    }
}
```

**Ce spun:** "Închid metoda `onCreate` și clasa. Aplicația este completă - interfața este definită în XML, iar logica de gestionare a evenimentelor este în Java."

**Checkpoint:** Clasa `MainActivity` este completă și folosește layout-ul XML, obține referințe la elemente cu `findViewById()` și gestionează evenimentele butonului.

---

### Pasul 9: Verificarea structurii proiectului

**Ce fac:** Verific că toate fișierele necesare sunt prezente și că structura este corectă.

**Ce spun:** "Am creat toate fișierele necesare pentru aplicația Simple Hello World. Diferențele principale față de aplicația Hello World anterioară sunt: interfața este definită în XML (`activity_main.xml`) în loc să fie creată programatic, folosim `ConstraintLayout` pentru o poziționare flexibilă, și avem un buton cu gestionare de evenimente. Aplicația nu folosește Parcelable, deci nu avem clase Parcelable de creat."

**Checkpoint:** Toate fișierele sunt create și structura proiectului este completă, cu separarea corectă între XML (View) și Java (Controller).

---

## Compilare, Instalare și Rulare

### Compilarea aplicației

**Ce fac:** Compilez aplicația pentru a genera APK-ul de debug.

**Ce scriu în terminal:**
```bash
cd akrilki_01
gradle build
```

**Ce spun:** "Navighez în directorul proiectului și rulez `gradle build` pentru a compila aplicația. Această comandă va genera APK-ul de debug în `app/build/outputs/apk/debug/app-debug.apk`. Gradle va procesa și fișierul XML de layout, generând clasele R necesare."

**Checkpoint:** Build-ul se finalizează cu succes, APK-ul este generat, și clasele R sunt create cu referințe către elementele din XML.

---

### Listarea dispozitivelor conectate

**Ce fac:** Verific ce dispozitive Android sunt conectate și disponibile pentru instalare.

**Ce scriu în terminal:**
```bash
adb devices
```

**Ce spun:** "Folosesc `adb devices` pentru a lista toate dispozitivele Android conectate prin USB sau emulatoarele care rulează. Trebuie să văd cel puțin un dispozitiv cu status 'device'."

**Checkpoint:** Se afișează lista de dispozitive, de exemplu: `List of devices attached` urmat de ID-ul dispozitivului și statusul `device`.

---

### Instalarea aplicației

**Ce fac:** Instalez APK-ul de debug pe dispozitivul conectat.

**Ce scriu în terminal:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Ce spun:** "Folosesc `adb install` pentru a instala APK-ul de debug pe dispozitiv. Dacă aplicația există deja, pot folosi `adb install -r` pentru reinstalare."

**Checkpoint:** Aplicația este instalată cu succes. Mesajul de confirmare arată "Success" sau "Performing Streamed Install".

---

### Lansarea activității principale

**Ce fac:** Lansez activitatea principală a aplicației pe dispozitiv.

**Ce scriu în terminal:**
```bash
adb shell am start -n ro.makore.akrilki_01/.MainActivity
```

**Ce spun:** "Folosesc `adb shell am start` pentru a lansa activitatea. Opțiunea `-n` specifică componenta prin pachet și numele clasei. Aplicația se va deschide pe dispozitiv și va afișa 'Hello World' sus și butonul 'Quit' jos, conform layout-ului XML definit."

**Checkpoint:** Aplicația se deschide pe dispozitiv, se afișează textul "Hello World" centrat sus și butonul "Quit" în partea de jos. La apăsarea butonului, aplicația se închide complet.

---

### Afișarea logurilor filtrate

**Ce fac:** Monitorizez logurile aplicației pentru a verifica comportamentul.

**Ce scriu în terminal:**
```bash
adb logcat | grep -i "akrilki_01\|MainActivity"
```

**Ce spun:** "Folosesc `adb logcat` pentru a afișa logurile sistemului Android. Filtrez output-ul cu `grep` pentru a vedea doar mesajele legate de aplicația noastră sau `MainActivity`. Pot folosi și `adb logcat *:S MainActivity:D` pentru a vedea doar log-urile de debug din MainActivity."

**Alternativă (Windows PowerShell):**
```powershell
adb logcat | Select-String -Pattern "akrilki_01|MainActivity"
```

**Checkpoint:** Logurile se afișează în terminal, arătând activitatea aplicației. Când apăsăm butonul "Quit", putem vedea în loguri că activitatea se închide și aplicația iese.

---

## Rezumat

Am creat o aplicație Android "Simple Hello World cu Buton Quit" care demonstrează:
- Separarea interfeței utilizatorului (definită în XML) de logica aplicației (în Java)
- Utilizarea `ConstraintLayout` pentru poziționare flexibilă a elementelor
- Pattern-ul `findViewById()` pentru conectarea codului Java cu elementele UI din XML
- Gestionarea evenimentelor cu `setOnClickListener()` și lambda expressions
- Structura de bază a unui proiect Android cu Gradle
- Compilarea, instalarea și rularea aplicației din linia de comandă

**Diferențele arhitecturale față de `01_hello_world`:**
- Interfața este definită în fișier XML (`activity_main.xml`) în loc să fie creată programatic
- Folosim `ConstraintLayout` în loc de `LinearLayout` pentru o poziționare mai flexibilă
- Codul Java folosește `setContentView(R.layout.activity_main)` pentru încărcarea layout-ului
- Pattern-ul `findViewById()` conectează Java cu XML
- Gestionarea evenimentelor demonstrează interacțiunea utilizatorului cu aplicația

Aplicația este funcțională și gata pentru dezvoltare ulterioară.

