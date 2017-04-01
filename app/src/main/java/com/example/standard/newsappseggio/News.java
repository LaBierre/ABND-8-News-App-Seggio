package com.example.standard.newsappseggio;

/**
 * Created by vince on 31.03.2017.
 */

public class News {

    //News news = new News(Topic, title, webUrl, authorsName, webPublicationDate)
    private String mTopic, mTitle, mWebUrl, mAuthorsName, mWebPublicationDate;

    public News(String mTopic, String mTitle, String mWebUrl, String mAuthorsName, String mWebPublicationDate) {
        this.mTopic = mTopic;
        this.mTitle = mTitle;
        this.mWebUrl = mWebUrl;
        this.mAuthorsName = mAuthorsName;
        this.mWebPublicationDate = mWebPublicationDate;
    }

    public String getmTopic() {
        return mTopic;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmWebUrl() {
        return mWebUrl;
    }

    public String getmAuthorsName() {
        return mAuthorsName;
    }

    public String getmWebPublicationDate() {
        return mWebPublicationDate;
    }
}
