package ca.mcgill.science.ctf.views;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.PrintData;
import ca.mcgill.science.ctf.auth.AccountUtil;

/**
 * Created by Allan Wang on 01/04/2017.
 */

public class PrintJobView extends ConstraintLayout {

    @BindView(R.id.pv_short_user)
    TextView shortUser;
    @BindView(R.id.pv_status)
    TextView status;
    @BindView(R.id.pv_date_formatted)
    TextView dateFormatted;
    @BindView(R.id.pv_date_relative)
    TextView dateRelative;
    @BindView(R.id.pv_no_self_refund)
    TextView noSelfRefund;
    @BindView(R.id.pv_count_pages)
    TextView pageCount;
    @BindView(R.id.pv_count_coloured)
    TextView colourCount;

    public PrintJobView(Context context) {
        super(context);
        init();
    }

    public PrintJobView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PrintJobView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_print_job, this);
        ButterKnife.bind(this);
    }

    public PrintJobView bind(PrintData data) {
        shortUser.setText(data.getUserIdentification());
        if (data.getFailed() != null)
            status.setText(R.string.failed);
        else if (data.getRefunded())
            status.setText(R.string.refunded);
        else if (data.getPrinted() != null)
            status.setText(R.string.printed);
        else if (data.getProcessed() != null)
            status.setText(R.string.processed);
        else status.setText(R.string.processing);
        dateFormatted.setText(data.getFormattedDate());
        dateRelative.setText(data.getRelativeDate());
        pageCount.setText(String.format(getContext().getString(R.string.page_count_format), data.getPages()));
        colourCount.setText(String.format(getContext().getString(R.string.colour_page_count_format), data.getColorPages()));
        if (data.getUserIdentification().equals(AccountUtil.getShortUser()))
            noSelfRefund.setVisibility(VISIBLE);
        return this;
    }
}
