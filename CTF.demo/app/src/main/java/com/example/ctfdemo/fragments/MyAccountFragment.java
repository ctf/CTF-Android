package com.example.ctfdemo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ctfdemo.R;
import com.example.ctfdemo.adapter.PrintJobAdapter;
import com.example.ctfdemo.auth.AccountUtil;
import com.example.ctfdemo.requests.CTFSpiceService;
import com.example.ctfdemo.requests.JobsRequest;
import com.example.ctfdemo.requests.NickRequest;
import com.example.ctfdemo.requests.QuotaRequest;
import com.example.ctfdemo.tepid.PrintJob;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pitchedapps.capsule.library.event.CFabEvent;
import com.pitchedapps.capsule.library.fragments.CapsuleFragment;
import com.pitchedapps.capsule.library.logging.CLog;

import java.util.ArrayList;
import java.util.Arrays;

public class MyAccountFragment extends BaseFragment {

    public static final String TAG = "MY_ACCOUNT_FRAGMENT";

    TextView quotaView, usernameView;
    private RecyclerView mRecyclerView;
    private PrintJobAdapter mPrintAdapter;

    public static MyAccountFragment newInstance(String token) {
        return (MyAccountFragment) fragmentWithToken(new MyAccountFragment(), token);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_my_account, container, false);
        mPrintAdapter = new PrintJobAdapter(getActivity(), null, PrintJobAdapter.MY_ACCOUNT);
        usernameView = (TextView) rootView.findViewById(R.id.my_account_username);
        usernameView.setText(getString(R.string.dashboard_username_text, AccountUtil.getNick()));
        quotaView = (TextView) rootView.findViewById(R.id.my_account_quota);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.print_history);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mPrintAdapter);
        rootView.findViewById(R.id.change_nick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = (EditText) rootView.findViewById(R.id.nick_field);
                String nick = et.getText().toString();

                if (!nick.isEmpty()) {
                    requestManager.execute(new NickRequest(token, nick), new NickRequestListener());
                }
            }
        });

        return rootView;
    }

    @Override
    protected void getUIData() {
        requestManager.execute(new QuotaRequest(token), new QuotaRequestListener());
        requestManager.execute(new JobsRequest(token), new UserJobsRequestListener());
    }

    @Override
    public int getTitleId() {
        return R.string.userinfo;
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
            mPrintAdapter.updateList(new ArrayList<PrintJob>(Arrays.asList(printJobs)));
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

}

