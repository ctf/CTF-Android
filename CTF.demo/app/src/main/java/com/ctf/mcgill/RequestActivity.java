package com.ctf.mcgill;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ctf.mcgill.enums.DataType;
import com.ctf.mcgill.events.LoadEvent;
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
import java.util.List;
import java.util.Map;

import static com.ctf.mcgill.enums.DataType.Single.Destinations;
import static com.ctf.mcgill.enums.DataType.Single.Queues;

/**
 * Created by Allan Wang on 26/12/2016.
 * <p>
 * Requests should be loaded asap, not just when we switch to the tab.
 */

public abstract class RequestActivity extends CapsuleActivityFrame {

    protected String mToken;
    private SpiceManager mRequestManager = new SpiceManager(CTFSpiceService.class);
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
    protected Map<String, Destination> rDestinationMap;
    protected ArrayList<RoomInformation> rRoomInfoList;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        mRequestManager.start(this);
    }

    /**
     * Helper method for Category loading
     * Will load all of the inner Single DataTypes
     *
     * @param type   Category DataType
     * @param extras Optional variables needed to complete certain requests
     */
    @Subscribe
    public void loadData(DataType.Category type, Object... extras) {
        for (DataType.Single s : type.getContent()) loadData(s, extras);
    }

    /**
     * Listener for single data type loading
     * Will only trigger a new request if one is not already in session
     *
     * @param type   Single DataType
     * @param extras Optional variables needed to complete certain requests
     */
    @Subscribe
    public void loadData(DataType.Single type, Object... extras) {
        if (isInProgress(type)) {
            CLog.d("%s request is already in session", type);
            return;
        }
        CLog.d("Sending request for %s", type);
        setInProgress(type);
        if (type.getCacheKey() != null)
            mRequestManager.execute(type.getRequest(mToken, extras), type.getCacheKey(), DurationInMillis.ONE_MINUTE, type.getListener());
        else mRequestManager.execute(type.getRequest(mToken, extras), type.getListener());
    }

    @Subscribe
    public void onLoadEvent(@NonNull LoadEvent event) {
        if (event.isFragmentOnly()) return;
        if (!event.isSuccessful || event.data == null) {
            if (event.type == Destinations) { //Queue won't be loaded, send empty post
                postEvent(new LoadEvent(Queues, false, null));
            }
            return;
        }
        updateTime(event.type);
        switch (event.type) {
            case Quota:
                rQuota = String.valueOf(event.data);
                break;
            case UserJobs:
                rPrintJobArray = (PrintJob[]) event.data;
                break;
            case Destinations:
                rDestinationMap = (Map<String, Destination>) event.data;
                loadData(DataType.Single.Queues); //destinations found; load and parse queues
                break;
            case Queues: //Process into RoomInfo first; then send
                rRoomInfoList = new ArrayList<>();
                for (PrintQueue q : (List<PrintQueue>) event.data) {
                    String name = q.name;
                    RoomInformation roomInfo = new RoomInformation(name, true); //TODO put actual computer status

                    if (rDestinationMap != null) {
                        for (String d : ((PrintQueue) q).destinations) {
                            roomInfo.addPrinter(rDestinationMap.get(d).getName(), rDestinationMap.get(d).isUp());
                        }
                    }
                    rRoomInfoList.add(roomInfo);
                }
                postEvent(new LoadEvent(Queues, true, rRoomInfoList).fragmentOnly());
                break;
            case Nickname:
                rNickname = String.valueOf(event.data);
                break;
        }
    }

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

}
