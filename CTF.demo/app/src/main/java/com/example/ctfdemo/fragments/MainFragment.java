package com.example.ctfdemo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ctfdemo.R;
import com.example.ctfdemo.auth.AccountUtil;
import com.example.ctfdemo.requests.CTFSpiceService;
import com.example.ctfdemo.requests.LastJobRequest;
import com.example.ctfdemo.requests.QuotaRequest;
import com.example.ctfdemo.requests.TokenRequest;
import com.example.ctfdemo.tepid.PrintJob;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class MainFragment extends Fragment{

    private static final String KEY_USERNAME = "username", KEY_TOKEN = "token";
    private ImageView[][] statusIcons = new ImageView[3][2];
    private TextView usernameView, quotaView, lastJobView;
    private SpiceManager requestManager = new SpiceManager(CTFSpiceService.class);
    // todo keys for the Spice cache, not used yet
    private static final String KEY_QUOTA = "QUOTA", KEY_LAST_JOB = "LAST JOB";
    private String username, token;

    //todo do something with the username and token, see fragment lifecycle
    public static MainFragment newInstance(String username, String token) {
        MainFragment frag = new MainFragment();
        Bundle args = new Bundle();
        args.putString(KEY_USERNAME, username);
        args.putString(KEY_TOKEN, token);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    // the static layout elements defined in xml will now be visible to the user
    // the dynamic ones will be populated here
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            username = args.getString(KEY_USERNAME);
            token = args.getString(KEY_TOKEN);
        }
        populateUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        requestManager.start(getActivity());
    }

    @Override
    public void onStop() {
        // Please review https://github.com/octo-online/robospice/issues/96 for the reason of that
        // ugly if statement.
        if (requestManager.isStarted()) {
            requestManager.shouldStop();
        }
        super.onStop();
    }

    private void populateUI() {
        int[] rooms = {R.id.dashboard_row_1B16, R.id.dashboard_row_1B17, R.id.dashboard_row_1B18};
        for (int i = 0; i < 3; i++) {
            TableRow row = (TableRow) getView().findViewById(rooms[i]);
            statusIcons[i][0] = (ImageView) row.getChildAt(1);
            statusIcons[i][1] = (ImageView) row.getChildAt(2);
        }
        usernameView = ((TextView) getView().findViewById(R.id.dashboard_username));
        usernameView.setText(getString(R.string.dashboard_username_text, username));
        quotaView = (TextView) getView().findViewById(R.id.dashboard_quota);
        quotaView.setText(getString(R.string.dashboard_quota_text, ""));
        lastJobView = (TextView) getView().findViewById(R.id.dashboard_last_print_job);
        lastJobView.setText(getString(R.string.dashboard_last_job_text, ""));

        performQuotaRequest(token);
        performLastJobRequest(token);
    }

    private void performQuotaRequest(String token) {
        requestManager.execute(new QuotaRequest(token), KEY_QUOTA, DurationInMillis.ONE_MINUTE, new QuotaRequestListener());
    }

    private void performLastJobRequest(String token) {
        requestManager.execute(new LastJobRequest(token), KEY_LAST_JOB, DurationInMillis.ONE_MINUTE, new LastJobRequestListener());
    }

    private final class QuotaRequestListener implements RequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Error: failed to load data from TEPID server.", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onRequestSuccess(String quota) {
            quotaView.setText(getString(R.string.dashboard_quota_text, quota));
        }
    }

    private final class LastJobRequestListener implements RequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Error: failed to load data from TEPID server.", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onRequestSuccess(String p) {
            lastJobView.setText(getString(R.string.dashboard_last_job_text, p));
        }
    }



/*    private final class DestinationStatusRequest extends SpiceRequest<> {

        @Override
        public Destination[] loadDataFromNetwork() throws Exception {
            return new Destination[0];
        }
    }*/
}
