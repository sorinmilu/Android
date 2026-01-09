# Script Live Coding - Aplicație Știri cu RecyclerView și API

## Prezentare Aplicație

Această aplicație Android preia știri de la un API extern și le afișează într-o listă scrollabilă folosind `RecyclerView`. Aplicația are două activități: `MainActivity` care afișează lista de știri și `NewsDetailActivity` care afișează detaliile unei știri selectate. 

**Diferențe arhitecturale față de aplicațiile anterioare:**

- **RecyclerView**: Față de aplicațiile `01_hello_world`, `01_simple`, `02_simple`, `03_less_simple`, `04_three_fragments` și `05_maps` care foloseau layout-uri statice sau fragmente, această aplicație introduce `RecyclerView` pentru afișarea eficientă a listelor de date. RecyclerView este optimizat pentru performanță și permite afișarea unui număr mare de elemente fără consum excesiv de memorie.

- **Parcelable**: Aplicația folosește interfața `Parcelable` pentru transferul eficient de obiecte complexe (`NewsItem`) între activități prin `Intent`. Aceasta este mai eficientă decât `Serializable` și este pattern-ul recomandat în Android.

- **HTTP API Calls**: Aplicația face apeluri HTTP reale către un API extern (newsapi.ai) folosind biblioteca `OkHttp`. Aceasta este prima aplicație care integrează date externe prin rețea.

- **JSON Parsing**: Datele primite de la API sunt în format JSON și sunt parsate folosind biblioteca `Gson`. Aplicația demonstrează parsarea structurilor JSON complexe.

- **Image Loading**: Aplicația încarcă imagini din URL-uri folosind biblioteca `Glide`, demonstrând gestionarea asincronă a încărcării resurselor externe.

- **Background Threading**: Operațiile de rețea rulează pe un thread separat folosind `ExecutorService`, evitând blocarea thread-ului principal UI.

- **Assets Folder**: Aplicația folosește fișiere JSON din folderul `assets` pentru configurarea request-urilor API, demonstrând gestionarea resurselor statice.

- **Date/Time Processing**: Aplicația folosește biblioteca `ThreeTenABP` pentru procesarea datelor și orelor, necesară pentru API level-uri mai vechi decât 26.

**Fluxul de date:** MainActivity → NewsAPI (HTTP request) → NewsParser (JSON parsing) → NewsAdapter (RecyclerView) → NewsDetailActivity (prin Intent cu Parcelable)

## Structura Directorului Aplicației

```
akrilki_06/
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── assets/
│           │   ├── api_key.json
│           │   └── request_body.json
│           ├── java/
│           │   └── ro/
│           │       └── makore/
│           │           └── akrilki_06/
│           │               ├── MainActivity.java
│           │               ├── NewsDetailActivity.java
│           │               ├── adapter/
│           │               │   └── NewsAdapter.java
│           │               ├── api/
│           │               │   └── NewsAPI.java
│           │               ├── model/
│           │               │   └── NewsItem.java
│           │               └── parser/
│           │                   └── NewsParser.java
│           └── res/
│               ├── drawable/
│               │   ├── ic_launcher_6.png
│               │   ├── ic_launcher_round_6.png
│               │   ├── ic_quit_black_24dp.xml
│               │   └── ic_refresh_black_24dp.xml
│               ├── layout/
│               │   ├── activity_main.xml
│               │   ├── activity_news_detail.xml
│               │   └── item_news.xml
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

**Ce fac:** Creez structura completă de directoare pentru aplicația Android, inclusiv directoarele pentru model, adapter, API, parser și assets.

**Ce scriu în terminal:**
```bash
mkdir -p akrilki_06/app/src/main/java/ro/makore/akrilki_06/{adapter,api,model,parser}
mkdir -p akrilki_06/app/src/main/res/{layout,drawable,values}
mkdir -p akrilki_06/app/src/main/assets
```

**Ce spun:** "Vom începe prin a crea structura de directoare pentru aplicația noastră. Observați că am adăugat directoare separate pentru model, adapter, API și parser. Aceasta este o organizare modulară care separă responsabilitățile: modelul pentru date, adapterul pentru RecyclerView, API-ul pentru comunicarea cu serverul și parserul pentru procesarea JSON. De asemenea, am adăugat directorul `assets` unde vom stoca fișierele JSON pentru configurarea request-urilor API."

**Checkpoint:** Structura de directoare este creată, inclusiv directoarele pentru organizarea modulară a codului.

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

rootProject.name = "akrilki_06"
include ':app'
```

**Ce spun:** "Creez fișierul `settings.gradle` cu configurația standard. Observați că numele proiectului este `akrilki_06`."

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

**Ce spun:** "Creez fișierul `build.gradle` la rădăcină care configurează plugin-ul Android pentru Gradle."

**Checkpoint:** Fișierul `build.gradle` la rădăcină este creat.

---

### Pasul 4: Crearea fișierului gradle.properties

**Ce fac:** Creez fișierul `gradle.properties` cu configurații pentru build.

**Ce scriu:**
```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.enableJetifier=true
```

**Ce spun:** "Creez fișierul `gradle.properties` cu configurații standard pentru AndroidX și encoding."

**Checkpoint:** Fișierul `gradle.properties` este creat.

---

### Pasul 5: Crearea fișierului build.gradle pentru app

**Ce fac:** Creez fișierul `app/build.gradle` cu toate dependențele necesare.

**Ce scriu:**
```groovy
apply plugin: 'com.android.application'

android {
    compileSdkVersion 34
    namespace 'ro.makore.akrilki_06'

    defaultConfig {
        minSdkVersion 21
        targetSdkVersion 34
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
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

    buildFeatures {
        viewBinding true
    }
}

dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.0'
    implementation 'androidx.cardview:cardview:1.0.0'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'com.squareup.okhttp3:okhttp:4.10.0'
    implementation 'com.google.code.gson:gson:2.8.8'
    implementation 'com.github.bumptech.glide:glide:4.13.0'
    implementation 'com.jakewharton.threetenabp:threetenabp:1.4.4'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
```

**Ce spun:** "Creez fișierul `build.gradle` pentru modulul app. Observați dependențele: `recyclerview` pentru listele scrollabile, `okhttp` pentru apeluri HTTP, `gson` pentru parsarea JSON, `glide` pentru încărcarea imaginilor și `threetenabp` pentru procesarea datelor. De asemenea, activez `viewBinding` pentru accesul tip-safe la view-uri."

**Checkpoint:** Fișierul `app/build.gradle` este creat cu toate dependențele.

---

### Pasul 6: Crearea fișierului AndroidManifest.xml

**Ce fac:** Creez fișierul `AndroidManifest.xml` cu permisiunile și declarațiile activităților.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:allowBackup="true"
        android:label="@string/app_name"
        android:theme="@style/Theme.Akrilki06">

        <activity
            android:name=".MainActivity"
            android:label="@string/app_name"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity
            android:name=".NewsDetailActivity"
            android:label="@string/news_detail_activity_label" />

    </application>

</manifest>
```

**Ce spun:** "Creez fișierul `AndroidManifest.xml`. Observați permisiunile `INTERNET` și `ACCESS_NETWORK_STATE` necesare pentru apelurile API. Declar două activități: `MainActivity` ca activitate principală și `NewsDetailActivity` pentru afișarea detaliilor."

**Checkpoint:** Fișierul `AndroidManifest.xml` este creat.

---

### Pasul 7: Crearea fișierului strings.xml

**Ce fac:** Creez fișierul `strings.xml` cu string-urile aplicației.

**Ce scriu:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Akrilki06 News App</string>
    <string name="thumbnail_description">Thumbnail image of the news</string>
    <string name="action_back">Back</string>
    <string name="no_news_found">No news found</string>
    <string name="news_detail_activity_label">News detail</string>
</resources>
```

**Ce spun:** "Creez fișierul `strings.xml` cu toate string-urile aplicației pentru localizare și reutilizare."

**Checkpoint:** Fișierul `strings.xml` este creat.

---

### Pasul 8: Crearea fișierelor JSON în assets

**Ce fac:** Creez fișierele JSON pentru configurarea API-ului.

**Ce scriu în `assets/request_body.json`:**
```json
{
    "query": {
      "$query": {
        "lang": "eng"
      },
      "$filter": {
        "forceMaxDataTimeWindow": "31",
        "dataType": ["news", "blog"],
        "isDuplicate": "skipDuplicates"
      }
    },
    "resultType": "articles",
    "articlesSortBy": "date",
    "includeArticleConcepts": true,
    "includeArticleImage": true,
    "articlesPage": 1,
    "articlesCount": 50
}
```

**Ce scriu în `assets/api_key.json`:**
```json
{
    "apiKey": "9506e4f0-519a-4c9b-87b4-0c4ca8009126"
}
```

**Ce spun:** "Creez două fișiere JSON în directorul `assets`. Primul, `request_body.json`, conține configurația pentru request-ul API - limba, filtrele, tipul de rezultate. Al doilea, `api_key.json`, conține cheia API. Această abordare permite modificarea ușoară a parametrilor fără a recompila codul."

**Checkpoint:** Fișierele JSON sunt create în directorul `assets`.

---

### Pasul 9: Crearea clasei NewsItem (Parcelable)

**Ce fac:** Creez clasa `NewsItem` care implementează interfața `Parcelable` pentru transferul eficient între activități.

**Ce scriu în `model/NewsItem.java`:**
```java
package ro.makore.akrilki_06.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class NewsItem implements Parcelable {
    private String title;
    private String body;
    private String thumbnailUrl;
    private String language;
    private String source;
    private String datetime;
    private List<String> concepts;
```

**Ce spun:** "Creez clasa `NewsItem` care implementează `Parcelable`. Această interfață permite serializarea eficientă a obiectelor pentru transfer între activități. Declar câmpurile private pentru o știre: titlu, corp, URL imagine, limbă, sursă, dată și lista de concepte."

**Ce scriu în continuare:**
```java
    // Constructor normal
    public NewsItem() {
    }

    // Constructor Parcelable - aceeași ordine ca în writeToParcel
    protected NewsItem(Parcel in) {
        title = in.readString();
        body = in.readString();
        thumbnailUrl = in.readString();
        language = in.readString();
        source = in.readString();
        datetime = in.readString();
        concepts = in.createStringArrayList();
    }
```

**Ce spun:** "Adaug două constructori: unul normal pentru crearea obiectelor noi și unul special care primește un `Parcel` pentru deserializare. Ordinea citirii din Parcel trebuie să fie identică cu ordinea scrierii în `writeToParcel`."

**Ce scriu în continuare:**
```java
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(body);
        dest.writeString(thumbnailUrl);
        dest.writeString(language);
        dest.writeString(source);
        dest.writeString(datetime);
        dest.writeStringList(concepts);
    }

    @Override
    public int describeContents() {
        return 0;
    }
```

**Ce spun:** "Implementez metoda `writeToParcel` care scrie toate câmpurile în Parcel în aceeași ordine ca în constructor. Metoda `describeContents` returnează 0 pentru obiecte simple fără file descriptors."

**Ce scriu în continuare:**
```java
    public static final Creator<NewsItem> CREATOR = new Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };
```

**Ce spun:** "Creez constanta `CREATOR` care este obligatorie pentru `Parcelable`. Aceasta conține două metode: `createFromParcel` care creează un obiect din Parcel și `newArray` care creează un array de obiecte. Android folosește acest CREATOR pentru a reconstrui obiectele."

**Ce scriu în continuare:**
```java
    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public List<String> getConcepts() {
        return concepts;
    }

    public void setConcepts(List<String> concepts) {
        this.concepts = concepts;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDateTime() {
        return datetime;
    }

    public void setDateTime(String datetime) {
        this.datetime = datetime;
    }
}
```

**Ce spun:** "Adaug getter-ele și setter-ele pentru toate câmpurile. Acestea permit accesul controlat la datele obiectului."

**Checkpoint:** Clasa `NewsItem` este creată cu implementarea completă a `Parcelable`.

---

### Pasul 10: Crearea clasei NewsAPI

**Ce fac:** Creez clasa `NewsAPI` care face apeluri HTTP către API-ul de știri.

**Ce scriu în `api/NewsAPI.java`:**
```java
package ro.makore.akrilki_06.api;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import java.io.IOException;
import java.io.InputStream;
import org.json.JSONObject;

public class NewsAPI {
    private static final String API_URL = "https://eventregistry.org/api/v1/article/getArticles";
```

**Ce spun:** "Creez clasa `NewsAPI` care va gestiona comunicarea cu API-ul. Declar URL-ul API-ului ca constantă statică."

**Ce scriu în continuare:**
```java
    private static String readJsonFromAssets(Context context, String fileName) throws IOException {
        InputStream is = context.getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, "UTF-8");
    }
```

**Ce spun:** "Creez o metodă helper privată `readJsonFromAssets` care citește un fișier JSON din directorul `assets`. Folosesc `InputStream` pentru a citi fișierul ca array de bytes, apoi îl convertesc în String cu encoding UTF-8."

**Ce scriu în continuare:**
```java
    public static String fetchNews(Context context) throws Exception {
        OkHttpClient client = new OkHttpClient();

        // Read the JSON body from the assets folder and the API key
        String jsonBody = readJsonFromAssets(context, "request_body.json");
        String apiKeyJson = readJsonFromAssets(context, "api_key.json");

        // Parse them into JSON objects
        JSONObject requestBody = new JSONObject(jsonBody);
        JSONObject apiKeyObject = new JSONObject(apiKeyJson);

        // Add the API key dynamically
        requestBody.put("apiKey", apiKeyObject.getString("apiKey"));

        // Convert updated JSON object back to a string
        jsonBody = requestBody.toString();
```

**Ce spun:** "Implementez metoda statică `fetchNews`. Inițializez un client OkHttp. Citesc ambele fișiere JSON din assets, le parsez în obiecte JSONObject, apoi adaug cheia API din `api_key.json` în request body. Convertesc înapoi la String pentru a-l folosi în request."

**Ce scriu în continuare:**
```java
        // Define the media type for JSON
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        // RequestBody with JSON data
        RequestBody body = RequestBody.create(JSON, jsonBody);
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
```

**Ce spun:** "Creez `MediaType` pentru JSON, apoi creez `RequestBody` cu datele JSON. Construiesc request-ul HTTP POST cu URL-ul și body-ul. Execut request-ul și returnez răspunsul ca String. Folosesc try-with-resources pentru a închide automat response-ul."

**Checkpoint:** Clasa `NewsAPI` este creată și poate face apeluri HTTP către API.

---

### Pasul 11: Crearea clasei NewsParser

**Ce fac:** Creez clasa `NewsParser` care parsează răspunsul JSON și creează obiecte `NewsItem`.

**Ce scriu în `parser/NewsParser.java`:**
```java
package ro.makore.akrilki_06.parser;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ro.makore.akrilki_06.model.NewsItem;
import java.util.ArrayList;
import java.util.List;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.Duration;
import org.threeten.bp.ZoneId;

public class NewsParser {
```

**Ce spun:** "Creez clasa `NewsParser` care va parsa JSON-ul primit de la API. Import bibliotecile necesare: Gson pentru parsare și ThreeTenABP pentru procesarea datelor."

**Ce scriu în continuare:**
```java
    public static List<NewsItem> parseNews(String jsonResponse) {
        List<NewsItem> newsItems = new ArrayList<>();
        Gson gson = new Gson();

        JsonArray articles = gson.fromJson(jsonResponse, JsonObject.class)
            .getAsJsonObject("articles")
            .getAsJsonArray("results");
```

**Ce spun:** "Implementez metoda statică `parseNews` care primește răspunsul JSON ca String. Creez o listă goală pentru rezultate și un obiect Gson. Parsez JSON-ul și extrag array-ul `results` din obiectul `articles`."

**Ce scriu în continuare:**
```java
        for (int i = 0; i < articles.size(); i++) {
            JsonObject article = articles.get(i).getAsJsonObject();
            NewsItem item = new NewsItem();
            
            if (article.has("title") && !article.get("title").isJsonNull()) {
                item.setTitle(article.get("title").getAsString());
            } else {
                item.setTitle("Untitled article");
            }

            if (article.has("body") && !article.get("body").isJsonNull()) {
                item.setBody(article.get("body").getAsString());
            } else {
                item.setBody("no body content for the newsitem");
            }

            if (article.has("image") && !article.get("image").isJsonNull()) {
                item.setThumbnailUrl(article.get("image").getAsString());
            } else {
                item.setThumbnailUrl("");
            }

            if (article.has("lang") && !article.get("lang").isJsonNull()) {
                item.setLanguage(article.get("lang").getAsString());
            } else {
                item.setLanguage("");
            }
```

**Ce spun:** "Parcurg fiecare articol din array. Pentru fiecare articol, creez un nou `NewsItem` și extrag câmpurile: titlu, corp, URL imagine și limbă. Folosesc verificări pentru a evita erorile când câmpurile lipsesc sau sunt null, oferind valori default."

**Ce scriu în continuare:**
```java
            if (article.has("source") && !article.get("source").isJsonNull()) {
                JsonObject source = article.getAsJsonObject("source");
                if (source.has("title") && !source.get("title").isJsonNull()) {
                    item.setSource(source.get("title").getAsString());
                } else {
                    item.setSource("");
                }
            } else {
                item.setSource("");
            }
```

**Ce spun:** "Extrag sursa articolului. Observați că `source` este un obiect JSON, nu un string simplu, deci trebuie să extrag obiectul și apoi câmpul `title` din el."

**Ce scriu în continuare:**
```java
            if (article.has("dateTime") && !article.get("dateTime").isJsonNull()) {
                try {
                    String dateTime = article.get("dateTime").getAsString();
                    
                    // Parse the ISO 8601 dateTime string
                    Instant parsedInstant = Instant.parse(dateTime);
                    LocalDateTime parsedDateTime = LocalDateTime.ofInstant(parsedInstant, ZoneId.systemDefault());
        
                    // Format the date and time as "HH:mm yyyy-MM-dd"
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm yyyy-MM-dd");
                    String formattedDateTime = parsedDateTime.format(formatter);
        
                    // Calculate the difference between now and the parsed dateTime
                    Instant now = Instant.now();
                    Duration duration = Duration.between(parsedInstant, now);
        
                    // Determine the relative time string
                    String relativeTime;
                    long seconds = duration.getSeconds();
                    if (seconds < 120) {
                        relativeTime = (seconds < 60) ? "now" : "1 minute ago";
                    } else if (seconds < 3600) {
                        relativeTime = (seconds / 60) + " minutes ago";
                    } else if (seconds < 86400) {
                        relativeTime = (seconds / 3600) + " hours ago";
                    } else {
                        long days = seconds / 86400;
                        relativeTime = days + (days == 1 ? " day ago" : " days ago");
                    }
        
                    // Combine formatted dateTime with relative time
                    String result = formattedDateTime + " (" + relativeTime + ")";
                    item.setDateTime(result);
                } catch (Exception e) {
                    item.setDateTime("Invalid dateTime");
                }
            } else {
                item.setDateTime("NO has datetime");
            }
```

**Ce spun:** "Procesez data și ora. Parsez string-ul ISO 8601 într-un `Instant`, apoi îl convertesc în `LocalDateTime` folosind timezone-ul sistemului. Formatez data ca "HH:mm yyyy-MM-dd". Calculez diferența de timp față de acum și creez un string relativ ("X minutes ago", "X hours ago", etc.). Combin formatul absolut cu cel relativ."

**Ce scriu în continuare:**
```java
            // Parse concepts
            if (article.has("concepts") && article.get("concepts").isJsonArray()) {
                JsonArray concepts = article.getAsJsonArray("concepts");
                List<String> conceptsList = new ArrayList<>();
                for (int j = 0; j < concepts.size(); j++) {
                    JsonObject concept = concepts.get(j).getAsJsonObject();
                    if (concept.has("label")) {
                        JsonObject label = concept.getAsJsonObject("label");
                        if (label.has("eng")) {
                            conceptsList.add(label.get("eng").getAsString());
                        }
                    }
                }
                item.setConcepts(conceptsList);
            } else {
                item.setConcepts(new ArrayList<>());
            }
            newsItems.add(item);
        }

        return newsItems;
    }
}
```

**Ce spun:** "Parsez conceptele care sunt un array de obiecte complexe. Pentru fiecare concept, extrag obiectul `label` și apoi câmpul `eng` din el. Adaug toate conceptele într-o listă și o setez pe item. La final, adaug item-ul în listă și returnez lista completă."

**Checkpoint:** Clasa `NewsParser` este creată și poate parsa JSON-ul în obiecte `NewsItem`.

---

### Pasul 12: Crearea layout-ului item_news.xml

**Ce fac:** Creez layout-ul pentru un element din RecyclerView.

**Ce scriu în `res/layout/item_news.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="4dp">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="8dp">

        <ImageView
            android:id="@+id/thumbnail"
            android:layout_width="100dp"
            android:layout_height="100dp"
            android:layout_marginEnd="16dp"
            android:scaleType="centerCrop"
            android:contentDescription="@string/thumbnail_description"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <TextView
            android:id="@+id/title"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:textSize="18sp"
            android:textColor="@android:color/black"
            android:paddingStart="16dp"
            app:layout_constraintLeft_toRightOf="@id/thumbnail"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintRight_toRightOf="parent" />

        <TextView
            android:id="@+id/body"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:textSize="14sp"
            android:textColor="@android:color/black"
            android:layout_marginTop="8dp"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintTop_toBottomOf="@id/thumbnail"
            app:layout_constraintRight_toRightOf="parent" />

        <View
            android:id="@+id/line"
            android:layout_width="0dp"
            android:layout_height="1dp"
            android:layout_marginTop="4dp"
            android:background="@android:color/darker_gray"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintTop_toBottomOf="@id/body"
            app:layout_constraintRight_toRightOf="parent" />

        <TextView
            android:id="@+id/datetime"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:textSize="14sp"
            android:textColor="@android:color/black"
            android:gravity="end"
            android:layout_marginTop="4dp"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintTop_toBottomOf="@id/line"
            app:layout_constraintRight_toRightOf="parent" />

        <TextView
            android:id="@+id/tags"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:textSize="14sp"
            android:textColor="@android:color/black"
            android:layout_marginTop="4dp"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintTop_toBottomOf="@id/datetime"
            app:layout_constraintRight_toRightOf="parent" />

    </androidx.constraintlayout.widget.ConstraintLayout>
</androidx.cardview.widget.CardView>
```

**Ce spun:** "Creez layout-ul pentru un element din listă folosind `CardView` pentru un aspect modern cu umbre. Layout-ul conține: o imagine thumbnail la stânga, titlul lângă imagine, corpul articolului sub thumbnail, o linie separator, data/ora aliniată la dreapta și tag-urile. Folosesc `ConstraintLayout` pentru poziționare flexibilă."

**Checkpoint:** Layout-ul `item_news.xml` este creat.

---

### Pasul 13: Crearea clasei NewsAdapter

**Ce fac:** Creez adapterul pentru RecyclerView care conectează datele cu view-urile.

**Ce scriu în `adapter/NewsAdapter.java`:**
```java
package ro.makore.akrilki_06.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ro.makore.akrilki_06.R;
import ro.makore.akrilki_06.model.NewsItem;
import ro.makore.akrilki_06.NewsDetailActivity;
import com.bumptech.glide.Glide;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private final Context context;
    private final List<NewsItem> newsItemList;
```

**Ce spun:** "Creez clasa `NewsAdapter` care extinde `RecyclerView.Adapter`. Declar câmpurile: context-ul pentru acces la resurse și lista de `NewsItem` care va fi afișată."

**Ce scriu în continuare:**
```java
    public NewsAdapter(Context context, List<NewsItem> newsItemList) {
        this.context = context;
        this.newsItemList = newsItemList;
    }

    public void updateData(List<NewsItem> newsItemList) {
        this.newsItemList.clear();
        this.newsItemList.addAll(newsItemList);
        notifyDataSetChanged();
    }
```

**Ce spun:** "Adaug constructorul care inițializează câmpurile și metoda `updateData` care actualizează lista și notifică RecyclerView despre schimbări."

**Ce scriu în continuare:**
```java
    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }
```

**Ce spun:** "Implementez `onCreateViewHolder` care creează un nou ViewHolder prin inflarea layout-ului `item_news.xml`. Această metodă este apelată când RecyclerView are nevoie de un nou ViewHolder."

**Ce scriu în continuare:**
```java
    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem newsItem = newsItemList.get(position);

        // Load thumbnail image using Glide
        Glide.with(context)
            .load(newsItem.getThumbnailUrl())
            .into(holder.thumbnailImageView);

        // Set title and body (short fragment)
        holder.titleTextView.setText(newsItem.getTitle());
        holder.bodyTextView.setText(newsItem.getBody().length() > 300 
            ? newsItem.getBody().substring(0, 300) + "..." 
            : newsItem.getBody());
        holder.dateTimeTextView.setText(newsItem.getDateTime());

        // Join tags from concepts list and display them
        List<String> limitedTags = newsItem.getConcepts().subList(0, Math.min(newsItem.getConcepts().size(), 5));
        String tags = String.join(", ", limitedTags);
        holder.tagsTextView.setText(tags);
```

**Ce spun:** "Implementez `onBindViewHolder` care populează view-urile cu datele. Folosesc Glide pentru încărcarea asincronă a imaginii din URL. Setez titlul, trunchiez corpul la 300 de caractere, setez data/ora și afișez primele 5 concepte ca tag-uri separate prin virgulă."

**Ce scriu în continuare:**
```java
        // Handle click event to pass the NewsItem to NewsDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NewsDetailActivity.class);
            if (newsItem != null) {
                intent.putExtra("news_item", newsItem);
            }
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return newsItemList.size();
    }
```

**Ce spun:** "Adaug un click listener pe întregul item care creează un Intent către `NewsDetailActivity` și trimite obiectul `NewsItem` ca extra folosind `putExtra`. Deoarece `NewsItem` implementează `Parcelable`, Android poate serializa automat obiectul. Implementez `getItemCount` care returnează numărul de elemente."

**Ce scriu în continuare:**
```java
    // ViewHolder for each news item
    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        private final ImageView thumbnailImageView;
        private final TextView titleTextView;
        private final TextView bodyTextView;
        private final TextView tagsTextView;
        private final TextView dateTimeTextView;

        public NewsViewHolder(View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnail);
            titleTextView = itemView.findViewById(R.id.title);
            bodyTextView = itemView.findViewById(R.id.body);
            tagsTextView = itemView.findViewById(R.id.tags);
            dateTimeTextView = itemView.findViewById(R.id.datetime);
        }
    }
}
```

**Ce spun:** "Creez clasa internă `NewsViewHolder` care extinde `RecyclerView.ViewHolder`. ViewHolder-ul păstrează referințe către view-urile din layout pentru a evita apeluri repetate la `findViewById`. În constructor, inițializez toate referințele folosind ID-urile din layout."

**Checkpoint:** Clasa `NewsAdapter` este creată cu ViewHolder și logica de binding.

---

### Pasul 14: Crearea layout-ului activity_main.xml

**Ce fac:** Creez layout-ul principal cu RecyclerView și indicatori de încărcare.

**Ce scriu în `res/layout/activity_main.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/newsRecyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginTop="16dp"
        android:layout_marginBottom="16dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent" />

    <ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:visibility="gone"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <TextView
        android:id="@+id/loadingText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Loading the news..."
        android:textSize="16sp"
        android:visibility="gone"
        app:layout_constraintTop_toBottomOf="@id/progressBar"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:paddingTop="8dp" />

    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fab_quit"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginEnd="16dp"
        android:layout_marginBottom="96dp"
        android:elevation="16dp"
        android:src="@drawable/ic_quit_black_24dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fab_refresh"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginEnd="16dp"
        android:layout_marginBottom="28dp"
        android:elevation="16dp"
        android:src="@drawable/ic_refresh_black_24dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**Ce spun:** "Creez layout-ul principal cu un `RecyclerView` care ocupă tot spațiul disponibil, un `ProgressBar` și un `TextView` pentru indicatori de încărcare (inițial invizibile), și două `FloatingActionButton`-uri pentru quit și refresh."

**Checkpoint:** Layout-ul `activity_main.xml` este creat.

---

### Pasul 15: Crearea clasei MainActivity

**Ce fac:** Creez activitatea principală care inițializează RecyclerView și face apelul API.

**Ce scriu în `MainActivity.java`:**
```java
package ro.makore.akrilki_06;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import ro.makore.akrilki_06.model.NewsItem;
import ro.makore.akrilki_06.parser.NewsParser;
import ro.makore.akrilki_06.api.NewsAPI;
import ro.makore.akrilki_06.adapter.NewsAdapter;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private TextView loadingText;
    private ExecutorService executorService;
```

**Ce spun:** "Creez clasa `MainActivity` cu toate import-urile necesare. Declar câmpurile: RecyclerView, adapter, indicatori de încărcare și un ExecutorService pentru thread-uri de background."

**Ce scriu în continuare:**
```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);

        executorService = Executors.newSingleThreadExecutor();

        FloatingActionButton fabQuit = findViewById(R.id.fab_quit);
        fabQuit.setOnClickListener(v -> finishAffinity());

        FloatingActionButton fabRefresh = findViewById(R.id.fab_refresh);
        fabRefresh.setOnClickListener(v -> refreshNewsData());

        recyclerView = findViewById(R.id.newsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);

        refreshNewsData();
    }
```

**Ce spun:** "În `onCreate`, inițializez ThreeTenABP pentru procesarea datelor, setez layout-ul, creez ExecutorService cu un singur thread, configurez butoanele FAB, setez LinearLayoutManager pentru RecyclerView (care afișează elementele vertical) și apelez `refreshNewsData` pentru a încărca știrile la pornire."

**Ce scriu în continuare:**
```java
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
```

**Ce spun:** "Implementez `onDestroy` pentru a închide ExecutorService și evit memory leaks. Creez metoda `isNetworkAvailable` care verifică dacă există conexiune la internet folosind ConnectivityManager."

**Ce scriu în continuare:**
```java
    private void refreshNewsData() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection available.", Toast.LENGTH_LONG).show();
            return;
        }

        runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            loadingText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        });
```

**Ce spun:** "Încep implementarea metodei `refreshNewsData`. Verific conectivitatea și afișez un mesaj dacă nu există. Apoi, pe thread-ul UI, fac vizibile indicatorii de încărcare și ascund RecyclerView-ul."

**Ce scriu în continuare:**
```java
        executorService.execute(() -> {
            try {
                String jsonResponse = NewsAPI.fetchNews(this);
                List<NewsItem> newsItems = NewsParser.parseNews(jsonResponse);

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.scrollToPosition(0);

                    if (newsAdapter == null) {
                        newsAdapter = new NewsAdapter(MainActivity.this, newsItems);
                        recyclerView.setAdapter(newsAdapter);
                    } else {
                        newsAdapter.updateData(newsItems);
                    }
                });
            } catch (IOException e) {
                Log.e("NEWS06", "Error fetching news " + e.getMessage(), e);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Error fetching news. Please check your internet connection.", Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                Log.e("NEWS06", "Unexpected error", e);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Unexpected error occurred.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
```

**Ce spun:** "Execut operațiile de rețea pe thread-ul de background. Apelez `NewsAPI.fetchNews` pentru a obține JSON-ul, apoi `NewsParser.parseNews` pentru a crea lista de `NewsItem`. Pe thread-ul UI, ascund indicatorii, fac vizibil RecyclerView-ul, scroll la poziția 0, și actualizez adapterul (creez unul nou dacă nu există, altfel actualizez datele). Gestionez erorile cu try-catch și afișez mesaje Toast pentru utilizator."

**Checkpoint:** Clasa `MainActivity` este creată și poate încărca știri de la API.

---

### Pasul 16: Crearea layout-ului activity_news_detail.xml

**Ce fac:** Creez layout-ul pentru activitatea de detalii.

**Ce scriu în `res/layout/activity_news_detail.xml`:**
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <TextView
        android:id="@+id/titleTextView"
        android:text="Title"
        android:textSize="20sp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textStyle="bold" />

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:paddingTop="10dp">

        <LinearLayout
            android:orientation="vertical"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <ImageView
                android:id="@+id/dimageView"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:contentDescription="@string/thumbnail_description"/>

            <TextView
                android:id="@+id/descriptionTextView"
                android:text="Description"
                android:textSize="16sp"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="10dp" />

            <TextView
                android:id="@+id/tagsTextView"
                android:text="Tags"
                android:textSize="14sp"
                android:textColor="#888888"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="10dp" />
        </LinearLayout>
    </ScrollView>

    <Button
        android:id="@+id/backButton"
        android:text="Back"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp" />
</LinearLayout>
```

**Ce spun:** "Creez layout-ul pentru activitatea de detalii folosind `LinearLayout` vertical. Conține: titlul, un `ScrollView` cu imaginea, descrierea completă și tag-urile, și un buton Back. ScrollView permite derularea conținutului lung."

**Checkpoint:** Layout-ul `activity_news_detail.xml` este creat.

---

### Pasul 17: Crearea clasei NewsDetailActivity

**Ce fac:** Creez activitatea de detalii care primește `NewsItem` prin Intent și îl afișează.

**Ce scriu în `NewsDetailActivity.java`:**
```java
package ro.makore.akrilki_06;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.util.Log;
import ro.makore.akrilki_06.model.NewsItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.load.engine.GlideException;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.load.DataSource;
import androidx.appcompat.app.AppCompatActivity;

public class NewsDetailActivity extends AppCompatActivity {
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView tagsTextView;
    private ImageView dImageView;
    private Button backButton;
```

**Ce spun:** "Creez clasa `NewsDetailActivity` cu toate import-urile necesare, inclusiv pentru Glide și RequestListener. Declar câmpurile pentru view-uri."

**Ce scriu în continuare:**
```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        dImageView = findViewById(R.id.dimageView);
        tagsTextView = findViewById(R.id.tagsTextView);
        backButton = findViewById(R.id.backButton);

        NewsItem newsItem = getIntent().getParcelableExtra("news_item");
```

**Ce spun:** "În `onCreate`, setez layout-ul, obțin referințele la view-uri și extrag obiectul `NewsItem` din Intent folosind `getParcelableExtra`. Android deserializează automat obiectul Parcelable."

**Ce scriu în continuare:**
```java
        if (newsItem != null) {
            titleTextView.setText(newsItem.getTitle());
            descriptionTextView.setText(newsItem.getBody());
            tagsTextView.setText(String.join(", ", newsItem.getConcepts()));

            Glide.with(NewsDetailActivity.this)
            .load(newsItem.getThumbnailUrl())
            .listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.e("Glide", "Image load failed", e);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    dImageView.post(() -> {
                        int intrinsicWidth = resource.getIntrinsicWidth();
                        int intrinsicHeight = resource.getIntrinsicHeight();
                        int viewWidth = dImageView.getWidth();
                        int viewHeight = (int) ((float) intrinsicHeight / intrinsicWidth * viewWidth);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dImageView.getLayoutParams();
                        params.height = viewHeight;
                        dImageView.setLayoutParams(params);
                    });
                    return false;
                }
            })
            .into(dImageView);
        } else {
            titleTextView.setText("No news is good news");
            descriptionTextView.setText("isn't it");
        }

        backButton.setOnClickListener(v -> finish());
    }
}
```

**Ce spun:** "Dacă `newsItem` nu este null, setez texturile pentru titlu, descriere și tag-uri. Folosesc Glide pentru încărcarea imaginii cu un `RequestListener` care ajustează înălțimea ImageView-ului proporțional cu lățimea, păstrând aspect ratio-ul imaginii. Dacă `newsItem` este null, afișez un mesaj default. Butonul Back apelează `finish()` pentru a închide activitatea."

**Checkpoint:** Clasa `NewsDetailActivity` este creată și poate afișa detaliile unei știri.

---

## Build, Install și Run

### Build APK debug

**Ce fac:** Compilez aplicația într-un APK debug.

**Ce scriu în terminal:**
```bash
cd akrilki_06
gradle assembleDebug
```

**Ce spun:** "Compilez aplicația folosind Gradle. Comanda `assembleDebug` creează un APK debug în `app/build/outputs/apk/debug/`."

**Checkpoint:** APK-ul debug este generat.

---

### Listare dispozitive

**Ce fac:** Listez dispozitivele Android conectate.

**Ce scriu în terminal:**
```bash
adb devices
```

**Ce spun:** "Listez dispozitivele conectate. Asigurați-vă că aveți un emulator pornit sau un dispozitiv fizic conectat prin USB cu USB debugging activat."

**Checkpoint:** Dispozitivul este listat.

---

### Instalare APK

**Ce fac:** Instalez APK-ul pe dispozitiv.

**Ce scriu în terminal:**
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

**Ce spun:** "Instalez APK-ul pe dispozitiv. Flag-ul `-r` permite reinstalarea dacă aplicația există deja."

**Checkpoint:** Aplicația este instalată pe dispozitiv.

---

### Lansare activitate principală

**Ce fac:** Lansez activitatea principală.

**Ce scriu în terminal:**
```bash
adb shell am start -n ro.makore.akrilki_06/.MainActivity
```

**Ce spun:** "Lansez activitatea principală folosind `am start` cu numele complet al pachetului și activității."

**Checkpoint:** Aplicația se deschide și începe să încarce știrile.

---

### Afișare loguri filtrate

**Ce fac:** Afișez logurile filtrate pentru aplicație.

**Ce scriu în terminal:**
```bash
adb logcat | grep -E "(NEWS06|Glide|AndroidRuntime)"
```

**Ce spun:** "Afișez logurile filtrate pentru tag-urile noastre (`NEWS06`, `Glide`) și erorile (`AndroidRuntime`). Observați mesajele de la API call, parsare JSON și încărcare imagini."

**Checkpoint:** Logurile sunt afișate și pot fi monitorizate pentru debugging.

