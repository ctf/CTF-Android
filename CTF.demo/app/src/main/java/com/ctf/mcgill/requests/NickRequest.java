package com.ctf.mcgill.requests;

import com.ctf.mcgill.auth.AccountUtil;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NickRequest extends BaseTepidRequest<String> {

    private String token, nick, url;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public NickRequest(String token, String nick) {
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
