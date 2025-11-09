package ro.makore.akrilki_08;

import android.app.Application;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import okhttp3.OkHttpClient;
import java.io.InputStream;

@GlideModule
public class WeatherApp extends AppGlideModule {
    @Override
    public void registerComponents(android.content.Context context, Glide glide, Registry registry) {
        // Use the same OkHttpClient that works for API calls
        OkHttpClient client = new OkHttpClient.Builder()
            .build();
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
    }
}

