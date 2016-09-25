package com.example.ctfdemo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ctfdemo.R;
import com.example.ctfdemo.adapter.PrintJobAdapter;
import com.example.ctfdemo.auth.AccountUtil;
import com.example.ctfdemo.requests.CTFSpiceService;
import com.example.ctfdemo.requests.NickRequest;
import com.example.ctfdemo.requests.QuotaRequest;
import com.example.ctfdemo.requests.UserJobsRequest;
import com.example.ctfdemo.tepid.PrintJob;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MyAccountFragment extends Fragment {

    private static final String KEY_USERNAME = "username", KEY_TOKEN = "token";
    TextView quotaView, usernameView;
    private SpiceManager requestManager = new SpiceManager(CTFSpiceService.class);
    private String token;
    private RecyclerView mRecyclerView;

    public static MyAccountFragment newInstance(String token) {
        MyAccountFragment frag = new MyAccountFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TOKEN, token);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_account, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            token = args.getString(KEY_TOKEN);
        }
        populateUI();
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

    private void populateUI() {
        usernameView = (TextView) getView().findViewById(R.id.my_account_username);
        usernameView.setText(getString(R.string.dashboard_username_text, AccountUtil.getNick()));
        quotaView = (TextView) getView().findViewById(R.id.my_account_quota);
        quotaView.setText(getString(R.string.dashboard_quota_text, ""));
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.print_history);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Button changeNick = (Button) getView().findViewById(R.id.change_nick);
        changeNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = (EditText) getView().findViewById(R.id.nick_field);
                String nick = et.getText().toString();

                if (!nick.isEmpty()) {
                    requestManager.execute(new NickRequest(token, nick), new NickRequestListener());
                }
            }
        });

        performQuotaRequest(token);
        performUserJobsRequest(token);
    }

    private void performQuotaRequest(String token) {
        requestManager.execute(new QuotaRequest(token), new QuotaRequestListener());
    }

    private void performUserJobsRequest(String token) {
        requestManager.execute(new UserJobsRequest(token), new UserJobsRequestListener());
    }

    private final class QuotaRequestListener implements RequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Error: failed to load data from TEPID server.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(String quota) {
            MyAccountFragment.this.getActivity().setProgressBarIndeterminateVisibility(false);
            quotaView.setText(getString(R.string.dashboard_quota_text, quota));
        }
    }

    private final class UserJobsRequestListener implements  RequestListener<PrintJob[]> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Error: failed to load data from TEPID server.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(PrintJob[] printJobs) {
            mRecyclerView.setAdapter(new PrintJobAdapter(getActivity(), new ArrayList<PrintJob>(Arrays.asList(printJobs)), PrintJobAdapter.MY_ACCOUNT));
        }
    }

    private final class NickRequestListener implements RequestListener<String> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Nick request failed...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(String nick) {
            AccountUtil.updateNick(nick);
            usernameView.setText(getString(R.string.dashboard_username_text, AccountUtil.getNick()));
        }
    }

//todo add edittext + button, onclicklistener sends nickrequest, responselistener updates the username current
}

