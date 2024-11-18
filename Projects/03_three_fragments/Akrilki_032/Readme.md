# Aplicatie mobila cu o activitate si trei fragmente

Aplicatia are o activitate principala care contine trei fragmente distincte, fiecare dintre ele preluand o serie de informatii dintr-un anumit API si afisand-ul intr-un TextView. Componentele sunt urmatoarele: 


## Descrierea aplicatiei

### Chuck Norris facts

Preia date de la URL-ul https://api.chucknorris.io/jokes/random. Acest API returneaza un JSON care arata astfel: 

```json
{
  "categories": [],
  "created_at": "2020-01-05 13:42:25.628594",
  "icon_url": "https://api.chucknorris.io/img/avatar/chuck-norris.png",
  "id": "xlNv9jQvQJ2WNLO3gDe0Jw",
  "updated_at": "2020-01-05 13:42:25.628594",
  "url": "https://api.chucknorris.io/jokes/xlNv9jQvQJ2WNLO3gDe0Jw",
  "value": "Chuck Norris spoke to Helen Keller on the phone"
}
```

Fragmentul preia continutul cheii "value" si o afiseaza in textview. Fragmentul contine de asemenea un buton de refresh care reia procesul. 

### Jokes 

Preia datele de la https://official-joke-api.appspot.com/random_joke care returneaza un json care arata astfel: 

```json
{
  "type": "general",
  "setup": "What is the leading cause of dry skin?",
  "punchline": "Towels",
  "id": 246
}
```

Fragmentul preia continutul cheilor "setup" si "punchline" si le afiseaza in TextView. Butonul de refresh reia procesul

### Cocktails

Preia datele de la url-ul: https://www.thecocktaildb.com/api/json/v1/1/random.php care retunreaza un JSON cu urmatoarea structura: 

```bash
{
  "drinks": [
    {
      "idDrink": "17223",
      "strDrink": "Abbey Martini",
      "strDrinkAlternate": null,
      "strTags": null,
      "strVideo": null,
      "strCategory": "Cocktail",
      "strIBA": null,
      "strAlcoholic": "Alcoholic",
      "strGlass": "Cocktail glass",
      "strInstructions": "Put all ingredients into a shaker and mix, then strain contents into a chilled cocktail glass.",
      "strInstructionsES": "Ponga todos los ingredientes en una coctelera, mézclelos y cuele el contenido en una copa de cóctel fría.",
      "strInstructionsDE": "Alle Zutaten in einen Shaker geben und mischen, dann den Inhalt in ein gekühltes Cocktailglas abseihen.",
      "strInstructionsFR": "Mettre tous les ingrédients dans un shaker et mélanger, puis filtrer le contenu dans un verre à cocktail réfrigéré.",
      "strInstructionsIT": "Mettere tutti gli ingredienti in uno shaker e mescolare, quindi filtrare il contenuto in una coppetta da cocktail fredda.",
      "strInstructionsZH-HANS": null,
      "strInstructionsZH-HANT": null,
      "strDrinkThumb": "https://www.thecocktaildb.com/images/media/drink/2mcozt1504817403.jpg",
      "strIngredient1": "Gin",
      "strIngredient2": "Sweet Vermouth",
      "strIngredient3": "Orange Juice",
      "strIngredient4": "Angostura Bitters",
      "strMeasure1": "2 shots ",
      "strMeasure2": "1 shot ",
      "strMeasure3": "1 shot ",
      "strMeasure4": "3 dashes ",
      "strImageSource": null,
      "strImageAttribution": null,
      "strCreativeCommonsConfirmed": "No",
      "dateModified": "2017-09-07 21:50:03"
    }
  ]
}
```

Fragmentul preia aceste date, inclusiv imaginea din cheia "strDrinkThumb" si le afiseaza in fragment. 

## Structura aplicatiei




