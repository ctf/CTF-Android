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

public class LastJobRequest extends BaseTepidRequest<String> {

    private static final String url = "https://tepid.sus.mcgill.ca:8443/tepid/jobs/" + AccountUtil.getUserName() + "/";
    private String token;

    public LastJobRequest(String token) {
        super(String.class);
        this.token = token;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
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

        PrintJob[] userJobs = gson.fromJson(response.body().string(), PrintJob[].class);
        Date mostRecent = userJobs[userJobs.length-1].getPrinted();
        Date current = new Date();
        return computeDifference(mostRecent, current);
    }

    private String computeDifference(Date leastRecent, Date mostRecent) {
        String unit = "", diff = "";
        int count = 0;

        if (leastRecent.getYear() == mostRecent.getYear()) {
            if (leastRecent.getMonth() == mostRecent.getMonth()) {
                if (leastRecent.getDate() == mostRecent.getDate()) {
                    if (leastRecent.getHours() == mostRecent.getHours()) {
                        if (leastRecent.getMinutes() == mostRecent.getMinutes()) {
                            count = 1;
                            unit = "minute";
                        } else {
                            count = mostRecent.getMinutes() - leastRecent.getMinutes();
                            unit = "minute";
                        }
                    } else {
                        count = mostRecent.getHours() - leastRecent.getHours();
                        unit = "hour";
                    }
                } else {
                    count = mostRecent.getDate() - leastRecent.getDate();
                    unit = "day";
                }
            } else {
                count = mostRecent.getMonth() - leastRecent.getMonth();
                unit = "month";
            }
        } else {
            count = mostRecent.getYear() - leastRecent.getYear();
            unit = "year";
        }

        diff += count + " " + unit + (count>1?"s ":" ") + "ago.";
        return diff;
    }

}
