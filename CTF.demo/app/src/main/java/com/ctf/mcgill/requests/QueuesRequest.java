package com.ctf.mcgill.requests;

import com.ctf.mcgill.tepid.PrintQueue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class QueuesRequest extends BaseTepidRequest<List> {

    private static String url = baseUrl + "queues/";
    private String token;

    public QueuesRequest(String token) {
        super(List.class);
        this.token = token;
    }

    @Override
    public List<PrintQueue> loadDataFromNetwork() throws Exception {
        Request request = new Request.Builder()
                .header("Authorization", "Token " + token)
                .url(url)
                .build();

        Response response = getOkHttpClient()
                .newCall(request)
                .execute();

        if (!response.isSuccessful()) {
            throw new Exception("UH OH AN ERROR OCCURRED!!!!!!!!!");
        }

        Type t = new TypeToken<List<PrintQueue>>(){}.getType(); // todo the param type get lost anyways when we return from the request, any way around this?
        return new Gson().fromJson(response.body().string(), t);
    }

}
