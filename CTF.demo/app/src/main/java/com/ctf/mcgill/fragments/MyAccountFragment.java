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

import com.ctf.mcgill.R;
import com.ctf.mcgill.adapter.PrintJobAdapter;
import com.ctf.mcgill.auth.AccountUtil;
import com.ctf.mcgill.enums.DataType;
import com.ctf.mcgill.events.LoadEvent;
import com.ctf.mcgill.events.SingleDataEvent;
import com.ctf.mcgill.tepid.PrintJob;
import com.pitchedapps.capsule.library.adapters.CapsuleAdapter;
import com.pitchedapps.capsule.library.utils.ParcelUtils;

import org.greenrobot.eventbus.Subscribe;

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

    private String rNickname, rQuota;
    private PrintJob[] rPrintJobs;

    private static final String BUNDLE_QUOTA = "quota", BUNDLE_PRINT_JOBS = "print_jobs", BUNDLE_NICKNAME = "nickname", BUNDLE_COMPLETE = "complete";

    public static MyAccountFragment newInstance(String quota, PrintJob[] printJobs, String nickname) {
        ParcelUtils parcelUtils = new ParcelUtils<>(new MyAccountFragment());
        if (parcelUtils.putNullStatus(BUNDLE_COMPLETE, quota, printJobs, nickname)) {
            parcelUtils.putString(BUNDLE_QUOTA, quota)
                    .putParcelableArray(BUNDLE_PRINT_JOBS, printJobs)
                    .putString(BUNDLE_NICKNAME, nickname);
        }
        return (MyAccountFragment) parcelUtils.create();
    }

    @Override
    public void getArgs(Bundle args) {
        if (args == null || !args.getBoolean(BUNDLE_COMPLETE, false)) {
            updateList(null);
            return;
        }
        rQuota = args.getString(BUNDLE_QUOTA);
        rPrintJobs = (PrintJob[]) args.getParcelableArray(BUNDLE_PRINT_JOBS);
        rNickname = args.getString(BUNDLE_NICKNAME);
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linear = (LinearLayout) inflate(R.layout.fragment_my_account);
        bindButterKnife(linear);
        if (rQuota == null || rPrintJobs == null || rNickname == null) showRefresh();
        cLinear.addView(linear, 0);

        usernameView.setText(getString(R.string.dashboard_username_text, AccountUtil.getNick()));
        changeNickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rNickname = nickView.getText().toString();
                if (!rNickname.isEmpty()) {
                    postEvent(new SingleDataEvent(DataType.Single.NICKNAME, rNickname)); //Send load request; text will change if accepted
                }
            }
        });
        updateContent(getDataCategory().getContent());
    }

    @Override
    public int getTitleId() {
        return R.string.userinfo;
    }

    @Override
    protected CapsuleAdapter<PrintJob, PrintJobAdapter.ViewHolder> getAdapter(Context context) {
        return new PrintJobAdapter(context, null, PrintJobAdapter.TableType.MY_ACCOUNT);
    }

    @Override
    public DataType.Category getDataCategory() {
        return DataType.Category.MY_ACCOUNT;
    }

    @Override
    public boolean onLoadEvent(LoadEvent event) {
        if (!isLoadValid(event, DataType.Single.NICKNAME, DataType.Single.QUOTA, DataType.Single.USER_JOBS))
            return false;
        switch (event.type) {
            case NICKNAME:
                rNickname = String.valueOf(event.data);
                break;
            case QUOTA:
                rQuota = String.valueOf(event.data);
                break;
            case USER_JOBS:
                rPrintJobs = (PrintJob[]) event.data;
                break;
        }
        return true;
    }

    @Override
    public void updateContent(DataType.Single... types) {
        for (DataType.Single type : types) {
            switch (type) {
                case NICKNAME:
                    if (rNickname == null) continue;
                    AccountUtil.updateNick(rNickname);
                    usernameView.setText(getString(R.string.dashboard_username_text, rNickname));
                    break;
                case QUOTA:
                    if (rQuota == null) continue;
                    quotaView.setText(getString(R.string.dashboard_quota_text, rQuota));
                    break;
                case USER_JOBS:
                    if (rPrintJobs == null) continue;
                    cAdapter.updateList(new ArrayList<>(Arrays.asList(rPrintJobs)));
                    break;
            }
        }
    }

}

