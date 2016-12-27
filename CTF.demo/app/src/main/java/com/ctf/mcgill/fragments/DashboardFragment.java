package com.ctf.mcgill.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ctf.mcgill.R;
import com.ctf.mcgill.adapter.RoomInfoAdapter;
import com.ctf.mcgill.auth.AccountUtil;
import com.ctf.mcgill.enums.DataType;
import com.ctf.mcgill.events.LoadEvent;
import com.ctf.mcgill.tepid.PrintJob;
import com.ctf.mcgill.tepid.RoomInformation;
import com.ocpsoft.pretty.time.PrettyTime;
import com.pitchedapps.capsule.library.adapters.CapsuleAdapter;
import com.pitchedapps.capsule.library.utils.AnimUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;

public class DashboardFragment extends BaseFragment<RoomInformation, RoomInfoAdapter.ViewHolder> {

    @BindView(R.id.dashboard_username)
    TextView usernameView;
    @BindView(R.id.dashboard_quota)
    TextView quotaView;
    @BindView(R.id.dashboard_last_print_job)
    TextView lastJobView;
    @BindView(R.id.dashboard_container)
    LinearLayout parentLayout;

    private String mQuota;
    private PrintJob[] mPrintJobs;
    private ArrayList<RoomInformation> mRoomInfo;

    private static final String BUNDLE_QUOTA = "quota", BUNDLE_PRINT_JOBS = "print_jobs", BUNDLE_ROOM_INFO = "room_info", BUNDLE_COMPLETE = "complete";

    public static DashboardFragment newInstance(String quota, PrintJob[] printJobs, ArrayList<RoomInformation> roomInfo) {
        DashboardFragment f = new DashboardFragment();
        Bundle args = new Bundle();
        if (quota == null || printJobs == null || roomInfo == null) {
            args.putBoolean(BUNDLE_COMPLETE, false);
        } else {
            args.putBoolean(BUNDLE_COMPLETE, true);
            args.putString(BUNDLE_QUOTA, quota);
            args.putParcelableArray(BUNDLE_PRINT_JOBS, printJobs);
            args.putParcelableArrayList(BUNDLE_ROOM_INFO, roomInfo);
        }
        f.setArguments(args);
        return f;
    }

    @Override
    public void getArgs(Bundle args) {
        if (args == null || !args.getBoolean(BUNDLE_COMPLETE, false)) {
            updateList(null);
            showRefresh();
            return;
        }
        mQuota = args.getString(BUNDLE_QUOTA);
        mPrintJobs = (PrintJob[]) args.getParcelableArray(BUNDLE_PRINT_JOBS);
        mRoomInfo = args.getParcelableArrayList(BUNDLE_ROOM_INFO);
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linear = (LinearLayout) inflate(R.layout.fragment_dashboard);
        bindButterKnife(linear);
        cLinear.addView(linear, 0);
        usernameView.setText(getString(R.string.dashboard_username_text, AccountUtil.getNick()));
        updateContent(getDataCategory().getContent());
    }

    @Override
    protected CapsuleAdapter<RoomInformation, RoomInfoAdapter.ViewHolder> getAdapter(Context context) {
        return new RoomInfoAdapter(context, null);
    }

    @Override
    public int getTitleId() {
        return R.string.dashboard;
    }

    @Override
    public DataType.Category getDataCategory() {
        return DataType.Category.Dashboard;
    }

    @Subscribe
    @Override
    public void onLoadEvent(LoadEvent event) {
        if (event.isActivityOnly()) return;
        hideRefresh(); //TODO hide after all events loaded
        switch (event.type) {
            case Quota:
                if (isLoadSuccessful(event)) {
                    mQuota = String.valueOf(event.data);
                }
                break;
            case UserJobs:
                if (isLoadSuccessful(event)) {
                    mPrintJobs = (PrintJob[]) event.data;
                }
                break;
            case Queues: //Already changed into List<RoomInformation> through RequestActivity
                if (isLoadSuccessful(event)) {
                    mRoomInfo = (ArrayList<RoomInformation>) event.data;
                }
                break;
        }
        updateContent(event.type);
    }

    @Override
    public void updateContent(DataType.Single... types) {
        for (DataType.Single type : types) {
            switch (type) {
                case Quota:
                    if (mQuota == null) continue;
                    quotaView.setText(String.valueOf(mQuota));
                    AnimUtils.fadeIn(getContext(), quotaView, 0, 1000);
                    break;
                case UserJobs:
                    Date last;
                    if (mPrintJobs == null || mPrintJobs[0] == null) {
                        last = new Date();
                    } else {
                        last = mPrintJobs[0].started;
                    }
                    PrettyTime pt = new PrettyTime();
                    lastJobView.setText(getString(R.string.dashboard_last_job_text, pt.format(last)));
                    AnimUtils.fadeIn(getContext(), lastJobView, 0, 1000);
                    break;
                case Queues: //Already changed into List<RoomInformation> through RequestActivity
                    if (mRoomInfo == null) continue;
                    cAdapter.updateList(mRoomInfo);
                    break;
            }
        }
    }

}
