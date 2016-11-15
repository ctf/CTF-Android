package com.example.ctfdemo.fragments;

import android.graphics.Color;
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
import com.pitchedapps.capsule.library.event.CFabEvent;
import com.pitchedapps.capsule.library.fragments.CapsuleFragment;

public class ReportProblemFragment extends CapsuleFragment {

    //todo:  mock up ErrorReport object to send to tepid,
    //todo:  include room, general problem info, optional additional details (station number, paper jam, etc),
    //todo:  user submitting the report, date report was submitted, etc.

    public static final String TAG = "REPORT_PROBLEM_FRAGMENT";

    private static int roomSelect;
    private static String errorMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report_problem, container, false);

        roomSelect = 0;
        errorMessage = "";

        final Button b_1b16 = (Button) rootView.findViewById(R.id.button_1b16);
        final Button b_1b17 = (Button) rootView.findViewById(R.id.button_1b17);
        final Button b_1b18 = (Button) rootView.findViewById(R.id.button_1b18);

        b_1b16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_1b16.setBackgroundColor(Color.RED);
                b_1b17.setBackgroundColor(Color.GRAY);
                b_1b18.setBackgroundColor(Color.GRAY);
                roomSelect = 1;
            }
        });
        b_1b17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_1b16.setBackgroundColor(Color.GRAY);
                b_1b17.setBackgroundColor(Color.RED);
                b_1b18.setBackgroundColor(Color.GRAY);
                roomSelect = 2;
            }
        });
        b_1b18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_1b16.setBackgroundColor(Color.GRAY);
                b_1b17.setBackgroundColor(Color.GRAY);
                b_1b18.setBackgroundColor(Color.RED);
                roomSelect = 3;
            }
        });

        b_1b16.setBackgroundColor(Color.GRAY);
        b_1b17.setBackgroundColor(Color.GRAY);
        b_1b18.setBackgroundColor(Color.GRAY);

        // set up dropdown menu with list of problems
        final Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.problem_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // when an item in the dropdown is clicked, set the global "error message" string accordingly
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ReportProblemFragment.setErrorMessage(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ReportProblemFragment.setErrorMessage(-1);
            }
        });

        // handle clicks on "SEND" button...
        rootView.findViewById(R.id.button_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: we should do something with this error report...
                generateErrorReport();
            }
        });

        return rootView;
    }

    private static void setErrorMessage(int dropdownMenuIndex) {
        switch (dropdownMenuIndex) {
            case -1 :
                errorMessage = "[no error selected]";
                break;
            case 0 :
                errorMessage = "Printer Down";
                break;
            case 1 :
                errorMessage = "Computer Down";
                break;
        }
    }

    //todo here we would extract all info selected by user, add timestamp, username, etc, and send the object to tepid/ticketing system
    private String generateErrorReport() {
        String errorReport = " Problem in room ";

        switch (roomSelect) {
            case 0:
                // TODO: signal the idiot user they must select a room
                break;
            case 1:
                errorReport += "1B16 :";
                break;
            case 2:
                errorReport += "1B17 :";
                break;
            case 3:
                errorReport += "1B18 :";
                break;
        }

        errorReport += errorMessage;
        return errorReport;
    }

    @Nullable
    @Override
    protected CFabEvent updateFab() {
        return null;
    }

    @Override
    public int getTitleId() {
        return R.string.reportproblem;
    }
}
