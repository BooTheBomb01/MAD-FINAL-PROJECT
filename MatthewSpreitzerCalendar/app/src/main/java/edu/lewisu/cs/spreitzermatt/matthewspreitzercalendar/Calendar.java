package edu.lewisu.cs.spreitzermatt.matthewspreitzercalendar;

public class Calendar {

    private String uid;
    private String date;
    private String title;
    private String body;
    private String time;

    public Calendar(String uid, String date, String title, String body, String time) {
        this.uid = uid;
        this.date = date;
        this.title = title;
        this.body = body;
        this.time = time;
    }

    public Calendar(String uid) {
        this.uid = uid;
    }

    public Calendar() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
