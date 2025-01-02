# Aplicatie care preia un feed de stiri si il afiseaza in doua activitati

<!-- TOC -->

- [Aplicatie care preia un feed de stiri si il afiseaza in doua activitati](#aplicatie-care-preia-un-feed-de-stiri-si-il-afiseaza-in-doua-activitati)
  - [newsAPI](#newsapi)
  - [Screenshots](#screenshots)
  - [Concepte importante](#concepte-importante)
    - [RecyclerView](#recyclerview)
    - [Parcelable](#parcelable)
    - [Structura impartita pe componente](#structura-impartita-pe-componente)
  - [Structura aplicatiei](#structura-aplicatiei)
    - [Structura requestului](#structura-requestului)
    - [ro.makore.akrilki\_06.api - clasa NewsAPI](#romakoreakrilki_06api---clasa-newsapi)
    - [ro.makore.akrilki\_06.model NewsItem](#romakoreakrilki_06model-newsitem)
    - [ro.makore.akrilki\_06.parser NewsParser](#romakoreakrilki_06parser-newsparser)
    - [ro.makore.akrilki\_06.adapter NewsAdapter](#romakoreakrilki_06adapter-newsadapter)
  - [Activități](#activități)
    - [MainActivity](#mainactivity)
    - [NewDetailActivity](#newdetailactivity)
  - [Layouturi](#layouturi)
    - [activity\_main.xml](#activity_mainxml)

<!-- /TOC -->
<!-- /TOC -->tivity](#mainactivity)

<!-- /TOC -->
<!-- /TOC -->Structura unui element de tip stire


## newsAPI

Aplicația preia un feed de știri JSON de la https://www.newsapi.ai/. Pentru replicarea funcționaliății trebuie să vă înregistrați pe site-ul https://www.newsapi.ai/ și să obțineți o cheie API. Cheia API se pozitioneaza într-un fișier numit api_key.json în directorul app/src/main/assets, unde există deja request_body.json

Conținutul fișierului este astfel: 

```json
{
"apiKey": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
}
```

## Screenshots

![alt text](images/loading.jpg)
![alt text](images/mainview.jpg)
![alt text](images/newsdetail.jpg)


## Concepte importante

### RecyclerView

Un component (view) care reprezinta o varianta avansata a vechilor ListView sau GridView. Recycler view este din punct de vedere al afișarii un listview cu un management diferit al memoriei. 

### Parcelable

Parcelable este o interfata disponibila in Android care permite o serializare /deserializare eficienta a obiectelor complexe utilizat la transferul de date intre activitati sau procese. 

Parcelable este mai eficient decat vechea interfata Serializable

### Structura impartita pe componente

Preluarea stirilor de la un API extern are o serie de subactivitati:

- Interactiunea cu API-ul (HTTP Request)
- Parsarea rezultatelor (implica transformarea rezultatelor primite de la API, de obicei in format JSON intr-un obiect Java)
- Definirea structurii obiectului Java care va stoca o stire
- Update al interfetei Android care sa afiseze informatiile

Fiecare dintre aceste operatii a fost implementata intr-o clasa separata. 


## Structura aplicatiei

```sh
 akrilki_06
    ├── app
    │   ├── build.gradle
    │   └── src
    │       └── main
    │           ├── AndroidManifest.xml
    │           ├── assets
    │           │   └── request_body.json
    │           ├── java
    │           │   └── ro
    │           │       └── makore
    │           │           └── akrilki_06
    │           │               ├── MainActivity.java
    │           │               ├── NewsDetailActivity.java
    │           │               ├── adapter
    │           │               │   └── NewsAdapter.java
    │           │               ├── api
    │           │               │   └── NewsAPI.java
    │           │               ├── model
    │           │               │   └── NewsItem.java
    │           │               └── parser
    │           │                   └── NewsParser.java
    │           └── res
    │               ├── drawable
<!-- TOC -->

- [Aplicatie care preia un feed de stiri si il afiseaza in doua activitati](#aplicatie-care-preia-un-feed-de-stiri-si-il-afiseaza-in-doua-activitati)
        - [](#)
    - [newsAPI](#newsapi)
    - [Concepte importante](#concepte-importante)
        - [RecyclerView](#recyclerview)
        - [Parcelable](#parcelable)
        - [Structura impartita pe componente](#structura-impartita-pe-componente)
    - [Structura aplicatiei](#structura-aplicatiei)
        - [Structura requestului](#structura-requestului)
        - [ro.makore.akrilki_06.api - clasa NewsAPI](#romakoreakrilki_06api---clasa-newsapi)
        - [ro.makore.akrilki_06.model NewsItem](#romakoreakrilki_06model-newsitem)
        - [ro.makore.akrilki_06.parser NewsParser](#romakoreakrilki_06parser-newsparser)

<!-- /TOC -->

```json
{
  "articles": {
    "page": 1,
    "pages": 490130,
    "totalResults": 4901294,
    "results": [
      {
        "uri": "8459037900",
        "lang": "eng",
        "isDuplicate": false,
        "date": "2024-12-15",
        "time": "21:14:37",
        "dateTime": "2024-12-15T21:14:37Z",
        "dateTimePub": "2024-12-15T21:13:24Z",
        "dataType": "news",
        "sim": 0,
        "url": "https://www.cbssports.com/.......",
        "title": "NFL Week 15 live updates, scores, highlights: Lamar Jackson makes MVP case; Rodgers-Adams turn back the clock - CBSSports.com",
        "body": "It's Week 15 in the NFL, and playoff spots are on the line. ....",
        "source": {
          "uri": "cbssports.com",
          "dataType": "news",
          "title": "CBS Sports"
        },
        "authors": [
          {
            "uri": "jordan_dajani@cbssports.com",
            "name": "Jordan Dajani",
            "type": "author",
            "isAgency": false
          }
        ],
        "concepts": [
          {
            "uri": "http://en.wikipedia.org/wiki/Philadelphia_Eagles",
            "type": "wiki",
            "score": 4,
            "label": {
              "eng": "Philadelphia Eagles"
            }
          },
          {
            "uri": "http://en.wikipedia.org/wiki/Kansas_City_Chiefs",
            "type": "wiki",
            "score": 3,
            "label": {
              "eng": "Kansas City Chiefs"
            }
          },
          {
            "uri": "http://en.wikipedia.org/wiki/Indianapolis_Colts",
            "type": "wiki",
            "score": 1,
            "label": {
              "eng": "Indianapolis Colts"
            }
          },
          {
            "uri": "http://en.wikipedia.org/wiki/Green_Bay,_Wisconsin",
            "type": "loc",
            "score": 1,
            "label": {
              "eng": "Green Bay, Wisconsin"
            },
            "location": {
              "type": "place",
              "label": {
                "eng": "Green Bay, Wisconsin"
              },
              "country": {
                "type": "country",
                "label": {
                  "eng": "United States"
                }
              }
            }
          }
        ],
        "image": "https://.....image.jpg",
        "eventUri": null,
        "sentiment": 0.1607843137254903,
        "wgt": 471993277,
        "relevance": 1
      },
      {
        "uri": "8459038215",
        "lang": "eng",
        "isDuplicate": false,
        "date": "2024-12-15",
        "time": "21:14:37",
        "dateTime": "2024-12-15T21:14:37Z",
        "dateTimePub": "2024-12-15T21:13:50Z",
        "dataType": "news",
        "sim": 0,
        "url": "https://siouxcityjournal.com/.....",
        "title": "Italy's Goggia wins World Cup super-G",
        "body": "PAT GRAHAM Associated Press\n\nBEAVER CREEK, Colo. ...",
        "source": {
          "uri": "siouxcityjournal.com",
          "dataType": "news",
          "title": "Sioux City Journal"
        },
        "authors": [],
        "concepts": [
          {
            "uri": "http://en.wikipedia.org/wiki/Sofia_Goggia",
            "type": "person",
            "score": 5,
            "label": {
              "eng": "Sofia Goggia"
            }
          },
          {
            "uri": "http://en.wikipedia.org/wiki/Lindsey_Vonn",
            "type": "person",
            "score": 5,
            "label": {
              "eng": "Lindsey Vonn"
            }
          },
          {
            "uri": "http://en.wikipedia.org/wiki/Sioux_City,_Iowa",
            "type": "loc",
            "score": 5,
            "label": {
              "eng": "Sioux City, Iowa"
            },
            "location": {
              "type": "place",
              "label": {
                "eng": "Sioux City, Iowa"
              },
              "country": {
                "type": "country",
                "label": {
                  "eng": "United States"
                }
              }
            }
          },
        ],
        "image": "https://...",
        "eventUri": null,
        "sentiment": 0.1529411764705881,
        "wgt": 471993277,
        "relevance": 1
      }
    ]
  }
}
```


### Structura requestului

Pentru a permite modificarea ușoară a parametrilor solicitarii către api-ul newsapi, elementele acesteia sunt plasate într-un fișier json, numit assets/requests_body.json. La acesta se va adăuga intrarea apikey din fisierul api_key.json. Intr-o versiune avansată a aplicației, acest json poate fi modificat într-un element de interfață.  

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
    "apiKey": "...",
    "articlesPage": 1,
    "articlesCount": 50
  }
```



### ro.makore.akrilki_06.api - clasa NewsAPI

Clasa NewsAPI se ocupă cu comunicarea cu serverul de știri. Aceasta exportă metoda publică "fetchNews" care face următoarele:

Inițializează clientul OkHTTpClient. OkHTTp este un client HTTP cu ajutorul căruia pot fi făcute apeluri prin protocoalele HTTP/HTTPS. 

```java
    OkHttpClient client = new OkHttpClient();
```

Pentru a nu plasa parametrii solicitării HTTP direct în cod (în eventualitatea în care dorim să modificăm paginația, parametrii solicitării, etc.) aceștia sunt înregistrați într-un fișier json plasat în directorul "assets" al aplicației. API-ul Android face disponibilă metoda readJsonFromAssets care permite citirea acestui fișier. 

Parametrii din fișierul JSON for fi convertiți în corpul solicitării HTTP.

De asemenea, se va citi si fișierul api_key.json care va fi integrat în request

```java
        String jsonBody = readJsonFromAssets(context, "request_body.json");
        String apiKeyJson = readJsonFromAssets(context, "api_key.json");

        // Parse them into JSON objects
        JSONObject requestBody = new JSONObject(jsonBody);
        JSONObject apiKeyObject = new JSONObject(apiKeyJson);

        // Add the API key dynamically
        requestBody.put("apiKey", apiKeyObject.getString("apiKey"));

        // Convert updated JSON object back to a string
        jsonBody = requestBody.toString();

        // Define the media type for JSON
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        // RequestBody with JSON data
        RequestBody body = RequestBody.create(JSON, jsonBody);
```

În continuare se construiește solicitarea HTTP folosind URL-ul și corpul solicitării și este trimisă către server folosind clientul inițializat în variabila "client" mai sus. Funcția aceasta va returna răspunsul primit. 

```java
    Request request = new Request.Builder()
            .url(API_URL)
            .post(body)
            .build();

    try (Response response = client.newCall(request).execute()) {
        return response.body().string();
    }

```

### ro.makore.akrilki_06.model NewsItem

NewsItem reprezintă o știre. Clasa este poziționată în namespace-ul "model" - de obicei clasele care "modelează" structuri de date reale sunt numite "modele" și sunt grupate într-un namespace corespunzător. 

O particularitate a acestui model este faptul că implementează interfața "parcelable" - interfață care permite o serializare a obiectelor tipică pentru aplicațiile Android. Parcelable impune doua metode de tip constructor: un constructor normal, care se va apela când datele provin direct din altă metodă și un constructor special pentru cazul în care obiectul este recreat prin citire din datele serializate. 

Parcelable este mai rapid decât interfața standard Serializable care neavând un constructor special, are nevoie să inspecteze obiectul serializat, adică să identifice toate metodele/atributele si valorile acestuia. 


```java

    // Unparceleable Constructor
    public NewsItem() {
    }

    // Parcelable constructor - aceasi ordine ca in metoda writeToParcel
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

Pe lângă constructorul special, Parcelable cere ca obiectul să implementeze si metoda writeToParcel
Aceasta trebuie să scrie în obiectul destinație (dest în acest caz), elementele selectate ale obiectului în aceeași ordine în care sunt trecute în constructor. 

```java
    // writeString trebuie date in fix aceeasi ordine ca in constructor
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
```

describeContents este o metodă care trebuie implementată în clasa care implementează interfața Parcelable - aceasta va fi apelată de Android framework pentru a determina dacă obiectul serializat conține sau nu fișiere serializate. În cele mai multe cazuri această metodă trebuie să returneze 0. Dacă obiectul serializat conține un fisier atunci aceasta trebuie să returneze Parcelable.CONTENTS_FILE_DESCRIPTOR

```java


    @Override
    public int describeContents() {
        return 0;  // No special objects inside
    }
```

CREATOR este un atribut static (de clasă) care este final (nu mai poate fi modificat după atribuire)  care trebuie să existe în orice clasă care implementează interfața Parcelable în Android. 

Acesta este o instanță a interfeței Parcelable.Creator<T>, unde T este tipul clasei (NewsItem). Scopul său este de a ajuta sistemul Android să recreeze obiectele clasei din containerul serializat

CREATOR conține două metode principale:

createFromParcel(Parcel in) – Această metodă este folosită pentru a crea o instanță a clasei NewsItem din datele stocate în Parcel. De obicei, aceasta apelează constructorul clasei care primește un Parcel ca parametru.

newArray(int size) – Această metodă este utilizată pentru a crea un array de obiecte ale clasei NewsItem.

Fără CREATOR, sistemul Android nu ar putea să reconstruiască obiectele atunci când 
le transmite între componente precum activități sau servicii. Astfel, CREATOR este un element obligatoriu pentru orice clasă care implementează Parcelable.

```java

    // Parcelable CREATOR to help with deserialization
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
Dupa metodelete obligatorii determinate de interfața "Parcelable" clasa are getterele si setterele obișnuite - metode necesare pentru obținerea și setarea proprietăților unei știri.

Pentru simplificarea acestui model, optăm pentru a nu îngloba toate caracrteristicile unei știri și cele care sunt înglobate sunt înglobate în format simplu, ca șiruri sau ca listă de siruri. 

O versiune mai avansată a aplicației ar stoca data/ora sub formă de datetime, lista conceptelor cu atributele fiecăruia, ceea ce ar obliga la construcția unei clase pentru acestea, etc. 

```java
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
```



### ro.makore.akrilki_06.parser NewsParser

NewsParser primește răspunsul json (string) provenit de la NewsAPI și returnează o colecție de tip listă care conține obiecte de tip NewsItem. NewsParser convertește informația datetime înstr-un șir la care se adaugă timpul trecut de la apariție - adică pe baza unei intrări de genul: "dateTime": "2024-12-15T21:14:37Z" se obține "21:14 2024-12-15 (32 minutes ago)".

Pentru manipularea datetime-ului trebuie utilizate (ca în orice limbaj de programare) biblioteci speciale. Există două variante: ne bazăm pe bibliotecile native java.time : 

```java
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.time.ZoneId;
```

care însă sunt disponibile doar pentru api level 26 sau mai sus.  

```sh
\06_news\akrilki_06\app\src\main\java\ro\makore\akrilki_06\parser\NewsParser.java:74: Error: Call requires API level 26 (current min is 21): java.time.Instant#parse [NewApi]
                      Instant parsedInstant = Instant.parse(dateTime);
                                                      ~~~~~
```                                                      
Pentru api leveluri mai vechi, exista o alternativă: https://www.threeten.org/. Pentru utilizarea acesteia: 

1. se introduce în build.gradle de la nivelul aplicatiei referinta catre pachetul respectiv: 

```sh
implementation 'com.jakewharton.threetenabp:threetenabp:1.4.4'
```

2. Se înlocuiesc bibliotecile java.time cu threeten: 

```java
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.Duration;
import org.threeten.bp.ZoneId;
```

Clasa are o metodă numită "parseNews" care este de altfel singura metoda - parseNews. Aceasta primește un String în care se găsește răspunsul (în format JSON) venit de la API și returnează o listă de obiecte de tip NewsItem. 

Prima operație este parsarea JSON-ului folosind libraria gson: https://github.com/google/gson. GSON parsează (în acest caz) jsonul și îl convertește în JsonObject care reprezintă un obiect JSON sub formă de perechi cheie-valoare, unde cheile sunt întotdeauna string-uri, iar valorile pot fi primitive, alte obiecte JSON sau array-uri JSON. Acesta permite accesul dinamic la datele dintr-un JSON fără a defini o clasă Java specifică. Se pot adăuga, modifica sau șterge proprietăți folosind metode precum add(), remove() sau get(). Se poate testa existența unei proprietăți folosind has().

Dacă ne uităm la structura JSON-ului care vine de la API, observăm că ceea ce ne interesează este cheia "results" care este o subcheie a cheii "articles". 

```json
{
  "articles": {
    "page": 1,
    "pages": 490130,
    "totalResults": 4901294,
    "results": []
  }
}
```
Codul va trebui să cicleze prin "results" astfel vom "sari" peste cheia articles preluând direct conținutul cheii "results" ca JsonArray. 

```java
    public static List<NewsItem> parseNews(String jsonResponse) {
        List<NewsItem> newsItems = new ArrayList<>();
        Gson gson = new Gson();
        JsonArray articles = gson.fromJson(jsonResponse, JsonObject.class)
            .getAsJsonObject("articles")
            .getAsJsonArray("results");
```
Următorul fragment de cod parcurge toate articolele primite și le transformă, cu ceva modificări și validări. Informațiile se preiau din obiectul article care este atribuit pe rând tuturor intrărilor din lista "articles" și se plasează în item care este o variabilă de tip "NewsItem".

```java
        for (int i = 0; i < articles.size(); i++) {
            JsonObject article = articles.get(i).getAsJsonObject();
            NewsItem item = new NewsItem();
            if (article.has("title") && !article.get("title").isJsonNull()) {
                item.setTitle(article.get("title").getAsString());
            } else {
                item.setTitle("Untitled article"); // Fallback to an empty string
            }

            if (article.has("body") && !article.get("body").isJsonNull()) {
                item.setBody(article.get("body").getAsString());
            } else {
                item.setBody("no body content for the newsitem"); // Fallback to an empty string
            }    

            if (article.has("image") && !article.get("image").isJsonNull()) {
                item.setThumbnailUrl(article.get("image").getAsString());
            } else {
                item.setThumbnailUrl(""); // Fallback to an empty string
            }

            if (article.has("lang") && !article.get("lang").isJsonNull()) {
                item.setLanguage(article.get("lang").getAsString());
            } else {
                item.setLanguage(""); 
            }
```

Extragerea sursei articolului este putin diferita pentru că sursa are propria structură de date

```json
"source": {
    "uri": "cbssports.com",
    "dataType": "news",
    "title": "CBS Sports"
}
 ```

```java
            if (article.has("source") && !article.get("source").isJsonNull()) {
                JsonObject source = article.getAsJsonObject("source"); // Get the source object
                if (source.has("title") && !source.get("title").isJsonNull()) {
                    item.setSource(source.get("title").getAsString()); // Set title as language
                } else {
                    item.setSource(""); // Default value if title is missing
                }
            } else {
                item.setSource(""); 
            }
```

DateTime are parte de un tratament aparte. După extragerea șirului este necesară parsarea acestuia într-un obiect de tip LocalDateTime apoi scrierea obiectului într-un șir cu un anumit format. Apoi, se calculează diferența dintre momentul apariției știrii și momentul prezent și se generează un string cu timpul trecut de la apariția știrii.

```java

            if (article.has("dateTime") && !article.get("dateTime").isJsonNull()) {
                //datetime
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
                    if (seconds < 120) { // Less than 2 minutes
                        relativeTime = (seconds < 60) ? "now" : "1 minute ago";
                    } else if (seconds < 3600) { // Less than 1 hour
                        relativeTime = (seconds / 60) + " minutes ago";
                    } else if (seconds < 86400) { // Less than 1 day
                        relativeTime = (seconds / 3600) + " hours ago";
                    } else { // More than 1 day
                        long days = seconds / 86400;
                        relativeTime = days + (days == 1 ? " day ago" : " days ago");
                    }
        
                    // Combine formatted dateTime with relative time
                    String result = formattedDateTime + " (" + relativeTime + ")";
        
                    // Set the result in the item
                    item.setDateTime(result);
                } catch (Exception e) {
                    // Handle parsing errors gracefully
                    item.setDateTime("Invalid dateTime");
                }
            } else {
                item.setDateTime("NO has datetime");
            }
```
Conceptele ap parte de tratament aparte - acestea sunt un array de obiecte complexe cu structura următoare: 

```json
{
    "uri": "http://en.wikipedia.org/wiki/Indianapolis_Colts",
    "type": "wiki",
    "score": 1,
    "label": {
        "eng": "Indianapolis Colts"
     }
}
```
Ceea ce dorim să extragem din aceasta structură de date este continutul cheii "eng" din valoarea cheii "label". Trebuie să extragem conceptele ca array, să ciclăm prin ele și să verificăm existența întregii structuri de date și în final să extragem conținutul acestuia și să-l adăugăm la lista conceptsList. 


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
                item.setConcepts(new ArrayList<>()); // Fallback to an empty list
            }
            newsItems.add(item);
        }

        return newsItems;
    }
```


### ro.makore.akrilki_06.adapter NewsAdapter

Clasa necesara pentru manevrarea datelor dintr-un ReciclerView. 

Extinde RecyclerView.Adapter și trebuie să implementeze o serie de metode sau clase subordonate


```java

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final Context context;
    private final List<NewsItem> newsItemList;
```

Constructorul: Acest constructor inițializează clasa Adapter cu contextul și lista de obiecte NewsItem care vor fi afișate. Contextul este transmis pentru accesarea resurselor, iar lista conține elementele de date pentru fiecare rând din RecyclerView.

```java
    public NewsAdapter(Context context, List<NewsItem> newsItemList) {
        this.context = context;
        this.newsItemList = newsItemList;
    }
```
updateData: Această metodă actualizează datele din adapter prin înlocuirea listei curente cu o listă nouă. Se șterge datele existente și se adaugă toate elementele din lista nouă. După actualizarea datelor, se apelează notifyDataSetChanged() pentru a notifica RecyclerView că datele s-au schimbat și trebuie să reîmprospăteze vizualizarea.


```java
    public void updateData(List<NewsItem> newsItemList) {
        Log.v("NEWS06", "Updating data");
        this.newsItemList.clear();
        this.newsItemList.addAll(newsItemList);
        notifyDataSetChanged(); // Refresh the RecyclerView
    }
```
onCreateViewHolder: Această metodă creează un nou ViewHolder pentru o știre, prin completarea elementelor de layout pentru fiecare element individual din listă (R.layout.item_news). Apoi, returnează instanța ViewHolder corespunzătoare.

```java

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }
```

onBindViewHolder: Această metodă preia datele din sursa lor și le poziționează în viewHolder. 

Pentru încărcarea imaginii care însoțește știrea se folosește o bibliotecă specială care încarcă imaginea din URL-ul NewsItem în ImageView. 
Glide este o biblioteca java care decodifică imagini din diferite formate și le încarcă imagini în memorie 



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
De asemenea, setează un eveniment de click care va da apela următoarea activitate, cea în care se va vedea toată știrea. Evenimentul va lansa un intent (un eveniment de apel al unei alte activități) către care va trimite datele știrii curente sub forma unui obiect de tip Parcel (colet) pe care îl va numi "news_item". Activitatea secundară va putea prelua acest obiect și desface datele din el. 

După definirea intentului însoțit de colet, se lansează activitatea. definită în intent. 

```java        
        // Handle click event to pass the NewsItem to NewsDetailActivity

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NewsDetailActivity.class);
            if (newsItem != null) {
                intent.putExtra("news_item", newsItem); // Pass the NewsItem to the next activity
            }    
            context.startActivity(intent);
        });
    }
```
GetItemCount este o metodă care transmite RecyclerView-ului numărul total de elemente din 
colecția care trebuie afișată.  

```java
    @Override
    public int getItemCount() {
        return newsItemList.size();
    }
```

ViewHolder-ul este o clasă internă care extinde RecyclerView.ViewHolder și reprezintă un container 
pentru componentele vizuale ale unui element individual din RecyclerView. De obicei, este definită 
ca o clasă statică internă în cadrul unui Adapter și are scopul de a păstra referințele către 
componentele UI (cum ar fi TextView sau ImageView), în loc să se apeleze repetat findViewById 
pentru fiecare element afișat.

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

            // Initialize views
            thumbnailImageView = itemView.findViewById(R.id.thumbnail);
            titleTextView = itemView.findViewById(R.id.title);
            bodyTextView = itemView.findViewById(R.id.body);
            tagsTextView = itemView.findViewById(R.id.tags);
            dateTimeTextView = itemView.findViewById(R.id.datetime);
        }
    }
}

```

## Activități

Aplicația are două activități - activitatea principală care conține RecyclerView unde se va încărca lista 
de știri și activitatea de detaliu - cea în care se va încărca fiecare știre. 

### MainActivity

MainActivity este activitatea principală a aplicației care încarcă elementele de interfață și declanșează operația de preluare a știrilor apelând metoda refreshNewsData(); 

```java

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);

        FloatingActionButton fabQuit = findViewById(R.id.fab_quit);
        fabQuit.setOnClickListener(v -> finishAffinity());

        FloatingActionButton fabRefresh = findViewById(R.id.fab_refresh);
        fabRefresh.setOnClickListener(v -> refreshNewsData());
        //recyclerview has its own layout that has to be set here
        recyclerView = findViewById(R.id.newsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);

        refreshNewsData();
    }
```

refreshNewData va deschide un thread separat pentru a evita blocarea thread-ului principal al aplicației. Pe Android, thread-ul principal este responsabil pentru interfața grafică (UI) și interacțiunea cu utilizatorul. Dacă efectuăm operațiuni consumatoare de timp, precum preluarea datelor din rețea sau  procesarea intensivă a datelor acestea pot bloca thread-ul principal, ceea ce duce la probleme precum UI înghețat, erori ANR (Application Not Responding).

Prin utilizarea unui fir de execuție separat, operațiunile de rețea (care pot dura câteva secunde sau mai mult) rulează în fundal. După ce operațiunile sunt finalizate, actualizarea interfeței grafice este redirecționată către thread-ul principal folosind metoda runOnUiThread.

Astfel, UI-ul rămâne fluid și interacțiunea utilizatorului nu este afectată.

Consecința neplăcută a acestui lucru este faptul că aplicația se va încărca și va rămâne fără nici un conținut până la citirea și parsarea știrilor. De aceea, utilizăm un ProgressBar și un TextView care vor fi afișate până la încărcarea știrilor, și de câte ori apăsăm pe butonul "refresh".  

În debutul metodei refreshNewsData vom face vizibile cele două elemente de interfață care se constituie în animația de progres și textul explicativ "Loading the news..". 

```java

     private void refreshNewsData() {
        
        // Show loading indicators
        runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            loadingText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        });
```

În continuare se deschide un nou thread și se lansează metoda fetchNews din NewsAPI care va deschide requestul HTTP către serverul de știri, urmat de lansarea metodei parseNews din clasa NewsParser către care se trimite rezultatul apelului, JSON-ul cu știri. 

```java
        new Thread(() -> {
            try {
                String jsonResponse = NewsAPI.fetchNews(this);
                List<NewsItem> newsItems = NewsParser.parseNews(jsonResponse);

                int count = newsItems.size();

```
După obținerea știrilor și parsarea lor, se revine la threadul principal, se ascund ProgressBar-ul și TextView-ul si se face vizibil recyclerView-ul. De asemenea, se comanda recycler view-ului să se deruleze la poziția 0 (pentru cazul în care se apasă butonul refresh în altă poziție). 


```java
                // Update UI on the main thread
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                     // Scroll to the top (first item)
                    recyclerView.scrollToPosition(0);    
```
In continuare, se verifică existența unei instanțe a adapterului. Dacă există deja, aceasta se refolosește. La prima lansare a aplicației aceasta nu există dar la următoarele comenzi de refresh, da. 
Dacă nu există, aceasta se instanțiază și se atașează recycler-ului. Dacă există deja, se apelează metoda updateData către care se trimite noua listă de știri.  

Testarea existenței instanței se face prin testarea definirii variabilei de instanță "newsAdapter" al cărui conținut se menține pe toată durata existenței instanței curente (adică a aplicației). 

```java
                    if (newsAdapter == null) {
                        newsAdapter = new NewsAdapter(MainActivity.this, newsItems);
                        recyclerView.setAdapter(newsAdapter);
                    } else {
                        newsAdapter.updateData(newsItems);
                    }
                });
            } catch (IOException e) {
                Log.e("NEWS06", "Error fetching news "+ e.getMessage(), e);
            } catch (Exception e) {
                // Handle any other exceptions that might occur
                Log.e("NEWS06", "Unexpected error", e);
            }
        }).start();
    }

```

### NewDetailActivity 

NewsDetailActivity este o activitate secundară care va înlocui activitatea principală atunci când se va da click/tap pe una dintre știri. Aceasta este folosită pentru a afișa în alt mod știrea respectivă. Datorită faptului că apelul API principal aduce tot conținutul de care avem nevoie, NewsDetailActivity nu trebuie să execute un apel API secundar, trebuie doar să preia fragmentul corespunzător (un membru al listei) și să îl afișeze în layoutul său. 

Activitatea curentă este lansată în execuție de un "intent". Intentul poate purta după el un obiect de tip Parcelable care poate fi "despachetat". 

```java

public class NewsDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView tagsTextView;
    private ImageView dImageView;    
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        dImageView = findViewById(R.id.dimageView);
        tagsTextView = findViewById(R.id.tagsTextView);
        backButton = findViewById(R.id.backButton);

```

Se preia continutul din coletul trimis odată cu intentul

```java
        NewsItem newsItem = getIntent().getParcelableExtra("news_item");
```

Următorul bloc de cod încarcă știrea în layoutul activității. Primele instrucțiuni încarcă elementele de tip text, prin utilizarea metodei setText a textView-urilor corespunzătoare:

```java

        if (newsItem != null) {
            titleTextView.setText(newsItem.getTitle());
            descriptionTextView.setText(newsItem.getBody());
            tagsTextView.setText(String.join(", ", newsItem.getConcepts()));
```

Urmează un bloc de cod care se ocupă de încărcarea imaginii în componentul ImageView. Acest bloc de cod trebuie nu numai să încarce imaginea dar trebuie să și redimensioneze componentul ImageView conform containerului în care este înărcat (dependent de rezoluția dispozitivului) și conform înălțimii imaginii. Pentru aceasta vom defini o clasă anonimă care mplementeaza interfața RequestListener<Drawable> din biblioteca Glide, permițând gestionarea evenimentelor legate de încărcarea imaginilor. Metoda onLoadFailed gestionează situațiile în care încărcarea imaginii eșuează, logând eroarea și permițând Glide să gestioneze eroarea în modul său implicit. Metoda onResourceReady ajustează înălțimea componentului ImageView (dImageView) în funcție de dimensiunile intrinseci ale imaginii încărcate, păstrând proporțiile corecte. Acest lucru este realizat prin calcularea unei înălțimi proporționale bazate pe lățimea imaginii și setarea noilor parametri de layout pentru ImageView. 

Metoda onResourceReady va returna tot false ceea ce permite Glide să continue operațiile ulterioare de afișare a imaginii. 

```java
            Glide.with(NewsDetailActivity.this)
            .load(newsItem.getThumbnailUrl())
            .listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.e("Glide", "Image load failed", e);
                    return false; // Allow Glide to handle the error
                }
        
                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    dImageView.post(() -> {
                        // Get the intrinsic dimensions of the loaded image
                        int intrinsicWidth = resource.getIntrinsicWidth();
                        int intrinsicHeight = resource.getIntrinsicHeight();
        
                        // Get the width of the ImageView
                        int viewWidth = dImageView.getWidth();
        
                        // Calculate the proportional height based on the image aspect ratio
                        int viewHeight = (int) ((float) intrinsicHeight / intrinsicWidth * viewWidth);
        
                        // Update the ImageView layout parameters
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dImageView.getLayoutParams();
                        params.height = viewHeight;
                        dImageView.setLayoutParams(params);
                    });
                    return false; // Allow Glide to set the resource on the ImageView
                }
            })
            .into(dImageView);
        } else {
            titleTextView.setText("No news is good news");
            descriptionTextView.setText("isn't it");
        }

```

## Layouturi

### activity_main.xml

Layoutul principal care se încarcă odată cu pornirea aplicației. Conține un layout de tip constraintlayout în interiorul căruia se găsesc următoarele elemente: 

 - RecyclerView - care conține lista de știri 
 - ProgressBar - Care afișează 
 - TextView
 - FloatingActionButton (quit)
 - FloatingActionButton (refresh)

