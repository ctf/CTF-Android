package com.example.ctfdemo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ctfdemo.R;
import com.example.ctfdemo.adapter.PrintJobAdapter;
import com.example.ctfdemo.requests.CTFSpiceService;
import com.example.ctfdemo.requests.DestinationsRequest;
import com.example.ctfdemo.requests.QueueRequest;
import com.example.ctfdemo.tepid.Destination;
import com.example.ctfdemo.tepid.PrintJob;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pitchedapps.capsule.library.event.CFabEvent;
import com.pitchedapps.capsule.library.fragments.CapsuleFragment;
import com.pitchedapps.capsule.library.logging.CLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class RoomFragment extends BaseFragment {

    public static final String TAG = "ROOM_FRAGMENT";

    // the number of tabs on the room info page
    private static final int FRAGMENT_COUNT = 3;
    private static final String KEY_TOKEN = "token";


    public static RoomFragment newInstance(String token) {
        return (RoomFragment) fragmentWithToken(new RoomFragment(), token);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_viewpager, container, false);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        viewPager.setAdapter(new RoomPagerAdapter(getChildFragmentManager(), token));

        tabLayout = (TabLayout) rootView.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        return rootView;
    }


    @Override
    public int getTitleId() {
        return R.string.roominfo;
    }

    @Override
    protected void getUIData() {
        //Data retrieved from inner fragments
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
            return RoomTabFragment.newInstance("1B" + (16 + position), token);
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
        private String room, token;
        private SpiceManager requestManager = new SpiceManager(CTFSpiceService.class);
        private RecyclerView mRecyclerView;
        private ImageView statusNorth, statusSouth;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static RoomTabFragment newInstance(String roomNumber, String token) {
            RoomTabFragment fragment = new RoomTabFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_NUMBER, roomNumber);
            args.putString(KEY_TOKEN, token);
            fragment.setArguments(args);
            return fragment;
        }

        // Required empty public constructor
        public RoomTabFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            room = args.getString(ARG_SECTION_NUMBER); // corresponds to room number 1B16, etc.
            token = args.getString(KEY_TOKEN);

            View rootView = inflater.inflate(R.layout.fragment_room_tab, container, false);
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recent_jobs);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            statusNorth = (ImageView) rootView.findViewById(R.id.printer_north);
            if (!room.equals("1B18")) {
                statusSouth = (ImageView) rootView.findViewById(R.id.printer_south);
            }
            requestManager.execute(new QueueRequest(token, room), new QueueRequestListener());
            requestManager.execute(new DestinationsRequest(token), new DestinationRequestListener());

            rootView.findViewById(R.id.map_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRoomMapDialog();
                }
            });

            return rootView;
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

        private void showRoomMapDialog() {
            FragmentManager fm = getFragmentManager();
            RoomMapFragment roomMapFragment = RoomMapFragment.newInstance("dummy_title");
            roomMapFragment.show(fm, "room_map");
        }

        private final class QueueRequestListener implements RequestListener<PrintJob[]> {

            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(PrintJob[] printJobs) {
                mRecyclerView.setAdapter(new PrintJobAdapter(getActivity(), new ArrayList<PrintJob>(Arrays.asList(printJobs)), PrintJobAdapter.ROOMS));
            }
        }

        private final class DestinationRequestListener implements RequestListener<Map> {

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                System.out.println("FAILURE");
            }

            @Override
            public void onRequestSuccess(Map destinations) {
                for (Object d : destinations.values()) {
                    String[] id = ((Destination)d).getName().split("-");
                    boolean isUp = ((Destination)d).isUp();
                    if (id[0].equals(room)) {
                        switch (id[1]) {
                            case "North":
                                if (isUp) {
                                    statusNorth.setImageResource(R.drawable.printer_up);
                                } else {
                                    statusNorth.setImageResource(R.drawable.printer_down);
                                }
                                break;
                            case "South":
                                if (isUp) {
                                    statusSouth.setImageResource(R.drawable.printer_up);
                                } else {
                                    statusSouth.setImageResource(R.drawable.printer_down);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }
}