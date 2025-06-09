# Dezvoltarea aplicațiilor mobile - Proiecte

Acestea sunt câteva proiecte care pot fi compilate și instalate pe un dispozitiv mobil Android. Fiecare dintre ele are o serie de descrieri și explicații. 

[Hello World](01_hello_world/Readme.md) este cea mai simplă aplicație Andoid posibilă, fără pictograme personalizate, cu absolut totul redus la minim, layouturi în fișiere xml, stringuri hardcodate... etc. 

[Simple](01_simple/Readme.md) este o versiune puțin mai avansată a aplicației HelloWorld, care are o structură mai apropiată de o aplicație Android reală, cu layoutul separat într-un fișier xml, cu propria pictogramă, cu un buton care permite părăsirea aplicației. 

[Simple](02_simple/Readme.md) este o aplicație care interacționează cu un API extern care servește glume. Aplicația preia o glumă de la acel API și o afișează în activitatea principală. Aplicația oferă și un buton care preia o altă glumă de la API și o înlocuiește pe cea din ecran. 

[Less Simple](03_less_simple) este o versiune puțin mai complicată a aplicației anterioare care utilizează un mod diferit de preluare și de stocare a informațiilor preluate de la API astfel încât rotirea ecranului (și deci resetarea activității) să nu declanșeze un nou apel API și modificarea glumei. 

[Three fragments](04_three_fragments/Readme.md) Aplicație care conține trei fragmente, fiecare dintre ele interacționând cu un alt API pentru a prelua conținutul. Fiecare fragment are un buton care permite preluarea unui nou element de la API-ul respectiv. Navigarea de la un fragment la altul se face cu ajutorul unei bare de navigatie care se afla la baza ecranului. 

[Maps](05_maps/Readme.md) Aplicație care utilizează capacitatea Google Maps API pentru afișarea hărților și interacțiunea cu senzorul GPS. Aplicația navighează între mai multe fragmente folosind un meniu lateral derulant.  

[News](06_news/Readme.md) Aplicație de afișare a unui feed de news preluat de la un API. Aplicația permite refresh-ul feed-ului de news și o vizualizare de detaliu a fiecărei știri. 

[Camera](07_camera/Readme.md) Aplicație care demonstrează utilizarea camerei foto disponibile pe un dispozitiv Android. 