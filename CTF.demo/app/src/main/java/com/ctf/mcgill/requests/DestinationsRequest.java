package com.ctf.mcgill.requests;

import com.ctf.mcgill.tepid.Destination;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

/**
 * request a HashMap<UUID, Destination> from the tepid server,
 * each destination represents a printer in one of the labs
 */
public class DestinationsRequest extends BaseTepidRequest<HashMap> {

    private String token;
    private static String url = baseUrl + "destinations/";

    public DestinationsRequest(String token) {
        super(HashMap.class);
        this.token = token;
    }

    @Override
    public HashMap<String, Destination> loadDataFromNetwork() throws Exception {

        // Build a GET request with our auth token to ask TEPID for the destinations
        Request request = new Request.Builder()
                .header("Authorization", "Token " + token)
                .url(url)
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
        Type t = new TypeToken<HashMap<String, Destination>>(){}.getType();
        return new Gson().fromJson(response.body().string(), t);
    }
}
