# Aplicatie care preia un feed de stiri si il afiseaza in doua activitati

## Concepte importante

### RecyclerView

Un component (view) care reprezinta o varianta avansata a vechilor ListView sau GridView. 

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
    │               │   ├── ic_launcher_6.png
    │               │   └── ic_launcher_round_6.png
    │               ├── layout
    │               │   ├── activity_main.xml
    │               │   ├── activity_news_detail.xml
    │               │   └── item_news.xml
    │               └── values
    │                   ├── colors.xml
    │                   ├── strings.xml
    │                   └── themes.xml
    ├── build.gradle
    ├── gradle.properties
    └── settings.gradle
```