package com.ctf.mcgill.eventRequests;

import android.support.annotation.Nullable;

import com.ctf.mcgill.auth.AccountUtil;
import com.ctf.mcgill.enums.DataType;
import com.ctf.mcgill.requests.BaseTepidRequest;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Allan Wang on 28/12/2016.
 */

public class NicknameEventRequest extends BaseEventRequest<String> {

    @Override
    BaseTepidRequest<String> getRequest(String token, @Nullable Object extra) {
        return new NickRequest(token, getExtra(extra, String.class, "NicknameRequest must receive a nonnull and valid nickname string"));
    }

    @Override
    DataType.Single getDataType() {
        return DataType.Single.NICKNAME;
    }

    private static class NickRequest extends BaseTepidRequest<String> {

        private String token, nick, url;
        private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        NickRequest(String token, String nick) {
            super(String.class);
            this.token = token;
            this.nick = nick;
            this.url = baseUrl + "users/" + AccountUtil.getShortUser() + "/nick" + "";
        }

        @Override
        public String loadDataFromNetwork() throws Exception {

            // create a PUT request with the requested nick in json format
            Request request = new Request.Builder()
                    .header("Authorization", "Token " + token)
                    .url(url)
                    .put(RequestBody.create(JSON, nick))
                    .build();

            Response response = getOkHttpClient()
                    .newCall(request)
                    .execute();

            // TEPID responds to the request with a boolean indicating whether the nick was successfully changed or not
            Boolean isok = new JSONObject(response.body().string()).getBoolean("ok");

            if (!response.isSuccessful() || !isok) {
                throw new Exception("UH OH AN ERROR OCCURRED!!!!!!!!!");
            }

            return nick;
        }
    }
}
