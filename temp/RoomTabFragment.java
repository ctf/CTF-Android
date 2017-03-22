package ca.mcgill.science.ctf.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ca.allanwang.capsule.library.event.CFabEvent;
import ca.allanwang.capsule.library.event.SnackbarEvent;
import ca.allanwang.capsule.library.fragments.CapsulePageFragment;
import ca.allanwang.capsule.library.utils.ParcelUtils;
import ca.allanwang.capsule.library.views.SwipeRefreshRecyclerView;

import org.apache.commons.lang3.ArrayUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ca.mcgill.science.ctf.Events;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.adapter.PrintJobAdapter;
import ca.mcgill.science.ctf.enums.DataType;
import ca.mcgill.science.ctf.enums.Room;
import ca.mcgill.science.ctf.tepid.Destination;
import ca.mcgill.science.ctf.tepid.PrintJob;
import ca.mcgill.science.ctf.wrappers.DestinationHashMap;
import ca.mcgill.science.ctf.wrappers.RoomPrintJob;

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
    private HashMap<String, Destination> rDesinationMap;


    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static RoomTabFragment newInstance(@NonNull Room roomNumber, EnumMap<Room, RoomPrintJob> roomPrintJobMap, HashMap<String, Destination> destinationMap) {
        ParcelUtils parcelUtils = new ParcelUtils<>(new RoomTabFragment());
        parcelUtils.getBundle().putSerializable(BUNDLE_ROOM, roomNumber);
        PrintJob[] printJobs = null;
        if (roomPrintJobMap != null && roomPrintJobMap.containsKey(roomNumber)) //RoomJobs found; retrieve it
            printJobs = roomPrintJobMap.get(roomNumber).printJobs;
        if (parcelUtils.putNullStatus(BUNDLE_COMPLETE, printJobs, destinationMap)) {
            parcelUtils.putParcelableArray(BUNDLE_PRINT_JOBS, printJobs)
                    .putMap(BUNDLE_DESTINATION_MAP, new DestinationHashMap(destinationMap));
        }
        return (RoomTabFragment) parcelUtils.create();
    }


    @Override
    public void getArgs(Bundle args) { //Should never be null
        rRoom = (Room) args.getSerializable(BUNDLE_ROOM);
        if (!args.getBoolean(BUNDLE_COMPLETE, false)) {
            requestData();
            return;
        }
        rPrintJobArray = (PrintJob[]) args.getParcelableArray(BUNDLE_PRINT_JOBS);
        rDesinationMap = ParcelUtils.getMap(args, BUNDLE_DESTINATION_MAP, DestinationHashMap.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        postEvent(new Events.CategoryDataEvent(getDataCategory(), rRoom));
    }

    /**
     * Wrapper for abstract load event so we don't need to worry about the Subscription annotation
     *
     * @param event loading event sent
     */
    @Subscribe
    @Override
    public final void onLoadEventSubscription(Events.LoadEvent event) {
        if (event.isActivityOnly()) return;
        mRefresher.setRefreshing(false); //TODO hide only after all pending events are received
        if (onLoadEvent(event)) updateContent(event.getType());
    }


    @Override
    public boolean onLoadEvent(Events.LoadEvent event) {
        if (!isLoadValid(event, DataType.Single.ROOM_JOBS, DataType.Single.DESTINATIONS))
            return false;
        switch (event.getType()) {
            case ROOM_JOBS:
                RoomPrintJob roomPrintJob = (RoomPrintJob) event.getData();
                if (roomPrintJob.room != rRoom) break;
                rPrintJobArray = roomPrintJob.printJobs;
                break;
            case DESTINATIONS:
                rDesinationMap = ((HashMap<String, Destination>) event.getData());
        }
        return true;
    }

    /**
     * Specified whether or not eventSubscription should count as a loaded event
     *
     * @param event data event
     * @param types types that are valid subscriptions
     * @return true if event contains a valid type and valid data
     */
    protected final boolean isLoadValid(Events.LoadEvent event, DataType.Single... types) {
        if (!ArrayUtils.contains(types, event.getType())) return false;
        if (event.isSuccessful()) return true;
        if (event.getData() == null) return false; //Error String is null -> Silent error
        snackbar(new SnackbarEvent(String.valueOf(event.getData())));
        return false;
    }

    @Override
    public void updateContent(DataType.Single... types) {
        for (DataType.Single type : types) {
            switch (type) {
                case DESTINATIONS:
                    for (Destination d : rDesinationMap.values()) {
                        String[] id = d.getName().split("-");
                        boolean isUp = d.isUp();
                        if (id[0].equals(rRoom.getRoomName())) {
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
                    mAdapter.updateList(new ArrayList<>(Arrays.asList(rPrintJobArray)));
                    break;
            }
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
