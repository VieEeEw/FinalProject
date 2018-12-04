package com.uiuc.phillip.finalprojectcrawler;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        new Thread(runnable).run();
    }
    Runnable runnable = new Runnable () {
        @Override
        public void run() {
            Parser parser = new Parser ();
            parser.parseForCrn ();
            Log.d ("ONCREATE", Arrays.toString (parser.getAval ()));
        }
    };
}