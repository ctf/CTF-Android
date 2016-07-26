package com.example.ctfdemo.requests;

import com.example.ctfdemo.tepid.Session;
import com.example.ctfdemo.tepid.SessionRequest;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson.JacksonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

public class AuthRequest extends GoogleHttpClientSpiceRequest<Session> {

    private String url, username, password;

    public AuthRequest(String username, String password) {
        super(Session.class);
        this.username = username;
        this.password = password;
        url = "https://tepid.sus.mcgill.ca:8443/tepid/sessions/";
    }

    @Override
    public Session loadDataFromNetwork() throws Exception {
        SessionRequest sr = new SessionRequest()
                .withUsername(username)
                .withPassword(password)
                .withPersistent(true)
                .withPermanent(true);

/*        HashMap<String, String> sr = new HashMap<String, String>();
        sr.put("username", username);
        sr.put("password", password);
        sr.put("persistent", "true");
        sr.put("permanent", "true");*/

        HttpContent content = new JsonHttpContent(new JacksonFactory(), sr);
        HttpRequest request = getHttpRequestFactory().buildPostRequest(new GenericUrl(url), content);
        request.setParser(new JacksonFactory().createJsonObjectParser());

        return request.execute().parseAs(getResultType());
    }
}
