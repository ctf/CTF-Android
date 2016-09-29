package com.erasmas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.erasmas.solitaire.R.id;
import com.erasmas.solitaire.R.layout;

public class KeyAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private String[] keyLabels;

    public KeyAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setKeyLabels(String[] keyLabels) {
        this.keyLabels = keyLabels;
    }

    @Override
    public int getCount() {
        return keyLabels.length;
    }

    @Override
    public Object getItem(int position) {
        return keyLabels[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = inflater.inflate(layout.key_list_item, parent, false);
        ((TextView) rootView.findViewById(id.key_label)).setText(keyLabels[position]);
        return rootView;
    }
}
