package ro.makore.akrilki_06;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import ro.makore.akrilki_06.model.NewsItem;
import ro.makore.akrilki_06.parser.NewsParser;
import ro.makore.akrilki_06.api.NewsAPI;
import ro.makore.akrilki_06.adapter.NewsAdapter;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar; // ProgressBar for loading indicator
    private TextView loadingText; // TextView for loading message
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);

        // Initialize ExecutorService with single thread
        executorService = Executors.newSingleThreadExecutor();

        FloatingActionButton fabQuit = findViewById(R.id.fab_quit);
        fabQuit.setOnClickListener(v -> finishAffinity());

        FloatingActionButton fabRefresh = findViewById(R.id.fab_refresh);
        fabRefresh.setOnClickListener(v -> refreshNewsData());

        recyclerView = findViewById(R.id.newsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);

        refreshNewsData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown executor to prevent memory leaks
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void refreshNewsData() {
        
        // Check network availability first
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection available.", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Show loading indicators
        runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            loadingText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        });

        executorService.execute(() -> {
            try {
                String jsonResponse = NewsAPI.fetchNews(this);
                List<NewsItem> newsItems = NewsParser.parseNews(jsonResponse);

                int count = newsItems.size();

                // Update UI on the main thread
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    
                    // Scroll to the top (first item)
                    recyclerView.scrollToPosition(0);    
                    if (newsAdapter == null) {
                        newsAdapter = new NewsAdapter(MainActivity.this, newsItems);
                        recyclerView.setAdapter(newsAdapter);
                    } else {
                        newsAdapter.updateData(newsItems);
                    }
                });
            } catch (IOException e) {
                Log.e("NEWS06", "Error fetching news "+ e.getMessage(), e);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Error fetching news. Please check your internet connection.", Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                // Handle any other exceptions that might occur
                Log.e("NEWS06", "Unexpected error", e);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Unexpected error occurred.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
}
