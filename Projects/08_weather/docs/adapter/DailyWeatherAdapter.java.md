# DailyWeatherAdapter.java — Documentație detaliată a codului sursă

Acest document prezintă codul complet al clasei `DailyWeatherAdapter.java` împărțit în fragmente logice, fiecare precedat de explicații detaliate în limba română. Documentația este destinată programatorilor cu cunoștințe de Java dar cu experiență limitată în programarea Android.

## Context general

`DailyWeatherAdapter` este un adapter pentru RecyclerView care afișează previziunile meteo grupate pe zile. Această clasă este responsabilă pentru:

1. **Binding date → UI** - Transformă lista de `DailyWeatherItem` în elemente vizuale
2. **Grafice interactive** - Desenează grafice de temperatură cu MPAndroidChart
3. **Overlay iconiți meteo** - Pozițîonează iconiți de vreme deasupra graficului
4. **Download imagini** - Folosește OkHttp pentru a descărca iconiți în background
5. **Navigare detalii** - Click pe un item deschide WeatherDetailActivity
6. **Formatare date** - Transformă "2026-01-08" în "Wednesday, January 8, 2026"

Este un exemplu avansat de adapter RecyclerView cu manipulare complexă de grafice și imagini.

## 1. Declararea pachetului

Clasa aparține sub-pachetului `adapter` din aplicație.

```java
package ro.makore.akrilki_08.adapter;
```

**Explicație:**
- Sub-pachetul `adapter` grupează toate adaptoarele pentru RecyclerView
- Separare logică: adapters gestionează binding-ul date → UI

## 2. Import-uri pentru componente Android de bază

```java
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import ro.makore.akrilki_08.R;
import android.util.Log;
```

**Linie cu linie:**
- **`Context`** - Pentru acces la resurse și pornirea Activity-urilor
- **`Intent`** - Pentru navigare către WeatherDetailActivity
- **`Color`** - Pentru manipularea culorilor (parseColor, TRANSPARENT)
- **`LayoutInflater`** - Pentru inflating (transformarea XML → View objects)
- **`View`** - Clasa de bază pentru toate componentele UI
- **`ViewGroup`** - Container pentru alte View-uri
- **`ImageView`** - Pentru afișarea iconiților meteo
- **`LinearLayout`** - Layout liniar (nu folosit efectiv în cod)
- **`TextView`** - Pentru afișarea textului (oraș, dată)
- **`@NonNull`** - Adnotare pentru parametri non-null
- **`R`** - Clasa cu ID-uri de resurse
- **`Log`** - Pentru logging/debugging

## 3. Import-uri pentru RecyclerView

```java
import androidx.recyclerview.widget.RecyclerView;
import ro.makore.akrilki_08.model.DailyWeatherItem;
import ro.makore.akrilki_08.model.WeatherItem;
import ro.makore.akrilki_08.WeatherDetailActivity;
```

**Linie cu linie:**
- **`RecyclerView`** - Componentă pentru liste scrollabile eficiente
- **`DailyWeatherItem`** - Model pentru vremea unei zile (conține listă de WeatherItem)
- **`WeatherItem`** - Model pentru vremea la o oră specifică
- **`WeatherDetailActivity`** - Activity-ul pentru detalii

## 4. Import-uri pentru Glide (comentat, nu folosit)

```java
import com.bumptech.glide.Glide;
```

**Explicație:**
- Glide e importat dar nu folosit în versiunea curentă
- Se folosește OkHttp direct pentru download imagini (vezi mai jos)
- Probabil a fost înlocuit pentru control mai fin

## 5. Import-uri pentru MPAndroidChart - Biblioteca de grafice

```java
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
```

**Linie cu linie:**
- **`LineChart`** - Componenta chart pentru grafice liniare
- **`XAxis`** - Axa orizontală (dezactivată în cod)
- **`YAxis`** - Axa verticală (dezactivată în cod)
- **`Entry`** - Punct de date (x, y) pentru grafic
- **`LineData`** - Container pentru toate datele graficului
- **`LineDataSet`** - Set de date (seria de temperaturi)
- **`ILineDataSet`** - Interfața pentru set de date

## 6. Import-uri duplicate pentru ViewGroup și FrameLayout

```java
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.view.View;
```

**Explicație:**
- Duplicate (deja importate mai sus)
- **`FrameLayout`** - Layout care permite suprapunerea View-urilor (folosit pentru overlay iconiți)

## 7. Import-uri pentru colecții

```java
import java.util.ArrayList;
import java.util.List;
```

**Explicație:**
- **`ArrayList`** - Listă dinamică
- **`List`** - Interfața pentru liste

## 8. Declararea clasei și câmpurilor membre

### Declararea clasei

```java
public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherAdapter.DailyWeatherViewHolder> {
```

**Explicație:**
- Extinde `RecyclerView.Adapter` - Clasa de bază pentru adaptoare
- **Parametru generic**: `<DailyWeatherViewHolder>` - Tipul de ViewHolder folosit
- Pattern: Adapter pentru RecyclerView (design pattern standard Android)

### Câmpuri membre

```java
    private final Context context;
    private final List<DailyWeatherItem> dailyWeatherItemList;
```

**Linie cu linie:**
- **`context`** - Context pentru acces la resurse, pornire Activity-uri
- **`dailyWeatherItemList`** - Lista de date care va fi afișată
- `final` - Referințele nu se schimbă (dar conținutul listei da)

## 9. Constructorul

```java
    public DailyWeatherAdapter(Context context, List<DailyWeatherItem> dailyWeatherItemList) {
        this.context = context;
        this.dailyWeatherItemList = dailyWeatherItemList;
    }
```

**Explicație:**
- Primește și salvează context-ul și lista de date
- Lista trebuie să fie aceeași referință pentru `updateData()` să funcționeze

## 10. Metoda updateData() - Actualizarea datelor

```java
    public void updateData(List<DailyWeatherItem> dailyWeatherItemList) {
        Log.v("WEATHER08", "Updating daily weather data");
        this.dailyWeatherItemList.clear();
        this.dailyWeatherItemList.addAll(dailyWeatherItemList);
        notifyDataSetChanged(); // Refresh the RecyclerView
    }
```

**Linie cu linie:**
- Metodă apelată când avem date noi (după un refresh)
- `clear()` - Golește lista existentă
- `addAll()` - Adaugă toate elementele noi
- **Important**: Modificăm lista existentă, nu înlocuim referința
- `notifyDataSetChanged()` - Spune RecyclerView-ului să se redeseneze
- **Pattern**: Actualizare in-place pentru a păstra referința

## 11. Metoda onCreateViewHolder() - Crearea ViewHolder-ului

```java
    @NonNull
    @Override
    public DailyWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_daily_weather, parent, false);
        return new DailyWeatherViewHolder(view);
    }
```

**Linie cu linie:**
- `@Override` - Suprascriem metoda din RecyclerView.Adapter
- **Parametri**: `parent` = RecyclerView, `viewType` = tip item (nu folosim)
- `LayoutInflater.from(context)` - Obținem un inflater
- `inflate(R.layout.item_daily_weather, parent, false)` - Transformă XML în View
  - **Layout**: `item_daily_weather.xml` - Layout-ul unui item zilnic
  - **parent**: RecyclerView-ul (pentru parametri de layout corecți)
  - **false**: Nu atașa View-ul la parent încă (RecyclerView o face singur)
- Returnăm un ViewHolder nou cu view-ul inflated
- **Apelat**: O dată pentru fiecare tip de item necesar pe ecran + câteva extra pentru recycling

## 12. Metoda onBindViewHolder() - Popularea UI-ului cu date

### Semnătura metodei și extragerea datelor

```java
    @Override
    public void onBindViewHolder(@NonNull DailyWeatherViewHolder holder, int position) {
        DailyWeatherItem dailyItem = dailyWeatherItemList.get(position);
        List<WeatherItem> hourlyData = dailyItem.getHourlyData();

        if (hourlyData == null || hourlyData.isEmpty()) {
            return;
        }
```

**Linie cu linie:**
- `@Override` - Suprascriem metoda din Adapter
- **Parametri**: `holder` = ViewHolder-ul de populat, `position` = index în listă
- Extragem item-ul zilnic de la poziția curentă
- Extragem lista de date orare (de obicei 8 elemente = 8 previziuni pe 3 ore)
- **Guard clause**: Dacă nu avem date, ieșim

### Afișarea numelui orașului (doar pe primul item)

```java
        // Set city name and country (only on first item)
        if (position == 0) {
            String cityCountry = dailyItem.getCityName();
            if (dailyItem.getCountry() != null && !dailyItem.getCountry().isEmpty()) {
                cityCountry += ", " + dailyItem.getCountry();
            }
            holder.cityTextView.setText(cityCountry);
            holder.cityTextView.setVisibility(View.VISIBLE);
        } else {
            holder.cityTextView.setVisibility(View.GONE);
        }
```

**Linie cu linie:**
- **UX decision**: Afișăm orașul doar pe primul item (evităm repetiția)
- Construim string: "Bucharest, RO" sau doar "Bucharest" dacă țara lipsește
- `setVisibility(View.VISIBLE)` - Facem TextView-ul vizibil
- Pentru toate celelalte pozițîi (1, 2, 3, 4), ascundem orașul
- `View.GONE` - Invizibil și nu ocupă spațiu în layout

### Formatarea și setarea datei

```java
        // Format and set date
        String formattedDate = formatDate(dailyItem.getDate());
        holder.dateTextView.setText(formattedDate);
```

**Explicație:**
- Apelăm metoda helper `formatDate()` pentru formatare
- **Transformare**: "2026-01-08" → "Wednesday, January 8, 2026"
- Setăm data formatată în TextView

### Configurarea graficului

```java
        // Setup line chart
        setupLineChart(holder.lineChart, hourlyData);
```

**Explicație:**
- Apelăm metoda helper pentru configurarea complexă a graficului
- Transmitem referința la LineChart și datele orare

### Click listener pentru navigare

```java
        // Handle click event to pass the first WeatherItem to WeatherDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WeatherDetailActivity.class);
            if (!hourlyData.isEmpty()) {
                intent.putExtra("weather_item", hourlyData.get(0)); // Pass the first item
            }
            context.startActivity(intent);
        });
    }
```

**Linie cu linie:**
- `holder.itemView` - Întregul item (View-ul rădăcină)
- Lambda pentru click listener
- Creăm Intent pentru WeatherDetailActivity
- **Pattern**: Transmitem primul WeatherItem din zi (ora 00:00 sau prima disponibilă)
- `putExtra("weather_item", ...)` - WeatherItem trebuie să fie Parcelable
- `startActivity()` - Pornim Activity-ul de detalii

## 13. Metoda setupLineChart() - Configurarea graficului de temperaturi

### Pregătirea datelor pentru grafic

```java
    private void setupLineChart(LineChart lineChart, List<WeatherItem> hourlyData) {
        // Prepare data for chart
        List<Entry> entries = new ArrayList<>();
        List<String> iconUrls = new ArrayList<>();

        for (int i = 0; i < hourlyData.size(); i++) {
            WeatherItem item = hourlyData.get(i);
            entries.add(new Entry(i, (float) item.getTemperature()));
            
            // Store icon URL for each entry
            if (item.getIconUrl() != null && !item.getIconUrl().isEmpty()) {
                iconUrls.add(item.getIconUrl());
            } else {
                iconUrls.add("");
            }
        }
```

**Linie cu linie:**
- Creăm două liste: `entries` pentru punctele graficului, `iconUrls` pentru iconiți
- Parcurgem toate datele orare (de obicei 8)
- `new Entry(i, (float) item.getTemperature())` - Creăm punct grafic
  - **x**: index (0, 1, 2, ..., 7)
  - **y**: temperatura (ex: 5.2°C)
- Salvăm URL-ul iconiței pentru fiecare punct
- **Sincronizare**: entries și iconUrls au același index

### Crearea și configurarea DataSet-ului

```java
        // Create dataset with fill under the line
        LineDataSet dataSet = new LineDataSet(entries, "Temperature");
        dataSet.setColor(Color.parseColor("#2196F3"));
        dataSet.setLineWidth(3f);
        dataSet.setDrawCircles(false); // Don't draw circles
        dataSet.setDrawValues(true); // Show temperature values on nodes
        dataSet.setValueTextColor(Color.parseColor("#000000"));
        dataSet.setValueTextSize(12f);
        dataSet.setValueTypeface(null); // Use default typeface
```

**Linie cu linie:**
- `LineDataSet` - Set de date pentru graficul liniar
- `setColor(#2196F3)` - Culoarea liniei (albastru Material Design)
- `setLineWidth(3f)` - Grosimea liniei în pixeli
- `setDrawCircles(false)` - Nu desenează cercuri la noduri
- `setDrawValues(true)` - AFIȘEAZĂ valorile temperaturilor pe grafic
- Text negru (#000000), dimensiune 12sp
- `setValueTypeface(null)` - Font default

### Formatarea valorilor afișate

```java
        // Remove any text stroke/background
        dataSet.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f°", value);
            }
        });
```

**Linie cu linie:**
- `setValueFormatter()` - Personalizează cum sunt afișate valorile
- Clasă anonimă care implementează ValueFormatter
- `String.format("%.0f°", value)` - Formatează temperatura
  - **%.0f**: Număr fără zecimale
  - **°**: Simbolul de grade
  - **Exemplu**: 23.5 → "24°"

### Configurarea fill (umplere sub linie)

```java
        dataSet.setDrawFilled(true); // Fill area under the line
        dataSet.setFillColor(Color.parseColor("#2196F3"));
        dataSet.setFillAlpha(128); // Semi-transparent fill (0-255, 128 = 50% opacity)
```

**Linie cu linie:**
- `setDrawFilled(true)` - Umple zona sub linie
- Aceeași culoare ca linia (#2196F3 - albastru)
- `setFillAlpha(128)` - Transparență 50% (0=transparent complet, 255=opac complet)
- **Efect vizual**: Zona sub grafic e umplută cu albastru semi-transparent

### Crearea și setarea datelor graficului

```java
        // Create line data
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);
```

**Linie cu linie:**
- Creăm listă de dataset-uri (putem avea multiple linii pe același grafic)
- Adăugăm dataset-ul nostru
- `LineData` - Wrapper pentru toate datele graficului
- `setData()` - Setăm datele pe grafic

### Dezactivarea axelor

```java
        // Disable all axes
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(false); // Completely disable X axis

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setEnabled(false); // Completely disable left Y axis

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false); // Completely disable right Y axis
```

**Linie cu linie:**
- Obținem referințe la cele 3 axe (X, Y stânga, Y dreapta)
- `setEnabled(false)` pe toate - Ascunde complet axele
- **Design decision**: Grafic minimal, clean, fără axe și etichete

### Configurarea generală a graficului

```java
        // Configure chart
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setTouchEnabled(false); // Disable touch interactions
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChart.setExtraOffsets(20f, 20f, 20f, 60f); // Add bottom padding for icons below chart
```

**Linie cu linie:**
- `setDescription().setEnabled(false)` - Fără descriere text
- `setLegend().setEnabled(false)` - Fără legendă
- `setTouchEnabled(false)` - Nu poate fi atins (nu zoom, drag)
- Toate interacțiunile dezactivate (drag, scale, pinch zoom)
- `setDrawGridBackground(false)` - Fără background grid
- `setBackgroundColor(Color.TRANSPARENT)` - Transparent
- `setExtraOffsets(20, 20, 20, 60)` - Padding: stânga, sus, dreapta, jos
  - **60px jos**: Spațiu pentru iconiții care vor fi overlay-uiți sub grafic

### Refresh și overlay iconiți cu delay

```java
        // Refresh chart first to get dimensions
        lineChart.invalidate();
        
        // After chart is drawn, overlay icons at data points
        // Use a small delay to ensure chart is fully rendered
        lineChart.postDelayed(() -> {
            overlayIconsOnChart(lineChart, entries, iconUrls);
        }, 100);
    }
```

**Linie cu linie:**
- `invalidate()` - Forțează redesenarea graficului
- `postDelayed()` - Programează executarea după 100ms
- **Motivație**: Graficul trebuie să fie desenat complet înainte să calculăm pozițiile iconiților
- Lambda cu apelul `overlayIconsOnChart()`
- **Pattern**: Async UI update cu delay pentru a aștepta rendering

## 14. Metoda overlayIconsOnChart() - Suprapunerea iconiților pe grafic

### Verificarea părintelui (FrameLayout)

```java
    private void overlayIconsOnChart(LineChart lineChart, List<Entry> entries, List<String> iconUrls) {
        // Get the parent view (should be a FrameLayout)
        ViewGroup parent = (ViewGroup) lineChart.getParent();
        if (parent == null || !(parent instanceof FrameLayout)) {
            Log.e("WEATHER08", "Parent is not a FrameLayout: " + (parent != null ? parent.getClass().getName() : "null"));
            return;
        }

        FrameLayout frameLayout = (FrameLayout) parent;
```

**Linie cu linie:**
- Obținem părintele graficului (ar trebui să fie FrameLayout din XML)
- **Verificare**: Dacă nu e FrameLayout, nu putem face overlay (FrameLayout permite suprapunere)
- Cast la FrameLayout pentru acces la metodele specifice
- **Layout requirement**: item_daily_weather.xml trebuie să aibă FrameLayout ca rădăcină

### Ștergerea iconiților existenți

```java
        // Remove existing icon views
        for (int i = frameLayout.getChildCount() - 1; i >= 0; i--) {
            View child = frameLayout.getChildAt(i);
            if (child.getTag() != null && child.getTag().equals("weather_icon")) {
                frameLayout.removeView(child);
            }
        }
```

**Linie cu linie:**
- Parcurgem toți copiii FrameLayout-ului **invers** (pentru ștergere sigură)
- Verificăm tag-ul fiecărui copil
- Dacă e "weather_icon", îl ștergem
- **Motivație**: La re-bind (scroll RecyclerView), nu vrem să adăugăm iconiți duplicați
- **Pattern**: Cleanup înainte de populare

### Obținerea DataSet-ului și Transformer-ului

```java
        // Get chart's data set
        com.github.mikephil.charting.interfaces.datasets.ILineDataSet dataSet = lineChart.getData().getDataSetByIndex(0);
        if (dataSet == null) {
            Log.e("WEATHER08", "DataSet is null");
            return;
        }

        // Get transformer to convert values to pixel positions
        com.github.mikephil.charting.utils.Transformer transformer = lineChart.getTransformer(dataSet.getAxisDependency());
```

**Linie cu linie:**
- Obținem primul (și singur) dataset din grafic
- Guard clause dacă e null
- **`Transformer`** - Obiect MPAndroidChart care convertește coordonate valori → pixeli
  - **Valori**: (index, temperatură) ex: (3, 15.5°C)
  - **Pixeli**: (x, y) pe ecran ex: (450px, 120px)
- `getAxisDependency()` - Pe care axă depinde (LEFT sau RIGHT)

### Calcularea dimensiunilor graficului

```java
        // Get chart's content rect (the actual drawing area)
        android.graphics.RectF contentRect = lineChart.getViewPortHandler().getContentRect();
        float contentBottom = contentRect.bottom;
        float contentHeight = lineChart.getHeight();
        float contentTop = contentRect.top;
        float contentLeft = contentRect.left;
        float contentWidth = contentRect.width();
```

**Linie cu linie:**
- `getContentRect()` - Zona efectivă de desenare (exclude padding-uri)
- Extragem coordonatele și dimensiunile
- **Folosite**: Pentru a poziționa iconiții corect relativ la grafic

### Calcularea dimensiunii iconiților

```java
        int iconSize = (int) (context.getResources().getDisplayMetrics().density * 40); // 40dp
        int iconSpacing = (int) (context.getResources().getDisplayMetrics().density * 8); // 8dp spacing
```

**Linie cu linie:**
- **DP → PX conversion**: `density * dp = px`
- `getDisplayMetrics().density` - Factor de scalare (1.0 pentru mdpi, 2.0 pentru xhdpi, etc.)
- **40dp**: Dimensiune iconița (independent de rezoluția ecranului)
- **8dp**: Spațiu între iconița și grafic
- **Exemplu**: Pe xhdpi (density=2.0): 40dp × 2.0 = 80px

### Logging pentru debugging

```java
        Log.d("WEATHER08", "Overlaying " + entries.size() + " icons below chart");
        Log.d("WEATHER08", "Content rect: " + contentRect.toString());
        Log.d("WEATHER08", "Chart height: " + contentHeight);
        Log.d("WEATHER08", "FrameLayout height: " + frameLayout.getHeight());
        Log.d("WEATHER08", "FrameLayout width: " + frameLayout.getWidth());
```

**Explicație:**
- Logging extensiv pentru debugging pozițiilor
- Afișează dimensiuni și coordonate în logcat

### Loop-ul principal: Calcularea pozițiilor iconiților

```java
        // Calculate positions for each icon - align horizontally below chart
        for (int i = 0; i < entries.size() && i < iconUrls.size(); i++) {
            Entry entry = entries.get(i);
            String iconUrl = iconUrls.get(i);
            if (iconUrl == null || iconUrl.isEmpty()) {
                Log.w("WEATHER08", "Icon URL is empty for entry " + i);
                continue;
            }
```

**Linie cu linie:**
- Parcurgem toate entry-urile (previziunile orare)
- Guard clause pentru URL-uri lipsă
- `continue` - Sărim peste entry-urile fără iconița

### Transformarea coordonatelor valori → pixeli

```java
            // Transform entry X coordinate to pixel position
            // We only need X position to align with the node
            float[] point = new float[] { entry.getX(), entry.getY() };
            transformer.pointValuesToPixel(point);
            
            // X position aligns with the data node
            float xPos = point[0];
```

**Linie cu linie:**
- Creăm array cu coordonatele valorii: [index, temperatură]
- `pointValuesToPixel(point)` - Transformă IN-PLACE în coordonate pixel
- **După transformare**: point[0] = x în pixeli, point[1] = y în pixeli
- Extragem doar xPos (poziția orizontală)
- **Exemplu**: Entry(3, 15.5) → point[450.5, 120.3]

### Calcularea poziției verticale (fixe sub grafic)

```java
            // Y position is fixed - below the chart on a horizontal line
            // Position icons at a fixed Y position below the chart content area
            // Use chart height minus some padding for icons
            float yPos = lineChart.getHeight() - iconSize - iconSpacing;
            
            // Ensure icons are within FrameLayout bounds
            if (yPos + iconSize > frameLayout.getHeight()) {
                yPos = frameLayout.getHeight() - iconSize - iconSpacing;
            }
            if (yPos < 0) {
                yPos = lineChart.getHeight() + iconSpacing;
            }
```

**Linie cu linie:**
- **Design**: Iconiții sunt pe o linie orizontală fixă sub grafic
- Calculăm yPos: înălțimea graficului - dimensiune iconița - spacing
- **Validări**: Asigurăm că iconiții sunt în limitele FrameLayout-ului
- Dacă iconița ar depăși FrameLayout-ul, ajustăm poziția
- **Safety checks** pentru diverse dimensiuni de ecran

### Validarea poziției orizontale

```java
            // Ensure X position is within bounds
            if (xPos < iconSize / 2) {
                xPos = iconSize / 2;
            } else if (xPos > frameLayout.getWidth() - iconSize / 2) {
                xPos = frameLayout.getWidth() - iconSize / 2;
            }
```

**Explicație:**
- Asigurăm că iconița nu iese din ecran orizontal
- Minimum: jumătate din dimensiunea iconiței (pentru centrare)
- Maximum: lățime FrameLayout - jumătate iconița

### Logging poziții calculate

```java
            Log.d("WEATHER08", "Icon position: xPos=" + xPos + ", yPos=" + yPos + ", frameLayout size=" + frameLayout.getWidth() + "x" + frameLayout.getHeight());

            Log.d("WEATHER08", "Entry " + i + ": temp=" + entry.getY() + ", xPos=" + xPos + ", yPos=" + yPos + ", iconUrl=" + iconUrl);
```

**Explicație:**
- Debugging: Afișează poziția calculată pentru fiecare iconița
- Util pentru debugging layout issues

### Crearea și configurarea ImageView-ului

```java
            // Create and position icon
            ImageView iconView = new ImageView(context);
            iconView.setTag("weather_icon");
            
            // Position icon centered horizontally on the data node, below the chart
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(iconSize, iconSize);
            params.leftMargin = (int) (xPos - iconSize / 2);
            params.topMargin = (int) yPos;
            iconView.setLayoutParams(params);
```

**Linie cu linie:**
- Creăm ImageView programatic
- Setăm tag "weather_icon" (pentru ștergere ulterioară)
- Creăm LayoutParams cu dimensiuni (iconSize × iconSize)
- **leftMargin**: Centrat pe xPos (xPos - jumătate din lățime)
- **topMargin**: yPos calculat mai devreme
- Setăm parametrii pe iconView

### Configurarea proprietăților ImageView

```java
            iconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iconView.setBackgroundColor(Color.TRANSPARENT); // Remove debug background
            iconView.setElevation(10f); // Make sure icons are on top
            iconView.setVisibility(View.VISIBLE); // Explicitly set visibility
            iconView.setAdjustViewBounds(true); // Allow image to adjust bounds
```

**Linie cu linie:**
- `FIT_CENTER` - Scalează imaginea să fie complet vizibilă, păstrând aspect ratio
- Background transparent (comentariul sugerează că a fost folosit pentru debug)
- `setElevation(10f)` - Z-index ridicat (iconița peste grafic)
- Vizibilitate explicită
- `setAdjustViewBounds(true)` - Permite ajustarea limitelor

### Adăugarea la FrameLayout ÎNAINTE de încărcarea imaginii

```java
            // Add icon view to FrameLayout FIRST, then load image
            // This ensures the ImageView is properly attached before Glide tries to load
            frameLayout.addView(iconView);
```

**Explicație:**
- **IMPORTANT**: Adăugăm View-ul la layout ÎNAINTE de încărcare
- Comentariul menționează Glide, dar codul folosește OkHttp
- **Pattern**: Attach view → load content (evită probleme de lifecycle)

### Logging înainte de download

```java
            // Load icon using OkHttp (which we know works) then load into ImageView
            Log.d("WEATHER08", "Loading icon from URL: " + iconUrl);
            Log.d("WEATHER08", "Icon position: leftMargin=" + params.leftMargin + ", topMargin=" + params.topMargin);
```

**Explicație:**
- Debugging: Confirmă încercarea de încărcare
- Afișează poziția finală

## 15. Download imagini cu OkHttp în background thread

### Pornirea thread-ului de download

```java
            // Download image using OkHttp in background thread - use same client config as WeatherAPI
            final String finalIconUrl = iconUrl; // Make final for lambda
            new Thread(() -> {
                try {
                    // Use the same OkHttpClient configuration that works for API calls
                    okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
                    
                    okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(finalIconUrl)
                        .header("User-Agent", "Mozilla/5.0")
                        .build();
```

**Linie cu linie:**
- `final String finalIconUrl` - Necesar pentru folosire în lambda (inner class)
- `new Thread(() -> { ... })` - Thread nou pentru operațiuni de rețea
- **NECESAR**: Operațiuni HTTP nu pot rula pe main thread
- Creăm OkHttpClient nou (comentariul zice să folosim config ca în WeatherAPI)
- Construim request cu URL și User-Agent header
- **User-Agent**: Unele servere cer header valid

### Executarea request-ului și procesarea răspunsului

```java
                    try (okhttp3.Response response = client.newCall(request).execute()) {
                        if (response.isSuccessful() && response.body() != null) {
                            byte[] imageBytes = response.body().bytes();
                            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
```

**Linie cu linie:**
- `try-with-resources` - Închide automat response-ul
- `execute()` - Execută request-ul sincron (blocking)
- Verificăm succes și body non-null
- `bytes()` - Citește tot body-ul ca byte array
- `BitmapFactory.decodeByteArray()` - Decodează bytes în Bitmap
  - **Parametri**: bytes, offset (0), length

### Setarea bitmap-ului pe main thread

```java
                            // Load bitmap into ImageView on UI thread
                            android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                            mainHandler.post(() -> {
                                if (bitmap != null && iconView.getParent() != null) {
                                    android.graphics.Bitmap scaledBitmap = android.graphics.Bitmap.createScaledBitmap(bitmap, iconSize, iconSize, true);
                                    iconView.setImageBitmap(scaledBitmap);
                                    Log.d("WEATHER08", "SUCCESS loaded icon from: " + finalIconUrl);
                                } else {
                                    Log.e("WEATHER08", "Failed to set bitmap - bitmap null or view detached");
                                }
                            });
```

**Linie cu linie:**
- **CRITICAL**: Modificări UI trebuie pe main thread
- Creăm Handler pentru main thread
- `post()` - Pune Runnable în coada main thread
- Verificări: bitmap valid și View încă atașat la parent
- `createScaledBitmap()` - Scalează la dimensiunea exactă (iconSize × iconSize)
- `setImageBitmap()` - Setează imaginea pe ImageView
- Logging success sau failure

### Gestionarea erorilor HTTP

```java
                        } else {
                            Log.e("WEATHER08", "HTTP error loading icon: " + response.code() + " from: " + finalIconUrl);
                        }
                    }
```

**Explicație:**
- Logăm erori HTTP (404, 500, etc.)
- Nu crashuim aplicația - iconița pur și simplu nu apare

### Gestionarea erorilor de rețea (UnknownHostException)

```java
                } catch (java.net.UnknownHostException e) {
                    Log.e("WEATHER08", "Network error - cannot resolve host: " + finalIconUrl, e);
```

**Explicație:**
- **UnknownHostException**: Nu poate rezolva DNS
- Exemplu: No internet connection
- Logăm fără crash

### Gestionarea erorilor SSL cu fallback HTTP

```java
                } catch (javax.net.ssl.SSLException e) {
                    Log.e("WEATHER08", "SSL error loading icon: " + finalIconUrl, e);
                    // Try HTTP instead of HTTPS as fallback
                    String httpUrl = finalIconUrl.replace("https://", "http://");
                    try {
                        Log.d("WEATHER08", "Trying HTTP fallback: " + httpUrl);
                        okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient();
                        okhttp3.Request httpRequest = new okhttp3.Request.Builder()
                            .url(httpUrl)
                            .build();
```

**Linie cu linie:**
- **SSLException**: Probleme cu certificat SSL
- **Fallback strategy**: Încearcă HTTP în loc de HTTPS
- `replace("https://", "http://")` - Conversie URL
- Creăm client nou și request nou cu URL HTTP
- **Use case**: Servere cu SSL configuration greșit

### Executarea fallback-ului și procesarea

```java
                        try (okhttp3.Response httpResponse = httpClient.newCall(httpRequest).execute()) {
                            if (httpResponse.isSuccessful() && httpResponse.body() != null) {
                                byte[] imageBytes = httpResponse.body().bytes();
                                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                if (bitmap != null) {
                                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                                    mainHandler.post(() -> {
                                        if (iconView.getParent() != null) {
                                            iconView.setImageBitmap(android.graphics.Bitmap.createScaledBitmap(bitmap, iconSize, iconSize, true));
                                            Log.d("WEATHER08", "SUCCESS loaded icon via HTTP fallback: " + httpUrl);
                                        }
                                    });
                                }
                            }
                        }
```

**Explicație:**
- Logică identică cu încercarea HTTPS
- Download → Decode → Scale → Set pe main thread
- Logging success pentru HTTP fallback

### Gestionarea eșecului fallback-ului

```java
                    } catch (Exception fallbackException) {
                        Log.e("WEATHER08", "HTTP fallback also failed: " + httpUrl, fallbackException);
                    }
```

**Explicație:**
- Dacă și fallback-ul eșuează, logăm
- Nu mai avem alte strategii - iconița nu va apărea

### Gestionarea altor excepții și pornirea thread-ului

```java
                } catch (Exception e) {
                    Log.e("WEATHER08", "Exception loading icon from: " + finalIconUrl, e);
                }
            }).start();
```

**Linie cu linie:**
- Catch-all pentru orice altă excepție
- `.start()` - **CRITICAL**: Pornește efectiv thread-ul
- Fără start(), codul nu se execută!

### Logging final după adăugarea iconView

```java
            Log.d("WEATHER08", "Added icon view at position " + params.leftMargin + ", " + params.topMargin + " for URL: " + iconUrl);
            Log.d("WEATHER08", "Icon view bounds: width=" + iconView.getWidth() + ", height=" + iconView.getHeight());
            Log.d("WEATHER08", "FrameLayout child count: " + frameLayout.getChildCount());
```

**Explicație:**
- Debugging: Confirmă adăugarea View-ului
- **Note**: Width/Height vor fi 0 (View-ul nu e măsurat încă)

### Post-delayed logging pentru poziție efectivă

```java
            // Force layout to ensure icon is positioned
            iconView.post(() -> {
                Log.d("WEATHER08", "Icon view actual position: left=" + iconView.getLeft() + ", top=" + iconView.getTop() + ", right=" + iconView.getRight() + ", bottom=" + iconView.getBottom());
                Log.d("WEATHER08", "Icon view visibility: " + iconView.getVisibility() + ", alpha: " + iconView.getAlpha());
            });
        }
    }
```

**Linie cu linie:**
- `iconView.post()` - Pune Runnable în coada View-ului
- Se execută după ce View-ul e măsurat și poziționat
- **Debugging**: Afișează poziția efectivă după layout
- Verifică vizibilitatea și alpha (transparența)
- **Utile**: Pentru debugging probleme de layout

## 16. Metoda formatDate() - Formatarea datei

```java
    private String formatDate(String dateStr) {
        try {
            // Parse date string (format: "yyyy-MM-dd")
            org.threeten.bp.LocalDate date = org.threeten.bp.LocalDate.parse(dateStr);
            org.threeten.bp.format.DateTimeFormatter formatter = org.threeten.bp.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
            return date.format(formatter);
        } catch (Exception e) {
            return dateStr; // Return original if parsing fails
        }
    }
```

**Linie cu linie:**
- **Input**: "2026-01-08" (format ISO)
- `LocalDate.parse()` - Parsează string-ul (format implicit yyyy-MM-dd)
- Creăm formatter cu pattern "EEEE, MMMM d, yyyy"
  - **EEEE**: Numele complet al zilei (Wednesday)
  - **MMMM**: Numele complet al lunii (January)
  - **d**: Ziua lunii fără zero (8, nu 08)
  - **yyyy**: Anul cu 4 cifre (2026)
- **Output**: "Wednesday, January 8, 2026"
- Try-catch: Dacă parsarea eșuează, returnează string-ul original
- **Locale**: Folosește locale-ul sistemului (EN, RO, etc.)

## 17. Metoda getItemCount() - Numărul de elemente

```java
    @Override
    public int getItemCount() {
        return dailyWeatherItemList.size();
    }
```

**Explicație:**
- Metodă obligatorie din RecyclerView.Adapter
- Returnează numărul total de elemente (de obicei 5 zile)
- RecyclerView folosește acest număr pentru scrolling și recycling

## 18. Clasa internă DailyWeatherViewHolder - ViewHolder pattern

### Declararea clasei ViewHolder

```java
    // ViewHolder for each daily weather item
    public static class DailyWeatherViewHolder extends RecyclerView.ViewHolder {

        private final TextView cityTextView;
        private final TextView dateTextView;
        private final LineChart lineChart;
```

**Linie cu linie:**
- `static` - Clasă statică internă (nu necesită instanță a clasei externe)
- Extinde `RecyclerView.ViewHolder` - Pattern standard
- **Scop**: Păstrează referințe la View-urile din item layout
- Trei câmpuri pentru componentele UI principale

### Constructorul ViewHolder-ului

```java
        public DailyWeatherViewHolder(View itemView) {
            super(itemView);

            // Initialize views
            cityTextView = itemView.findViewById(R.id.city_name);
            dateTextView = itemView.findViewById(R.id.date_text);
            lineChart = itemView.findViewById(R.id.temperature_chart);
        }
    }

}
```

**Linie cu linie:**
- Primește View-ul inflated (item_daily_weather)
- `super(itemView)` - Apelează constructorul ViewHolder
- `findViewById()` pentru fiecare componentă
- **Pattern**: Inițializare o singură dată (la creare), apoi refolosire (recycling)
- **Performance**: findViewById e costisitor - facem o singură dată per ViewHolder

---

## Rezumat

Acest adapter este un exemplu complex de RecyclerView adapter cu grafice și overlay imagini:

### **Scop principal**
- Afișează previziunile meteo grupate pe zile într-un RecyclerView
- Fiecare item conține un grafic de temperatură cu iconiți overlay

### **Funcționalități cheie:**
1. **RecyclerView pattern** - Adapter cu ViewHolder pentru liste eficiente
2. **MPAndroidChart** - Grafice liniare de temperatură configurabile
3. **Overlay iconiți** - Pozițîonare programatică deasupra graficului
4. **Download imagini** - OkHttp pentru încărcarea iconiților în background
5. **Thread management** - Background threads pentru rețea + main thread pentru UI
6. **Error handling** - SSL fallback, retry logic, graceful degradation
7. **Formatare date** - ThreeTenABP pentru formatare user-friendly
8. **Click navigation** - Intent către WeatherDetailActivity

### **Pattern-uri și tehnici:**

**RecyclerView Adapter Pattern:**
- `onCreateViewHolder()` - Creează ViewHolder-i (inflatează layout)
- `onBindViewHolder()` - Populează ViewHolder cu date
- `getItemCount()` - Număr total de elemente
- `updateData()` - Actualizare date cu notifyDataSetChanged()

**ViewHolder Pattern:**
- Păstrează referințe la View-uri pentru refolosire
- Evită findViewById() repetat (optimization)
- Static inner class pentru memory efficiency

**Coordinate Transformation:**
- MPAndroidChart Transformer: valori → pixeli
- Calculare pozițîi absolute pentru overlay
- Margin-based positioning în FrameLayout

**Multi-threading:**
- Background thread pentru HTTP downloads
- Handler pentru comunicare cu main thread
- Post-delayed pentru async UI updates

**Error Handling:**
- Try-catch pentru parsare date
- SSL fallback la HTTP
- Null checks pentru bitmap și parent
- Graceful degradation (iconița lipsește, dar app-ul nu crashuiește)

### **Flow-ul de afișare:**

1. **MainActivity** → actualizează datele → `updateData()`
2. **RecyclerView** → apelează `onBindViewHolder()` pentru fiecare item vizibil
3. **Pentru fiecare zi**:
   - Setează orașul (doar primul item)
   - Formatează și setează data
   - Configurează graficul cu `setupLineChart()`
     - Creează Entry-uri din temperaturi
     - Configurează DataSet (culori, fill, formatare)
     - Dezactivează axe și interacțiuni
     - Invalidate + 100ms delay
   - `overlayIconsOnChart()`:
     - Calculează pozițîi cu Transformer
     - Creează ImageView-uri
     - Adaugă la FrameLayout
     - Pornește thread-uri pentru download iconiți
     - Download → Decode → Scale → Set bitmap pe main thread
4. **Click pe item** → Intent cu primul WeatherItem → WeatherDetailActivity

### **Configurarea graficului:**

| Proprietate | Valoare | Scop |
|-------------|---------|------|
| Line color | #2196F3 (Material Blue) | Culoare linie |
| Line width | 3px | Grosime linie |
| Draw circles | false | Fără cercuri la noduri |
| Draw values | true | Afișează temperaturi |
| Value format | "%.0f°" | Format: 24° |
| Fill enabled | true | Umplere sub linie |
| Fill alpha | 128 (50%) | Semi-transparent |
| All axes | disabled | Grafic minimal |
| Touch | disabled | Nu interacțiune |
| Background | transparent | Integrat în layout |
| Extra offsets | 20, 20, 20, 60 | Spațiu pentru iconiți |

### **Download imagini - Strategie robustă:**

1. **Primary**: HTTPS cu OkHttp
2. **Fallback**: HTTP dacă SSL eșuează
3. **Error handling**: UnknownHost, SSL, generic exceptions
4. **Thread safety**: Background download + main thread UI update
5. **View lifecycle**: Verificare parent înainte de setare bitmap
6. **Scaling**: createScaledBitmap pentru dimensiune exactă
7. **Cleanup**: Ștergere iconiți existenți înainte de re-populate

### **Optimizări:**

- ViewHolder pattern pentru findViewById eficient
- RecyclerView recycling pentru memorie
- Post-delayed pentru layout timing
- Cleanup iconiți vechi (evită memory leaks)
- Guard clauses pentru exit rapid
- Final variables pentru lambda usage
- Try-with-resources pentru resource management

### **Debugging extensiv:**

- 15+ linii de logging pentru poziții și dimensiuni
- Success/failure logging pentru download imagini
- Layout bounds logging post-layout
- Helpful pentru debugging complex layout issues

Această clasă demonstrează integrarea avansată a bibliotecilor third-party (MPAndroidChart, OkHttp) cu componente Android native (RecyclerView, Handler, Thread) pentru o experiență UX bogată și responsivă!
