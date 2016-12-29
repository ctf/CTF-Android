package com.ctf.mcgill.eventRequests;

import com.ctf.mcgill.tepid.Destination;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.octo.android.robospice.request.SpiceRequest;

import java.lang.reflect.Type;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simplified {@link SpiceRequest} that makes it even easier to use a
 * OkHttpClient. Also the base class for robospice requests to the TEPID server.
 *
 * @param <T> the result type of this request.
 */
public abstract class BaseEventTepidRequest<T> extends SpiceRequest<T> {

    private OkHttpClient okHttpClient;
    protected static final String baseUrl = "https://tepid.sus.mcgill.ca:8443/tepid/";
    private final String token;

    public BaseEventTepidRequest(String token, Class<T> clazz) {
        super(clazz);
        this.token = token;
    }

    private void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    private OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    abstract String getUrl();

    @Override
    public T loadDataFromNetwork() throws Exception {

        // Build a GET request with our auth token to ask TEPID for the destinations
        Request request = new Request.Builder()
                .header("Authorization", "Token " + token)
                .url(baseUrl + getUrl())
                .build();

        // execute the request
        Response response = getOkHttpClient()
                .newCall(request)
                .execute();

        // check the status code of TEPID's response is between 200 & 300
        if (!response.isSuccessful()) {
            throw new Exception(String.valueOf(response.code()));
        }

        // deserialize and return TEPID's response
        Type t = new TypeToken<HashMap<String, Destination>>() {
        }.getType();
        return new Gson().fromJson(response.body().string(), t);
    }

}
