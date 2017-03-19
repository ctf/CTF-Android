package ca.mcgill.science.ctf.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.mcgill.science.ctf.R;

/**
 * Created by Allan Wang on 19/03/2017.
 */

public class DashboardHeader extends LinearLayout {

    @BindView(R.id.dashboard_username)
    TextView username;
    @BindView(R.id.dashboard_quota)
    TextView quota;
    @BindView(R.id.dashboard_last_print_job)
    TextView lastPrintJob;


    public DashboardHeader(Context context) {
        super(context);
        init();
    }

    public DashboardHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DashboardHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DashboardHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.header_dashboard, this);
        ButterKnife.bind(this);
    }

    public DashboardHeader setUserName(String s) {
        username.setText(s);
        return this;
    }

    public DashboardHeader setQuota(String s) {
        quota.setText(s);
        return this;
    }

    public DashboardHeader setLastPrintJob(String s) {
        lastPrintJob.setText(s);
        return this;
    }
}
