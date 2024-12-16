package ro.makore.akrilki_06.parser;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ro.makore.akrilki_06.model.NewsItem;

import java.util.ArrayList;
import java.util.List;

public class NewsParser {
    public static List<NewsItem> parseNews(String jsonResponse) {
        List<NewsItem> newsItems = new ArrayList<>();
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
        JsonArray articles = jsonObject.getAsJsonObject("articles").getAsJsonArray("results");

        for (int i = 0; i < articles.size(); i++) {
            JsonObject article = articles.get(i).getAsJsonObject();
            NewsItem item = new NewsItem();
            if (article.has("title") && !article.get("title").isJsonNull()) {
                item.setTitle(article.get("title").getAsString());
            } else {
                item.setTitle("Untitled article"); // Fallback to an empty string
            }

            if (article.has("body") && !article.get("body").isJsonNull()) {
                item.setBody(article.get("body").getAsString());
            } else {
                item.setBody("no body content for the newsitem"); // Fallback to an empty string
            }    

            if (article.has("image") && !article.get("image").isJsonNull()) {
                item.setThumbnailUrl(article.get("image").getAsString());
            } else {
                item.setThumbnailUrl(""); // Fallback to an empty string
            }

            // Parse concepts
            if (article.has("concepts") && article.get("concepts").isJsonArray()) {
                JsonArray concepts = article.getAsJsonArray("concepts");
                List<String> conceptsList = new ArrayList<>();
                for (int j = 0; j < concepts.size(); j++) {
                    JsonObject concept = concepts.get(j).getAsJsonObject();
                    if (concept.has("label")) {
                        JsonObject label = concept.getAsJsonObject("label");
                        if (label.has("eng")) {
                            conceptsList.add(label.get("eng").getAsString());
                        }
                    }
                }
                item.setConcepts(conceptsList);
            } else {
                item.setConcepts(new ArrayList<>()); // Fallback to an empty list
            }
            newsItems.add(item);
        }

        return newsItems;
    }
}
