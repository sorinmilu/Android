# Script Live Coding - Aplicație cu Interacțiune API

<!-- TOC -->

- [Script Live Coding - Aplicație cu Interacțiune API](#script-live-coding---aplica%C8%9Bie-cu-interac%C8%9Biune-api)
    - [Prezentare Aplicație](#prezentare-aplica%C8%9Bie)
    - [Structura Directorului Aplicației](#structura-directorului-aplica%C8%9Biei)
    - [Pași Live Coding](#pa%C8%99i-live-coding)
        - [Pasul 1: Crearea structurii de directoare](#pasul-1-crearea-structurii-de-directoare)
        - [Pasul 2: Crearea fișierului settings.gradle](#pasul-2-crearea-fi%C8%99ierului-settingsgradle)
        - [Pasul 3: Crearea fișierului build.gradle la rădăcină](#pasul-3-crearea-fi%C8%99ierului-buildgradle-la-r%C4%83d%C4%83cin%C4%83)
        - [Pasul 4: Crearea fișierului gradle.properties](#pasul-4-crearea-fi%C8%99ierului-gradleproperties)
        - [Pasul 5: Crearea fișierului app/build.gradle](#pasul-5-crearea-fi%C8%99ierului-appbuildgradle)
        - [Pasul 6: Crearea fișierului AndroidManifest.xml](#pasul-6-crearea-fi%C8%99ierului-androidmanifestxml)
        - [Pasul 7: Crearea fișierului activity_main.xml](#pasul-7-crearea-fi%C8%99ierului-activity_mainxml)
        - [Pasul 8: Crearea clasei MainActivity.java - Partea 1: Imports și Declarații](#pasul-8-crearea-clasei-mainactivityjava---partea-1-imports-%C8%99i-declara%C8%9Bii)
        - [Pasul 9: Crearea clasei MainActivity.java - Partea 2: Metoda onCreate](#pasul-9-crearea-clasei-mainactivityjava---partea-2-metoda-oncreate)
        - [Pasul 10: Crearea clasei MainActivity.java - Partea 3: Metoda fetchJoke](#pasul-10-crearea-clasei-mainactivityjava---partea-3-metoda-fetchjoke)
        - [Pasul 11: Verificarea structurii proiectului](#pasul-11-verificarea-structurii-proiectului)
    - [Compilare, Instalare și Rulare](#compilare-instalare-%C8%99i-rulare)
        - [Compilarea aplicației](#compilarea-aplica%C8%9Biei)
        - [Listarea dispozitivelor conectate](#listarea-dispozitivelor-conectate)
        - [Instalarea aplicației](#instalarea-aplica%C8%9Biei)
        - [Lansarea activității principale](#lansarea-activit%C4%83%C8%9Bii-principale)
        - [Afișarea logurilor filtrate](#afi%C8%99area-logurilor-filtrate)
    - [Rezumat](#rezumat)

<!-- /TOC -->

## Prezentare Aplicație

Această aplicație Android extinde aplicațiile anterioare prin introducerea comunicării cu un API extern pentru a prelua și afișa glume în format JSON. Diferențele arhitecturale principale față de aplicațiile `01_hello_world` și `01_simple` sunt: introducerea operațiunilor de rețea asincrone folosind biblioteca OkHttp, procesarea răspunsurilor JSON, și pattern-ul `runOnUiThread()` pentru actualizarea interfeței utilizatorului din thread-uri de background. Aplicația demonstrează programarea asincronă în Android: apelurile HTTP se execută pe thread-uri secundare, iar actualizarea UI-ului se face pe thread-ul principal folosind `runOnUiThread()`. Aplicația necesită permisiunea `INTERNET` în manifest și folosește tema Material3. Fluxul de date este: utilizatorul apasă butonul "Refresh Joke" → se face un request HTTP asincron către API → răspunsul JSON este parsat → UI-ul este actualizat pe thread-ul principal. Aplicația nu folosește Parcelable și nu are navigare între activități multiple.

## Structura Directorului Aplicației

```
akrilki_02/
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/
│           │   └── ro/
│           │       └── makore/
│           │           └── akrilki_02/
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

**Actiuni:** Creez structura completă de directoare pentru aplicația Android.

**Ce scriu în terminal:**
```bash
mkdir -p akrilki_02/app/src/main/java/ro/makore/akrilki_02
mkdir -p akrilki_02/app/src/main/res/layout
```

**Note:** "Vom începe prin a crea structura de directoare pentru aplicația noastră. Această aplicație va comunica cu un API extern, deci va avea nevoie de permisiuni de internet și biblioteci pentru apeluri HTTP."

**Checkpoint:** Structura de directoare este creată.

---

### Pasul 2: Crearea fișierului settings.gradle

**Actiuni:** Creez fișierul `settings.gradle` la rădăcina proiectului pentru configurarea modulelor Gradle.

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

rootProject.name = "akrilki_02"
include ':app'
```

**Note:** "Creez fișierul `settings.gradle` cu configurația standard. Numele proiectului este `akrilki_02`."

**Checkpoint:** Fișierul `settings.gradle` este creat.

---

### Pasul 3: Crearea fișierului build.gradle la rădăcină

**Actiuni:** Creez fișierul `build.gradle` la nivelul rădăcină.

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

**Note:** "Creez fișierul `build.gradle` la rădăcină cu configurația pentru plugin-ul Gradle Android."

**Checkpoint:** Fișierul `build.gradle` rădăcină este creat.

---

### Pasul 4: Crearea fișierului gradle.properties

**Actiuni:** Creez fișierul `gradle.properties` pentru configurații globale.

**Ce scriu:**
```
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.nonTransitiveRClass=true
```

**Note:** "Creez fișierul `gradle.properties` cu setările standard pentru memorie JVM și AndroidX."

**Checkpoint:** Fișierul `gradle.properties` este creat.

---

### Pasul 5: Crearea fișierului app/build.gradle

**Actiuni:** Creez fișierul `build.gradle` pentru modulul `app` cu dependențele necesare pentru apeluri HTTP.

**Ce scriu:**
```groovy
apply plugin: 'com.android.application'

android {
    namespace 'ro.makore.akrilki_02'
    compileSdk 34

    defaultConfig {
        applicationId "ro.makore.akrilki_02"
        minSdk 21
        targetSdk 34
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
```

**Note:** "Creez fișierul `app/build.gradle`. Setez namespace-ul la `ro.makore.akrilki_02` și `minSdk` la 21 pentru suport pe dispozitive mai vechi."

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

**Note:** "Configurez tipurile de build și opțiunile de compilare Java."

**Ce scriu (continuare):**
```groovy
dependencies {
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'com.google.android.material:material:1.12.0'
    implementation 'androidx.activity:activity:1.8.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.2.0'
    implementation 'com.squareup.okhttp3:okhttp:4.10.0'
}
```

**Note:** "În blocul `dependencies` adaug bibliotecile necesare. Observați dependența nouă `okhttp:4.10.0` - aceasta este biblioteca pentru apeluri HTTP asincrone. De asemenea, adaug `material:1.12.0` pentru tema Material3 și `activity:1.8.0` pentru suport modern al activităților."

**Checkpoint:** Fișierul `app/build.gradle` este complet configurat, inclusiv dependența OkHttp pentru apeluri HTTP.

---

### Pasul 6: Crearea fișierului AndroidManifest.xml

**Actiuni:** Creez fișierul `AndroidManifest.xml` cu permisiunea INTERNET necesară pentru apeluri HTTP.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    <uses-permission android:name="android.permission.INTERNET" />
```

**Note:** "Creez fișierul `AndroidManifest.xml` în `app/src/main/`. Prima linie importantă este `uses-permission` pentru `INTERNET` - această permisiune este obligatorie pentru a face apeluri HTTP către servere externe. Fără această permisiune, aplicația nu va putea accesa rețeaua."

**Ce scriu (continuare):**
```xml
    <application
        android:label="Alkrilki02"
        android:theme="@style/Theme.Material3.DayNight.NoActionBar"
        tools:targetApi="31">
```

**Note:** "În tag-ul `application` setez label-ul aplicației și tema la `Theme.Material3.DayNight.NoActionBar` - aceasta este tema Material Design 3 care se adaptează automat la modul întunecat sau luminos al sistemului."

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

**Note:** "Declar activitatea `MainActivity` ca activitate principală cu `intent-filter` pentru `MAIN` și `LAUNCHER`."

**Checkpoint:** Fișierul `AndroidManifest.xml` este creat cu permisiunea INTERNET și tema Material3.

---

### Pasul 7: Crearea fișierului activity_main.xml

**Actiuni:** Creez fișierul XML de layout cu TextView pentru glumă și două butoane.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
```

**Note:** "Creez fișierul `activity_main.xml` în directorul `app/src/main/res/layout/`. Folosesc `ConstraintLayout` ca layout principal pentru poziționare flexibilă."

**Ce scriu (continuare):**
```xml
    <!-- TextView to display the joke -->
    <TextView
        android:id="@+id/jokeTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Joke will appear here"
        android:textSize="24sp"
        android:textAlignment="center"
```

**Note:** "Adaug un `TextView` cu ID-ul `jokeTextView` pentru afișarea glumei. Observați că `layout_width="0dp"` în ConstraintLayout înseamnă că va ocupa tot spațiul disponibil între constrângeri. Textul inițial este 'Joke will appear here' și va fi înlocuit când se preia gluma de la API."

**Ce scriu (continuare):**
```xml
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@+id/refreshButton"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:padding="16dp"
        android:gravity="center"
        android:layout_marginTop="16dp"
        android:layout_marginBottom="16dp"/>
```

**Note:** "Setez constrângerile pentru TextView: fixat sus de parent, jos deasupra butonului de refresh, și centrat orizontal. Adaug padding și margin pentru spațiere."

**Ce scriu (continuare):**
```xml
    <!-- Refresh Joke Button -->
    <Button
        android:id="@+id/refreshButton"
        android:layout_width="407dp"
        android:layout_height="97dp"
        android:text="Refresh Joke"
        android:textSize="18sp"
        app:layout_constraintBottom_toTopOf="@+id/quitButton"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />
```

**Note:** "Adaug butonul 'Refresh Joke' cu ID-ul `refreshButton`. Acest buton va declanșa un nou apel API pentru a prelua o altă glumă."

**Ce scriu (continuare):**
```xml
    <!-- Quit Button -->
    <Button
        android:id="@+id/quitButton"
        android:layout_width="404dp"
        android:layout_height="98dp"
        android:text="Quit"
        android:textSize="18sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />
```

**Note:** "Adaug butonul 'Quit' cu ID-ul `quitButton`, fixat în partea de jos a ecranului."

**Ce scriu (continuare):**
```xml
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Note:** "Închid tag-ul `ConstraintLayout`. Layout-ul este complet: TextView pentru glumă sus, buton Refresh Joke în mijloc, și buton Quit jos."

**Checkpoint:** Fișierul `activity_main.xml` este creat cu TextView și două butoane.

---

### Pasul 8: Crearea clasei MainActivity.java - Partea 1: Imports și Declarații

**Actiuni:** Creez clasa Java principală care va gestiona apelurile HTTP și parsarea JSON.

**Ce scriu:**
```java
package ro.makore.akrilki_02;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
```

**Note:** "Creez fișierul `MainActivity.java` în directorul `app/src/main/java/ro/makore/akrilki_02/`. Declar pachetul și adaug import-urile de bază: `Bundle` pentru stare, `Log` pentru logging, `AppCompatActivity` ca clasă de bază, și componentele UI."

**Ce scriu (continuare):**
```java
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
```

**Note:** "Adaug import-urile pentru procesarea JSON: `JSONObject` pentru parsarea răspunsului JSON de la API, `JSONException` pentru gestionarea erorilor de parsare, și `IOException` pentru erorile de rețea."

**Ce scriu (continuare):**
```java
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
```

**Note:** "Adaug import-urile pentru OkHttp: `OkHttpClient` pentru clientul HTTP, `Request` pentru construirea cererilor, `Response` pentru răspunsuri, `Callback` pentru apeluri asincrone, și `Call` pentru reprezentarea unei cereri."

**Ce scriu (continuare):**
```java
public class MainActivity extends AppCompatActivity {

    private TextView jokeTextView;
    private Button refreshButton;
    private Button quitButton;
    private OkHttpClient client;
```

**Note:** "Definesc clasa `MainActivity` care extinde `AppCompatActivity`. Declar atributele private: referințe la componentele UI și `OkHttpClient` care va fi folosit pentru toate apelurile HTTP. Clientul este declarat ca atribut de clasă pentru a putea fi reutilizat."

**Checkpoint:** Clasa este declarată cu toate import-urile și atributele necesare.

---

### Pasul 9: Crearea clasei MainActivity.java - Partea 2: Metoda onCreate

**Ce scriu (continuare):**
```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
```

**Note:** "Suprascriu metoda `onCreate` și încarc layout-ul din XML folosind `setContentView(R.layout.activity_main)`."

**Ce scriu (continuare):**
```java
        client = new OkHttpClient();
```

**Note:** "Inițializez clientul OkHttp. Acest client va gestiona toate conexiunile HTTP. Este thread-safe și poate fi reutilizat pentru multiple cereri."

**Ce scriu (continuare):**
```java
        jokeTextView = findViewById(R.id.jokeTextView);
        refreshButton = findViewById(R.id.refreshButton);
        quitButton = findViewById(R.id.quitButton);
```

**Note:** "Obțin referințe la componentele UI folosind `findViewById()`. Aceste referințe vor fi folosite pentru a actualiza UI-ul și pentru a atașa listeneri de evenimente."

**Ce scriu (continuare):**
```java
        // fetch the first joke    
        fetchJoke();
```

**Note:** "Apelez metoda `fetchJoke()` imediat după inițializare pentru a prelua prima glumă când aplicația se deschide."

**Ce scriu (continuare):**
```java
        quitButton.setOnClickListener(v -> finishAffinity());
        refreshButton.setOnClickListener(v -> fetchJoke());
```

**Note:** "Atașez listeneri de evenimente: butonul Quit închide aplicația cu `finishAffinity()`, iar butonul Refresh Joke apelează din nou `fetchJoke()` pentru a prelua o nouă glumă."

**Ce scriu (continuare):**
```java
    }
```

**Note:** "Închid metoda `onCreate`. Acum trebuie să implementăm metoda `fetchJoke()` care va face apelul HTTP."

**Checkpoint:** Metoda `onCreate` este completă și inițializează toate componentele.

---

### Pasul 10: Crearea clasei MainActivity.java - Partea 3: Metoda fetchJoke

**Ce scriu (continuare):**
```java
    private void fetchJoke() {
        String url = "https://official-joke-api.appspot.com/random_joke";
```

**Note:** "Creez metoda privată `fetchJoke()` care va prelua gluma de la API. Prima linie definește URL-ul API-ului care returnează glume aleatoare în format JSON."

**Ce scriu (continuare):**
```java
        Request request = new Request.Builder()
                .url(url)
                .build();
```

**Note:** "Construiesc un obiect `Request` folosind pattern-ul Builder. Setez URL-ul și construiesc cererea. Aceasta este o cerere GET simplă - OkHttp folosește GET ca metodă implicită."

**Ce scriu (continuare):**
```java
        client.newCall(request).enqueue(new Callback() {
```

**Note:** "Această linie este crucială! `client.newCall(request)` creează un apel HTTP, iar `enqueue()` îl execută asincron pe un thread secundar. Nu blocăm thread-ul principal! `Callback` este o interfață cu două metode: `onFailure` pentru erori și `onResponse` pentru răspunsuri de succes."

**Ce scriu (continuare):**
```java
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MainActivity", "Failed to fetch joke", e);
                runOnUiThread(() -> jokeTextView.setText("Failed to load joke."));
            }
```

**Note:** "Implementez metoda `onFailure` care se execută când apelul HTTP eșuează - de exemplu, fără conexiune la internet. Folosesc `Log.e()` pentru a înregistra eroarea. Apoi folosesc `runOnUiThread()` - aceasta este esențială! Callback-ul se execută pe un thread secundar, dar actualizarea UI-ului trebuie să se facă pe thread-ul principal. `runOnUiThread()` execută lambda-ul pe thread-ul UI."

**Ce scriu (continuare):**
```java
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> jokeTextView.setText("Failed to load joke."));
                    return;
                }
```

**Note:** "Implementez metoda `onResponse` care se execută când primim un răspuns de la server. Verific dacă răspunsul este de succes folosind `response.isSuccessful()` - aceasta verifică dacă codul de stare HTTP este între 200 și 299. Dacă nu, afișez un mesaj de eroare și ies din metodă."

**Ce scriu (continuare):**
```java
                try {
                    String responseData = response.body().string();
```

**Note:** "Încep un bloc `try-catch` pentru a gestiona erorile de parsare JSON. `response.body().string()` citește întregul corp al răspunsului ca un string. Această metodă poate arunca `IOException`, de aceea este în blocul try."

**Ce scriu (continuare):**
```java
                    JSONObject json = new JSONObject(responseData);
                    String setup = json.getString("setup");
                    String punchline = json.getString("punchline");
```

**Note:** "Parsez string-ul JSON într-un obiect `JSONObject`. API-ul returnează un obiect JSON cu două câmpuri: `setup` (introducerea glumei) și `punchline` (rezultatul glumei). Extrag ambele folosind `getString()`."

**Ce scriu (continuare):**
```java
                    String joke = setup + "\n\n" + punchline;

                    // Update the UI
                    runOnUiThread(() -> jokeTextView.setText(joke));
```

**Note:** "Concatenez setup-ul și punchline-ul cu două linii noi între ele pentru formatare. Apoi folosesc din nou `runOnUiThread()` pentru a actualiza TextView-ul pe thread-ul principal. Fără `runOnUiThread()`, aplicația ar arunca o excepție `CalledFromWrongThreadException`."

**Ce scriu (continuare):**
```java
                } catch (JSONException e) {
                    Log.e("MainActivity", "Failed to parse joke JSON", e);
                    runOnUiThread(() -> jokeTextView.setText("Failed to load joke."));
                }
            }
        });
    }
}
```

**Note:** "Blocul `catch` prinde excepțiile `JSONException` care pot apărea dacă formatul JSON este invalid. Loghez eroarea și afișez un mesaj de eroare în UI. Închid callback-ul, metoda `fetchJoke()`, și clasa."

**Checkpoint:** Clasa `MainActivity` este completă cu apeluri HTTP asincrone, parsare JSON, și actualizare UI pe thread-ul principal.

---

### Pasul 11: Verificarea structurii proiectului

**Actiuni:** Verific că toate fișierele necesare sunt prezente.

**Note:** "Am creat toate fișierele necesare pentru aplicația cu interacțiune API. Aplicația demonstrează: apeluri HTTP asincrone cu OkHttp, parsare JSON, și pattern-ul `runOnUiThread()` pentru actualizarea UI-ului din thread-uri secundare. Aplicația nu folosește Parcelable, deci nu avem clase Parcelable de creat."

**Checkpoint:** Toate fișierele sunt create și structura proiectului este completă, cu toate dependențele și permisiunile necesare pentru apeluri HTTP.

---

## Compilare, Instalare și Rulare

### Compilarea aplicației

**Actiuni:** Compilez aplicația pentru a genera APK-ul de debug.

**Ce scriu în terminal:**
```bash
cd akrilki_02
gradle build
```

**Note:** "Navighez în directorul proiectului și rulez `gradle build` pentru a compila aplicația. Gradle va descărca automat dependența OkHttp și va compila codul. APK-ul va fi generat în `app/build/outputs/apk/debug/app-debug.apk`."

**Checkpoint:** Build-ul se finalizează cu succes, APK-ul este generat, și dependența OkHttp este inclusă.

---

### Listarea dispozitivelor conectate

**Actiuni:** Verific ce dispozitive Android sunt conectate și disponibile pentru instalare.

**Ce scriu în terminal:**
```bash
adb devices
```

**Note:** "Folosesc `adb devices` pentru a lista toate dispozitivele Android conectate. Asigurați-vă că dispozitivul are conexiune la internet pentru ca aplicația să funcționeze corect."

**Checkpoint:** Se afișează lista de dispozitive cu status 'device'.

---

### Instalarea aplicației

**Actiuni:** Instalez APK-ul de debug pe dispozitivul conectat.

**Ce scriu în terminal:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Note:** "Folosesc `adb install` pentru a instala APK-ul de debug pe dispozitiv. Dacă aplicația există deja, pot folosi `adb install -r` pentru reinstalare."

**Checkpoint:** Aplicația este instalată cu succes.

---

### Lansarea activității principale

**Actiuni:** Lansez activitatea principală a aplicației pe dispozitiv.

**Ce scriu în terminal:**
```bash
adb shell am start -n ro.makore.akrilki_02/.MainActivity
```

**Note:** "Folosesc `adb shell am start` pentru a lansa activitatea. Aplicația se va deschide și va face automat un apel API pentru a prelua prima glumă. Dacă dispozitivul are conexiune la internet, ar trebui să vedeți gluma afișată în câteva secunde."

**Checkpoint:** Aplicația se deschide, se face apelul API, și gluma este afișată în TextView. La apăsarea butonului "Refresh Joke", se preia o nouă glumă. La apăsarea butonului "Quit", aplicația se închide.

---

### Afișarea logurilor filtrate

**Actiuni:** Monitorizez logurile aplicației pentru a verifica apelurile HTTP și eventualele erori.

**Ce scriu în terminal:**
```bash
adb logcat | grep -i "akrilki_02\|MainActivity\|OkHttp"
```

**Note:** "Folosesc `adb logcat` pentru a afișa logurile sistemului Android. Filtrez output-ul pentru a vedea mesajele legate de aplicația noastră, MainActivity, și OkHttp. Aici putem vedea apelurile HTTP, răspunsurile, și eventualele erori de parsare JSON sau de rețea."

**Alternativă (Windows PowerShell):**
```powershell
adb logcat | Select-String -Pattern "akrilki_02|MainActivity|OkHttp"
```

**Checkpoint:** Logurile se afișează în terminal, arătând apelurile HTTP, răspunsurile JSON, și eventualele erori. Când apăsăm "Refresh Joke", putem vedea în loguri noul apel HTTP către API.

---

## Rezumat

Am creat o aplicație Android care interacționează cu un API extern pentru a prelua și afișa glume. Aplicația demonstrează:
- Apeluri HTTP asincrone folosind biblioteca OkHttp
- Procesarea răspunsurilor JSON cu `JSONObject`
- Pattern-ul `runOnUiThread()` pentru actualizarea UI-ului din thread-uri secundare
- Gestionarea erorilor de rețea și de parsare JSON
- Permisiunea INTERNET în manifest
- Tema Material3 pentru interfață modernă
- Structura de bază a unui proiect Android cu Gradle
- Compilarea, instalarea și rularea aplicației din linia de comandă

**Diferențele arhitecturale față de `01_hello_world` și `01_simple`:**
- Introducerea operațiunilor de rețea asincrone (OkHttp)
- Programare asincronă cu callbacks (`Callback` interface)
- Parsare JSON pentru procesarea răspunsurilor API
- Pattern-ul `runOnUiThread()` pentru sincronizarea thread-urilor
- Permisiunea INTERNET în manifest
- Dependențe externe (OkHttp, Material Design)
- Gestionarea erorilor de rețea și parsare

Aplicația este funcțională și demonstrează conceptele fundamentale de programare asincronă și comunicare cu API-uri externe în Android.


