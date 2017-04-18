package ca.mcgill.science.ctf.views;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.PrintData;

/**
 * Created by Allan Wang on 01/04/2017.
 */

public class UserData extends ConstraintLayout {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.quota)
    TextView quota;
    @BindView(R.id.colour_printing)
    AppCompatCheckBox colourPrinting;

    public UserData(Context context) {
        super(context);
        init();
    }

    public UserData(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UserData(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.header_user_data, this);
        ButterKnife.bind(this);
    }

    public UserData bind(PrintData data) {

        return this;
    }
}
