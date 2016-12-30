package com.ctf.mcgill;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ctf.mcgill.enums.DataType;
import com.ctf.mcgill.eventRequests.BaseEventRequest;
import com.ctf.mcgill.eventRequests.DestinationEventRequest;
import com.ctf.mcgill.eventRequests.NicknameEventRequest;
import com.ctf.mcgill.eventRequests.QuotaEventRequest;
import com.ctf.mcgill.eventRequests.RoomInfoEventRequest;
import com.ctf.mcgill.eventRequests.RoomJobsEventRequest;
import com.ctf.mcgill.eventRequests.UserJobsEventRequest;
import com.ctf.mcgill.events.CategoryDataEvent;
import com.ctf.mcgill.events.LoadEvent;
import com.ctf.mcgill.events.SingleDataEvent;
import com.ctf.mcgill.requests.CTFSpiceService;
import com.ctf.mcgill.tepid.Destination;
import com.ctf.mcgill.tepid.PrintJob;
import com.ctf.mcgill.tepid.RoomInformation;
import com.octo.android.robospice.SpiceManager;
import com.pitchedapps.capsule.library.activities.CapsuleActivityFrame;
import com.pitchedapps.capsule.library.logging.CLog;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

import static com.ctf.mcgill.enums.DataType.Single.DESTINATIONS;
import static com.ctf.mcgill.enums.DataType.Single.QUEUES;
import static com.ctf.mcgill.enums.DataType.Single.ROOM_JOBS;

/**
 * Created by Allan Wang on 26/12/2016.
 * <p>
 * Requests should be loaded asap, not just when we switch to the tab.
 */

public abstract class RequestActivity extends CapsuleActivityFrame {

    protected String mToken;
    private SpiceManager mRequestManager = new SpiceManager(CTFSpiceService.class);
    private static final long FROM_LOCAL_THRESHOLD = 15L; //Seconds from last update where you should pull from local
    /*
     * Map that keeps track of latest update time
     * If time shows as -1, it is currently in progress
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
    public void loadData(CategoryDataEvent event) {
        for (DataType.Single s : event.type.getContent())
            loadData(new SingleDataEvent(s, event.extra));
    }

    /**
     * Listener for single data type loading
     * Will only trigger a new request if one is not already in session
     *
     * @param event Single DataType and optional variables needed to complete certain requests
     */
    @Subscribe
    public void loadData(SingleDataEvent event) {
        DataType.Single type = event.type;
        /*
         * Currently, if any fragment does not have all of the necessary information, it will reload all the necessary data
         * ForceReload should only be true when the user decides to pull down the SwipeRefreshLayout
         * Otherwise, if we already have data that is recent enough to use, there is no need to request it again
         */
        if (!event.forceReload && isWithinSeconds(type, FROM_LOCAL_THRESHOLD)) {
            Object data = getLocalData(type);
            if (data != null) {
                CLog.d("Send %s from local data", type);
                postEvent(new LoadEvent(type, true, getLocalData(type)).fragmentOnly());
                return;
            } //data should never be null, unless the timeMap is out of sync
        }
//        startSpice(); //For precautions; eventually stopSpice will be implemented more where necessary
        CLog.d("Sending request for %s", type);
        if (type == QUEUES && event.extra == null) {
            CLog.d("Destinations is null, get that first");
            waitingForRoomInfo = true;
            type = DESTINATIONS; //Load destinations first
        }
        if (event.type != ROOM_JOBS) { //TODO change this (room_jobs is currently not saved)
            if (isInProgress(type)) {
                CLog.d("%s request is already in session", type);
                return;
            }
            setInProgress(type);
        }


        getEventRequest(type).execute(mRequestManager, this, mToken, event.extra); //Call a new request with a new listener for the given type
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
    public final void onLoadEventSubscription(@NonNull LoadEvent event) {
        onLoadEvent(event);
    }

    /**
     * On load event.
     *
     * @param event the event received (containing the type and data)
     */
    private void onLoadEvent(@NonNull LoadEvent event) {
        if (event.isFragmentOnly()) return;
        CLog.d("%s has loaded", event.type);
        updateTime(event.type);
        loadRemaining(); //Load all remaining data if not done already
        if (!event.isSuccessful || event.data == null) {
            CLog.e("Unsuccessful or null load event: %s", event);
            return;
        }
        switch (event.type) {
            case QUOTA:
                rQuota = String.valueOf(event.data);
                break;
            case USER_JOBS:
                rPrintJobArray = (PrintJob[]) event.data;
                break;
            case DESTINATIONS:
                rDestinationMap = (HashMap<String, Destination>) event.data;
                if (waitingForRoomInfo) {
                    CLog.d("Proceed to load Queue");
                    loadData(new SingleDataEvent(QUEUES, rDestinationMap)); //Submit new QUEUE request with given destinationMap
                }
                break;
            case QUEUES: //Process into RoomInfo first; then send
                waitingForRoomInfo = false;
                rRoomInfoList = (ArrayList<RoomInformation>) event.data;
                break;
            case NICKNAME:
                rNickname = String.valueOf(event.data);
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
            case QUEUES: //Process into RoomInfo first; then send
                return rRoomInfoList;
            case NICKNAME:
                return rNickname;
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
            if (time == -1) return; //Finish loading what you need first
        }
        for (DataType.Single type : DataType.Single.values()) {
            if (!mUpdateMap.containsKey(type)) loadData(new SingleDataEvent(type));
        }
    }

    //Set map time of current type to current time
    private void updateTime(DataType.Single type) {
        mUpdateMap.put(type, System.currentTimeMillis());
    }

    /**
     * Sets timemap value to -1 to signify that it is loading
     *
     * @param type Single DataType
     */
    private void setInProgress(DataType.Single type) {
        mUpdateMap.put(type, -1L);
    }

    /**
     * Check if timestamp is -1
     *
     * @param type Single Data type
     * @return update status
     */
    private boolean isInProgress(DataType.Single type) {
        return mUpdateMap.containsKey(type) && mUpdateMap.get(type) == -1L;
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
