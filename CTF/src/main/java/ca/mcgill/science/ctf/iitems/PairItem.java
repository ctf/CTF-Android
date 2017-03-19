package ca.mcgill.science.ctf.iitems;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.mcgill.science.ctf.R;

/**
 * Created by Allan Wang on 18/03/2017.
 */

public class PairItem extends AbstractItem<PairItem, PairItem.ViewHolder> {
    private static final ViewHolderFactory<? extends PairItem.ViewHolder> FACTORY = new PairItem.ItemFactory();
    private String left, right;

    public PairItem(String left, String right) {
        this.left = left;
        this.right = right;
    }

    public int getType() {
        return R.id.ctf_pair_item;
    }

    public int getLayoutRes() {
        return R.layout.two_item_row;
    }

    @Override
    public void bindView(PairItem.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.left.setText(left);
        viewHolder.right.setText(right);
    }

    @Override
    public void unbindView(PairItem.ViewHolder holder) {
        super.unbindView(holder);
        holder.left.setText(null);
        holder.right.setText(null);
    }

    public ViewHolderFactory<? extends PairItem.ViewHolder> getFactory() {
        return FACTORY;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_left)
        TextView left;
        @BindView(R.id.text_right)
        TextView right;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    protected static class ItemFactory implements ViewHolderFactory<PairItem.ViewHolder> {
        protected ItemFactory() {
        }

        public PairItem.ViewHolder create(View v) {
            return new PairItem.ViewHolder(v);
        }
    }
}
