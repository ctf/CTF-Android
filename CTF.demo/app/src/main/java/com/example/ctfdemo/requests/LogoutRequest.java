package com.example.ctfdemo.requests;

import okhttp3.Request;
import okhttp3.Response;

/**
 * request for TEPID to invalidate Session, to use before logout
 */
public class LogoutRequest extends BaseTepidRequest<Void> {

    private String token, url;

    public LogoutRequest(String token, String id) {
        super(Void.class);
        this.token = token;
        this.url = baseUrl + "sessions/" + id;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {

        // create the DELETE request with our session id in the url
        Request request = new Request.Builder()
                .header("Authorization", "Token " + token)
                .url(url)
                .delete()
                .build();

        // execute the request
        Response response = getOkHttpClient()
                .newCall(request)
                .execute();

        // check response status is between 200 & 300
        if (!response.isSuccessful()) {
            throw new Exception(String.valueOf(response.code())); // will be caught by a RequestListener.onRequestFailure
        }

        return null;
    }

}
