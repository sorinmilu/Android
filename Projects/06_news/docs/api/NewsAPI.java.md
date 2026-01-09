# NewsAPI.java - Documentație detaliată

## Declarația pachetului
```java
package ro.makore.akrilki_06.api;
```
 Declară pachetul în care se află clasa NewsAPI. Subpachetul `api` indică că această clasă gestionează comunicarea cu API-uri externe.

## Import-uri Android Framework

```java
import android.content.Context;
```
 Import pentru Context, necesar pentru a accesa folderul assets din care se citesc fișierele JSON cu body-ul cererii și cheia API.

```java
import android.util.Log;
```
 Import pentru clasa Log, disponibilă pentru logging (deși nu este folosit în versiunea actuală a codului).

## Import-uri OkHttp

```java
import okhttp3.OkHttpClient;
```
 Import pentru OkHttpClient, clientul HTTP folosit pentru a face cereri sincrone către serverul de știri.

```java
import okhttp3.MediaType;
```
 Import pentru MediaType, folosit pentru a specifica tipul de conținut al cererii (application/json).

```java
import okhttp3.Request;
```
 Import pentru Request, reprezentarea cererii HTTP care conține URL-ul, metoda (POST), headers și body.

```java
import okhttp3.Response;
```
 Import pentru Response, reprezentarea răspunsului HTTP primit de la server care conține status code, headers și body.

```java
import okhttp3.RequestBody;
```
 Import pentru RequestBody, folosit pentru a crea corpul cererii POST cu datele JSON.

## Import-uri Java

```java
import java.io.IOException;
```
 Import pentru IOException, aruncată când operațiunile de citire/scriere (fișiere assets, cereri HTTP) eșuează.

```java
import java.io.InputStream;
```
 Import pentru InputStream, folosit pentru a citi conținutul fișierelor JSON din folderul assets.

```java
import org.json.JSONObject;
```
 Import pentru JSONObject, folosit pentru a parsa și manipula obiectele JSON (adăugarea cheii API în body-ul cererii).

## Declarația clasei

```java
public class NewsAPI {
```
 Declară clasa publică NewsAPI care conține metode statice pentru a face cereri HTTP către API-ul de știri EventRegistry.

## Constante

```java
private static final String API_URL = "https://eventregistry.org/api/v1/article/getArticles";
```
 Declară URL-ul endpoint-ului API pentru obținerea articolelor de știri de la EventRegistry. Constanta este private static final (neschimbabilă).

## Metoda readJsonFromAssets

```java
private static String readJsonFromAssets(Context context, String fileName) throws IOException {
```
 Declară metoda privată statică care citește conținutul unui fișier JSON din folderul assets și îl returnează ca String. Aruncă IOException dacă citirea eșuează.

```java
InputStream is = context.getAssets().open(fileName);
```
 Deschide fișierul specificat din folderul assets folosind AssetManager-ul obținut din Context. Returnează un InputStream pentru citire.

```java
int size = is.available();
```
 Obține numărul de bytes disponibili pentru citire din InputStream, reprezentând dimensiunea fișierului.

```java
byte[] buffer = new byte[size];
```
 Creează un array de bytes cu dimensiunea fișierului pentru a stoca tot conținutul într-o singură citire.

```java
is.read(buffer);
```
 Citește tot conținutul InputStream-ului în buffer-ul de bytes creat anterior.

```java
is.close();
```
 Închide InputStream-ul pentru a elibera resursele sistemului de fișiere.

```java
return new String(buffer, "UTF-8");
```
 Convertește array-ul de bytes în String folosind encoding-ul UTF-8 și returnează rezultatul.

## Metoda fetchNews

```java
public static String fetchNews(Context context) throws Exception {
```
 Declară metoda publică statică care face cererea HTTP POST către API și returnează răspunsul JSON ca String. Aruncă Exception dacă cererea eșuează.

```java
OkHttpClient client = new OkHttpClient();
```
 Creează o instanță nouă de OkHttpClient pentru a executa cererea HTTP. Clientul gestionează conexiunile, timeout-urile și retry-urile.

### Citirea fișierelor JSON

```java
String jsonBody = readJsonFromAssets(context, "request_body.json");
```
 Citește conținutul fișierului request_body.json din folderul assets, care conține parametrii cererii (query, filters, resultType, etc.).

```java
String apiKeyJson = readJsonFromAssets(context, "api_key.json");
```
 Citește conținutul fișierului api_key.json din folderul assets, care conține cheia API pentru autentificare la EventRegistry.

### Parsarea și manipularea JSON

```java
JSONObject requestBody = new JSONObject(jsonBody);
```
 Parsează String-ul JSON citit din request_body.json într-un obiect JSONObject pentru manipulare programatică.

```java
JSONObject apiKeyObject = new JSONObject(apiKeyJson);
```
 Parsează String-ul JSON citit din api_key.json într-un obiect JSONObject pentru a extrage cheia API.

```java
requestBody.put("apiKey", apiKeyObject.getString("apiKey"));
```
 Adaugă cheia API în obiectul requestBody, extragând-o din apiKeyObject. Acest lucru combină parametrii cererii cu autentificarea.

```java
jsonBody = requestBody.toString();
```
 Convertește obiectul JSONObject modificat înapoi în String pentru a fi trimis ca body al cererii HTTP.

### Configurarea cererii HTTP

```java
MediaType JSON = MediaType.get("application/json; charset=utf-8");
```
 Creează un obiect MediaType care specifică că body-ul cererii este JSON cu encoding UTF-8.

```java
RequestBody body = RequestBody.create(JSON, jsonBody);
```
 Creează RequestBody-ul cererii POST folosind MediaType-ul JSON și String-ul cu datele JSON.

```java
Request request = new Request.Builder()
        .url(API_URL)
        .post(body)
        .build();
```
 Construiește obiectul Request folosind Builder pattern: setează URL-ul endpoint-ului, specifică metoda POST cu body-ul JSON și finalizează construcția.

### Execuția cererii și returnarea răspunsului

```java
try (Response response = client.newCall(request).execute()) {
```
 Execută cererea HTTP sincron (blocant) folosind try-with-resources pentru a închide automat Response-ul. newCall() creează un Call, execute() îl execută.

```java
return response.body().string();
```
 Extrage body-ul răspunsului HTTP și îl convertește în String (JSON), apoi îl returnează către apelant pentru parsare.

## Fluxul cererii HTTP

### La apelarea fetchNews() din MainActivity:
1. Se creează OkHttpClient nou
2. Se citește `request_body.json` din assets:
   - Conține query (filtru pentru limb, dată, tip date)
   - Conține resultType (articles)
   - Conține sorting (sortBy date)
   - Conține opțiuni (includeArticleConcepts, includeArticleImage)
   - Conține paginare (articlesPage, articlesCount)
3. Se citește `api_key.json` din assets:
   - Conține cheia API pentru autentificare
4. Se parsează ambele String-uri JSON în JSONObject
5. Se adaugă cheia API din api_key.json în request_body
6. Se convertește JSONObject modificat înapoi în String
7. Se creează MediaType pentru application/json
8. Se creează RequestBody cu MediaType și String-ul JSON
9. Se construiește Request cu:
   - URL: https://eventregistry.org/api/v1/article/getArticles
   - Method: POST
   - Body: RequestBody cu parametrii JSON
10. Se execută cererea sincron cu execute()
11. Se extrage body-ul răspunsului ca String
12. String-ul JSON este returnat către MainActivity pentru parsare

## Structura fișierelor assets

### request_body.json:
Conține parametrii cererii:
- `query`: Filtrele pentru selecția articolelor (limbă, dată, tip)
- `resultType`: Tipul rezultatelor dorite (articles)
- `articlesSortBy`: Criteriul de sortare (date/rel)
- `includeArticleConcepts`: Flag pentru includerea conceptelor/tag-urilor
- `includeArticleImage`: Flag pentru includerea URL-urilor imaginilor
- `articlesPage`: Numărul paginii (pentru paginare)
- `articlesCount`: Numărul de articole per pagină

### api_key.json:
Conține cheia de autentificare:
- `apiKey`: Cheia API pentru EventRegistry (9506e4f0-519a-4c9b-87b4-0c4ca8009126)

## Securitate

### Separarea cheii API:
Cheia API este stocată separat în `api_key.json` și nu este hardcodată în cod. Aceasta permite:
- Schimbarea cheii fără recompilare
- Excluderea din controlul versiunilor (gitignore)
- Gestionarea diferită pentru build-uri de development/production

### Limitări actuale:
- Cheia API este încă în folderul assets (accesibilă în APK)
- Fișierul assets poate fi extras din APK decompilat
- Pentru securitate completă, API-ul ar trebui apelat printr-un server intermediar

## Particularități tehnice

### Sincronicitate:
- `execute()` este sincron (blocant) - trebuie executat pe thread secundar
- MainActivity folosește ExecutorService pentru a apela fetchNews() pe background thread
- Dacă ar fi apelat pe main thread, ar arunca NetworkOnMainThreadException

### Try-with-resources:
- `try (Response response = ...)` închide automat response-ul
- Previne memory leak-uri și connection leak-uri
- Echivalent cu finally { response.close(); }

### Gestionarea resurselor:
- InputStream este închis explicit cu `close()`
- Response este închis automat prin try-with-resources
- OkHttpClient reutilizează conexiunile pentru eficiență
