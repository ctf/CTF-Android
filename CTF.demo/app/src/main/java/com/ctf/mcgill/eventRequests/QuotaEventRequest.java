package com.ctf.mcgill.eventRequests;

import android.support.annotation.Nullable;

import com.ctf.mcgill.auth.AccountUtil;
import com.ctf.mcgill.enums.DataType;
import com.ctf.mcgill.requests.BaseTepidRequest;

import okhttp3.Request;
import okhttp3.Response;

public class QuotaEventRequest extends BaseEventRequest<String> {

    @Override
    BaseTepidRequest<String> getRequest(String token, @Nullable Object extra) {
        return new QuotaRequest(token);
    }

    @Override
    DataType.Single getDataType() {
        return DataType.Single.QUOTA;
    }

    private static class QuotaRequest extends BaseTepidRequest<String> {
        private String token, url = baseUrl + "users/" + AccountUtil.getShortUser() + "/quota/";

        QuotaRequest(String token) {
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

            return response.body().string();
        }
    }

}
