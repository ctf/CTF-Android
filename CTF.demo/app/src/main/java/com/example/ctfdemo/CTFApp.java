package com.example.ctfdemo;

import android.app.Application;
import android.content.Context;

public class CTFApp extends Application {
    private static CTFApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static CTFApp getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return getInstance().getApplicationContext();
    }
}
