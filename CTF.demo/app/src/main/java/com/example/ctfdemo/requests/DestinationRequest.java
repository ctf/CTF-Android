package com.example.ctfdemo.requests;

import com.example.ctfdemo.tepid.Destination;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

public class DestinationRequest extends BaseTepidRequest<Map> {

    private static String token, url;

    public DestinationRequest(String token) {
        super(Map.class);
        this.token = token;
        this.url = "https://tepid.sus.mcgill.ca:8443/tepid/destinations";
    }
    @Override
    public Map<String, Destination> loadDataFromNetwork() throws Exception {
        Request request = new Request.Builder()
                .header("Authorization", "Token " + token)
                .url(url)
                .build();

        Response response = getOkHttpClient()
                .newCall(request)
                .execute();

        GsonBuilder builder = new GsonBuilder();
        // Register an adapter to manage the date types as long values
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
        Gson gson = builder.create();

        String raw = response.body().string();
        Type t = new TypeToken<Map<String, Destination>>(){}.getType();
        Map<String, Destination> map = gson.fromJson(raw, t);
        return map;
    }
}
