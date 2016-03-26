package com.example.fragments;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.ctfdemo.R;

/**
 * Created by erasmas on 1/8/16.
 */
public class ReportProblemFragment extends Fragment {

    private int roomSelect;
    private String errorReport;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_send_error, container, false);

        roomSelect = 0;
        errorReport = "Printer Down";

        final Button b_1b16 = (Button) rootView.findViewById(R.id.button_1b16);
        final Button b_1b17 = (Button) rootView.findViewById(R.id.button_1b17);
        final Button b_1b18 = (Button) rootView.findViewById(R.id.button_1b18);
        Button send_button = (Button) rootView.findViewById(R.id.button_send);
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.problem_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        b_1b16.setBackgroundColor(Color.CYAN);
        b_1b17.setBackgroundColor(Color.GRAY);
        b_1b18.setBackgroundColor(Color.GRAY);

        View.OnClickListener onClickListener1B16 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_1b16.setBackgroundColor(Color.CYAN);
                b_1b17.setBackgroundColor(Color.GRAY);
                b_1b18.setBackgroundColor(Color.GRAY);
                roomSelect = 1;
            }
        };
        View.OnClickListener onClickListener1B17 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_1b16.setBackgroundColor(Color.GRAY);
                b_1b17.setBackgroundColor(Color.CYAN);
                b_1b18.setBackgroundColor(Color.GRAY);
                roomSelect = 2;
            }
        };
        View.OnClickListener onClickListener1B18 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_1b16.setBackgroundColor(Color.GRAY);
                b_1b17.setBackgroundColor(Color.GRAY);
                b_1b18.setBackgroundColor(Color.CYAN);
                roomSelect = 3;
            }
        };

        b_1b16.setOnClickListener(onClickListener1B16);
        b_1b17.setOnClickListener(onClickListener1B17);
        b_1b18.setOnClickListener(onClickListener1B18);


        return rootView;

    }
}
