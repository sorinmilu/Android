# Script Live Coding - Aplicație cu Trei Fragmente și Navigation Component

<!-- TOC -->

- [Script Live Coding - Aplicație cu Trei Fragmente și Navigation Component](#script-live-coding---aplica%C8%9Bie-cu-trei-fragmente-%C8%99i-navigation-component)
    - [Prezentare Aplicație](#prezentare-aplica%C8%9Bie)
    - [Structura Directorului Aplicației](#structura-directorului-aplica%C8%9Biei)
    - [Pași Live Coding](#pa%C8%99i-live-coding)
        - [Pasul 1: Crearea structurii de directoare](#pasul-1-crearea-structurii-de-directoare)
        - [Pasul 2: Crearea fișierului settings.gradle](#pasul-2-crearea-fi%C8%99ierului-settingsgradle)
        - [Pasul 3: Crearea fișierului build.gradle la rădăcină](#pasul-3-crearea-fi%C8%99ierului-buildgradle-la-r%C4%83d%C4%83cin%C4%83)
        - [Pasul 4: Crearea fișierului gradle.properties](#pasul-4-crearea-fi%C8%99ierului-gradleproperties)
        - [Pasul 5: Crearea fișierului app/build.gradle](#pasul-5-crearea-fi%C8%99ierului-appbuildgradle)
        - [Pasul 6: Crearea fișierului AndroidManifest.xml](#pasul-6-crearea-fi%C8%99ierului-androidmanifestxml)
        - [Pasul 7: Crearea fișierului bottom_nav_menu.xml](#pasul-7-crearea-fi%C8%99ierului-bottom_nav_menuxml)
        - [Pasul 8: Crearea fișierului mobile_navigation.xml](#pasul-8-crearea-fi%C8%99ierului-mobile_navigationxml)
        - [Pasul 9: Crearea fișierului activity_main.xml](#pasul-9-crearea-fi%C8%99ierului-activity_mainxml)
        - [Pasul 10: Crearea fișierului fragment_chuck.xml](#pasul-10-crearea-fi%C8%99ierului-fragment_chuckxml)
        - [Pasul 11: Crearea clasei ChuckViewModel.java](#pasul-11-crearea-clasei-chuckviewmodeljava)
        - [Pasul 12: Crearea clasei ChuckFragment.java](#pasul-12-crearea-clasei-chuckfragmentjava)
        - [Pasul 13: Crearea clasei MainActivity.java](#pasul-13-crearea-clasei-mainactivityjava)
        - [Pasul 14: Verificarea structurii proiectului](#pasul-14-verificarea-structurii-proiectului)
    - [Compilare, Instalare și Rulare](#compilare-instalare-%C8%99i-rulare)
        - [Compilarea aplicației](#compilarea-aplica%C8%9Biei)
        - [Listarea dispozitivelor conectate](#listarea-dispozitivelor-conectate)
        - [Instalarea aplicației](#instalarea-aplica%C8%9Biei)
        - [Lansarea activității principale](#lansarea-activit%C4%83%C8%9Bii-principale)
        - [Afișarea logurilor filtrate](#afi%C8%99area-logurilor-filtrate)
    - [Rezumat](#rezumat)

<!-- /TOC -->



## Prezentare Aplicație

Această aplicație Android extinde aplicațiile anterioare prin introducerea Fragmentelor și a Navigation Component pentru gestionarea navigării între multiple ecrane. Diferențele arhitecturale principale față de aplicațiile `01_hello_world`, `01_simple`, `02_simple` și `03_less_simple` sunt: introducerea Fragmentelor (componente UI reutilizabile care pot fi înlocuite în cadrul unei activități), utilizarea Navigation Component pentru gestionarea automată a navigării între fragmente, utilizarea ViewBinding pentru accesul la elementele UI (în loc de `findViewById()`), și pattern-ul de un ViewModel per Fragment pentru separarea logicii. Aplicația demonstrează arhitectura multi-fragment: o singură activitate (`MainActivity`) găzduiește trei fragmente distincte (ChuckFragment, JokeFragment, CocktailFragment) care sunt schimbate prin `BottomNavigationView`. Fiecare fragment are propriul ViewModel și propriul layout XML. Navigation Component gestionează automat stiva de back și tranzițiile între fragmente. ViewBinding generează automat clase de binding pentru fiecare layout, eliminând necesitatea `findViewById()`. Fluxul de date este: utilizatorul selectează un tab în `BottomNavigationView` → Navigation Component înlocuiește fragmentul activ → Fragment-ul observă LiveData din ViewModel-ul său → ViewModel face apeluri HTTP și actualizează LiveData → UI-ul se actualizează automat. Aplicația nu folosește Parcelable și nu are navigare între activități multiple.

## Structura Directorului Aplicației

```
akrilki_04/
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/
│           │   └── ro/
│           │       └── makore/
│           │           └── akrilki_04/
│           │               ├── MainActivity.java
│           │               └── ui/
│           │                   ├── chuck/
│           │                   │   ├── ChuckFragment.java
│           │                   │   └── ChuckViewModel.java
│           │                   ├── joke/
│           │                   │   ├── JokeFragment.java
│           │                   │   └── JokeViewModel.java
│           │                   └── cocktail/
│           │                       ├── CocktailFragment.java
│           │                       └── CocktailViewModel.java
│           └── res/
│               ├── layout/
│               │   ├── activity_main.xml
│               │   ├── fragment_chuck.xml
│               │   ├── fragment_joke.xml
│               │   └── fragment_cocktail.xml
│               ├── menu/
│               │   └── bottom_nav_menu.xml
│               └── navigation/
│                   └── mobile_navigation.xml
├── build.gradle
├── gradle.properties
└── settings.gradle
```

## Pași Live Coding

### Pasul 1: Crearea structurii de directoare

**Actiuni:** Creez structura completă de directoare pentru aplicația Android, inclusiv directoarele pentru fragmente și resurse de navigare.

**Ce scriu în terminal:**
```bash
mkdir -p akrilki_04/app/src/main/java/ro/makore/akrilki_04/ui/chuck
mkdir -p akrilki_04/app/src/main/java/ro/makore/akrilki_04/ui/joke
mkdir -p akrilki_04/app/src/main/java/ro/makore/akrilki_04/ui/cocktail
mkdir -p akrilki_04/app/src/main/res/layout
mkdir -p akrilki_04/app/src/main/res/menu
mkdir -p akrilki_04/app/src/main/res/navigation
```

**Note:** "Vom începe prin a crea structura de directoare pentru aplicația noastră. Observați că am adăugat directoare pentru trei fragmente (`ui/chuck`, `ui/joke`, `ui/cocktail`), fiecare cu propriul ViewModel. De asemenea, am adăugat directoare pentru meniu și navigare, necesare pentru Navigation Component."

**Checkpoint:** Structura de directoare este creată, inclusiv directoarele pentru fragmente și resurse de navigare.

---

### Pasul 2: Crearea fișierului settings.gradle

**Actiuni:** Creez fișierul `settings.gradle` la rădăcina proiectului.

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

rootProject.name = "Akrilki_04"
include ':app'
```

**Note:** "Creez fișierul `settings.gradle` cu configurația standard. Numele proiectului este `Akrilki_04`."

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
android.enableJetifier=true
```

**Note:** "Creez fișierul `gradle.properties` cu setările standard. `android.enableJetifier=true` permite migrarea automată a bibliotecilor vechi la AndroidX."

**Checkpoint:** Fișierul `gradle.properties` este creat.

---

### Pasul 5: Crearea fișierului app/build.gradle

**Actiuni:** Creez fișierul `build.gradle` pentru modulul `app` cu dependențele necesare pentru Navigation Component și ViewBinding.

**Ce scriu:**
```groovy
apply plugin: 'com.android.application'

android {
    namespace 'ro.makore.akrilki_04'
    compileSdk 34

    defaultConfig {
        applicationId "ro.makore.akrilki_04"
        minSdk 21
        targetSdk 34
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
```

**Note:** "Creez fișierul `app/build.gradle`. Setez namespace-ul la `ro.makore.akrilki_04` și `minSdk` la 21."

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
    buildFeatures {
        viewBinding true
    }
}
```

**Note:** "Configurez tipurile de build și opțiunile de compilare Java. Observați blocul `buildFeatures` - setez `viewBinding true` pentru a activa ViewBinding. Aceasta va genera automat clase de binding pentru fiecare layout XML, eliminând necesitatea `findViewById()`."

**Ce scriu (continuare):**
```groovy
dependencies {
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'com.google.android.material:material:1.12.0'
    implementation 'androidx.activity:activity:1.8.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.2.0'
    implementation 'com.squareup.okhttp3:okhttp:4.10.0'
    implementation 'com.squareup.picasso:picasso:2.71828'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.3'
    implementation 'androidx.navigation:navigation-ui-ktx:2.7.3'
}
```

**Note:** "În blocul `dependencies` adaug bibliotecile necesare. Observați noile dependențe: `navigation-fragment-ktx` și `navigation-ui-ktx` pentru Navigation Component, `picasso` pentru încărcarea imaginilor din URL-uri, și `lifecycle-viewmodel-ktx` pentru ViewModel-uri."

**Checkpoint:** Fișierul `app/build.gradle` este complet configurat, inclusiv ViewBinding și Navigation Component.

---

### Pasul 6: Crearea fișierului AndroidManifest.xml

**Actiuni:** Creez fișierul `AndroidManifest.xml` cu permisiunea INTERNET.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
```

**Note:** "Creez fișierul `AndroidManifest.xml` în `app/src/main/`. Adaug permisiunea `INTERNET` necesară pentru apeluri HTTP."

**Ce scriu (continuare):**
```xml
    <application
        android:label="Akrilki_04"
        android:theme="@style/Theme.MaterialComponents.DayNight.DarkActionBar"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="Akrilki_04">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

**Note:** "Declar activitatea `MainActivity` ca activitate principală cu tema Material Components."

**Checkpoint:** Fișierul `AndroidManifest.xml` este creat cu permisiunea INTERNET.

---

### Pasul 7: Crearea fișierului bottom_nav_menu.xml

**Actiuni:** Creez fișierul XML pentru meniul de navigare de jos.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">

    <item
        android:id="@+id/navigation_chuck"
        android:icon="@drawable/ic_chuck_black_24dp"
        android:tint="@null"
        android:title="Chuck" />

    <item
        android:id="@+id/navigation_joke"
        android:icon="@drawable/ic_jokes_color_24dp"
        android:tint="@null"
        android:title="Jokes" />

    <item
        android:id="@+id/navigation_cocktail"
        android:icon="@drawable/ic_cocktail_color_24dp"
        android:tint="@null"
        android:title="Cocktails" />

</menu>
```

**Note:** "Creez fișierul `bottom_nav_menu.xml` în directorul `app/src/main/res/menu/`. Acest fișier definește elementele meniului pentru `BottomNavigationView`. Fiecare `item` are un ID care corespunde cu ID-urile fragmentelor din graful de navigare, o iconiță, și un titlu. ID-urile trebuie să corespundă exact cu cele din graful de navigare pentru ca navigarea să funcționeze."

**Checkpoint:** Fișierul `bottom_nav_menu.xml` este creat cu trei elemente de meniu.

---

### Pasul 8: Crearea fișierului mobile_navigation.xml

**Actiuni:** Creez graful de navigare care definește fragmentele și relațiile dintre ele.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/mobile_navigation"
    app:startDestination="@+id/navigation_chuck">
```

**Note:** "Creez fișierul `mobile_navigation.xml` în directorul `app/src/main/res/navigation/`. Acesta este graful de navigare care definește structura navigării aplicației. Tag-ul `navigation` este rădăcina grafului. `app:startDestination` specifică fragmentul care se încarcă la pornirea aplicației - în cazul nostru `navigation_chuck`."

**Ce scriu (continuare):**
```xml
    <fragment
        android:id="@+id/navigation_chuck"
        android:name="ro.makore.akrilki_04.ui.chuck.ChuckFragment"
        android:label="Chuck"
        tools:layout="@layout/fragment_chuck" />
```

**Note:** "Adaug primul fragment în graf. `android:id` este ID-ul fragmentului folosit pentru navigare, `android:name` este numele complet al clasei Java a fragmentului, `android:label` este eticheta afișată în ActionBar, și `tools:layout` este layout-ul folosit pentru preview în instrumentele de dezvoltare."

**Ce scriu (continuare):**
```xml
    <fragment
        android:id="@+id/navigation_joke"
        android:name="ro.makore.akrilki_04.ui.joke.JokeFragment"
        android:label="Jokes"
        tools:layout="@layout/fragment_joke" />

    <fragment
        android:id="@+id/navigation_cocktail"
        android:name="ro.makore.akrilki_04.ui.cocktail.CocktailFragment"
        android:label="Cocktail"
        tools:layout="@layout/fragment_cocktail" />
</navigation>
```

**Note:** "Adaug celelalte două fragmente în graf. Observați că ID-urile (`navigation_joke`, `navigation_cocktail`) corespund cu ID-urile din meniul de navigare. Navigation Component va conecta automat meniul cu fragmentele pe baza acestor ID-uri."

**Checkpoint:** Fișierul `mobile_navigation.xml` este creat cu trei fragmente definite.

---

### Pasul 9: Crearea fișierului activity_main.xml

**Actiuni:** Creez layout-ul principal al activității cu `BottomNavigationView` și `NavHostFragment`.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/container"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingTop="?attr/actionBarSize">
```

**Note:** "Creez fișierul `activity_main.xml` în directorul `app/src/main/res/layout/`. Folosesc `ConstraintLayout` ca layout principal. `android:paddingTop` adaugă padding pentru ActionBar."

**Ce scriu (continuare):**
```xml
    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/nav_view"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="0dp"
        android:layout_marginEnd="0dp"
        android:background="?android:attr/windowBackground"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:menu="@menu/bottom_nav_menu" />
```

**Note:** "Adaug `BottomNavigationView` care va afișa meniul de navigare în partea de jos a ecranului. `app:menu="@menu/bottom_nav_menu"` conectează meniul XML creat anterior. Constraint-urile fixează meniul în partea de jos a ecranului."

**Ce scriu (continuare):**
```xml
    <fragment
        android:id="@+id/nav_host_fragment_activity_main"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:defaultNavHost="true"
        app:layout_constraintBottom_toTopOf="@id/nav_view"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:navGraph="@navigation/mobile_navigation" />
```

**Note:** "Adaug `NavHostFragment` - acesta este un fragment special care găzduiește fragmentele și gestionează navigarea. `android:name="androidx.navigation.fragment.NavHostFragment"` specifică clasa specială pentru Navigation Component. `app:defaultNavHost="true"` permite gestionarea automată a butonului Back. `app:navGraph="@navigation/mobile_navigation"` conectează graful de navigare creat anterior. Constraint-urile plasează `NavHostFragment` deasupra meniului de navigare."

**Ce scriu (continuare):**
```xml
    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fab_quit"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginEnd="16dp"
        android:layout_marginBottom="200dp"
        app:layout_constraintBottom_toBottomOf="@id/nav_view"
        app:layout_constraintEnd_toEndOf="parent"
        android:elevation="16dp"
        android:src="@drawable/ic_quit_black_24dp" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**Note:** "Adaug un `FloatingActionButton` pentru butonul de quit, poziționat deasupra meniului de navigare. `android:src` referă o iconiță din drawable."

**Checkpoint:** Fișierul `activity_main.xml` este creat cu `BottomNavigationView`, `NavHostFragment`, și `FloatingActionButton`.

---

### Pasul 10: Crearea fișierului fragment_chuck.xml

**Actiuni:** Creez layout-ul pentru `ChuckFragment`.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".ui.chuck.ChuckFragment">
```

**Note:** "Creez fișierul `fragment_chuck.xml` în directorul `app/src/main/res/layout/`. Folosesc `ConstraintLayout` ca layout principal. `tools:context` specifică fragmentul asociat pentru preview."

**Ce scriu (continuare):**
```xml
    <TextView
        android:id="@+id/title_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Chuck Norris facts"
        android:textSize="24sp"
        android:textAlignment="center"
        app:layout_constraintBottom_toTopOf="@id/imageView"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
```

**Note:** "Adaug un `TextView` pentru titlul fragmentului, fixat sus și centrat orizontal."

**Ce scriu (continuare):**
```xml
    <ImageView
        android:id="@+id/imageView"
        android:layout_width="150dp"
        android:layout_height="150dp"
        android:src="@drawable/chuck"
        app:layout_constraintBottom_toTopOf="@id/text_chuck"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
```

**Note:** "Adaug un `ImageView` pentru imaginea Chuck Norris, centrat pe ecran. `android:src="@drawable/chuck"` referă o imagine din drawable."

**Ce scriu (continuare):**
```xml
    <TextView
        android:id="@+id/text_chuck"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginStart="8dp"
        android:layout_marginTop="8dp"
        android:layout_marginEnd="8dp"
        android:textAlignment="center"
        android:textSize="20sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
```

**Note:** "Adaug un `TextView` cu ID-ul `text_chuck` pentru afișarea faptului Chuck Norris. Acest ID va fi folosit în ViewBinding pentru a accesa elementul din cod."

**Ce scriu (continuare):**
```xml
    <Button
        android:id="@+id/refreshButton"
        android:layout_width="404dp"
        android:layout_height="98dp"
        android:layout_marginBottom="68dp"
        android:text="Get New Fact"
        android:textSize="18sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Note:** "Adaug butonul de refresh, fixat în partea de jos a fragmentului. Închid tag-ul `ConstraintLayout`."

**Checkpoint:** Fișierul `fragment_chuck.xml` este creat cu TextView pentru titlu, ImageView, TextView pentru text, și buton de refresh.

---

### Pasul 11: Crearea clasei ChuckViewModel.java

**Actiuni:** Creez ViewModel-ul pentru `ChuckFragment` care gestionează logica de business.

**Ce scriu:**
```java
package ro.makore.akrilki_04.ui.chuck;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
```

**Note:** "Creez fișierul `ChuckViewModel.java` în directorul `app/src/main/java/ro/makore/akrilki_04/ui/chuck/`. Declar pachetul și adaug import-urile: ViewModel și LiveData pentru arhitectură, JSON pentru parsare, și OkHttp pentru apeluri HTTP."

**Ce scriu (continuare):**
```java
public class ChuckViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private OkHttpClient client;
    
    public ChuckViewModel() {
        mText = new MutableLiveData<>();
        fetchCNFact();
    }
```

**Note:** "Definesc clasa `ChuckViewModel` care extinde `ViewModel`. Declar `mText` ca `MutableLiveData<String>` pentru a stoca faptul Chuck Norris. În constructor, inițializez `mText` și apelez imediat `fetchCNFact()` pentru a prelua primul fapt."

**Ce scriu (continuare):**
```java
    public void fetchCNFact() {
        String url = "https://api.chucknorris.io/jokes/random";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mText.postValue("Failed to load joke." + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mText.postValue("Failed to load joke.");
                    return;
                }

                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    String cnfact = json.getString("value");
                    mText.postValue(cnfact);
                } catch (JSONException e) {
                    mText.postValue("Failed to parse joke.");
                }
            }
        });
    }

    public LiveData<String> getText() {
        return mText;
    }
}
```

**Note:** "Implementez metoda `fetchCNFact()` care face apelul HTTP către API-ul Chuck Norris, parsează răspunsul JSON, și actualizează LiveData cu `postValue()`. Metoda `getText()` returnează LiveData read-only pentru observare. Această implementare este similară cu ViewModel-ul din aplicația anterioară, dar este dedicat unui singur fragment."

**Checkpoint:** Clasa `ChuckViewModel` este completă cu logica de business pentru preluarea faptelor Chuck Norris.

---

### Pasul 12: Crearea clasei ChuckFragment.java

**Actiuni:** Creez fragmentul care folosește ViewBinding și observă LiveData din ViewModel.

**Ce scriu:**
```java
package ro.makore.akrilki_04.ui.chuck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import ro.makore.akrilki_04.databinding.FragmentChuckBinding;
```

**Note:** "Creez fișierul `ChuckFragment.java` în directorul `app/src/main/java/ro/makore/akrilki_04/ui/chuck/`. Declar pachetul și adaug import-urile: `Fragment` ca clasă de bază, `ViewModelProvider` pentru a obține ViewModel-ul, și `FragmentChuckBinding` - aceasta este clasa generată automat de ViewBinding pentru layout-ul `fragment_chuck.xml`."

**Ce scriu (continuare):**
```java
public class ChuckFragment extends Fragment {

    private FragmentChuckBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
```

**Note:** "Definesc clasa `ChuckFragment` care extinde `Fragment`. Declar `binding` de tip `FragmentChuckBinding` - aceasta va conține referințe la toate elementele UI din layout. Suprascriu metoda `onCreateView()` care este apelată pentru a crea view-ul fragmentului."

**Ce scriu (continuare):**
```java
        ChuckViewModel chuckViewModel =
                new ViewModelProvider(this).get(ChuckViewModel.class);
```

**Note:** "Obțin instanța ViewModel-ului folosind `ViewModelProvider`. `this` este fragmentul curent - ViewModel-ul va fi legat de lifecycle-ul fragmentului și va supraviețui recreării fragmentului."

**Ce scriu (continuare):**
```java
        binding = FragmentChuckBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
```

**Note:** "Această linie este esențială pentru ViewBinding! `FragmentChuckBinding.inflate()` creează instanța de binding și inflatează layout-ul XML. `binding.getRoot()` returnează view-ul rădăcină al layout-ului. Observați că nu mai folosim `setContentView()` sau `findViewById()` - ViewBinding gestionează totul automat!"

**Ce scriu (continuare):**
```java
        final TextView textView = binding.textChuck;
        chuckViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
```

**Note:** "Obțin referința la TextView folosind `binding.textChuck` - ViewBinding generează automat proprietăți pentru fiecare element cu ID din layout! Apoi observ LiveData din ViewModel folosind `observe()`. `getViewLifecycleOwner()` este lifecycle owner-ul specific pentru view-ul fragmentului - permite LiveData să știe când view-ul este distrus. `textView::setText` este o method reference care actualizează automat TextView-ul când LiveData se schimbă."

**Ce scriu (continuare):**
```java
        Button refreshButton = binding.refreshButton;
        refreshButton.setOnClickListener(v -> {
            chuckViewModel.fetchCNFact();
        });

        return root;
    }
```

**Note:** "Obțin referința la buton folosind `binding.refreshButton` și atașez listener care apelează `fetchCNFact()` din ViewModel. Returnez `root` - view-ul rădăcină al fragmentului."

**Ce scriu (continuare):**
```java
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
```

**Note:** "Suprascriu `onDestroyView()` pentru a seta `binding` la `null` când view-ul este distrus. Aceasta previne memory leaks - ViewBinding păstrează referințe la view-uri care ar putea fi distruse."

**Checkpoint:** Clasa `ChuckFragment` este completă și folosește ViewBinding și pattern-ul Observer pentru a observa LiveData din ViewModel.

---

### Pasul 13: Crearea clasei MainActivity.java

**Actiuni:** Creez activitatea principală care configurează Navigation Component.

**Ce scriu:**
```java
package ro.makore.akrilki_04;

import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import ro.makore.akrilki_04.databinding.ActivityMainBinding;
```

**Note:** "Creez fișierul `MainActivity.java` în directorul `app/src/main/java/ro/makore/akrilki_04/`. Declar pachetul și adaug import-urile: componente Material Design, Navigation Component, și `ActivityMainBinding` generat automat de ViewBinding."

**Ce scriu (continuare):**
```java
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
```

**Note:** "Definesc clasa `MainActivity` care extinde `AppCompatActivity`. Declar `binding` de tip `ActivityMainBinding`. În `onCreate`, folosesc ViewBinding pentru a inflata layout-ul - `ActivityMainBinding.inflate(getLayoutInflater())` creează binding-ul, iar `binding.getRoot()` returnează view-ul rădăcină."

**Ce scriu (continuare):**
```java
        FloatingActionButton fabQuit = findViewById(R.id.fab_quit);
        fabQuit.setOnClickListener(v -> finishAffinity());
```

**Note:** "Obțin referința la FloatingActionButton și atașez listener pentru quit. Observați că pentru acest element folosesc încă `findViewById()` - puteam folosi și `binding.fabQuit` dacă doream."

**Ce scriu (continuare):**
```java
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_chuck, R.id.navigation_joke, R.id.navigation_cocktail)
                .build();
```

**Note:** "Obțin referința la `BottomNavigationView` și creez `AppBarConfiguration` care specifică care fragmente sunt destinații de nivel superior. Acest lucru este important pentru ActionBar - butonul Back nu va apărea pentru aceste destinații de top."

**Ce scriu (continuare):**
```java
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
```

**Note:** "Obțin `NavController` folosind `Navigation.findNavController()` - trebuie să trec ID-ul `NavHostFragment`-ului din layout. Configurez ActionBar să lucreze cu Navigation Component. Apoi conectez `BottomNavigationView` cu `NavController` folosind `NavigationUI.setupWithNavController()` - aceasta face ca navigarea să funcționeze automat când utilizatorul apasă pe elementele meniului!"

**Ce scriu (continuare):**
```java
    }
}
```

**Note:** "Închid metoda `onCreate` și clasa. Activity-ul este mult mai simplu acum - Navigation Component gestionează toată logica de navigare automat!"

**Checkpoint:** Clasa `MainActivity` este completă și configurează Navigation Component pentru navigare automată între fragmente.

---

### Pasul 14: Verificarea structurii proiectului

**Actiuni:** Verific că toate fișierele necesare sunt prezente.

**Note:** "Am creat structura de bază pentru aplicația cu trei fragmente. Pentru simplitate, am creat doar `ChuckFragment` și `ChuckViewModel` ca exemplu. Fragmentele `JokeFragment` și `CocktailFragment` urmează același pattern - fiecare are propriul ViewModel și layout XML. Aplicația nu folosește Parcelable, deci nu avem clase Parcelable de creat."

**Checkpoint:** Toate fișierele esențiale sunt create: MainActivity cu Navigation Component, un fragment exemplu (ChuckFragment) cu ViewModel, și toate resursele necesare (meniu, navigare, layout-uri).

---

## Compilare, Instalare și Rulare

### Compilarea aplicației

**Actiuni:** Compilez aplicația pentru a genera APK-ul de debug.

**Ce scriu în terminal:**
```bash
cd akrilki_04
gradle build
```

**Note:** "Navighez în directorul proiectului și rulez `gradle build` pentru a compila aplicația. Gradle va genera automat clasele de ViewBinding pentru toate layout-urile XML și va compila codul. APK-ul va fi generat în `app/build/outputs/apk/debug/app-debug.apk`."

**Checkpoint:** Build-ul se finalizează cu succes, APK-ul este generat, și clasele de ViewBinding sunt create automat.

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
adb shell am start -n ro.makore.akrilki_04/.MainActivity
```

**Note:** "Folosesc `adb shell am start` pentru a lansa activitatea. Aplicația se va deschide și va afișa automat `ChuckFragment` (destinația de start). `BottomNavigationView` va permite navigarea între cele trei fragmente. Testați navigarea - fiecare fragment ar trebui să se încarce corect când apăsați pe elementele meniului!"

**Checkpoint:** Aplicația se deschide, `ChuckFragment` se încarcă automat, și navigarea între fragmente funcționează prin `BottomNavigationView`. La apăsarea butonului "Get New Fact", se preia un nou fapt Chuck Norris. La apăsarea butonului Quit, aplicația se închide.

---

### Afișarea logurilor filtrate

**Actiuni:** Monitorizez logurile aplicației pentru a verifica navigarea și lifecycle-ul fragmentelor.

**Ce scriu în terminal:**
```bash
adb logcat | grep -i "akrilki_04\|MainActivity\|ChuckFragment\|Navigation"
```

**Note:** "Folosesc `adb logcat` pentru a afișa logurile sistemului Android. Filtrez output-ul pentru a vedea mesajele legate de aplicația noastră, MainActivity, ChuckFragment, și Navigation Component. Aici putem vedea navigarea între fragmente, lifecycle-ul fragmentelor, și eventualele erori."

**Alternativă (Windows PowerShell):**
```powershell
adb logcat | Select-String -Pattern "akrilki_04|MainActivity|ChuckFragment|Navigation"
```

**Checkpoint:** Logurile se afișează în terminal, arătând navigarea între fragmente, lifecycle-ul fragmentelor, și apelurile HTTP. Când navigați între fragmente, putem observa în loguri cum Navigation Component gestionează tranzițiile.

---

## Rezumat

Am creat o aplicație Android cu trei fragmente și Navigation Component care demonstrează:
- Utilizarea Fragmentelor pentru componente UI reutilizabile
- Navigation Component pentru gestionarea automată a navigării între fragmente
- ViewBinding pentru accesul la elementele UI fără `findViewById()`
- Pattern-ul de un ViewModel per Fragment pentru separarea logicii
- `BottomNavigationView` pentru interfața de navigare
- `NavHostFragment` pentru găzduirea fragmentelor
- Graful de navigare XML pentru definirea structurii de navigare
- Pattern-ul Observer cu LiveData pentru actualizarea UI-ului
- Apeluri HTTP asincrone cu OkHttp și parsare JSON
- Permisiunea INTERNET în manifest
- Tema Material Components pentru interfață modernă
- Structura de bază a unui proiect Android cu Gradle
- Compilarea, instalarea și rularea aplicației din linia de comandă

**Diferențele arhitecturale față de `01_hello_world`, `01_simple`, `02_simple` și `03_less_simple`:**
- Introducerea Fragmentelor pentru componente UI modulare și reutilizabile
- Navigation Component pentru gestionarea automată a navigării și stivei de back
- ViewBinding pentru accesul la elemente UI fără `findViewById()`
- Pattern-ul de un ViewModel per Fragment (în loc de un ViewModel pentru întreaga activitate)
- `BottomNavigationView` pentru interfața de navigare
- Graful de navigare XML pentru definirea structurii de navigare
- `NavHostFragment` pentru găzduirea fragmentelor
- Arhitectură multi-fragment cu o singură activitate

Aplicația este funcțională și demonstrează conceptele fundamentale ale arhitecturii multi-fragment și Navigation Component în Android.


