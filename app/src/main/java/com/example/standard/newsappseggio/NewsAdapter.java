package com.example.standard.newsappseggio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vince on 31.03.2017.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    ViewHolderItem viewHolder;

    public NewsAdapter(Context context, ArrayList<News> objects) {
        super(context, 0, objects);
    }

    private static class ViewHolderItem {
        TextView topic, title, webUrl, authors, publishedDate;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);

            viewHolder = new ViewHolderItem();

            viewHolder.topic = (TextView) convertView.findViewById(R.id.topic_item);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title_item);
            viewHolder.webUrl = (TextView) convertView.findViewById(R.id.web_url_item);
            viewHolder.authors = (TextView) convertView.findViewById(R.id.authors_item);
            viewHolder.publishedDate = (TextView) convertView.findViewById(R.id.published_item);

            // store the holder with the view
            convertView.setTag(viewHolder);
        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        // Get the {@link Word} object located at this position in the list
        News currentNews = getItem(position);

        //Fit in the values in the items
        viewHolder.topic.setText(currentNews.getmTopic());
        viewHolder.title.setText(currentNews.getmTitle());
        viewHolder.webUrl.setText(currentNews.getmWebUrl());
        viewHolder.authors.setText(currentNews.getmAuthorsName());
        viewHolder.publishedDate.setText(currentNews.getmWebPublicationDate());

        return convertView;
    }
}

