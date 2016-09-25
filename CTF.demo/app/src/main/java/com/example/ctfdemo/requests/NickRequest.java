package com.example.ctfdemo.requests;

import com.example.ctfdemo.auth.AccountUtil;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NickRequest extends BaseTepidRequest<String> {

    private static String url;
    private String token, nick;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public NickRequest(String token, String nick) {
        super(String.class);
        this.token = token;
        this.nick = nick;
        this.url = "https://tepid.sus.mcgill.ca:8443/tepid/users/" + AccountUtil.getUsername() + "/nick" + "";
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        Request request = new Request.Builder()
                .header("Authorization", "Token " + token)
                .url(url)
                .put(RequestBody.create(JSON, nick))
                .build();

        Response response = getOkHttpClient()
                .newCall(request)
                .execute();

        Boolean isok = !new JSONObject(response.body().string()).getBoolean("ok");
        if (!response.isSuccessful() || isok) {
            throw new Exception("UH OH AN ERROR OCCURRED!!!!!!!!!");
        }

        return nick;
    }

}
