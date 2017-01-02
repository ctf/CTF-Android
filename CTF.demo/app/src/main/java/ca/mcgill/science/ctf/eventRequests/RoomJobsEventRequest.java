package ca.mcgill.science.ctf.eventRequests;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.octo.android.robospice.SpiceManager;

import ca.mcgill.science.ctf.enums.DataType;
import ca.mcgill.science.ctf.enums.Room;
import ca.mcgill.science.ctf.requests.BaseTepidRequest;
import ca.mcgill.science.ctf.tepid.PrintJob;
import ca.mcgill.science.ctf.wrappers.RoomPrintJob;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Allan Wang on 28/12/2016.
 */

public class RoomJobsEventRequest extends BaseEventRequest<RoomPrintJob> {

    @Override
    public void execute(SpiceManager manager, Context context, String token, @Nullable Object extra) {
        Room room = getExtraNullable(extra, Room.class);
        if (room == null) {
            for (Room r : Room.values()) execute(manager, context, token, r); //Execute for each room
            return;
        }
        if (!manager.isStarted()) manager.start(context);
        manager.execute(getRequest(token, extra), getListener());
    }

    @Override
    BaseTepidRequest<RoomPrintJob> getRequest(String token, @Nullable Object extra) {
        return new RoomJobsRequest(token, getExtra(extra, Room.class, "RoomJobsRequest must have Room enum"));
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
