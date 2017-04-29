package ca.mcgill.science.ctf.iitems;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.allanwang.capsule.library.logging.CLog;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.FullUser;
import ca.mcgill.science.ctf.api.ITEPID;
import ca.mcgill.science.ctf.api.PrintData;
import ca.mcgill.science.ctf.api.TEPIDAPI;
import ca.mcgill.science.ctf.api.User;
import ca.mcgill.science.ctf.fragments.base.BaseFragment;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Allan Wang on 18/03/2017.
 */

public class UserHeaderItem extends AbstractItem<UserHeaderItem, UserHeaderItem.ViewHolder> {

    private String username, quota, lastPrintJob;
    private boolean colourPrinting;

    public UserHeaderItem(FullUser user) {
        this.username = user.getUser().getDisplayName();
        this.quota = Integer.toString(user.getQuota());
        this.lastPrintJob = user.getPrintJobs().get(0).getName();
        this.colourPrinting = user.getUser().getColorPrinting();
    }

    public int getType() {
        return R.id.ctf_user_header;
    }

    public int getLayoutRes() {
        return R.layout.header_user;
    }

    public UserHeaderItem bind(User data) {
        username = data.getDisplayName();
        return this;
    }

    public static void inject(ItemAdapter<UserHeaderItem> adapter, BaseFragment fragment) {
        ITEPID caller = TEPIDAPI.Companion.getInstanceDangerously();
        Observable<User> user = caller.getUserObservable(fragment.getShortUser());
        Observable<Integer> quota = caller.getQuotaObservable(fragment.getShortUser());
        Observable<List<PrintData>> printJobs = caller.getUserPrintJobsObservable(fragment.getShortUser());
        Observable<FullUser> fullUserObservable = Observable.zip(user, quota, printJobs, FullUser::new);

        //TODO fix observable
        fullUserObservable.subscribe(new DisposableObserver<FullUser>() {
            @Override
            public void onNext(FullUser fullUser) {
                CLog.e("User retrieved");
                adapter.add(new UserHeaderItem(fullUser));
            }

            @Override
            public void onError(Throwable e) {
                CLog.e("Errors %s", e.getMessage());
            }

            @Override
            public void onComplete() {
                CLog.e("Complete");
            }
        });
    }

    @Override
    public void bindView(UserHeaderItem.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.username.setText(username);
        viewHolder.quota.setText(quota);
        viewHolder.lastPrintJob.setText(lastPrintJob);
        viewHolder.colourPrinting.setChecked(colourPrinting);
    }

    @Override
    public void unbindView(UserHeaderItem.ViewHolder holder) {
        super.unbindView(holder);
        holder.username.setText(null);
        holder.quota.setText(null);
        holder.lastPrintJob.setText(null);
        holder.colourPrinting.setChecked(false);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.username)
        AppCompatTextView username;
        @BindView(R.id.quota)
        AppCompatTextView quota;
        @BindView(R.id.last_print_job)
        AppCompatTextView lastPrintJob;
        @BindView(R.id.colour_printing)
        AppCompatCheckBox colourPrinting;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
