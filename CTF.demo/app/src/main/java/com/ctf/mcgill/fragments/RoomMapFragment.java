package com.ctf.mcgill.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ctf.mcgill.R;

public class RoomMapFragment extends DialogFragment {

    public RoomMapFragment() {
        // required empty constructor, see newInstance for init
    }

    public static RoomMapFragment newInstance(String title) {
        RoomMapFragment frag = new RoomMapFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        return inflater.inflate(R.layout.room_map, container);
    }



}
