package com.ctf.mcgill;

import android.content.Context;
import android.widget.Toast;

import com.ctf.mcgill.enums.DataType;
import com.ctf.mcgill.events.LoadEvent;
import com.ctf.mcgill.fragments.BaseFragment;
import com.ctf.mcgill.fragments.DashboardFragment;
import com.ctf.mcgill.requests.CTFSpiceService;
import com.ctf.mcgill.requests.DestinationsRequest;
import com.ctf.mcgill.requests.JobsRequest;
import com.ctf.mcgill.requests.QueuesRequest;
import com.ctf.mcgill.requests.QuotaRequest;
import com.ctf.mcgill.tepid.PrintJob;
import com.ctf.mcgill.tepid.PrintQueue;
import com.ctf.mcgill.tepid.RoomInformation;
import com.ocpsoft.pretty.time.PrettyTime;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pitchedapps.capsule.library.activities.CapsuleActivityFrame;
import com.pitchedapps.capsule.library.utils.AnimUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Allan Wang on 26/12/2016.
 * <p>
 * Requests should be loaded asap, not just when we switch to the tab.
 */

public abstract class SpiceActivity extends CapsuleActivityFrame {

    private String mToken;
    private Context mContext;
    private SpiceManager mRequestManager = new SpiceManager(CTFSpiceService.class);
    private static final String KEY_QUOTA = "QUOTA", KEY_LAST_JOB = "LAST JOB", KEY_QUEUES = "QUEUES", KEY_DESTINATIONS = "DESTINATIONS";

    @Subscribe
    public void loadData(DataType.Category type) {
        for (DataType.Single s : type.getContent()) loadData(s);
    }

    @Subscribe
    public void loadData(DataType.Single type) {
        switch (type) {
            mRequestManager.execute(new QuotaRequest(mToken), KEY_QUOTA, DurationInMillis.ONE_MINUTE, new QuotaRequestListener());
            mRequestManager.execute(new JobsRequest(mToken), KEY_LAST_JOB, DurationInMillis.ONE_MINUTE, new DashboardFragment.UserJobsRequestListener());
            mRequestManager.execute(new DestinationsRequest(mToken), KEY_DESTINATIONS, DurationInMillis.ONE_MINUTE, new DashboardFragment.DestinationsRequestListener());
        }
    }

    private void postLoadEvent(DataType type, boolean isSuccessful, Object data) {
        postEvent(new LoadEvent(type, isSuccessful, data));
    }

     /*
     * All the listeners used with the request manager
     */

    private final class QuotaRequestListener implements RequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            //todo improve error handling, maybe an "error fragment" w/ sadcat?
            Toast.makeText(getActivity(), "Quota request failed...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(String quota) {
            quotaView.setText(getString(R.string.dashboard_quota_text, quota));
            AnimUtils.fadeIn(getContext(), quotaView, 0, 1000);
        }
    }

    private final class UserJobsRequestListener implements RequestListener<PrintJob[]> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "User jobs request failed...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(PrintJob[] p) {
            Date last = p[0].started;
            PrettyTime pt = new PrettyTime(); //todo if date is null pt uses current time
            lastJobView.setText(getString(R.string.dashboard_last_job_text, pt.format(last)));
            AnimUtils.fadeIn(getContext(), lastJobView, 0, 1000);
        }
    }

    private final class DestinationsRequestListener implements RequestListener<Map> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Destinations request failed...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(Map map) {
            destinations = map;
            requestManager.execute(new QueuesRequest(token), KEY_QUEUES, DurationInMillis.ONE_MINUTE, new DashboardFragment.QueuesRequestListener());
        }
    }

    //Called from within Destinations Request Listener
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
