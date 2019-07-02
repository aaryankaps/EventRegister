package com.example.na00556573.eventregister;

import java.util.Date;

public class postComments {
    private String commentText;
    private String commentUser;
    private long commentTime;

    public postComments() {
    }

    public postComments(String commentText, String commentUser) {
        this.commentText = commentText;
        this.commentUser = commentUser;
        this.commentTime = new Date().getTime();
    }

    public postComments(String commentText, String commentUser, long commentTime) {
        this.commentText = commentText;
        this.commentUser = commentUser;
        this.commentTime = commentTime;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(String commentUser) {
        this.commentUser = commentUser;
    }

    public long getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(long commentTime) {
        this.commentTime = commentTime;
    }
}
