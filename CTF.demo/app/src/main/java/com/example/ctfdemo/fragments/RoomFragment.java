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
import com.example.ctfdemo.auth.AccountUtil;
import com.example.ctfdemo.requests.CTFSpiceService;
import com.example.ctfdemo.requests.TokenRequest;
import com.example.ctfdemo.tepid.PrintJob;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoomFragment extends Fragment {

    // the number of tabs on the room info page
    private static final int FRAGMENT_COUNT = 3;
    private SpiceManager requestManager = new SpiceManager(CTFSpiceService.class);
    private static final String KEY_TOKEN = "token";
    private String token;


    public static RoomFragment newInstance(String token) {
        RoomFragment frag = new RoomFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TOKEN, token);
        frag.setArguments(args);
        return frag;
    }

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_viewpager, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            token = args.getString(KEY_TOKEN);
        }
        viewPager = (ViewPager) getView().findViewById(R.id.viewpager);
        viewPager.setAdapter(new RoomPagerAdapter(getChildFragmentManager(), token));

        tabLayout = (TabLayout) getView().findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onStart() {
        super.onStart();
        requestManager.start(getActivity());
    }

    @Override
    public void onStop() {
        // Please review https://github.com/octo-online/robospice/issues/96 for the reason of that
        // ugly if statement.
        if (requestManager.isStarted()) {
            requestManager.shouldStop();
        }
        super.onStop();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the room pages.
     */
    public class RoomPagerAdapter extends FragmentPagerAdapter {

        private String token;

        public RoomPagerAdapter(FragmentManager fm, String token) {
            super(fm);
            this.token = token;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // we return a RoomTabFragment
            return RoomTabFragment.newInstance(position, token);
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
        private static final String ARG_SECTION_NUMBER = "section_number", KEY_TOKEN = "token";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static RoomTabFragment newInstance(int sectionNumber, String token) {
            RoomTabFragment fragment = new RoomTabFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(KEY_TOKEN, token);
            fragment.setArguments(args);
            return fragment;
        }

        // Required empty public constructor
        public RoomTabFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_room_tabs, container, false);

            Bundle args = getArguments();
            int position = args.getInt(ARG_SECTION_NUMBER); // position corresponds to room number 1B16, etc.
            String token = args.getString(KEY_TOKEN);

            RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recent_jobs);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(new PrintJobAdapter(getActivity(), getData(token), PrintJobAdapter.ROOMS));

            rootView.findViewById(R.id.map_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRoomMapDialog();
                }
            });

            return rootView;
        }

        /**
         * get a bunch of PrintJob objects to pass to the PrintJobAdapter,
         * which extracts the relevant data and formats it depending on the
         * type of table it is filling (user's job history or room queues)
         */
        public static List<PrintJob> getData(String token) {
            List<PrintJob> printJobs = new ArrayList<>();

            // TODO: query server for recent job info and store in array
            PrintJob testJob = new PrintJob();
            testJob.setName("final_grades.xml");
            testJob.setPages(1);
            testJob.setPrinted(new Date());
            testJob.setUserIdentification(token);

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