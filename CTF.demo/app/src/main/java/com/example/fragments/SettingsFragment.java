package com.example.fragments;

import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;

import com.example.ctfdemo.R;

/**
 * Created by erasmas on 1/14/16.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
