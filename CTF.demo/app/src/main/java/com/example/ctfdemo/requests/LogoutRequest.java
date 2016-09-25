package com.example.ctfdemo.requests;

import okhttp3.Request;
import okhttp3.Response;

public class LogoutRequest extends BaseTepidRequest<String> {

    private String url, token;

    public LogoutRequest(String token, String id) {
        super(String.class);
        this.token = token;
        this.url = "https://tepid.sus.mcgill.ca:8443/tepid/sessions/" + id;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        Request request = new Request.Builder()
                .header("Authorization", "Token " + token)
                .url(url)
                .delete()
                .build();

        Response response = getOkHttpClient()
                .newCall(request)
                .execute();

        if (!response.isSuccessful()) {
            throw new Exception(String.valueOf(response.code()));
        }

        return "";
    }

}
