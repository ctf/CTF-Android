package ca.mcgill.science.ctf.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ocpsoft.pretty.time.PrettyTime;
import ca.allanwang.capsule.library.adapters.CapsuleAdapter;
import ca.allanwang.capsule.library.item.CapsuleViewHolder;

import java.util.Date;
import java.util.List;

import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.tepid.PrintJob;

/**
 * custom adapter for taking PrintJob objects, extracting the relevant data for the current page,
 * and putting it in a view holder corresponding to a single table row in the recycler views
 */
public class PrintJobAdapter extends CapsuleAdapter<PrintJob, PrintJobAdapter.ViewHolder> {

    //Defines display type
    private TableType tableType;
    private Context mContext;

    public enum TableType {
        ROOMS, MY_ACCOUNT
    }

    public PrintJobAdapter(Context context, List<PrintJob> data, TableType tableType) {
        super(data);
        mContext = context;
        this.tableType = tableType;
    }

    @Override
    protected int getLayoutRes(int position) {
        return R.layout.two_item_row;
    }

    @Override
    protected
    @NonNull
    ViewHolder inflateViewHolder(View view, @LayoutRes int layoutId) {
        return new ViewHolder(view, layoutId);
    }

    /**
     * bind data to a new view holder, or recycle a view holder by replacing its contents when it goes offscreen (invoked by layout manager)
     * view holder (defined below) wraps several text views for ease of use
     */
    @Override
    public void onBindViewHolder(PrintJobAdapter.ViewHolder vh, int position) {
        // get element from dataset at "position" and replace view holder's contents with that element
        // in this case an element is a pair of text views
        PrintJob current = getItem(position);

        switch (tableType) {
            case ROOMS:
                vh.textLeft.setText(current.getUserIdentification());
                break;
            case MY_ACCOUNT:
                vh.textLeft.setText(current.getName());
                break;
        }

        Date printed = current.getPrinted() != null ? current.getPrinted() : current.started;
        vh.textRight.setText(new PrettyTime().format(printed));//new SimpleDateFormat("E, MMM d, h:m").format(printed));

        if (position % 2 == 0) {
            vh.linear.setBackgroundColor(ContextCompat.getColor(mContext, R.color.transparentBlack)); // this alternates the row colors
        }
    }

    /**
     * for easy access, view holder object collects the views used to display a data item as a row in table
     */

    public static class ViewHolder extends CapsuleViewHolder {

        TextView textLeft, textRight;
        LinearLayout linear;

        ViewHolder(View view, int layoutId) {
            super(view, layoutId);

            textLeft = (TextView) view.findViewById(R.id.text_left);
            textRight = (TextView) view.findViewById(R.id.text_right);
            linear = (LinearLayout) view.findViewById(R.id.linear_container);
        }

    }
}