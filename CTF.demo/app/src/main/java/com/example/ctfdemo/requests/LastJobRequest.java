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
        long mostRecent = userJobs[userJobs.length-1].getPrinted().getTime();
        long current = new Date().getTime();
        return computeDifference(current - mostRecent);
    }

    private String computeDifference(long difference) {
        int count = 0;
        String unit = "";
        difference = Math.abs(difference);

        if (difference > 3600000) {
            if (difference > 86400000) {
                if (difference > 6.048e+8) {
                    if (difference > 2.628e+9) {
                        if (difference > 3.154e+10) {
                            count = (int) (difference / 3.154e+10);
                            unit = "year";
                        } else {
                            count = (int) (difference / 2.628e+9);
                            unit = "month";
                        }
                    } else {
                        count = (int) (difference / 6.048e+8);
                        unit = "week";
                    }
                } else {
                    count = (int) (difference / 86400000);
                    unit = "week";
                }
            } else {
                count = (int) (difference / 3600000);
                unit = "hour";
            }
        } else {
            count = (int) (difference / 60000);
            unit = "minute";
        }

        return "" + count + " " + unit + ((count > 1)?"s":"") + " ago.";
    }

}
