package ca.mcgill.science.ctf.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.allanwang.capsule.library.logging.CLog;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.PrinterInfo;

/**
 * Created by Allan Wang on 18/03/2017.
 */

public class PrinterInfoView extends LinearLayout {

    @BindView(R.id.printer_image)
    public ImageView image;

    @BindView(R.id.printer_name)
    public TextView name;

    public PrinterInfoView(Context context) {
        super(context);
        init();
    }

    public static PrinterInfoView bindData(@IdRes int id, View v, @Nullable PrinterInfo data) {
        PrinterInfoView view = (PrinterInfoView) v.findViewById(id);
        view.bind(data);
        return view;
    }

    public PrinterInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PrinterInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PrinterInfoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        CLog.e("INITT");
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_printer, this);
//        inflate(getContext(), R.layout.item_printer, this);
        setVisibility(INVISIBLE);
        ButterKnife.bind(this);
    }

    public PrinterInfoView bind(@Nullable PrinterInfo data) {
        if (data == null) {
            CLog.e("NULL DATA");
            setVisibility(INVISIBLE);
            image.setImageDrawable(null);
            name.setText(null);
        } else {
            CLog.e("DATA %s", data.getName());
            setVisibility(VISIBLE);
            image.setImageResource(data.getUp() ? R.drawable.printer_up : R.drawable.printer_down);
            name.setText(data.getName());
        }
        return this;
    }

}
