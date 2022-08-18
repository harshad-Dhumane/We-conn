package com.mohitsprojects.instaclone.Retrotfit;

import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static Dispatcher dispatcher;

    private static Retrofit retrofit = null;
    public static final String BASE_URL = "http://wetap.in/scts/";
    public static final String URL_STATUS_LIST = "app_incidence_status_list";
    public static final String URL_INCIDENCE_STATUS = "app_create_Incidence_status";


    public static Retrofit getRetrofit(String BASE_URL) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(180, TimeUnit.SECONDS);
        httpClient.readTimeout(180, TimeUnit.SECONDS);
        httpClient.writeTimeout(180, TimeUnit.SECONDS);
        httpClient.addInterceptor(logging);
        Retrofit.Builder builder = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL);


        dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(2);
        httpClient.dispatcher(dispatcher);

        Retrofit retrofit = builder.client(httpClient.build()).build();
        Interface service = retrofit.create(Interface.class);
        return retrofit;
    }


    public static Interface getApiService() {
        return getRetroClient().create(Interface.class);
    }

    private static Retrofit getRetroClient() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}