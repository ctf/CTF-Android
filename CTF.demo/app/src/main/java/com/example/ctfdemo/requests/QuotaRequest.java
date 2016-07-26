package com.example.ctfdemo.requests;

import com.example.ctfdemo.auth.AccountUtil;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.jackson.JacksonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

public class QuotaRequest extends GoogleHttpClientSpiceRequest<String> {

    private String url;

    public QuotaRequest() {
        super(String.class);
        url = "https://tepid.sus.mcgill.ca:8443/tepid/users/" + AccountUtil.getUserName() + "/quota/";

    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        HttpRequest request = getHttpRequestFactory().buildGetRequest(new GenericUrl(url)).setHeaders(new HttpHeaders().setAuthorization(AccountUtil.getAuthTokenHash()));
        request.setParser(new JacksonFactory().createJsonObjectParser());

        return request.execute().parseAs(getResultType());
    }
}
