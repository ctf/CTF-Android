package ca.mcgill.science.ctf.eventRequests;

import android.support.annotation.Nullable;

import com.google.gson.Gson;

import ca.mcgill.science.ctf.auth.AccountUtil;
import ca.mcgill.science.ctf.enums.DataType;
import ca.mcgill.science.ctf.requests.BaseTepidRequest;
import ca.mcgill.science.ctf.tepid.PrintJob;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Allan Wang on 28/12/2016.
 */

public class UserJobsEventRequest extends BaseEventRequest<PrintJob[]> {

    @Override
    BaseTepidRequest<PrintJob[]> getRequest(String token, @Nullable Object extra) {
        return new UserJobsRequest(token);
    }

    @Override
    DataType.Single getDataType() {
        return DataType.Single.USER_JOBS;
    }

    /**
     * request to get a list of user's jobs from TEPID
     */
    private static class UserJobsRequest extends BaseTepidRequest<PrintJob[]> {

        private static final String url = baseUrl + "jobs/" + AccountUtil.getShortUser() + "/";
        private String token;

         UserJobsRequest(String token) {
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
}
