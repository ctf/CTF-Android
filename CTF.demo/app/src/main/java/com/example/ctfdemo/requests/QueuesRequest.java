package com.example.ctfdemo.requests;

import com.example.ctfdemo.tepid.PrintQueue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class QueuesRequest extends BaseTepidRequest<List> {

    private static String url;
    private String token;

    public QueuesRequest(String token) {
        super(List.class);
        this.token = token;
        this.url = "https://tepid.sus.mcgill.ca:8443/tepid/queues/";
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

        GsonBuilder builder = new GsonBuilder();
        // Register an adapter to manage the date types as long values
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
        Gson gson = builder.create();
        Type t = new TypeToken<List<PrintQueue>>(){}.getType();

        return gson.fromJson(response.body().string(), t);
    }

}
