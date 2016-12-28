package com.ctf.mcgill.fragments;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.ctf.mcgill.R;
import com.ctf.mcgill.enums.DataType;
import com.ctf.mcgill.events.LoadEvent;
import com.ctf.mcgill.interfaces.RoboFragmentContract;
import com.pitchedapps.capsule.library.event.CFabEvent;
import com.pitchedapps.capsule.library.event.SnackbarEvent;
import com.pitchedapps.capsule.library.fragments.SwipeRecyclerFragmentAnimated;
import com.pitchedapps.capsule.library.item.CapsuleViewHolder;
import com.pitchedapps.capsule.library.logging.CLog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by Allan Wang on 2016-11-15.
 * <p>
 * Combine some common functions of the other fragments
 */

public abstract class BaseFragment<T, V extends CapsuleViewHolder> extends SwipeRecyclerFragmentAnimated<T, V> implements RoboFragmentContract {

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); //TODO Why? Didn't see any differences - Allan
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

    @Override
    public void requestData() {
        postEvent(getDataCategory());
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        cSwipeRefreshRecyclerView.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.accent));
        cSwipeRefreshRecyclerView.setRefreshing(true); //TODO figure out why first refresh does not show indicator (only blank circle)
        super.onViewCreated(view, savedInstanceState);
    }

    protected boolean isLoadSuccessful(LoadEvent event) {
        if (event.isSuccessful) return true;
        if (event.data == null) return false; //Error String is null -> Silent error
        snackbar(new SnackbarEvent(String.valueOf(event.data)));
        return false;
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
