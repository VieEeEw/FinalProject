package com.uiuc.phillip.finalprojectcrawler;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    TextView[] CRNs = new TextView[11];
    TextView[] Availabilities = new TextView[11];

    private EditText subject;
    private EditText courseNumber;
    private EditText crn;

    private Button scrape;
    private Button nextPage;
    private Button prePage;

    private Map<String, String> storedMap;
    private String[] storedCRNs;
    private String[] storedAval;

    private Map<String, String> toScrape = new HashMap<> ();

    private int currentPage = 0;
    private boolean hasNextPage = false;
    private boolean hasPrePage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

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
        CRNs[10] = findViewById (R.id.CRN_10);

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
        Availabilities[10] = findViewById (R.id.Availability_10);

        scrape = findViewById (R.id.scrape);
        nextPage = findViewById (R.id.nextPage);
        prePage = findViewById (R.id.prePage);

        subject = findViewById (R.id.subject);
        courseNumber = findViewById (R.id.number);
        crn = findViewById (R.id.crn);

        scrape.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (subject.getText ().toString ().matches (".+")) {
                    toScrape.put("subject", subject.getText ().toString ().toUpperCase ());
                } else {
                    return;
                }
                if (courseNumber.getText ().toString ().matches ("[0-9]{3}")) {
                    toScrape.put ("courseNumber", courseNumber.getText ().toString ());
                } else {
                    return;
                }
                toScrape.put("crn", crn.getText ().toString ());
                for (int i = 0; i < 10; i++) {
                    CRNs[i + 1].setText ("Scraping");
                    Availabilities[i + 1].setText ("Scraping");
                }
                new Thread(runnable).start();
            }
        });

        nextPage.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                currentPage++;
                hasPrePage = true;
                prePage.setVisibility (View.VISIBLE);
                int count;
                if ((storedAval.length - currentPage * 10) > 10) {
                    count = currentPage * 10 + 10;
                } else {
                    hasNextPage = false;
                    count = storedAval.length;
                }
                for (int i = 1; i < 11; i++) {
                    CRNs[i].setText ("");
                    Availabilities[i].setText ("");
                }
                for (int i = currentPage * 10; i < count; i++) {
                    CRNs[i + 1 - currentPage * 10].setText (storedCRNs[i]);
                    Availabilities[i + 1 - currentPage * 10].setText (storedAval[i]);
                }
                if (!hasNextPage) {
                    nextPage.setVisibility (View.INVISIBLE);
                }
            }
        });

        prePage.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                currentPage--;
                hasNextPage = true;
                nextPage.setVisibility (View.VISIBLE);
                if (currentPage == 0) {
                    hasPrePage = false;
                }
                for (int i = currentPage * 10; i < currentPage * 10 + 10; i++) {
                    CRNs[i + 1 - currentPage * 10].setText (storedCRNs[i]);
                    Availabilities[i + 1 - currentPage * 10].setText (storedAval[i]);
                }
                if (!hasPrePage) {
                    prePage.setVisibility (View.INVISIBLE);
                }
            }
        });
    }
    Runnable runnable = new Runnable () {
        @Override
        public void run() {
            Parser parser = new Parser ();
            parser.parseForCrn ();
            storedMap = parser.getMap ();
            storedAval = parser.getAval ();
            storedCRNs = parser.getCrns ();
            handler.sendEmptyMessage(0);
        }
    };
    Handler handler = new Handler () {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int count;
            if (storedAval.length > 10) {
                hasNextPage = true;
                count = 10;
            } else {
                count = storedAval.length;
            }
            for (int i = 0; i < count; i++) {
                CRNs[i + 1].setText (storedCRNs[i]);
                Availabilities[i + 1].setText (storedAval[i]);
            }
            if (hasNextPage) {
                nextPage.setVisibility (View.VISIBLE);
            }
        }
    };
}