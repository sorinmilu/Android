package ro.makore.akrilki_06;


import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;

import ro.makore.akrilki_06.model.NewsItem;


import androidx.appcompat.app.AppCompatActivity;

public class NewsDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView tagsTextView;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        tagsTextView = findViewById(R.id.tagsTextView);
        backButton = findViewById(R.id.backButton);

        // NewsItem newsItem = (NewsItem) getIntent().getSerializableExtra("news_item");
        NewsItem newsItem = getIntent().getParcelableExtra("news_item");
        if (newsItem != null) {
            titleTextView.setText(newsItem.getTitle());
            descriptionTextView.setText(newsItem.getBody());
            tagsTextView.setText(String.join(", ", newsItem.getConcepts()));
        } else {
            titleTextView.setText("No news is good news");
            descriptionTextView.setText("isn't it");
        }

        backButton.setOnClickListener(v -> finish());
    }
}
