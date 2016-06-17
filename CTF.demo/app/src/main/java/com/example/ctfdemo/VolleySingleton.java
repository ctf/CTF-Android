package com.example.ctfdemo;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * class to manage access to the volley request queue,
 * methods that make requests (POST, GET, etc) do so via
 * VolleySingleton.getInstance().getRequestQueue().add(request);
 */
public class VolleySingleton {
    private static VolleySingleton mInstance = null;
    private RequestQueue mRequestQueue;

    private VolleySingleton() {
        mRequestQueue = Volley.newRequestQueue(CTFApp.getAppContext());
    }

    public static VolleySingleton getInstance() {
        if (mInstance == null) {
            mInstance = new VolleySingleton();
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
