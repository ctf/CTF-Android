package com.example.fragments;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.ctfdemo.R;

public class ReportProblemFragment extends Fragment {

    private static int roomSelect;
    private static String errorMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_report_problem, container, false);

        roomSelect = 0;
        errorMessage = "";

        Button send_button = (Button) rootView.findViewById(R.id.button_send);

        // buttons to select the room
        final Button b_1b16 = (Button) rootView.findViewById(R.id.button_1b16);
        final Button b_1b17 = (Button) rootView.findViewById(R.id.button_1b17);
        final Button b_1b18 = (Button) rootView.findViewById(R.id.button_1b18);

        b_1b16.setBackgroundColor(Color.GRAY);
        b_1b17.setBackgroundColor(Color.GRAY);
        b_1b18.setBackgroundColor(Color.GRAY);

        final Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.problem_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        View.OnClickListener onClickListener1B16 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_1b16.setBackgroundColor(Color.RED);
                b_1b17.setBackgroundColor(Color.GRAY);
                b_1b18.setBackgroundColor(Color.GRAY);
                roomSelect = 1;
            }
        };
        View.OnClickListener onClickListener1B17 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_1b16.setBackgroundColor(Color.GRAY);
                b_1b17.setBackgroundColor(Color.RED);
                b_1b18.setBackgroundColor(Color.GRAY);
                roomSelect = 2;
            }
        };
        View.OnClickListener onClickListener1B18 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_1b16.setBackgroundColor(Color.GRAY);
                b_1b17.setBackgroundColor(Color.GRAY);
                b_1b18.setBackgroundColor(Color.RED);
                roomSelect = 3;
            }
        };
        // when an item in the dropdown is clicked, set the global "error message" string correspondingly
        AdapterView.OnItemSelectedListener spinnerDropdownSelectionListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ReportProblemFragment.setErrorMessage(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ReportProblemFragment.setErrorMessage(-1);
            }
        };
        View.OnClickListener onClickListenerSendReport = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: we should do something with the error report...
                generateErrorReport();
            }
        };

        b_1b16.setOnClickListener(onClickListener1B16);
        b_1b17.setOnClickListener(onClickListener1B17);
        b_1b18.setOnClickListener(onClickListener1B18);
        spinner.setOnItemSelectedListener(spinnerDropdownSelectionListener);
        send_button.setOnClickListener(onClickListenerSendReport);

        return rootView;

    }

    private static void setErrorMessage(int dropdownMenuIndex) {
        switch (dropdownMenuIndex) {
            case -1 :
                errorMessage = "";
            case 0 :
                errorMessage = "Printer Down";
            case 1 :
                errorMessage = "Computer Down";
            case 2 :
                errorMessage = "pls halp";
        }
    }

    // TODO: why is this hitting each case statement?!
    private String generateErrorReport() {
        String errorReport = " Problem in room ";
        switch (roomSelect) {
            case 0: {
                // TODO: signal the idiot user they must select a room
            }
            case 1: {
                errorReport += "1B16 ";
            }
            case 2: {
                errorReport += "1B17 ";
            }
            case 3: {
                errorReport += "1B18 ";
            }
        }
        errorReport += errorMessage;
        return errorReport;
    }
}
