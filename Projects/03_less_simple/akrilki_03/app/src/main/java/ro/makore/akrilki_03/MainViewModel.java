package ro.makore.akrilki_03;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainViewModel  extends ViewModel {

    private final MutableLiveData<String> mText;
    private OkHttpClient client;
    public MainViewModel () {
        mText = new MutableLiveData<>();
        fetchJoke();
    }

    public void fetchJoke() {
        String url = "https://official-joke-api.appspot.com/random_joke";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client = new OkHttpClient();        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MainActivity", "Failed to fetch joke", e);
                mText.postValue("Failed to load joke." + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mText.postValue("Failed to load joke.");
                    return;
                }

                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    String setup = json.getString("setup");
                    String punchline = json.getString("punchline");

                    String joke = setup + "\n\n" + punchline;

                    // Update the UI
                    mText.postValue(joke);
                } catch (JSONException e) {
                    Log.e("MainActivity", "Failed to parse joke JSON", e);
                    mText.postValue("Failed to parse joke." + e.getMessage());
                }
            }
        });
    }

    public LiveData<String> getText() {
        return mText;
    }
}