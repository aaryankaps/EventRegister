package com.example.na00556573.eventregister;

public class EventValue {

    private String AddedBy;
    private String Name;
    private String Date;
    private String Time;
    private String Venue;
    private String Image;
    private String Likes;

    public EventValue(String addedBy, String name, String date, String time, String venue, String image, String likes) {
        AddedBy = addedBy;
        Name = name;
        Date = date;
        Time = time;
        Venue = venue;
        Image = image;
        Likes = likes;
    }

    public EventValue() {
    }

    public String getAddedBy() {
        return AddedBy;
    }

    public void setAddedBy(String addedBy) {
        AddedBy = addedBy;
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

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getLikes() {
        return Likes;
    }

    public void setLikes(String likes) {
        Likes = likes;
    }
}
