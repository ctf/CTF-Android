package com.ctf.mcgill.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ctf.mcgill.R;
import com.ctf.mcgill.adapter.PrintJobAdapter;
import com.ctf.mcgill.enums.DataType;
import com.ctf.mcgill.enums.Room;
import com.ctf.mcgill.events.LoadEvent;
import com.ctf.mcgill.interfaces.RoboFragmentContract;
import com.ctf.mcgill.items.DestinationMap;
import com.ctf.mcgill.tepid.Destination;
import com.ctf.mcgill.tepid.PrintJob;
import com.pitchedapps.capsule.library.event.CFabEvent;
import com.pitchedapps.capsule.library.event.SnackbarEvent;
import com.pitchedapps.capsule.library.fragments.CapsulePageFragment;
import com.pitchedapps.capsule.library.views.SwipeRefreshRecyclerView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Allan Wang on 2016-11-20.
 */

public class RoomTabFragment extends CapsulePageFragment implements SwipeRefreshRecyclerView.OnRefreshListener, RoboFragmentContract {

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
    private static final String BUNDLE_ROOM = "quota", BUNDLE_PRINT_JOBS = "print_jobs", BUNDLE_DESTINATION_MAP = "destination_map", BUNDLE_COMPLETE = "complete";

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

    private Room rRoom;
    private PrintJob[] rPrintJobArray;
    private DestinationMap rDesinationMap;


    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static RoomTabFragment newInstance(@NonNull Room roomNumber, PrintJob[] printJobs, DestinationMap destinationMap) {
        RoomTabFragment f = new RoomTabFragment();
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_ROOM, roomNumber);
        if (printJobs == null || destinationMap == null) {
            args.putBoolean(BUNDLE_COMPLETE, false);
        } else {
            args.putParcelableArray(BUNDLE_PRINT_JOBS, printJobs);
            args.putParcelable(BUNDLE_DESTINATION_MAP, destinationMap);
        }
        f.setArguments(args);
        return f;
    }

    @Override
    public void getArgs(Bundle args) { //Should never be null
        rRoom = (Room) args.getSerializable(BUNDLE_ROOM);
        if (!args.getBoolean(BUNDLE_COMPLETE, false)) {
            requestData();
            return;
        }
        rPrintJobArray = (PrintJob[]) args.getParcelableArray(BUNDLE_PRINT_JOBS);
        rDesinationMap = args.getParcelable(BUNDLE_DESTINATION_MAP);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); //TODO Why? Didn't see any differences - Allan
        getArgs(getArguments());
    }

    protected void bindButterKnife(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.room_tab, container, false);
        bindButterKnife(rootView);
        mAdapter = new PrintJobAdapter(getContext(), null, PrintJobAdapter.TableType.ROOMS);
        mAdapter.bindRecyclerView(mRecycler);
        mRefresher.setInternalRecyclerView(mRecycler);
        mRefresher.setOnRefreshListener(this);
        rootView.findViewById(R.id.map_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRoomMapDialog();
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) unbinder.unbind();
        super.onDestroyView();
    }

    private void showRoomMapDialog() {
        FragmentManager fm = getFragmentManager();
        RoomMapFragment roomMapFragment = RoomMapFragment.newInstance("dummy_title");
        roomMapFragment.show(fm, "room_map");
    }

    @Override
    public void onRefresh() {
        requestData();
    }

    @Override
    public DataType.Category getDataCategory() {
        return DataType.Category.ROOM_TAB;
    }

    @Override
    public void requestData() {
        postEvent(getDataCategory());
    }

    @Override
    @Subscribe
    public void onLoadEvent(LoadEvent event) {
        if (event.isActivityOnly()) return;
        mRefresher.setRefreshing(false); //TODO hide after all events loaded
        switch (event.type) {
            case ROOM_JOBS:
                if (isLoadSuccessful(event)) {
                    rPrintJobArray = (PrintJob[]) event.data;
                } else return;
                break;
            case DESTINATIONS:
                if (isLoadSuccessful(event)) {
                    rDesinationMap = new DestinationMap((Map<String, Destination>) event.data);
                } else return;
            default: //Event is not one of the ones we wish to see; don't bother updating content for it
                return;
        }
        updateContent(event.type);
    }

    protected boolean isLoadSuccessful(LoadEvent event) {
        if (event.isSuccessful) return true;
        if (event.data == null) return false; //Error String is null -> Silent error
        snackbar(new SnackbarEvent(String.valueOf(event.data)));
        return false;
    }

    @Override
    public void updateContent(DataType.Single... types) {
        for (DataType.Single type : types) {
            switch (type) {
                case DESTINATIONS:
                    for (Destination d : rDesinationMap.map.values()) {
                        String[] id = d.getName().split("-");
                        boolean isUp = d.isUp();
                        if (id[0].equals(rRoom.getName())) {
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
                            return; //We've found the room, no need to keep looking
                        }
                    }
                    break;
                case ROOM_JOBS:
                    mAdapter.updateList(new ArrayList<PrintJob>(Arrays.asList(rPrintJobArray))); //TODO see if you need to wrap it, since we aren't really changing the data
                    break;
            }
        }
    }

}
