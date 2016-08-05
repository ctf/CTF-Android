package com.example.ctfdemo.requests;

import com.example.ctfdemo.tepid.Session;
import com.example.ctfdemo.tepid.SessionRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginRequest extends BaseTepidRequest<Session> {

    private static final String url = "https://tepid.sus.mcgill.ca:8443/tepid/sessions/";
    private String username, password;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public LoginRequest(String username, String password) {
        super(Session.class);
        this.username = username;
        this.password = password;
    }

    @Override
    public Session loadDataFromNetwork() throws Exception {

        SessionRequest sr = new SessionRequest()
                .withUsername(username)
                .withPassword(password)
                .withPermanent(true)
                .withPersistent(true);

        RequestBody body = RequestBody.create(JSON, new Gson().toJson(sr));

        Request request= new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = getOkHttpClient()
                .newCall(request)
                .execute();

        if (!response.isSuccessful()) {
            throw new Exception("UH OH AN ERROR OCCURRED!!!!!!!!!");
        }

        GsonBuilder builder = new GsonBuilder();
        // Register an adapter to manage the date types as long values
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
        Gson gson = builder.create();

        Session session = gson.fromJson(response.body().string(), Session.class);

        return session;
    }
}
