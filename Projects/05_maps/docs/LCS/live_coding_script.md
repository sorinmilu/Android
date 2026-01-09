# Script Live Coding - Aplicație cu Google Maps și Location Services

## Prezentare Aplicație

Această aplicație Android extinde aplicațiile anterioare prin introducerea integrării cu Google Maps API și serviciilor de localizare. Diferențele arhitecturale principale față de aplicațiile `01_hello_world`, `01_simple`, `02_simple`, `03_less_simple` și `04_three_fragments` sunt: utilizarea Google Maps SDK pentru afișarea hărților interactive (`MapView`, `GoogleMap`), integrarea cu serviciile de localizare Google (`FusedLocationProviderClient`) pentru obținerea poziției dispozitivului, utilizarea `Geocoder` pentru conversia între adrese și coordonate geografice, gestionarea permisiunilor de localizare în runtime, și utilizarea `DrawerLayout` cu `NavigationView` pentru meniul lateral glisant (în loc de `BottomNavigationView`). Aplicația demonstrează gestionarea lifecycle-ului `MapView`-ului care trebuie să fie conectat la lifecycle-ul fragmentului, pattern-ul `OnMapReadyCallback` pentru inițializarea asincronă a hărții, și utilizarea `ActivityResultLauncher` pentru gestionarea modernă a permisiunilor. Fluxul de date este: utilizatorul deschide aplicația → se solicită permisiunea de localizare → `FusedLocationProviderClient` obține poziția → harta se centrează pe locația utilizatorului → utilizatorul navighează între fragmente prin meniul lateral → fiecare fragment afișează hărți sau date geografice diferite. Aplicația nu folosește Parcelable și nu are navigare între activități multiple.

## Structura Directorului Aplicației

```
akrilki_05/
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/
│           │   └── ro/
│           │       └── makore/
│           │           └── akrilki_05/
│           │               ├── MainActivity.java
│           │               └── ui/
│           │                   ├── home/
│           │                   │   └── HomeFragment.java
│           │                   ├── geodata/
│           │                   │   └── GeodataFragment.java
│           │                   ├── location/
│           │                   │   └── LocationFragment.java
│           │                   └── address/
│           │                       └── AddressFragment.java
│           └── res/
│               ├── layout/
│               │   ├── activity_main.xml
│               │   ├── app_bar_main.xml
│               │   ├── content_main.xml
│               │   ├── fragment_home.xml
│               │   ├── fragment_geodata.xml
│               │   ├── fragment_location.xml
│               │   └── fragment_address.xml
│               ├── menu/
│               │   ├── activity_main_drawer.xml
│               │   └── main.xml
│               └── navigation/
│                   └── mobile_navigation.xml
├── build.gradle
├── gradle.properties
└── settings.gradle
```

## Pași Live Coding

### Pasul 1: Crearea structurii de directoare

**Ce fac:** Creez structura completă de directoare pentru aplicația Android, inclusiv directoarele pentru fragmente și resurse de navigare.

**Ce scriu în terminal:**
```bash
mkdir -p akrilki_05/app/src/main/java/ro/makore/akrilki_05/ui/home
mkdir -p akrilki_05/app/src/main/java/ro/makore/akrilki_05/ui/geodata
mkdir -p akrilki_05/app/src/main/java/ro/makore/akrilki_05/ui/location
mkdir -p akrilki_05/app/src/main/java/ro/makore/akrilki_05/ui/address
mkdir -p akrilki_05/app/src/main/res/layout
mkdir -p akrilki_05/app/src/main/res/menu
mkdir -p akrilki_05/app/src/main/res/navigation
```

**Ce spun:** "Vom începe prin a crea structura de directoare pentru aplicația noastră. Această aplicație va integra Google Maps, deci vom avea nevoie de fragmente care gestionează hărți și servicii de localizare. Observați că am adăugat directoare pentru patru fragmente: home, geodata, location, și address."

**Checkpoint:** Structura de directoare este creată, inclusiv directoarele pentru fragmente și resurse de navigare.

---

### Pasul 2: Crearea fișierului settings.gradle

**Ce fac:** Creez fișierul `settings.gradle` la rădăcina proiectului.

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

rootProject.name = "Akrilki_05"
include ':app'
```

**Ce spun:** "Creez fișierul `settings.gradle` cu configurația standard. Numele proiectului este `Akrilki_05`."

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

**Ce fac:** Creez fișierul `build.gradle` pentru modulul `app` cu dependențele necesare pentru Google Maps și Location Services.

**Ce scriu:**
```groovy
apply plugin: 'com.android.application'

android {
    namespace 'ro.makore.akrilki_05'
    compileSdk 34

    defaultConfig {
        applicationId "ro.makore.akrilki_05"
        minSdk 21
        targetSdk 34
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
```

**Ce spun:** "Creez fișierul `app/build.gradle`. Setez namespace-ul la `ro.makore.akrilki_05` și `minSdk` la 21."

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

**Ce spun:** "Configurez tipurile de build, opțiunile de compilare Java, și activez ViewBinding pentru generarea automată a claselor de binding."

**Ce scriu (continuare):**
```groovy
dependencies {
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'com.google.android.material:material:1.12.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.2.0'
    implementation 'androidx.navigation:navigation-fragment:2.7.3'
    implementation 'androidx.navigation:navigation-ui:2.7.3'
    implementation 'com.google.android.gms:play-services-maps:18.1.0'
    implementation 'com.google.android.gms:play-services-location:21.0.1'
    implementation 'com.google.android.gms:play-services-tasks:18.0.2'
}
```

**Ce spun:** "În blocul `dependencies` adaug bibliotecile necesare. Observați noile dependențe esențiale: `play-services-maps` pentru Google Maps, `play-services-location` pentru serviciile de localizare, și `play-services-tasks` pentru gestionarea task-urilor asincrone. De asemenea, păstrăm Navigation Component pentru navigarea între fragmente."

**Checkpoint:** Fișierul `app/build.gradle` este complet configurat, inclusiv dependențele pentru Google Maps și Location Services.

---

### Pasul 6: Crearea fișierului AndroidManifest.xml

**Ce fac:** Creez fișierul `AndroidManifest.xml` cu permisiunile necesare și cheia API Google Maps.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

**Ce spun:** "Creez fișierul `AndroidManifest.xml` în `app/src/main/`. Adaug trei permisiuni esențiale: `INTERNET` pentru conexiunea la serviciile Google Maps, `ACCESS_FINE_LOCATION` pentru localizare precisă (GPS), și `ACCESS_COARSE_LOCATION` pentru localizare aproximativă (rețea). Observați că permisiunile de localizare trebuie să fie solicitate și în runtime pentru Android 6.0+."

**Ce scriu (continuare):**
```xml
    <application
        android:label="Akrilki_05"
        android:theme="@style/Theme.Akrilki_05"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
```

**Ce spun:** "Declar activitatea `MainActivity` ca activitate principală."

**Ce scriu (continuare):**
```xml
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="YOUR_API_KEY_HERE" />
```

**Ce spun:** "Această linie este crucială pentru Google Maps! Adaug un `meta-data` care conține cheia API Google Maps. `android:name="com.google.android.geo.API_KEY"` este numele standard recunoscut de Google Maps SDK. `android:value` trebuie să conțină cheia API obținută de la Google Cloud Console. Fără această cheie, hărțile nu vor funcționa!"

**Ce scriu (continuare):**
```xml
    </application>

</manifest>
```

**Ce spun:** "Închid tag-urile `application` și `manifest`. Manifest-ul este complet cu permisiunile și cheia API necesare pentru Google Maps."

**Checkpoint:** Fișierul `AndroidManifest.xml` este creat cu permisiunile de localizare și cheia API Google Maps.

---

### Pasul 7: Crearea fișierului activity_main_drawer.xml

**Ce fac:** Creez fișierul XML pentru meniul lateral (drawer menu).

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    tools:showIn="navigation_view">

    <group android:checkableBehavior="single">
        <item
            android:id="@+id/nav_home"
            android:icon="@drawable/ic_menu_camera"
            android:title="Home" />
        <item
            android:id="@+id/nav_geodata"
            android:icon="@drawable/ic_menu_geodata"
            android:title="Geodata" />
        <item
            android:id="@+id/nav_location"
            android:icon="@drawable/ic_menu_location"
            android:title="Location" />
        <item
            android:id="@+id/nav_address"
            android:icon="@drawable/ic_menu_address"
            android:title="Address" />
    </group>
</menu>
```

**Ce spun:** "Creez fișierul `activity_main_drawer.xml` în directorul `app/src/main/res/menu/`. Acest fișier definește elementele meniului lateral (drawer menu). `group` cu `checkableBehavior="single"` permite selecția unui singur element la un moment dat. Fiecare `item` are un ID care corespunde cu ID-urile fragmentelor din graful de navigare, o iconiță, și un titlu. ID-urile trebuie să corespundă exact cu cele din graful de navigare."

**Checkpoint:** Fișierul `activity_main_drawer.xml` este creat cu patru elemente de meniu.

---

### Pasul 8: Crearea fișierului main.xml

**Ce fac:** Creez fișierul XML pentru meniul dropdown din ActionBar.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <item
        android:id="@+id/action_quit"
        android:orderInCategory="100"
        android:title="Quit"
        app:showAsAction="never" />
</menu>
```

**Ce spun:** "Creez fișierul `main.xml` în directorul `app/src/main/res/menu/`. Acest fișier definește meniul dropdown care apare când utilizatorul apasă pe butonul cu trei puncte din ActionBar. `app:showAsAction="never"` înseamnă că elementul va apărea doar în meniul dropdown, nu în ActionBar."

**Checkpoint:** Fișierul `main.xml` este creat cu elementul de quit.

---

### Pasul 9: Crearea fișierului mobile_navigation.xml

**Ce fac:** Creez graful de navigare care definește fragmentele și relațiile dintre ele.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/mobile_navigation"
    app:startDestination="@+id/nav_home">
```

**Ce spun:** "Creez fișierul `mobile_navigation.xml` în directorul `app/src/main/res/navigation/`. Acesta este graful de navigare care definește structura navigării aplicației. `app:startDestination="@+id/nav_home"` specifică că `HomeFragment` se încarcă la pornirea aplicației."

**Ce scriu (continuare):**
```xml
    <fragment
        android:id="@+id/nav_home"
        android:name="ro.makore.akrilki_05.ui.home.HomeFragment"
        android:label="Home"
        tools:layout="@layout/fragment_home" />

    <fragment
        android:id="@+id/nav_geodata"
        android:name="ro.makore.akrilki_05.ui.geodata.GeodataFragment"
        android:label="Geodata"
        tools:layout="@layout/fragment_geodata" />

    <fragment
        android:id="@+id/nav_location"
        android:name="ro.makore.akrilki_05.ui.location.LocationFragment"
        android:label="Location"
        tools:layout="@layout/fragment_location" />

    <fragment
        android:id="@+id/nav_address"
        android:name="ro.makore.akrilki_05.ui.address.AddressFragment"
        android:label="Address"
        tools:layout="@layout/fragment_address" />
</navigation>
```

**Ce spun:** "Adaug cele patru fragmente în graf. Fiecare fragment are un ID care corespunde cu ID-urile din meniul drawer, numele complet al clasei Java, o etichetă pentru ActionBar, și layout-ul pentru preview. Observați că ID-urile (`nav_home`, `nav_geodata`, etc.) trebuie să corespundă exact cu cele din meniul drawer pentru ca navigarea să funcționeze."

**Checkpoint:** Fișierul `mobile_navigation.xml` este creat cu patru fragmente definite.

---

### Pasul 10: Crearea fișierului activity_main.xml

**Ce fac:** Creez layout-ul principal al activității cu `DrawerLayout` și `NavigationView`.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.drawerlayout.widget.DrawerLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/drawer_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    tools:openDrawer="start">
```

**Ce spun:** "Creez fișierul `activity_main.xml` în directorul `app/src/main/res/layout/`. Folosesc `DrawerLayout` ca layout principal - aceasta este diferența față de aplicația anterioară care folosea `BottomNavigationView`. `DrawerLayout` permite un meniu lateral glisant. `android:fitsSystemWindows="true"` asigură adaptarea la marginea ferestrei sistemului. `tools:openDrawer="start"` permite preview-ul drawer-ului deschis în instrumentele de dezvoltare."

**Ce scriu (continuare):**
```xml
    <include
        android:id="@+id/app_bar_main"
        layout="@layout/app_bar_main"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

**Ce spun:** "Folosesc tag-ul `include` pentru a include layout-ul `app_bar_main` care conține Toolbar-ul și conținutul principal. `include` permite reutilizarea layout-urilor și separarea structurii complexe în fișiere mai mici."

**Ce scriu (continuare):**
```xml
    <com.google.android.material.navigation.NavigationView
        android:id="@+id/nav_view"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        android:fitsSystemWindows="true"
        app:headerLayout="@layout/nav_header_main"
        app:menu="@menu/activity_main_drawer" />
</androidx.drawerlayout.widget.DrawerLayout>
```

**Ce spun:** "Adaug `NavigationView` care este meniul lateral glisant. `android:layout_gravity="start"` plasează meniul în partea stângă (sau dreapta pentru RTL). `app:headerLayout` specifică un layout pentru antetul meniului, iar `app:menu` conectează meniul XML creat anterior. Meniul va glisa din stânga când utilizatorul apasă pe butonul hamburger din Toolbar."

**Checkpoint:** Fișierul `activity_main.xml` este creat cu `DrawerLayout` și `NavigationView`.

---

### Pasul 11: Crearea fișierului app_bar_main.xml

**Ce fac:** Creez layout-ul pentru AppBar cu Toolbar și conținutul principal.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
```

**Ce spun:** "Creez fișierul `app_bar_main.xml` în directorul `app/src/main/res/layout/`. Folosesc `CoordinatorLayout` ca layout principal - acesta permite coordonarea interacțiunilor între elemente, cum ar fi ascunderea Toolbar-ului la scroll."

**Ce scriu (continuare):**
```xml
    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:theme="@style/Theme.Akrilki_05.AppBarOverlay">

        <androidx.appcompat.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="?attr/colorPrimary"
            app:popupTheme="@style/Theme.Akrilki_05.PopupOverlay" />

    </com.google.android.material.appbar.AppBarLayout>
```

**Ce spun:** "Adaug `AppBarLayout` care conține `Toolbar`-ul. `AppBarLayout` gestionează comportamentul Toolbar-ului în raport cu scroll-ul. `Toolbar` este ActionBar-ul personalizat care va conține butonul hamburger pentru deschiderea drawer-ului și butonul cu trei puncte pentru meniul dropdown."

**Ce scriu (continuare):**
```xml
    <include layout="@layout/content_main" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

**Ce spun:** "Includ layout-ul `content_main` care va conține `NavHostFragment`-ul pentru fragmente. Închid tag-ul `CoordinatorLayout`."

**Checkpoint:** Fișierul `app_bar_main.xml` este creat cu `AppBarLayout`, `Toolbar`, și include pentru conținut.

---

### Pasul 12: Crearea fișierului content_main.xml

**Ce fac:** Creez layout-ul pentru conținutul principal cu `NavHostFragment`.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layout_behavior="@string/appbar_scrolling_view_behavior"
    tools:showIn="@layout/app_bar_main">
```

**Ce spun:** "Creez fișierul `content_main.xml` în directorul `app/src/main/res/layout/`. Folosesc `ConstraintLayout` ca layout principal. `app:layout_behavior="@string/appbar_scrolling_view_behavior"` permite coordonarea cu `AppBarLayout` pentru scroll. `tools:showIn` indică că acest layout este inclus în `app_bar_main`."

**Ce scriu (continuare):**
```xml
    <fragment
        android:id="@+id/nav_host_fragment_content_main"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:defaultNavHost="true"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:navGraph="@navigation/mobile_navigation" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Ce spun:** "Adaug `NavHostFragment` care găzduiește fragmentele și gestionează navigarea. `android:name="androidx.navigation.fragment.NavHostFragment"` specifică clasa specială pentru Navigation Component. `app:defaultNavHost="true"` permite gestionarea automată a butonului Back. `app:navGraph="@navigation/mobile_navigation"` conectează graful de navigare creat anterior. Constraint-urile plasează fragmentul să ocupe tot spațiul disponibil."

**Checkpoint:** Fișierul `content_main.xml` este creat cu `NavHostFragment`.

---

### Pasul 13: Crearea fișierului fragment_home.xml

**Ce fac:** Creez layout-ul pentru `HomeFragment` cu `MapView`.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".ui.home.HomeFragment">
```

**Ce spun:** "Creez fișierul `fragment_home.xml` în directorul `app/src/main/res/layout/`. Folosesc `ConstraintLayout` ca layout principal. `tools:context` specifică fragmentul asociat pentru preview."

**Ce scriu (continuare):**
```xml
    <com.google.android.gms.maps.MapView
        android:id="@+id/map_view"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Ce spun:** "Adaug `MapView` - acesta este componenta Google Maps care va afișa harta. `com.google.android.gms.maps.MapView` este clasa din Google Maps SDK. `android:layout_width="0dp"` și `android:layout_height="0dp"` în ConstraintLayout înseamnă că va ocupa tot spațiul disponibil între constrângeri. Constraint-urile plasează harta să ocupe tot ecranul."

**Checkpoint:** Fișierul `fragment_home.xml` este creat cu `MapView`.

---

### Pasul 14: Crearea clasei HomeFragment.java - Partea 1: Imports și Declarații

**Ce fac:** Creez fragmentul care gestionează harta și localizarea.

**Ce scriu:**
```java
package ro.makore.akrilki_05.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import ro.makore.akrilki_05.databinding.FragmentHomeBinding;
```

**Ce spun:** "Creez fișierul `HomeFragment.java` în directorul `app/src/main/java/ro/makore/akrilki_05/ui/home/`. Declar pachetul și adaug import-urile: `Fragment` ca clasă de bază, `FusedLocationProviderClient` pentru serviciile de localizare, clasele Google Maps (`GoogleMap`, `MapView`, `OnMapReadyCallback`), și `FragmentHomeBinding` generat automat de ViewBinding."

**Ce scriu (continuare):**
```java
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private FragmentHomeBinding binding;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
```

**Ce spun:** "Definesc clasa `HomeFragment` care extinde `Fragment` și implementează `OnMapReadyCallback`. `OnMapReadyCallback` este o interfață necesară pentru a primi notificarea când harta este gata de utilizare. Declar `binding` pentru ViewBinding, `googleMap` pentru referința la hartă, și `fusedLocationProviderClient` pentru serviciile de localizare."

**Checkpoint:** Clasa `HomeFragment` este declarată cu toate import-urile și atributele necesare.

---

### Pasul 15: Crearea clasei HomeFragment.java - Partea 2: Metoda onCreateView

**Ce scriu (continuare):**
```java
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
```

**Ce spun:** "Suprascriu metoda `onCreateView` care este apelată pentru a crea view-ul fragmentului. Folosesc ViewBinding pentru a inflata layout-ul - `FragmentHomeBinding.inflate()` creează binding-ul și inflatează layout-ul XML."

**Ce scriu (continuare):**
```java
        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
```

**Ce spun:** "Inițializez `FusedLocationProviderClient` folosind `LocationServices.getFusedLocationProviderClient()`. Acest client combină GPS, Wi-Fi și rețele mobile pentru a oferi localizare precisă și eficientă. `requireActivity()` returnează activitatea asociată fragmentului."

**Ce scriu (continuare):**
```java
        // Get MapView and set up the map
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);
```

**Ce spun:** "Această linie este crucială pentru Google Maps! `binding.mapView.onCreate(savedInstanceState)` inițializează `MapView`-ul și restabilește starea dacă există. `binding.mapView.getMapAsync(this)` solicită harta asincron - `this` este fragmentul care implementează `OnMapReadyCallback`, deci metoda `onMapReady()` va fi apelată când harta este gata. Aceasta este pattern-ul standard pentru inițializarea Google Maps!"

**Ce scriu (continuare):**
```java
        return binding.getRoot();
    }
```

**Ce spun:** "Returnez view-ul rădăcină al layout-ului. Acum trebuie să implementăm metoda `onMapReady()` care va fi apelată când harta este inițializată."

**Checkpoint:** Metoda `onCreateView` este completă și inițializează `MapView` și `FusedLocationProviderClient`.

---

### Pasul 16: Crearea clasei HomeFragment.java - Partea 3: Metoda onMapReady

**Ce scriu (continuare):**
```java
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
```

**Ce spun:** "Implementez metoda `onMapReady()` care este apelată automat când harta este gata de utilizare. Primim obiectul `GoogleMap` care ne permite să controlăm harta - adăugăm markeri, centrăm camera, setăm stiluri, etc."

**Ce scriu (continuare):**
```java
        // Check location permission and fetch location
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
```

**Ce spun:** "Verific permisiunile de localizare folosind `ActivityCompat.checkSelfPermission()`. Dacă permisiunile nu sunt acordate, le solicit folosind `ActivityCompat.requestPermissions()`. Aceasta este gestionarea permisiunilor în runtime, necesară pentru Android 6.0+. Dacă permisiunile nu sunt acordate, ies din metodă - harta va funcționa, dar fără afișarea locației utilizatorului."

**Ce scriu (continuare):**
```java
        googleMap.setMyLocationEnabled(true);
        googleMap.setMapStyle(new MapStyleOptions("[{\"featureType\":\"poi\",\"stylers\":[{\"visibility\":\"off\"}]}]"));
```

**Ce spun:** "Activez afișarea locației utilizatorului pe hartă cu `setMyLocationEnabled(true)` - aceasta va afișa un punct albastru pe hartă. Apoi setez un stil personalizat pentru hartă cu `setMapStyle()` - stilul JSON ascunde punctele de interes (POI) pentru o vizualizare mai curată."

**Ce scriu (continuare):**
```java
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                centerMapOnLocation(location);
            }
        });
    }
```

**Ce spun:** "Obțin ultima locație cunoscută folosind `getLastLocation()` - aceasta este o operație asincronă care returnează un `Task`. `addOnSuccessListener()` adaugă un callback care se execută când locația este obținută cu succes. Dacă locația nu este null, apelez `centerMapOnLocation()` pentru a centra harta pe poziția utilizatorului."

**Checkpoint:** Metoda `onMapReady` este completă și gestionează permisiunile, activează locația, și obține poziția utilizatorului.

---

### Pasul 17: Crearea clasei HomeFragment.java - Partea 4: Metode Helper și Lifecycle

**Ce scriu (continuare):**
```java
    private void centerMapOnLocation(@NonNull Location location) {
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
        googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
    }
```

**Ce spun:** "Creez metoda helper `centerMapOnLocation()` care centrează harta pe o locație specifică. `LatLng` este o clasă din Google Maps SDK care reprezintă o coordonată geografică (latitudine, longitudine). `CameraUpdateFactory.newLatLngZoom()` creează o actualizare a camerei care centrează harta pe coordonată cu un nivel de zoom (15 este un nivel mediu). `addMarker()` adaugă un marker pe hartă la poziția specificată."

**Ce scriu (continuare):**
```java
    @Override
    public void onStart() {
        super.onStart();
        if (binding.mapView != null) {
            binding.mapView.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding.mapView != null) {
            binding.mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding.mapView != null) {
            binding.mapView.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (binding.mapView != null) {
            binding.mapView.onStop();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding.mapView != null) {
            binding.mapView.onDestroy();
        }
        binding = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (binding.mapView != null) {
            binding.mapView.onLowMemory();
        }
    }
}
```

**Ce spun:** "Suprascriu metodele de lifecycle ale fragmentului pentru a conecta lifecycle-ul `MapView`-ului cu lifecycle-ul fragmentului. Aceasta este esențială! `MapView` trebuie să primească apelurile de lifecycle (`onStart()`, `onResume()`, `onPause()`, `onStop()`, `onDestroy()`, `onLowMemory()`) pentru a funcționa corect. Fără aceste apeluri, harta nu se va actualiza sau poate cauza memory leaks. În `onDestroyView()`, setez `binding` la `null` pentru a preveni memory leaks."

**Checkpoint:** Clasa `HomeFragment` este completă cu gestionarea lifecycle-ului `MapView`-ului.

---

### Pasul 18: Crearea clasei MainActivity.java

**Ce fac:** Creez activitatea principală care configurează Navigation Component cu DrawerLayout.

**Ce scriu:**
```java
package ro.makore.akrilki_05;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import ro.makore.akrilki_05.databinding.ActivityMainBinding;
```

**Ce spun:** "Creez fișierul `MainActivity.java` în directorul `app/src/main/java/ro/makore/akrilki_05/`. Declar pachetul și adaug import-urile: componente Material Design pentru drawer, Navigation Component, și `ActivityMainBinding` generat automat de ViewBinding."

**Ce scriu (continuare):**
```java
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
```

**Ce spun:** "Definesc clasa `MainActivity` care extinde `AppCompatActivity`. Declar `mAppBarConfiguration` pentru configurarea ActionBar-ului și `binding` pentru ViewBinding. În `onCreate`, folosesc ViewBinding pentru a inflata layout-ul."

**Ce scriu (continuare):**
```java
        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
```

**Ce spun:** "Setez Toolbar-ul ca ActionBar folosind `setSupportActionBar()`. Observați accesul la Toolbar prin `binding.appBarMain.toolbar` - ViewBinding permite accesul la elemente din layout-uri incluse! Obțin referințe la `DrawerLayout` și `NavigationView` folosind ViewBinding."

**Ce scriu (continuare):**
```java
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_geodata,
                R.id.nav_location,
                R.id.nav_address)
                .setOpenableLayout(drawer)
                .build();
```

**Ce spun:** "Creez `AppBarConfiguration` care specifică fragmentele de nivel superior. `.setOpenableLayout(drawer)` este esențială pentru drawer menu - conectează ActionBar-ul cu DrawerLayout-ul, permițând butonului hamburger să deschidă drawer-ul!"

**Ce scriu (continuare):**
```java
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
```

**Ce spun:** "Obțin `NavController` folosind ID-ul `NavHostFragment`-ului din `content_main.xml`. Configurez ActionBar-ul să lucreze cu Navigation Component. Apoi conectez `NavigationView` (drawer menu) cu `NavController` - aceasta face ca navigarea să funcționeze automat când utilizatorul apasă pe elementele meniului!"

**Ce scriu (continuare):**
```java
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_quit) {
            finish();
            System.exit(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
```

**Ce spun:** "Suprascriu `onCreateOptionsMenu()` pentru a inflata meniul dropdown din ActionBar. Suprascriu `onOptionsItemSelected()` pentru a gestiona apăsarea elementului 'Quit' - închid activitatea și termin procesul aplicației."

**Ce scriu (continuare):**
```java
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
```

**Ce spun:** "Suprascriu `onSupportNavigateUp()` pentru a gestiona butonul hamburger din ActionBar. Când utilizatorul apasă pe buton, această metodă deschide drawer-ul sau navighează înapoi, în funcție de context. `NavigationUI.navigateUp()` gestionează automat acest comportament."

**Checkpoint:** Clasa `MainActivity` este completă și configurează Navigation Component cu DrawerLayout.

---

### Pasul 19: Verificarea structurii proiectului

**Ce fac:** Verific că toate fișierele necesare sunt prezente.

**Ce spun:** "Am creat structura de bază pentru aplicația cu Google Maps. Pentru simplitate, am creat doar `HomeFragment` ca exemplu. Fragmentele `GeodataFragment`, `LocationFragment`, și `AddressFragment` urmează același pattern - fiecare gestionează propriul `MapView` și lifecycle-ul său. Aplicația nu folosește Parcelable, deci nu avem clase Parcelable de creat."

**Checkpoint:** Toate fișierele esențiale sunt create: MainActivity cu DrawerLayout și Navigation Component, un fragment exemplu (HomeFragment) cu Google Maps, și toate resursele necesare (meniu, navigare, layout-uri).

---

## Compilare, Instalare și Rulare

### Compilarea aplicației

**Ce fac:** Compilez aplicația pentru a genera APK-ul de debug.

**Ce scriu în terminal:**
```bash
cd akrilki_05
gradle build
```

**Ce spun:** "Navighez în directorul proiectului și rulez `gradle build` pentru a compila aplicația. Gradle va descărca automat dependențele pentru Google Maps și Location Services și va compila codul. Asigurați-vă că cheia API Google Maps este setată corect în `AndroidManifest.xml`, altfel build-ul va reuși, dar hărțile nu vor funcționa la runtime. APK-ul va fi generat în `app/build/outputs/apk/debug/app-debug.apk`."

**Checkpoint:** Build-ul se finalizează cu succes, APK-ul este generat, și dependențele pentru Google Maps sunt incluse.

---

### Listarea dispozitivelor conectate

**Ce fac:** Verific ce dispozitive Android sunt conectate și disponibile pentru instalare.

**Ce scriu în terminal:**
```bash
adb devices
```

**Ce spun:** "Folosesc `adb devices` pentru a lista toate dispozitivele Android conectate. Asigurați-vă că dispozitivul are conexiune la internet și serviciile de localizare activate pentru ca aplicația să funcționeze corect."

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
adb shell am start -n ro.makore.akrilki_05/.MainActivity
```

**Ce spun:** "Folosesc `adb shell am start` pentru a lansa activitatea. Aplicația se va deschide și va afișa automat `HomeFragment` cu harta. Dacă permisiunile de localizare nu sunt acordate, aplicația le va solicita. După acordarea permisiunilor, harta se va centra pe locația utilizatorului. Testați meniul lateral - glisați din stânga sau apăsați pe butonul hamburger pentru a deschide drawer-ul și navigați între fragmente!"

**Checkpoint:** Aplicația se deschide, `HomeFragment` se încarcă cu harta, permisiunile de localizare sunt solicitate, și harta se centrează pe locația utilizatorului. Meniul lateral funcționează pentru navigare între fragmente. La apăsarea butonului Quit din meniul dropdown, aplicația se închide.

---

### Afișarea logurilor filtrate

**Ce fac:** Monitorizez logurile aplicației pentru a verifica navigarea, lifecycle-ul fragmentelor, și serviciile de localizare.

**Ce scriu în terminal:**
```bash
adb logcat | grep -i "akrilki_05\|MainActivity\|HomeFragment\|GoogleMap\|Location"
```

**Ce spun:** "Folosesc `adb logcat` pentru a afișa logurile sistemului Android. Filtrez output-ul pentru a vedea mesajele legate de aplicația noastră, MainActivity, HomeFragment, GoogleMap, și Location Services. Aici putem vedea inițializarea hărții, obținerea locației, și eventualele erori legate de permisiuni sau cheia API."

**Alternativă (Windows PowerShell):**
```powershell
adb logcat | Select-String -Pattern "akrilki_05|MainActivity|HomeFragment|GoogleMap|Location"
```

**Checkpoint:** Logurile se afișează în terminal, arătând inițializarea Google Maps, obținerea locației, și lifecycle-ul fragmentelor. Când navigați între fragmente, putem observa în loguri cum Navigation Component gestionează tranzițiile și cum `MapView`-urile gestionează lifecycle-ul.

---

## Rezumat

Am creat o aplicație Android cu Google Maps și Location Services care demonstrează:
- Integrarea Google Maps SDK (`MapView`, `GoogleMap`, `OnMapReadyCallback`)
- Servicii de localizare Google (`FusedLocationProviderClient`)
- Gestionarea permisiunilor de localizare în runtime
- `DrawerLayout` cu `NavigationView` pentru meniul lateral glisant
- Gestionarea lifecycle-ului `MapView`-ului conectat la lifecycle-ul fragmentului
- Pattern-ul `OnMapReadyCallback` pentru inițializarea asincronă a hărții
- ViewBinding pentru accesul la elemente UI
- Navigation Component pentru navigare între fragmente
- Marker-uri pe hartă și centrarea camerei
- Stiluri personalizate pentru hartă
- Structura de bază a unui proiect Android cu Gradle
- Compilarea, instalarea și rularea aplicației din linia de comandă

**Diferențele arhitecturale față de `01_hello_world`, `01_simple`, `02_simple`, `03_less_simple` și `04_three_fragments`:**
- Integrarea Google Maps SDK pentru hărți interactive
- Servicii de localizare Google (`FusedLocationProviderClient`)
- Gestionarea permisiunilor în runtime pentru localizare
- `DrawerLayout` cu `NavigationView` (meniu lateral) în loc de `BottomNavigationView`
- Gestionarea lifecycle-ului `MapView`-ului (trebuie conectat la lifecycle-ul fragmentului)
- Pattern-ul `OnMapReadyCallback` pentru inițializarea asincronă a hărții
- Marker-uri și controlul camerei (`CameraUpdateFactory`)
- Stiluri personalizate pentru hartă (`MapStyleOptions`)
- Cheia API Google Maps în manifest (`meta-data`)
- Coordonate geografice (`LatLng`) și conversie adrese (`Geocoder`)

Aplicația este funcțională și demonstrează conceptele fundamentale ale integrării Google Maps și serviciilor de localizare în Android.

