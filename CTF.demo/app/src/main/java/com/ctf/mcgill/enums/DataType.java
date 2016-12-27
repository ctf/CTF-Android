package com.ctf.mcgill.enums;

import android.support.v7.widget.LinearLayoutCompat;
import android.widget.Toast;

import com.ctf.mcgill.R;
import com.ctf.mcgill.events.LoadEvent;
import com.ctf.mcgill.fragments.DashboardFragment;
import com.ctf.mcgill.requests.DestinationsRequest;
import com.ctf.mcgill.requests.JobsRequest;
import com.ctf.mcgill.requests.QueuesRequest;
import com.ctf.mcgill.requests.QuotaRequest;
import com.ctf.mcgill.tepid.PrintJob;
import com.ctf.mcgill.tepid.PrintQueue;
import com.ctf.mcgill.tepid.RoomInformation;
import com.ocpsoft.pretty.time.PrettyTime;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pitchedapps.capsule.library.utils.AnimUtils;
import com.pitchedapps.capsule.library.utils.EventUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.ctf.mcgill.enums.DataType.Single.Destinations;
import static com.ctf.mcgill.enums.DataType.Single.Quota;
import static com.ctf.mcgill.enums.DataType.Single.UserJobs;

/**
 * Created by Allan Wang on 26/12/2016.
 */

public class DataType {
    /**
     * Sets of requests; usually reflects the requests of an entire fragment
     */
    public enum Category {
        Dashboard(Quota, UserJobs, Destinations);

        private final Single[] content;

        Category(Single... ss) {
            content = ss;
        }

        public Single[] getContent() {
            return content;
        }
    }

    private static final String KEY_QUOTA = "QUOTA", KEY_LAST_JOB = "LAST JOB", KEY_QUEUES = "QUEUES", KEY_DESTINATIONS = "DESTINATIONS";

    /**
     * Single unique request
     */
    public enum Single {
        Quota(KEY_QUOTA) {
            @Override
            public SpiceRequest getRequest(String token) {
                return new QuotaRequest(token);
            }

            @Override
            public RequestListener getListener() {
                return new QueuesRequestListener();
            }
        }, UserJobs(KEY_LAST_JOB) {
            @Override
            public SpiceRequest getRequest(String token) {
                return new JobsRequest(token);
            }

            @Override
            public RequestListener getListener() {
                return new UserJobsRequestListener();
            }
        }, Destinations(KEY_DESTINATIONS) {
            @Override
            public SpiceRequest getRequest(String token) {
                return new DestinationsRequest(token);
            }

            @Override
            public RequestListener getListener() {
                return new DestinationsRequestListener();
            }
        };

        private final String cacheKey;

        Single(String cacheKey) {
            this.cacheKey = cacheKey;
        }

        public String getCacheKey() {
            return cacheKey;
        }

        public abstract SpiceRequest getRequest(String token);

        public abstract RequestListener getListener();
    }

    private static void postLoadEvent(DataType type, boolean isSuccessful, Object data) {
        EventUtils.post(new LoadEvent(type, isSuccessful, data));
    }

    private static class QuotaRequestListener implements RequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            //todo improve error handling, maybe an "error fragment" w/ sadcat?
            Toast.makeText(getActivity(), "Quota request failed...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(String quota) {
            quotaView.setText(getString(R.string.dashboard_quota_text, quota));
            AnimUtils.fadeIn(getContext(), quotaView, 0, 1000);
        }
    }

    private static class UserJobsRequestListener implements RequestListener<PrintJob[]> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "User jobs request failed...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(PrintJob[] p) {
            Date last = p[0].started;
            PrettyTime pt = new PrettyTime(); //todo if date is null pt uses current time
            lastJobView.setText(getString(R.string.dashboard_last_job_text, pt.format(last)));
            AnimUtils.fadeIn(getContext(), lastJobView, 0, 1000);
        }
    }

    private static class DestinationsRequestListener implements RequestListener<Map> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Destinations request failed...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(Map map) {
            destinations = map;
            requestManager.execute(new QueuesRequest(token), KEY_QUEUES, DurationInMillis.ONE_MINUTE, new DashboardFragment.QueuesRequestListener());
        }
    }

    //Called from within Destinations Request Listener
    private static class QueuesRequestListener implements RequestListener<List> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Queues request failed...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(List list) { //todo clean this up, e.g., getView() methods for each type of item that sets the correct params, do the same thing in room fragment
            List<RoomInformation> rooms = new ArrayList<>();
            for (Object q : list) {
                String name = ((PrintQueue) q).name;
                RoomInformation roomInfo = new RoomInformation(name, true); //TODO put actual computer status

                if (destinations != null) {
                    for (String d : ((PrintQueue) q).destinations) {
                        roomInfo.addPrinter(destinations.get(d).getName(), destinations.get(d).isUp());
                    }
                }
                rooms.add(roomInfo);
            }
            cAdapter.updateList(rooms);
        }
    }
}
