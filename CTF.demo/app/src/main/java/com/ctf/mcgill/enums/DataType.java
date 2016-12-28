package com.ctf.mcgill.enums;

import com.ctf.mcgill.events.LoadEvent;
import com.ctf.mcgill.requests.DestinationsRequest;
import com.ctf.mcgill.requests.JobsRequest;
import com.ctf.mcgill.requests.NickRequest;
import com.ctf.mcgill.requests.QueueRequest;
import com.ctf.mcgill.requests.QueuesRequest;
import com.ctf.mcgill.requests.QuotaRequest;
import com.ctf.mcgill.tepid.PrintJob;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pitchedapps.capsule.library.utils.EventUtils;

import java.util.List;
import java.util.Map;

import static com.ctf.mcgill.enums.DataType.Single.Destinations;
import static com.ctf.mcgill.enums.DataType.Single.Nickname;
import static com.ctf.mcgill.enums.DataType.Single.Queues;
import static com.ctf.mcgill.enums.DataType.Single.Quota;
import static com.ctf.mcgill.enums.DataType.Single.RoomJobs;
import static com.ctf.mcgill.enums.DataType.Single.UserJobs;

/**
 * Created by Allan Wang on 26/12/2016.
 * <p>
 * Contains possible Data Type enums as well as all their respective listeners. The request classes are separate in the requests/ folder
 */

public class DataType {
    /**
     * Sets of requests; usually reflects the requests of an entire fragment
     * Contains the list of individual inner requests necessary for that category
     */
    public enum Category {
        Dashboard(Quota, UserJobs, Destinations), //Destinations leads to Queues
        MyAccount(Quota, UserJobs, Nickname),
        RoomTab();

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
        /**
         * Quota request
         * Gets count for current user
         * Returns String
         */
        Quota(KEY_QUOTA) {
            @Override
            public SpiceRequest getRequest(String token, Object... extras) {
                return new QuotaRequest(token);
            }

            @Override
            public RequestListener getListener() {
                return new QuotaRequestListener();
            }
        },
        /**
         * UserJobs request
         * Gets list of print jobs for current user
         * Returns PrintJob[]
         */
        UserJobs(KEY_LAST_JOB) {
            @Override
            public SpiceRequest getRequest(String token, Object... extras) {
                return new JobsRequest(token);
            }

            @Override
            public RequestListener getListener() {
                return new UserJobsRequestListener();
            }
        },
        /**
         * RoomUJobs request
         * Gets list of print jobs for specified room (see extras enum)
         * Extras must contain the specified room enum
         * Returns PrintJob[]
         */
        RoomJobs(null) {
            @Override
            public SpiceRequest getRequest(String token, Object... extras) {
                if (extras == null || extras[0] == null || !(extras[0] instanceof Room))
                    throw new IllegalArgumentException("Room request must send a Room number (see enums)");
                Room room = (Room) extras[0];
                return new QueueRequest(token, room.getName());
            }

            @Override
            public RequestListener getListener() {
                return null;
            }
        },
        /**
         * Destination request
         * Returns Map<String, Destination>
         */
        Destinations(KEY_DESTINATIONS) {
            @Override
            public SpiceRequest getRequest(String token, Object... extras) {
                return new DestinationsRequest(token);
            }

            @Override
            public RequestListener getListener() {
                return new DestinationsRequestListener();
            }
        },
        /**
         * Queue request
         * returns List<PrintQueue>
         * //TODO add more documentation; this one is called only after Destinations
         */
        Queues(KEY_QUEUES) {
            @Override
            public SpiceRequest getRequest(String token, Object... extras) {
                return new QueuesRequest(token);
            }

            @Override
            public RequestListener getListener() {
                return new QueuesRequestListener();
            }
        },
        /**
         * Nickname request
         * Extras must contain a String specifying the desired name change
         * //TODO if necessary, add length/char check to see that nickname is appropriate
         * Returns String
         */
        Nickname(null) {
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

    /**
     * Post loadEvent marked as successful
     * Event will be received by all subscribed Activities and Fragments
     *
     * @param type Single DataType
     * @param data Castable object containing appropriate data
     */
    private static void postLoadEvent(DataType.Single type, Object data) {
        EventUtils.post(new LoadEvent(type, true, data));
    }

    /**
     * Post loadEvent marked as successful
     * Furthermore, it will only be run through RoomActivity (which will likely reformat and push to fragments)
     *
     * @param type Single DataType
     * @param data Castable object containing appropriate data
     */
    private static void postLoadEventActivityOnly(DataType.Single type, Object data) {
        EventUtils.post(new LoadEvent(type, true, data).activityOnly());
    }

    /**
     * Post loadEvent marked as unsuccessful
     *
     * @param type  Single DataType
     * @param error String detailing the error; will be put into event data object and logged
     */
    private static void postErrorEvent(DataType.Single type, String error) {
        EventUtils.post(new LoadEvent(type, false, error));
    }

    /*
     * All available listeners
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
        public void onRequestSuccess(PrintJob[] printJobs) {
            postLoadEvent(UserJobs, printJobs);
        }
    }

    private final class RoomJobsRequestListener implements RequestListener<PrintJob[]> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            postErrorEvent(RoomJobs, "Room jobs request failed...");
        }

        @Override
        public void onRequestSuccess(PrintJob[] printJobs) {
            postLoadEvent(RoomJobs, printJobs);
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
