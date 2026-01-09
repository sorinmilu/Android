# NewsParser.java - Documentație detaliată

## Declarația pachetului
```java
package ro.makore.akrilki_06.parser;
```
 Declară pachetul în care se află clasa NewsParser. Subpachetul `parser` indică că această clasă se ocupă cu parsarea datelor (JSON → obiecte Java).

## Import-uri Gson

```java
import com.google.gson.Gson;
```
 Import pentru clasa principală Gson care convertește JSON în obiecte Java și invers.

```java
import com.google.gson.JsonArray;
```
 Import pentru JsonArray, reprezentarea Gson a unui array JSON pentru acces direct la elemente.

```java
import com.google.gson.JsonObject;
```
 Import pentru JsonObject, reprezentarea Gson a unui obiect JSON pentru acces direct la câmpuri.

## Import-uri specifice aplicației

```java
import ro.makore.akrilki_06.model.NewsItem;
```
 Import pentru clasa model NewsItem în care se parsează datele JSON ale fiecărei știri.

## Import-uri Java

```java
import java.util.ArrayList;
import java.util.List;
```
 Import-uri pentru ArrayList și List, folosite pentru a stoca colecția de NewsItem și lista de concepte.

## Import-uri ThreeTen (AndroidThreeTen)

```java
import org.threeten.bp.Instant;
```
 Import pentru Instant din biblioteca ThreeTen, reprezentând un moment precis în timp (timestamp).

```java
import org.threeten.bp.LocalDateTime;
```
 Import pentru LocalDateTime din ThreeTen, reprezentând o dată și oră fără timezone (pentru formatare).

```java
import org.threeten.bp.format.DateTimeFormatter;
```
 Import pentru DateTimeFormatter din ThreeTen, folosit pentru a formata LocalDateTime în String cu pattern custom.

```java
import org.threeten.bp.Duration;
```
 Import pentru Duration din ThreeTen, reprezentând diferența de timp între două Instant-uri (pentru calcul timp relativ).

```java
import org.threeten.bp.ZoneId;
```
 Import pentru ZoneId din ThreeTen, reprezentând zona de timp (folosit pentru conversie Instant → LocalDateTime).

## Declarația clasei

```java
public class NewsParser {
```
 Declară clasa publică NewsParser care conține metode statice pentru parsarea JSON-ului de la API în listă de obiecte NewsItem.

## Metoda parseNews

```java
public static List<NewsItem> parseNews(String jsonResponse) {
```
 Declară metoda publică statică care primește răspunsul JSON ca String și returnează listă de NewsItem parseate.

```java
List<NewsItem> newsItems = new ArrayList<>();
```
 Creează lista goală de NewsItem care va fi populată cu știrile parseate și returnată la final.

```java
Gson gson = new Gson();
```
 Creează instanță nouă de Gson pentru a parsa JSON-ul în structuri JsonObject și JsonArray.

### Extragerea array-ului de articole

```java
JsonArray articles = gson.fromJson(jsonResponse, JsonObject.class)
    .getAsJsonObject("articles")
    .getAsJsonArray("results");
```
 Parsează JSON-ul: convertește String-ul în JsonObject, extrage obiectul "articles", apoi extrage array-ul "results" care conține lista de articole.

### Iterarea prin articole

```java
for (int i = 0; i < articles.size(); i++) {
```
 Începe bucla for care iterează prin toate articolele din array-ul results.

```java
JsonObject article = articles.get(i).getAsJsonObject();
```
 Extrage articolul curent la poziția i și îl convertește în JsonObject pentru acces la câmpurile sale.

```java
NewsItem item = new NewsItem();
```
 Creează o instanță nouă goală de NewsItem care va fi populată cu datele parseate din JSON.

### Parsarea câmpului title

```java
if (article.has("title") && !article.get("title").isJsonNull()) {
```
 Verifică dacă articolul conține câmpul "title" ȘI dacă valoarea nu este null JSON, pentru a evita excepții.

```java
item.setTitle(article.get("title").getAsString());
```
 Extrage valoarea String a câmpului "title" din JSON și o setează în obiectul NewsItem.

```java
} else {
```
 Ramura else se execută când câmpul "title" lipsește sau este null.

```java
item.setTitle("Untitled article");
```
 Setează un titlu de fallback când datele originale lipsesc, evitând să fie null.

### Parsarea câmpului body

```java
if (article.has("body") && !article.get("body").isJsonNull()) {
```
 Verifică dacă articolul conține câmpul "body" ȘI dacă valoarea nu este null JSON.

```java
item.setBody(article.get("body").getAsString());
```
 Extrage valoarea String a câmpului "body" (conținutul complet) din JSON și o setează în NewsItem.

```java
} else {
```
 Ramura else se execută când câmpul "body" lipsește sau este null.

```java
item.setBody("no body content for the newsitem");
```
 Setează un mesaj de fallback când conținutul lipsește, evitând să fie null.

### Parsarea câmpului image

```java
if (article.has("image") && !article.get("image").isJsonNull()) {
```
 Verifică dacă articolul conține câmpul "image" ȘI dacă valoarea nu este null JSON.

```java
item.setThumbnailUrl(article.get("image").getAsString());
```
 Extrage URL-ul imaginii din câmpul "image" și îl setează ca thumbnailUrl în NewsItem.

```java
} else {
```
 Ramura else se execută când câmpul "image" lipsește sau este null.

```java
item.setThumbnailUrl("");
```
 Setează String gol ca fallback pentru URL-ul imaginii când lipsește.

### Parsarea câmpului lang

```java
if (article.has("lang") && !article.get("lang").isJsonNull()) {
```
 Verifică dacă articolul conține câmpul "lang" ȘI dacă valoarea nu este null JSON.

```java
item.setLanguage(article.get("lang").getAsString());
```
 Extrage codul limbii din câmpul "lang" și îl setează în NewsItem.

```java
} else {
```
 Ramura else se execută când câmpul "lang" lipsește sau este null.

```java
item.setLanguage("");
```
 Setează String gol ca fallback pentru limba când lipsește.

### Parsarea obiectului source

```java
if (article.has("source") && !article.get("source").isJsonNull()) {
```
 Verifică dacă articolul conține câmpul "source" ȘI dacă valoarea nu este null JSON.

```java
JsonObject source = article.getAsJsonObject("source");
```
 Extrage obiectul JSON imbricat "source" care conține informații despre sursa/publicația știrii.

```java
if (source.has("title") && !source.get("title").isJsonNull()) {
```
 Verifică dacă obiectul source conține câmpul "title" (numele publicației) ȘI dacă nu este null.

```java
item.setSource(source.get("title").getAsString());
```
 Extrage titlul sursei (numele publicației) din obiectul source și îl setează în NewsItem.

```java
} else {
```
 Ramura else se execută când câmpul "title" din obiectul source lipsește sau este null.

```java
item.setSource("");
```
 Setează String gol ca fallback pentru sursa când titlul lipsește.

```java
} else {
```
 Ramura else exterioară se execută când întregul obiect "source" lipsește sau este null.

```java
item.setSource("");
```
 Setează String gol ca fallback pentru sursa când întregul obiect lipsește.

### Parsarea și formatarea câmpului dateTime

```java
if (article.has("dateTime") && !article.get("dateTime").isJsonNull()) {
```
 Verifică dacă articolul conține câmpul "dateTime" ȘI dacă valoarea nu este null JSON.

```java
try {
```
 Începe blocul try pentru a prinde excepțiile de parsare a datei (format invalid, timezone probleme).

```java
String dateTime = article.get("dateTime").getAsString();
```
 Extrage String-ul dateTime din JSON (format ISO 8601, de ex. "2024-01-09T15:30:00Z").

```java
Instant parsedInstant = Instant.parse(dateTime);
```
 Parsează String-ul ISO 8601 într-un Instant (moment precis în timp, UTC).

```java
LocalDateTime parsedDateTime = LocalDateTime.ofInstant(parsedInstant, ZoneId.systemDefault());
```
 Convertește Instant-ul în LocalDateTime folosind zona de timp a sistemului pentru formatare locală.

```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm yyyy-MM-dd");
```
 Creează formatter cu pattern-ul "HH:mm yyyy-MM-dd" (de ex. "15:30 2024-01-09").

```java
String formattedDateTime = parsedDateTime.format(formatter);
```
 Formatează LocalDateTime-ul conform pattern-ului în String pentru afișare.

```java
Instant now = Instant.now();
```
 Obține Instant-ul curent (momentul de acum) pentru a calcula diferența de timp.

```java
Duration duration = Duration.between(parsedInstant, now);
```
 Calculează durata (diferența) între momentul știrii și momentul curent.

```java
String relativeTime;
long seconds = duration.getSeconds();
```
 Extrage numărul total de secunde din Duration pentru a determina timpul relativ.

```java
if (seconds < 120) {
```
 Verifică dacă au trecut mai puțin de 120 secunde (2 minute).

```java
relativeTime = (seconds < 60) ? "now" : "1 minute ago";
```
 Setează "now" dacă mai puțin de 60 secunde, altfel "1 minute ago". Expresie ternară pentru decizie.

```java
} else if (seconds < 3600) {
```
 Verifică dacă au trecut mai puțin de 3600 secunde (1 oră).

```java
relativeTime = (seconds / 60) + " minutes ago";
```
 Calculează numărul de minute (secunde / 60) și construiește String-ul "X minutes ago".

```java
} else if (seconds < 86400) {
```
 Verifică dacă au trecut mai puțin de 86400 secunde (24 ore = 1 zi).

```java
relativeTime = (seconds / 3600) + " hours ago";
```
 Calculează numărul de ore (secunde / 3600) și construiește String-ul "X hours ago".

```java
} else {
```
 Ramura else se execută când au trecut mai mult de 24 ore.

```java
long days = seconds / 86400;
```
 Calculează numărul de zile împărțind secundele la 86400 (secunde într-o zi).

```java
relativeTime = days + (days == 1 ? " day ago" : " days ago");
```
 Construiește String-ul cu singular "day ago" pentru 1 zi sau plural "days ago" pentru mai multe zile.

```java
String result = formattedDateTime + " (" + relativeTime + ")";
```
 Combină data formatată cu timpul relativ: "15:30 2024-01-09 (2 hours ago)".

```java
item.setDateTime(result);
```
 Setează String-ul combinat în NewsItem pentru afișare în UI.

```java
} catch (Exception e) {
```
 Prinde orice excepție care poate apărea la parsarea sau formatarea datei.

```java
item.setDateTime("Invalid dateTime");
```
 Setează mesaj de eroare când parsarea datei eșuează, evitând crash-ul aplicației.

```java
} else {
```
 Ramura else se execută când câmpul "dateTime" lipsește sau este null.

```java
item.setDateTime("NO has datetime");
```
 Setează mesaj indicând că data/ora lipsește din date.

### Parsarea array-ului concepts

```java
if (article.has("concepts") && article.get("concepts").isJsonArray()) {
```
 Verifică dacă articolul conține câmpul "concepts" ȘI dacă este de tip array JSON.

```java
JsonArray concepts = article.getAsJsonArray("concepts");
```
 Extrage array-ul JSON "concepts" care conține lista de concepte/tag-uri asociate cu știrea.

```java
List<String> conceptsList = new ArrayList<>();
```
 Creează listă goală de String-uri pentru a stoca label-urile conceptelor extrase.

```java
for (int j = 0; j < concepts.size(); j++) {
```
 Iterează prin toate elementele din array-ul de concepte.

```java
JsonObject concept = concepts.get(j).getAsJsonObject();
```
 Extrage conceptul curent la poziția j și îl convertește în JsonObject.

```java
if (concept.has("label")) {
```
 Verifică dacă obiectul concept conține câmpul "label" (etichetele multilingve).

```java
JsonObject label = concept.getAsJsonObject("label");
```
 Extrage obiectul JSON imbricat "label" care conține traduceri în diferite limbi.

```java
if (label.has("eng")) {
```
 Verifică dacă label-ul conține traducerea în engleză (cheia "eng").

```java
conceptsList.add(label.get("eng").getAsString());
```
 Extrage label-ul în engleză și îl adaugă în lista de concepte.

```java
item.setConcepts(conceptsList);
```
 Setează lista de concepte extrasă în obiectul NewsItem după terminarea iterației.

```java
} else {
```
 Ramura else se execută când câmpul "concepts" lipsește sau nu este array.

```java
item.setConcepts(new ArrayList<>());
```
 Setează listă goală ca fallback pentru concepte când datele lipsesc, evitând null.

```java
newsItems.add(item);
```
 Adaugă obiectul NewsItem complet populat în lista de newsItems pentru returnare.

### Returnarea rezultatului

```java
return newsItems;
```
 Returnează lista completă de NewsItem parseate către apelant (MainActivity).

## Structura JSON de intrare

### Structura generală:
```json
{
  "articles": {
    "results": [
      {
        "title": "Article Title",
        "body": "Full article content...",
        "image": "https://example.com/image.jpg",
        "lang": "eng",
        "source": {
          "title": "BBC News"
        },
        "dateTime": "2024-01-09T15:30:00Z",
        "concepts": [
          {
            "label": {
              "eng": "Technology",
              "ron": "Tehnologie"
            }
          }
        ]
      }
    ]
  }
}
```

## Procesul de parsare

### Pașii principali:
1. **Extragere structură:** jsonResponse → JsonObject → "articles" → "results" → JsonArray
2. **Iterare articole:** Pentru fiecare element din array
3. **Parsare câmpuri simple:** title, body, image, lang (cu verificări null)
4. **Parsare obiect imbricat:** source.title (cu verificări pe două niveluri)
5. **Parsare și formatare dată:**
   - Parsare ISO 8601 → Instant
   - Conversie la LocalDateTime (timezone sistem)
   - Formatare cu pattern "HH:mm yyyy-MM-dd"
   - Calcul timp relativ (now, minutes/hours/days ago)
   - Combinare în format final: "15:30 2024-01-09 (2 hours ago)"
6. **Parsare array imbricat:** concepts → extragere label.eng pentru fiecare
7. **Adăugare în listă:** NewsItem populat → newsItems
8. **Returnare:** Lista completă de NewsItem

## Gestionarea erorilor

### Verificări pentru null:
- `has("field")`: Verifică existența câmpului
- `!get("field").isJsonNull()`: Verifică că nu este null JSON
- Fallback-uri: String-uri goale, mesaje descriptive, liste goale

### Try-catch pentru dateTime:
- Prinde excepții de parsare (format invalid, timezone)
- Setează "Invalid dateTime" în caz de eroare
- Previne crash-ul aplicației pentru date corupte

### Verificări pentru tipuri:
- `isJsonArray()`: Verifică că "concepts" este array
- Validări pe obiecte imbricate (source.title, label.eng)

## Formatarea timpului relativ

### Logica de calcul:
- **< 60s:** "now"
- **60s - 120s:** "1 minute ago"
- **2min - 1h:** "X minutes ago"
- **1h - 24h:** "X hours ago"
- **> 24h:** "X day(s) ago"

### Avantaje:
- Utilizatorii văd cât de recente sunt știrile
- Informație dublă: dată exactă + timp relativ
- Actualizare la fiecare refresh (recalculare)
