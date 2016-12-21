package com.ctf.mcgill.requests;

import com.ctf.mcgill.auth.AccountUtil;
import com.ctf.mcgill.tepid.PrintJob;
import com.google.gson.Gson;

import okhttp3.Request;
import okhttp3.Response;

/**
 * request to get a list of user's jobs from TEPID
 */
public class JobsRequest extends BaseTepidRequest<PrintJob[]> {

    private static final String url = baseUrl + "jobs/" + AccountUtil.getShortUser() + "/";
    private String token;

    public JobsRequest(String token) {
        super(PrintJob[].class);
        this.token = token;
    }

    @Override
    public PrintJob[] loadDataFromNetwork() throws Exception {

        // create a GET request with our auth token to the specified url
        Request request = new Request.Builder()
                .header("Authorization", "Token " + token)
                .url(url)
                .build();

        // execute the request
        Response response = getOkHttpClient()
                .newCall(request)
                .execute();

        // check response status code is between 200 & 300
        if (!response.isSuccessful()) {
            throw new Exception("UH OH AN ERROR OCCURRED!!!!!!!!!"); // will be caught by a RequestListener.onRequestFailure
        }

        // deserialize and return TEPID response
        return new Gson().fromJson(response.body().string(), PrintJob[].class);
    }

}
