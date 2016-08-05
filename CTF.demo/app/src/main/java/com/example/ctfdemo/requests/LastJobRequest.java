package com.example.ctfdemo.requests;

import com.example.ctfdemo.auth.AccountUtil;
import com.example.ctfdemo.tepid.PrintJob;
import com.google.gson.Gson;

import okhttp3.Request;
import okhttp3.Response;

public class LastJobRequest extends BaseTepidRequest<PrintJob> {

    private static final String url = "https://tepid.sus.mcgill.ca:8443/tepid/jobs/" + AccountUtil.getUserName() + "/";

    public LastJobRequest() {
        super(PrintJob.class);

    }

    @Override
    public PrintJob loadDataFromNetwork() throws Exception {
        Request request = new Request.Builder()
                .header("Authorization", "token " + AccountUtil.getAuthToken())
                .url(url)
                .build();

        Response response = getOkHttpClient()
                .newCall(request)
                .execute();

        if (!response.isSuccessful()) {
            throw new Exception("UH OH AN ERROR OCCURRED!!!!!!!!!");
        }

        return new Gson().fromJson(response.body().toString(), PrintJob.class);
    }

}
