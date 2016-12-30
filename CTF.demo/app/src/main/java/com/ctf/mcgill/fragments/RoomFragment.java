package com.ctf.mcgill.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.ctf.mcgill.R;
import com.ctf.mcgill.enums.Room;
import com.ctf.mcgill.events.LoadEvent;
import com.ctf.mcgill.wrappers.DestinationHashMap;
import com.ctf.mcgill.tepid.Destination;
import com.pitchedapps.capsule.library.adapters.ViewPagerAdapter;
import com.pitchedapps.capsule.library.fragments.ViewPagerFragment;
import com.pitchedapps.capsule.library.interfaces.CPage;
import com.pitchedapps.capsule.library.item.PageItem;
import com.pitchedapps.capsule.library.utils.ParcelUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RoomFragment extends ViewPagerFragment {

    public static final String TAG = "ROOM_FRAGMENT"; //TODO remove

    private HashMap<String, Destination> rDesinationMap;

    private static final String BUNDLE_DESTINATION_MAP = "destination_map", BUNDLE_COMPLETE = "complete";


    public static RoomFragment newInstance(HashMap<String, Destination> destinationMap) {
        ParcelUtils parcelUtils = new ParcelUtils<>(new RoomFragment());
        if (parcelUtils.putNullStatus(BUNDLE_COMPLETE, destinationMap)) {
            parcelUtils.putHashMap(BUNDLE_DESTINATION_MAP, new DestinationHashMap(destinationMap));
        }
        return (RoomFragment) parcelUtils.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
        getArgs(getArguments());
    }

    public void getArgs(Bundle args) {
        if (args == null || !args.getBoolean(BUNDLE_COMPLETE, false)) {
            return;
        }
        rDesinationMap = ParcelUtils.getHashMap(args, BUNDLE_DESTINATION_MAP, DestinationHashMap.class);
    }

    @Override
    protected List<CPage> setPages() {
        return Arrays.asList(new CPage[]{
                new PageItem(RoomTabFragment.newInstance(Room._1B16, null, rDesinationMap), 0), //TODO change newInstance; this is for testing
                new PageItem(RoomTabFragment.newInstance(Room._1B17, null, rDesinationMap), 0),
                new PageItem(RoomTabFragment.newInstance(Room._1B18, null, rDesinationMap), 0)
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
    protected ViewPagerAdapter setAdapter(Context context, FragmentManager fragmentManager, ViewPager viewPager, List<CPage> pages) {
        return new RoomPagerAdapter(context, fragmentManager, viewPager, pages);
    }


    @Override
    public int getTitleId() {
        return R.string.roominfo;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the room pages.
     */
    class RoomPagerAdapter extends ViewPagerAdapter {

        RoomPagerAdapter(Context context, FragmentManager fm, ViewPager viewPager, @NonNull List<CPage> pages) {
            super(context, fm, viewPager, pages);
        }

        @Override
        public String getPageTitle(int position) { //TODO change to R.string
            switch (position) {
                case 0:
                    return "1B16";
                case 1:
                    return "1B17";
                case 2:
                    return "1B18";
            }
            return null;
        }
    }

    @Subscribe
    public void onLoadEvent(LoadEvent event) {
        if (event.isActivityOnly()) return;
        switch (event.type) {
            case DESTINATIONS:
                rDesinationMap = (HashMap<String, Destination>) event.data; //TODO check if worth saving
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