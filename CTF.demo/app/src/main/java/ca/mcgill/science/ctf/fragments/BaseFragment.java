package ca.mcgill.science.ctf.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.pitchedapps.capsule.library.event.CFabEvent;
import com.pitchedapps.capsule.library.event.SnackbarEvent;
import com.pitchedapps.capsule.library.fragments.SwipeRecyclerFragmentAnimated;
import com.pitchedapps.capsule.library.item.CapsuleViewHolder;

import org.apache.commons.lang3.ArrayUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.enums.DataType;
import ca.mcgill.science.ctf.events.CategoryDataEvent;
import ca.mcgill.science.ctf.events.LoadEvent;
import ca.mcgill.science.ctf.interfaces.RoboFragmentContract;

/**
 * Created by Allan Wang on 2016-11-15.
 * <p>
 * Combine some common functions of the other fragments
 * TODO figure out a way to save data on rotate/start; the data is only passed via bundles when creating the fragment, but if it recreates itself the data will be outdated
 */

public abstract class BaseFragment<T, V extends CapsuleViewHolder> extends SwipeRecyclerFragmentAnimated<T, V> implements RoboFragmentContract {

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArgs(getArguments());
    }

    protected void bindButterKnife(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }

    @Override
    @CallSuper
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    @CallSuper
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    /**
     * Wrapper for abstract load event so we don't need to worry about the Subscription annotation
     *
     * @param event loading event sent
     */
    @Subscribe
    @Override
    public final void onLoadEventSubscription(LoadEvent event) {
        if (event.isActivityOnly()) return;
        hideRefresh(); //TODO hide only after all pending events are received
        if (onLoadEvent(event)) updateContent(event.type);
    }

    /**
     * Specified whether or not eventSubscription should count as a loaded event
     *
     * @param event data event
     * @param types types that are valid subscriptions
     * @return true if event contains a valid type and valid data
     */
    protected final boolean isLoadValid(LoadEvent event, DataType.Single... types) {
        if (!ArrayUtils.contains(types, event.type)) return false;
        if (event.isSuccessful) return true;
        if (event.data == null) return false; //Error String is null -> Silent error
        snackbar(new SnackbarEvent(String.valueOf(event.data)));
        return false;
    }

    @Override
    public void requestData() {
        postEvent(new CategoryDataEvent(getDataCategory()));
    }

    @SuppressLint("MissingSuperCall")
    @Override
    @CallSuper
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        cSwipeRefreshRecyclerView.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.accent));
//        super.onViewCreated(view, savedInstanceState); //We are managing list loading ourselves
    }

    @Override
    protected void updateList(List<T> oldList) {
        requestData();
    }


    @Nullable
    @Override
    protected CFabEvent updateFab() {
        return null;
    }

}
