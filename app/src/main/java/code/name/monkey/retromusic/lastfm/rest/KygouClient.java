package code.name.monkey.retromusic.lastfm.rest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;

import code.name.monkey.retromusic.Constants;
import code.name.monkey.retromusic.lastfm.rest.service.KuGouApiService;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hemanths on 23/08/17.
 */

public class KygouClient {
    public static final String BASE_URL = Constants.BASE_API_URL_KUGOU;

    private KuGouApiService apiService;

    public KygouClient(@NonNull Context context) {
        this(createDefaultOkHttpClientBuilder(context).build());
    }

    public KygouClient(@NonNull Call.Factory client) {
        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .callFactory(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        apiService = restAdapter.create(KuGouApiService.class);
    }

    @Nullable
    public static Cache createDefaultCache(Context context) {
        File cacheDir = new File(context.getCacheDir().getAbsolutePath(), "/okhttp-lastfm/");
        if (cacheDir.mkdirs() || cacheDir.isDirectory()) {
            return new Cache(cacheDir, 1024 * 1024 * 10);
        }
        return null;
    }

    public static Interceptor createCacheControlInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request modifiedRequest = chain.request().newBuilder()
                        .addHeader("Cache-Control", String.format("max-age=%d, max-stale=%d", 31536000, 31536000))
                        .build();
                return chain.proceed(modifiedRequest);
            }
        };
    }

    public static OkHttpClient.Builder createDefaultOkHttpClientBuilder(Context context) {
        return new OkHttpClient.Builder()
                .cache(createDefaultCache(context))
                .addInterceptor(createCacheControlInterceptor());
    }

    public KuGouApiService getApiService() {
        return apiService;
    }
}
