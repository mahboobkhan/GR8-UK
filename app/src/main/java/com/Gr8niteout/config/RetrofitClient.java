package com.Gr8niteout.config;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    
    private static RetrofitClient instance = null;
    private Retrofit retrofit;
    
    private RetrofitClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);
        
        retrofit = new Retrofit.Builder()
                .baseUrl("https://gr8niteout.probrains.co/")  // Use the correct URL from Postman
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }
    
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }
    
    public Retrofit getRetrofit() {
        return retrofit;
    }
}
