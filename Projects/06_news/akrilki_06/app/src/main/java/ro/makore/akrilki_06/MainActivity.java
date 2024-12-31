package ro.makore.akrilki_06;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;

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

        // Fetch the news data
        // new Thread(() -> {
        //     try {
        //         String jsonResponse = NewsAPI.fetchNews(this);
        //         List<NewsItem> newsItems = NewsParser.parseNews(jsonResponse);

        //         // Update UI on the main thread
        //         runOnUiThread(() -> {
        //             newsAdapter = new NewsAdapter(MainActivity.this, newsItems);
        //             recyclerView.setAdapter(newsAdapter);
        //         });
        //     } catch (IOException e) {
        //         Log.e("MainActivity", "Error fetching news", e);
        //     }  catch (Exception e) {
        //         // Handle any other exceptions that might occur
        //         Log.e("MainActivity", "Unexpected error", e);
        //     }
        // }).start();
        refreshNewsData();
    }

    private void refreshNewsData() {
        new Thread(() -> {
            Log.v("NEWS06", "Try to retrieve news");
            try {
                String jsonResponse = NewsAPI.fetchNews(this);
                List<NewsItem> newsItems = NewsParser.parseNews(jsonResponse);

                int count = newsItems.size();
                Log.v("NEWS06", "Number of NewsItems: " + count);
                // Update UI on the main thread
                runOnUiThread(() -> {
                    if (newsAdapter == null) {
                        newsAdapter = new NewsAdapter(MainActivity.this, newsItems);
                        recyclerView.setAdapter(newsAdapter);
                    } else {
                        newsAdapter.updateData(newsItems);
                    }
                });
            } catch (IOException e) {
                Log.e("NEWS06", "Error fetching news", e);
            } catch (Exception e) {
                // Handle any other exceptions that might occur
                Log.e("NEWS06", "Unexpected error", e);
            }
        }).start();
    }
    
}
