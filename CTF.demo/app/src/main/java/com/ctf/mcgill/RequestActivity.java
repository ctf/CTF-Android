package com.ctf.mcgill;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    private EnumMap<DataType.Single, Long> mUpdateMap;

    /*
     * Collection of requested data
     */
    private String rQuota;
    private PrintJob[] rPrintJobArray;
    private Map<String, Destination> rDestinationMap;
    private List<RoomInformation> rRoomInfoList;

    @Subscribe
    public void loadData(DataType.Category type) {
        for (DataType.Single s : type.getContent()) loadData(s);
    }

    @Subscribe
    public void loadData(DataType.Single type) {
        mRequestManager.execute(type.getRequest(mToken), type.getCacheKey(), DurationInMillis.ONE_MINUTE, type.getListener());
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
        }
    }

    private void updateTime(DataType.Single type) {
        mUpdateMap.put(type, System.currentTimeMillis());
    }

}
