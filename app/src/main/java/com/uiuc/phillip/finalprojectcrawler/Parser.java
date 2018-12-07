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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

import java.io.IOException;


public class Parser {
    public static final String URL_BASE = "https://courses.illinois.edu/schedule/2019/spring/";
    private String urlToParse;

    private List<String[]> stored;

    private String crnToFind;

    public Parser() {
        urlToParse = "https://courses.illinois.edu/schedule/2019/spring/CS/125";
    }

    Parser(String courseName, String courseNumber) {
        urlToParse = URL_BASE + courseName + "/" + courseNumber;
        //System.out.println (urlToParse);
        //Log.d("url", urlToParse);
    }

    Parser (String courseName, String courseNumber, String crn) {
        this(courseName, courseNumber);
        crnToFind = crn;
    }

    public boolean parseForCrn() {
        Document doc;
        try {
            doc = Jsoup.connect (urlToParse).get ();
        } catch (IOException e) {
            e.printStackTrace ();
            //Log.d("parsing", "IO");
            //System.out.println ("IO");
            return false;
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
            //Log.d("parsing", "No script");
            //System.out.println ("no scrpit");
            return false;
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
            //Log.d("parsing", "No CRN");
            //System.out.println ("No crn");
            return false;
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
            //Log.d("parsing", "Not the end");
            //System.out.println ("Not the end");
            return false;
        }
        ArrayList<String[]> toReturn = new ArrayList<> (2);
        if (crnToFind != null) {
            int idx = Arrays.asList(crns).indexOf (crnToFind);
            String[] c = {crnToFind, };
            String[] a = {aval[idx], };
            toReturn.add(a);
            toReturn.add(c);
            stored = toReturn;
            return true;
        }
        toReturn.add(aval);
        toReturn.add(crns);
        stored = toReturn;
        return true;
    }
    public String[] getCrns() {
        return stored.get (1);
    }
    public String[] getAval() {
        return stored.get (0);
    }
    public Map<String, String> getMap() {
        HashMap<String, String> toReturn = new HashMap<> ();
        for (int i = 0; i < getCrns ().length; i++) {
            toReturn.put(getAval ()[i], getCrns ()[i]);
        }
        return toReturn;
    }
}

class Test {
    public static void main(String[] args) throws Exception {
        Parser p = new Parser ("CS", "125", "31155");
        p.parseForCrn ();
        String[] avl = p.getAval ();
        String[] crns = p.getCrns ();
        for (int i = 0; i < avl.length; i++) {
            System.out.println (avl[i] + " : " + crns[i]);
        }
    }
}
