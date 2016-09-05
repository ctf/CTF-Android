package com.example.ctfdemo.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.ctfdemo.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        System.out.print("poop");
    }

}
