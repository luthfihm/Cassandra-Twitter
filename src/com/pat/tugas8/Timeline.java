package com.pat.tugas8;

/**
 * Created by luthfi on 11/12/2015.
 */
public class Timeline {
    private String username;
    private String time;
    private Tweet tweet;

    public Timeline() {
        this.username = null;
        this.time = null;
        this.tweet = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Tweet getTweet() {
        return tweet;
    }

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }
}
