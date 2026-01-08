# DailyWeatherItem.java — Documentație detaliată a codului sursă

Acest document prezintă codul complet al clasei `DailyWeatherItem.java` împărțit în fragmente logice, fiecare precedat de explicații detaliate în limba română. Documentația este destinată programatorilor cu cunoștințe de Java dar cu experiență limitată în programarea Android.

## Context general

`DailyWeatherItem` este o clasă model (POJO - Plain Old Java Object) care reprezintă vremea pentru o zi întreagă. Această clasă:

1. **Agregare date** - Grupează multiple previziuni orare (WeatherItem) pentru o zi
2. **Parcelable** - Poate fi transmisă între Activity-uri prin Intent
3. **Container** - Conține listă de WeatherItem-uri (de obicei 8 previziuni × 3 ore)
4. **Metadata** - Păstrează informații despre oraș, țară, și dată
5. **Helper methods** - Metodă addHourlyData() pentru construire incrementală

Este un exemplu clasic de model de date cu implementare Parcelable pentru Android.

## 1. Declararea pachetului

Clasa aparține sub-pachetului `model` din aplicație.

```java
package ro.makore.akrilki_08.model;
```

**Explicație:**
- Sub-pachetul `model` grupează toate clasele de date (models, DTOs, entities)
- Organizare logică: separarea datelor de logica business și UI

## 2. Import-uri pentru Parcelable

```java
import android.os.Parcel;
import android.os.Parcelable;
```

**Linie cu linie:**
- **`Parcel`** - Container pentru serializarea datelor în Android
- **`Parcelable`** - Interfață pentru obiecte care pot fi scrise într-un Parcel
- **Scop**: Permite transmiterea obiectului între componente Android (Activity, Service, etc.)
- **Alternativa**: Serializable (mai lent, mai mult overhead)

## 3. Import-uri pentru colecții

```java
import java.util.List;
import java.util.ArrayList;
```

**Explicație:**
- **`List`** - Interfața pentru liste
- **`ArrayList`** - Implementare dinamică de listă
- Folosim pentru stocarea listei de WeatherItem-uri (date orare)

## 4. Declararea clasei și implementarea Parcelable

```java
public class DailyWeatherItem implements Parcelable {
```

**Explicație:**
- Clasă publică pentru acces din orice pachet
- `implements Parcelable` - Declară că obiectul poate fi parcelat
- **Obligații**: Trebuie să implementeze:
  1. `writeToParcel()` - Scrie datele în Parcel
  2. `describeContents()` - Descrie conținutul special (FileDescriptors, etc.)
  3. `CREATOR` - Factory pentru deserializare

## 5. Câmpurile clasei

```java
    private String cityName;
    private String country;
    private String date; // Date in format "yyyy-MM-dd"
    private List<WeatherItem> hourlyData; // List of 3-hourly data points for this day
```

**Linie cu linie:**
- **`cityName`** - Numele orașului (ex: "Bucharest")
- **`country`** - Codul țării (ex: "RO")
- **`date`** - Data în format ISO (ex: "2026-01-08")
  - Comentariul specifică formatul exact
  - Folosit ca și cheie pentru grupare în parser
- **`hourlyData`** - Lista de previziuni orare pentru această zi
  - De obicei 8 elemente (forecast la fiecare 3 ore)
  - Comentariul explică că sunt "3-hourly data points"
- Toate `private` - Accesate doar prin getters/setters (encapsulation)

## 6. Constructorul default (unparcelable)

```java
    // Unparcelable Constructor
    public DailyWeatherItem() {
        hourlyData = new ArrayList<>();
    }
```

**Linie cu linie:**
- Constructor fără parametri (default constructor)
- Comentariul "Unparcelable" = folosit pentru creare normală (nu din Parcel)
- Inițializăm `hourlyData` cu listă goală
- **Important**: Evită NullPointerException când adăugăm elemente
- **Use case**: `new DailyWeatherItem()` în parser când creăm obiecte noi

## 7. Constructorul Parcelable - Deserializare

```java
    // Parcelable constructor - same order as in writeToParcel method
    protected DailyWeatherItem(Parcel in) {
        cityName = in.readString();
        country = in.readString();
        date = in.readString();
        hourlyData = in.createTypedArrayList(WeatherItem.CREATOR);
    }
```

**Linie cu linie:**
- Constructor `protected` - Apelat doar de CREATOR
- Parametru `Parcel in` - Parcel-ul din care citim datele
- **CRITIC**: Ordinea de citire trebuie să fie IDENTICĂ cu ordinea de scriere
  - Comentariul avertizează: "same order as in writeToParcel method"
- `in.readString()` - Citește String-ul următor din Parcel
  - Pentru cityName, country, date (în această ordine)
- `in.createTypedArrayList(WeatherItem.CREATOR)` - Citește listă de Parcelable
  - Folosește CREATOR-ul din WeatherItem pentru deserializare
  - **Necesar**: WeatherItem trebuie și ea să fie Parcelable
- **Flow**: Parcel → readString × 3 → createTypedArrayList → Obiect reconstruit

## 8. Metoda writeToParcel() - Serializare

```java
    // writeToParcel must be in the same order as constructor
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cityName);
        dest.writeString(country);
        dest.writeString(date);
        dest.writeTypedList(hourlyData);
    }
```

**Linie cu linie:**
- `@Override` - Implementează metoda din interfața Parcelable
- **Parametri**:
  - `dest` - Parcel-ul destinație unde scriem
  - `flags` - Flag-uri speciale (de obicei 0 sau PARCELABLE_WRITE_RETURN_VALUE)
- Comentariul avertizează din nou: "must be in the same order as constructor"
- `dest.writeString()` - Scrie String în Parcel (pentru cityName, country, date)
- `dest.writeTypedList(hourlyData)` - Scrie listă de Parcelable
  - Apelează automat `writeToParcel()` pentru fiecare WeatherItem
  - **Necesar**: Toate elementele din listă trebuie să fie Parcelable
- **IMPORTANT**: Orice schimbare aici TREBUIE reflectată în constructor Parcelable

## 9. Metoda describeContents()

```java
    @Override
    public int describeContents() {
        return 0;  // No special objects inside
    }
```

**Linie cu linie:**
- `@Override` - Implementează metoda din Parcelable
- Returnează 0 în majoritatea cazurilor
- Comentariul explică: "No special objects inside"
- **Când returnăm altceva**: Dacă obiectul conține FileDescriptors
  - `CONTENTS_FILE_DESCRIPTOR` (valoare 1) pentru FileDescriptors
- **Use case**: Android folosește pentru optimizări la serializare

## 10. Field-ul static CREATOR - Factory pentru deserializare

### Declararea CREATOR-ului

```java
    // Parcelable CREATOR to help with deserialization
    public static final Creator<DailyWeatherItem> CREATOR = new Creator<DailyWeatherItem>() {
```

**Linie cu linie:**
- `public static final` - Constantă publică statică
- **Nume fix**: TREBUIE să se numească exact "CREATOR" (Android framework caută acest nume)
- Tipul: `Creator<DailyWeatherItem>` - Generic pentru tipul nostru
- Instanțiere anonimă a interfeței Creator
- **Scop**: Android framework folosește acest CREATOR pentru a crea obiecte din Parcel

### Metoda createFromParcel()

```java
        @Override
        public DailyWeatherItem createFromParcel(Parcel in) {
            return new DailyWeatherItem(in);
        }
```

**Linie cu linie:**
- Apelată de framework pentru a crea un obiect din Parcel
- Simplu: Apelează constructorul Parcelable
- **Flow**: Intent → Parcel → CREATOR.createFromParcel() → Constructor(Parcel) → Obiect

### Metoda newArray()

```java
        @Override
        public DailyWeatherItem[] newArray(int size) {
            return new DailyWeatherItem[size];
        }
    };
```

**Linie cu linie:**
- Apelată pentru a crea array de obiecte
- **Parametru**: `size` - Dimensiunea array-ului dorit
- Returnează array gol de DailyWeatherItem
- **Use case**: Când Intent conține array/listă de DailyWeatherItem

## 11. Getters și Setters - Encapsulation

### Getter și Setter pentru cityName

```java
    // Getters and Setters
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
```

**Explicație:**
- Pattern standard JavaBean
- `getCityName()` - Returnează numele orașului
- `setCityName(String)` - Setează numele orașului
- `this.cityName` - Referință la câmpul clasei (pentru a evita ambiguitatea cu parametrul)

### Getter și Setter pentru country

```java
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
```

**Explicație:**
- Identic cu cityName
- Returnează/setează codul țării

### Getter și Setter pentru date

```java
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
```

**Explicație:**
- Returnează/setează data în format "yyyy-MM-dd"
- **Note**: Nu validează formatul (ar putea fi îmbunătățit cu validare)

### Getter și Setter pentru hourlyData

```java
    public List<WeatherItem> getHourlyData() {
        return hourlyData;
    }

    public void setHourlyData(List<WeatherItem> hourlyData) {
        this.hourlyData = hourlyData;
    }
```

**Explicație:**
- Returnează/setează întreaga listă de previziuni orare
- **Atenție**: Returnează referința directă (nu copie)
  - Modificări la lista returnată afectează obiectul
  - **Best practice**: Ar trebui returnată copie defensivă (`new ArrayList<>(hourlyData)`)

## 12. Metoda addHourlyData() - Helper pentru construire

```java
    public void addHourlyData(WeatherItem item) {
        if (hourlyData == null) {
            hourlyData = new ArrayList<>();
        }
        hourlyData.add(item);
    }
}
```

**Linie cu linie:**
- Metodă helper pentru adăugare incrementală de WeatherItem-uri
- **Defensive programming**: Verifică dacă lista e null
  - Dacă e null, creează listă nouă (evită NullPointerException)
  - **Scenariul**: Obiect creat cu constructor Parcelable fără hourlyData
- `hourlyData.add(item)` - Adaugă item-ul la listă
- **Use case în parser**:
```java
DailyWeatherItem dailyItem = new DailyWeatherItem();
for (WeatherItem hourlyItem : hourlyItems) {
    dailyItem.addHourlyData(hourlyItem); // Mai ușor decât set + get
}
```

---

## Rezumat

Această clasă este un model de date pentru vremea zilnică cu suport Parcelable:

### **Scop principal**
- Reprezintă vremea pentru o zi întreagă
- Grupează multiple previziuni orare (WeatherItem)
- Poate fi transmisă între Activity-uri prin Intent

### **Structura datelor:**

```
DailyWeatherItem
├── cityName: String         // "Bucharest"
├── country: String          // "RO"
├── date: String             // "2026-01-08"
└── hourlyData: List<WeatherItem>  // [8 WeatherItem objects]
    ├── WeatherItem (00:00)
    ├── WeatherItem (03:00)
    ├── WeatherItem (06:00)
    ├── WeatherItem (09:00)
    ├── WeatherItem (12:00)
    ├── WeatherItem (15:00)
    ├── WeatherItem (18:00)
    └── WeatherItem (21:00)
```

### **Implementare Parcelable - Componentele necesare:**

**1. Interfața:**
```java
implements Parcelable
```

**2. Constructor de deserializare:**
```java
protected DailyWeatherItem(Parcel in) {
    // Citire în ordinea writeToParcel
}
```

**3. Metoda writeToParcel():**
```java
public void writeToParcel(Parcel dest, int flags) {
    // Scriere în aceeași ordine ca în constructor
}
```

**4. Metoda describeContents():**
```java
public int describeContents() {
    return 0; // Sau CONTENTS_FILE_DESCRIPTOR
}
```

**5. Field-ul CREATOR:**
```java
public static final Creator<DailyWeatherItem> CREATOR = new Creator<>() {
    public DailyWeatherItem createFromParcel(Parcel in) { ... }
    public DailyWeatherItem[] newArray(int size) { ... }
};
```

### **Ordinea de scriere/citire CRITICĂ:**

| Ordine | writeToParcel() | Constructor(Parcel) |
|--------|-----------------|---------------------|
| 1 | writeString(cityName) | cityName = readString() |
| 2 | writeString(country) | country = readString() |
| 3 | writeString(date) | date = readString() |
| 4 | writeTypedList(hourlyData) | hourlyData = createTypedArrayList() |

**IMPORTANT**: Dacă ordinea nu e identică → Date corupte sau crash!

### **Flow-ul Parcelable:**

**Scriere (Activity A → Intent):**
```
DailyWeatherItem object
    ↓
writeToParcel() apelat automat
    ↓
cityName → Parcel
country → Parcel
date → Parcel
hourlyData → Parcel (pentru fiecare WeatherItem)
    ↓
Parcel în Intent
```

**Citire (Intent → Activity B):**
```
Intent cu Parcel
    ↓
CREATOR.createFromParcel() apelat automat
    ↓
Constructor(Parcel in)
    ↓
Citire cityName din Parcel
Citire country din Parcel
Citire date din Parcel
Citire hourlyData din Parcel (folosind WeatherItem.CREATOR)
    ↓
DailyWeatherItem object reconstruit
```

### **Utilizare în aplicație:**

**1. În WeatherParser (creare):**
```java
DailyWeatherItem dailyItem = new DailyWeatherItem();
dailyItem.setCityName("Bucharest");
dailyItem.setCountry("RO");
dailyItem.setDate("2026-01-08");

for (WeatherItem hourlyItem : hourlyItems) {
    dailyItem.addHourlyData(hourlyItem);
}
```

**2. În DailyWeatherAdapter (transmitere):**
```java
holder.itemView.setOnClickListener(v -> {
    Intent intent = new Intent(context, WeatherDetailActivity.class);
    if (!hourlyData.isEmpty()) {
        intent.putExtra("weather_item", hourlyData.get(0)); // Primul WeatherItem
    }
    context.startActivity(intent);
});
```

**3. În WeatherDetailActivity (primire):**
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    WeatherItem item = getIntent().getParcelableExtra("weather_item");
    // Folosește item-ul pentru a popula UI
}
```

### **Avantajele Parcelable vs Serializable:**

| Aspect | Parcelable | Serializable |
|--------|------------|--------------|
| **Performance** | ✅ Foarte rapid | ❌ Lent (reflection) |
| **Overhead** | ✅ Minimal | ❌ Mare (metadata) |
| **Cod** | ❌ Boilerplate | ✅ Simplu (@Serializable) |
| **Android** | ✅ Optimizat | ⚠️ Funcționează dar mai lent |
| **Debugging** | ⚠️ Mai dificil | ✅ Mai ușor |

### **Best Practices implementate:**

✅ **Comentarii clare** pentru ordinea Parcelable
✅ **Constructor default** pentru creare normală
✅ **Null safety** în addHourlyData()
✅ **Encapsulation** cu private fields + getters/setters
✅ **Helper method** addHourlyData() pentru usability

### **Îmbunătățiri posibile:**

❌ **Validare date** - Nu validează formatul "yyyy-MM-dd"
❌ **Defensive copy** - getHourlyData() returnează referință directă
❌ **Immutability** - Obiectul poate fi modificat după creare
❌ **Builder pattern** - Constructor cu mulți parametri ar beneficia de Builder

**Exemplu Builder pattern:**
```java
DailyWeatherItem item = new DailyWeatherItem.Builder()
    .setCityName("Bucharest")
    .setCountry("RO")
    .setDate("2026-01-08")
    .addHourlyData(item1)
    .addHourlyData(item2)
    .build();
```

### **Relația cu WeatherItem:**

- **Agregare**: DailyWeatherItem CONȚINE listă de WeatherItem
- **One-to-Many**: 1 DailyWeatherItem → 8 WeatherItem-uri (de obicei)
- **Dependency**: Ambele trebuie Parcelable pentru transmitere în Intent
- **Hierarchy**: Nu e moștenire, e compoziție (has-a, nu is-a)

### **Memory considerations:**

- Obiect mic: ~100 bytes (fără hourlyData)
- Cu 8 WeatherItem-uri: ~1-2 KB total
- OK pentru Intent (limita e ~1 MB)
- Pentru liste mari (50+ zile), considerați:
  - Transmitere ID-uri în loc de obiecte
  - Database (Room) pentru persistență
  - ViewModel pentru partajare între fragments

Această clasă demonstrează implementarea corectă a pattern-ului Parcelable în Android, esențială pentru comunicarea între componente!
