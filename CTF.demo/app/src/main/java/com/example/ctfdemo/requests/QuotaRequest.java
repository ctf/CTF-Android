package com.example.ctfdemo.requests;

import com.example.ctfdemo.auth.AccountUtil;

import okhttp3.Request;
import okhttp3.Response;

public class QuotaRequest extends BaseTepidRequest<String> {

    private String url, token;

    public QuotaRequest(String token) {
        super(String.class);
        url = "https://tepid.sus.mcgill.ca:8443/tepid/users/" + AccountUtil.getUsername() + "/quota/";
        this.token = token;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        Request request = new Request.Builder()
                .header("Authorization", "Token "+token)
                .url(url)
                .build();

        Response response = getOkHttpClient()
                .newCall(request)
                .execute();

        if (!response.isSuccessful()) {
            throw new Exception("UH OH AN ERROR OCCURRED!!!!!!!!!");
        }

        return response.body().string();
    }

}
