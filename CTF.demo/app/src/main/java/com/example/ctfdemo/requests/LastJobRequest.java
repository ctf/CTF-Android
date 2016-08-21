package com.example.ctfdemo.requests;

import com.example.ctfdemo.auth.AccountUtil;
import com.example.ctfdemo.tepid.PrintJob;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

import okhttp3.Request;
import okhttp3.Response;

public class LastJobRequest extends BaseTepidRequest<PrintJob> {

    private static final String url = "https://tepid.sus.mcgill.ca:8443/tepid/jobs/" + AccountUtil.getUserName() + "/";
    private String token;

    public LastJobRequest(String token) {
        super(PrintJob.class);
        this.token = token;
    }

    @Override
    public PrintJob loadDataFromNetwork() throws Exception {
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

        String rawData = response.body().string();
        PrintJob[] userJobs = gson.fromJson(rawData, PrintJob[].class);
        return userJobs[userJobs.length-1];
    }

}
