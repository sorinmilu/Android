# Hello world


<!-- TOC -->

- [Hello world](#hello-world)
    - [Compilarea aplicatiei](#compilarea-aplicatiei)
    - [Instalarea aplicatiei pe terminal](#instalarea-aplicatiei-pe-terminal)
    - [Structura aplicatiei](#structura-aplicatiei)
        - [Fisierele sursa](#fisierele-sursa)
            - [MainActivity.java](#mainactivityjava)
            - [AndroidManifest.xml](#androidmanifestxml)
        - [Fisierele care configureaza constructia pachetului](#fisierele-care-configureaza-constructia-pachetului)
            - [app/build.gradle](#appbuildgradle)
            - [build.gradle](#buildgradle)
            - [settings.gradle](#settingsgradle)

<!-- /TOC -->
Aceasta este cea mai simpla aplicatie Android care afiseaza ceva. 

![alt text](images/app_screenshot.jpg)

## Compilarea aplicatiei


```sh
 gradle build

> Task :app:lintReportDebug
Wrote HTML report to file:///C:/Users/sorin/AndroidProjects/Android/Projects/001_hello_world/hello_world/app/build/reports/lint-results-debug.html

BUILD SUCCESSFUL in 22s
85 actionable tasks: 85 executed
PS C:\Users\sorin\AndroidProjects\Android\Projects\001_hello_world\hello_world>
```

## Instalarea aplicatiei pe terminal

```
adb devices
List of devices attached
ZY224F8Q2G      device
```

`adb -s ZY224F8Q2G install .\app\build\outputs\apk\debug\app-debug.apk`

`adb uninstall ro.makore.hello_world`


## Structura aplicatiei


    hello_world/
    ├── app
    │   ├── build.gradle
    │   └── src
    │       └── main
    │           ├── AndroidManifest.xml
    │           └── java
    │               └── ro
    │                   └── makore
    │                       └── hello_world
    │                           └── MainActivity.java
    ├── build.gradle
    ├── gradle.properties
    └── settings.gradle

### Fisierele sursa

#### MainActivity.java

MainActivity.java este fisierul principal al proiectului, singurul fragment de cod java disponibil. 

Codul este compus din urmatoarele:

Declaratia pachetului

```java
package ro.makore.hello_world; // Declara pachetul aplicației
```


Blocul de import al pachetelor
```java
import android.os.Bundle; // Importă clasa pentru manipularea Bundle (utilizată la salvarea/restaurarea stării)
import android.widget.LinearLayout; // Importă clasa pentru gestionarea unui container de tip LinearLayout
import android.widget.TextView; // Importă clasa pentru afișarea textului pe ecran
import androidx.appcompat.app.AppCompatActivity; // Importă clasa de bază pentru activități compatibile cu AppCompat
```

Clase MainActivity care extinde AppCompatActivity

Orice aplicatie Android trebuie sa aiba o activitate principala. O activitate este practic o fereastra in Android care ofera o serie de metode care pot fi extinse pentru a controla comportamentul acesteia. 

AppCompatActivity este o clasă de bază din biblioteca AndroidX, utilizată pentru a crea activități compatibile cu versiunile mai vechi de Android. Aceasta extinde funcționalitatea clasei Activity standard, oferind suport pentru elemente moderne ale interfeței utilizator (cum ar fi bara de acțiuni - action bar) și teme din biblioteca AppCompat.Permite utilizarea funcțiilor moderne ale interfeței pe dispozitive cu versiuni mai vechi de Android avand in acelasi timp suport pentru teme și stiluri moderne: Material Design și alte stiluri personalizate.

Este recomandată utilizarea acestei clase în locul clasei Activity, mai ales pentru aplicații care trebuie să ruleze pe mai multe versiuni de Android.

Codul suprascrie functia onCreate care se executa la crearea ferestrei. Continutul ferestrei este organizat folosind ceea ce numeste un "layout "

un layout este o structură de date care definește modul de organizare și dispunere a componentelor vizuale (de obicei numite "view"-uri) pe o interfață grafică. Un layout acționează ca un container pentru diverse elemente vizuale, precum butoane, câmpuri de text, imagini etc., și determină cum acestea sunt plasate pe ecran. Layout-ul poate aplica reguli de poziționare și dimensionare a fiecărui element din interiorul său, de exemplu, plasând elementele într-o coloană verticală, într-o linie orizontală sau într-o grilă.

În funcție de framework-ul folosit, pot exista diferite tipuri de layout-uri (exemplu, LinearLayout, RelativeLayout sau ConstraintLayout în Android), fiecare având un comportament diferit la plasarea elementelor.

De obicei layoutul este definit intr-un fisier .xml dar acesta poate fi initializat programatic. 

Dupa initializarea layoutului, elemnentele vizuale (view-uri) se plaseaza in interiorul acestuia. La terminarea constructiei layoutului, acesta se asociaza cu activitatea prin metoda `setContentView(layout)`. 

```java
// Definirea clasei principale a activității
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Apelează metoda de bază pentru crearea activității

        // Creează un LinearLayout programatic
        LinearLayout layout = new LinearLayout(this); 
        layout.setOrientation(LinearLayout.VERTICAL); // Setează orientarea verticală pentru layout

        // Creează un TextView care afișează "Hello World"
        TextView textView = new TextView(this); 
        textView.setText("Hello World"); // Setează textul afișat
        textView.setTextSize(24); // Setează dimensiunea textului la 24sp

        // Adaugă TextView-ul în cadrul layout-ului
        layout.addView(textView);

        // Setează layout-ul ca vizualizare principală a activității
        setContentView(layout);
    }
}
```

#### AndroidManifest.xml

AndroidManifest.xml este un fisier care contine metadate esentiale pentru aplicatia android. Este utilizat in timpul compilarii aplicatiei precum si in timpul instalarii si rularii. 

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android" xmlns:tools="http://schemas.android.com/tools">
    
    <application
        android:theme="@style/Theme.AppCompat.Light.NoActionBar"
        android:label="HelloWorld"
        tools:targetApi="34">
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

Acest fragment defineste modul in care sistemul de operare va porni aplicatia: 

```xml
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
```            

 intent filter este o componentă din manifestul Android care permite unei activități, servicii sau receiver de broadcast să specifice tipurile de intents (cereri de acțiuni, date sau evenimente) pe care le poate gestiona. Termenul „filtru” vine de la faptul că intent filter-ul acționează ca un mecanism de filtrare. Acesta specifică tipurile de intents (cereri de acțiune, date sau evenimente) pe care o componentă (de exemplu, o activitate) este dispusă să le gestioneze. Când un intent este trimis de către sistem (de exemplu, pentru a lansa o aplicație sau un serviciu), Android verifică filtrele de intent ale componentelor disponibile pentru a vedea care dintre ele poate gestiona intent-ul pe baza acțiunii, categoriei și datelor specificate.


### Fisierele care configureaza constructia pachetului

Constructia pachetului implica o serie mare de operatii. Acestea sunt automatizate cu ajutorul unui sistem de constructie numit gradle. Gradle asteapta doua fisiere de configurare a constructiei: unul la nivel de proiect si altul la nivel de aplicatie (un proiect poate avea mai multe aplicatii)

Fisierele build.gradle sunt scrise in propriul lor limbaj de programare care poate fi groovy sau kotlin. Diferenta nu este foarte mare. 

#### app/build.gradle

```groovy
apply plugin: 'com.android.application'

android {
    namespace 'ro.makore.hello_world'
    compileSdk 34

    defaultConfig {
        applicationId "ro.makore.hello_world"
        minSdk 28
        targetSdk 34
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

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

dependencies {
    implementation 'androidx.appcompat:appcompat:1.5.1'
}
```



#### build.gradle

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

#### settings.gradle

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

rootProject.name = "hello_world"
include ':app'
```

