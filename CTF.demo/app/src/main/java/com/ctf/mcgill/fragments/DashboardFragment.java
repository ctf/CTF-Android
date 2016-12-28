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
import com.pitchedapps.capsule.library.logging.CLog;
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

    private String rQuota;
    private PrintJob[] rPrintJobs;
    private ArrayList<RoomInformation> rRoomInfo;

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
            return;
        }
        rQuota = args.getString(BUNDLE_QUOTA);
        rPrintJobs = (PrintJob[]) args.getParcelableArray(BUNDLE_PRINT_JOBS);
        rRoomInfo = args.getParcelableArrayList(BUNDLE_ROOM_INFO);
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linear = (LinearLayout) inflate(R.layout.fragment_dashboard);
        bindButterKnife(linear);
        showRefresh();
        cLinear.addView(linear, 0);
        usernameView.setText(getString(R.string.dashboard_username_text, AccountUtil.getNick()));
        CLog.e("Call Load");
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
        return DataType.Category.DASHBOARD;
    }

    @Subscribe
    @Override
    public void onLoadEvent(LoadEvent event) {
        if (event.isActivityOnly()) return;
        hideRefresh(); //TODO hide after all events loaded
        switch (event.type) {
            case QUOTA:
                if (isLoadSuccessful(event)) {
                    rQuota = String.valueOf(event.data);
                } else return;
                break;
            case USER_JOBS:
                if (isLoadSuccessful(event)) {
                    rPrintJobs = (PrintJob[]) event.data;
                } else return;
                break;
            case QUEUES: //Already changed into List<RoomInformation> through RequestActivity
                if (isLoadSuccessful(event)) {
                    rRoomInfo = (ArrayList<RoomInformation>) event.data;
                } else return;
                break;
            default: //Event is not one of the ones we wish to see; don't bother updating content for it
                return;
        }
        updateContent(event.type);
    }

    @Override
    public void updateContent(DataType.Single... types) {
        for (DataType.Single type : types) {
            switch (type) {
                case QUOTA:
                    if (rQuota == null) continue;
                    quotaView.setText(String.valueOf(rQuota));
                    AnimUtils.fadeIn(getContext(), quotaView, 0, 1000);
                    break;
                case USER_JOBS:
                    Date last;
                    if (rPrintJobs == null || rPrintJobs[0] == null) {
                        last = new Date();
                    } else {
                        last = rPrintJobs[0].started;
                    }
                    PrettyTime pt = new PrettyTime();
                    lastJobView.setText(getString(R.string.dashboard_last_job_text, pt.format(last)));
                    AnimUtils.fadeIn(getContext(), lastJobView, 0, 1000);
                    break;
                case QUEUES: //Already changed into List<RoomInformation> through RequestActivity
                    if (rRoomInfo == null) continue;
                    cAdapter.updateList(rRoomInfo);
                    break;
            }
        }
    }

}
