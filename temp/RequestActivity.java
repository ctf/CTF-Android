package ca.mcgill.science.ctf;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.octo.android.robospice.SpiceManager;
import ca.allanwang.capsule.library.activities.CapsuleActivityFrame;
import ca.allanwang.capsule.library.logging.CLog;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

import ca.mcgill.science.ctf.enums.DataType;
import ca.mcgill.science.ctf.enums.Room;
import ca.mcgill.science.ctf.eventRequests.BaseEventRequest;
import ca.mcgill.science.ctf.eventRequests.DestinationEventRequest;
import ca.mcgill.science.ctf.eventRequests.NicknameEventRequest;
import ca.mcgill.science.ctf.eventRequests.QuotaEventRequest;
import ca.mcgill.science.ctf.eventRequests.RoomInfoEventRequest;
import ca.mcgill.science.ctf.eventRequests.RoomJobsEventRequest;
import ca.mcgill.science.ctf.eventRequests.UserJobsEventRequest;
import ca.mcgill.science.ctf.requests.CTFSpiceService;
import ca.mcgill.science.ctf.tepid.Destination;
import ca.mcgill.science.ctf.tepid.PrintJob;
import ca.mcgill.science.ctf.tepid.RoomInformation;
import ca.mcgill.science.ctf.wrappers.RoomPrintJob;

import static ca.mcgill.science.ctf.enums.DataType.Single.DESTINATIONS;
import static ca.mcgill.science.ctf.enums.DataType.Single.QUEUES;

/**
 * Created by Allan Wang on 26/12/2016.
 * <p>
 * This Activity takes care of executing and sending all load requests and responses
 * It operates through an EnumMap that keeps track of update times
 * If a request is sent and has not yet been received, the request will not be sent again
 * If all requests are fulfilled, we are free to load all null/outdated data
 * If a request is sent moments after new data has been received and is not a forced request, we may load the old data
 */

public abstract class RequestActivity extends CapsuleActivityFrame {

    protected String mToken;
    private SpiceManager mRequestManager = new SpiceManager(CTFSpiceService.class);
    private static final long FROM_LOCAL_THRESHOLD = 15L, //Seconds from last update where you should pull from local
    //Special flags; all < 0
    PENDING_REQUEST = -1L, //We've sent this request and are waiting for the response
            PENDING_BUT_NOT_EXECUTED = -2L; //Signifies that we need the request but we are sending other requests first to fulfill this one
    /*
     * Map that keeps track of latest update time
     * If time shows as PENDING_REQUEST, it is currently in progress
     */
    private EnumMap<DataType.Single, Long> mUpdateMap = new EnumMap<>(DataType.Single.class);

    private boolean waitingForRoomInfo = false;
    private boolean allLoaded = false;

    /*
     * Collection of requested data
     */
    protected String rQuota, rNickname;
    protected PrintJob[] rPrintJobArray;
    protected HashMap<String, Destination> rDestinationMap;
    protected ArrayList<RoomInformation> rRoomInfoList;
    protected EnumMap<Room, RoomPrintJob> rRoomJobsMap = new EnumMap<>(Room.class);

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        startSpice();
    }

    /**
     * Helper method for Category loading
     * Will load all of the inner Single DataTypes
     *
     * @param event Category DataType and optional variables needed to complete certain requests
     */
    @Subscribe
    public void loadData(Events.CategoryDataEvent event) {
        for (DataType.Single s : event.getType().getContent())
            loadData(new Events.SingleDataEvent(s, event.getExtra()));
    }

    /**
     * Listener for single data type loading
     * Will only trigger a new request if one is not already in session
     *
     * @param event Single DataType and optional variables needed to complete certain requests
     */
    @Subscribe
    public void loadData(Events.SingleDataEvent event) {
        DataType.Single type = event.getType();
        /*
         * Currently, if any fragment does not have all of the necessary information, it will reload all the necessary data
         * ForceReload should only be true when the user decides to pull down the SwipeRefreshLayout
         * Otherwise, if we already have data that is recent enough to use, there is no need to request it again
         */
        if (!event.getForceReload() && isWithinSeconds(type, FROM_LOCAL_THRESHOLD)) {
            Object data = getLocalData(type);
            if (data != null) {
                CLog.d("Send %s from local data", type);
                postEvent(new Events.LoadEvent(type, true, getLocalData(type)).fragmentOnly());
                return;
            } //data should never be null, unless the timeMap is out of sync
        }
//        startSpice(); //For precautions; eventually stopSpice will be implemented more where necessary
        CLog.d("Sending request for %s", type);
        if (type == QUEUES && event.getExtra() == null) {
            CLog.d("Destinations is null, get that first");
            waitingForRoomInfo = true;
            setValue(QUEUES, PENDING_BUT_NOT_EXECUTED);
            type = DESTINATIONS; //Load destinations first
        }

        if (isInProgress(type)) {
            CLog.d("%s request is already in session", type);
            return;
        }
        setInProgress(type);

        getEventRequest(type).execute(mRequestManager, this, mToken, event.getExtra()); //Call a new request with a new listener for the given type
    }

    private BaseEventRequest getEventRequest(DataType.Single type) {
        switch (type) {
            case QUOTA:
                return new QuotaEventRequest();
            case USER_JOBS:
                return new UserJobsEventRequest();
            case ROOM_JOBS:
                return new RoomJobsEventRequest();
            case DESTINATIONS:
                return new DestinationEventRequest();
            case QUEUES:
                return new RoomInfoEventRequest();
            case NICKNAME:
                return new NicknameEventRequest();
            default:
                throw new RuntimeException(sf(R.string.load_data_no_event_request, type));
        }
    }

    @Subscribe
    public final void onLoadEventSubscription(@NonNull Events.LoadEvent event) {
        onLoadEvent(event);
    }

    /**
     * On load event.
     *
     * @param event the event received (containing the type and data)
     */
    private void onLoadEvent(@NonNull Events.LoadEvent event) {
        if (event.isFragmentOnly()) return;
        CLog.d("%s has loaded", event.getType());
        updateTime(event.getType());
        loadRemaining(); //Load all remaining data if not done already
        if (!event.isSuccessful() || event.getData() == null) {
            CLog.e("Unsuccessful or null load event: %s", event);
            return;
        }
        switch (event.getType()) {
            case QUOTA:
                rQuota = String.valueOf(event.getData());
                break;
            case USER_JOBS:
                rPrintJobArray = (PrintJob[]) event.getData();
                break;
            case DESTINATIONS:
                rDestinationMap = (HashMap<String, Destination>) event.getData();
                if (waitingForRoomInfo) {
                    CLog.d("Proceed to load Queue");
                    loadData(new Events.SingleDataEvent(QUEUES, rDestinationMap)); //Submit new QUEUE request with given destinationMap
                }
                break;
            case QUEUES: //Process into RoomInfoItem first; then send
                waitingForRoomInfo = false;
                rRoomInfoList = (ArrayList<RoomInformation>) event.getData();
                break;
            case NICKNAME:
                rNickname = String.valueOf(event.getData());
                break;
            case ROOM_JOBS:
                RoomPrintJob roomPrintJob = (RoomPrintJob) event.getData();
                rRoomJobsMap.put(roomPrintJob.room, roomPrintJob);
                break;
        }
    }

    //Get requested data from saved values
    private Object getLocalData(DataType.Single type) {
        switch (type) {
            case QUOTA:
                return rQuota;
            case USER_JOBS:
                return rPrintJobArray;
            case DESTINATIONS:
                return rDestinationMap;
            case QUEUES: //Process into RoomInfoItem first; then send
                return rRoomInfoList;
            case NICKNAME:
                return rNickname;
            case ROOM_JOBS:
                return rRoomJobsMap;
            default:
                CLog.e(sf(R.string.no_local_data, type));
                return null;
        }
    }

    private void loadRemaining() {
        if (allLoaded) return;
        //Check if all are loaded
        if (mUpdateMap.size() == DataType.Single.values().length) {
            allLoaded = true;
            return;
        }
        //Check for pending requests
        for (Long time : mUpdateMap.values()) {
            if (time < 0) return; //Special flag found, load this first
        }
        for (DataType.Single type : DataType.Single.values()) {
            if (!mUpdateMap.containsKey(type)) loadData(new Events.SingleDataEvent(type));
        }
    }

    //Set map time of current type to current time
    private void updateTime(DataType.Single type) {
        setValue(type, System.currentTimeMillis());
    }

    /**
     * Sets timemap value to PENDING_REQUEST to signify that it is loading
     *
     * @param type Single DataType
     */
    private void setInProgress(DataType.Single type) {
        setValue(type, PENDING_REQUEST);
    }

    private void setValue(DataType.Single type, long value) {
        mUpdateMap.put(type, value);
    }

    /**
     * Check if timestamp is PENDING_REQUEST
     *
     * @param type Single Data type
     * @return update status
     */
    private boolean isInProgress(DataType.Single type) {
        return mUpdateMap.containsKey(type) && mUpdateMap.get(type) == PENDING_REQUEST;
    }

    private boolean isWithinSeconds(DataType.Single type, long seconds) {
        if (!mUpdateMap.containsKey(type)) return false;
        long diff = System.currentTimeMillis() - mUpdateMap.get(type);
        return diff < (seconds * 1000);
    }

    private void startSpice() {
        if (!mRequestManager.isStarted()) mRequestManager.start(this);
    }

    private void stopSpice() {
        if (mRequestManager.isStarted()) mRequestManager.shouldStop();
    }


    @Override
    protected void onStart() {
        super.onStart();
        startSpice();
    }

    @Override
    protected void onStop() {
        stopSpice();
        super.onStop();
    }


}
