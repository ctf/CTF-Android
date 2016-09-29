package com.erasmas.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erasmas.solitaire.R.layout;

public class FragmentKeyDialog extends DialogFragment {

    public FragmentKeyDialog() {
        // required empty constructor
    }

    public static FragmentKeyDialog newInstance(final String title) {
        FragmentKeyDialog f = new FragmentKeyDialog();
        return f;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle saveInstanceState) {
        final View rootView = inflater.inflate(layout.key_dialog_fragment, container);
        return rootView;
    }

}
