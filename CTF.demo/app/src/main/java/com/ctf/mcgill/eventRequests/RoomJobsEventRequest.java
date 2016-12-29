package com.ctf.mcgill.eventRequests;

import android.support.annotation.Nullable;

import com.ctf.mcgill.auth.AccountUtil;
import com.ctf.mcgill.enums.DataType;
import com.ctf.mcgill.enums.Room;
import com.ctf.mcgill.requests.BaseTepidRequest;
import com.ctf.mcgill.tepid.PrintJob;
import com.ctf.mcgill.wrappers.RoomPrintJob;
import com.google.gson.Gson;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Allan Wang on 28/12/2016.
 */

public class RoomJobsEventRequest extends BaseEventRequest<RoomPrintJob> {

    @Override
    BaseTepidRequest<RoomPrintJob> getRequest(String token, @Nullable Object extra) {
        return new RoomJobsRequest(token, getExtra(extra, Room.class, "RoomJobRequest must receive valid room enum"));
    }

    @Override
    DataType.Single getDataType() {
        return DataType.Single.ROOM_JOBS;
    }

    /**
     * get a list of jobs most recently printed on a specific queue (e.g., 1B16, 1B17, 1B18)
     */
    private static class RoomJobsRequest extends BaseTepidRequest<RoomPrintJob> {

        private String url;
        private String token;
        private final Room room;

        RoomJobsRequest(String token, Room room) {
            super(RoomPrintJob.class);
            this.token = token;
            this.room = room;
            url = baseUrl + "queues/" + room.getName() + "?limit=15";
        }

        @Override
        public RoomPrintJob loadDataFromNetwork() throws Exception {
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

            PrintJob[] jobs = new Gson().fromJson(response.body().string(), PrintJob[].class);

            return new RoomPrintJob(room, jobs);
        }

    }
}
