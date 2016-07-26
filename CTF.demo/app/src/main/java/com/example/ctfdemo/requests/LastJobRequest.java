package com.example.ctfdemo.requests;

import com.example.ctfdemo.auth.AccountUtil;
import com.example.ctfdemo.tepid.PrintJob;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.jackson.JacksonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

public class LastJobRequest extends GoogleHttpClientSpiceRequest<PrintJob> {

    private String url;

    public LastJobRequest() {
        super(PrintJob.class);
        url = "https://tepid.sus.mcgill.ca:8443/tepid/jobs/" + AccountUtil.getUserName() + "/";

    }

    @Override
    public PrintJob loadDataFromNetwork() throws Exception {
        HttpRequest request = getHttpRequestFactory().buildGetRequest(new GenericUrl(url)).setHeaders(new HttpHeaders().setAuthorization(AccountUtil.getAuthTokenHash()));
        request.setParser(new JacksonFactory().createJsonObjectParser());

        return request.execute().parseAs(getResultType());
    }

}
