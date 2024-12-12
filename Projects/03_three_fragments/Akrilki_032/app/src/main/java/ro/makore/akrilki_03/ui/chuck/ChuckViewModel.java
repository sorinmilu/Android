package ro.makore.akrilki_03.ui.chuck;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChuckViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private OkHttpClient client;
    public ChuckViewModel() {
        mText = new MutableLiveData<>();
        fetchCNFact();
    }

    public void fetchCNFact() {
        String url = "https://api.chucknorris.io/jokes/random";

        // Create the request
        Request request = new Request.Builder()
                .url(url)
                .build();


        client = new OkHttpClient();
        // Execute the network call asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // If the request fails, update the LiveData with an error message
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
                    String cnfact = json.getString("value");
                    mText.postValue(cnfact);
                } catch (JSONException e) {
                    mText.postValue("Failed to parse joke.");
                }
            }
        });
    }

    public LiveData<String> getText() {
        return mText;
    }
}