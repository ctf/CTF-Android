package ca.mcgill.science.ctf.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.allanwang.capsule.library.adapters.CapsuleAdapter;
import ca.allanwang.capsule.library.logging.CLog;
import ca.allanwang.capsule.library.utils.ParcelUtils;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import ca.mcgill.science.ctf.Events;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.adapter.PrintJobAdapter;
import ca.mcgill.science.ctf.auth.AccountUtil;
import ca.mcgill.science.ctf.enums.DataType;
import ca.mcgill.science.ctf.tepid.PrintJob;

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
    @BindView(R.id.my_account_color)
    AppCompatCheckBox turnColor;
    private boolean rColor;
    private String rNickname, rQuota;
    private PrintJob[] rPrintJobs;

    private static final String BUNDLE_QUOTA = "quota", BUNDLE_PRINT_JOBS = "print_jobs", BUNDLE_NICKNAME = "nickname", BUNDLE_COMPLETE = "complete", BUNDLE_COLOR = "has_color";


    public static MyAccountFragment newInstance(String quota, PrintJob[] printJobs, String nickname, boolean color) {
        ParcelUtils parcelUtils = new ParcelUtils<>(new MyAccountFragment());
        if (parcelUtils.putNullStatus(BUNDLE_COMPLETE, quota, printJobs, nickname)) {
            parcelUtils.putString(BUNDLE_QUOTA, quota)
                    .putParcelableArray(BUNDLE_PRINT_JOBS, printJobs)
                    .putString(BUNDLE_NICKNAME, nickname)
                    .putBoolean(BUNDLE_COLOR, color);
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
        rColor = args.getBoolean(BUNDLE_COLOR);
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linear = (LinearLayout) inflate(R.layout.fragment_my_account);
        bindButterKnife(linear);
        if (rQuota == null || rPrintJobs == null || rNickname == null) showRefresh();
        cLinear.addView(linear, 0);
        turnColor.setChecked(rColor);
        turnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnColor.toggle();
            }
        });

        usernameView.setText(getString(R.string.dashboard_username_text, AccountUtil.getNick()));
        changeNickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rNickname = nickView.getText().toString();
                if (!rNickname.isEmpty()) {
                    showRefresh();
                    postEvent(new Events.SingleDataEvent(DataType.Single.NICKNAME, rNickname)); //Send load request; text will change if accepted
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
    public boolean onLoadEvent(Events.LoadEvent event) {
        if (!isLoadValid(event, DataType.Single.NICKNAME, DataType.Single.QUOTA, DataType.Single.USER_JOBS, DataType.Single.COLOR))
            return false;
        switch (event.getType()) {
            case NICKNAME:
                rNickname = String.valueOf(event.getData());
                break;
            case QUOTA:
                rQuota = String.valueOf(event.getData());
                break;
            case USER_JOBS:
                rPrintJobs = (PrintJob[]) event.getData();
                break;
            case COLOR:
                rColor = (boolean) event.getData();
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

