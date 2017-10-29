package wy.orbproto;

import java.util.ArrayList;

/**
 * Created by weiyang on 6/16/2017.
 */

public class Event {
    private String year;
    private String month;
    private String day;
    private int startTimeHour;
    private int startTimeMinute;
    private int endTimeHour;
    private int endTimeMinute;
    private String title;
    private String location;
    private ArrayList<String> attending = new ArrayList<String>();
    private ArrayList<String> notattending = new ArrayList<String>();
    private String trainingOverview;
    private String nameDay;

    public Event(){

    }

    public Event(int startTimeHour, int startTimeMinute, int endTimeHour, int endTimeMinute) {
        this.startTimeHour = startTimeHour;
        this.startTimeMinute = startTimeMinute;
        this.endTimeHour = endTimeHour;
        this.endTimeMinute = endTimeMinute;
    }

    public String getYear() {
        return year;
    }

    public String getNameDay() {
        return nameDay;
    }

    public void setNameDay(String nameDay) {
        this.nameDay = nameDay;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public int getStartTimeHour() {
        return startTimeHour;
    }

    public int getStartTimeMinute() {
        return startTimeMinute;
    }

    public int getEndTimeHour() {
        return endTimeHour;
    }

    public int getEndTimeMinute() {
        return endTimeMinute;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public ArrayList<String> getAttending() {
        return attending;
    }

    public ArrayList<String> getNotattending() {
        return notattending;
    }

    public String getTrainingOverview() {
        return trainingOverview;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setStartTimeHour(int startTimeHour) {
        this.startTimeHour = startTimeHour;
    }

    public void setStartTimeMinute(int startTimeMinute) {
        this.startTimeMinute = startTimeMinute;
    }

    public void setEndTimeHour(int endTimeHour) {
        this.endTimeHour = endTimeHour;
    }

    public void setEndTimeMinute(int endTimeMinute) {
        this.endTimeMinute = endTimeMinute;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTrainingOverview(String trainingOverview) {
        this.trainingOverview = trainingOverview;
    }
    public void addNotAttending(ArrayList<String> ID){
        this.notattending = ID;
    }
}
