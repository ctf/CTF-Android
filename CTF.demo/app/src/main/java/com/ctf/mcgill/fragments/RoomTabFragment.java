package com.ctf.mcgill.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ctf.mcgill.R;
import com.ctf.mcgill.adapter.PrintJobAdapter;
import com.ctf.mcgill.requests.CTFSpiceService;
import com.ctf.mcgill.requests.DestinationsRequest;
import com.ctf.mcgill.requests.QueueRequest;
import com.ctf.mcgill.tepid.Destination;
import com.ctf.mcgill.tepid.PrintJob;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pitchedapps.capsule.library.event.CFabEvent;
import com.pitchedapps.capsule.library.fragments.CapsulePageFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by Allan Wang on 2016-11-20.
 */

public class RoomTabFragment extends CapsulePageFragment {
    @Nullable
    @Override
    protected CFabEvent updateFab() {
        return null;
    }

    @Override
    public int getTitleId() {
        return 0;
    } //title overriden by adapter

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number", KEY_TOKEN = "token";
    private String token, room;
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
        requestManager.execute(new QueueRequest(token, room), new RoomTabFragment.QueueRequestListener());
        requestManager.execute(new DestinationsRequest(token), new RoomTabFragment.DestinationRequestListener());

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
