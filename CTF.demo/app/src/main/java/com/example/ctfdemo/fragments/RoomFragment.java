package com.example.ctfdemo.fragments;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ctfdemo.R;
import com.example.ctfdemo.adapter.PrintJobAdapter;
import com.example.ctfdemo.tepid.PrintJob;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    // fragment representing a single tab in the room fragment's view pager (fragment for 1B16-18)
    public static class RoomTabFragment extends Fragment {

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static RoomTabFragment newInstance(int sectionNumber) {
            RoomTabFragment fragment = new RoomTabFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        // Required empty public constructor
        public RoomTabFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // rootView occupies screen space below tabs
            View rootView = inflater.inflate(R.layout.fragment_room_tabs, container, false);
            RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recent_jobs);

            Bundle bundle = getArguments();
            int position = bundle.getInt(ARG_SECTION_NUMBER); // position corresponds to room number 1B16, etc.

            // recent jobs table uses a linear layout manager and a custom adapter for table rows
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(new PrintJobAdapter(getActivity(), getData(), PrintJobAdapter.ROOMS));

            Button viewSeats = (Button) rootView.findViewById(R.id.map_button);
            View.OnClickListener buttonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRoomMapDialog();
                }
            };
            viewSeats.setOnClickListener(buttonClickListener);

            return rootView;
        }

        /**
         * get a bunch of PrintJob objects to pass to the PrintJobAdapter,
         * which extracts the relevant data and formats it depending on the
         * type of table it is filling (user's job history or room queues)
         */
        public static List<PrintJob> getData() {
            List<PrintJob> printJobs = new ArrayList<>();

            // TODO: query server for recent job info and store in array
            PrintJob testJob = new PrintJob();
            testJob.setName("final_grades.xml");
            testJob.setPages(1);
            testJob.setPrinted(new Date());
            testJob.setUserIdentification("student123");

            for (int i = 0; i < 10; i++) {
                printJobs.add(testJob);
            }

            return printJobs;
        }

        private void showRoomMapDialog() {
            FragmentManager fm = getFragmentManager();
            RoomMapFragment roomMapFragment = RoomMapFragment.newInstance("dummy_title");
            roomMapFragment.show(fm, "room_map");
        }

    }
}