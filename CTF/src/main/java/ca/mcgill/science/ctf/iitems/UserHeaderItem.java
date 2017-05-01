package ca.mcgill.science.ctf.iitems;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.allanwang.capsule.library.event.SnackbarEvent;
import ca.allanwang.capsule.library.logging.CLog;
import ca.allanwang.capsule.library.swiperecyclerview.SwipeRecyclerView;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.ColorResponse;
import ca.mcgill.science.ctf.api.FullUser;
import ca.mcgill.science.ctf.api.TepidApi;
import ca.mcgill.science.ctf.api.TepidUtils;
import ca.mcgill.science.ctf.api.User;
import ca.mcgill.science.ctf.auth.AccountUtil;
import ca.mcgill.science.ctf.fragments.base.BaseFragment;
import ca.mcgill.science.ctf.utils.Utils;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Allan Wang on 18/03/2017.
 */

public class UserHeaderItem extends AbstractItem<UserHeaderItem, UserHeaderItem.ViewHolder> {

    private FullUser fullUser;

    public UserHeaderItem(FullUser fullUser) {
        this.fullUser = fullUser;
    }

    public int getType() {
        return R.id.ctf_user_header;
    }

    public int getLayoutRes() {
        return R.layout.header_user;
    }

    public static void inject(ItemAdapter<UserHeaderItem> adapter, BaseFragment fragment) {
        retrieve(fragment, (fullUser1 -> {
            adapter.add(new UserHeaderItem(fullUser1));
            new Handler().postDelayed(() -> {
                SwipeRecyclerView srv = fragment.getSRV();
                if (srv.getLayoutManager().findFirstCompletelyVisibleItemPosition() == 1) //one below header
                    srv.smoothScrollToPosition(0, (originalSpeed) -> originalSpeed * 3);
            }, 600);
            if (fullUser1.getUser().getColorPrinting())
                adapter.getFastAdapter().select(adapter.getAdapterItemCount() - 1);
        }));
    }

    public static void injectSilently(ItemAdapter<UserHeaderItem> adapter, BaseFragment fragment) {
        retrieve(fragment, (fullUser1 -> {
            adapter.set(0, new UserHeaderItem(fullUser1));
            if (fullUser1.getUser().getColorPrinting())
                adapter.getFastAdapter().select(0);
        }));
    }

    public static void retrieve(BaseFragment fragment, @NonNull FullUserResponse response) {
        TepidUtils.getFullUser(fragment.getShortUser(), new SingleObserver<FullUser>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(FullUser fullUser) {
                response.onSuccess(fullUser);
            }

            @Override
            public void onError(Throwable e) {
                CLog.e("Errors %s", e.getMessage());
            }
        });
    }

    public interface FullUserResponse {
        void onSuccess(FullUser fullUser);
    }

    public static abstract class UserColorClickEvent extends ClickEventHook<UserHeaderItem> {
        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof UserHeaderItem.ViewHolder)
                return ((ViewHolder) viewHolder).colourPrinting;
            return null;
        }
    }

    @Override
    public void bindView(UserHeaderItem.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        User user = fullUser.getUser();
        viewHolder.username.setText(user.getDisplayName());
        viewHolder.quota.setText(String.format(Locale.CANADA, viewHolder.itemView.getContext().getString(R.string.pages_remaining), fullUser.getQuota()));
        viewHolder.colourPrinting.setChecked(isSelected());
        viewHolder.shortUser.setText(user.getShortUser());
        viewHolder.email.setText(user.getEmail());
    }

    @Override
    public void unbindView(UserHeaderItem.ViewHolder holder) {
        super.unbindView(holder);
        holder.username.setText(null);
        holder.quota.setText(null);
        holder.colourPrinting.setChecked(false);
        holder.shortUser.setText(null);
        holder.email.setText(null);
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
        @BindView(R.id.colour_printing)
        AppCompatCheckBox colourPrinting;
        @BindView(R.id.short_user)
        AppCompatTextView shortUser;
        @BindView(R.id.email)
        AppCompatTextView email;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static void config(FastAdapter<UserHeaderItem> adapter, final BaseFragment fragment) {
        adapter.withOnPreClickListener((v, adapter1, item, position) -> {
            // consume otherwise radio/checkbox will be deselected
            return true;
        });
        adapter.withItemEvent(new UserColorClickEvent() {
            @Override
            public void onClick(View v, int position, FastAdapter<UserHeaderItem> fastAdapter, UserHeaderItem item) {
                fastAdapter.toggleSelection(position);
                setColourPrinting(fragment, item.isSelected());
            }
        });
    }

    private static void setColourPrinting(final BaseFragment fragment, final boolean enable) {
        //TODO allow colour toggle for any user if person is ctfer
        TepidApi.Companion.getInstanceDangerously().enableColor(AccountUtil.getShortUser(), enable).enqueue(new Callback<ColorResponse>() {
            @Override
            public void onResponse(Call<ColorResponse> call, Response<ColorResponse> response) {
                if (!call.isCanceled()) {
                    fragment.snackbar(new SnackbarEvent(Utils.stringFormatter(fragment.getContext(), R.string.colour_printing_result, enable, R.string.enabled, R.string.disabled)));
                    fragment.refresh();
                }
            }

            @Override
            public void onFailure(Call<ColorResponse> call, Throwable t) {
                fragment.snackbar(new SnackbarEvent(R.string.colour_printing_toggle_fail));
            }
        });
    }
}
