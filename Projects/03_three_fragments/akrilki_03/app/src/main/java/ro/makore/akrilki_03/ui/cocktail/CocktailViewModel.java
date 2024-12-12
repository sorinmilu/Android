package ro.makore.akrilki_03.ui.cocktail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


public class CocktailViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private OkHttpClient client;

    private MutableLiveData<CocktailData> cocktailLiveData;  // Declare MutableLiveData here

    public MutableLiveData<CocktailData> getCocktailLiveData() {
        return cocktailLiveData;
    }

    public CocktailViewModel() {
        mText = new MutableLiveData<>();
        client = new OkHttpClient();
        cocktailLiveData = new MutableLiveData<>();  // Initialize MutableLiveData
        fetchCocktailData();
    }


    public void fetchCocktailData() {
        String url = "https://www.thecocktaildb.com/api/json/v1/1/random.php";

        Log.i("CocktailViewModel", "Fetching cocktail data.");
        // Create the request
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Execute the network call asynchronously
//        client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // If the request fails, post a failure message
                postFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    postFailure();
                    return;
                }

                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    JSONArray drinksArray = json.getJSONArray("drinks");
                    JSONObject cocktail = drinksArray.getJSONObject(0);

                    // Extract cocktail details
                    String cocktailName = cocktail.getString("strDrink");
                    String instructions = cocktail.getString("strInstructions");

                    Log.i("CocktailViewModel", instructions);
                    // Ingredients
                    StringBuilder ingredients = new StringBuilder();
                    for (int i = 1; i <= 15; i++) {
                        String ingredientKey = "strIngredient" + i;
                        String measureKey = "strMeasure" + i;
                        String ingredient = cocktail.optString(ingredientKey, null);
                        String measure = cocktail.optString(measureKey, null);
                        Log.i("CocktailViewModel ingredient", ingredient);
                        Log.i("CocktailViewModel measure", measure);
                        if (!ingredient.equals("null") && !ingredient.isEmpty()) {
                            ingredients.append(measure != null ? measure : "")
                                    .append(ingredient)
                                    .append("\n");
                        }
                    }

                    // Image URL
                    String imageUrl = cocktail.optString("strDrinkThumb", null);

                    // Create CocktailData object and post it
                    CocktailData cocktailData = new CocktailData(cocktailName, instructions, ingredients.toString(), imageUrl);

                    Log.i("CocktailViewModel", cocktailData.name);

                    postSuccess(cocktailData);

                } catch (JSONException e) {
                    postFailure();
                }
            }
        });
    }

    private void postFailure() {
        // Post failure message (can be a fallback or error message)
        new Handler(Looper.getMainLooper()).post(() -> cocktailLiveData.setValue(null));
    }

    private void postSuccess(CocktailData cocktailData) {
        // Post the fetched cocktail data
        Log.i("CocktailViewModel", "post success here");
        new Handler(Looper.getMainLooper()).post(() -> cocktailLiveData.setValue(cocktailData));
    }

    // CocktailData class to hold the cocktail information
    public static class CocktailData {
        private String name;
        private String instructions;
        private String ingredients;
        private String imageUrl;

        public CocktailData(String name, String instructions, String ingredients, String imageUrl) {
            this.name = name;
            this.instructions = instructions;
            this.ingredients = ingredients;
            this.imageUrl = imageUrl;
        }

        public String getName() {
            return name;
        }

        public String getInstructions() {
            return instructions;
        }

        public String getIngredients() {
            return ingredients;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }

    public LiveData<String> getText() {
        return mText;
    }
}