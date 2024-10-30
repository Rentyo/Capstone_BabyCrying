package com.example.a1215dday;

public class ListAccountLog {
    int num;
    String DateTime;
    String Log;
    public ListAccountLog(int num, String date, String log){
        this.num = num;
        this.DateTime = date;
        this.Log = log;
    }
    public int getNum() {
        return num;
    }

    public String getDateTime() {
        return DateTime;
    }

    public String getLog() {
        return Log;
    }
}
