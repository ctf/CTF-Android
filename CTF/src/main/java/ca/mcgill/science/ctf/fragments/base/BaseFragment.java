package ca.mcgill.science.ctf.fragments.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import ca.allanwang.capsule.library.swiperecyclerview.SwipeRecyclerView;
import ca.allanwang.capsule.library.swiperecyclerview.adapters.AnimationAdapter;
import ca.allanwang.capsule.library.swiperecyclerview.interfaces.ISwipeRecycler;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.ITepid;
import ca.mcgill.science.ctf.api.TepidApi;
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

public abstract class BaseFragment<I extends IItem, C> extends CapsuleSRVFragment<I> implements SwipeRecyclerView.SilentRefreshListener {

    private Unbinder unbinder;
    private Call<C> mCall;
    private static final String TAG_TOKEN = "auth_token", TAG_SHORT_USER = "short_user";
    protected ITepid api;

    public static Fragment getFragment(String token, String shortUser, Fragment fragment) {
        if (fragment == null) return null;
        Bundle args = new Bundle();
        args.putString(TAG_TOKEN, token);
        args.putString(TAG_SHORT_USER, shortUser);
        fragment.setArguments(args);
        return fragment;
    }

    public static String getToken(Fragment fragment) {
        if (fragment.getArguments() == null) return null;
        return fragment.getArguments().getString(TAG_TOKEN);
    }

    public static String getShortUser(Fragment fragment) {
        if (fragment.getArguments() == null) return null;
        return fragment.getArguments().getString(TAG_SHORT_USER);
    }

    public static ITepid getAPI(Fragment fragment) {
        return TepidApi.Companion.getInstance(getToken(fragment), fragment.getContext());
    }

    @Override
    @CallSuper
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = getAPI(this);
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
//        srv.getSwipeRefreshLayout().setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.accent));
        srv.setOnRefreshStatus(new ISwipeRecycler.OnRefreshStatus() {
            @Override
            public void onSuccess() {
                srv.hideRefresh();
                mCall = null;
            }

            @Override
            public void onFailure() {
                postEvent(new SnackbarEvent(s(R.string.error_load_fail)).setDuration(3000));
                srv.hideRefresh();
                mCall = null;
            }
        });
        srv.setSilentRefreshListener(this);
        srv.refresh();
    }

    @Nullable
    @Override
    protected CFabEvent updateFab() {
        return new CFabEvent(false); //be safe; hide fab every time
    }

    @Override
    public void onRefresh(final ISwipeRecycler.OnRefreshStatus onRefreshStatus) {
        mAdapter.clear();
        if (!Utils.isNetworkAvailable(getContext()))
            postEvent(new SnackbarEvent("No internet; Retrieving from cache"));
        if (mCall != null) mCall.cancel(); //cancel old if it's still running
        mCall = getAPICall(api);
        mCall.enqueue(new Callback<C>() {
            @Override
            public void onResponse(Call<C> call, Response<C> response) {
                CLog.d("RESPONSE DATA %s", response.toString());
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
    public void onSilentRefresh() {
        if (!Utils.isNetworkAvailable(getContext()))
            postEvent(new SnackbarEvent("No internet; Retrieving from cache"));
        if (mCall != null) mCall.cancel(); //cancel old if it's still running
        mCall = getAPICall(api);
        mCall.enqueue(new Callback<C>() {
            @Override
            public void onResponse(Call<C> call, Response<C> response) {
                CLog.e("SILENT RESPONSE DATA %s", response.toString());
                if (response.body() != null && response.isSuccessful())
                    onSilentResponseReceived(response.body());
            }

            @Override
            public void onFailure(Call<C> call, Throwable t) {
                if (!call.isCanceled())
                    CLog.e("Retrofit Silent OnFailure: %s", t.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mCall != null) mCall.cancel();
        super.onDestroy();
    }

    protected abstract Call<C> getAPICall(ITepid api);

    protected abstract void onResponseReceived(@NonNull C body, final ISwipeRecycler.OnRefreshStatus onRefreshStatus);

    /**
     * Callback for silent refreshes; must be implemented on a per fragment basis to use!
     *
     * @param body data received
     */
    protected void onSilentResponseReceived(@NonNull C body) {
    }

    public String getShortUser() {
        if (BaseFragment.getShortUser(this) == null) return AccountUtil.getShortUser();
        return BaseFragment.getShortUser(this);
    }

}
