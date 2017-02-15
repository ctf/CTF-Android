package ca.mcgill.science.ctf.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.pitchedapps.capsule.library.fragments.ViewPagerFragment;
import com.pitchedapps.capsule.library.interfaces.CPage;
import com.pitchedapps.capsule.library.item.PageItem;
import com.pitchedapps.capsule.library.utils.ParcelUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

import ca.mcgill.science.ctf.Events;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.enums.Room;
import ca.mcgill.science.ctf.tepid.Destination;
import ca.mcgill.science.ctf.wrappers.DestinationHashMap;
import ca.mcgill.science.ctf.wrappers.RoomJobEnumMap;
import ca.mcgill.science.ctf.wrappers.RoomPrintJob;

public class RoomFragment extends ViewPagerFragment {

    private HashMap<String, Destination> rDesinationMap;
    private EnumMap<Room, RoomPrintJob> rRoomPrintJobMap;

    private static final String BUNDLE_DESTINATION_MAP = "destination_map", BUNDLE_ROOM_JOBS_MAP = "room_jobs_map", BUNDLE_COMPLETE = "complete";

    public static RoomFragment newInstance(HashMap<String, Destination> destinationMap, EnumMap<Room, RoomPrintJob> roomPrintJobMap) {
        ParcelUtils parcelUtils = new ParcelUtils<>(new RoomFragment());
        if (parcelUtils.putNullStatus(BUNDLE_COMPLETE, destinationMap, roomPrintJobMap)) {
            parcelUtils.putMap(BUNDLE_DESTINATION_MAP, new DestinationHashMap(destinationMap));
            parcelUtils.putMap(BUNDLE_ROOM_JOBS_MAP, new RoomJobEnumMap(roomPrintJobMap));
        }
        return (RoomFragment) parcelUtils.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArgs(getArguments());
    }

    public void getArgs(Bundle args) {
        if (args == null || !args.getBoolean(BUNDLE_COMPLETE, false)) {
            return;
        }
        rDesinationMap = ParcelUtils.getMap(args, BUNDLE_DESTINATION_MAP, DestinationHashMap.class);
        rRoomPrintJobMap = ParcelUtils.getMap(args, BUNDLE_ROOM_JOBS_MAP, RoomJobEnumMap.class);
    }

    @Override
    protected List<CPage> setPages() {
        return Arrays.asList(new CPage[]{
                new PageItem(RoomTabFragment.newInstance(Room._1B16, rRoomPrintJobMap, rDesinationMap), R.string.string_1b16),
                new PageItem(RoomTabFragment.newInstance(Room._1B17, rRoomPrintJobMap, rDesinationMap), R.string.string_1b17),
                new PageItem(RoomTabFragment.newInstance(Room._1B18, rRoomPrintJobMap, rDesinationMap), R.string.string_1b18)
        });
    }

    @Override
    protected void viewPagerSetup(ViewPager viewPager, int pageCount) {
        /**
         * The size is small enough so that it's worth it
         * Please be aware that the RoomTabFragments do not save their data when started/stopped
         * If they are ever paused by the viewPager, they need to have their own way of getting data back
         * Their data is however saved in this fragment, but only when switched to this tab from the drawer
         */
        viewPager.setOffscreenPageLimit(pageCount);
    }


    @Override
    public int getTitleId() {
        return R.string.roominfo;
    }


    @Subscribe
    public void onLoadEvent(Events.LoadEvent event) {
        if (event.isActivityOnly()) return;
        if (!event.isSuccessful() || event.getData() == null) return;
        switch (event.getType()) {
            case DESTINATIONS:
                rDesinationMap = (HashMap<String, Destination>) event.getData(); //TODO check if worth saving
                break;
            case ROOM_JOBS:
                if (rRoomPrintJobMap == null)
                    rRoomPrintJobMap = new EnumMap<>(Room.class);
                RoomPrintJob printJob = (RoomPrintJob) event.getData();
                rRoomPrintJobMap.put(printJob.room, printJob);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }
}