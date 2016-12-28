package com.ctf.mcgill.enums;

import com.ctf.mcgill.R;
import com.ctf.mcgill.auth.AccountUtil;
import com.ctf.mcgill.events.LoadEvent;
import com.ctf.mcgill.requests.DestinationsRequest;
import com.ctf.mcgill.requests.JobsRequest;
import com.ctf.mcgill.requests.NickRequest;
import com.ctf.mcgill.requests.QueuesRequest;
import com.ctf.mcgill.requests.QuotaRequest;
import com.ctf.mcgill.tepid.PrintJob;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pitchedapps.capsule.library.event.SnackbarEvent;
import com.pitchedapps.capsule.library.logging.CLog;
import com.pitchedapps.capsule.library.utils.EventUtils;

import java.util.List;
import java.util.Map;

import static com.ctf.mcgill.enums.DataType.Single.Destinations;
import static com.ctf.mcgill.enums.DataType.Single.Nickname;
import static com.ctf.mcgill.enums.DataType.Single.Queues;
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
        Dashboard(Quota, UserJobs, Destinations), //Destinations leads to Queues
        MyAccount(Quota, UserJobs, Nickname);

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
     * Contains the cacheKey, request, and listener
     * If cacheKey is null, result is not cached
     * Beware of correcting casting
     */
    public enum Single {
        Quota(KEY_QUOTA) {
            @Override
            public SpiceRequest getRequest(String token, Object... extras) {
                return new QuotaRequest(token);
            }

            @Override
            public RequestListener getListener() {
                return new QuotaRequestListener();
            }
        }, UserJobs(KEY_LAST_JOB) {
            @Override
            public SpiceRequest getRequest(String token, Object... extras) {
                return new JobsRequest(token);
            }

            @Override
            public RequestListener getListener() {
                return new UserJobsRequestListener();
            }
        }, Destinations(KEY_DESTINATIONS) {
            @Override
            public SpiceRequest getRequest(String token, Object... extras) {
                return new DestinationsRequest(token);
            }

            @Override
            public RequestListener getListener() {
                return new DestinationsRequestListener();
            }
        }, Queues(KEY_QUEUES) {
            @Override
            public SpiceRequest getRequest(String token, Object... extras) {
                return new QueuesRequest(token);
            }

            @Override
            public RequestListener getListener() {
                return new QueuesRequestListener();
            }
        }, Nickname(null) {
            @Override
            public SpiceRequest getRequest(String token, Object... extras) {
                if (extras == null || extras[0] == null)
                    throw new IllegalArgumentException("Nickname request must send the nickname");
                return new NickRequest(token, String.valueOf(extras[0]));
            }

            @Override
            public RequestListener getListener() {
                return new NickRequestListener();
            }
        };

        private final String cacheKey;

        Single(String cacheKey) {
            this.cacheKey = cacheKey;
        }

        public String getCacheKey() {
            return cacheKey;
        }

        public abstract SpiceRequest getRequest(String token, Object... extras);

        public abstract RequestListener getListener();
    }

    private static void postLoadEvent(DataType.Single type, Object data) {
        EventUtils.post(new LoadEvent(type, true, data));
    }

    private static void postLoadEventActivityOnly(DataType.Single type, Object data) {
        EventUtils.post(new LoadEvent(type, true, data).activityOnly());
    }

    private static void postErrorEvent(DataType.Single type, String error) {
        EventUtils.post(new LoadEvent(type, false, error));
    }

    /*
     * Dashboard Listeners
     */

    private static class QuotaRequestListener implements RequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            postErrorEvent(Quota, "Quota request failed...");
        }

        @Override
        public void onRequestSuccess(String quota) {
            postLoadEvent(Quota, quota);
        }
    }

    private static class UserJobsRequestListener implements RequestListener<PrintJob[]> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            postErrorEvent(UserJobs, "User jobs request failed...");
        }

        @Override
        public void onRequestSuccess(PrintJob[] p) {
            postLoadEvent(UserJobs, p);
        }
    }

    private static class DestinationsRequestListener implements RequestListener<Map> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            postErrorEvent(Destinations, "Destinations request failed...");
        }

        @Override
        public void onRequestSuccess(Map map) {
            postLoadEvent(Destinations, map);
        }
    }

    //Called from within Destinations Request Listener
    private static class QueuesRequestListener implements RequestListener<List> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            postErrorEvent(Queues, "Queues request failed...");
        }

        @Override
        public void onRequestSuccess(List list) { //todo clean this up, e.g., getView() methods for each type of item that sets the correct params, do the same thing in room fragment
            postLoadEventActivityOnly(Queues, list);
        }
    }

    private static class NickRequestListener implements RequestListener<String> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            postErrorEvent(Nickname, "Nick request failed...");
        }

        @Override
        public void onRequestSuccess(String nick) {
            postLoadEvent(Nickname, nick);
        }
    }
}
