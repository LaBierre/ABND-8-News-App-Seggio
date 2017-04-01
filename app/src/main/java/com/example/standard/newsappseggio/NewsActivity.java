package com.example.standard.newsappseggio;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final String NEWS_REQUEST_URL = "http://content.guardianapis.com/search?order-by=newest&use-date=last-modified&show-tags=contributor&api-key=test";

    private static final String NAME_PREFS = "MyPrefs" ;
    private static final String ITEM = "item";
    private static final String SECTION = "section";
    private static final String ADD_QUERY = "addQuery";

    private static final String INTENT_KEY = "add";

    SharedPreferences sharedprefs;

    private String mUrl, mSerchItem, mSection, mAddQuery;
    private NewsAdapter mAdapter;

    ListView newsListView;
    EditText serchItem, section;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        //Check if the device has internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        Boolean connectivity = networkInfo != null && networkInfo.isConnectedOrConnecting();
        if (!connectivity){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_web_available), Toast.LENGTH_LONG).show();
        }

        //prohibits that Edittexts fold on when App starts
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        newsListView = (ListView) findViewById(R.id.list);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        serchItem = (EditText) findViewById(R.id.item_edit);
        section = (EditText) findViewById(R.id.topic_edit);

        sharedprefs = getSharedPreferences(NAME_PREFS, Context.MODE_PRIVATE);

        //For display it in EditText with Shared Preferences
        serchItem.setText(sharedprefs.getString(ITEM, ""));
        section.setText(sharedprefs.getString(SECTION, ""));

        //Put in the values mSerchItem, mSection and mAddQuery with Shared Preferences
        mAddQuery = sharedprefs.getString(ADD_QUERY, "");
        mSerchItem = getString(R.string.qm_activity) + sharedprefs.getString(ITEM, "");
        mSection = getString(R.string.section_activity) + sharedprefs.getString(SECTION, "");
        mSection = mSection.toLowerCase();

        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        newsListView.setAdapter(mAdapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                News currentNews = mAdapter.getItem(position);

                if (!currentNews.getmWebUrl().equals("")){
                    // Convert the String URL into a URI object (to pass into the Intent constructor)
                    Uri bookUri = Uri.parse(currentNews.getmWebUrl());

                    // Create a new intent to view the news URI
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                    // Send the intent to launch a new activity
                    startActivity(websiteIntent);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_web_available), Toast.LENGTH_LONG).show();
                }
            }
        });

        //Assemble the Url
        if (mAddQuery != null){
            mAddQuery = mAddQuery.toLowerCase();
            mUrl = NEWS_REQUEST_URL + mAddQuery;
        } else {
            mUrl = NEWS_REQUEST_URL + mAddQuery;
        }

        LoaderManager loader = getLoaderManager();

        loader.initLoader(0, null, this);
    }

    public void submitOnClick (View v){

        SharedPreferences.Editor editor = sharedprefs.edit();

        String addQuery = "";

        //saving edit text content in shared preferences
        mSerchItem = serchItem.getText().toString();
        mSerchItem = mSerchItem.trim();
        editor.putString(ITEM, mSerchItem);
        mSection = section.getText().toString();
        mSection = mSection.trim();
        editor.putString(SECTION, mSection);

        //If both Text fields are empty the listview displays "No news available"
        if (mSerchItem.equals("") && mSection.equals("")){
            addQuery = getString(R.string.no_items_selected);
            Toast.makeText(this, R.string.no_items_toast, Toast.LENGTH_LONG).show();
        }
        //handles the case that only the search item edit text is filled
        if (mSection.equals("") && !mSerchItem.equals("")){
            mSerchItem = getString(R.string.qm_activity) + mSerchItem;
            addQuery = mSerchItem;
        }
        //handles the case that only the section edit text is filled
        if (mSerchItem.equals("") && !mSection.equals("")){
            mSection = getString(R.string.section_activity) + mSection;
            addQuery = mSection;
        }
        //handles the case that both edit text are filled
        if (!mSerchItem.equals("") && !mSection.equals("")){
            mSerchItem = getString(R.string.qm_activity) + mSerchItem;
            mSection = getString(R.string.section_activity) + mSection;
            addQuery = mSerchItem + mSection;
        }

        editor.putString(ADD_QUERY, addQuery);

        //save Shared Preferences
        editor.apply();

        //The Activity and hence the (new) search will be started new by clicking the button
        Intent i = new Intent(getApplicationContext(), NewsActivity.class);

        startActivity(i);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        return new NewsLoader(getApplicationContext(), mUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        progressBar.setVisibility(View.GONE);

        // Clear the adapter of previous book data
        mAdapter.clear();

        if (news != null && !news.isEmpty()){
            mAdapter.addAll(news);
        } else {
            newsListView = (ListView) findViewById(R.id.list);
            newsListView.setEmptyView(findViewById(R.id.empty_view));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Clear the adapter of previous book data
        mAdapter.clear();
    }
}
