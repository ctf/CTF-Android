package com.erasmas.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.erasmas.adapter.KeyAdapter;
import com.erasmas.fragment.FragmentAddKey;
import com.erasmas.fragment.FragmentKeyDialog;
import com.erasmas.solitaire.R.id;
import com.erasmas.solitaire.R.layout;

import java.util.Arrays;

public class FragmentKeys extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(layout.fragment_keys, container, false);

        // get sorted list of keys
        SharedPreferences keyStore = getActivity().getPreferences(Context.MODE_PRIVATE);
        String[] keyLabels = keyStore.getAll().keySet().toArray(new String[0]);
        Arrays.sort(keyLabels);

        // setup adapter with list of key labels
        KeyAdapter adapter = new KeyAdapter(getContext());
        adapter.setKeyLabels(keyLabels);

        // setup listview to display list of key labels
        ListViewCompat keyList = (ListViewCompat) rootView.findViewById(id.key_list);
        keyList.setAdapter(adapter);
        keyList.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                showDialog();
                return false;
            }
        });

        // setup new key button
        rootView.findViewById(id.new_key).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(id.content_frame, new FragmentAddKey()).commit();
            }
        });

        return rootView;
    }

    public void showDialog() {
        FragmentManager fm = getFragmentManager();
        FragmentKeyDialog dialog = FragmentKeyDialog.newInstance("dummy_title");
        dialog.show(fm, "");
    }

}
