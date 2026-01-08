package ro.makore.akrilki_06.api;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import org.json.JSONObject;

public class NewsAPI {

//apiKey=9506e4f0-519a-4c9b-87b4-0c4ca8009126

//    private static final String API_URL = "https://newsapi.org/v2/top-headlines?language=en&pageSize=10&apiKey=908261f1c43545299e4b6019be14c62a";

//    private static final String API_URL = "https://eventregistry.org/api/v1/article/getArticles?query=%7B%22%24query%22%3A%7B%22lang%22%3A%22eng%22%7D%2C%22%24filter%22%3A%7B%22forceMaxDataTimeWindow%22%3A%2231%22%2C%22dataType%22%3A%5B%22news%22%2C%22blog%22%5D%2C%22isDuplicate%22%3A%22skipDuplicates%22%7D%7D&resultType=articles&articlesSortBy=date&includeArticleConcepts=true&includeArticleImage=true&apiKey=9506e4f0-519a-4c9b-87b4-0c4ca8009126&articlesPage=1&articlesCount=10";

    private static final String API_URL = "https://eventregistry.org/api/v1/article/getArticles";
//    private static final String API_URL = "https://eventregistry.org/api/v1/article/getArticles?query=%7B%22%24query%22%3A%7B%22%24and%22%3A%5B%7B%22keyword%22%3A%22AI%20%5C%22Artificial%20intelligence%5C%22%20%5C%22machine%20learning%5C%22%20%5C%22deep%20learning%5C%22%20chatgpt%20%5C%22generative%20AI%5C%22%22%2C%22keywordSearchMode%22%3A%22simple%22%7D%2C%7B%22categoryUri%22%3A%22news%2FTechnology%22%7D%5D%7D%2C%22%24filter%22%3A%7B%22forceMaxDataTimeWindow%22%3A%2231%22%7D%7D&resultType=articles&articlesSortBy=rel&apiKey=9506e4f0-519a-4c9b-87b4-0c4ca8009126";


    private static String readJsonFromAssets(Context context, String fileName) throws IOException {
        InputStream is = context.getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, "UTF-8");
    }

    public static String fetchNews(Context context) throws Exception {
        OkHttpClient client = new OkHttpClient();

        // Read the JSON body from the assets folder and the API key
        String jsonBody = readJsonFromAssets(context, "request_body.json");
        String apiKeyJson = readJsonFromAssets(context, "api_key.json");

        // Parse them into JSON objects
        JSONObject requestBody = new JSONObject(jsonBody);
        JSONObject apiKeyObject = new JSONObject(apiKeyJson);

        // Add the API key dynamically
        requestBody.put("apiKey", apiKeyObject.getString("apiKey"));

        // Convert updated JSON object back to a string
        jsonBody = requestBody.toString();

        // Define the media type for JSON
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        // RequestBody with JSON data
        RequestBody body = RequestBody.create(JSON, jsonBody);
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
