# Android lifecycle

În Android, "ciclul de viață" (lifecycle) al unei activități sau fragmente se referă la secvența de stări și tranziții prin care trece o activitate de-a lungul existenței sale, de la crearea sa până la distrugerea finală. Fiecare activitate din Android trece printr-o serie de evenimente, iar în fiecare dintre aceste evenimente, sistemul de operare al Android poate apela metode specifice pentru a gestiona resursele și a răspunde la schimbările contextuale, cum ar fi interacțiunea utilizatorului, schimbările de orientare ale ecranului sau gestionarea memoriei.

## Evenimente legate de ciclul de viata

Ciclul de viață al unei activități include metode cheie, cum ar fi:

- onCreate(): Se apelează atunci când activitatea este creată.
- onStart(): Se apelează atunci când activitatea devine vizibilă pentru utilizator.
- onResume(): Se apelează atunci când activitatea devine activă și interactivă.
- onPause(): Se apelează atunci când activitatea nu mai este interactivă, dar încă vizibilă.
- onStop(): Se apelează atunci când activitatea nu mai este vizibilă.
- onDestroy(): Se apelează atunci când activitatea este distrusă.

Acest ciclu de viață permite aplicațiilor să gestioneze eficient resursele, să salveze date importante atunci când activitatea este întreruptă (de exemplu, în timpul unei schimbări de orientare a ecranului), și să își restabilească starea atunci când activitatea este recreată.

Fiecare dintre aceste metode se ocupă cu o anumită fază din viața unei activități Android. De la crearea și afișarea acesteia (în onCreate() și onStart()), până la gestionarea resurselor și salvarea stării aplicației (în onPause() și onSaveInstanceState()), fiecare metodă ajută să controlezi și să răspunzi la modificările în ciclul de viață al aplicației.


### onCreate(Bundle savedInstanceState)

Definiție: Este apelată când activitatea este creată pentru prima dată. Este locul unde se inițializează componentele activității, cum ar fi UI-ul și variabilele. Se folosește pentru a seta layout-ul și pentru a inițializa resursele necesare.
Exemplu: super.onCreate(savedInstanceState); setContentView(R.layout.activity_main);

### onStart()

Definiție: Este apelată când activitatea devine vizibilă, dar nu interactivă (adică este pe ecran, dar nu a fost încă complet încărcată). Este un moment în care se pregătesc resursele pentru interacțiune cu utilizatorul.
Exemplu: Aici nu sunt de obicei modificări semnificative, dar poți porni animații sau alte resurse care trebuie să fie active înainte ca activitatea să devină complet interactivă.

### onResume()

Definiție: Este apelată când activitatea devine complet activă și interactivă. În acest punct, activitatea este pe ecran și utilizatorul poate interacționa cu ea. Aici se reiau de obicei sarcinile care au fost întrerupte, cum ar fi redarea multimedia sau activitățile care erau active în onPause().
Exemplu: Actualizarea UI-ului sau continuarea unor operațiuni de rețea.

### onPause()

Definiție: Este apelată când activitatea este pe cale să fie înlocuită de o altă activitate, dar încă nu este distrusă (de exemplu, când utilizatorul deschide o altă activitate, dar aceasta nu este încă complet acoperită). Este folosită pentru a salva date și a opri activitățile care nu mai sunt vizibile.
Exemplu: Salvarea progresului, oprirea animațiilor sau a redării multimedia.

### onStop()

Definiție: Este apelată când activitatea nu mai este vizibilă pentru utilizator (de exemplu, când utilizatorul navighează într-o altă activitate sau aplicație). În acest punct, activitatea nu mai are nevoie de resurse vizuale și poate să-și elibereze resursele.
Exemplu: Eliberarea resurselor heavy (precum conexiuni la baze de date, rețele) sau oprirea proceselor care nu mai sunt necesare.

### onRestart()

Definiție: Este apelată când activitatea care era oprită (din cauza unei schimbări de context) este pe cale să fie repornită. Este similară cu onStart(), dar de obicei se află între onStop() și onStart().
Exemplu: Reînceperea unor procese care au fost oprite în onStop().

### onDestroy()

Definiție: Este apelată când activitatea este pe cale să fie distrusă (de obicei, când utilizatorul navighează complet de la ea sau când sistemul o elimină pentru a elibera resurse). Aici se eliberează toate resursele care mai sunt necesare înainte de distrugerea activității.
Exemplu: Închiderea fișierelor deschise, eliberarea conexiunilor la rețea.
onSaveInstanceState(Bundle outState)

Definiție: Este apelată pentru a salva starea activității, de exemplu, când activitatea este distrusă temporar din cauza schimbării orientării ecranului sau a unui apel al sistemului. Aceasta permite salvarea datelor temporare (de exemplu, text introdus de utilizator).
Exemplu: outState.putString("key", "value");

### onRestoreInstanceState(Bundle savedInstanceState)

Definiție: Este apelată pentru a restaura starea activității salvată anterior în onSaveInstanceState(). Acest lucru ajută la restabilirea stării utilizatorului atunci când activitatea este recreată (de exemplu, după o schimbare a orientării ecranului).
Exemplu: String savedData = savedInstanceState.getString("key");

## Androix.lifecycle 

androidx.lifecycle, este un namespace din bibliotecile Android care oferă o serie de instrumente pentru gestionarea mai eficientă a ciclului de viață al componentelor, precum activități, fragmente și chiar ViewModels. Scopul principal al acestui namespace este de a ajuta dezvoltatorii să gestioneze stările aplicațiilor și să îmbunătățească modul în care datele sunt păstrate și actualizate pe parcursul ciclului de viață al unei activități sau al altor componente ale aplicației.

Componente principale din androidx.lifecycle:
LiveData: Este un tip de date observabil care se actualizează automat atunci când datele asociate se schimbă. Acesta permite gestionarea datelor care pot fi observate de activități și fragmente, fără a fi nevoie de o gestionare manuală a resurselor sau de a face upgrade-uri explicite ale UI-ului. LiveData respectă ciclul de viață al componentelor care o observă, asigurându-se că aceste componente nu vor primi actualizări după ce au fost distruse.

ViewModel: Este o componentă destinată să păstreze datele legate de o activitate sau fragment pe termen lung. ViewModel nu va fi distrus la schimbarea orientării ecranului sau la alte modificări ale ciclului de viață, astfel încât să poată păstra datele aplicației chiar și atunci când activitatea este recreată.

LifecycleOwner: Oricare componentă care are un ciclu de viață (cum ar fi o activitate sau un fragment) poate implementa interfața LifecycleOwner. Acesta furnizează un obiect care poate fi utilizat pentru a monitoriza și răspunde la schimbările ciclului de viață, cum ar fi onCreate(), onStart(), onStop(), etc.

LifecycleObserver: Oferă un mecanism de ascultare a schimbărilor de stare ale unui obiect care implementează LifecycleOwner. Acesta permite implementarea de logică personalizată pentru fiecare stare a ciclului de viață.

Exemple de utilizare:
LiveData și ViewModel sunt deosebit de utile pentru gestionarea datelor persistente pe termen lung, care sunt legate de activități și fragmente, cum ar fi datele de utilizator, răspunsuri de la API-uri, sau starea aplicației.
LifecycleObserver ajută la adăugarea de comportamente sau logică ce trebuie să fie activată sau dezactivată în funcție de starea ciclului de viață.
În concluzie, androidx.lifecycle adaugă un strat suplimentar de abstracție pentru gestionarea ciclului de viață în Android, astfel încât dezvoltatorii să poată crea aplicații mai robuste, care sunt mai ușor de întreținut și mai eficiente din punct de vedere al resurselor.