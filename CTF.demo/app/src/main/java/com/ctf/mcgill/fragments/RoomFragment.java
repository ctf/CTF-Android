package com.ctf.mcgill.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.ctf.mcgill.R;
import com.ctf.mcgill.enums.Room;
import com.ctf.mcgill.events.LoadEvent;
import com.ctf.mcgill.items.DestinationMap;
import com.ctf.mcgill.tepid.Destination;
import com.ctf.mcgill.tepid.PrintJob;
import com.pitchedapps.capsule.library.adapters.ViewPagerAdapter;
import com.pitchedapps.capsule.library.fragments.ViewPagerFragment;
import com.pitchedapps.capsule.library.interfaces.CPage;
import com.pitchedapps.capsule.library.item.PageItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RoomFragment extends ViewPagerFragment {

    public static final String TAG = "ROOM_FRAGMENT";

    // the number of tabs on the room info page
    private static final int FRAGMENT_COUNT = 3;

    private DestinationMap rDesinationMap;

    private static final String BUNDLE_DESTINATION_MAP = "destination_map", BUNDLE_COMPLETE = "complete";


    public static RoomFragment newInstance(DestinationMap destinationMap) {
        RoomFragment f = new RoomFragment();
        Bundle args = new Bundle();
        if (destinationMap == null) {
            args.putBoolean(BUNDLE_COMPLETE, false);
        } else {
            args.putBoolean(BUNDLE_COMPLETE, true);
            args.putParcelable(BUNDLE_DESTINATION_MAP, destinationMap);
        }
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); //TODO Why? Didn't see any differences - Allan
        getArgs(getArguments());
    }

    public void getArgs(Bundle args) {
        if (args == null || !args.getBoolean(BUNDLE_COMPLETE, false)) {
            return;
        }
        rDesinationMap = args.getParcelable(BUNDLE_DESTINATION_MAP);
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
        public String getPageTitle(int position) {
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
                rDesinationMap = new DestinationMap((Map<String, Destination>) event.data);
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