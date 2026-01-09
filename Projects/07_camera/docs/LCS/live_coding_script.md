# Script Live Coding - Aplicație Cameră cu SurfaceView

<!-- TOC -->

- [Script Live Coding - Aplicație Cameră cu SurfaceView](#script-live-coding---aplica%C8%9Bie-camer%C4%83-cu-surfaceview)
    - [Prezentare Aplicație](#prezentare-aplica%C8%9Bie)
    - [Structura Directorului Aplicației](#structura-directorului-aplica%C8%9Biei)
    - [Pași Live Coding](#pa%C8%99i-live-coding)
        - [Pasul 1: Crearea structurii de directoare](#pasul-1-crearea-structurii-de-directoare)
        - [Pasul 2: Crearea fișierului settings.gradle](#pasul-2-crearea-fi%C8%99ierului-settingsgradle)
        - [Pasul 3: Crearea fișierului build.gradle la rădăcină](#pasul-3-crearea-fi%C8%99ierului-buildgradle-la-r%C4%83d%C4%83cin%C4%83)
        - [Pasul 4: Crearea fișierului gradle.properties](#pasul-4-crearea-fi%C8%99ierului-gradleproperties)
        - [Pasul 5: Crearea fișierului build.gradle pentru app](#pasul-5-crearea-fi%C8%99ierului-buildgradle-pentru-app)
        - [Pasul 6: Crearea fișierului AndroidManifest.xml](#pasul-6-crearea-fi%C8%99ierului-androidmanifestxml)
        - [Pasul 7: Crearea fișierului strings.xml](#pasul-7-crearea-fi%C8%99ierului-stringsxml)
        - [Pasul 8: Crearea layout-ului activity_main.xml](#pasul-8-crearea-layout-ului-activity_mainxml)
        - [Pasul 9: Crearea clasei MainActivity - Declarații și onCreate](#pasul-9-crearea-clasei-mainactivity---declara%C8%9Bii-%C8%99i-oncreate)
        - [Pasul 10: Implementarea SurfaceHolder.Callback](#pasul-10-implementarea-surfaceholdercallback)
        - [Pasul 11: Implementarea metodei onResume și hasCameraPermission](#pasul-11-implementarea-metodei-onresume-%C8%99i-hascamerapermission)
        - [Pasul 12: Implementarea metodei openCamera](#pasul-12-implementarea-metodei-opencamera)
        - [Pasul 13: Implementarea metodelor pentru flash](#pasul-13-implementarea-metodelor-pentru-flash)
        - [Pasul 14: Implementarea metodei switchCamera](#pasul-14-implementarea-metodei-switchcamera)
        - [Pasul 15: Implementarea metodei takePicture](#pasul-15-implementarea-metodei-takepicture)
        - [Pasul 16: Implementarea metodei saveImage](#pasul-16-implementarea-metodei-saveimage)
        - [Pasul 17: Implementarea metodelor onPause, stopCamera și onRequestPermissionsResult](#pasul-17-implementarea-metodelor-onpause-stopcamera-%C8%99i-onrequestpermissionsresult)
    - [Build, Install și Run](#build-install-%C8%99i-run)
        - [Build APK debug](#build-apk-debug)
        - [Listare dispozitive](#listare-dispozitive)
        - [Instalare APK](#instalare-apk)
        - [Lansare activitate principală](#lansare-activitate-principal%C4%83)
        - [Afișare loguri filtrate](#afi%C8%99are-loguri-filtrate)

<!-- /TOC -->


## Prezentare Aplicație

Această aplicație Android demonstrează interacțiunea cu camera dispozitivului folosind Camera API-ul clasic (deprecated, dar încă funcțional). Aplicația permite utilizatorului să facă fotografii, să comute între camera frontală și cea din spate, și să controleze modul flash-ului.

**Diferențe arhitecturale față de aplicațiile anterioare:**

- **Camera API**: Față de aplicațiile anterioare (`01_hello_world`, `01_simple`, `02_simple`, `03_less_simple`, `04_three_fragments`, `05_maps`, `06_news`) care foloseau doar UI-uri statice sau API-uri externe, această aplicație accesează direct hardware-ul dispozitivului (camera) folosind Camera API-ul clasic. Aceasta este prima aplicație care interacționează cu hardware-ul nativ al dispozitivului.

- **SurfaceView și SurfaceHolder**: Aplicația folosește `SurfaceView` pentru afișarea preview-ului camerei în timp real. `SurfaceView` permite redarea conținutului pe un thread separat, esențial pentru fluxul video al camerei. `SurfaceHolder` oferă control asupra suprafeței prin callback-uri pentru evenimentele de creare, schimbare și distrugere.

- **Runtime Permissions**: Aplicația gestionează permisiunile runtime pentru accesul la cameră, verificând și solicitând permisiunea `CAMERA` la runtime (Android 6.0+). Aceasta demonstrează pattern-ul de gestionare a permisiunilor sensibile.

- **Camera Lifecycle Management**: Aplicația gestionează corect ciclul de viață al camerei, deschizând-o în `onResume`, oprind-o în `onPause`, și eliberând resursele în `surfaceDestroyed`. Aceasta este crucială pentru a evita blocarea camerei de către alte aplicații.

- **File I/O pentru Imagini**: Aplicația salvează imaginile capturate pe stocarea externă folosind `getExternalFilesDir`, demonstrând gestionarea fișierelor și I/O-ul pentru date binare (imagini JPEG).

- **Hardware Feature Detection**: Aplicația verifică dacă dispozitivul suportă flash și dacă există cameră frontală, demonstrând detectarea caracteristicilor hardware disponibile.

**Fluxul de date:** Utilizator → Butoane UI → MainActivity → Camera API → SurfaceView (preview) → File System (salvare imagine)

**No Parcelable in this project; skipping.**

## Structura Directorului Aplicației

```
akrilki_07/
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/
│           │   └── ro/
│           │       └── makore/
│           │           └── akrilki_07/
│           │               └── MainActivity.java
│           └── res/
│               ├── drawable/
│               │   ├── ic_launcher_7.png
│               │   └── ic_launcher_round_7.png
│               ├── layout/
│               │   └── activity_main.xml
│               └── values/
│                   ├── colors.xml
│                   ├── strings.xml
│                   └── themes.xml
├── build.gradle
├── gradle.properties
└── settings.gradle
```

## Pași Live Coding

### Pasul 1: Crearea structurii de directoare

**Actiuni:** Creez structura completă de directoare pentru aplicația Android.

**Ce scriu în terminal:**
```bash
mkdir -p akrilki_07/app/src/main/java/ro/makore/akrilki_07
mkdir -p akrilki_07/app/src/main/res/{layout,drawable,values}
```

**Note:** "Vom începe prin a crea structura de directoare pentru aplicația noastră de cameră. Această aplicație este mai simplă din punct de vedere structural decât aplicațiile anterioare - nu avem nevoie de directoare separate pentru model, adapter sau API, deoarece toată logica este în MainActivity."

**Checkpoint:** Structura de directoare este creată.

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

rootProject.name = "akrilki_07"
include ':app'
```

**Note:** "Creez fișierul `settings.gradle` cu configurația standard. Observați că numele proiectului este `akrilki_07`."

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

**Note:** "Creez fișierul `build.gradle` la rădăcină care configurează plugin-ul Android pentru Gradle."

**Checkpoint:** Fișierul `build.gradle` la rădăcină este creat.

---

### Pasul 4: Crearea fișierului gradle.properties

**Actiuni:** Creez fișierul `gradle.properties` cu configurații pentru build.

**Ce scriu:**
```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.enableJetifier=true
```

**Note:** "Creez fișierul `gradle.properties` cu configurații standard pentru AndroidX și encoding."

**Checkpoint:** Fișierul `gradle.properties` este creat.

---

### Pasul 5: Crearea fișierului build.gradle pentru app

**Actiuni:** Creez fișierul `app/build.gradle` cu dependențele necesare.

**Ce scriu:**
```groovy
apply plugin: 'com.android.application'

android {
    compileSdkVersion 34
    namespace 'ro.makore.akrilki_07'

    defaultConfig {
        applicationId "ro.makore.akrilki_07"
        minSdkVersion 21
        targetSdkVersion 34
        versionCode 1
        versionName "1.0"
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
    implementation 'androidx.appcompat:appcompat:1.4.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.1'
}
```

**Note:** "Creez fișierul `build.gradle` pentru modulul app. Observați că `minSdkVersion` este 21, necesar pentru Camera API-ul clasic. Dependențele sunt minime - doar AppCompat și ConstraintLayout. Camera API-ul clasic este parte din Android SDK și nu necesită dependențe externe."

**Checkpoint:** Fișierul `app/build.gradle` este creat.

---

### Pasul 6: Crearea fișierului AndroidManifest.xml

**Actiuni:** Creez fișierul `AndroidManifest.xml` cu permisiunile și declarațiile necesare pentru cameră.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.CAMERA" />
    <uses-feature android:name="android.hardware.camera" />
    <uses-feature android:name="android.hardware.camera.front" android:required="false" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-feature android:name="android.hardware.camera.flash" />

    <application
        android:allowBackup="true"
        android:label="@string/app_name"
        android:theme="@style/Theme.Akrilki07">

        <activity
            android:name=".MainActivity"
            android:label="@string/app_name"
            android:exported="true"
            android:screenOrientation="landscape">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>
```

**Note:** "Creez fișierul `AndroidManifest.xml`. Observați permisiunile: `CAMERA` pentru accesul la cameră, `WRITE_EXTERNAL_STORAGE` și `READ_EXTERNAL_STORAGE` pentru salvarea imaginilor. Declar și caracteristicile hardware: `camera`, `camera.front` (opțional) și `camera.flash`. Activitatea este setată în modul landscape pentru o experiență mai bună cu camera."

**Checkpoint:** Fișierul `AndroidManifest.xml` este creat.

---

### Pasul 7: Crearea fișierului strings.xml

**Actiuni:** Creez fișierul `strings.xml` cu string-urile aplicației.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Akrilki07 Camera App</string>
</resources>
```

**Note:** "Creez fișierul `strings.xml` cu numele aplicației."

**Checkpoint:** Fișierul `strings.xml` este creat.

---

### Pasul 8: Crearea layout-ului activity_main.xml

**Actiuni:** Creez layout-ul principal cu SurfaceView pentru preview-ul camerei și butoanele de control.

**Ce scriu în `res/layout/activity_main.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <SurfaceView
        android:id="@+id/surface_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

**Note:** "Creez layout-ul principal folosind `RelativeLayout`. Adaug un `SurfaceView` care va afișa preview-ul camerei. `SurfaceView` ocupă tot ecranul și permite redarea conținutului pe un thread separat, esențial pentru fluxul video al camerei."

**Ce scriu în continuare:**
```xml
    <Button
        android:id="@+id/btnSwitchCamera"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Switch Camera"
        android:layout_alignParentBottom="true"
        android:layout_alignParentLeft="true" />

    <Button
        android:id="@+id/btnTakePicture"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Take Picture"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true" />

    <Button
        android:id="@+id/btnFlashMode"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Flash Mode"
        android:layout_alignParentBottom="true"
        android:layout_alignParentRight="true" />
</RelativeLayout>
```

**Note:** "Adaug trei butoane în partea de jos a ecranului: `btnSwitchCamera` pentru comutarea între camere, `btnTakePicture` pentru capturarea unei fotografii și `btnFlashMode` pentru controlul flash-ului. Butoanele sunt poziționate folosind `RelativeLayout` - unul la stânga, unul centrat și unul la dreapta."

**Checkpoint:** Layout-ul `activity_main.xml` este creat.

---

### Pasul 9: Crearea clasei MainActivity - Declarații și onCreate

**Actiuni:** Creez clasa `MainActivity` cu declarațiile de câmpuri și metoda `onCreate`.

**Ce scriu în `MainActivity.java`:**
```java
package ro.makore.akrilki_07;

import android.Manifest;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.hardware.Camera.Parameters;
import android.os.Environment;
import android.os.Looper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;
import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {
    private Camera camera;
    private int currentCameraId = 0; // 0 = back camera, 1 = front camera
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private boolean isSurfaceReady = false;
    private boolean isPermissionRequested = false;
    private Button btnSwitchCamera, btnTakePicture, btnFlashMode;
```

**Note:** "Creez clasa `MainActivity` cu toate import-urile necesare. Declar câmpurile: `camera` pentru instanța camerei, `currentCameraId` pentru a ține evidența camerei curente (0 = spate, 1 = față), `surfaceView` și `surfaceHolder` pentru preview, flag-uri pentru gestionarea stării și referințele la butoane."

**Ce scriu în continuare:**
```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            }
        }
```

**Note:** "În `onCreate`, setez layout-ul. Apoi verific dacă dispozitivul rulează Android 6.0 (Marshmallow) sau mai nou și, dacă permisiunea pentru cameră nu a fost acordată, o solicit utilizatorului. Aceasta este gestionarea inițială a permisiunilor runtime."

**Ce scriu în continuare:**
```java
        btnSwitchCamera = findViewById(R.id.btnSwitchCamera);
        btnTakePicture = findViewById(R.id.btnTakePicture);
        btnFlashMode = findViewById(R.id.btnFlashMode);
        surfaceView = (SurfaceView)this.findViewById(R.id.surface_view);
        surfaceHolder = surfaceView.getHolder();
```

**Note:** "Obțin referințele la butoane și la `SurfaceView`. Apoi obțin `SurfaceHolder` din `SurfaceView` - acesta este interfața care permite controlul suprafeței și înregistrarea callback-urilor pentru evenimentele de lifecycle."

**Checkpoint:** Clasa `MainActivity` este creată cu declarațiile și `onCreate` parțial implementat.

---

### Pasul 10: Implementarea SurfaceHolder.Callback

**Actiuni:** Implementez callback-urile pentru `SurfaceHolder` care gestionează lifecycle-ul suprafeței.

**Ce scriu în continuare în `onCreate`:**
```java
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                isSurfaceReady = true;
                if (hasCameraPermission()) {
                    openCamera(currentCameraId);
                }
            }
```

**Note:** "Adaug un callback pentru `SurfaceHolder`. Metoda `surfaceCreated` este apelată când suprafața este creată și gata de utilizare. Setez flag-ul `isSurfaceReady` și, dacă permisiunea este acordată, deschid camera."

**Ce scriu în continuare:**
```java
            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                if (camera != null) {
                    camera.stopPreview();
                    try {
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error restarting camera preview: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
```

**Note:** "Implementez `surfaceChanged` care este apelată când suprafața se schimbă (de exemplu, la rotirea ecranului). Oprește preview-ul, setează noua suprafață și repornește preview-ul. Aceasta asigură că preview-ul se adaptează la noile dimensiuni."

**Ce scriu în continuare:**
```java
            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                isSurfaceReady = false;
                stopCamera();
            }
        });
```

**Note:** "Implementez `surfaceDestroyed` care este apelată când suprafața este distrusă. Setez flag-ul și opresc camera pentru a elibera resursele. Aceasta este crucială pentru a evita blocarea camerei."

**Ce scriu în continuare:**
```java
        btnSwitchCamera.setOnClickListener(v -> switchCamera());
        btnTakePicture.setOnClickListener(v -> takePicture());
        btnFlashMode.setOnClickListener(v -> toggleFlashMode());
    }
```

**Note:** "Conectez butoanele la metodele lor respective folosind lambda expressions. Închid metoda `onCreate`."

**Checkpoint:** Callback-urile `SurfaceHolder` sunt implementate și butoanele sunt conectate.

---

### Pasul 11: Implementarea metodei onResume și hasCameraPermission

**Actiuni:** Implementez `onResume` pentru gestionarea lifecycle-ului și metoda helper pentru verificarea permisiunilor.

**Ce scriu:**
```java
    @Override
    protected void onResume() {
        super.onResume();
        if (!hasCameraPermission() && !isPermissionRequested) {
            isPermissionRequested = true;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else if (hasCameraPermission() && isSurfaceReady) {
            openCamera(currentCameraId);
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
```

**Note:** "Implementez `onResume` care este apelată când activitatea devine vizibilă. Verific permisiunile și, dacă sunt acordate și suprafața este gata, deschid camera. `hasCameraPermission` verifică dacă permisiunea CAMERA a fost acordată folosind `ContextCompat.checkSelfPermission`."

**Checkpoint:** Metodele `onResume` și `hasCameraPermission` sunt implementate.

---

### Pasul 12: Implementarea metodei openCamera

**Actiuni:** Implementez metoda `openCamera` care deschide camera și configurează preview-ul.

**Ce scriu:**
```java
    private void openCamera(int cameraId) {
        try {
            // Release the previous camera if exists
            if (camera != null) {
                camera.stopPreview();
                camera.release();
            }

            // Open the selected camera (0 for back camera, 1 for front camera)
            camera = Camera.open(cameraId);
            Camera.Parameters params = camera.getParameters();

            if (isFlashSupported()) {
                updateFlashButtonText();
            }

            camera.setParameters(params);
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to open camera", Toast.LENGTH_SHORT).show();
        }
    }
```

**Note:** "Implementez `openCamera` care primește ID-ul camerei. Mai întâi, dacă există deja o cameră deschisă, o opresc și o eliberez pentru a evita memory leaks. Apoi deschid camera cu ID-ul specificat folosind `Camera.open`. Obțin parametrii camerei, verific dacă flash-ul este suportat și actualizez textul butonului. Setez preview display-ul pe `surfaceHolder` și pornesc preview-ul."

**Checkpoint:** Metoda `openCamera` este implementată.

---

### Pasul 13: Implementarea metodelor pentru flash

**Actiuni:** Implementez metodele pentru gestionarea flash-ului.

**Ce scriu:**
```java
    private void updateFlashButtonText() {
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            String currentMode = params.getFlashMode();

            if (Camera.Parameters.FLASH_MODE_OFF.equals(currentMode)) {
                btnFlashMode.setText("Flash: Off");
            } else if (Camera.Parameters.FLASH_MODE_AUTO.equals(currentMode)) {
                btnFlashMode.setText("Flash: Auto");
            } else if (Camera.Parameters.FLASH_MODE_ON.equals(currentMode)) {
                btnFlashMode.setText("Flash: On");
            }
        }
    }

    private boolean isFlashSupported() {
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            List<String> supportedModes = params.getSupportedFlashModes();
            return supportedModes != null && !supportedModes.isEmpty();
        }
        return false;
    }
```

**Note:** "Implementez `updateFlashButtonText` care actualizează textul butonului flash în funcție de modul curent (Off, Auto, On). `isFlashSupported` verifică dacă dispozitivul suportă flash prin verificarea listei de moduri suportate din parametrii camerei."

**Ce scriu în continuare:**
```java
    private void toggleFlashMode() {
        if (camera != null && isFlashSupported()) {
            Camera.Parameters params = camera.getParameters();
            String currentMode = params.getFlashMode();
            String nextMode;

            // Cycle through flash modes
            if (Camera.Parameters.FLASH_MODE_OFF.equals(currentMode)) {
                nextMode = Camera.Parameters.FLASH_MODE_AUTO;
            } else if (Camera.Parameters.FLASH_MODE_AUTO.equals(currentMode)) {
                nextMode = Camera.Parameters.FLASH_MODE_ON;
            } else {
                nextMode = Camera.Parameters.FLASH_MODE_OFF;
            }

            camera.stopPreview();
            params.setFlashMode(nextMode);
            camera.setParameters(params);
            camera.startPreview();
            updateFlashButtonText();
        } else {
            Toast.makeText(this, "Flash not supported", Toast.LENGTH_SHORT).show();
        }
    }
```

**Note:** "Implementez `toggleFlashMode` care ciclă prin modurile flash: Off → Auto → On → Off. Oprește preview-ul, setează noul mod flash, repornește preview-ul și actualizează textul butonului. Dacă flash-ul nu este suportat, afișez un mesaj Toast."

**Checkpoint:** Metodele pentru gestionarea flash-ului sunt implementate.

---

### Pasul 14: Implementarea metodei switchCamera

**Actiuni:** Implementez metoda `switchCamera` care comută între camera frontală și cea din spate.

**Ce scriu:**
```java
    private void switchCamera() {
        currentCameraId = (currentCameraId == 0) ? 1 : 0; // Toggle between front and back
        openCamera(currentCameraId);
    }
```

**Note:** "Implementez `switchCamera` care comută ID-ul camerei între 0 (spate) și 1 (față) folosind un operator ternar, apoi deschide noua cameră."

**Checkpoint:** Metoda `switchCamera` este implementată.

---

### Pasul 15: Implementarea metodei takePicture

**Actiuni:** Implementez metoda `takePicture` care capturează o fotografie.

**Ce scriu:**
```java
    private void takePicture() {
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            String currentFlashMode = params.getFlashMode();

            if (Camera.Parameters.FLASH_MODE_ON.equals(currentFlashMode)) {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            } else {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            camera.setParameters(params);

            new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
                camera.takePicture(null, null, (data, camera) -> {
                    saveImage(data);
                    camera.startPreview();
                });
            }, 200);
        }
    }
```

**Note:** "Implementez `takePicture`. Verific dacă camera este disponibilă, obțin parametrii și asigur că flash-ul este setat corect. Folosesc un `Handler` cu delay de 200ms pentru a permite flash-ului să se activeze înainte de captură. Apelez `camera.takePicture` cu trei parametri: primul și al doilea sunt null (pentru ShutterCallback și PictureCallback intermediar), al treilea este un lambda care primește datele imaginii ca byte array. În callback, salvez imaginea și repornesc preview-ul."

**Checkpoint:** Metoda `takePicture` este implementată.

---

### Pasul 16: Implementarea metodei saveImage

**Actiuni:** Implementez metoda `saveImage` care salvează imaginea pe stocarea externă.

**Ce scriu:**
```java
    private void saveImage(byte[] data) {
        FileOutputStream fos = null;
        try {
            File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CameraApp");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = "image_" + System.currentTimeMillis() + ".jpg";
            File pictureFile = new File(directory, fileName);

            fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.flush();

            Toast.makeText(this, "Image saved: " + pictureFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
```

**Note:** "Implementez `saveImage` care primește datele imaginii ca byte array. Folosesc `getExternalFilesDir` cu `DIRECTORY_PICTURES` pentru a obține directorul pentru imagini al aplicației, apoi creez subdirectorul 'CameraApp' dacă nu există. Generez un nume de fișier unic folosind timestamp-ul curent. Scriu datele în fișier folosind `FileOutputStream`, flush pentru a mă asigura că datele sunt scrise, și afișez calea completă într-un Toast. Gestionez erorile cu try-catch-finally și închid stream-ul în blocul finally."

**Checkpoint:** Metoda `saveImage` este implementată.

---

### Pasul 17: Implementarea metodelor onPause, stopCamera și onRequestPermissionsResult

**Actiuni:** Implementez metodele finale pentru gestionarea lifecycle-ului și permisiunilor.

**Ce scriu:**
```java
    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private void stopCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera(currentCameraId);
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
```

**Note:** "Implementez `onPause` care oprește și eliberează camera când activitatea este pusă pe pauză - esențial pentru a evita blocarea camerei. `stopCamera` este o metodă helper care face același lucru și este apelată din `surfaceDestroyed`. `onRequestPermissionsResult` este apelată când utilizatorul răspunde la cererea de permisiune - dacă permisiunea este acordată, deschid camera, altfel afișez un mesaj."

**Checkpoint:** Toate metodele pentru gestionarea lifecycle-ului și permisiunilor sunt implementate.

---

## Build, Install și Run

### Build APK debug

**Actiuni:** Compilez aplicația într-un APK debug.

**Ce scriu în terminal:**
```bash
cd akrilki_07
gradle assembleDebug
```

**Note:** "Compilez aplicația folosind Gradle. Comanda `assembleDebug` creează un APK debug în `app/build/outputs/apk/debug/`."

**Checkpoint:** APK-ul debug este generat.

---

### Listare dispozitive

**Actiuni:** Listez dispozitivele Android conectate.

**Ce scriu în terminal:**
```bash
adb devices
```

**Note:** "Listez dispozitivele conectate. Asigurați-vă că aveți un emulator pornit sau un dispozitiv fizic conectat prin USB cu USB debugging activat. Pentru aplicația de cameră, recomand un dispozitiv fizic sau un emulator cu suport pentru cameră."

**Checkpoint:** Dispozitivul este listat.

---

### Instalare APK

**Actiuni:** Instalez APK-ul pe dispozitiv.

**Ce scriu în terminal:**
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

**Note:** "Instalez APK-ul pe dispozitiv. Flag-ul `-r` permite reinstalarea dacă aplicația există deja."

**Checkpoint:** Aplicația este instalată pe dispozitiv.

---

### Lansare activitate principală

**Actiuni:** Lansez activitatea principală.

**Ce scriu în terminal:**
```bash
adb shell am start -n ro.makore.akrilki_07/.MainActivity
```

**Note:** "Lansez activitatea principală folosind `am start` cu numele complet al pachetului și activității. Aplicația va solicita permisiunea pentru cameră la prima lansare."

**Checkpoint:** Aplicația se deschide și solicită permisiunea pentru cameră.

---

### Afișare loguri filtrate

**Actiuni:** Afișez logurile filtrate pentru aplicație.

**Ce scriu în terminal:**
```bash
adb logcat | grep -E "(MainActivity|Camera|AndroidRuntime)"
```

**Note:** "Afișez logurile filtrate pentru tag-urile relevante (`MainActivity`, `Camera`) și erorile (`AndroidRuntime`). Observați mesajele legate de deschiderea camerei, permisiuni și salvarea imaginilor."

**Checkpoint:** Logurile sunt afișate și pot fi monitorizate pentru debugging.


