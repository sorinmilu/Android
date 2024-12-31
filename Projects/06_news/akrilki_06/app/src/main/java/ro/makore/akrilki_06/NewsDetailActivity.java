package ro.makore.akrilki_06;


import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.util.Log;


import ro.makore.akrilki_06.model.NewsItem;
import com.bumptech.glide.Glide;


import androidx.appcompat.app.AppCompatActivity;

public class NewsDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView tagsTextView;
    private ImageView dImageView;    
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        dImageView = findViewById(R.id.dimageView);
        tagsTextView = findViewById(R.id.tagsTextView);
        backButton = findViewById(R.id.backButton);

        // NewsItem newsItem = (NewsItem) getIntent().getSerializableExtra("news_item");
        NewsItem newsItem = getIntent().getParcelableExtra("news_item");

        dImageView.post(new Runnable() {
            @Override
            public void run() {
                // Get the width of the ImageView (after layout is done)
                int width = dImageView.getWidth();
        
                // Set the height to match the width (1:1 aspect ratio)
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dImageView.getLayoutParams();
                params.height = width;
                dImageView.setLayoutParams(params);
            }
        });

        if (newsItem != null) {
            titleTextView.setText(newsItem.getTitle());
            descriptionTextView.setText(newsItem.getBody());
            tagsTextView.setText(String.join(", ", newsItem.getConcepts()));
            Log.v("THUMBNAILURL: ", newsItem.getThumbnailUrl());
            Log.v("LANGUAGE: ", newsItem.getLanguage());
            Log.v("Title: ", newsItem.getTitle());            
            Glide.with(NewsDetailActivity.this)
            .load(newsItem.getThumbnailUrl())
            .into(dImageView);    
        } else {
            titleTextView.setText("No news is good news");
            descriptionTextView.setText("isn't it");
        }

        backButton.setOnClickListener(v -> finish());
    }
}
