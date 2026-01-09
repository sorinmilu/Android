# Script Live Coding - Hello World Android

## Prezentare Aplicație

Această aplicație Android este cea mai simplă aplicație posibilă care afișează textul "Hello World" pe ecran. Aplicația folosește o singură activitate (`MainActivity`) care extinde `AppCompatActivity` din biblioteca AndroidX pentru compatibilitate cu versiuni mai vechi de Android. Interfața utilizatorului este creată programatic, fără fișiere XML de layout, folosind un `LinearLayout` vertical în care este plasat un `TextView` cu textul "Hello World". Aplicația nu folosește Parcelable și nu are navigare între activități. Este o aplicație minimală care demonstrează conceptele de bază: crearea unei activități, inițializarea interfeței programatic și asocierea layout-ului cu activitatea prin metoda `setContentView()`.

## Structura Directorului Aplicației

```
hello_world/
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           └── java/
│               └── ro/
│                   └── makore/
│                       └── hello_world/
│                           └── MainActivity.java
├── build.gradle
├── gradle.properties
└── settings.gradle
```

## Pași Live Coding

### Pasul 1: Crearea structurii de directoare

**Ce fac:** Creez structura completă de directoare pentru aplicația Android.

**Ce scriu în terminal:**
```bash
mkdir -p hello_world/app/src/main/java/ro/makore/hello_world
mkdir -p hello_world/app/src/main/res
```

**Ce spun:** "Vom începe prin a crea structura de directoare pentru aplicația noastră. Structura respectă convențiile Android: directorul `app` conține codul aplicației, `src/main` conține sursele principale, iar `java/ro/makore/hello_world` respectă structura pachetului Java."

**Checkpoint:** Structura de directoare este creată și gata pentru fișiere.

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
```

**Ce spun:** "Creez fișierul `settings.gradle` care configurează gestionarea plugin-urilor. Blocul `pluginManagement` specifică depozitele de unde Gradle va descărca plugin-urile. Folosim depozitul Google pentru plugin-urile Android și Maven Central pentru celelalte."

**Ce scriu (continuare):**
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "hello_world"
include ':app'
```

**Ce spun:** "Blocul `dependencyResolutionManagement` configurează gestionarea dependențelor. Setăm modul la `FAIL_ON_PROJECT_REPOS` pentru a forța utilizarea depozitelor centrale. Definim numele proiectului rădăcină ca `hello_world` și includem modulul `app`."

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
        classpath 'com.android.tools.build:gradle:8.4.2'
    }
}
```

**Ce spun:** "Creez fișierul `build.gradle` la rădăcină. Blocul `buildscript` definește depozitele și dependențele necesare pentru construirea proiectului. Aici adăugăm plugin-ul Gradle pentru Android, versiunea 8.4.2."

**Checkpoint:** Fișierul `build.gradle` rădăcină este creat și conține configurația pentru plugin-ul Android.

---

### Pasul 4: Crearea fișierului gradle.properties

**Ce fac:** Creez fișierul `gradle.properties` pentru configurații globale Gradle.

**Ce scriu:**
```
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.nonTransitiveRClass=true
```

**Ce spun:** "Creez fișierul `gradle.properties`. Prima linie setează memoria maximă pentru JVM la 2GB și codificarea fișierelor la UTF-8. A doua linie activează AndroidX în locul bibliotecilor vechi de suport. A treia linie configurează generarea unui fișier R non-transitiv pentru o mai bună separare a modulelor."

**Checkpoint:** Fișierul `gradle.properties` este creat cu configurațiile necesare.

---

### Pasul 5: Crearea fișierului app/build.gradle

**Ce fac:** Creez fișierul `build.gradle` pentru modulul `app` care definește configurația aplicației Android.

**Ce scriu:**
```groovy
apply plugin: 'com.android.application'

android {
    namespace 'ro.makore.hello_world'
    compileSdk 34
```

**Ce spun:** "Creez fișierul `app/build.gradle`. Aplic plugin-ul `com.android.application` care transformă acest modul într-o aplicație Android. În blocul `android`, setez namespace-ul la `ro.makore.hello_world` și SDK-ul de compilare la 34, corespunzător Android 14."

**Ce scriu (continuare):**
```groovy
    defaultConfig {
        applicationId "ro.makore.hello_world"
        minSdkVersion 21
        targetSdkVersion 34
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
```

**Ce spun:** "În `defaultConfig` definesc ID-ul aplicației, versiunea minimă de Android acceptată (API 21, Android 5.0), versiunea țintă (API 34), codul de versiune intern și numele versiunii. De asemenea, specific runner-ul pentru teste instrumentate."

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

**Ce spun:** "Blocul `buildTypes` definește tipurile de build. Pentru release, dezactivez minificarea pentru simplitate. În `compileOptions` setez compatibilitatea Java la versiunea 11."

**Ce scriu (continuare):**
```groovy
dependencies {
    implementation 'androidx.appcompat:appcompat:1.5.1'
}
```

**Ce spun:** "În blocul `dependencies` adaug biblioteca `appcompat` din AndroidX, versiunea 1.5.1, necesară pentru `AppCompatActivity`."

**Checkpoint:** Fișierul `app/build.gradle` este complet configurat cu toate setările necesare pentru aplicație.

---

### Pasul 6: Crearea fișierului AndroidManifest.xml

**Ce fac:** Creez fișierul `AndroidManifest.xml` care declară componentele aplicației și metadatele esențiale.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android" xmlns:tools="http://schemas.android.com/tools">
```

**Ce spun:** "Creez fișierul `AndroidManifest.xml` în `app/src/main/`. Prima linie este declarația XML standard. În tag-ul `manifest` includ namespace-urile pentru Android și tools."

**Ce scriu (continuare):**
```xml
    <application
        android:theme="@style/Theme.AppCompat.Light.NoActionBar"
        android:label="HelloWorld"
        tools:targetApi="34">
```

**Ce spun:** "Tag-ul `application` definește configurația aplicației. Setez tema la `Theme.AppCompat.Light.NoActionBar` pentru un fundal clar fără bara de acțiuni. Label-ul aplicației este 'HelloWorld'."

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

**Ce spun:** "Declar activitatea `MainActivity` cu `android:name=".MainActivity"` - punctul înseamnă că este în pachetul definit în namespace. `android:exported="true"` permite sistemului să o lanseze. `intent-filter` cu acțiunea `MAIN` și categoria `LAUNCHER` face ca această activitate să fie punctul de intrare al aplicației, apărând în lista de aplicații."

**Checkpoint:** Fișierul `AndroidManifest.xml` este creat și declară corect activitatea principală ca launcher.

---

### Pasul 7: Crearea clasei MainActivity.java

**Ce fac:** Creez clasa Java principală `MainActivity` care extinde `AppCompatActivity` și creează interfața programatic.

**Ce scriu:**
```java
package ro.makore.hello_world;
```

**Ce spun:** "Creez fișierul `MainActivity.java` în directorul `app/src/main/java/ro/makore/hello_world/`. Prima linie declară pachetul aplicației, care trebuie să corespundă cu structura de directoare."

**Ce scriu (continuare):**
```java
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
```

**Ce spun:** "Adaug import-urile necesare: `Bundle` pentru starea activității, `LinearLayout` și `TextView` pentru interfață, și `AppCompatActivity` ca clasă de bază pentru activitate."

**Ce scriu (continuare):**
```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
```

**Ce spun:** "Definesc clasa `MainActivity` care extinde `AppCompatActivity`. Suprascriu metoda `onCreate` care este apelată când activitatea este creată. Apelez `super.onCreate` pentru inițializarea corectă a clasei de bază."

**Ce scriu (continuare):**
```java
        // Create a LinearLayout programmatically
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
```

**Ce spun:** "Creez un `LinearLayout` programatic, pasând contextul activității prin `this`. Setez orientarea la `VERTICAL` pentru a aranja elementele vertical."

**Ce scriu (continuare):**
```java
        // Create a TextView with "Hello World"
        TextView textView = new TextView(this);
        textView.setText("Hello World");
        textView.setTextSize(24);
```

**Ce spun:** "Creez un `TextView` programatic și setez textul la 'Hello World'. Dimensiunea textului este setată la 24sp, o unitate scalabilă care se adaptează la preferințele utilizatorului pentru dimensiunea fontului."

**Ce scriu (continuare):**
```java
        // Add the TextView to the layout
        layout.addView(textView);

        // Set the layout as the content view
        setContentView(layout);
    }
}
```

**Ce spun:** "Adaug `TextView`-ul în layout folosind `addView`. Apoi asociez layout-ul cu activitatea prin `setContentView`, care face ca layout-ul să fie afișat pe ecran."

**Checkpoint:** Clasa `MainActivity` este completă și creează interfața programatic cu un TextView care afișează "Hello World".

---

### Pasul 8: Verificarea structurii proiectului

**Ce fac:** Verific că toate fișierele necesare sunt prezente.

**Ce spun:** "Am creat toate fișierele necesare pentru aplicația Hello World. Aplicația nu folosește Parcelable, deci nu avem clase Parcelable de creat. Structura este completă și gata pentru compilare."

**Checkpoint:** Toate fișierele sunt create și structura proiectului este completă.

---

## Compilare, Instalare și Rulare

### Compilarea aplicației

**Ce fac:** Compilez aplicația pentru a genera APK-ul de debug.

**Ce scriu în terminal:**
```bash
cd hello_world
gradle build
```

**Ce spun:** "Navighez în directorul proiectului și rulez `gradle build` pentru a compila aplicația. Această comandă va genera APK-ul de debug în `app/build/outputs/apk/debug/app-debug.apk`."

**Checkpoint:** Build-ul se finalizează cu succes și APK-ul este generat.

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
adb shell am start -n ro.makore.hello_world/.MainActivity
```

**Ce spun:** "Folosesc `adb shell am start` pentru a lansa activitatea. Opțiunea `-n` specifică componenta prin pachet și numele clasei. Aplicația se va deschide pe dispozitiv și va afișa 'Hello World'."

**Checkpoint:** Aplicația se deschide pe dispozitiv și se afișează textul "Hello World" pe ecran.

---

### Afișarea logurilor filtrate

**Ce fac:** Monitorizez logurile aplicației pentru a verifica comportamentul.

**Ce scriu în terminal:**
```bash
adb logcat | grep -i "hello_world\|MainActivity"
```

**Ce spun:** "Folosesc `adb logcat` pentru a afișa logurile sistemului Android. Filtrez output-ul cu `grep` pentru a vedea doar mesajele legate de aplicația noastră sau `MainActivity`. Pot folosi și `adb logcat *:S MainActivity:D` pentru a vedea doar log-urile de debug din MainActivity."

**Alternativă (Windows PowerShell):**
```powershell
adb logcat | Select-String -Pattern "hello_world|MainActivity"
```

**Checkpoint:** Logurile se afișează în terminal, arătând activitatea aplicației și eventualele mesaje de log dacă am adăugat `Log.d()` în cod.

---

## Rezumat

Am creat o aplicație Android minimală "Hello World" care demonstrează:
- Structura de bază a unui proiect Android cu Gradle
- Configurarea fișierelor de build (`build.gradle`, `settings.gradle`, `gradle.properties`)
- Declararea activității în `AndroidManifest.xml`
- Crearea interfeței programatic în Java folosind `LinearLayout` și `TextView`
- Compilarea, instalarea și rularea aplicației din linia de comandă

Aplicația este funcțională și gata pentru dezvoltare ulterioară.

