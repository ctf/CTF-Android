package ca.mcgill.science.ctf.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.mcgill.science.ctf.R;

/**
 * Created by Allan Wang on 19/03/2017.
 */

public class HeaderUser extends LinearLayout {

    @BindView(R.id.username)
    AppCompatTextView username;
    @BindView(R.id.quota)
    AppCompatTextView quota;
    @BindView(R.id.last_print_job)
    AppCompatTextView lastPrintJob;
    @BindView(R.id.colour_printing)
    AppCompatCheckBox colorToggle;


    public HeaderUser(Context context) {
        super(context);
        init();
    }

    public HeaderUser(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeaderUser(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HeaderUser(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.header_user, this);
        ButterKnife.bind(this);
    }

    public HeaderUser setUserName(String s) {
        username.setText(s);
        return this;
    }

    public HeaderUser setQuota(String s) {
        quota.setText(s);
        return this;
    }

    public HeaderUser setLastPrintJob(String s) {
        lastPrintJob.setText(s);
        return this;
    }
}
