package com.ctf.mcgill.eventRequests;

import android.content.Context;
import android.support.annotation.Nullable;

import com.ctf.mcgill.auth.AccountUtil;
import com.ctf.mcgill.enums.DataType;
import com.ctf.mcgill.requests.BaseTepidRequest;
import com.ctf.mcgill.tepid.Destination;
import com.ctf.mcgill.tepid.PrintJob;
import com.ctf.mcgill.tepid.PrintQueue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Allan Wang on 28/12/2016.
 */

public class QueueEventRequest extends BaseEventRequest<List> {

    private SpiceManager manager;
    private Context context;

    @Override
    public void execute(SpiceManager manager, Context context, String token, @Nullable Object extra) {
        if (!manager.isStarted()) manager.start(context);
        this.manager = manager;
        this.context = context;
        manager.execute(getRequest(token, extra), getListener());
    }

    @Override
    BaseTepidRequest<List> getRequest(String token, @Nullable Object extra) {
        return new QueuesRequest(token, manager, context);
    }

    @Override
    DataType.Single getDataType() {
        return DataType.Single.QUEUES;
    }

    private static class QueuesRequest extends BaseTepidRequest<List> {

        private static String url = baseUrl + "queues/";
        private String token;
        private SpiceManager manager;
        private Context context;

        QueuesRequest(String token, SpiceManager manager, Context context) {
            super(List.class);
            this.token = token;
            this.manager = manager;
            this.context = context;
        }

        @Override
        public List<PrintQueue> loadDataFromNetwork() throws Exception {
            new DestinationToQueueEventRequest(new RequestListener<HashMap>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {

                }

                @Override
                public void onRequestSuccess(HashMap hashMap) {

                }
            }).execute(manager, context, token, null);
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

            Type t = new TypeToken<List<PrintQueue>>() {
            }.getType(); // todo the param type get lost anyways when we return from the request, any way around this?
            return new Gson().fromJson(response.body().string(), t);
        }

    }

}
