package ro.makore.akrilki_06.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import ro.makore.akrilki_06.R;

import androidx.recyclerview.widget.RecyclerView;
import ro.makore.akrilki_06.model.NewsItem;

import ro.makore.akrilki_06.NewsDetailActivity;



import com.bumptech.glide.Glide;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final Context context;
    private final List<NewsItem> newsItemList;

    public NewsAdapter(Context context, List<NewsItem> newsItemList) {
        this.context = context;
        this.newsItemList = newsItemList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem newsItem = newsItemList.get(position);

        // Load thumbnail image using Glide
        Glide.with(context)
            .load(newsItem.getThumbnailUrl())
            .into(holder.thumbnailImageView);

        // Set title and body (short fragment)
        holder.titleTextView.setText(newsItem.getTitle());
        holder.bodyTextView.setText(newsItem.getBody().length() > 300 
            ? newsItem.getBody().substring(0, 300) + "..." 
            : newsItem.getBody());

        // Join tags from concepts list and display them
        List<String> limitedTags = newsItem.getConcepts().subList(0, Math.min(newsItem.getConcepts().size(), 5));
        String tags = String.join(", ", limitedTags);
        holder.tagsTextView.setText(tags);

        // Handle click event to pass the NewsItem to NewsDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NewsDetailActivity.class);
            if (newsItem != null) {
                intent.putExtra("news_item", newsItem); // Pass the NewsItem to the next activity
            }    
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return newsItemList.size();
    }

    // ViewHolder for each news item
    public static class NewsViewHolder extends RecyclerView.ViewHolder {

        private final ImageView thumbnailImageView;
        private final TextView titleTextView;
        private final TextView bodyTextView;
        private final TextView tagsTextView;

        public NewsViewHolder(View itemView) {
            super(itemView);

            // Initialize views
            thumbnailImageView = itemView.findViewById(R.id.thumbnail);
            titleTextView = itemView.findViewById(R.id.title);
            bodyTextView = itemView.findViewById(R.id.body);
            tagsTextView = itemView.findViewById(R.id.tags);
        }
    }
}