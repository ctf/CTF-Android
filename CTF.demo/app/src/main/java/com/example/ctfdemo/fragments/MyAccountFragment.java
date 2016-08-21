package com.example.ctfdemo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ctfdemo.requests.CTFSpiceService;
import com.example.ctfdemo.tepid.PrintJob;
import com.example.ctfdemo.adapter.PrintJobAdapter;
import com.example.ctfdemo.requests.QuotaRequest;
import com.example.ctfdemo.R;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyAccountFragment extends Fragment {

    private static final String KEY_USERNAME = "username";
    TextView quotaView;
    private SpiceManager requestManager = new SpiceManager(CTFSpiceService.class);

    public static MyAccountFragment newInstance(String username) {
        MyAccountFragment frag = new MyAccountFragment();
        Bundle args = new Bundle();
        args.putString(KEY_USERNAME, username);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_account, container, false);

        quotaView = (TextView) rootView.findViewById(R.id.quota);

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.print_history);

        // print history table uses a linear layout manager and a custom adapter for table rows
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new PrintJobAdapter(getActivity(), getPrintHistoryData(), PrintJobAdapter.MY_ACCOUNT));
        return rootView;
    }
    /**
     * get a bunch of PrintJob objects to pass to the PrintJobAdapter,
     * which extracts the relevant data and formats it depending on the
     * type of table it is filling (user's job history or room queues)
     */
    public List<PrintJob> getPrintHistoryData() {
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

    private void performQuotaRequest(String token) {
        quotaView.setText(getString(R.string.dashboard_quota_text, ""));

        MyAccountFragment.this.getActivity().setProgressBarIndeterminateVisibility(true);

        QuotaRequest request = new QuotaRequest(token);
        requestManager.execute(request, new QuotaRequestListener());
    }

    private final class QuotaRequestListener implements RequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Error: failed to load data from TEPID server.", Toast.LENGTH_SHORT).show();
            MyAccountFragment.this.getActivity().setProgressBarIndeterminateVisibility(false);
        }

        @Override
        public void onRequestSuccess(String quota) {
            MyAccountFragment.this.getActivity().setProgressBarIndeterminateVisibility(false);
            quotaView.setText(getString(R.string.dashboard_quota_text, quota));
        }
    }


}

