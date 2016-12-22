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
import com.pitchedapps.capsule.library.adapters.CapsuleAdapter;
import com.pitchedapps.capsule.library.utils.AnimUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class DashboardFragment extends BaseFragment<RoomInformation, RoomInfoAdapter.ViewHolder> {

    public static final String TAG = "MAIN_FRAGMENT";
    @BindView(R.id.dashboard_username)
    TextView usernameView;
    @BindView(R.id.dashboard_quota)
    TextView quotaView;
    @BindView(R.id.dashboard_last_print_job)
    TextView lastJobView;
    @BindView(R.id.dashboard_container)
    LinearLayout parentLayout;
    private Map<String, Destination> destinations;
    private static final String KEY_QUOTA = "QUOTA", KEY_LAST_JOB = "LAST JOB", KEY_QUEUES = "QUEUES", KEY_DESTINATIONS = "DESTINATIONS";

    public static DashboardFragment newInstance(String token) {
        return (DashboardFragment) fragmentWithToken(new DashboardFragment(), token);
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
    protected void getUIData(SpiceManager requestManager) {
        requestManager.execute(new QuotaRequest(token), KEY_QUOTA, DurationInMillis.ONE_MINUTE, new QuotaRequestListener());
        requestManager.execute(new JobsRequest(token), KEY_LAST_JOB, DurationInMillis.ONE_MINUTE, new UserJobsRequestListener());
        requestManager.execute(new DestinationsRequest(token), KEY_DESTINATIONS, DurationInMillis.ONE_MINUTE, new DestinationsRequestListener());
    }

    @Override
    public int getTitleId() {
        return R.string.dashboard;
    }

    private final class QuotaRequestListener extends MyRequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            super.onRequestFailure(spiceException);
            //todo improve error handling, maybe an "error fragment" w/ sadcat?
            Toast.makeText(getActivity(), "Quota request failed...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(String quota) {
            super.onRequestSuccess(quota);
            quotaView.setText(getString(R.string.dashboard_quota_text, quota));
            AnimUtils.fadeIn(getContext(), quotaView, 0, 1000);
        }
    }

    private final class UserJobsRequestListener extends MyRequestListener<PrintJob[]> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            super.onRequestFailure(spiceException);
            Toast.makeText(getActivity(), "User jobs request failed...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(PrintJob[] p) {
            super.onRequestSuccess(p);
            Date last = p[0].started;
            PrettyTime pt = new PrettyTime(); //todo if date is null pt uses current time
            lastJobView.setText(getString(R.string.dashboard_last_job_text, pt.format(last)));
            AnimUtils.fadeIn(getContext(), lastJobView, 0, 1000);
        }
    }

    private final class DestinationsRequestListener extends MyRequestListener<Map> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            super.onRequestFailure(spiceException);
            Toast.makeText(getActivity(), "Destinations request failed...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(Map map) {
            super.onRequestSuccess(map);
            destinations = map;
            requestManager.execute(new QueuesRequest(token), KEY_QUEUES, DurationInMillis.ONE_MINUTE, new QueuesRequestListener());
        }
    }

    private final class QueuesRequestListener extends MyRequestListener<List> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            super.onRequestFailure(spiceException);
            Toast.makeText(getActivity(), "Queues request failed...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(List list) { //todo clean this up, e.g., getView() methods for each type of item that sets the correct params, do the same thing in room fragment
            super.onRequestSuccess(list);
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
