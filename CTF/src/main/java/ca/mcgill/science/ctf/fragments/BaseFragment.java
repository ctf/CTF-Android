package ca.mcgill.science.ctf.fragments;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.mikepenz.fastadapter.IItem;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import ca.allanwang.capsule.library.event.CFabEvent;
import ca.allanwang.capsule.library.event.SnackbarEvent;
import ca.allanwang.capsule.library.fragments.CapsuleSRVFragment;
import ca.allanwang.swiperecyclerview.library.SwipeRecyclerView;
import ca.allanwang.swiperecyclerview.library.interfaces.ISwipeRecycler;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.interfaces.DataFragmentContract;

/**
 * Created by Allan Wang on 2016-11-15.
 * <p>
 * Combine some common functions of the other fragments
 * TODO figure out a way to save data on rotate/start; the data is only passed via bundles when creating the fragment, but if it recreates itself the data will be outdated
 */

public abstract class BaseFragment<I extends IItem> extends CapsuleSRVFragment<I> implements DataFragmentContract {

    private Unbinder unbinder;
    private static final String TAG_TOKEN = "auth_token";
    protected String token;

    public static <I extends IItem, F extends BaseFragment<I>> F getFragment(String token, F fragment) {
        Bundle args = new Bundle();
        args.putString(TAG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) token = getArguments().getString(TAG_TOKEN);
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
    @CallSuper
    protected void configSRV(final SwipeRecyclerView srv) {
        SwipeRefreshLayout swipe = srv.getSwipeRefreshLayout();
        swipe.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.accent));
        srv.setOnRefreshStatus(new ISwipeRecycler.OnRefreshStatus() {
            @Override
            public void onSuccess() {
                srv.hideRefresh();
            }

            @Override
            public void onFailure() {
                postEvent(new SnackbarEvent("Contents failed to load...").setDuration(Snackbar.LENGTH_INDEFINITE));
                srv.hideRefresh();
            }
        });
        srv.refresh();
    }

    @Nullable
    @Override
    protected CFabEvent updateFab() {
        return null;
    }

}
