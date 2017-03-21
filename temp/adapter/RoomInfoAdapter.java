package ca.mcgill.science.ctf.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.allanwang.capsule.library.adapters.CapsuleAdapter;
import ca.allanwang.capsule.library.item.CapsuleViewHolder;

import java.util.List;

import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.tepid.RoomInformation;

/**
 * Created by Allan Wang on 2016-12-22.
 */

public class RoomInfoAdapter extends CapsuleAdapter<RoomInformation, RoomInfoAdapter.ViewHolder> {

    private Context mContext;


    public RoomInfoAdapter(Context context, List<RoomInformation> data) {
        super(data);
        mContext = context;
    }

    @Override
    protected int getLayoutRes(int position) {
        return R.layout.room_info; //Just a linearlayout
    }

    @Override
    protected
    @NonNull
    RoomInfoAdapter.ViewHolder inflateViewHolder(View view, @LayoutRes int layoutId) {
        return new ViewHolder(view, layoutId);
    }

    /**
     * bind data to a new view holder, or recycle a view holder by replacing its contents when it goes offscreen (invoked by layout manager)
     * view holder (defined below) wraps several text views for ease of use
     */
    @Override
    public void onBindViewHolder(RoomInfoAdapter.ViewHolder vh, int position) {
        // get element from dataset at "position" and replace view holder's contents with that element
        // in this case an element is a pair of text views
        RoomInformation current = getItem(position);
        vh.roomName.setText(current.roomName);
        vh.computerAvailability.setImageResource(R.drawable.computers_available_true);
        vh.linear.removeAllViews();
        for (RoomInformation.Printer printer : current.getPrinters()) {
            LinearLayout item = new LinearLayout(mContext);
            item.setOrientation(LinearLayout.VERTICAL);
            TextView tv = new TextView(mContext);
            tv.setGravity(Gravity.CENTER);
            tv.setText(printer.name);
            ImageView im = new ImageView(mContext);
            im.setImageResource(printer.isUp ? R.drawable.printer_up : R.drawable.printer_down);
            item.addView(im);
            item.addView(tv);
            vh.linear.addView(item);
        }
    }

    /**
     * for easy access, view holder object collects the views used to display a data item as a row in table
     */

    public static class ViewHolder extends CapsuleViewHolder {

        LinearLayout linear;
        TextView roomName;
        ImageView computerAvailability;

        ViewHolder(View view, int layoutId) {
            super(view, layoutId);
            linear = (LinearLayout) view.findViewById(R.id.linear_layout);
            roomName = (TextView) view.findViewById(R.id.room_name);
            computerAvailability = (ImageView) view.findViewById(R.id.computer_availability);
        }

    }
}