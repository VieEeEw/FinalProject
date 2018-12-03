package com.uiuc.phillip.finalprojectcrawler;

import android.nfc.Tag;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.*;

import java.io.IOException;


public class Parser {
    public static final String urlBase = "https://courses.illinois.edu/schedule/2019/spring/";
    private String urlToParse;

    private ArrayList<String[]> stored;

    public Parser() {
        urlToParse = "https://courses.illinois.edu/schedule/2019/spring/MATH/441";
    }

    Parser(String courseName, String courseNumber) {
        urlToParse = urlBase + courseName + "/" + courseNumber;
    }

    public ArrayList<String[]> parseForCrn() {
        Document doc;
        try {
            doc = Jsoup.connect (urlToParse).get ();
        } catch (IOException e) {
            e.printStackTrace ();
            return null;
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
            return null;
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
            return null;
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
            return  null;
        }
        ArrayList<String[]> toReturn = new ArrayList<> (2);
        toReturn.add(aval);
        toReturn.add(crns);
        stored = toReturn;
        return toReturn;
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
        while (true) {
            Parser p = new Parser ();
            ArrayList<String[]> store = p.parseForCrn ();
            System.out.println (Arrays.deepToString (store.toArray ()));
            String[] avl = store.get (0);
            String[] crns = store.get (1);
            for (int i = 0; i < avl.length; i++) {
                System.out.println ("error");
                System.out.println (avl[i] + " : " + crns[i]);
            }
            Thread.sleep (3000);
        }
    }
}
