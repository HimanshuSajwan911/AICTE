package com.himanshu.aicte.common.news;


import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

public class News {

    private String headline, body, author;
    private Timestamp timestamp;

    public News() {
    }

    public News(String headline, String body, String author, Timestamp timestamp) {
        this.headline = headline;
        this.body = body;
        this.author = author;
        this.timestamp = timestamp;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return "News{" +
                "headline='" + headline + '\'' +
                ", author='" + author + '\'' +
                ", timestamp=" + timestamp +
                ", body='" + body + '\'' +
                '}';
    }
}
