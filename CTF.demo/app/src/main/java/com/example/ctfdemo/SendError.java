package com.example.ctfdemo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SendError extends AppCompatActivity {

    private int roomSelect;
    private String errorReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_error);
        setTitle(R.string.error_report_title);
        //refer to strings

        roomSelect = 0;
        errorReport = "Printer Down";

        final Button b_1b16 = (Button) findViewById(R.id.button_1b16);
        final Button b_1b17 = (Button) findViewById(R.id.button_1b17);
        final Button b_1b18 = (Button) findViewById(R.id.button_1b18);
        Button send_button = (Button) findViewById(R.id.button_send);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.problem_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        b_1b16.setBackgroundColor(Color.CYAN);
        b_1b17.setBackgroundColor(Color.GRAY);
        b_1b18.setBackgroundColor(Color.GRAY);

        OnClickListener onClickListener1B16 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                b_1b16.setBackgroundColor(Color.CYAN);
                b_1b17.setBackgroundColor(Color.GRAY);
                b_1b18.setBackgroundColor(Color.GRAY);
                roomSelect = 1;
            }
        };
        OnClickListener onClickListener1B17 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                b_1b16.setBackgroundColor(Color.GRAY);
                b_1b17.setBackgroundColor(Color.CYAN);
                b_1b18.setBackgroundColor(Color.GRAY);
                roomSelect = 2;
            }
        };
        OnClickListener onClickListener1B18 = new OnClickListener() {
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
    }
}