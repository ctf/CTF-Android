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

public class DashboardHeaderItem extends AbstractItem<DashboardHeaderItem, DashboardHeaderItem.ViewHolder> {
    private static final ViewHolderFactory<? extends DashboardHeaderItem.ViewHolder> FACTORY = new DashboardHeaderItem.ItemFactory();
    private String username, quota, lastPrintJob;

    public DashboardHeaderItem(String username, String quota, String lastPrintJob) {
        this.username = username;
        this.quota = quota;
        this.lastPrintJob = lastPrintJob;
    }

    public int getType() {
        return R.id.ctf_dashboard_header;
    }

    public int getLayoutRes() {
        return R.layout.header_dashboard;
    }

    @Override
    public void bindView(DashboardHeaderItem.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.username.setText(username);
        viewHolder.quota.setText(quota);
        viewHolder.lastPrintJob.setText(lastPrintJob);
    }

    @Override
    public void unbindView(DashboardHeaderItem.ViewHolder holder) {
        super.unbindView(holder);
        holder.username.setText(null);
        holder.quota.setText(null);
        holder.lastPrintJob.setText(null);
    }

    public ViewHolderFactory<? extends DashboardHeaderItem.ViewHolder> getFactory() {
        return FACTORY;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.dashboard_username)
        TextView username;
        @BindView(R.id.dashboard_quota)
        TextView quota;
        @BindView(R.id.dashboard_last_print_job)
        TextView lastPrintJob;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    protected static class ItemFactory implements ViewHolderFactory<DashboardHeaderItem.ViewHolder> {
        protected ItemFactory() {
        }

        public DashboardHeaderItem.ViewHolder create(View v) {
            return new DashboardHeaderItem.ViewHolder(v);
        }
    }
}
