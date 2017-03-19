package ca.mcgill.science.ctf.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ocpsoft.pretty.time.PrettyTime;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import ca.allanwang.capsule.library.adapters.CapsuleAdapter;
import ca.allanwang.capsule.library.logging.CLog;
import ca.allanwang.capsule.library.utils.AnimUtils;
import ca.allanwang.capsule.library.utils.ParcelUtils;
import ca.allanwang.swiperecyclerview.library.items.CheckBoxItem;
import ca.mcgill.science.ctf.Events;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.adapter.RoomInfoAdapter;
import ca.mcgill.science.ctf.auth.AccountUtil;
import ca.mcgill.science.ctf.enums.DataType;
import ca.mcgill.science.ctf.models.PrintData;
import ca.mcgill.science.ctf.tepid.PrintJob;
import ca.mcgill.science.ctf.tepid.RoomInformation;

public class DashboardFragment extends BaseFragment<PrintData> {

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
        ParcelUtils parcelUtils = new ParcelUtils<>(new DashboardFragment());
        if (parcelUtils.putNullStatus(BUNDLE_COMPLETE, quota, printJobs, roomInfo)) {
            parcelUtils.putString(BUNDLE_QUOTA, quota)
                    .putParcelableArray(BUNDLE_PRINT_JOBS, printJobs)
                    .putParcelableArrayList(BUNDLE_ROOM_INFO, roomInfo);
        }
        CheckBoxItem
        return (DashboardFragment) parcelUtils.create();
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
        if (rQuota == null || rPrintJobs == null || rRoomInfo == null) showRefresh();
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
        return DataType.Category.DASHBOARD;
    }

    //TODO remove flicker on refresh and keep
    @Override
    public boolean onLoadEvent(Events.LoadEvent event) {
        if (!isLoadValid(event, DataType.Single.QUOTA, DataType.Single.USER_JOBS, DataType.Single.QUEUES))
            return false;
        switch (event.getType()) {
            case QUOTA:
                rQuota = String.valueOf(event.getData());
                break;
            case USER_JOBS:
                rPrintJobs = (PrintJob[]) event.getData();
                break;
            case QUEUES:
                CLog.d("New Queue");
                rRoomInfo = (ArrayList<RoomInformation>) event.getData();
                break;
        }
        return true;
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
