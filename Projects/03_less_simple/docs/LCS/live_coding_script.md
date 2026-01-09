# Script Live Coding - Aplicație cu ViewModel și LiveData

## Prezentare Aplicație

Această aplicație Android extinde aplicațiile anterioare prin introducerea pattern-ului arhitectural ViewModel și a componentelor LiveData pentru gestionarea datelor legate de UI. Diferențele arhitecturale principale față de aplicațiile `01_hello_world`, `01_simple` și `02_simple` sunt: separarea logicii de business de activitate prin introducerea clasei `MainViewModel` care extinde `ViewModel`, utilizarea `LiveData` și `MutableLiveData` pentru date observabile care actualizează automat UI-ul, și pattern-ul Observer pentru comunicarea între ViewModel și Activity. Aplicația demonstrează arhitectura recomandată de Google: logica de business și datele sunt în ViewModel (care supraviețuiește schimbărilor de configurație precum rotația ecranului), iar Activity-ul observă schimbările prin `observe()`. În loc de `runOnUiThread()`, folosim `MutableLiveData.postValue()` care actualizează automat UI-ul pe thread-ul principal. Fluxul de date este: utilizatorul apasă "Refresh Joke" → Activity apelează `mViewModel.fetchJoke()` → ViewModel face apelul HTTP asincron → răspunsul JSON este parsat → `mText.postValue()` actualizează LiveData → Observer-ul din Activity actualizează automat TextView-ul. Aplicația nu folosește Parcelable și nu are navigare între activități multiple.

## Structura Directorului Aplicației

```
akrilki_03/
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/
│           │   └── ro/
│           │       └── makore/
│           │           └── akrilki_03/
│           │               ├── MainActivity.java
│           │               └── MainViewModel.java
│           └── res/
│               └── layout/
│                   └── activity_main.xml
├── build.gradle
├── gradle.properties
└── settings.gradle
```

## Pași Live Coding

### Pasul 1: Crearea structurii de directoare

**Ce fac:** Creez structura completă de directoare pentru aplicația Android, inclusiv directorul pentru clasele Java.

**Ce scriu în terminal:**
```bash
mkdir -p akrilki_03/app/src/main/java/ro/makore/akrilki_03
mkdir -p akrilki_03/app/src/main/res/layout
```

**Ce spun:** "Vom începe prin a crea structura de directoare pentru aplicația noastră. Această aplicație va introduce pattern-ul ViewModel, deci vom avea două clase Java: `MainActivity` și `MainViewModel`."

**Checkpoint:** Structura de directoare este creată.

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

rootProject.name = "akrilki_03"
include ':app'
```

**Ce spun:** "Creez fișierul `settings.gradle` cu configurația standard. Numele proiectului este `akrilki_03`."

**Checkpoint:** Fișierul `settings.gradle` este creat.

---

### Pasul 3: Crearea fișierului build.gradle la rădăcină

**Ce fac:** Creez fișierul `build.gradle` la nivelul rădăcină.

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

**Ce spun:** "Creez fișierul `build.gradle` la rădăcină cu configurația pentru plugin-ul Gradle Android."

**Checkpoint:** Fișierul `build.gradle` rădăcină este creat.

---

### Pasul 4: Crearea fișierului gradle.properties

**Ce fac:** Creez fișierul `gradle.properties` pentru configurații globale.

**Ce scriu:**
```
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.nonTransitiveRClass=true
```

**Ce spun:** "Creez fișierul `gradle.properties` cu setările standard pentru memorie JVM și AndroidX."

**Checkpoint:** Fișierul `gradle.properties` este creat.

---

### Pasul 5: Crearea fișierului app/build.gradle

**Ce fac:** Creez fișierul `build.gradle` pentru modulul `app` cu dependențele necesare pentru ViewModel și LiveData.

**Ce scriu:**
```groovy
apply plugin: 'com.android.application'

android {
    namespace 'ro.makore.akrilki_03'
    compileSdk 34

    defaultConfig {
        applicationId "ro.makore.akrilki_03"
        minSdk 21
        targetSdk 34
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
```

**Ce spun:** "Creez fișierul `app/build.gradle`. Setez namespace-ul la `ro.makore.akrilki_03` și `minSdk` la 21."

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

**Ce spun:** "Configurez tipurile de build și opțiunile de compilare Java."

**Ce scriu (continuare):**
```groovy
dependencies {
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.6.0'
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'com.google.android.material:material:1.12.0'
    implementation 'androidx.activity:activity:1.8.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.2.0'
    implementation 'com.squareup.okhttp3:okhttp:4.10.0'
}
```

**Ce spun:** "În blocul `dependencies` adaug bibliotecile necesare. Observați noile dependențe: `lifecycle-viewmodel-ktx:2.6.0` pentru ViewModel și `lifecycle-livedata-ktx:2.6.0` pentru LiveData. Acestea sunt componentele cheie ale arhitecturii recomandate de Google. De asemenea, păstrăm OkHttp pentru apeluri HTTP și Material Design pentru interfață."

**Checkpoint:** Fișierul `app/build.gradle` este complet configurat, inclusiv dependențele pentru ViewModel și LiveData.

---

### Pasul 6: Crearea fișierului AndroidManifest.xml

**Ce fac:** Creez fișierul `AndroidManifest.xml` cu permisiunea INTERNET necesară pentru apeluri HTTP.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    <uses-permission android:name="android.permission.INTERNET" />
```

**Ce spun:** "Creez fișierul `AndroidManifest.xml` în `app/src/main/`. Adaug permisiunea `INTERNET` necesară pentru apeluri HTTP către API-ul extern."

**Ce scriu (continuare):**
```xml
    <application
        android:label="Alkrilki03"
        android:theme="@style/Theme.Material3.DayNight.NoActionBar"
        tools:targetApi="31">
```

**Ce spun:** "În tag-ul `application` setez label-ul aplicației și tema Material3 care se adaptează la modul întunecat sau luminos."

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

**Ce spun:** "Declar activitatea `MainActivity` ca activitate principală cu `intent-filter` pentru `MAIN` și `LAUNCHER`."

**Checkpoint:** Fișierul `AndroidManifest.xml` este creat cu permisiunea INTERNET și tema Material3.

---

### Pasul 7: Crearea fișierului activity_main.xml

**Ce fac:** Creez fișierul XML de layout cu TextView pentru glumă și două butoane.

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

**Ce spun:** "Creez fișierul `activity_main.xml` în directorul `app/src/main/res/layout/`. Folosesc `ConstraintLayout` ca layout principal."

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
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@+id/refreshButton"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:padding="16dp"
        android:gravity="center"
        android:layout_marginTop="16dp"
        android:layout_marginBottom="16dp"/>
```

**Ce spun:** "Adaug un `TextView` cu ID-ul `jokeTextView` pentru afișarea glumei. Textul inițial este 'Joke will appear here' și va fi actualizat automat când LiveData din ViewModel se schimbă."

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

**Ce spun:** "Adaug butonul 'Refresh Joke' cu ID-ul `refreshButton`. Acest buton va declanșa apelarea metodei `fetchJoke()` din ViewModel."

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

**Ce spun:** "Adaug butonul 'Quit' cu ID-ul `quitButton`, fixat în partea de jos a ecranului."

**Ce scriu (continuare):**
```xml
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Ce spun:** "Închid tag-ul `ConstraintLayout`. Layout-ul este complet: TextView pentru glumă sus, buton Refresh Joke în mijloc, și buton Quit jos."

**Checkpoint:** Fișierul `activity_main.xml` este creat cu TextView și două butoane.

---

### Pasul 8: Crearea clasei MainViewModel.java - Partea 1: Imports și Declarații

**Ce fac:** Creez clasa `MainViewModel` care va gestiona logica de business și datele aplicației.

**Ce scriu:**
```java
package ro.makore.akrilki_03;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;
```

**Ce spun:** "Creez fișierul `MainViewModel.java` în directorul `app/src/main/java/ro/makore/akrilki_03/`. Declar pachetul și adaug import-urile pentru ViewModel și LiveData: `ViewModel` este clasa de bază pentru ViewModel-uri, `LiveData` este o clasă observabilă read-only, iar `MutableLiveData` este versiunea mutabilă care permite modificarea valorii. `Log` este pentru logging."

**Ce scriu (continuare):**
```java
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
```

**Ce spun:** "Adaug import-urile pentru procesarea JSON: `JSONObject` pentru parsarea răspunsului JSON, `JSONException` pentru gestionarea erorilor, și `IOException` pentru erorile de rețea."

**Ce scriu (continuare):**
```java
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
```

**Ce spun:** "Adaug import-urile pentru OkHttp pentru apeluri HTTP asincrone."

**Ce scriu (continuare):**
```java
public class MainViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private OkHttpClient client;
```

**Ce spun:** "Definesc clasa `MainViewModel` care extinde `ViewModel`. Aceasta este diferența arhitecturală principală față de aplicațiile anterioare! ViewModel supraviețuiește schimbărilor de configurație precum rotația ecranului. Declar `mText` ca `MutableLiveData<String>` - aceasta va stoca gluma și va notifica automat observatorii când se schimbă. `final` înseamnă că referința nu se poate schimba după inițializare, dar conținutul `MutableLiveData` poate fi modificat. `OkHttpClient` va fi folosit pentru apeluri HTTP."

**Checkpoint:** Clasa `MainViewModel` este declarată cu toate import-urile și atributele necesare.

---

### Pasul 9: Crearea clasei MainViewModel.java - Partea 2: Constructor și Metoda fetchJoke

**Ce scriu (continuare):**
```java
    public MainViewModel() {
        mText = new MutableLiveData<>();
        fetchJoke();
    }
```

**Ce spun:** "Creez constructorul `MainViewModel`. Inițializez `mText` cu o nouă instanță de `MutableLiveData<>()` - diamond operator-ul `<>` permite type inference, deci nu trebuie să specificăm `<String>` din nou. Apelez imediat `fetchJoke()` pentru a prelua prima glumă când ViewModel-ul este creat."

**Ce scriu (continuare):**
```java
    public void fetchJoke() {
        String url = "https://official-joke-api.appspot.com/random_joke";
```

**Ce spun:** "Creez metoda publică `fetchJoke()` care va prelua gluma de la API. Prima linie definește URL-ul API-ului."

**Ce scriu (continuare):**
```java
        Request request = new Request.Builder()
                .url(url)
                .build();

        client = new OkHttpClient();
```

**Ce spun:** "Construiesc un obiect `Request` folosind pattern-ul Builder și inițializez clientul OkHttp."

**Ce scriu (continuare):**
```java
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MainActivity", "Failed to fetch joke", e);
                mText.postValue("Failed to load joke." + e.getMessage());
            }
```

**Ce spun:** "Execut apelul HTTP asincron cu `enqueue()`. În `onFailure`, loghez eroarea și folosesc `mText.postValue()` pentru a actualiza LiveData. `postValue()` este esențială aici - poate fi apelată din orice thread și va actualiza automat UI-ul pe thread-ul principal. Aceasta înlocuiește `runOnUiThread()` din aplicația anterioară!"

**Ce scriu (continuare):**
```java
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mText.postValue("Failed to load joke.");
                    return;
                }
```

**Ce spun:** "Implementez metoda `onResponse` care se execută când primim un răspuns. Verific dacă răspunsul este de succes și folosesc din nou `postValue()` pentru a actualiza LiveData."

**Ce scriu (continuare):**
```java
                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    String setup = json.getString("setup");
                    String punchline = json.getString("punchline");

                    String joke = setup + "\n\n" + punchline;

                    // Update the UI
                    mText.postValue(joke);
```

**Ce spun:** "Parsez răspunsul JSON: citesc corpul răspunsului, creez un obiect JSON, extrag `setup` și `punchline`, le concatenez, și actualizez LiveData cu `postValue()`. Observați că nu mai folosim `runOnUiThread()` - `postValue()` gestionează automat thread-ul corect!"

**Ce scriu (continuare):**
```java
                } catch (JSONException e) {
                    Log.e("MainActivity", "Failed to parse joke JSON", e);
                    mText.postValue("Failed to parse joke." + e.getMessage());
                }
            }
        });
    }
```

**Ce spun:** "Blocul `catch` prinde excepțiile `JSONException` și actualizează LiveData cu un mesaj de eroare. Închid callback-ul și metoda `fetchJoke()`."

**Checkpoint:** Metoda `fetchJoke()` este completă și folosește `postValue()` pentru actualizarea LiveData.

---

### Pasul 10: Crearea clasei MainViewModel.java - Partea 3: Metoda getText

**Ce scriu (continuare):**
```java
    public LiveData<String> getText() {
        return mText;
    }
}
```

**Ce spun:** "Creez metoda publică `getText()` care returnează `LiveData<String>` în loc de `MutableLiveData<String>`. Aceasta este o practică bună de securitate - expunem doar interfața read-only `LiveData` către Activity, păstrând `MutableLiveData` privat. Activity-ul poate observa schimbările, dar nu poate modifica direct datele. Închid clasa `MainViewModel`."

**Checkpoint:** Clasa `MainViewModel` este completă cu toate metodele necesare.

---

### Pasul 11: Crearea clasei MainActivity.java - Partea 1: Imports și Declarații

**Ce fac:** Creez clasa `MainActivity` care va observa LiveData din ViewModel.

**Ce scriu:**
```java
package ro.makore.akrilki_03;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
```

**Ce spun:** "Creez fișierul `MainActivity.java` în directorul `app/src/main/java/ro/makore/akrilki_03/`. Declar pachetul și adaug import-urile: `Bundle` pentru stare, componentele UI, `AppCompatActivity` ca clasă de bază, și `ViewModelProvider` pentru a obține instanța ViewModel-ului. `ViewModelProvider` gestionează lifecycle-ul ViewModel-ului și asigură că aceeași instanță este returnată chiar dacă Activity-ul este recreat."

**Ce scriu (continuare):**
```java
public class MainActivity extends AppCompatActivity {

    private MainViewModel mViewModel;
    private TextView jokeTextView;
    private Button refreshButton;
```

**Ce spun:** "Definesc clasa `MainActivity` care extinde `AppCompatActivity`. Declar referințe: `mViewModel` pentru ViewModel-ul nostru, și componentele UI. Observați că logica de business nu mai este în Activity - este mutată în ViewModel!"

**Checkpoint:** Clasa `MainActivity` este declarată cu toate import-urile și atributele necesare.

---

### Pasul 12: Crearea clasei MainActivity.java - Partea 2: Metoda onCreate

**Ce scriu (continuare):**
```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
```

**Ce spun:** "Suprascriu metoda `onCreate` și încarc layout-ul din XML."

**Ce scriu (continuare):**
```java
        // Initialize the ViewModel
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
```

**Ce spun:** "Această linie este crucială! `new ViewModelProvider(this)` creează un provider care este legat de lifecycle-ul Activity-ului. `get(MainViewModel.class)` returnează instanța ViewModel-ului: dacă nu există, o creează; dacă există deja (de exemplu, după rotația ecranului), returnează aceeași instanță. Aceasta este magia ViewModel-ului - datele supraviețuiesc recreării Activity-ului!"

**Ce scriu (continuare):**
```java
        jokeTextView = findViewById(R.id.jokeTextView);
        refreshButton = findViewById(R.id.refreshButton);

        Button quitButton = findViewById(R.id.quitButton);
        quitButton.setOnClickListener(v -> finishAffinity());
```

**Ce spun:** "Obțin referințe la componentele UI folosind `findViewById()` și atașez listener pentru butonul Quit."

**Ce scriu (continuare):**
```java
        // Observe LiveData from the ViewModel
        mViewModel.getText().observe(this, joke -> {
            jokeTextView.setText(joke);
        });
```

**Ce spun:** "Această linie implementează pattern-ul Observer! `mViewModel.getText()` returnează `LiveData<String>`, iar `observe(this, joke -> {...})` înregistrează un observator. Când valoarea din LiveData se schimbă (prin `postValue()` în ViewModel), lambda-ul se execută automat pe thread-ul principal și actualizează TextView-ul. `this` este `LifecycleOwner` - Activity-ul nostru - care permite LiveData să știe când să activeze sau dezactiveze observatorul în funcție de lifecycle."

**Ce scriu (continuare):**
```java
        // Refresh the joke when the button is clicked
        refreshButton.setOnClickListener(v -> mViewModel.fetchJoke());
```

**Ce spun:** "Atașez listener pentru butonul Refresh Joke care apelează `mViewModel.fetchJoke()`. Observați că logica de fetch este în ViewModel, nu în Activity!"

**Ce scriu (continuare):**
```java
    }
}
```

**Ce spun:** "Închid metoda `onCreate` și clasa `MainActivity`. Activity-ul este mult mai simplu acum - doar observă datele din ViewModel și gestionează interacțiunile UI. Toată logica de business este în ViewModel!"

**Checkpoint:** Clasa `MainActivity` este completă și folosește pattern-ul Observer pentru a observa LiveData din ViewModel.

---

### Pasul 13: Verificarea structurii proiectului

**Ce fac:** Verific că toate fișierele necesare sunt prezente.

**Ce spun:** "Am creat toate fișierele necesare pentru aplicația cu ViewModel și LiveData. Aplicația demonstrează: separarea logicii de business în ViewModel, utilizarea LiveData pentru date observabile, pattern-ul Observer pentru comunicare, și supraviețuirea datelor la schimbări de configurație. Aplicația nu folosește Parcelable, deci nu avem clase Parcelable de creat."

**Checkpoint:** Toate fișierele sunt create și structura proiectului este completă, cu arhitectura ViewModel implementată corect.

---

## Compilare, Instalare și Rulare

### Compilarea aplicației

**Ce fac:** Compilez aplicația pentru a genera APK-ul de debug.

**Ce scriu în terminal:**
```bash
cd akrilki_03
gradle build
```

**Ce spun:** "Navighez în directorul proiectului și rulez `gradle build` pentru a compila aplicația. Gradle va descărca automat dependențele pentru ViewModel și LiveData și va compila codul. APK-ul va fi generat în `app/build/outputs/apk/debug/app-debug.apk`."

**Checkpoint:** Build-ul se finalizează cu succes, APK-ul este generat, și dependențele pentru ViewModel și LiveData sunt incluse.

---

### Listarea dispozitivelor conectate

**Ce fac:** Verific ce dispozitive Android sunt conectate și disponibile pentru instalare.

**Ce scriu în terminal:**
```bash
adb devices
```

**Ce spun:** "Folosesc `adb devices` pentru a lista toate dispozitivele Android conectate. Asigurați-vă că dispozitivul are conexiune la internet pentru ca aplicația să funcționeze corect."

**Checkpoint:** Se afișează lista de dispozitive cu status 'device'.

---

### Instalarea aplicației

**Ce fac:** Instalez APK-ul de debug pe dispozitivul conectat.

**Ce scriu în terminal:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Ce spun:** "Folosesc `adb install` pentru a instala APK-ul de debug pe dispozitiv. Dacă aplicația există deja, pot folosi `adb install -r` pentru reinstalare."

**Checkpoint:** Aplicația este instalată cu succes.

---

### Lansarea activității principale

**Ce fac:** Lansez activitatea principală a aplicației pe dispozitiv.

**Ce scriu în terminal:**
```bash
adb shell am start -n ro.makore.akrilki_03/.MainActivity
```

**Ce spun:** "Folosesc `adb shell am start` pentru a lansa activitatea. Aplicația se va deschide și ViewModel-ul va face automat un apel API pentru a prelua prima glumă. Dacă dispozitivul are conexiune la internet, ar trebui să vedeți gluma afișată în câteva secunde. Testați rotația ecranului - gluma ar trebui să rămână, demonstrând că ViewModel-ul supraviețuiește recreării Activity-ului!"

**Checkpoint:** Aplicația se deschide, se face apelul API, și gluma este afișată în TextView. La rotația ecranului, gluma rămâne (ViewModel supraviețuiește). La apăsarea butonului "Refresh Joke", se preia o nouă glumă. La apăsarea butonului "Quit", aplicația se închide.

---

### Afișarea logurilor filtrate

**Ce fac:** Monitorizez logurile aplicației pentru a verifica apelurile HTTP și lifecycle-ul ViewModel-ului.

**Ce scriu în terminal:**
```bash
adb logcat | grep -i "akrilki_03\|MainActivity\|MainViewModel\|OkHttp"
```

**Ce spun:** "Folosesc `adb logcat` pentru a afișa logurile sistemului Android. Filtrez output-ul pentru a vedea mesajele legate de aplicația noastră, MainActivity, MainViewModel, și OkHttp. Aici putem vedea apelurile HTTP, răspunsurile, și eventualele erori. De asemenea, putem observa că ViewModel-ul nu este recreat la rotația ecranului."

**Alternativă (Windows PowerShell):**
```powershell
adb logcat | Select-String -Pattern "akrilki_03|MainActivity|MainViewModel|OkHttp"
```

**Checkpoint:** Logurile se afișează în terminal, arătând apelurile HTTP, răspunsurile JSON, și eventualele erori. Când rotim ecranul, putem observa că ViewModel-ul nu este recreat (nu apare constructorul în loguri), demonstrând supraviețuirea datelor.

---

## Rezumat

Am creat o aplicație Android care folosește pattern-ul arhitectural ViewModel și componentele LiveData pentru gestionarea datelor. Aplicația demonstrează:
- Separarea logicii de business în ViewModel (care extinde `ViewModel`)
- Utilizarea `LiveData` și `MutableLiveData` pentru date observabile
- Pattern-ul Observer pentru comunicarea între ViewModel și Activity
- `ViewModelProvider` pentru gestionarea lifecycle-ului ViewModel-ului
- `MutableLiveData.postValue()` pentru actualizarea UI-ului din thread-uri secundare
- Supraviețuirea datelor la schimbări de configurație (rotația ecranului)
- Apeluri HTTP asincrone cu OkHttp și parsare JSON
- Permisiunea INTERNET în manifest
- Tema Material3 pentru interfață modernă
- Structura de bază a unui proiect Android cu Gradle
- Compilarea, instalarea și rularea aplicației din linia de comandă

**Diferențele arhitecturale față de `01_hello_world`, `01_simple` și `02_simple`:**
- Introducerea pattern-ului ViewModel pentru separarea logicii de business
- Utilizarea LiveData pentru date observabile și actualizare automată a UI-ului
- Pattern-ul Observer (`observe()`) pentru comunicarea între ViewModel și Activity
- `MutableLiveData.postValue()` în loc de `runOnUiThread()` pentru actualizarea UI-ului
- `ViewModelProvider` pentru gestionarea lifecycle-ului ViewModel-ului
- Supraviețuirea datelor la schimbări de configurație (rotația ecranului nu mai reface apelul API)
- Arhitectură recomandată de Google (Android Architecture Components)

Aplicația este funcțională și demonstrează conceptele fundamentale ale arhitecturii recomandate pentru aplicații Android moderne.

