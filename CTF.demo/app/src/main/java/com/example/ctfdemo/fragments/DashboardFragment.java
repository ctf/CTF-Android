package com.example.ctfdemo.fragments;

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

import com.example.ctfdemo.R;
import com.example.ctfdemo.auth.AccountUtil;
import com.example.ctfdemo.requests.CTFSpiceService;
import com.example.ctfdemo.requests.DestinationsRequest;
import com.example.ctfdemo.requests.JobsRequest;
import com.example.ctfdemo.requests.QueuesRequest;
import com.example.ctfdemo.requests.QuotaRequest;
import com.example.ctfdemo.tepid.Destination;
import com.example.ctfdemo.tepid.PrintJob;
import com.example.ctfdemo.tepid.PrintQueue;
import com.ocpsoft.pretty.time.PrettyTime;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pitchedapps.capsule.library.event.CFabEvent;
import com.pitchedapps.capsule.library.fragments.CapsuleFragment;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends CapsuleFragment{

    public static final String TAG = "MAIN_FRAGMENT";
    private SpiceManager requestManager = new SpiceManager(CTFSpiceService.class);
    private TextView quotaView, lastJobView;
    private Map<String, Destination> destinations;
    private static final String KEY_QUOTA = "QUOTA", KEY_LAST_JOB = "LAST JOB", KEY_QUEUES = "QUEUES", KEY_DESTINATIONS = "DESTINATIONS";
    private LinearLayout parentLayout;

    private static final String KEY_TOKEN = "TOKEN";
    private String token;

    public static DashboardFragment newInstance(String token) {
        DashboardFragment frag = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TOKEN, token);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle args = getArguments();
        if (args != null) {
            token = args.getString(KEY_TOKEN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        ((TextView) rootView.findViewById(R.id.dashboard_username)).setText(getString(R.string.dashboard_username_text, AccountUtil.getNick()));

        // init these views, will be filled by async request callbacks
        quotaView = (TextView) rootView.findViewById(R.id.dashboard_quota);
        lastJobView = (TextView) rootView.findViewById(R.id.dashboard_last_print_job);
        parentLayout = (LinearLayout) rootView.findViewById(R.id.dashboard_container);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        requestManager.start(getActivity());

        getUIData();
    }

    @Override
    public void onStop() {
        if (requestManager.isStarted()) {
            requestManager.shouldStop();
        }
        super.onStop();
    }

    private void getUIData() {
        requestManager.execute(new QuotaRequest(token), KEY_QUOTA, DurationInMillis.ONE_MINUTE, new QuotaRequestListener());
        requestManager.execute(new JobsRequest(token), KEY_LAST_JOB, DurationInMillis.ONE_MINUTE, new UserJobsRequestListener());
        requestManager.execute(new DestinationsRequest(token), KEY_DESTINATIONS, DurationInMillis.ONE_MINUTE, new DestinationsRequestListener());
    }

    @Nullable
    @Override
    protected CFabEvent updateFab() {
        return null;
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
                        tv.setTextColor(Color.WHITE);
                        ImageView im = new ImageView(getContext());
                        im.setImageResource(destinations.get(d).isUp() ? R.drawable.printer_up : R.drawable.printer_down);
                        item.addView(im);
                        item.addView(tv);
                        queueContainer.addView(item);
                    }
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                parentLayout.addView(queueContainer, 0, params);
            }
        }
    }

}
