package ro.makore.akrilki_06;


import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.util.Log;


import ro.makore.akrilki_06.model.NewsItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.load.engine.GlideException;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.load.DataSource;

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

        if (newsItem != null) {
            titleTextView.setText(newsItem.getTitle());
            descriptionTextView.setText(newsItem.getBody());
            tagsTextView.setText(String.join(", ", newsItem.getConcepts()));

            Glide.with(NewsDetailActivity.this)
            .load(newsItem.getThumbnailUrl())
            .listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.e("Glide", "Image load failed", e);
                    return false; // Allow Glide to handle the error
                }
        
                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    dImageView.post(() -> {
                        // Get the intrinsic dimensions of the loaded image
                        int intrinsicWidth = resource.getIntrinsicWidth();
                        int intrinsicHeight = resource.getIntrinsicHeight();
        
                        // Get the width of the ImageView
                        int viewWidth = dImageView.getWidth();
        
                        // Calculate the proportional height based on the image aspect ratio
                        int viewHeight = (int) ((float) intrinsicHeight / intrinsicWidth * viewWidth);
        
                        // Update the ImageView layout parameters
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dImageView.getLayoutParams();
                        params.height = viewHeight;
                        dImageView.setLayoutParams(params);
                    });
                    return false; // Allow Glide to set the resource on the ImageView
                }
            })
            .into(dImageView);
        

        } else {
            titleTextView.setText("No news is good news");
            descriptionTextView.setText("isn't it");
        }

        backButton.setOnClickListener(v -> finish());
    }
}
