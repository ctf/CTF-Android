package com.ctf.mcgill.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ctf.mcgill.R;
import com.ctf.mcgill.auth.AccountUtil;
import com.ctf.mcgill.requests.DestinationsRequest;
import com.ctf.mcgill.requests.JobsRequest;
import com.ctf.mcgill.requests.QueuesRequest;
import com.ctf.mcgill.requests.QuotaRequest;
import com.ctf.mcgill.tepid.Destination;
import com.ctf.mcgill.tepid.PrintJob;
import com.ctf.mcgill.tepid.PrintQueue;
import com.ocpsoft.pretty.time.PrettyTime;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pitchedapps.capsule.library.utils.AnimUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class DashboardFragment extends BaseFragment{

    public static final String TAG = "MAIN_FRAGMENT";
    @BindView(R.id.dashboard_username) TextView usernameView;
    @BindView(R.id.dashboard_quota) TextView quotaView;
    @BindView(R.id.dashboard_last_print_job) TextView lastJobView;
    @BindView(R.id.dashboard_container) LinearLayout parentLayout;
    private Map<String, Destination> destinations;
    private static final String KEY_QUOTA = "QUOTA", KEY_LAST_JOB = "LAST JOB", KEY_QUEUES = "QUEUES", KEY_DESTINATIONS = "DESTINATIONS";

    public static DashboardFragment newInstance(String token) {
        return (DashboardFragment) fragmentWithToken(new DashboardFragment(), token);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        bindButterKnife(rootView);
        usernameView.setText(getString(R.string.dashboard_username_text, AccountUtil.getNick()));
        return rootView;
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
            for (Object q : list) {
                LinearLayout queueContainer = new LinearLayout(getContext());
                queueContainer.setOrientation(LinearLayout.HORIZONTAL);

                TextView textView = new TextView(getContext());
                textView.setText(((PrintQueue)q).name);
                textView.setTextColor(Color.WHITE);
                queueContainer.addView(textView);
                ImageView imageView = new ImageView(getContext());
                imageView.setImageResource(R.drawable.computers_available_true);
                queueContainer.addView(imageView);

                if (null != destinations) {
                    for (String d : ((PrintQueue) q).destinations) {
                        LinearLayout item = new LinearLayout(getContext());
                        item.setOrientation(LinearLayout.VERTICAL);
                        TextView tv = new TextView(getContext());
                        tv.setText(destinations.get(d).getName());
//                        tv.setTextColor(Color.WHITE); //TODO theme
                        ImageView im = new ImageView(getContext());
                        im.setImageResource(destinations.get(d).isUp() ? R.drawable.printer_up : R.drawable.printer_down);
                        item.addView(im);
                        item.addView(tv);
                        queueContainer.addView(item);
                    }
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                parentLayout.addView(queueContainer, 0, params);
//                AnimUtils.fadeIn(getContext(), queueContainer, 0, 1000);

            }
        }
    }

}
