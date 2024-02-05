package com.oruphones.nativediagnostic.webservices;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    public static final String KEY_PREFIX = "Basic ";
    public static Gson sGson;
    public static Retrofit getClient(String baseUrl) {
        String authToken = Credentials.basic("appstoreuser", "$ecr3T");
        return generateClient(baseUrl, authToken);
    }

    public static Retrofit getClient(String baseUrl, String key) {
        String authToken = KEY_PREFIX + key;
        return generateClient(baseUrl, authToken);
    }

    private static Retrofit generateClient(final String baseUrl, final String authToken) {
        Interceptor headerAuthorizationInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request request = chain.request();
                Headers headers = request.headers().newBuilder().add("Authorization", authToken).build();
                request = request.newBuilder().headers(headers).build();
                return chain.proceed(request);
            }
        };

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(headerAuthorizationInterceptor)
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    public static Gson getGson() {
        if(sGson==null){
            sGson=new Gson();
        }
        return sGson;
    }

}
