package ca.mcgill.science.ctf.iitems;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.models.PrinterInfo;
import ca.mcgill.science.ctf.models.RoomInfo;
import ca.mcgill.science.ctf.views.PrinterInfoView;

/**
 * Created by Allan Wang on 18/03/2017.
 */

public class RoomInfoItem extends AbstractItem<RoomInfoItem, RoomInfoItem.ViewHolder> {
    private static final ViewHolderFactory<? extends RoomInfoItem.ViewHolder> FACTORY = new RoomInfoItem.ItemFactory();
    private List<PrinterInfo> roomInfoData = new ArrayList<>();
    private String roomName;

    public RoomInfoItem(String roomName, RoomInfo data) {
        this.roomName = roomName;
        for (PrinterInfo printerInfo : data.getPrinters())
            if (printerInfo.getName().contains(roomName))
                roomInfoData.add(printerInfo);
    }

    public int getType() {
        return R.id.ctf_room_info_item;
    }

    public int getLayoutRes() {
        return R.layout.room_info;
    }

    @Override
    public void bindView(RoomInfoItem.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.roomName.setText(roomName);
//        viewHolder.computerVisibility.setImageDrawable(null);
        if (roomInfoData.size() >= 1)
            viewHolder.printer1.bind(roomInfoData.get(0));
        if (roomInfoData.size() >= 2)
            viewHolder.printer2.bind(roomInfoData.get(1));
    }

    @Override
    public void unbindView(RoomInfoItem.ViewHolder holder) {
        super.unbindView(holder);
        holder.roomName.setText(null);
        holder.computerVisibility.setImageDrawable(null);
        holder.printer1.bind(null);
        holder.printer2.bind(null);
    }

    public ViewHolderFactory<? extends RoomInfoItem.ViewHolder> getFactory() {
        return FACTORY;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.room_name)
        TextView roomName;
        @BindView(R.id.computer_availability)
        ImageView computerVisibility;
        @BindView(R.id.printer_1)
        PrinterInfoView printer1;
        @BindView(R.id.printer_2)
        PrinterInfoView printer2;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(view);
        }
    }

    protected static class ItemFactory implements ViewHolderFactory<RoomInfoItem.ViewHolder> {
        protected ItemFactory() {
        }

        public RoomInfoItem.ViewHolder create(View v) {
            return new RoomInfoItem.ViewHolder(v);
        }
    }
}
