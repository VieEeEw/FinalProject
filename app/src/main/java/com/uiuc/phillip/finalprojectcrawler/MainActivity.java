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
    private static int ROW_NUMBER = 10;

    private TextView[] crnsText = new TextView[10];
    private TextView[] avalText = new TextView[10];
    private TextView[] locaText = new TextView[10];
    private TextView[] timeText = new TextView[10];

    private Button nextPage;
    private Button prePage;
    private Button scrape;

    private EditText subject;
    private EditText number;
    private EditText crn;

    private Map<String, Map<String, String>> storedMap;
    private String[] storedTime;
    private String[] storedLoca;
    private String[] storedCrns;
    private String[] storedAval;

    private Map<String, String> toScrape = new HashMap<> ();

    private int currentPage = 0;
    private boolean hasNext = false;
    private boolean hasPre = false;

    private boolean findModeOn = false;
    private boolean parseByDefault = false;
    private int trickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        crnsText[0] = findViewById (R.id.CRN_1);
        crnsText[1] = findViewById (R.id.CRN_2);
        crnsText[2] = findViewById (R.id.CRN_3);
        crnsText[3] = findViewById (R.id.CRN_4);
        crnsText[4] = findViewById (R.id.CRN_5);
        crnsText[5] = findViewById (R.id.CRN_6);
        crnsText[6] = findViewById (R.id.CRN_7);
        crnsText[7] = findViewById (R.id.CRN_8);
        crnsText[8] = findViewById (R.id.CRN_9);
        crnsText[9] = findViewById (R.id.CRN_10);

        avalText[0] = findViewById (R.id.Availability_1);
        avalText[1] = findViewById (R.id.Availability_2);
        avalText[2] = findViewById (R.id.Availability_3);
        avalText[3] = findViewById (R.id.Availability_4);
        avalText[4] = findViewById (R.id.Availability_5);
        avalText[5] = findViewById (R.id.Availability_6);
        avalText[6] = findViewById (R.id.Availability_7);
        avalText[7] = findViewById (R.id.Availability_8);
        avalText[8] = findViewById (R.id.Availability_9);
        avalText[9] = findViewById (R.id.Availability_10);

        locaText[0] = findViewById (R.id.Location_1);
        locaText[1] = findViewById (R.id.Location_2);
        locaText[2] = findViewById (R.id.Location_3);
        locaText[3] = findViewById (R.id.Location_4);
        locaText[4] = findViewById (R.id.Location_5);
        locaText[5] = findViewById (R.id.Location_6);
        locaText[6] = findViewById (R.id.Location_7);
        locaText[7] = findViewById (R.id.Location_8);
        locaText[8] = findViewById (R.id.Location_9);
        locaText[9] = findViewById (R.id.Location_10);

        timeText[0] = findViewById (R.id.Time_1);
        timeText[1] = findViewById (R.id.Time_2);
        timeText[2] = findViewById (R.id.Time_3);
        timeText[3] = findViewById (R.id.Time_4);
        timeText[4] = findViewById (R.id.Time_5);
        timeText[5] = findViewById (R.id.Time_6);
        timeText[6] = findViewById (R.id.Time_7);
        timeText[7] = findViewById (R.id.Time_8);
        timeText[8] = findViewById (R.id.Time_9);
        timeText[9] = findViewById (R.id.Time_10);

        subject = findViewById (R.id.Subject);
        number = findViewById (R.id.CourseNumber);
        crn = findViewById (R.id.CRN);

        scrape = findViewById (R.id.scrape);
        scrape.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                nextPage.setVisibility (View.INVISIBLE);
                prePage.setVisibility (View.INVISIBLE);
                currentPage = 0;
                if (trickCount == 5) {
                    trickCount = 0;
                    parseByDefault = true;
                    Toast.makeText (MainActivity.this, "You found it! Welcome to CS125!",
                            Toast.LENGTH_SHORT).show();
                    new Thread(crawl).start();
                }
                if (subject.getText ().toString ().equals("Subject")) {
                    Toast.makeText (MainActivity.this, "Invalid subject!", Toast.LENGTH_SHORT).show();
                    trickCount++;
                    return;
                } else if (subject.getText ().toString ().matches ("[a-z, A-Z]{2,4}")) {
                    toScrape.put("subject", subject.getText ().toString ().toUpperCase ());
                } else {
                    Toast.makeText (MainActivity.this, "Invalid subject input!", Toast.LENGTH_SHORT).show();
                    trickCount++;
                    return;
                }
                if (number.getText ().toString ().matches ("[0-9]{3}")) {
                    toScrape.put ("number", number.getText ().toString ());
                } else {
                    Toast.makeText (MainActivity.this, "Invalid course number input!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (crn.getText ().toString ().matches ("[0-9]{5}")) {
                    findModeOn = true;
                    Toast.makeText (MainActivity.this, "Find mode on", Toast.LENGTH_SHORT).show();
                    toScrape.put("crn", crn.getText ().toString ());
                } else if (crn.getText ().toString ().equals("CRN to find") ||
                        crn.getText ().toString ().equals("")){
                    findModeOn = false;
                    Toast.makeText (MainActivity.this, "Find mode off", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText (MainActivity.this, "Invalid CRN input!", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < ROW_NUMBER; i++) {
                    crnsText[i].setText ("Scraping");
                    avalText[i].setText ("Scraping");
                    timeText[i].setText ("Scraping");
                    locaText[i].setText ("Scraping");
                }
                new Thread(crawl).start();
            }
        });

        nextPage = findViewById (R.id.nextPage);
        nextPage.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                currentPage++;
                hasPre = true;
                clear();
                int count;
                if (storedCrns.length - currentPage * ROW_NUMBER > ROW_NUMBER) {
                    hasNext = true;
                    count = ROW_NUMBER;
                } else {
                    hasNext = false;
                    count = storedCrns.length - currentPage * ROW_NUMBER;
                }
                set(count, currentPage * ROW_NUMBER);
                if (!hasNext) {
                    nextPage.setVisibility (View.INVISIBLE);
                }
                if (hasPre) {
                    prePage.setVisibility (View.VISIBLE);
                }
            }
        });

        prePage = findViewById (R.id.prePage);
        prePage.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                currentPage--;
                hasNext = true;
                clear();
                int count;
                if (storedCrns.length - currentPage * ROW_NUMBER > ROW_NUMBER) {
                    hasPre = true;
                    count = ROW_NUMBER;
                } else {
                    hasPre = false;
                    count = storedCrns.length - currentPage * ROW_NUMBER;
                }
                set(count, currentPage * ROW_NUMBER);
                if (currentPage == 0) {
                    prePage.setVisibility (View.INVISIBLE);
                }
                if (hasNext) {
                    nextPage.setVisibility (View.VISIBLE);
                }
            }
        });
    }

    Runnable crawl = new Runnable () {
        @Override
        public void run() {
            ParserXml parser;
            if (parseByDefault) {
                parser = new ParserXml();
            } else if (!findModeOn){
                parser = new ParserXml (toScrape.get("subject"), toScrape.get ("number"));
            } else {
                parser = new ParserXml (toScrape.get("subject"), toScrape.get("number"),
                        toScrape.get("crn"));
            }
            if (parser.parseFromXml ()) {
                storedCrns = parser.getCrns ();
                storedMap = parser.getMap ();
                storedAval = parser.getAval ();
                storedLoca = parser.getLoca ();
                storedTime = parser.getTheTime ();
                parsingSuccess.sendEmptyMessage (0);
            } else {
                Log.d("Parsing Error", "Parsing failed!");
                parsingError.sendEmptyMessage (0);
            }
        }
    };
    Handler parsingError = new Handler () {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage (msg);
            Toast.makeText (MainActivity.this, "Cannot find", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < ROW_NUMBER; i++) {
                crnsText[i].setText ("Cannot find your course!");
                avalText[i].setText ("Cannot find your course!");
                locaText[i].setText ("Cannot find your course!");
                timeText[i].setText ("Cannot find your course!");
            }
        }
    };
    Handler parsingSuccess = new Handler () {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int count;
            if (storedAval.length > 10) {
                hasNext = true;
                count = 10;
            } else {
                count = storedAval.length;
            }
            clear();
            set(count);
            if (hasNext) {
                nextPage.setVisibility (View.VISIBLE);
            }
        }
    };

    private void clear() {
        for (int i = 0; i < ROW_NUMBER; i++) {
            crnsText[i].setText ("");
            avalText[i].setText ("");
            locaText[i].setText ("");
            timeText[i].setText ("");
        }
    }
    private void set(int rowMax) {
        set(rowMax, 0);
    }
    private void set(int rowMax, int start) {
        for (int i = 0; i < rowMax; i++) {
            crnsText[i].setText (storedCrns[start + i]);
            avalText[i].setText (storedAval[start + i]);
            timeText[i].setText (storedTime[start + i]);
            locaText[i].setText (storedLoca[start + i]);
        }
    }
}