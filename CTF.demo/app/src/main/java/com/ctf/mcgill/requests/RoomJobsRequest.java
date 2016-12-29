package com.ctf.mcgill.requests;

import com.ctf.mcgill.enums.Room;
import com.ctf.mcgill.tepid.PrintJob;
import com.ctf.mcgill.wrappers.RoomPrintJob;
import com.google.gson.Gson;

import okhttp3.Request;
import okhttp3.Response;

/**
 * get a list of jobs most recently printed on a specific queue (e.g., 1B16, 1B17, 1B18)
 */
public class RoomJobsRequest extends BaseTepidRequest<RoomPrintJob> {

    private String url;
    private String token;
    private final Room room;

    public RoomJobsRequest(String token, Room room) {
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
