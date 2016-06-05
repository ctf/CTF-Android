package com.example.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ctfdemo.PrintJob;
import com.example.ctfdemo.R;
import com.example.ctfdemo.PrintJobAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by erasmas on 1/8/16.
 */
public class MyAccountFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_account, container, false);

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.print_history);

        // print history table uses a linear layout manager and a custom adapter for table rows
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new PrintJobAdapter(getActivity(), getData(), PrintJobAdapter.MY_ACCOUNT));
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

}

