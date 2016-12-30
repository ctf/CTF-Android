package com.ctf.mcgill.eventRequests;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ctf.mcgill.enums.DataType;
import com.ctf.mcgill.requests.BaseTepidRequest;
import com.ctf.mcgill.tepid.Destination;
import com.ctf.mcgill.tepid.PrintQueue;
import com.ctf.mcgill.tepid.RoomInformation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pitchedapps.capsule.library.logging.CLog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Allan Wang on 28/12/2016.
 *
 * TODO see if there's a way of getting the data we want without reorganizing the data ourselves
 * As of now we aren't actually using the PrintQueue data so I'm not saving it
 */

public class RoomInfoEventRequest extends BaseEventRequest<ArrayList> {

    @Override
    BaseTepidRequest<ArrayList> getRequest(String token, @Nullable Object extra) {
        return new QueuesRequest(token, getExtra(extra, HashMap.class, "QueuesRequest must be given a nonnull Hashmap of the destinations"));
    }


    @Override
    DataType.Single getDataType() {
        return DataType.Single.QUEUES;
    }

    private static class QueuesRequest extends BaseTepidRequest<ArrayList> {

        private static String url = baseUrl + "queues/";
        private String token;
        private HashMap<String, Destination> destinations;

        QueuesRequest(String token, @NonNull HashMap<String, Destination> destinations) {
            super(ArrayList.class);
            this.token = token;
            this.destinations = destinations;
        }

        @Override
        public ArrayList<RoomInformation> loadDataFromNetwork() throws Exception {

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
            List<PrintQueue> queue = new Gson().fromJson(response.body().string(), t);

            //Convert PrintQueues to RoomInformations
            ArrayList<RoomInformation> roomInfos = new ArrayList<>();
            for (PrintQueue q : queue) {
                String name = q.name;
                RoomInformation roomInfo = new RoomInformation(name, true); //TODO put actual computer status
                for (String d : q.destinations) {
                    roomInfo.addPrinter(destinations.get(d).getName(), destinations.get(d).isUp());
                }
                roomInfos.add(roomInfo);
            }
            return roomInfos;
        }

    }

}
