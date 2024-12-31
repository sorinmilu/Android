package ro.makore.akrilki_06.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class NewsItem implements Parcelable {
    private String title;
    private String body;
    private String thumbnailUrl;
    private String language;
    private String source;
    private String datetime;

    private List<String> concepts;

    // Unparceleable Constructor
    public NewsItem() {
    }

    // Parcelable constructor - aceasi ordine ca in metoda writeToParcel
    protected NewsItem(Parcel in) {
        title = in.readString();
        body = in.readString();
        thumbnailUrl = in.readString();
        language = in.readString();
        source = in.readString();
        datetime = in.readString();
        concepts = in.createStringArrayList();
    }

    // writeString trebuie date in fix aceeasi ordine ca in constructor
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(body);
        dest.writeString(thumbnailUrl);
        dest.writeString(language);
        dest.writeString(source);
        dest.writeString(datetime);
        dest.writeStringList(concepts);
    }

    @Override
    public int describeContents() {
        return 0;  // No special objects inside
    }

    // Parcelable CREATOR to help with deserialization
    public static final Creator<NewsItem> CREATOR = new Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };

     // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public List<String> getConcepts() {
        return concepts;
    }

    public void setConcepts(List<String> concepts) {
        this.concepts = concepts;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDateTime() {
        return datetime;
    }

    public void setDateTime(String datetime) {
        this.datetime = datetime;
    }

}