package ca.mcgill.science.ctf.iitems;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.validation.constraints.Null;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.allanwang.capsule.library.logging.CLog;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.PrinterInfo;
import ca.mcgill.science.ctf.views.PrinterInfoView;

/**
 * Created by Allan Wang on 18/03/2017.
 * <p>
 * Shows room name and printers
 * We are currently hardcoding the maximum number of printers to 2. Otherwise we should incorporate a horizontal scrollview
 */

public class RoomInfoItem extends AbstractItem<RoomInfoItem, RoomInfoItem.ViewHolder> {
    private static final ViewHolderFactory<? extends RoomInfoItem.ViewHolder> FACTORY = new RoomInfoItem.ItemFactory();
    private PrinterInfo printer1, printer2;
    private String roomName;

    public static List<RoomInfoItem> getItems(Collection<PrinterInfo> info) {
        TreeMap<String, List<PrinterInfo>> map = new TreeMap<>();
        for (final PrinterInfo printer : info) {
            String room = printer.getRoomName();
            if (map.containsKey(room)) map.get(room).add(printer);
            else map.put(room, new ArrayList<PrinterInfo>() {{
                add(printer);
            }});
        }
        List<RoomInfoItem> itemList = new ArrayList<>();
        for (String room : map.keySet())
            itemList.add(new RoomInfoItem(room, map.get(room)));
        return itemList;
    }

    public RoomInfoItem(String roomName, @NonNull List<PrinterInfo> data) {
        this.roomName = roomName;
        if (data.size() >= 1) printer1 = data.get(0);
        if (data.size() >= 2) printer2 = data.get(1);
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
        viewHolder.printer1.bind(printer1);
        viewHolder.printer2.bind(printer2);
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
            ButterKnife.bind(this, view);
        }
    }

    protected static class ItemFactory implements ViewHolderFactory<RoomInfoItem.ViewHolder> {
        protected ItemFactory() {
        }

        public RoomInfoItem.ViewHolder create(View v) {
            return new RoomInfoItem.ViewHolder(v);
        }
    }

    /**
     * Click evens for the printers
     * Will show info on whether they are up or not, and give the option to enable/disable it
     */
    public static class PrinterClickEvent extends ClickEventHook<RoomInfoItem> {

        @Nullable
        @Override
        public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof RoomInfoItem.ViewHolder) {
                return ClickListenerHelper.toList(((ViewHolder) viewHolder).printer1, ((ViewHolder) viewHolder).printer2);
            }
            return super.onBindMany(viewHolder);
        }

        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof RoomInfoItem.ViewHolder) {
                return ((ViewHolder) viewHolder).printer1;
            }
            return null;
        }

        @Override
        public void onClick(View v, int position, FastAdapter<RoomInfoItem> fastAdapter, RoomInfoItem item) {
            switch (v.getId()) {
                case R.id.printer_1:
                    onClick(v.getContext(), item.printer1);
                    break;
                case R.id.printer_2:
                    onClick(v.getContext(), item.printer2);
                    break;
            }
        }

        private void onClick(Context context, @Nullable final PrinterInfo roomItem) {
            if (roomItem == null) return; //precaution
            new MaterialDialog.Builder(context)
                    .title(roomItem.get_id())
                    .positiveText(roomItem.getUp() ? R.string.disable : R.string.enable)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (roomItem.getUp()) { //disabling printer

                            } 
                        }
                    })
                    .show();
        }
    }
}
