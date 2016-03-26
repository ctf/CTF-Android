package com.example.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ctfdemo.R;
import com.example.ctfdemo.TableData;
import com.example.ctfdemo.mPrintHistTableRowAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by erasmas on 1/8/16.
 */
// this fragment is swapped out by the fragment manager in the main activity
// probably needs to be replaced by an AppCompatActivity depending on the functionality this page needs
// on the bright side at least the navigation drawer functions on this page...
public class UserInfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_userinfo, container, false);

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.print_history);

        // print history table uses a linear layout manager and a custom adapter for table rows
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new mPrintHistTableRowAdapter(getActivity(), getData()));
        return rootView;
    }

    // make a bunch of TableData objects to pass to the table row adapter, depending on the current room displayed
    public static List<TableData> getData() {
        List<TableData> data = new ArrayList<>();
        // TODO: query server for recent job info and store in array
        String[] jobIds = {"JOBS","test user","test user","test user","test user","test user","test user","test user","test user"};
        String[] dates = {"DATES","test user","test user","test user","test user","test user","test user","test user","test user"};
        String[] pageCounts = {"PAGES","test date","test date","test date","test date","test date","test date","test date","test date"};
        for (int i=0; i<jobIds.length && i<dates.length && i<pageCounts.length; i++) {
            TableData currentData = new TableData();
            currentData.setJobId(jobIds[i]);
            currentData.setDate(dates[i]);
            currentData.setPageCount(pageCounts[i]);
            data.add(currentData);
        }
        return data;
    }

}

