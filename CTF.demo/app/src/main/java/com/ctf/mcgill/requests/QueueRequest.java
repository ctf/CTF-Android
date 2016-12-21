package com.ctf.mcgill.requests;

import com.ctf.mcgill.tepid.PrintJob;
import com.google.gson.Gson;

import okhttp3.Request;
import okhttp3.Response;

/**
 * get a list of jobs most recently printed on a specific queue (e.g., 1B16, 1B17, 1B18)
 */
public class QueueRequest extends BaseTepidRequest<PrintJob[]> {

    private static String url;
    private String token;

    public QueueRequest(String token, String queue) {
        super(PrintJob[].class);
        this.token = token;
        url = baseUrl + "queues/" + queue + "?limit=15";
    }

    @Override
    public PrintJob[] loadDataFromNetwork() throws Exception {
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

        return new Gson().fromJson(response.body().string(), PrintJob[].class);
    }

}
