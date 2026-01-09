# MainActivity.java - Documentație Linie cu Linie

## Prezentare

`MainActivity` este activitatea principală a aplicației de hărți, responsabilă pentru:
- **Navigation Drawer** (meniu lateral glisant) pentru navigarea între fragmente
- **Navigation Component** pentru gestionarea destinațiilor aplicației
- **Toolbar** personalizat cu acțiuni în menu
- **View Binding** pentru acces type-safe la componentele UI

**Arhitectură aplicație:**
- **Navigation Drawer** în loc de Bottom Navigation (panou lateral vs bare jos)
- **4 destinații principale:** Home, Geodata, Location, Address
- **DrawerLayout** + NavigationView pentru UI-ul lateral
- **Opțiune Quit** în menu pentru închiderea aplicației

**Diferențe față de 04_three_fragments:**
- Navigation Drawer vs Bottom Navigation
- DrawerLayout vs BottomNavigationView
- Menu lateral vs menu inferior
- Opțiune de închidere aplicație

## Analiza Linie cu Linie

### Declararea Pachetului

```java
package ro.makore.akrilki_05;
```

Declară pachetul aplicației de hărți.

---

### Import Bundle

```java
import android.os.Bundle;
```

Importă `Bundle` folosit pentru salvarea și restaurarea stării activității.

---

### Import View

```java
import android.view.View;
```

Importă clasa de bază `View` (nu este folosită explicit în acest cod, probabil generată automat de template).

---

### Import Menu

```java
import android.view.Menu;
```

Importă clasa `Menu` pentru crearea meniului din action bar.

---

### Import MenuItem

```java
import android.view.MenuItem;
```

Importă clasa `MenuItem` pentru gestionarea acțiunilor din menu.

---

### Import Snackbar

```java
import com.google.android.material.snackbar.Snackbar;
```

Importă `Snackbar` din Material Design (nu este folosit în acest cod, probabil rămas din template).

---

### Import NavigationView

```java
import com.google.android.material.navigation.NavigationView;
```

Importă `NavigationView` - componenta Material Design care afișează meniul lateral din drawer.

**Rol:** Afișează lista de destinații în panoul lateral glisant.

---

### Import NavController

```java
import androidx.navigation.NavController;
```

Importă `NavController` - controllerul central pentru navigarea între destinații.

---

### Import Navigation

```java
import androidx.navigation.Navigation;
```

Importă clasa utilitară `Navigation` pentru găsirea NavController-ului.

---

### Import AppBarConfiguration

```java
import androidx.navigation.ui.AppBarConfiguration;
```

Importă `AppBarConfiguration` pentru configurarea comportamentului action bar-ului cu Navigation Component.

**Rol:** Definește care destinații sunt "top level" (nu afișează buton back, ci hamburger icon pentru drawer).

---

### Import NavigationUI

```java
import androidx.navigation.ui.NavigationUI;
```

Importă clasa utilitară `NavigationUI` pentru sincronizarea UI-ului de navigare cu NavController.

---

### Import DrawerLayout

```java
import androidx.drawerlayout.widget.DrawerLayout;
```

Importă `DrawerLayout` - layout-ul container care permite afișarea unui panou glisant lateral.

**Structură:**
```
DrawerLayout
    ├─── Content principal (NavHostFragment)
    └─── Drawer (NavigationView cu meniul)
```

---

### Import AppCompatActivity

```java
import androidx.appcompat.app.AppCompatActivity;
```

Importă clasa de bază `AppCompatActivity` care oferă suport pentru tema Material Design și action bar.

---

### Import View Binding

```java
import ro.makore.akrilki_05.databinding.ActivityMainBinding;
```

Importă clasa de binding generată automat din `activity_main.xml`.

---

### Declararea Clasei

```java
public class MainActivity extends AppCompatActivity {
```

Declară clasa `MainActivity` care moștenește din `AppCompatActivity`.

---

### Variabilă AppBarConfiguration

```java
    private AppBarConfiguration mAppBarConfiguration;
```

Declară variabila care va conține configurația action bar-ului pentru Navigation Component.

**Rol:** Definește:
- Care destinații sunt "top level" (afișează hamburger icon, nu back arrow)
- Layout-ul care poate fi deschis (DrawerLayout)

---

### Variabilă Binding

```java
    private ActivityMainBinding binding;
```

Declară variabila care va reține obiectul de binding pentru layout-ul principal.

---

### Metoda onCreate - Semnătura

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
```

Metodă de lifecycle apelată când activitatea este creată.

**Parametru savedInstanceState:** Conține starea salvată din instanță anterioară (null la prima creare).

---

### Apel Super onCreate

```java
        super.onCreate(savedInstanceState);
```

Apelează implementarea din clasa părinte pentru inițializare standard.

---

### Inflare View Binding

```java
        binding = ActivityMainBinding.inflate(getLayoutInflater());
```

Inflează layout-ul folosind View Binding.

**getLayoutInflater():** Obține LayoutInflater-ul activității.

**Rezultat:** Obiectul `binding` conține referințe la toate View-urile din `activity_main.xml`.

---

### Setare Content View

```java
        setContentView(binding.getRoot());
```

Setează layout-ul activității la view-ul root din binding.

**binding.getRoot():** Returnează view-ul root (probabil un DrawerLayout).

---

### Setare Toolbar ca Action Bar

```java
        setSupportActionBar(binding.appBarMain.toolbar);
```

Setează Toolbar-ul personalizat ca action bar al activității.

**binding.appBarMain.toolbar:**
- `appBarMain` = referință la include-ul `app_bar_main.xml`
- `toolbar` = Toolbar-ul din acel layout
- Permite personalizarea completă a action bar-ului

**Efect:** Toolbar-ul devine action bar-ul activității (afișează titlu, menu, navigație).

---

### Găsire DrawerLayout

```java
        DrawerLayout drawer = binding.drawerLayout;
```

Obține referința la DrawerLayout din binding.

**DrawerLayout:** Container principal care permite afișarea panoului lateral glisant.

---

### Găsire NavigationView

```java
        NavigationView navigationView = binding.navView;
```

Obține referința la NavigationView din binding.

**NavigationView:** Componenta care afișează meniul lateral cu destinațiile de navigare.

---

### Construire AppBarConfiguration - Builder

```java
        mAppBarConfiguration = new AppBarConfiguration.Builder(
```

Creează un builder pentru configurarea action bar-ului cu Navigation Component.

**Builder Pattern:** Permite configurarea treptată a obiectului.

---

### Definire Top Level Destinations - nav_home

```java
                R.id.nav_home,
```

Adaugă destinația Home ca destinație top level.

**Top level destination:** Destinație principală care afișează hamburger icon (☰) pentru deschidere drawer, nu back arrow (←).

---

### Definire Top Level Destinations - nav_geodata

```java
                R.id.nav_geodata,
```

Adaugă destinația Geodata ca destinație top level.

---

### Definire Top Level Destinations - nav_location

```java
                R.id.nav_location,
```

Adaugă destinația Location ca destinație top level.

---

### Definire Top Level Destinations - nav_address

```java
                R.id.nav_address)
```

Adaugă destinația Address ca destinație top level.

**Toate cele 4 destinații:** Afișează hamburger icon pentru acces la drawer, nu back arrow.

---

### Setare Openable Layout

```java
                .setOpenableLayout(drawer)
```

Asociază DrawerLayout-ul cu configurația, permițând deschiderea drawer-ului prin apăsarea hamburger icon-ului.

**Comportament:**
- Click pe hamburger icon → deschide drawer
- Swipe de la marginea stângă → deschide drawer
- Click pe destinație → închide drawer și navighează

---

### Finalizare Build AppBarConfiguration

```java
                .build();
```

Finalizează construirea obiectului `AppBarConfiguration`.

---

### Găsire NavController

```java
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
```

Găsește NavController-ul asociat cu NavHostFragment-ul din layout.

**Parametri:**

**this:** Context-ul activității.

**R.id.nav_host_fragment_content_main:** ID-ul FragmentContainerView-ului care găzduiește NavHostFragment.

**NavController:** Gestionează navigarea între fragmentele definite în navigation graph.

---

### Setup ActionBar cu NavController

```java
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
```

Sincronizează action bar-ul cu NavController-ul.

**Efecte:**

**Titlu actualizat automat:** Titlul din action bar se schimbă pe baza label-ului destinației curente.

**Hamburger icon pentru top level:** Destinațiile top level afișează hamburger icon (☰).

**Back arrow pentru alte destinații:** Alte destinații ar afișa back arrow (←).

**Click pe icon:** Deschide drawer (top level) sau navighează back (alte destinații).

---

### Setup NavigationView cu NavController

```java
        NavigationUI.setupWithNavController(navigationView, navController);
```

Sincronizează NavigationView-ul (meniul lateral) cu NavController-ul.

**Efecte:**

**Highlight destinație curentă:** Item-ul corespunzător destinației active este evidențiat.

**Click pe item:** Navighează automat la destinația respectivă și închide drawer-ul.

**Sincronizare automată:** Destinația din drawer și din NavController sunt mereu sincronizate.

---

### Metoda onCreateOptionsMenu - Semnătura

```java
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
```

Metodă de lifecycle apelată când trebuie creat menu-ul din action bar.

**Parametru menu:** Obiectul Menu în care se adaugă item-urile.

**Return:** `true` = menu-ul este afișat, `false` = menu-ul nu este afișat.

---

### Inflare Menu

```java
        getMenuInflater().inflate(R.menu.main, menu);
```

Inflează menu-ul definit în `res/menu/main.xml` și adaugă item-urile în action bar.

**getMenuInflater():** Returnează MenuInflater-ul activității.

**R.menu.main:** Resursa XML care definește item-urile meniului (probabil conține "Quit").

**Rezultat:** Item-urile din XML apar în action bar (iconițe sau overflow menu).

---

### Return True pentru Afișare Menu

```java
        return true;
```

Returnează `true` pentru a indica că menu-ul trebuie afișat.

---

### Metoda onOptionsItemSelected - Semnătura

```java
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
```

Metodă apelată când un item din menu este selectat.

**Parametru item:** Item-ul care a fost selectat.

**Return:** `true` = evenimentul a fost gestionat, `false` = evenimentul nu a fost gestionat.

---

### Extragere ID Item

```java
        int id = item.getItemId();
```

Obține ID-ul item-ului selectat pentru a determina ce acțiune să execute.

---

### Verificare Quit Action

```java
        if (id == R.id.action_quit) {
```

Verifică dacă item-ul selectat este opțiunea "Quit".

**R.id.action_quit:** ID-ul definit în `res/menu/main.xml` pentru opțiunea de închidere.

---

### Închidere Activity

```java
            finish();
```

Închide activitatea curentă (MainActivity).

**Efect:** Apelează `onPause()`, `onStop()`, `onDestroy()` și elimină activitatea din back stack.

---

### Terminare Process

```java
            System.exit(0);
```

Termină complet procesul aplicației.

**System.exit(0):**
- **0** = exit code pentru succes
- Termină JVM-ul și procesul Android
- Toate activitățile și serviciile sunt oprite
- **Utilizare:** Pentru aplicații unde se dorește închidere completă (nu doar minimizare)

**Note:** În general, Android nu recomandă `System.exit()` (sistemul gestionează procesele automat), dar este folosit aici pentru comportament explicit de închidere.

---

### Return True pentru Eveniment Gestionat

```java
            return true;
```

Returnează `true` pentru a indica că evenimentul a fost gestionat.

---

### Delegare la Super pentru Alte Items

```java
        return super.onOptionsItemSelected(item);
```

Delege gestionarea altor item-uri către implementarea din clasa părinte.

**Când se execută:** Pentru item-uri care nu sunt "Quit" (dacă ar exista altele în viitor).

---

### Metoda onSupportNavigateUp - Semnătura

```java
    @Override
    public boolean onSupportNavigateUp() {
```

Metodă apelată când utilizatorul apasă butonul de navigare up (hamburger icon sau back arrow) din action bar.

**Rol:** Gestionează comportamentul personalizat pentru navigarea up.

---

### Găsire NavController pentru Navigate Up

```java
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
```

Găsește NavController-ul pentru a gestiona navigarea.

---

### Delegare Navigare la NavigationUI

```java
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
```

**Explicație detaliată:**

Încearcă să gestioneze navigarea prin NavigationUI, iar dacă aceasta eșuează, delege la implementarea default.

**NavigationUI.navigateUp(navController, mAppBarConfiguration):**
- Gestionează navigarea bazată pe configurație
- Pentru top level destinations: deschide drawer-ul
- Pentru alte destinații: navighează back
- Returnează `true` dacă a gestionat navigarea, `false` altfel

**|| super.onSupportNavigateUp():**
- Operator OR logic
- Dacă NavigationUI returnează `false`, se apelează implementarea default
- Asigură că navigarea funcționează chiar dacă NavigationUI nu o gestionează

**Flux:**
```
User apasă hamburger icon (top level destination)
    │
    └──> NavigationUI.navigateUp()
         │
         ├──> Verifică AppBarConfiguration
         │    └──> Destinație top level detectată
         │         └──> Deschide drawer
         │              └──> return true
         │
         └──> super.onSupportNavigateUp() NU se apelează (short-circuit)
```

---

## Arhitectură Navigation Drawer

```
activity_main.xml (DrawerLayout)
    │
    ├─── Content Principal
    │    │
    │    └─── app_bar_main.xml (include)
    │         │
    │         ├─── Toolbar
    │         │    ├─── Hamburger Icon (top level)
    │         │    ├─── Titlu
    │         │    └─── Menu (Quit)
    │         │
    │         └─── content_main.xml (include)
    │              │
    │              └─── NavHostFragment
    │                   │
    │                   ├─── HomeFragment
    │                   ├─── GeodataFragment
    │                   ├─── LocationFragment
    │                   └─── AddressFragment
    │
    └─── Navigation Drawer (lateral)
         │
         └─── NavigationView
              │
              ├─── Header (informații user, logo, etc.)
              └─── Menu Items
                   ├─── Home
                   ├─── Geodata
                   ├─── Location
                   └─── Address
```

---

## Flux Navigare prin Drawer

```
[App pornit] → onCreate()
    │
    ├──> View Binding inflate
    ├──> setSupportActionBar(toolbar)
    ├──> AppBarConfiguration.Builder
    │    ├──> nav_home (top level)
    │    ├──> nav_geodata (top level)
    │    ├──> nav_location (top level)
    │    ├──> nav_address (top level)
    │    └──> setOpenableLayout(drawer)
    │
    ├──> Navigation.findNavController()
    ├──> NavigationUI.setupActionBarWithNavController()
    │    └──> Sincronizare action bar cu destinații
    │
    └──> NavigationUI.setupWithNavController(navigationView)
         └──> Sincronizare drawer menu cu destinații


[User apasă hamburger icon]
    │
    └──> onSupportNavigateUp()
         │
         └──> NavigationUI.navigateUp()
              │
              └──> DrawerLayout.openDrawer()
                   │
                   └──> Drawer glisează de la stânga


[User selectează item din drawer]
    │
    └──> NavigationView.OnNavigationItemSelectedListener
         │
         └──> NavController.navigate(destinationId)
              │
              ├──> Fragment swap în NavHostFragment
              ├──> Drawer se închide automat
              └──> Titlu action bar actualizat


[User apasă Quit din menu]
    │
    └──> onOptionsItemSelected(item)
         │
         ├──> if (id == R.id.action_quit)
         │    ├──> finish() ◄─ Închide MainActivity
         │    └──> System.exit(0) ◄─ Termină procesul
         │
         └──> App închis complet
```

---

## Comparație Bottom Navigation vs Navigation Drawer

| Aspect | Bottom Navigation (04_three_fragments) | Navigation Drawer (05_maps) |
|--------|----------------------------------------|------------------------------|
| **Poziție** | Jos (bottom) | Lateral stânga (side) |
| **Componente** | BottomNavigationView | DrawerLayout + NavigationView |
| **Acces** | Mereu vizibil | Ascuns, deschis prin swipe/hamburger |
| **Număr destinații** | 3-5 (recomandat) | Nelimitat (scrollable) |
| **Spațiu ecran** | Ocupă spațiu permanent jos | Nu ocupă spațiu când închis |
| **Utilizare** | Navigare rapidă între 3-5 destinații | Multe destinații sau opțiuni |
| **Icon în ActionBar** | Nu (fără toolbar special) | Hamburger icon (☰) |
| **AppBarConfiguration** | Top level IDs | Top level IDs + setOpenableLayout() |
| **Material Design** | Bottom app bar | Navigation drawer |

---

## Lifecycle și Navigation

```
MainActivity creată
    │
    ├─── onCreate()
    │    │
    │    ├──> Binding inflate
    │    ├──> Setup Toolbar
    │    ├──> Setup Navigation
    │    │    ├──> AppBarConfiguration (top level destinations)
    │    │    ├──> NavController găsit
    │    │    ├──> ActionBar sincronizat
    │    │    └──> NavigationView sincronizat
    │    │
    │    └──> NavHostFragment încarcă destinația start (Home)
    │
    ├─── onCreateOptionsMenu()
    │    └──> Menu inflate (Quit action)
    │
    ├─── onStart()
    ├─── onResume() ◄─ Activity activă
    │
    │ [User interacționează]
    │
    │ [User navighează între fragmente prin drawer]
    │    └──> Fragmente swapped în NavHostFragment
    │         └──> MainActivity rămâne activă
    │
    │ [User apasă Quit]
    │    │
    │    └──> onOptionsItemSelected()
    │         │
    │         ├──> finish()
    │         │    │
    │         │    ├──> onPause()
    │         │    ├──> onStop()
    │         │    └──> onDestroy()
    │         │
    │         └──> System.exit(0)
    │              └──> Process terminat
    │
    └─── App închis complet
```

---

## AppBarConfiguration - Top Level Destinations

```
Top Level Destination (nav_home, nav_geodata, nav_location, nav_address)
    │
    ├─── Hamburger Icon (☰) în ActionBar
    │    │
    │    └─── Click → onSupportNavigateUp()
    │         │
    │         └─── NavigationUI.navigateUp()
    │              │
    │              └─── DrawerLayout.openDrawer()
    │
    └─── NO Back Arrow (←)


Non-Top Level Destination (dacă ar exista sub-destinații)
    │
    ├─── Back Arrow (←) în ActionBar
    │    │
    │    └─── Click → onSupportNavigateUp()
    │         │
    │         └─── NavigationUI.navigateUp()
    │              │
    │              └─── NavController.navigateUp()
    │                   │
    │                   └─── Navigare back la destinația anterioară
    │
    └─── NO Hamburger Icon (☰)
```

---

## View Binding Hierarchy

```
ActivityMainBinding (activity_main.xml)
    │
    ├─── drawerLayout: DrawerLayout
    │    │
    │    └─── Root container pentru drawer pattern
    │
    ├─── appBarMain: AppBarMainBinding (include app_bar_main.xml)
    │    │
    │    └─── toolbar: Toolbar
    │         │
    │         └─── Toolbar personalizat ca ActionBar
    │
    └─── navView: NavigationView
         │
         └─── Meniul lateral cu destinațiile
```

**Accesare nested binding:**
```java
binding.appBarMain.toolbar  // Acces la toolbar din include
```

---

## Menu vs Navigation

### Menu (res/menu/main.xml)

```
Options Menu în ActionBar (3 dots sau direct)
    │
    └─── action_quit
         │
         └─── Click → onOptionsItemSelected()
              │
              ├──> finish()
              └──> System.exit(0)
```

### Navigation Menu (res/menu/activity_main_drawer.xml)

```
Drawer Navigation Menu
    │
    ├─── nav_home
    ├─── nav_geodata
    ├─── nav_location
    └─── nav_address
         │
         └─── Click → NavigationView.OnNavigationItemSelectedListener
              │
              └─── NavController.navigate(destinationId)
```

**Diferență:**
- **Options Menu** = acțiuni (Quit, Settings, Share, etc.)
- **Navigation Menu** = destinații (fragmente, ecrane)

---

## Sincronizare Automată NavigationUI

### setupActionBarWithNavController()

```
Sincronizează ActionBar cu NavController
    │
    ├─── Titlu actualizat automat
    │    └─── Ia label-ul din navigation graph pentru destinația curentă
    │
    ├─── Icon actualizat automat
    │    ├─── Top level → Hamburger (☰)
    │    └─── Non-top level → Back arrow (←)
    │
    └─── Click pe icon gestionat automat
         ├─── Hamburger → deschide drawer
         └─── Back arrow → navigare back
```

### setupWithNavController(navigationView)

```
Sincronizează NavigationView cu NavController
    │
    ├─── Highlight destinație curentă
    │    └─── Item-ul activ este evidențiat vizual
    │
    ├─── Click pe item → navigate automat
    │    ├─── NavController.navigate(itemId)
    │    └─── Drawer se închide automat
    │
    └─── Sincronizare bidirectională
         ├─── Navigate programatic → drawer actualizat
         └─── Click în drawer → NavController actualizat
```

---

## Rezumat Concepte Cheie

**Navigation Drawer:**
- Panou lateral glisant pentru navigare între destinații
- DrawerLayout ca root container
- NavigationView pentru afișarea menu-ului
- Hamburger icon pentru deschidere

**AppBarConfiguration:**
- Definește destinații top level (afișează hamburger, nu back)
- setOpenableLayout() pentru asociere cu DrawerLayout
- Gestionează comportamentul icon-ului din ActionBar

**NavigationUI:**
- Sincronizare automată între UI și NavController
- setupActionBarWithNavController() pentru ActionBar
- setupWithNavController() pentru NavigationView
- navigateUp() pentru gestionarea navigării up

**View Binding:**
- Acces type-safe la View-uri
- Include bindings (appBarMain.toolbar)
- Evită findViewById

**Options Menu:**
- Menu în ActionBar pentru acțiuni
- onCreateOptionsMenu() pentru inflare
- onOptionsItemSelected() pentru gestionare click
- Quit action: finish() + System.exit(0)

**Lifecycle:**
- onCreate() pentru setup
- onSupportNavigateUp() pentru navigare up customizată
- finish() pentru închidere activity
- System.exit(0) pentru terminare process
