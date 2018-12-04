package com.uiuc.phillip.finalprojectcrawler;

import android.nfc.Tag;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.*;

import java.io.IOException;


public class Parser {
    public static final String URL_BASE = "https://courses.illinois.edu/schedule/2019/spring/";
    private String urlToParse;

    private ArrayList<String[]> stored;

    public Parser() {
        urlToParse = "https://courses.illinois.edu/schedule/2019/spring/CS/125";
    }

    Parser(String courseName, String courseNumber) {
        urlToParse = URL_BASE + courseName.toUpperCase () + "/" + courseNumber;
    }

    public void parseForCrn() {
        Document doc;
        try {
            doc = Jsoup.connect (urlToParse).get ();
        } catch (IOException e) {
            e.printStackTrace ();
            return;
        }
        Elements scripts = doc.select ("script");
        String useful = null;
        for (int i = 0; i < scripts.size (); i++) {
            Element script = scripts.get (i);
            if (script.data ().contains ("crn")) {
                useful = script.data ();
                break;
            }
        }
        if (useful == null) {
            return;
        }
        ArrayList<String> toUse = new ArrayList<> ();
        for (String str: useful.split("crn...")) {
            if (str.contains("credit")) {
                for (String str1: str.split("...credit")) {
                    if (str1.contains("availability")) {
                        toUse.add (str1);
                    }
                }
            }
        }
        if (toUse.size() == 0) {
            return;
        }
        String[] aval = new String[toUse.size()];
        String[] crns = new String[toUse.size()];
        int i = 0;
        for (String str: toUse) {
            aval[i] = str.split ("availability...")[1];
            crns[i] = str.split ("...type")[0];
            i++;
        }
        if (i != toUse.size()) {
            return;
        }
        ArrayList<String[]> toReturn = new ArrayList<> (2);
        toReturn.add(aval);
        toReturn.add(crns);
        stored = toReturn;
    }
    public String[] getCrns() {
        return stored.get (1);
    }
    public String[] getAval() {
        return stored.get (0);
    }
}

class Test {
    public static void main(String[] args) throws Exception {
        Parser p = new Parser ("cS", "125");
        p.parseForCrn ();
        String[] avl = p.getAval ();
        String[] crns = p.getCrns ();
        for (int i = 0; i < avl.length; i++) {
            System.out.println (avl[i] + " : " + crns[i]);
        }
    }
}
