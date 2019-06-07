package com.example.na00556573.eventregister;

public class EventValue {
    private String Name;
    private String Date;
    private String Time;
    private String Venue;
    private String Image;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public EventValue(String name, String date, String time, String venue, String image) {
        Name = name;
        Date = date;
        Time = time;
        Venue = venue;
        Image = image;
    }

    public EventValue() {
    }

    public void setName(String name) {
        Name = name;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setVenue(String venue) {
        Venue = venue;
    }

    public String getName() {
        return Name;
    }

    public String getDate() {
        return Date;
    }

    public String getTime() {
        return Time;
    }

    public String getVenue() {
        return Venue;
    }
}
