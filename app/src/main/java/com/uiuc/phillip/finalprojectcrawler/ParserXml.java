package com.uiuc.phillip.finalprojectcrawler;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserXml {
    private static final String URL_BASE = "https://courses.illinois.edu/cisapp/explorer/schedule/2019/spring/";
    private static final String URL_DEFAULT =
            "https://courses.illinois.edu/cisapp/explorer/schedule/2019/spring/CS/125.xml";
    private String urlToParse;

    private List<String> avalList = new ArrayList<> ();
    private List<String> timeList = new ArrayList<> ();
    private List<String> locaList = new ArrayList<> ();
    private List<String> typeList = new ArrayList<> ();
    private String[] crns;
    private String[] aval;
    private String[] time;
    private String[] loca;
    private String[] type;

    private String crnToFind;

    ParserXml() {
        urlToParse = URL_DEFAULT;
    }

    ParserXml (String courseName, String courseNumber, String crn) {
        this(courseName, courseNumber);
        crnToFind = crn;
    }

    ParserXml(String subject, String number) {
        urlToParse = URL_BASE + subject.toUpperCase () + "/" + number + ".xml";
        crnToFind = null;
    }
    public boolean parseFromXml() {
        Document doc;
        ArrayList<String> crns = new ArrayList<> ();
        try {
            doc = Jsoup.connect (urlToParse).get ();
        } catch (IOException e) {
            try {
                Log.d ("Connection error", "Cannot connect");
            } catch (Exception error) {
                System.out.println ("Cannot connect");
            }
            return false;
        } finally {
            //Log.d("url", urlToParse);
            System.out.println (urlToParse);
        }
        Elements elements = doc.select("section");
        if (elements == null) {
            try {
                Log.d ("Parsing error", "Cannot find section");
            } catch (Exception e) {
                System.out.println ("Cannot find section");
            }
            return false;
        }
        for (Element e: elements) {
            crns.add (e.id ().trim());
            if (!childPage (e.attr ("href"))) {
                return false;
            }
        }
        this.crns = new String[crns.size()];
        crns.toArray (this.crns);
        this.time = new String[this.timeList.size()];
        this.timeList.toArray (this.time);
        this.aval = new String[this.avalList.size()];
        this.avalList.toArray (this.aval);
        this.loca = new String[this.locaList.size()];
        this.locaList.toArray (this.loca);
        this.type = new String[this.typeList.size()];
        this.typeList.toArray (this.type);
        for (int i = 0; i < this.crns.length; i++) {
            this.crns[i] = this.crns[i] + "(" + this.type[i] + ")";
        }
        try{
            getAval ();
        } catch (NullPointerException error) {
            return false;
        }
        return true;
    }

    private boolean childPage(String url) {
        Document doc;
        try {
            doc = Jsoup.connect (url).get();
        } catch (IOException e) {
            try {
                Log.d ("Connection error", "Cannot connect to child page");
            } catch (Exception error) {
                System.out.println ("Cannot connect to child page");
            }
            return false;
        }
        Element e = doc.select("enrollmentStatus").first ();
        if (e == null) {
            try {
                Log.d ("Parsing Error", "Cannot find Tag");
            } catch (Exception error) {
                System.out.println ("Cannot find Tag");
            }
            return false;
        }
        this.avalList.add(e.text().trim());
        String courseType = doc.selectFirst ("type").text().trim();
        this.typeList.add(courseType);
        String timeInterval = doc.selectFirst ("start").text().trim();
        timeInterval += " - ";
        timeInterval += doc.selectFirst ("end").text().trim();
        timeInterval += " on ";
        timeInterval += doc.selectFirst ("daysOfTheWeek").text().trim();
        this.timeList.add(timeInterval);
        String location;
        try {
            location = doc.selectFirst ("buildingName").text ().trim ();
        } catch (NullPointerException error) {
            location = "No designated room.";
            this.locaList.add(location);
            return true;
        }
        location += " ";
        location += doc.selectFirst ("roomNumber").text().trim();
        this.locaList.add (location);
        return true;
    }

    public String[] getCrns() {
        if (crnToFind == null) {
            return crns;
        } else {
            String[] toReturn = new String[1];
            toReturn[0] = crnToFind;
            return toReturn;
        }
    }

    public String[] getAval() {
        if (crnToFind == null) {
            return aval;
        } else {
            String[] toReturn = new String[1];
            toReturn[0] = getMap().get (crnToFind).get("Availability");
            return toReturn;
        }
    }

    public String[] getLoca() {
        if (crnToFind == null) {
            return loca;
        } else {
            String[] toReturn = new String[1];
            toReturn[0] = getMap().get (crnToFind).get("Location");
            return toReturn;
        }
    }

    public String[] getTheTime() {
        if (crnToFind == null) {
            return time;
        } else {
            String[] toReturn = new String[1];
            toReturn[0] = getMap().get (crnToFind).get("Time");
            return toReturn;
        }
    }

    public Map<String, Map<String, String>> getMap() {
        if (crns == null || aval == null || loca == null || time == null) {
            return null;
        }
        Map<String, Map<String, String>> toReturn = new HashMap<> ();
        for (int i = 0; i < crns.length; i++) {
            Map<String, String> tuple = new HashMap<> ();
            tuple.put("Availability", aval[i]);
            tuple.put("Location", loca[i]);
            tuple.put("Time", time[i]);
            toReturn.put(crns[i], tuple);
        }
        return toReturn;
    }
}

class Ttest {
    public static void main(String[] args) {
        ParserXml parser = new ParserXml ();
        if (parser.parseFromXml ()) {
            System.out.println (Arrays.toString (parser.getAval ()));
            System.out.println (Arrays.toString (parser.getCrns ()));
            System.out.println (Arrays.toString (parser.getTheTime ()));
            System.out.println (Arrays.toString (parser.getLoca ()));
        }
    }
}