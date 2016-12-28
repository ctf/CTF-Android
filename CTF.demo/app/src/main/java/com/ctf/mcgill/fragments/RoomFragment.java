package com.ctf.mcgill.fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.ctf.mcgill.R;
import com.ctf.mcgill.enums.Room;
import com.pitchedapps.capsule.library.adapters.ViewPagerAdapter;
import com.pitchedapps.capsule.library.fragments.ViewPagerFragment;
import com.pitchedapps.capsule.library.interfaces.CPage;
import com.pitchedapps.capsule.library.item.PageItem;

import java.util.Arrays;
import java.util.List;

public class RoomFragment extends ViewPagerFragment {

    public static final String TAG = "ROOM_FRAGMENT";

    // the number of tabs on the room info page
    private static final int FRAGMENT_COUNT = 3;
    private static final String KEY_TOKEN = "token";
    private String token;


    public static RoomFragment newInstance(String token) {
        return new RoomFragment();
    }

    @Override
    protected List<CPage> setPages() {
        return Arrays.asList(new CPage[]{
                new PageItem(RoomTabFragment.newInstance(Room._1B16, null, null), 0), //TODO change newInstance; this is for testing
                new PageItem(RoomTabFragment.newInstance(Room._1B17, null, null), 0),
                new PageItem(RoomTabFragment.newInstance(Room._1B18, null, null), 0)
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
}