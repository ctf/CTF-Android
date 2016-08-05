package com.example.ctfdemo.requests;

import com.octo.android.robospice.request.SpiceRequest;
import okhttp3.OkHttpClient;

/**
 * A simplified {@link SpiceRequest} that makes it even easier to use a
 * OkHttpClient.
 * @author SNI
 * @param <T>
 *            the result type of this request.
 */
public abstract class BaseTepidRequest<T> extends SpiceRequest<T> {

    private OkHttpClient okHttpClient;

    public BaseTepidRequest(Class<T> clazz) {
        super(clazz);
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
