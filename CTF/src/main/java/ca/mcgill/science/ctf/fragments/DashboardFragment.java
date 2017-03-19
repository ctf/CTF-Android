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
import ca.allanwang.swiperecyclerview.library.adapters.AnimationAdapter;
import ca.allanwang.swiperecyclerview.library.interfaces.ISwipeRecycler;
import ca.allanwang.swiperecyclerview.library.items.CheckBoxItem;
import ca.mcgill.science.ctf.Events;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.adapter.RoomInfoAdapter;
import ca.mcgill.science.ctf.auth.AccountUtil;
import ca.mcgill.science.ctf.enums.DataType;
import ca.mcgill.science.ctf.iitems.RoomInfoItem;
import ca.mcgill.science.ctf.models.PrintData;
import ca.mcgill.science.ctf.tepid.PrintJob;
import ca.mcgill.science.ctf.tepid.RoomInformation;

public class DashboardFragment extends BaseFragment<RoomInfoItem> {

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

    @Override
    public void requestData() {

    }

    @Override
    public void onLoadEventSubscription(Events.LoadEvent event) {

    }

    @Override
    protected void configAdapter(AnimationAdapter<RoomInfoItem> adapter) {

    }

    @Override
    public void onRefresh(ISwipeRecycler.OnRefreshStatus onRefreshStatus) {

    }
}
