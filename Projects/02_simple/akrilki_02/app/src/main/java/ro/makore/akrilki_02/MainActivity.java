package ro.makore.akrilki_02;

import android.os.Bundle;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import android.widget.Button;
import android.widget.TextView;
import androidx.core.view.WindowInsetsCompat;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView jokeTextView;
    private Button refreshButton;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        client = new OkHttpClient();

        jokeTextView = findViewById(R.id.jokeTextView);
        refreshButton = findViewById(R.id.refreshButton);

        // Find the quit button and the textview
        Button quitButton = findViewById(R.id.quitButton);
        TextView jokeTextView = findViewById(R.id.jokeTextView);

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // fetch the first joke    
        fetchJoke();

        // Add the quit button action
        quitButton.setOnClickListener(v -> finishAffinity());
        refreshButton.setOnClickListener(v -> fetchJoke());

    }

    private void fetchJoke() {
        String url = "https://official-joke-api.appspot.com/random_joke";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MainActivity", "Failed to fetch joke", e);
                runOnUiThread(() -> jokeTextView.setText("Failed to load joke."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> jokeTextView.setText("Failed to load joke."));
                    return;
                }

                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    String setup = json.getString("setup");
                    String punchline = json.getString("punchline");

                    String joke = setup + "\n\n" + punchline;

                    // Update the UI
                    runOnUiThread(() -> jokeTextView.setText(joke));
                } catch (JSONException e) {
                    Log.e("MainActivity", "Failed to parse joke JSON", e);
                    runOnUiThread(() -> jokeTextView.setText("Failed to load joke."));
                }
            }
        });
    }
}