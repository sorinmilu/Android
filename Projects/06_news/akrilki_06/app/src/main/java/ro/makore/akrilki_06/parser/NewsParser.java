package ro.makore.akrilki_06.parser;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ro.makore.akrilki_06.model.NewsItem;

import java.util.ArrayList;
import java.util.List;
// import java.time.Instant;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.time.Duration;
// import java.time.ZoneId;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.Duration;
import org.threeten.bp.ZoneId;

public class NewsParser {

    public static List<NewsItem> parseNews(String jsonResponse) {
        List<NewsItem> newsItems = new ArrayList<>();
        Gson gson = new Gson();

        JsonArray articles = gson.fromJson(jsonResponse, JsonObject.class)
            .getAsJsonObject("articles")
            .getAsJsonArray("results");


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

            if (article.has("lang") && !article.get("lang").isJsonNull()) {
                item.setLanguage(article.get("lang").getAsString());
            } else {
                item.setLanguage(""); 
            }

            if (article.has("source") && !article.get("source").isJsonNull()) {
                JsonObject source = article.getAsJsonObject("source"); // Get the source object
                if (source.has("title") && !source.get("title").isJsonNull()) {
                    item.setSource(source.get("title").getAsString()); // Set title as language
                } else {
                    item.setSource(""); // Default value if title is missing
                }
            } else {
                item.setSource(""); 
            }
            
            if (article.has("dateTime") && !article.get("dateTime").isJsonNull()) {
                //datetime
                try {
                    String dateTime = article.get("dateTime").getAsString();    

                    // Parse the ISO 8601 dateTime string
                    Instant parsedInstant = Instant.parse(dateTime);
                    LocalDateTime parsedDateTime = LocalDateTime.ofInstant(parsedInstant, ZoneId.systemDefault());
        
                    // Format the date and time as "HH:mm yyyy-MM-dd"
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm yyyy-MM-dd");
                    String formattedDateTime = parsedDateTime.format(formatter);
        
                    // Calculate the difference between now and the parsed dateTime
                    Instant now = Instant.now();
                    Duration duration = Duration.between(parsedInstant, now);
        
                    // Determine the relative time string
                    String relativeTime;
                    long seconds = duration.getSeconds();
                    if (seconds < 120) { // Less than 2 minutes
                        relativeTime = (seconds < 60) ? "now" : "1 minute ago";
                    } else if (seconds < 3600) { // Less than 1 hour
                        relativeTime = (seconds / 60) + " minutes ago";
                    } else if (seconds < 86400) { // Less than 1 day
                        relativeTime = (seconds / 3600) + " hours ago";
                    } else { // More than 1 day
                        long days = seconds / 86400;
                        relativeTime = days + (days == 1 ? " day ago" : " days ago");
                    }
        
                    // Combine formatted dateTime with relative time
                    String result = formattedDateTime + " (" + relativeTime + ")";
        
                    // Set the result in the item
                    item.setDateTime(result);
                } catch (Exception e) {
                    // Handle parsing errors gracefully
                    item.setDateTime("Invalid dateTime");
                }
            } else {
                item.setDateTime("NO has datetime");
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

