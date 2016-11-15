package com.example.ctfdemo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.ctfdemo.requests.CTFSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.pitchedapps.capsule.library.event.CFabEvent;
import com.pitchedapps.capsule.library.fragments.CapsuleFragment;

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

    protected static Fragment fragmentWithToken(Fragment fragment, String token) {
        Bundle args = new Bundle();
        args.putString(KEY_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle args = getArguments();
        if (args != null) {
            token = args.getString(KEY_TOKEN);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        requestManager.start(getActivity());

        getUIData();
    }

    protected abstract void getUIData();

    @Override
    public void onStop() {
        if (requestManager.isStarted()) {
            requestManager.shouldStop();
        }
        super.onStop();
    }

    @Nullable
    @Override
    protected CFabEvent updateFab() {
        return null;
    }
}
