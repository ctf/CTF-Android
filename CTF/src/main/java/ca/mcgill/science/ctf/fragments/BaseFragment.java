package ca.mcgill.science.ctf.fragments;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.IItem;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import ca.allanwang.capsule.library.event.CFabEvent;
import ca.allanwang.capsule.library.event.SnackbarEvent;
import ca.allanwang.capsule.library.fragments.CapsuleSRVFragment;
import ca.allanwang.capsule.library.logging.CLog;
import ca.allanwang.swiperecyclerview.library.SwipeRecyclerView;
import ca.allanwang.swiperecyclerview.library.adapters.AnimationAdapter;
import ca.allanwang.swiperecyclerview.library.interfaces.ISwipeRecycler;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.TEPIDAPI;
import ca.mcgill.science.ctf.auth.AccountUtil;
import ca.mcgill.science.ctf.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Allan Wang on 2016-11-15.
 * <p>
 * Combine some common functions of the other fragments
 * TODO figure out a way to save data on rotate/start; the data is only passed via bundles when creating the fragment, but if it recreates itself the data will be outdated
 */

public abstract class BaseFragment<I extends IItem, C> extends CapsuleSRVFragment<I> {

    private Unbinder unbinder;
    private Call<C> mCall;
    private static final String TAG_TOKEN = "auth_token";
    protected TEPIDAPI api;

    public static Fragment getFragment(String token, Fragment fragment) {
        Bundle args = new Bundle();
        args.putString(TAG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    public static String getToken(Fragment fragment) {
        if (fragment.getArguments() == null) return null;
        return fragment.getArguments().getString(TAG_TOKEN);
    }

    @Override
    @CallSuper
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = new TEPIDAPI(getToken(this), getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        bindButterKnife(v);
        return v;
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
    protected void configAdapter(AnimationAdapter<I> adapter) {

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
                mCall = null;
            }

            @Override
            public void onFailure() {
                postEvent(new SnackbarEvent("Contents failed to load...").setDuration(Snackbar.LENGTH_INDEFINITE));
                srv.hideRefresh();
                mCall = null;
            }
        });
        srv.refresh();
    }

    @Nullable
    @Override
    protected CFabEvent updateFab() {
        return null;
    }

    @Override
    public final void onRefresh(final ISwipeRecycler.OnRefreshStatus onRefreshStatus) {
        mAdapter.clear();
        if (!Utils.isNetworkAvailable(getContext()))
            postEvent(new SnackbarEvent("No internet; Retrieving from cache"));
        if (mCall != null) mCall.cancel(); //cancel old if it's still running
        mCall = getAPICall(api);
        mCall.enqueue(new Callback<C>() {
            @Override
            public void onResponse(Call<C> call, Response<C> response) {
                CLog.e("RESPONSE DATA %s", response.toString());
                if (response.body() == null || !response.isSuccessful())
                    onRefreshStatus.onFailure();
                else {
                    onResponseReceived(response.body(), onRefreshStatus);
                    onRefreshStatus.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<C> call, Throwable t) {
                if (call.isCanceled()) {
                    onRefreshStatus.onSuccess();
                    return;
                }
                CLog.e("Retrofit OnFailure: %s", t.getMessage());
                onRefreshStatus.onFailure();
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mCall != null) mCall.cancel();
        super.onDestroy();
    }

    protected abstract Call<C> getAPICall(TEPIDAPI api);

    protected abstract void onResponseReceived(Object body, final ISwipeRecycler.OnRefreshStatus onRefreshStatus);

    protected String getShortUser() {
        return AccountUtil.getShortUser();
    }

}
