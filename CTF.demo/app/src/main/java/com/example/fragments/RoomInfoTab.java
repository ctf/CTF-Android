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

import com.example.ctfdemo.R;
import com.example.ctfdemo.TableData;
import com.example.ctfdemo.mTableRowAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by erasmas on 19/03/16.
 * a single tab on the room info fragment
 */
public class RoomInfoTab extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RoomInfoTab newInstance(int sectionNumber) {
        RoomInfoTab fragment = new RoomInfoTab();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    // Required empty public constructor
    public RoomInfoTab() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // rootView occupies screen space below tabs
        View rootView = inflater.inflate(R.layout.fragment_room_info_tabs, container, false);
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recent_jobs);

        Bundle bundle = getArguments();
        int position = bundle.getInt(ARG_SECTION_NUMBER); // position corresponds to room number 1B16, etc.

        // recent jobs table uses a linear layout manager and a custom adapter for table rows
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new mTableRowAdapter(getActivity(), getData(position)));

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

    // make a bunch of TableData objects to pass to the table row adapter, depending on the current room displayed
    public static List<TableData> getData(int position) {
        List<TableData> data = new ArrayList<>();
        // TODO: query server for recent job info and store in array
        String[] users = {""+position,"test user","test user","test user","test user","test user","test user","test user","test user"};
        String[] dates = {""+position,"test date","test date","test date","test date","test date","test date","test date","test date"};
        for (int i=0; i<users.length && i<dates.length; i++) {
            TableData currentData = new TableData();
            currentData.setUser(users[i]);
            currentData.setDate(dates[i]);
            data.add(currentData);
        }
        return data;
    }

    private void showRoomMapDialog() {
        FragmentManager fm = getFragmentManager();
        RoomMapFragment roomMapFragment = RoomMapFragment.newInstance("dummy_title");
        roomMapFragment.show(fm, "room_map");
    }

}
