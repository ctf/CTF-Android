package com.example.fragments;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ctfdemo.R;

public class RoomFragment extends Fragment {

    // the number of tabs on the room info page
    private static final int FRAGMENT_COUNT = 3;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_viewpager, container, false);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        viewPager.setAdapter(new RoomPagerAdapter(getChildFragmentManager()));

        tabLayout = (TabLayout) rootView.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the room pages.
     */
    public class RoomPagerAdapter extends FragmentPagerAdapter {

        public RoomPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // we return a RoomTabFragment
            return RoomTabFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return FRAGMENT_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
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