package com.ctf.mcgill.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ctf.mcgill.R;
import com.ctf.mcgill.adapter.PrintJobAdapter;
import com.ctf.mcgill.auth.AccountUtil;
import com.ctf.mcgill.requests.JobsRequest;
import com.ctf.mcgill.requests.NickRequest;
import com.ctf.mcgill.requests.QuotaRequest;
import com.ctf.mcgill.tepid.PrintJob;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class MyAccountFragment extends BaseFragment {

    public static final String TAG = "MY_ACCOUNT_FRAGMENT";

    @BindView(R.id.print_history)
    RecyclerView mRecyclerView;
    @BindView(R.id.my_account_quota)
    TextView quotaView;
    @BindView(R.id.my_account_username)
    TextView usernameView;
    @BindView(R.id.nick_field)
    EditText nickView;
    @BindView(R.id.change_nick)
    Button changeNickView;
    private PrintJobAdapter mPrintAdapter;

    public static MyAccountFragment newInstance(String token) {
        return (MyAccountFragment) fragmentWithToken(new MyAccountFragment(), token);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_my_account, container, false);
        bindButterKnife(rootView);
        mPrintAdapter = new PrintJobAdapter(getActivity(), null, PrintJobAdapter.MY_ACCOUNT);
        usernameView.setText(getString(R.string.dashboard_username_text, AccountUtil.getNick()));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mPrintAdapter);
        mRecyclerView.setItemAnimator(new FadeInLeftAnimator(new FastOutLinearInInterpolator()));
        changeNickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nick = nickView.getText().toString();

                if (!nick.isEmpty()) {
                    requestManager.execute(new NickRequest(token, nick), new NickRequestListener());
                }
            }
        });

        return rootView;
    }

    @Override
    protected void getUIData(SpiceManager requestManager) {
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

    private final class UserJobsRequestListener implements RequestListener<PrintJob[]> {

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

