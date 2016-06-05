package com.example.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ctfdemo.PrintJob;
import com.example.ctfdemo.PrintJobAdapter;
import com.example.ctfdemo.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by erasmas on 19/03/16.
 * a single tab on the room info fragment
 */
public class RoomTabFragment extends Fragment {

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
