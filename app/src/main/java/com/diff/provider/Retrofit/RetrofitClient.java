package com.diff.provider.Retrofit;

import com.diff.provider.DiffApplication;
import com.diff.provider.Models.AccessDetails;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Tranxit Technologies Pvt Ltd, Chennai
 */

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static Retrofit retrofitAcceptReject = null;

    public static Retrofit getLiveTrackingClient() {
        if (retrofit == null) {
            /*retrofit = new Retrofit.Builder()
                    .baseUrl(AccessDetails.serviceurl)
                    .build();*/

            Gson gson = new GsonBuilder().setLenient().create();

            retrofit = new Retrofit
                    .Builder()
                    .baseUrl(AccessDetails.serviceurl)
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        }
        return retrofit;
    }

    private static OkHttpClient getHttpClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient().newBuilder()
                .cache(new Cache(DiffApplication.getInstance().getCacheDir(), 10 * 1024 * 1024)) // 10 MB
                .connectTimeout(10, TimeUnit.MINUTES)
               // .addNetworkInterceptor(new AddHeaderInterceptor())
//                .addNetworkInterceptor(new StethoInterceptor())
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .build();
    }

    public static Retrofit getAcceptRejectClient() {
        if (retrofitAcceptReject == null) {
            retrofitAcceptReject = new Retrofit.Builder()
                    .baseUrl(AccessDetails.serviceurl)
                    .build();
        }
        return retrofitAcceptReject;
    }
}
