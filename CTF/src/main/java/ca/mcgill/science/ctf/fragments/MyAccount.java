package ca.mcgill.science.ctf.fragments;

import android.support.annotation.NonNull;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;

import java.util.ArrayList;
import java.util.List;

import ca.allanwang.capsule.library.event.SnackbarEvent;
import ca.allanwang.capsule.library.swiperecyclerview.adapters.AnimationAdapter;
import ca.allanwang.capsule.library.swiperecyclerview.interfaces.ISwipeRecycler;
import ca.allanwang.capsule.library.swiperecyclerview.items.CheckBoxItem;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.ColorResponse;
import ca.mcgill.science.ctf.api.ITEPID;
import ca.mcgill.science.ctf.api.User;
import ca.mcgill.science.ctf.auth.AccountUtil;
import ca.mcgill.science.ctf.fragments.base.BaseFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAccount extends BaseFragment<CheckBoxItem, User> {

    @Override
    public int getTitleId() {
        return R.string.my_account;
    }

    @Override
    protected Call<User> getAPICall(ITEPID api) {
        return api.getUser(AccountUtil.getShortUser());
    }

    @Override
    protected int getNumColumns() {
        return 2;
    }

    @Override
    protected void configAdapter(AnimationAdapter<CheckBoxItem> adapter) {
        super.configAdapter(adapter);
        //TODO only show dialog if user is a CTF member
        adapter.withItemEvent(new ColourPrintingClickEvent());
    }

    @Override
    protected void onResponseReceived(@NonNull User body, final ISwipeRecycler.OnRefreshStatus onRefreshStatus) {
        mAdapter.add(generateView(body));
    }

    @Override
    protected void onSilentResponseReceived(@NonNull User body) {
        mAdapter.setNewList(generateView(body));
    }

    //Custom click events to ignore every item except for the colour printing one
    private class ColourPrintingClickEvent extends CheckBoxItem.CheckBoxClickEvent {
        @Override
        public void onClick(View v, int position, FastAdapter<CheckBoxItem> fastAdapter, CheckBoxItem item) {
            if (!item.showCheckBox) return;
            item.getViewHolder(v).setIsRecyclable(false);
            fastAdapter.toggleSelection(position);
            item.getViewHolder(v).setIsRecyclable(true);
            setColourPrinting(fastAdapter.getItem(position).isSelected());
        }
    }

    private void setColourPrinting(final boolean enable) {
        //TODO allow colour toggle for any user if person is ctfer
        api.enableColor(AccountUtil.getShortUser(), enable).enqueue(new Callback<ColorResponse>() {
            @Override
            public void onResponse(Call<ColorResponse> call, Response<ColorResponse> response) {
                if (!call.isCanceled()) {
                    snackbar(new SnackbarEvent(String.format(getString(R.string.colour_printing_result), enable ? getString(R.string.enabled) : getString(R.string.disabled))));
                    refreshSilently();
                }
            }

            @Override
            public void onFailure(Call<ColorResponse> call, Throwable t) {
                snackbar(new SnackbarEvent(R.string.colour_printing_toggle_fail));
            }
        });
    }

    private List<CheckBoxItem> generateView(User user) {
        List<CheckBoxItem> list = new ArrayList<>();
        addItems(list, user.getDisplayName(), user.getShortUser(), Integer.toString(user.getStudentId()), user.getEmail(), user.getFaculty());
        list.add(item(getString(R.string.colour_printing)).showCheckBox(true).withSetSelected(user.getColorPrinting()));
        getQuota();
        return list;
    }

    private void getQuota() {
        api.getQuota(AccountUtil.getShortUser()).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                mAdapter.add(new CheckBoxItem().hideCheckBox().withName(String.format("Quota: %d", response.body())));
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }

    private void addItems(List<CheckBoxItem> list, String... s) {
        for (String ss : s)
            list.add(item(ss));
    }

    //Helper method to generate checkboxitem and hide the checkbox by default
    private CheckBoxItem item(String s) {
        return new CheckBoxItem().withName(s).hideCheckBox();
    }
}
