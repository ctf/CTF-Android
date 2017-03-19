package ca.mcgill.science.ctf.iitems;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.materialize.holder.StringHolder;

import java.util.List;

import ca.allanwang.swiperecyclerview.library.items.CheckBoxItem;
import ca.mcgill.science.ctf.models.RoomInfo;

/**
 * Created by Allan Wang on 18/03/2017.
 */

public class RoomInfoItem extends AbstractItem<CheckBoxItem, CheckBoxItem.ViewHolder> {
    private static final ViewHolderFactory<? extends CheckBoxItem.ViewHolder> FACTORY = new RoomInfoItem.ItemFactory();
    public String header;
    public StringHolder name;
    public StringHolder description;

    public RoomInfoItem(RoomInfo data) {

    }

    public int getType() {
        return ca.allanwang.swiperecyclerview.library.R.id.checkbox;
    }

    public int getLayoutRes() {
        return ca.allanwang.swiperecyclerview.library.R.layout.fastitem_checkbox;
    }

    public void bindView(CheckBoxItem.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.checkBox.setChecked(this.isSelected());
        StringHolder.applyTo(this.name, viewHolder.name);
        StringHolder.applyToOrHide(this.description, viewHolder.description);
    }

    public void unbindView(CheckBoxItem.ViewHolder holder) {
        super.unbindView(holder);
        holder.name.setText((CharSequence)null);
        holder.description.setText((CharSequence)null);
    }

    public ViewHolderFactory<? extends RoomInfoItem.ViewHolder> getFactory() {
        return FACTORY;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected View view;
        public CheckBox checkBox;
        TextView name;
        TextView description;

        public ViewHolder(View view) {
            super(view);
            this.checkBox = (CheckBox)view.findViewById(ca.allanwang.swiperecyclerview.library.R.id.checkbox);
            this.name = (TextView)view.findViewById(ca.allanwang.swiperecyclerview.library.R.id.title);
            this.description = (TextView)view.findViewById(ca.allanwang.swiperecyclerview.library.R.id.description);
            this.view = view;
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
