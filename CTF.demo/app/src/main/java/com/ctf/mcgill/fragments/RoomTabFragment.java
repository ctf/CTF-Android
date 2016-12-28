package com.ctf.mcgill.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ctf.mcgill.R;
import com.ctf.mcgill.adapter.PrintJobAdapter;
import com.ctf.mcgill.enums.Room;
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
import com.pitchedapps.capsule.library.views.SwipeRefreshRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Allan Wang on 2016-11-20.
 */

public class RoomTabFragment extends CapsulePageFragment implements SwipeRefreshRecyclerView.OnRefreshListener {


    @Nullable
    @Override
    protected CFabEvent updateFab() {
        return null;
    }

    @Override
    public int getTitleId() {
        return 0;
    } //title overridden by adapter

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number", KEY_TOKEN = "token";
    private String token;
    private Room room;
    private SpiceManager requestManager = new SpiceManager(CTFSpiceService.class);
    @BindView(R.id.swipe_recycler)
    SwipeRefreshRecyclerView mRefresher;
    @BindView(R.id.inner_recycler)
    RecyclerView mRecycler;
    @BindView(R.id.printer_north)
    ImageView statusNorth;
    @BindView(R.id.printer_south)
    ImageView statusSouth;
    private PrintJobAdapter mAdapter;

    private Unbinder unbinder;


    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static RoomTabFragment newInstance(Room roomNumber, String token) {
        RoomTabFragment fragment = new RoomTabFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SECTION_NUMBER, roomNumber);
        args.putString(KEY_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    protected void bindButterKnife(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        room = (Room) args.getSerializable(ARG_SECTION_NUMBER); // corresponds to room number 1B16, etc.
        token = args.getString(KEY_TOKEN);

        View rootView = inflater.inflate(R.layout.room_tab, container, false);
        bindButterKnife(rootView);
        mAdapter = new PrintJobAdapter(getContext(), null, PrintJobAdapter.TableType.ROOMS);
        mAdapter.bindRecyclerView(mRecycler);
        mRefresher.setInternalRecyclerView(mRecycler);
        mRefresher.setOnRefreshListener(this);
        updateData();
        rootView.findViewById(R.id.map_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRoomMapDialog();
            }
        });

        return rootView;
    }

    private void updateData() {
        requestManager.execute(new QueueRequest(token, room.getName()), new RoomTabFragment.QueueRequestListener());
        requestManager.execute(new DestinationsRequest(token), new RoomTabFragment.DestinationRequestListener());

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }

    private void showRoomMapDialog() {
        FragmentManager fm = getFragmentManager();
        RoomMapFragment roomMapFragment = RoomMapFragment.newInstance("dummy_title");
        roomMapFragment.show(fm, "room_map");
    }

    @Override
    public void onRefresh() {
        updateData();
    }

    private final class QueueRequestListener implements RequestListener<PrintJob[]> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            mRefresher.setRefreshing(false);
        }

        @Override
        public void onRequestSuccess(PrintJob[] printJobs) {
            mRefresher.setRefreshing(false);
            mAdapter.updateList(new ArrayList<>(Arrays.asList(printJobs)));
        }
    }

    private final class DestinationRequestListener implements RequestListener<Map> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            mRefresher.setRefreshing(false);
            System.out.println("FAILURE");
        }

        @Override
        public void onRequestSuccess(Map destinations) {
            mRefresher.setRefreshing(false);

            for (Object d : destinations.values()) {
                String[] id = ((Destination) d).getName().split("-");
                boolean isUp = ((Destination) d).isUp();
                if (id[0].equals(room.getName())) {
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
