package ca.mcgill.science.ctf;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class CTFApp extends Application {
    private static CTFApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mInstance = this;

        //todo using for network debug
        //Stetho.initializeWithDefaults(this);
    }

    public static CTFApp getInstance() {
        return mInstance;
    }

    //TODO add tepid api here as a singleton?

    public static Context getAppContext() {
        return getInstance().getApplicationContext();
    }
}
