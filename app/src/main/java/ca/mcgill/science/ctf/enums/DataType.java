package ca.mcgill.science.ctf.enums;

/**
 * Created by Allan Wang on 26/12/2016.
 * <p>
 * Contains possible Data Type enums as well as all their respective listeners. The request classes are separate in the requests/ folder
 */

public class DataType {
    /**
     * Sets of requests; usually reflects the requests of an entire fragment
     * Contains the list of individual inner requests that should be loaded on start
     */
    public enum Category {
        DASHBOARD(Single.QUOTA, Single.USER_JOBS, Single.QUEUES),
        MY_ACCOUNT(Single.QUOTA, Single.USER_JOBS), //TODO check if NICKNAME should be added
        ROOM_TAB(Single.DESTINATIONS, Single.ROOM_JOBS);

        private final Single[] content;

        Category(Single... ss) {
            content = ss;
        }

        public Single[] getContent() {
            return content;
        }
    }

//    private static final String KEY_QUOTA = "QUOTA", KEY_LAST_JOB = "LAST JOB", KEY_QUEUES = "QUEUES", KEY_DESTINATIONS = "DESTINATIONS";

    /**
     * Single unique request
     * Contains the request and listener retrieval
     * Beware of correcting casting
     */
    public enum Single {

        QUOTA, USER_JOBS, ROOM_JOBS, DESTINATIONS, QUEUES, NICKNAME, COLOR
    }
}
