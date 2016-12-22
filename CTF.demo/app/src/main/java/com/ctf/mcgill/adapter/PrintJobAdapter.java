package com.ctf.mcgill.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.ctf.mcgill.R;
import com.ctf.mcgill.tepid.PrintJob;
import com.ocpsoft.pretty.time.PrettyTime;
import com.pitchedapps.capsule.library.adapters.CapsuleAdapter;
import com.pitchedapps.capsule.library.item.CapsuleViewHolder;

import java.util.Date;
import java.util.List;

/**
 * custom adapter for taking PrintJob objects, extracting the relevant data for the current page,
 * and putting it in a view holder corresponding to a single table row in the recycler views
 */
public class PrintJobAdapter extends CapsuleAdapter<PrintJob, PrintJobAdapter.ViewHolder> {

    // use these constants when creating the adapter to decide
    // the format of the table this adapter fills
    private TableType tableType;
    //    public final static int ROOMS = 0; //TODO align rows for table_row_room.xml
//    public final static int MY_ACCOUNT = 1;
    private Context mContext;

    public enum TableType {
        ROOMS(R.layout.table_row_room), MY_ACCOUNT(R.layout.table_row_user);

        private int layoutId;

        TableType(int i) {
            layoutId = i;
        }

        public int getLayoutId() {
            return layoutId;
        }
    }

    public PrintJobAdapter(Context context, List<PrintJob> data, TableType tableType) {
        super(data);
        mContext = context;
        this.tableType = tableType;
    }

    @Override
    protected int getLayoutRes(int position) {
        return tableType.getLayoutId();
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
                vh.userIdentification.setText(current.getUserIdentification());
                break;
            case MY_ACCOUNT:
                vh.jobName.setText(current.getName());
                break;
        }

        Date printed = current.getPrinted() != null ? current.getPrinted() : current.started;
        vh.datePrinted.setText(new PrettyTime().format(printed));//new SimpleDateFormat("E, MMM d, h:m").format(printed));

        if (position % 2 == 0) {
            vh.rowColor.setBackgroundColor(ContextCompat.getColor(mContext, R.color.transparentBlack)); // this alternates the row colors
        }
    }

    /**
     * for easy access, view holder object collects the views used to display a data item as a row in table
     */

    public static class ViewHolder extends CapsuleViewHolder {

        TextView jobName, userIdentification, datePrinted;
        TableRow rowColor;

        public ViewHolder(View view, int layoutId) {
            super(view, layoutId);
            switch (layoutId) {
                case R.layout.table_row_user: //MY_ACCOUNT
                    jobName = (TextView) itemView.findViewById(R.id.job_name);
                    datePrinted = (TextView) itemView.findViewById(R.id.job_date);
                    rowColor = (TableRow) itemView.findViewById(R.id.table_data_user);
                    break;
                case R.layout.table_row_room: //ROOM
                    userIdentification = (TextView) itemView.findViewById(R.id.queue_job_user);
                    datePrinted = (TextView) itemView.findViewById(R.id.queue_job_date);
                    rowColor = (TableRow) itemView.findViewById(R.id.table_data_room);
                    break;
            }
        }

    }
}