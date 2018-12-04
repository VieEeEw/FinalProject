package com.uiuc.phillip.finalprojectcrawler;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private TableRow r1;
    private TableRow r2;
    private TableRow r3;
    private TableRow r4;
    private TableRow r5;
    private TableRow r6;
    private TableRow r7;
    private TableRow r8;
    private TableRow r9;
    private TableRow r10;
    //private TableRow r11;
    TextView[] CRNs = new TextView[10];
    TextView[] Availabilities = new TextView[10];
    /*private TextView CRN_Instruction;
    private TextView CRN_1;
    private TextView CRN_2;
    private TextView CRN_3;
    private TextView CRN_4;
    private TextView CRN_5;
    private TextView CRN_6;
    private TextView CRN_7;
    private TextView CRN_8;
    private TextView CRN_9;
    private TextView Availability_Instruction;
    private TextView Availability_1;
    private TextView Availability_2;
    private TextView Availability_3;
    private TextView Availability_4;
    private TextView Availability_5;
    private TextView Availability_6;
    private TextView Availability_7;
    private TextView Availability_8;
    private TextView Availability_9;
    //private TextView Availability_10;
    */
    private Button toClick;

    private String[] crns;
    private String[] aval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        r1 = findViewById(R.id.r1);
        r2 = findViewById(R.id.r2);
        r3 = findViewById(R.id.r3);
        r4 = findViewById(R.id.r4);
        r5 = findViewById(R.id.r5);
        r6 = findViewById(R.id.r6);
        r7 = findViewById(R.id.r7);
        r8 = findViewById(R.id.r8);
        r9 = findViewById(R.id.r9);
        r10 = findViewById(R.id.r10);
        //r11 = findViewById(R.id.r11);
        CRNs[0] = findViewById (R.id.CRN_Instruction);
        CRNs[1] = findViewById (R.id.CRN_1);
        CRNs[2] = findViewById (R.id.CRN_2);
        CRNs[3] = findViewById (R.id.CRN_3);
        CRNs[4] = findViewById (R.id.CRN_4);
        CRNs[5] = findViewById (R.id.CRN_5);
        CRNs[6] = findViewById (R.id.CRN_6);
        CRNs[7] = findViewById (R.id.CRN_7);
        CRNs[8] = findViewById (R.id.CRN_8);
        CRNs[9] = findViewById (R.id.CRN_9);
        //CRN_10 = findViewById (R.id.CRN_10);
        Availabilities[0] = findViewById (R.id.Availability_Instruction);
        Availabilities[1] = findViewById (R.id.Availability_1);
        Availabilities[2] = findViewById (R.id.Availability_2);
        Availabilities[3] = findViewById (R.id.Availability_3);
        Availabilities[4] = findViewById (R.id.Availability_4);
        Availabilities[5] = findViewById (R.id.Availability_5);
        Availabilities[6] = findViewById (R.id.Availability_6);
        Availabilities[7] = findViewById (R.id.Availability_7);
        Availabilities[8] = findViewById (R.id.Availability_8);
        Availabilities[9] = findViewById (R.id.Availability_9);
        //Availability_10 = findViewById (R.id.Availability_10);
        toClick = findViewById (R.id.toClick);
        toClick.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 9; i++) {
                    CRNs[i + 1].setText ("Scraping");
                    Availabilities[i + 1].setText ("Scraping");
                }
                new Thread(runnable).start();
                Log.d("hello", "World");
            }
        });

    }
    Runnable runnable = new Runnable () {
        @Override
        public void run() {
            Parser parser = new Parser ();
            parser.parseForCrn ();
            crns = parser.getCrns ();
            aval = parser.getAval ();
            handler.sendEmptyMessage(0);
        }
    };
    Handler handler = new Handler () {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            for (int i = 0; i < 9; i++) {
                CRNs[i + 1].setText (crns[i]);
                Availabilities[i + 1].setText (aval[i]);
            }
        }
    };
}