package com.ctf.mcgill.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ctf.mcgill.R;
import com.ctf.mcgill.adapter.RoomInfoAdapter;
import com.ctf.mcgill.auth.AccountUtil;
import com.ctf.mcgill.enums.DataType;
import com.ctf.mcgill.events.LoadEvent;
import com.ctf.mcgill.requests.DestinationsRequest;
import com.ctf.mcgill.requests.JobsRequest;
import com.ctf.mcgill.requests.QueuesRequest;
import com.ctf.mcgill.requests.QuotaRequest;
import com.ctf.mcgill.tepid.Destination;
import com.ctf.mcgill.tepid.PrintJob;
import com.ctf.mcgill.tepid.PrintQueue;
import com.ctf.mcgill.tepid.RoomInformation;
import com.ocpsoft.pretty.time.PrettyTime;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pitchedapps.capsule.library.adapters.CapsuleAdapter;
import com.pitchedapps.capsule.library.utils.AnimUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import static com.ctf.mcgill.enums.DataType.Single.Quota;

public class DashboardFragment extends BaseFragment<RoomInformation, RoomInfoAdapter.ViewHolder> {

    @BindView(R.id.dashboard_username)
    TextView usernameView;
    @BindView(R.id.dashboard_quota)
    TextView quotaView;
    @BindView(R.id.dashboard_last_print_job)
    TextView lastJobView;
    @BindView(R.id.dashboard_container)
    LinearLayout parentLayout;

    @Override
    protected void updateList(List<RoomInformation> oldList) {

    }

    @Override
    @CallSuper
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linear = (LinearLayout) inflate(R.layout.fragment_dashboard);
        bindButterKnife(linear);
        cLinear.addView(linear, 0);
        usernameView.setText(getString(R.string.dashboard_username_text, AccountUtil.getNick()));
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
        switch (event.type) {
            case Quota:
                if (isLoadSuccessful(event)) {
                    quotaView.setText(String.valueOf(event.data));
                    AnimUtils.fadeIn(getContext(), quotaView, 0, 1000);
                }
                break;
            case UserJobs:
                if (isLoadSuccessful(event)) {
                    PrintJob[] p = (PrintJob[]) event.data;
                    Date last;
                    if (p == null || p[0] == null) {
                        last = new Date();
                    } else {
                        last = p[0].started;
                    }
                    PrettyTime pt = new PrettyTime();
                    lastJobView.setText(getString(R.string.dashboard_last_job_text, pt.format(last)));
                    AnimUtils.fadeIn(getContext(), lastJobView, 0, 1000);
                }
                break;
            case Queues: //TODO see where you want to handle RoomInfo generation
                break;
        }
    }

    @Override
    public void updateData() {

    }

    @Override
    public void getArgs(Bundle args) {

    }


    private final class DestinationsRequestListener implements RequestListener<Map> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Destinations request failed...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(Map map) {
            destinations = map;
            requestManager.execute(new QueuesRequest(token), KEY_QUEUES, DurationInMillis.ONE_MINUTE, new QueuesRequestListener());
        }
    }

    private final class QueuesRequestListener implements RequestListener<List> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Queues request failed...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(List list) { //todo clean this up, e.g., getView() methods for each type of item that sets the correct params, do the same thing in room fragment
            List<RoomInformation> rooms = new ArrayList<>();
            for (Object q : list) {
                String name = ((PrintQueue) q).name;
                RoomInformation roomInfo = new RoomInformation(name, true); //TODO put actual computer status

                if (destinations != null) {
                    for (String d : ((PrintQueue) q).destinations) {
                        roomInfo.addPrinter(destinations.get(d).getName(), destinations.get(d).isUp());
                    }
                }
                rooms.add(roomInfo);
            }
            cAdapter.updateList(rooms);
        }
    }

}
