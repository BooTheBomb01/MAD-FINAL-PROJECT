package edu.lewisu.cs.spreitzermatt.matthewspreitzercalendar;

public class Calendar {

    private String uid;
    private String date;
    private String title;
    private String body;


    public Calendar(String uid, String date, String title, String body) {
        this.uid = uid;
        this.date = date;
        this.title = title;
        this.body = body;
    }

    public Calendar(String uid) {
        this.uid = uid;
    }

    public Calendar() {
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
