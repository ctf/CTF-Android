package com.ctf.mcgill;

import android.app.Application;
import android.content.Context;

import okhttp3.OkHttpClient;

public class CTFApp extends Application {
    private static CTFApp mInstance;
    private static OkHttpClient httpClientInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        //todo using for network debug
        //Stetho.initializeWithDefaults(this);
    }

    public static CTFApp getInstance() {
        return mInstance;
    }

    public static OkHttpClient getHttpClient() {
        if (httpClientInstance == null) {
            httpClientInstance = new OkHttpClient.Builder()/*.addNetworkInterceptor(new StethoInterceptor())*/.build();
        }
        return httpClientInstance;
    }

    public static Context getAppContext() {
        return getInstance().getApplicationContext();
    }
}
