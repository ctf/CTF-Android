package com.ctf.mcgill.fragments;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.ctf.mcgill.requests.CTFSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.pitchedapps.capsule.library.event.CFabEvent;
import com.pitchedapps.capsule.library.event.SnackbarEvent;
import com.pitchedapps.capsule.library.fragments.CapsuleFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Allan Wang on 2016-11-15.
 * <p>
 * Combine some common functions of the other fragments
 * //TODO check if it's okay to have a protected token String
 */

public abstract class BaseFragment extends CapsuleFragment {

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

    //TODO check if you still need requestmanager after oncreate
    @Override
    @CallSuper
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (!requestManager.isStarted()) requestManager.start(getActivity());
        getUIData(requestManager);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!requestManager.isStarted()) requestManager.start(getActivity());
    }

    protected abstract void getUIData(SpiceManager requestManager);

    @Override
    public void onStop() {
        // Please review https://github.com/octo-online/robospice/issues/96 for the reason of that
        // ugly if statement.
        if (requestManager.isStarted()) requestManager.shouldStop();
        super.onStop();
    }

    @Nullable
    @Override
    protected CFabEvent updateFab() {
        return null;
    }
}
