package com.ctf.mcgill;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ctf.mcgill.enums.DataType;
import com.ctf.mcgill.events.CategoryDataEvent;
import com.ctf.mcgill.events.LoadEvent;
import com.ctf.mcgill.events.SingleDataEvent;
import com.ctf.mcgill.requests.CTFSpiceService;
import com.ctf.mcgill.tepid.Destination;
import com.ctf.mcgill.tepid.PrintJob;
import com.ctf.mcgill.tepid.PrintQueue;
import com.ctf.mcgill.tepid.RoomInformation;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.pitchedapps.capsule.library.activities.CapsuleActivityFrame;
import com.pitchedapps.capsule.library.logging.CLog;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ctf.mcgill.enums.DataType.Single.DESTINATIONS;
import static com.ctf.mcgill.enums.DataType.Single.DESTINATIONS_TO_QUEUE;
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
            loadData(new SingleDataEvent(s, event.extras));
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
            CLog.d("Send %s from local data", type);
            if (type == DESTINATIONS_TO_QUEUE)
                type = QUEUES; //TODO get better workaround; queue request starts from destinations_to_queue
            Object data = getLocalData(type);
            if (data != null) {
                postEvent(new LoadEvent(type, true, getLocalData(type)).fragmentOnly());
                return;
            } //data should never be null, unless the timeMap is out of sync
        }
        startSpice(); //For precautions; eventually stopSpice will implemented more where necessary
        Object[] extras = event.extras;
        CLog.d("Sending request for %s", type);
        if (event.type != ROOM_JOBS) { //TODO change this (room_jobs is currently not saved)
            if (isInProgress(type)) {
                CLog.d("%s request is already in session", type);
                return;
            }
            setInProgress(type);
        }

        mRequestManager.execute(type.getRequest(mToken, extras), type.getListener()); //Call a new request with a new listener for the given type
    }

    /**
     * On load event.
     *
     * @param event the event received (containing the type and data)
     */
    @Subscribe
    public void onLoadEvent(@NonNull LoadEvent event) {
        if (event.isFragmentOnly()) return;
        CLog.d("%s has loaded", event.type);
        updateTime(event.type);
        if (!event.isSuccessful || event.data == null) {
            CLog.e("Unsuccessful or null load event: %s", event);
            if (event.type == DESTINATIONS_TO_QUEUE) { //Queue won't be loaded, send empty post
                postEvent(new LoadEvent(QUEUES, false, null));
            }
            return;
        }
        switch (event.type) {
            case QUOTA:
                rQuota = String.valueOf(event.data);
                break;
            case USER_JOBS:
                rPrintJobArray = (PrintJob[]) event.data;
                break;
            case DESTINATIONS_TO_QUEUE:
                rDestinationMap = (HashMap<String, Destination>) event.data;
                loadData(new SingleDataEvent(DataType.Single.QUEUES)); //destinations found; load and parse queues
                break;
            case DESTINATIONS:
                rDestinationMap = (HashMap<String, Destination>) event.data;
                break;
            case QUEUES: //Process into RoomInfo first; then send
                rRoomInfoList = new ArrayList<>();
                for (PrintQueue q : (List<PrintQueue>) event.data) {
                    String name = q.name;
                    RoomInformation roomInfo = new RoomInformation(name, true); //TODO put actual computer status

                    if (rDestinationMap != null) {
                        for (String d : q.destinations) {
                            roomInfo.addPrinter(rDestinationMap.get(d).getName(), rDestinationMap.get(d).isUp());
                        }
                    }
                    rRoomInfoList.add(roomInfo);
                }
                postEvent(new LoadEvent(QUEUES, true, rRoomInfoList).fragmentOnly());
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
            case DESTINATIONS_TO_QUEUE:
                return rDestinationMap;
            case DESTINATIONS:
                return rDestinationMap;
            case QUEUES: //Process into RoomInfo first; then send
                return rRoomInfoList;
            case NICKNAME:
                return rNickname;
            default:
                CLog.e("No local data specified for %s", type);
                return null;
        }
    }

    //Set map time of current type to current time
    private void updateTime(DataType.Single type) {
        mUpdateMap.put(type.getTrueDataType(), System.currentTimeMillis());
    }

    /**
     * Sets timemap value to -1 to signify that it is loading
     *
     * @param type Single DataType
     */
    private void setInProgress(DataType.Single type) {
        mUpdateMap.put(type.getTrueDataType(), -1L);
    }

    /**
     * Check if timestamp is -1
     *
     * @param type Single Data type
     * @return update status
     */
    private boolean isInProgress(DataType.Single type) {
        return mUpdateMap.containsKey(type.getTrueDataType()) && mUpdateMap.get(type.getTrueDataType()) == -1L;
    }

    private boolean isWithinSeconds(DataType.Single type, long seconds) {
        if (!mUpdateMap.containsKey(type.getTrueDataType())) return false;
        long diff = System.currentTimeMillis() - mUpdateMap.get(type.getTrueDataType());
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
