# NewsItem.java - Documentație detaliată

## Declarația pachetului
```java
package ro.makore.akrilki_06.model;
```
 Declară pachetul în care se află clasa NewsItem. Subpachetul `model` indică că această clasă este un model de date (POJO - Plain Old Java Object).

## Import-uri Android Framework

```java
import android.os.Parcel;
```
 Import pentru Parcel, containerul folosit pentru serializarea și deserializarea obiectelor NewsItem pentru transfer între activități.

```java
import android.os.Parcelable;
```
 Import pentru interfața Parcelable, implementată de NewsItem pentru a permite trecerea eficientă a obiectelor prin Intent-uri.

## Import-uri Java

```java
import java.util.List;
```
 Import pentru interfața List, folosită pentru a stoca lista de concepte/tag-uri asociate cu știrea.

## Declarația clasei

```java
public class NewsItem implements Parcelable {
```
 Declară clasa publică NewsItem care implementează interfața Parcelable. Aceasta este clasa model care reprezintă o știre cu toate proprietățile sale.

## Declarații variabile membre

```java
private String title;
```
 Declară câmpul privat pentru titlul știrii, accesibil doar prin getter/setter.

```java
private String body;
```
 Declară câmpul privat pentru conținutul/descrierea completă a știrii, accesibil doar prin getter/setter.

```java
private String thumbnailUrl;
```
 Declară câmpul privat pentru URL-ul imaginii thumbnail asociate cu știrea, accesibil doar prin getter/setter.

```java
private String language;
```
 Declară câmpul privat pentru codul limbii în care este scrisă știrea (de ex. "eng" pentru engleză), accesibil doar prin getter/setter.

```java
private String source;
```
 Declară câmpul privat pentru sursa știrii (numele publicației/site-ului), accesibil doar prin getter/setter.

```java
private String datetime;
```
 Declară câmpul privat pentru data și ora publicării știrii, accesibil doar prin getter/setter.

```java
private List<String> concepts;
```
 Declară câmpul privat pentru lista de concepte/tag-uri/categorii asociate cu știrea, accesibil doar prin getter/setter.

## Constructor implicit

```java
public NewsItem() {
```
 Declară constructorul public fără parametri, necesar pentru crearea instanțelor goale (folosit de parsere JSON și de framework).

## Constructor Parcelable

```java
protected NewsItem(Parcel in) {
```
 Declară constructorul protected care primește un Parcel pentru deserializare. Recreează obiectul NewsItem din datele serializate. Acces protected deoarece este apelat doar de CREATOR.

```java
title = in.readString();
```
 Citește titlul din Parcel în ordinea în care a fost scris în writeToParcel. Prima citire corespunde primei scrieri.

```java
body = in.readString();
```
 Citește body-ul din Parcel în ordinea în care a fost scris. A doua citire corespunde celei de-a doua scrieri.

```java
thumbnailUrl = in.readString();
```
 Citește URL-ul thumbnail din Parcel în ordinea în care a fost scris. A treia citire corespunde celei de-a treia scrieri.

```java
language = in.readString();
```
 Citește codul limbii din Parcel în ordinea în care a fost scris. A patra citire corespunde celei de-a patra scrieri.

```java
source = in.readString();
```
 Citește sursa știrii din Parcel în ordinea în care a fost scris. A cincea citire corespunde celei de-a cincea scrieri.

```java
datetime = in.readString();
```
 Citește data/ora din Parcel în ordinea în care a fost scris. A șasea citire corespunde celei de-a șasea scrieri.

```java
concepts = in.createStringArrayList();
```
 Citește lista de String-uri (concepte) din Parcel. Metoda createStringArrayList() creează și populează automat ArrayList-ul.

## Metoda writeToParcel

```java
@Override
public void writeToParcel(Parcel dest, int flags) {
```
 Override-ul metodei din Parcelable care serializează obiectul NewsItem într-un Parcel. Parametrii: dest este Parcel-ul destinație, flags sunt opțiuni speciale.

```java
dest.writeString(title);
```
 Scrie titlul în Parcel ca primul element. Ordinea trebuie să corespundă exact cu ordinea din constructor.

```java
dest.writeString(body);
```
 Scrie body-ul în Parcel ca al doilea element. Ordinea trebuie să corespundă exact cu ordinea din constructor.

```java
dest.writeString(thumbnailUrl);
```
 Scrie URL-ul thumbnail în Parcel ca al treilea element. Ordinea trebuie să corespundă exact cu ordinea din constructor.

```java
dest.writeString(language);
```
 Scrie codul limbii în Parcel ca al patrulea element. Ordinea trebuie să corespundă exact cu ordinea din constructor.

```java
dest.writeString(source);
```
 Scrie sursa în Parcel ca al cincilea element. Ordinea trebuie să corespundă exact cu ordinea din constructor.

```java
dest.writeString(datetime);
```
 Scrie data/ora în Parcel ca al șaselea element. Ordinea trebuie să corespundă exact cu ordinea din constructor.

```java
dest.writeStringList(concepts);
```
 Scrie lista de concepte în Parcel ca ultimul element. Metoda writeStringList() serializează automat întreaga listă.

## Metoda describeContents

```java
@Override
public int describeContents() {
```
 Override-ul metodei din Parcelable care descrie tipurile speciale de obiecte conținute (file descriptors).

```java
return 0;
```
 Returnează 0 indicând că acest Parcelable nu conține obiecte speciale (file descriptors). Valoarea implicită pentru obiecte simple.

## CREATOR static

```java
public static final Creator<NewsItem> CREATOR = new Creator<NewsItem>() {
```
 Declară câmpul static final CREATOR necesar pentru Parcelable. Framework-ul Android folosește acest CREATOR pentru a deserializa obiectele NewsItem.

```java
@Override
public NewsItem createFromParcel(Parcel in) {
```
 Override-ul metodei care creează o instanță NewsItem din Parcel. Apelată de framework când se primește un NewsItem prin Intent.

```java
return new NewsItem(in);
```
 Creează și returnează un nou NewsItem folosind constructorul Parcelable care deserializează datele din Parcel.

```java
@Override
public NewsItem[] newArray(int size) {
```
 Override-ul metodei care creează un array de NewsItem cu dimensiunea specificată. Folosit de framework pentru deserializarea array-urilor.

```java
return new NewsItem[size];
```
 Creează și returnează un array gol de NewsItem cu dimensiunea cerută.

## Getter-i și Setter-i

### Getter și Setter pentru title

```java
public String getTitle() {
```
 Declară getter-ul public pentru a obține titlul știrii.

```java
return title;
```
 Returnează valoarea câmpului privat title.

```java
public void setTitle(String title) {
```
 Declară setter-ul public pentru a seta titlul știrii. Parametrul title este noua valoare.

```java
this.title = title;
```
 Atribuie parametrul title câmpului membru title. Keyword-ul this disambiguează între parametru și câmpul membru.

### Getter și Setter pentru body

```java
public String getBody() {
```
 Declară getter-ul public pentru a obține conținutul complet al știrii.

```java
return body;
```
 Returnează valoarea câmpului privat body.

```java
public void setBody(String body) {
```
 Declară setter-ul public pentru a seta conținutul știrii. Parametrul body este noua valoare.

```java
this.body = body;
```
 Atribuie parametrul body câmpului membru body. Keyword-ul this disambiguează între parametru și câmpul membru.

### Getter și Setter pentru thumbnailUrl

```java
public String getThumbnailUrl() {
```
 Declară getter-ul public pentru a obține URL-ul imaginii thumbnail.

```java
return thumbnailUrl;
```
 Returnează valoarea câmpului privat thumbnailUrl.

```java
public void setThumbnailUrl(String thumbnailUrl) {
```
 Declară setter-ul public pentru a seta URL-ul imaginii thumbnail. Parametrul thumbnailUrl este noua valoare.

```java
this.thumbnailUrl = thumbnailUrl;
```
 Atribuie parametrul thumbnailUrl câmpului membru thumbnailUrl. Keyword-ul this disambiguează între parametru și câmpul membru.

### Getter și Setter pentru concepts

```java
public List<String> getConcepts() {
```
 Declară getter-ul public pentru a obține lista de concepte/tag-uri asociate cu știrea.

```java
return concepts;
```
 Returnează referința către lista privată concepts. Modificările la lista returnată afectează obiectul original.

```java
public void setConcepts(List<String> concepts) {
```
 Declară setter-ul public pentru a seta lista de concepte. Parametrul concepts este noua listă.

```java
this.concepts = concepts;
```
 Atribuie parametrul concepts câmpului membru concepts. Keyword-ul this disambiguează între parametru și câmpul membru.

### Getter și Setter pentru language

```java
public String getLanguage() {
```
 Declară getter-ul public pentru a obține codul limbii știrii.

```java
return language;
```
 Returnează valoarea câmpului privat language.

```java
public void setLanguage(String language) {
```
 Declară setter-ul public pentru a seta codul limbii. Parametrul language este noua valoare.

```java
this.language = language;
```
 Atribuie parametrul language câmpului membru language. Keyword-ul this disambiguează între parametru și câmpul membru.

### Getter și Setter pentru source

```java
public String getSource() {
```
 Declară getter-ul public pentru a obține sursa/publicația știrii.

```java
return source;
```
 Returnează valoarea câmpului privat source.

```java
public void setSource(String source) {
```
 Declară setter-ul public pentru a seta sursa știrii. Parametrul source este noua valoare.

```java
this.source = source;
```
 Atribuie parametrul source câmpului membru source. Keyword-ul this disambiguează între parametru și câmpul membru.

### Getter și Setter pentru datetime

```java
public String getDateTime() {
```
 Declară getter-ul public pentru a obține data și ora publicării știrii. Numele metodei folosește CamelCase (DateTime).

```java
return datetime;
```
 Returnează valoarea câmpului privat datetime (lowercase).

```java
public void setDateTime(String datetime) {
```
 Declară setter-ul public pentru a seta data/ora publicării. Numele metodei folosește CamelCase (DateTime).

```java
this.datetime = datetime;
```
 Atribuie parametrul datetime câmpului membru datetime. Keyword-ul this disambiguează între parametru și câmpul membru.

## Implementarea Parcelable

### De ce Parcelable în loc de Serializable?

**Parcelable:**
- Mecanismul nativ Android pentru serializare
- Mult mai rapid decât Serializable (până la 10x)
- Mai eficient din punct de vedere al memoriei
- Optimizat pentru IPC (Inter-Process Communication)
- Necesită implementare manuală (writeToParcel, CREATOR)

**Ordinea critică a operațiunilor:**
1. În `writeToParcel()`: title → body → thumbnailUrl → language → source → datetime → concepts
2. În `constructor(Parcel)`: title → body → thumbnailUrl → language → source → datetime → concepts
3. **Ordinea trebuie să fie IDENTICĂ** altfel datele se citesc greșit

### Fluxul de serializare/deserializare

**Serializare (MainActivity → NewsDetailActivity):**
```java
// În NewsAdapter
intent.putExtra("news_item", newsItem);
// Framework apelează automat:
newsItem.writeToParcel(parcel, flags);
```

**Deserializare (în NewsDetailActivity):**
```java
// În NewsDetailActivity
NewsItem newsItem = getIntent().getParcelableExtra("news_item");
// Framework apelează automat:
NewsItem newsItem = CREATOR.createFromParcel(parcel);
// Care apelează:
new NewsItem(parcel);
```

## Structura datelor NewsItem

### Câmpuri String:
- `title`: Titlul știrii (de ex. "New AI breakthrough announced")
- `body`: Conținutul complet al știrii (text lung)
- `thumbnailUrl`: URL HTTP/HTTPS către imaginea asociată
- `language`: Cod limbă ISO (de ex. "eng", "ron")
- `source`: Numele publicației (de ex. "BBC News", "CNN")
- `datetime`: String cu data/ora în format ISO sau custom

### Câmp List:
- `concepts`: Lista de tag-uri/categorii (de ex. ["Technology", "AI", "Science"])

## Pattern-uri și convenții

### JavaBeans pattern:
- Câmpuri private
- Constructor public fără parametri
- Getter-i și setter-i publici
- Encapsulare completă

### Naming conventions:
- Câmpuri: camelCase (thumbnailUrl, datetime)
- Metode getter: get + PascalCase (getTitle, getDateTime)
- Metode setter: set + PascalCase (setTitle, setDateTime)
- Constante: UPPER_SNAKE_CASE (CREATOR)

### Compatibilitate cu JSON parsing:
- Constructor implicit permite crearea instanțelor goale
- Setter-ii permit popularea câmpurilor de către parser
- Numele câmpurilor trebuie să corespundă cu cheile JSON (sau folosirea adnotărilor)
