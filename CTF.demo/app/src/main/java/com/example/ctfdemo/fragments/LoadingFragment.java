package com.example.ctfdemo.fragments;

import android.support.annotation.Nullable;

import com.pitchedapps.capsule.library.event.CFabEvent;
import com.pitchedapps.capsule.library.fragments.CapsuleFragment;

/**
 * Created by Allan Wang on 2016-11-19.
 * If the token is null, don't bother trying to load a fragment; switch it to a loading view instead
 */

public class LoadingFragment extends CapsuleFragment{
    @Nullable
    @Override
    protected CFabEvent updateFab() {
        return null;
    }

    @Override
    public int getTitleId() {
        return 0;
    }
}
