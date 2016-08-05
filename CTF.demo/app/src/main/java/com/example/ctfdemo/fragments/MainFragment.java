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

import com.example.ctfdemo.auth.AccountUtil;
import com.example.ctfdemo.requests.CTFSpiceService;
import com.example.ctfdemo.requests.LastJobRequest;
import com.example.ctfdemo.tepid.PrintJob;
import com.example.ctfdemo.requests.QuotaRequest;
import com.example.ctfdemo.R;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class MainFragment extends Fragment{

    private ImageView[][] statusIcons = new ImageView[3][2];
    private TextView usernameView;
    private TextView quotaView;
    private TextView lastJobView;

    // todo use jacksongooglehttpclient or implement custom spice service?
    private SpiceManager requestManager = new SpiceManager(CTFSpiceService.class);

    // todo keys for the Spice cache, not used yet
    private static final String KEY_QUOTA = "QUOTA";
    private static final String KEY_LAST_JOB = "LAST JOB";

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

    // the static layout elements defined in xml will now be visible to the user, the dynamic ones will be populated here
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUIElements();
        performQuotaRequest();
        performLastJobRequest();
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

    private void initUIElements() {
        int[] rooms = {R.id.dashboard_row_1B16, R.id.dashboard_row_1B17, R.id.dashboard_row_1B18};
        for (int i = 0; i < 3; i++) {
            TableRow row = (TableRow) getView().findViewById(rooms[i]);
            statusIcons[i][0] = (ImageView) row.getChildAt(1);
            statusIcons[i][1] = (ImageView) row.getChildAt(2);
        }
        usernameView = ((TextView) getView().findViewById(R.id.dashboard_username));
        usernameView.setText(getString(R.string.dashboard_username_text, AccountUtil.getUserName()));
        quotaView = (TextView) getView().findViewById(R.id.dashboard_quota);
        lastJobView = (TextView) getView().findViewById(R.id.dashboard_last_print_job);
    }

    private void performQuotaRequest() {
        quotaView.setText(getString(R.string.dashboard_quota_text, ""));
        requestManager.execute(new QuotaRequest(), new QuotaRequestListener());
    }

    private final class QuotaRequestListener implements RequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Error: failed to load data from TEPID server.", Toast.LENGTH_SHORT).show();
            MainFragment.this.getActivity().setProgressBarIndeterminateVisibility(false);
        }
        @Override
        public void onRequestSuccess(String quota) {
            MainFragment.this.getActivity().setProgressBarIndeterminateVisibility(false);
            quotaView.setText(getString(R.string.dashboard_quota_text, quota));
        }
    }


/*    private final class LastJobRequest extends SpiceRequest<String> {

        public LastJobRequest() {
            super(String.class);
        }

        @Override
        public String loadDataFromNetwork() throws Exception {
*//*            final WebTarget tepidServer = ClientBuilder
                    .newBuilder()
                    *//**//*.register(JacksonFeature.class)*//**//*
                    .build()
                    .target(AccountUtil.tepidURL);

            String lastJob = tepidServer
                    .path("jobs")
                    .path(AccountUtil.getUserName())
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Token " + AccountUtil.getAuthToken())
                    .get().readEntity(String.class);*//*

            return "";//lastJob;
        }
    }*/

    private void performLastJobRequest() {
        lastJobView.setText(getString(R.string.dashboard_last_job_text, ""));
        requestManager.execute(new LastJobRequest(), new LastJobRequestListener());
    }

    private final class LastJobRequestListener implements RequestListener<PrintJob> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Error: failed to load data from TEPID server.", Toast.LENGTH_SHORT).show();
            MainFragment.this.getActivity().setProgressBarIndeterminateVisibility(false);
        }
        @Override
        public void onRequestSuccess(PrintJob p) {
            lastJobView.setText(getString(R.string.dashboard_last_job_text, p.getPrinted()));
        }
    }



/*    private final class DestinationStatusRequest extends SpiceRequest<> {

        @Override
        public Destination[] loadDataFromNetwork() throws Exception {
            return new Destination[0];
        }
    }*/
}
