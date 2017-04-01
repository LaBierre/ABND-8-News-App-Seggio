package com.example.standard.newsappseggio;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by vince on 31.03.2017.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String mUrl;

    public NewsLoader(Context context, String mUrl) {
        super(context);
        this.mUrl = mUrl;
    }

    @Override
    protected void onStartLoading() {

        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {

        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of news.
        List<News> news = QueryUtils.fetchNewsData(getContext(), mUrl);

        return news;
    }

}
