package com.ctf.mcgill.eventRequests;

import android.support.annotation.Nullable;

import com.ctf.mcgill.auth.AccountUtil;
import com.ctf.mcgill.enums.DataType;
import com.ctf.mcgill.requests.BaseTepidRequest;
import com.ctf.mcgill.tepid.Destination;
import com.ctf.mcgill.tepid.PrintJob;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Allan Wang on 28/12/2016.
 */

public class DestinationEventRequest extends BaseEventRequest<HashMap> {

    @Override
    BaseTepidRequest<HashMap> getRequest(String token, @Nullable Object extra) {
        return new DestinationsRequest(token);
    }

    @Override
    DataType.Single getDataType() {
        return DataType.Single.DESTINATIONS;
    }

    /**
     * request a HashMap<UUID, Destination> from the tepid server,
     * each destination represents a printer in one of the labs
     */
    private static class DestinationsRequest extends BaseTepidRequest<HashMap> {

        private String token;
        private static final String url = baseUrl + "destinations/";

        DestinationsRequest(String token) {
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
            Type t = new TypeToken<HashMap<String, Destination>>() {
            }.getType();
            return new Gson().fromJson(response.body().string(), t);
        }
    }
}
