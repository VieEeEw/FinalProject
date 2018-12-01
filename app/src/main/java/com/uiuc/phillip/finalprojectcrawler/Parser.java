package com.uiuc.phillip.finalprojectcrawler;

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

    public Parser() {
        urlToParse = "https://courses.illinois.edu/schedule/2019/spring/MATH/441";
    }
    Parser(String courseName, String courseNumber) {
        urlToParse = urlBase + courseName + "/" + courseNumber;
    }
    public ArrayList<String> parseForCrn() {
        Document doc;
        try{
            doc = Jsoup.connect(urlToParse).get();
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        Elements scripts = doc.select("script");
        String useful = null;
        for (int i = 0; i < scripts.size(); i++) {
            Element script = scripts.get(i);
            if (script.data().contains("crn")) {
                useful = script.data();
                break;
            }
        }
        if (useful == null) {
            return null;
        }
        Pattern pattern = Pattern.compile ("crn.+?([0-9]+)");
        Matcher matcher1 = pattern.matcher (useful);
        String name = null;
        if (matcher1.find()) {
            name = matcher1.group(1);
        }

        /*

        */
}

class Test {
    public static void main(String[] args) {
        Parser p = new Parser();
        System.out.println(Arrays.toString(p.parseForCrn().toArray()));
    }
}