package com.example.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ctfdemo.R;

/**
 * Created by erasmas on 1/8/16.
 */
// this fragment is swapped out by the fragment manager in the main activity
// probably needs to be replaced by an AppCompatActivity depending on the functionality this page needs
// on the bright side at least the navigation drawer functions on this page...
public class UserInfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_userinfo, container, false);

        return rootView;

    }
}
