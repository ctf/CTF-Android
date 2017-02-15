package ca.mcgill.science.ctf.enums

/**
 * Created by Allan Wang on 26/12/2016.
 *
 * Contains possible Data Type enums as well as all their respective listeners. The request classes are separate in the requests/ folder
 */
class DataType {
    /**
     * Sets of requests; usually reflects the requests of an entire fragment
     * Contains the list of individual inner requests that should be loaded on start
     */
    enum class Category(vararg ss: Single) {
        DASHBOARD(Single.QUOTA, Single.USER_JOBS, Single.QUEUES),
        MY_ACCOUNT(Single.QUOTA, Single.USER_JOBS), //TODO check if NICKNAME should be added
        ROOM_TAB(Single.DESTINATIONS, Single.ROOM_JOBS);

        val content: Array<out Single> = ss
    }

    /**
     * Single unique request
     * Contains the request and listener retrieval
     * Beware of correcting casting
     */
    enum class Single {
        QUOTA, USER_JOBS, ROOM_JOBS, DESTINATIONS, QUEUES, NICKNAME, COLOR
    }
}
