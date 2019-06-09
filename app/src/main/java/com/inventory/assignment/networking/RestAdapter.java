package com.inventory.assignment.networking;

import com.inventory.assignment.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestAdapter {

    public static ApiService createAPIWithUrl(String url) {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(httpLoggingInterceptor);
        }

        builder.cache(null);
        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).client(client).build();

        return retrofit.create(ApiService.class);

    }
}
