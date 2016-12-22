package com.ctf.mcgill.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.pitchedapps.capsule.library.adapters.CapsuleAdapter;
import com.pitchedapps.capsule.library.event.SnackbarEvent;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;

public class MyAccountFragment extends BaseFragment<PrintJob, PrintJobAdapter.ViewHolder> {

    public static final String TAG = "MY_ACCOUNT_FRAGMENT";

    @BindView(R.id.my_account_quota)
    TextView quotaView;
    @BindView(R.id.my_account_username)
    TextView usernameView;
    @BindView(R.id.nick_field)
    EditText nickView;
    @BindView(R.id.change_nick)
    Button changeNickView;

    public static MyAccountFragment newInstance(String token) {
        return (MyAccountFragment) fragmentWithToken(new MyAccountFragment(), token);
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linear = (LinearLayout) inflate(R.layout.fragment_my_account);
        bindButterKnife(linear);
        cLinear.addView(linear, 0);

        usernameView.setText(getString(R.string.dashboard_username_text, AccountUtil.getNick()));
        changeNickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nick = nickView.getText().toString();
                if (!nick.isEmpty()) {
                    requestManager.execute(new NickRequest(token, nick), new NickRequestListener());
                }
            }
        });
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

    @Override
    protected CapsuleAdapter<PrintJob, PrintJobAdapter.ViewHolder> getAdapter(Context context) {
        return new PrintJobAdapter(context, null, PrintJobAdapter.TableType.MY_ACCOUNT);
    }

    private final class QuotaRequestListener extends MyRequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            super.onRequestFailure(spiceException);
            Toast.makeText(getActivity(), "Error: failed to load data from TEPID server.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(String quota) {
            super.onRequestSuccess(quota);
//            MyAccountFragment.this.getActivity().setProgressBarIndeterminateVisibility(false); //TODO check if necessary

            quotaView.setText(getString(R.string.dashboard_quota_text, quota));
        }
    }

    private final class UserJobsRequestListener extends MyRequestListener<PrintJob[]> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            super.onRequestFailure(spiceException);
            Toast.makeText(getActivity(), "Error: failed to load data from TEPID server.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(PrintJob[] printJobs) {
            super.onRequestSuccess(printJobs);
            cAdapter.updateList(new ArrayList<PrintJob>(Arrays.asList(printJobs)));
        }
    }

    private final class NickRequestListener extends MyRequestListener<String> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            super.onRequestFailure(spiceException);
            snackbar(new SnackbarEvent("Nick request failed..."));
//            Toast.makeText(getActivity(), "Nick request failed...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(String nick) {
            super.onRequestSuccess(nick);
            AccountUtil.updateNick(nick);
            usernameView.setText(getString(R.string.dashboard_username_text, AccountUtil.getNick()));
        }
    }

}

