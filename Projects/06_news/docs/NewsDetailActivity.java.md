# NewsDetailActivity.java - Documentație detaliată

## Declarația pachetului
```java
package ro.makore.akrilki_06;
```
 Declară pachetul în care se află clasa NewsDetailActivity, același pachet ca MainActivity.

## Import-uri Android Framework

```java
import android.os.Bundle;
```
 Import pentru Bundle, folosit pentru a salva și restaura starea activității.

```java
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
```
 Import-uri pentru widget-uri UI: TextView pentru afișarea textului (titlu, descriere, tag-uri), ImageView pentru imaginea știrii, Button pentru butonul de înapoi.

```java
import android.widget.LinearLayout;
```
 Import pentru LinearLayout, necesar pentru a modifica parametrii de layout ai ImageView (înălțimea proporțională).

```java
import android.util.Log;
```
 Import pentru clasa Log, folosită pentru a scrie mesaje de eroare în Logcat când încărcarea imaginii eșuează.

## Import-uri specifice aplicației

```java
import ro.makore.akrilki_06.model.NewsItem;
```
 Import pentru clasa model NewsItem care conține datele unei știri (titlu, descriere, URL imagine, tag-uri).

## Import-uri biblioteca Glide

```java
import com.bumptech.glide.Glide;
```
 Import pentru Glide, biblioteca de încărcare și caching imagini care descarcă imaginea știrii de pe internet.

```java
import com.bumptech.glide.request.RequestListener;
```
 Import pentru RequestListener, interfața care permite ascultarea evenimentelor de succes sau eșec la încărcarea imaginii.

```java
import com.bumptech.glide.load.engine.GlideException;
```
 Import pentru GlideException, excepția aruncată de Glide când încărcarea imaginii eșuează.

```java
import android.graphics.drawable.Drawable;
```
 Import pentru Drawable, reprezentarea imaginii încărcate de Glide.

```java
import androidx.annotation.Nullable;
```
 Import pentru adnotarea @Nullable, indicând că un parametru poate fi null.

```java
import com.bumptech.glide.request.target.Target;
```
 Import pentru Target, destinația în care Glide încarcă imaginea (în acest caz, ImageView-ul).

```java
import com.bumptech.glide.load.DataSource;
```
 Import pentru DataSource, enumerarea care indică sursa imaginii (cache disk, cache memorie, rețea).

## Import-uri AndroidX

```java
import androidx.appcompat.app.AppCompatActivity;
```
 Import pentru clasa de bază AppCompatActivity care oferă compatibilitate cu versiuni mai vechi de Android.

## Declarația clasei

```java
public class NewsDetailActivity extends AppCompatActivity {
```
 Declară clasa NewsDetailActivity care moștenește AppCompatActivity. Aceasta este activitatea secundară care afișează detaliile complete ale unei știri.

## Declarații variabile membre

```java
private TextView titleTextView;
```
 Declară referința către TextView-ul care afișează titlul știrii.

```java
private TextView descriptionTextView;
```
 Declară referința către TextView-ul care afișează descrierea completă a știrii.

```java
private TextView tagsTextView;
```
 Declară referința către TextView-ul care afișează tag-urile/conceptele asociate știrii.

```java
private ImageView dImageView;
```
 Declară referința către ImageView-ul care afișează imaginea thumbnail a știrii încărcată de Glide.

```java
private Button backButton;
```
 Declară referința către butonul care permite utilizatorului să se întoarcă la lista de știri.

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
setContentView(R.layout.activity_news_detail);
```
 Setează layout-ul XML (activity_news_detail.xml) ca interfață vizuală a acestei activități.

```java
titleTextView = findViewById(R.id.titleTextView);
```
 Găsește TextView-ul pentru titlu din layout folosind ID-ul titleTextView și salvează referința.

```java
descriptionTextView = findViewById(R.id.descriptionTextView);
```
 Găsește TextView-ul pentru descriere din layout folosind ID-ul descriptionTextView și salvează referința.

```java
dImageView = findViewById(R.id.dimageView);
```
 Găsește ImageView-ul pentru imaginea știrii din layout folosind ID-ul dimageView și salvează referința.

```java
tagsTextView = findViewById(R.id.tagsTextView);
```
 Găsește TextView-ul pentru tag-uri din layout folosind ID-ul tagsTextView și salvează referința.

```java
backButton = findViewById(R.id.backButton);
```
 Găsește butonul de înapoi din layout folosind ID-ul backButton și salvează referința.

```java
NewsItem newsItem = getIntent().getParcelableExtra("news_item");
```
 Recuperează obiectul NewsItem trimis de MainActivity prin Intent folosind cheia "news_item". NewsItem implementează Parcelable pentru serializare eficientă.

```java
if (newsItem != null) {
```
 Verifică dacă obiectul NewsItem a fost primit cu succes (nu este null), pentru a preveni NullPointerException.

```java
titleTextView.setText(newsItem.getTitle());
```
 Setează titlul știrii în TextView folosind getter-ul getTitle() din obiectul NewsItem.

```java
descriptionTextView.setText(newsItem.getBody());
```
 Setează descrierea completă a știrii în TextView folosind getter-ul getBody() din obiectul NewsItem.

```java
tagsTextView.setText(String.join(", ", newsItem.getConcepts()));
```
 Concatenează lista de concepte/tag-uri ale știrii într-un String separat prin virgulă și spațiu, apoi setează textul în TextView.

## Încărcarea imaginii cu Glide

```java
Glide.with(NewsDetailActivity.this)
```
 Inițializează Glide cu Context-ul activității. NewsDetailActivity.this asigură referința corectă în contextul listener-ului.

```java
.load(newsItem.getThumbnailUrl())
```
 Specifică URL-ul imaginii de încărcat, obținut din NewsItem prin getter-ul getThumbnailUrl().

```java
.listener(new RequestListener<Drawable>() {
```
 Atașează un RequestListener pentru a asculta evenimentele de încărcare a imaginii (succes sau eșec).

### Metoda onLoadFailed

```java
@Override
public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
```
 Metoda apelată când Glide eșuează să încarce imaginea. Parametrii: excepția GlideException, model-ul (URL), target-ul (ImageView), și flag dacă e prima resursă.

```java
Log.e("Glide", "Image load failed", e);
```
 Scrie un mesaj de eroare în Logcat cu tag-ul "Glide" și stack trace-ul excepției pentru debugging.

```java
return false;
```
 Returnează false pentru a permite Glide să gestioneze eroarea în mod implicit (afișare placeholder sau error drawable).

### Metoda onResourceReady

```java
@Override
public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
```
 Metoda apelată când imaginea a fost încărcată cu succes. Parametrii: resursa Drawable încărcată, model-ul (URL), target-ul (ImageView), sursa datelor, și flag.

```java
dImageView.post(() -> {
```
 Postează un Runnable pe message queue-ul ImageView-ului pentru a executa codul după ce view-ul a fost măsurat și așezat în layout.

```java
int intrinsicWidth = resource.getIntrinsicWidth();
```
 Obține lățimea nativă (în pixeli) a imaginii încărcate din Drawable.

```java
int intrinsicHeight = resource.getIntrinsicHeight();
```
 Obține înălțimea nativă (în pixeli) a imaginii încărcate din Drawable.

```java
int viewWidth = dImageView.getWidth();
```
 Obține lățimea curentă a ImageView-ului în pixeli, așa cum a fost calculată de layout manager.

```java
int viewHeight = (int) ((float) intrinsicHeight / intrinsicWidth * viewWidth);
```
 Calculează înălțimea proporțională a ImageView-ului pentru a păstra aspect ratio-ul original al imaginii. Formula: (înălțime_originală / lățime_originală) × lățime_view.

```java
LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dImageView.getLayoutParams();
```
 Obține parametrii de layout ai ImageView-ului prin cast la LinearLayout.LayoutParams (presupune că parent-ul este LinearLayout).

```java
params.height = viewHeight;
```
 Setează înălțimea calculată în parametrii de layout pentru a ajusta ImageView-ul la dimensiunea proporțională.

```java
dImageView.setLayoutParams(params);
```
 Aplică parametrii de layout modificați la ImageView, declanșând re-layout-ul cu noua înălțime.

```java
return false;
```
 Returnează false pentru a permite Glide să seteze resursa în ImageView în mod implicit (după ce dimensiunile au fost ajustate).

```java
.into(dImageView);
```
 Specifică ImageView-ul destinație în care Glide va încărca imaginea după procesare.

## Ramura else - Lipsa datelor

```java
} else {
```
 Ramura else se execută când newsItem este null (nu s-au primit date de la MainActivity).

```java
titleTextView.setText("No news is good news");
```
 Setează un mesaj humoristic în titlu când nu există date de afișat.

```java
descriptionTextView.setText("isn't it");
```
 Completează mesajul humoristic în descriere când nu există date de afișat.

## Configurarea butonului de înapoi

```java
backButton.setOnClickListener(v -> finish());
```
 Setează un listener pentru butonul de înapoi care apelează finish() la click, închizând activitatea curentă și returnând la MainActivity.

## Fluxul aplicației

### La deschiderea detaliilor unei știri:
1. MainActivity apelează `startActivity()` cu Intent către NewsDetailActivity
2. Intent-ul conține obiectul NewsItem serializat ca Parcelable cu cheia "news_item"
3. `onCreate()` se execută:
   - Setează layout-ul activity_news_detail.xml
   - Găsește toate view-urile (TextViews, ImageView, Button)
   - Recuperează NewsItem din Intent
4. Dacă NewsItem există:
   - Setează titlul în titleTextView
   - Setează descrierea completă în descriptionTextView
   - Concatenează și setează tag-urile în tagsTextView
   - Inițializează Glide pentru încărcarea imaginii:
     * Încarcă imaginea de la URL-ul din getThumbnailUrl()
     * La succes: calculează dimensiuni proporționale și ajustează ImageView
     * La eșec: loghează eroarea în Logcat
5. Dacă NewsItem este null:
   - Afișează mesaje humoristice de fallback
6. Configurează butonul de înapoi să închidă activitatea

### La apăsarea butonului înapoi:
1. `finish()` este apelat
2. Activitatea se închide
3. Utilizatorul revine la MainActivity cu lista de știri

## Comunicare între activități

### Date trimise de MainActivity:
```java
Intent intent = new Intent(context, NewsDetailActivity.class);
intent.putExtra("news_item", newsItem); // newsItem implementează Parcelable
startActivity(intent);
```

### Date primite în NewsDetailActivity:
```java
NewsItem newsItem = getIntent().getParcelableExtra("news_item");
```

## Aspecte tehnice importante

### Calcul proporțional al înălțimii ImageView:
- Imaginea păstrează aspect ratio-ul original
- Formula: `înălțime_nouă = (înălțime_originală / lățime_originală) × lățime_view`
- Ajustarea se face după încărcarea completă a imaginii
- Executat în `post()` pentru a garanta că view-ul este măsurat

### Gestionarea erorilor Glide:
- `onLoadFailed()`: loghează eroarea și lasă Glide să afișeze placeholder
- `onResourceReady()`: ajustează dimensiunile înainte ca Glide să seteze imaginea

### Serializare eficientă:
- Folosește Parcelable în loc de Serializable pentru performanță mai bună
- NewsItem trebuie să implementeze interfața Parcelable
