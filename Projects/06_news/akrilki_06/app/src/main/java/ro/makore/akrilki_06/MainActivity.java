package ro.makore.akrilki_06;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import ro.makore.akrilki_06.model.NewsItem;
import ro.makore.akrilki_06.parser.NewsParser;
import ro.makore.akrilki_06.api.NewsAPI;
import ro.makore.akrilki_06.adapter.NewsAdapter;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar; // ProgressBar for loading indicator
    private TextView loadingText; // TextView for loading message

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);

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

    private void refreshNewsData() {
        
        // Show loading indicators
        runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            loadingText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        });

        new Thread(() -> {
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
            } catch (Exception e) {
                // Handle any other exceptions that might occur
                Log.e("NEWS06", "Unexpected error", e);
            }
        }).start();
    }
    
}
