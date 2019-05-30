package com.example.notes;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note implements Serializable {
    private String title, date, content, titleShort, contentShort;

    public Note(String t, String d, String c){
        setTitle(t);
        date = d;
        setContent(c);
    }
    public void setTitle(String t){
        title = t;
        if(t.length() > 80){
            titleShort = t.substring(0,80) + "...";
        }
        else {
            titleShort = t;
        }
    }
    public void setContent(String c){
        content = c;
        if(c.length() > 80){
            contentShort = c.substring(0,80) + "...";
        }
        else{
            contentShort = c;
        }
    }
    public String getTitle(){
        return title;
    }
    public String getDate(){
        return date;
    }
    public String getContent(){
        return content;
    }
    public String getTitleShort(){
        return titleShort;
    }
    public String getContentShort(){
        return contentShort;
    }

    public static String makeDate(){
        DateFormat dateFormat = new SimpleDateFormat("E MMM d, KK:mm a");
        Date date = new Date();
        return dateFormat.format(date);
    }
}