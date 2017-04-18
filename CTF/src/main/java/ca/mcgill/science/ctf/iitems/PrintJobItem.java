package ca.mcgill.science.ctf.iitems;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import ca.allanwang.capsule.library.event.RefreshEvent;
import ca.allanwang.capsule.library.event.SnackbarEvent;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.PrintData;
import ca.mcgill.science.ctf.api.TEPIDAPI;
import ca.mcgill.science.ctf.auth.AccountUtil;
import ca.mcgill.science.ctf.utils.Preferences;
import ca.mcgill.science.ctf.views.PrintJobView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Allan Wang on 01/04/2017.
 */

public class PrintJobItem extends AbstractItem<PrintJobItem, PrintJobItem.ViewHolder> {

    private PrintData data;

    public PrintJobItem(PrintData data) {
        this.data = data;
    }

    public int getType() {
        return R.id.ctf_print_job_item;
    }

    public int getLayoutRes() {
        return R.layout.iitem_print_job;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(PrintJobItem.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.title.setText(data.getName());
        viewHolder.date.setText(data.getRelativeDate());
        viewHolder.count.setText(Long.toString(data.getPages()));
        if (data.getFailed() != null) viewHolder.setError();
        if (data.getRefunded()) viewHolder.setRefunded();
        if (viewHolder.getAdapterPosition() % 2 == 0)
            viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), getShader()));
    }

    protected
    @ColorRes
    int getShader() {
        return R.color.capsule_srv_transparent_black;
    }

    @Override
    public void unbindView(PrintJobItem.ViewHolder holder) {
        super.unbindView(holder);
        holder.resetTextViews();
        holder.itemView.setBackgroundColor(0x00000000);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.print_title)
        TextView title;
        @BindView(R.id.print_date)
        TextView date;
        @BindView(R.id.print_page_count)
        TextView count;
        @BindColor(R.color.disabled_red)
        int errorRed;
        @BindColor(R.color.enabled_green)
        int refundGreen;
        int primaryColor;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            primaryColor = DialogUtils.resolveColor(view.getContext(), R.attr.material_drawer_primary_text);
        }

        interface TextViewChange {
            void changeTextView(TextView t);
        }

        //helper to change attribute for all textviews
        void allTextView(TextViewChange action) {
            action.changeTextView(title);
            action.changeTextView(date);
            action.changeTextView(count);
        }

        void setError() {
            allTextView((t) -> t.setTextColor(errorRed));
        }

        void setRefunded() {
            allTextView((t) -> t.setTextColor(refundGreen));
        }

        void resetTextViews() {
            allTextView((t) -> t.setText(null));
            allTextView((t) -> t.setTextColor(primaryColor));
        }
    }

    /**
     * Click evens for the printers
     * Will show info on whether they are up or not, and give the option to enable/disable it
     */
    public static class PrintJobClickEvent extends ClickEventHook<PrintJobItem> {

        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof PrintJobItem.ViewHolder)
                return viewHolder.itemView;
            return null;
        }

        @Override
        public void onClick(View v, int position, FastAdapter<PrintJobItem> fastAdapter, PrintJobItem item) {
            onClick(v.getContext(), item.data);
        }

        /**
         * Dialog creator for print jobs
         * Will open the name, page count, refund status, date, and more
         *
         * @param context   view context
         * @param printData info for specified print job
         */
        private void onClick(final Context context, @Nullable final PrintData printData) {
            if (printData == null) return; //precaution
            PrintJobView view = new PrintJobView(context).bind(printData);
            MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                    .title(printData.getName())
                    .theme(new Preferences(context).isDarkMode() ? Theme.DARK : Theme.LIGHT)
                    .widgetColorAttr(R.attr.material_drawer_primary_text)
                    .customView(view, true);

            if (printData.getUserIdentification().equals(AccountUtil.getShortUser()))
                builder.positiveText(R.string.close);
            else //TODO you cannot refund yourself; verify this is server side
                builder.negativeText(R.string.close)
                        .negativeColorAttr(R.attr.material_drawer_primary_text)
                        .positiveText(printData.getRefunded() ? R.string.unrefund : R.string.refund)
                        .positiveColorAttr(R.attr.material_drawer_primary_text)
                        .onPositive((dialog, which) -> toggleRefund(context, printData));
            builder.show();
        }

        private String s(Context context, @StringRes int id) {
            return context.getString(id);
        }

        private void toggleRefund(Context c, final PrintData printData) {
            TEPIDAPI.Companion.getInstanceDangerously().refund(printData.get_id(), !printData.getRefunded()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    EventBus.getDefault().post(new RefreshEvent(R.string.user_print_jobs, true));
                    snackbar(String.format(s(c, R.string.refund_success_format), trim(printData.getName()), s(c, printData.getRefunded() ? R.string.unrefunded : R.string.refunded)));
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    if (!call.isCanceled())
                        snackbar(String.format(s(c, R.string.refund_fail_format), trim(printData.getName()), s(c, printData.getRefunded() ? R.string.unrefunded : R.string.refunded)));
                }
            });
        }

        private String trim(String s) {
            if (s.length() <= 13) return s;
            return s.substring(0, 10) + "\u2026";
        }

        private void snackbar(String s) {
            EventBus.getDefault().post(new SnackbarEvent(s));
        }
    }
}
