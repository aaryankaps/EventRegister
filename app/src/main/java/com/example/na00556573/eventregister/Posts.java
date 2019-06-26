package com.example.na00556573.eventregister;

import java.util.Date;

public class Posts {
    private String postText;
    private String postUser;
    private long postTime;
    private String postLike;

    public Posts() {
    }

    public Posts(String postText, String postUser, String postLike) {
        this.postText = postText;
        this.postUser = postUser;
        this.postLike = postLike;
        this.postTime = new Date().getTime();

    }

    public Posts(String postText, String postUser, long postTime, String postLike) {
        this.postText = postText;
        this.postUser = postUser;
        this.postTime = postTime;
        this.postLike = postLike;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostUser() {
        return postUser;
    }

    public void setPostUser(String postUser) {
        this.postUser = postUser;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }

    public String getPostLike() {
        return postLike;
    }

    public void setPostLike(String postLike) {
        this.postLike = postLike;
    }
}
