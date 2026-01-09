# MainActivity.java - Documentație detaliată

## Declarația pachetului
```java
package ro.makore.akrilki_06;
```
 Declară pachetul în care se află clasa MainActivity. Conventia de denumire folosește domeniul inversat (ro.makore) urmat de numele aplicației (akrilki_06).

## Import-uri Android Framework

```java
import android.content.Context;
```
 Import pentru clasa Context, necesară pentru a accesa servicii sistem precum ConnectivityManager.

```java
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
```
 Import-uri pentru verificarea stării conexiunii la internet. ConnectivityManager permite interogarea stării rețelei, iar NetworkInfo conține informații despre conexiunea activă.

```java
import android.os.Bundle;
```
 Import pentru Bundle, folosit pentru a salva și restaura starea activității.

## Import-uri AndroidX

```java
import androidx.appcompat.app.AppCompatActivity;
```
 Import pentru clasa de bază AppCompatActivity care oferă compatibilitate cu versiuni mai vechi de Android.

```java
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
```
 Import-uri pentru RecyclerView și LinearLayoutManager. RecyclerView afișează lista de știri, iar LinearLayoutManager aranjează itemii vertical în listă.

## Import-uri UI

```java
import android.util.Log;
```
 Import pentru clasa Log, folosită pentru a scrie mesaje de debugging în Logcat.

```java
import android.view.View;
```
 Import pentru clasa View, necesară pentru a controla vizibilitatea elementelor UI (VISIBLE, GONE).

```java
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
```
 Import-uri pentru widget-uri UI: ProgressBar pentru indicatorul de încărcare, TextView pentru mesajul de încărcare, Toast pentru mesaje temporare către utilizator.

## Import-uri specifice aplicației

```java
import ro.makore.akrilki_06.model.NewsItem;
```
 Import pentru clasa model NewsItem care reprezintă o știre individuală (titlu, descriere, URL, imagine, etc.).

```java
import ro.makore.akrilki_06.parser.NewsParser;
```
 Import pentru NewsParser care parsează JSON-ul primit de la API în obiecte NewsItem.

```java
import ro.makore.akrilki_06.api.NewsAPI;
```
 Import pentru NewsAPI care face cererea HTTP către serverul de știri și returnează răspunsul JSON.

```java
import ro.makore.akrilki_06.adapter.NewsAdapter;
```
 Import pentru NewsAdapter care leagă lista de obiecte NewsItem de RecyclerView pentru afișare.

## Import-uri biblioteci externe

```java
import com.jakewharton.threetenabp.AndroidThreeTen;
```
 Import pentru biblioteca AndroidThreeTen care oferă API-ul modern de date și ore (backport pentru Android API < 26).

```java
import com.google.android.material.floatingactionbutton.FloatingActionButton;
```
 Import pentru FloatingActionButton, butonul circular colorat folosit pentru acțiuni principale (refresh și quit).

```java
import com.google.gson.Gson;
```
 Import pentru biblioteca Gson (Google JSON) folosită pentru parsarea JSON. Deși importat, în cod parsarea se face în NewsParser.

## Import-uri Java

```java
import java.io.IOException;
```
 Import pentru IOException care este aruncată când operațiunile de citire/scriere (inclusiv HTTP) eșuează.

```java
import java.util.List;
```
 Import pentru interfața List care stochează colecții de obiecte NewsItem.

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
```
 Import-uri pentru ExecutorService și Executors, folosite pentru a executa operațiuni de rețea pe un thread secundar, evitând blocarea UI-ului.

## Declarația clasei

```java
public class MainActivity extends AppCompatActivity {
```
 Declară clasa MainActivity care moștenește AppCompatActivity. Aceasta este activitatea principală a aplicației care afișează lista de știri.

## Declarații variabile membre

```java
private RecyclerView recyclerView;
```
 Declară referința către RecyclerView care afișează lista de știri în interfața utilizatorului.

```java
private NewsAdapter newsAdapter;
```
 Declară referința către adapter-ul care leagă datele (lista de NewsItem) de RecyclerView.

```java
private ProgressBar progressBar;
```
 Declară referința către ProgressBar afișat în timpul încărcării știrilor de la server.

```java
private TextView loadingText;
```
 Declară referința către TextView care afișează mesajul "Loading..." în timpul încărcării.

```java
private ExecutorService executorService;
```
 Declară referința către ExecutorService care gestionează un thread secundar pentru operațiuni de rețea, evitând blocarea UI-ului.

## Metoda onCreate

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
```
 Override-ul metodei onCreate apelată când activitatea este creată. Parametrul savedInstanceState conține starea salvată (null la prima creare).

```java
super.onCreate(savedInstanceState);
```
 Apelează implementarea părinte a metodei onCreate pentru inițializări Android framework.

```java
AndroidThreeTen.init(this);
```
 Inițializează biblioteca AndroidThreeTen pentru a putea folosi API-ul modern de date și ore în întreaga aplicație.

```java
setContentView(R.layout.activity_main);
```
 Setează layout-ul XML (activity_main.xml) ca interfață vizuală a acestei activități.

```java
executorService = Executors.newSingleThreadExecutor();
```
 Creează un ExecutorService cu un singur thread pentru a executa operațiuni de rețea în mod secvențial pe un thread de fundal.

```java
FloatingActionButton fabQuit = findViewById(R.id.fab_quit);
```
 Găsește butonul FloatingActionButton pentru ieșirea din aplicație din layout-ul XML folosind ID-ul fab_quit.

```java
fabQuit.setOnClickListener(v -> finishAffinity());
```
 Setează un listener pentru butonul quit care apelează finishAffinity() la click, închizând activitatea curentă și toate activitățile părinte din task.

```java
FloatingActionButton fabRefresh = findViewById(R.id.fab_refresh);
```
 Găsește butonul FloatingActionButton pentru reîmprospătarea listei de știri din layout-ul XML folosind ID-ul fab_refresh.

```java
fabRefresh.setOnClickListener(v -> refreshNewsData());
```
 Setează un listener pentru butonul refresh care apelează metoda refreshNewsData() la click, reîncărcând știrile de la server.

```java
recyclerView = findViewById(R.id.newsRecyclerView);
```
 Găsește RecyclerView din layout-ul XML folosind ID-ul newsRecyclerView și salvează referința în variabila membru.

```java
recyclerView.setLayoutManager(new LinearLayoutManager(this));
```
 Setează LinearLayoutManager pentru RecyclerView, care va aranja itemii vertical în listă. Parametrul this este Context-ul necesar pentru crearea layout manager-ului.

```java
progressBar = findViewById(R.id.progressBar);
```
 Găsește ProgressBar din layout și salvează referința pentru a putea controla vizibilitatea acestuia în timpul încărcării.

```java
loadingText = findViewById(R.id.loadingText);
```
 Găsește TextView-ul pentru mesajul de încărcare și salvează referința pentru a putea controla vizibilitatea acestuia.

```java
refreshNewsData();
```
 Apelează metoda refreshNewsData() pentru a încărca știrile imediat la pornirea aplicației.

## Metoda onDestroy

```java
@Override
protected void onDestroy() {
```
 Override-ul metodei onDestroy apelată când activitatea este distrusă. Aici se eliberează resursele alocate.

```java
super.onDestroy();
```
 Apelează implementarea părinte a metodei onDestroy pentru cleanup-ul framework-ului Android.

```java
if (executorService != null && !executorService.isShutdown()) {
```
 Verifică dacă ExecutorService există și dacă nu a fost deja oprit, pentru a evita excepții.

```java
executorService.shutdown();
```
 Oprește ExecutorService, prevenind acceptarea de noi task-uri și eliberând thread-ul la terminarea task-urilor în curs. Previne memory leak-uri.

## Metoda isNetworkAvailable

```java
private boolean isNetworkAvailable() {
```
 Declară metoda privată care verifică dacă dispozitivul are conexiune la internet activă. Returnează true dacă există conexiune, false altfel.

```java
ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
```
 Obține serviciul sistem ConnectivityManager prin cast-area rezultatului getSystemService. Acest serviciu oferă informații despre conectivitatea rețelei.

```java
NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
```
 Obține informații despre rețeaua activă curentă (WiFi, date mobile, etc.). Poate returna null dacă nu există conexiune activă.

```java
return activeNetworkInfo != null && activeNetworkInfo.isConnected();
```
 Returnează true doar dacă activeNetworkInfo nu este null ȘI dacă rețeaua este efectiv conectată. Verifică ambele condiții pentru siguranță.

## Metoda refreshNewsData

```java
private void refreshNewsData() {
```
 Declară metoda privată care reîmprospătează lista de știri făcând o cerere HTTP către API, parsând răspunsul și actualizând UI-ul.

```java
if (!isNetworkAvailable()) {
```
 Verifică mai întâi dacă există conexiune la internet. Dacă nu există, nu are sens să facă cererea HTTP.

```java
Toast.makeText(this, "No internet connection available.", Toast.LENGTH_LONG).show();
```
 Afișează un mesaj Toast (notificare temporară) informând utilizatorul că nu există conexiune la internet. Durata LONG înseamnă ~3.5 secunde.

```java
return;
```
 Iese din metodă fără a executa cererea HTTP, întrucât nu există conexiune la internet.

```java
runOnUiThread(() -> {
```
 Execută codul din lambda pe thread-ul principal UI. Necesar deoarece refreshNewsData() poate fi apelată din thread-ul executorului.

```java
progressBar.setVisibility(View.VISIBLE);
```
 Face ProgressBar-ul vizibil pentru a indica utilizatorului că se încarcă datele.

```java
loadingText.setVisibility(View.VISIBLE);
```
 Face TextView-ul cu mesajul "Loading..." vizibil alături de ProgressBar.

```java
recyclerView.setVisibility(View.GONE);
```
 Ascunde RecyclerView-ul în timpul încărcării (GONE nu ocupă spațiu în layout, spre deosebire de INVISIBLE).

```java
executorService.execute(() -> {
```
 Execută lambda-ul pe thread-ul secundar gestionat de ExecutorService, pentru a nu bloca UI-ul cu operațiunea de rețea.

```java
try {
```
 Începe blocul try pentru a prinde excepțiile care pot apărea în timpul operațiunilor de rețea sau parsare.

```java
String jsonResponse = NewsAPI.fetchNews(this);
```
 Apelează metoda statică fetchNews din clasa NewsAPI care face cererea HTTP și returnează răspunsul ca String JSON. Operațiune de blocare (sincronă).

```java
List<NewsItem> newsItems = NewsParser.parseNews(jsonResponse);
```
 Parsează String-ul JSON primit de la API în listă de obiecte NewsItem folosind NewsParser.

```java
int count = newsItems.size();
```
 Obține numărul de știri din lista parsată. Variabila este declarată dar nu este folosită în continuare.

```java
runOnUiThread(() -> {
```
 Schimbă înapoi pe thread-ul principal UI pentru a actualiza interfața. Modificările UI trebuie făcute pe thread-ul principal.

```java
progressBar.setVisibility(View.GONE);
```
 Ascunde ProgressBar-ul după ce încărcarea s-a terminat cu succes.

```java
loadingText.setVisibility(View.GONE);
```
 Ascunde mesajul "Loading..." după ce încărcarea s-a terminat.

```java
recyclerView.setVisibility(View.VISIBLE);
```
 Face RecyclerView-ul vizibil pentru a afișa lista de știri.

```java
recyclerView.scrollToPosition(0);
```
 Derulează RecyclerView-ul la prima poziție (top), astfel încât utilizatorul să vadă cele mai noi știri.

```java
if (newsAdapter == null) {
```
 Verifică dacă adapter-ul a fost deja creat. La prima încărcare newsAdapter este null.

```java
newsAdapter = new NewsAdapter(MainActivity.this, newsItems);
```
 Creează un nou NewsAdapter cu Context-ul activității și lista de știri. MainActivity.this este necesar în lambda pentru a obține referința corectă.

```java
recyclerView.setAdapter(newsAdapter);
```
 Atașează adapter-ul nou creat la RecyclerView pentru a afișa știrile.

```java
} else {
```
 Ramura else se execută când adapter-ul există deja (la refresh-uri ulterioare).

```java
newsAdapter.updateData(newsItems);
```
 Actualizează datele în adapter-ul existent cu noile știri, fără a crea un adapter nou. Mai eficient decât recrearea.

```java
} catch (IOException e) {
```
 Prinde excepțiile IOException care pot apărea la eșecul cererii HTTP (probleme de rețea, timeout, server indisponibil, etc.).

```java
Log.e("NEWS06", "Error fetching news "+ e.getMessage(), e);
```
 Scrie un mesaj de eroare în Logcat cu tag-ul "NEWS06", mesajul erorii și stack trace-ul complet pentru debugging.

```java
runOnUiThread(() -> {
```
 Schimbă pe thread-ul principal UI pentru a putea actualiza interfața și afișa mesajul de eroare.

```java
progressBar.setVisibility(View.GONE);
loadingText.setVisibility(View.GONE);
recyclerView.setVisibility(View.VISIBLE);
```
 Ascunde indicatorii de încărcare și face RecyclerView-ul vizibil chiar și în caz de eroare, pentru a nu bloca interfața.

```java
Toast.makeText(MainActivity.this, "Error fetching news. Please check your internet connection.", Toast.LENGTH_LONG).show();
```
 Afișează un Toast informând utilizatorul că a apărut o eroare la încărcarea știrilor și sugerează verificarea conexiunii.

```java
} catch (Exception e) {
```
 Prinde orice alte excepții neprevăzute care nu sunt IOException (erori de parsare, NullPointerException, etc.).

```java
Log.e("NEWS06", "Unexpected error", e);
```
 Scrie în Logcat eroarea neprevăzută pentru debugging, cu stack trace-ul complet.

```java
runOnUiThread(() -> {
```
 Schimbă pe thread-ul principal UI pentru a actualiza interfața în caz de eroare neprevăzută.

```java
progressBar.setVisibility(View.GONE);
loadingText.setVisibility(View.GONE);
recyclerView.setVisibility(View.VISIBLE);
```
 Ascunde indicatorii de încărcare și face RecyclerView-ul vizibil pentru a nu bloca interfața în caz de eroare neprevăzută.

```java
Toast.makeText(MainActivity.this, "Unexpected error occurred.", Toast.LENGTH_LONG).show();
```
 Afișează un Toast generic informând utilizatorul că a apărut o eroare neprevăzută în aplicație.

## Fluxul aplicației

### La pornirea aplicației:
1. `onCreate()` se execută
2. Inițializează AndroidThreeTen pentru lucrul cu date
3. Setează layout-ul XML
4. Creează ExecutorService cu un singur thread
5. Configurează listener-ii pentru butoanele FAB (quit, refresh)
6. Inițializează RecyclerView cu LinearLayoutManager
7. Găsește referințele către ProgressBar și loadingText
8. Apelează `refreshNewsData()` pentru încărcarea inițială

### La refresh (buton sau inițial):
1. `refreshNewsData()` verifică conexiunea la internet
2. Dacă nu există conexiune, afișează Toast și se oprește
3. Afișează ProgressBar și loadingText, ascunde RecyclerView
4. Lansează task pe ExecutorService (thread secundar):
   - Face cerere HTTP prin NewsAPI.fetchNews()
   - Parsează JSON-ul cu NewsParser.parseNews()
   - Pe thread-ul UI: ascunde loading, afișează RecyclerView
   - Dacă e primul load: creează NewsAdapter nou
   - Dacă e refresh: actualizează datele în adapter existent
   - Scroll la poziția 0 (top)
5. În caz de IOException: log + Toast despre eroare rețea
6. În caz de Exception: log + Toast despre eroare neprevăzută

### La închidere:
1. `onDestroy()` se execută
2. Verifică dacă ExecutorService există și nu e deja închis
3. Apelează `shutdown()` pentru a opri thread-ul și preveni memory leak-uri

## Permisiuni necesare (AndroidManifest.xml)
- `android.permission.INTERNET` - pentru cereri HTTP
- `android.permission.ACCESS_NETWORK_STATE` - pentru verificarea conexiunii cu isNetworkAvailable()
