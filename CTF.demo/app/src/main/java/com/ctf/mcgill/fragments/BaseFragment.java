package com.ctf.mcgill.fragments;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ctf.mcgill.R;
import com.ctf.mcgill.requests.CTFSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pitchedapps.capsule.library.event.CFabEvent;
import com.pitchedapps.capsule.library.event.SnackbarEvent;
import com.pitchedapps.capsule.library.fragments.SwipeRecyclerFragmentAnimated;
import com.pitchedapps.capsule.library.item.CapsuleViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

/**
 * Created by Allan Wang on 2016-11-15.
 * <p>
 * Combine some common functions of the other fragments
 */

public abstract class BaseFragment<T, V extends CapsuleViewHolder> extends SwipeRecyclerFragmentAnimated<T, V> {

    private static final String KEY_TOKEN = "token";
    protected String token;
    protected SpiceManager requestManager = new SpiceManager(CTFSpiceService.class);
    private Unbinder unbinder;

    protected static Fragment fragmentWithToken(Fragment fragment, String token) {
        Bundle args = new Bundle();
        args.putString(KEY_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); //TODO Why? Didn't see any differences - Allan
        Bundle args = getArguments();
        if (args != null) {
            token = args.getString(KEY_TOKEN);
        }
        if (token == null) {
            snackbar(new SnackbarEvent("Null token"));
        }

    }

    protected void bindButterKnife(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        cSwipeRefreshRecyclerView.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.accent));
        cSwipeRefreshRecyclerView.setRefreshing(true); //TODO figure out why first refresh does not show indicator (only blank circle)
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void updateList(List<T> oldList) {
        cAdapter.updateList(null);
        startRequestManager();
        getUIData(requestManager);
        stopRequestManager();
    }

    protected void startRequestManager() {
        if (!requestManager.isStarted()) requestManager.start(getActivity());
    }

    // Please review https://github.com/octo-online/robospice/issues/96 for the reason of that
    // ugly if statement.
    protected void stopRequestManager() {
        if (requestManager.isStarted()) requestManager.shouldStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        startRequestManager();
    }

    protected abstract void getUIData(SpiceManager requestManager);

    @Override
    public void onStop() {
        stopRequestManager();
        super.onStop();
    }

    @Nullable
    @Override
    protected CFabEvent updateFab() {
        return null;
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        getUIData(requestManager);
    }

    /**
     * RequestListener with methods that will be called for all requests
     * Keep things consistent
     *
     * @param <U>
     */
    protected class MyRequestListener<U> implements RequestListener<U> {

        @Override
        @CallSuper
        public void onRequestFailure(SpiceException spiceException) {
            hideRefresh();
        }

        @Override
        @CallSuper
        public void onRequestSuccess(U u) {
            hideRefresh();
        }
    }
}
