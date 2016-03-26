package com.example.ctfdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by erasmas on 1/8/16.
 */

// custom adapter for the tables in the room info pages, formats the data all pretty like using TableData objects and uses them to populate the tables
public class mPrintHistTableRowAdapter extends RecyclerView.Adapter<mPrintHistTableRowAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    List<TableData> data = Collections.emptyList();

    // constructor for the custom adapter
    public mPrintHistTableRowAdapter(Context context, List<TableData> data){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
    }

    // create new view holders for onscreen data items (invoked by layout manager)
    @Override
    public mPrintHistTableRowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view representing the root item of a single table row
        View v = inflater.inflate(R.layout.table_row_user, parent, false);
        // create a new view holder containing all the text views in a table row
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // bind data to a view holder, or recycle a view holder by replacing its contents when it goes offscreen (invoked by layout manager)
    @Override
    public void onBindViewHolder(mPrintHistTableRowAdapter.ViewHolder vh, int position) {
        // get element from dataset at "position" and replace view holder's contents with that element
        // in this case an element is a pair of text views
        TableData current = data.get(position);
        vh.jobId.setText(current.getJobId());
        vh.date.setText(current.getDate());
        vh.pageCount.setText(current.getPageCount());
        if (position % 2 == 0) {
            vh.rowColor.setBackgroundColor(context.getResources().getColor(R.color.transparentBlack)); // alternate row colors
        }
    }

    //return size of dataset (invoked by layout manager)
    @Override
    public int getItemCount() {
        return data.size();
    }

    // for easy access, view holder object collects the views used to display a data item as a row in table
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobId;
        TextView date;
        TextView pageCount;
        TableRow rowColor; // added this property just to alternate background color in onBindViewHolder... fuck's sake...
        public ViewHolder(View view) {
            super(view);
            jobId = (TextView) itemView.findViewById(R.id.print_hist_job);
            date = (TextView) itemView.findViewById(R.id.print_hist_date);
            pageCount = (TextView) itemView.findViewById(R.id.print_hist_pageCount);
            rowColor = (TableRow) itemView.findViewById(R.id.table_data_user);
        }
    }
}