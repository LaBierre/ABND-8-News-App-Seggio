package com.example.standard.newsappseggio;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vince on 31.03.2017.
 */

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getName();

    public QueryUtils (){

    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl (Context context, String stringUrl){
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, context.getResources().getString(R.string.malformed_exeption), e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest (Context context, URL url) throws IOException {

        String jsonResponse = "";

        //If the URL is null, then return earlier
        if (url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod(context.getResources().getString(R.string.get_http));
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(context, inputStream);
            } else {
                Log.e(LOG_TAG, context.getResources().getString(R.string.error_resp_code) + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, context.getResources().getString(R.string.io_exeption), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(Context context, InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName(context.getResources().getString(R.string.utf_8)));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static List<News> fetchNewsData (Context context, String requestUrl) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Create URL object
        URL url = createUrl(context, requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(context, url);
        } catch (IOException e) {
            Log.e(LOG_TAG, context.getResources().getString(R.string.io_exeption_http), e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link New}s
        List<News> news = extractFeatureFromJson(context, jsonResponse);

        // Return the list of {@link New}s
        return news;
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(Context context, String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<News> newsList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of features (or news).
            JSONObject newsObject = baseJsonResponse.getJSONObject(context.getString(R.string.response_utils));

            JSONArray resultArray = newsObject.getJSONArray(context.getString(R.string.results_utils));

            // For each news in the resultArray, create an {@link News} object
            for (int i = 0; i < resultArray.length(); i++) {
                // Get a single news at position i within the list of news
                JSONObject currentNews = resultArray.getJSONObject(i);

                String topic;
                if (currentNews.has(context.getString(R.string.section_utils))) {
                    topic = currentNews.getString(context.getString(R.string.section_utils));
                } else {
                    topic = "";
                }

                //Extract the title from the key "webTitle" from the JSONObject "currentNews"
                String title;
                if (currentNews.has(context.getString(R.string.webtitle_utils))) {
                    title = currentNews.getString(context.getString(R.string.webtitle_utils));
                } else {
                    title = "";
                }

                //Extract the web link from the key "webUrl"
                String webUrl;
                if (currentNews.has(context.getString(R.string.weburl_utils))) {
                    webUrl = currentNews.getString(context.getString(R.string.weburl_utils));
                } else {
                    webUrl = "";
                }

                //Extract the published date from the key "webPublicationDate"
                String webPublicationDate;
                if (currentNews.has(context.getString(R.string.published_utils))) {
                    webPublicationDate = currentNews.getString(context.getString(R.string.published_utils));
                } else {
                    webPublicationDate = "";
                }

                JSONArray tagsArray = currentNews.getJSONArray(context.getString(R.string.tags_utils));

                String authorsName = "";

                for (int j = 0; j < tagsArray.length(); j++) {

                    JSONObject currentTag = tagsArray.getJSONObject(j);

                    //Extract the String with name of authors from key "webtitle"
                    if (currentTag.has(context.getString(R.string.webtitle_utils))) {
                        authorsName = currentTag.getString(context.getString(R.string.webtitle_utils));
                    } else {
                        authorsName = "";
                    }
                }

                News news = new News(topic, title, webUrl, authorsName, webPublicationDate);
                newsList.add(news);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, context.getResources().getString(R.string.io_exepion_three), e);
        }
        // Return the list of news
        return newsList;
    }
}
