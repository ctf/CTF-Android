package com.example.ctfdemo.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.ctfdemo.R;
import com.example.ctfdemo.tepid.PrintJob;
import com.ocpsoft.pretty.time.PrettyTime;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * custom adapter for taking PrintJob objects, extracting the relevant data for the current page,
 * and putting it in a view holder corresponding to a single table row in the recycler views
 */
public class PrintJobAdapter extends RecyclerView.Adapter<PrintJobAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private List<PrintJob> pastJobs = Collections.emptyList(); // the data entries to display

    // use these constants when creating the adapter to decide
    // the format of the table this adapter fills
    private int tableType;
    public final static int ROOMS = 0; //TODO align rows for table_row_room.xml
    public final static int MY_ACCOUNT = 1;

    public PrintJobAdapter(Context context, List<PrintJob> data, int type){
        inflater = LayoutInflater.from(context);
        this.context = context;
        if (data != null) {
            this.pastJobs = data;
        }
        tableType = type;
    }

    public void updateList(List<PrintJob> data) {
        if (data != null) {
            this.pastJobs = data;
        } else {
            this.pastJobs.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * this creates new view holders for the currently visible table rows
     * (invoked by layout manager)
     */
    @Override
    public PrintJobAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        // create a new view representing the root item of a single table row
        if (tableType == MY_ACCOUNT) {
            v = inflater.inflate(R.layout.table_row_user, parent, false);
        } else if (tableType == ROOMS) {
            v = inflater.inflate(R.layout.table_row_room, parent, false);
        }

        // create a new view holder containing all the text views in a table row
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    /**
     * bind data to a new view holder, or recycle a view holder by replacing its contents when it goes offscreen (invoked by layout manager)
     * view holder (defined below) wraps several text views for ease of use
     */
    @Override
    public void onBindViewHolder(PrintJobAdapter.ViewHolder vh, int position) {
        // get element from dataset at "position" and replace view holder's contents with that element
        // in this case an element is a pair of text views
        PrintJob current = pastJobs.get(position);

        if (tableType == ROOMS) {
            vh.userIdentification.setText(current.getUserIdentification());
        } else if (tableType == MY_ACCOUNT) {
            vh.jobName.setText(current.getName());
        }

        Date printed = current.getPrinted() != null ? current.getPrinted() : current.started;
        vh.datePrinted.setText(new PrettyTime().format(printed));//new SimpleDateFormat("E, MMM d, h:m").format(printed));

        if (position % 2 == 0) {
            vh.rowColor.setBackgroundColor(ContextCompat.getColor(context, R.color.transparentBlack)); // this alternates the row colors
        }
    }

    /**
     * returns the size of the adapter's data set (invoked by layout manager)
     */
    @Override
    public int getItemCount() {
        return pastJobs.size();
    }

    /**
     * for easy access, view holder object collects the views used to display a data item as a row in table
     */

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView jobName, userIdentification, datePrinted;
        TableRow rowColor;

        public ViewHolder(View view) {
            super(view);
            if (tableType == MY_ACCOUNT) {
                jobName = (TextView) itemView.findViewById(R.id.job_name);
                datePrinted = (TextView) itemView.findViewById(R.id.job_date);
                rowColor = (TableRow) itemView.findViewById(R.id.table_data_user);
            } else if (tableType == ROOMS) {
                userIdentification = (TextView) itemView.findViewById(R.id.queue_job_user);
                datePrinted = (TextView) itemView.findViewById(R.id.queue_job_date);
                rowColor = (TableRow) itemView.findViewById(R.id.table_data_room);
            }
        }

    }
}