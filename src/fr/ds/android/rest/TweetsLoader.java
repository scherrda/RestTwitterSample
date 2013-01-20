package fr.ds.android.rest;


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class TweetsLoader extends AsyncTaskLoader<ArrayList<Tweet>> {

    private static final String TAG = TweetsLoader.class.getName();


    private HTTPVerb mVerb;
    private Uri mUri;
    private boolean mCancelled = false;
    private ArrayList<Tweet> mData = null;


    public enum HTTPVerb {
        GET {
            @Override
            HttpRequestBase getRequest(Uri uri) throws URISyntaxException {
                HttpRequestBase request = new HttpGet();

                request.setURI(new URI(uri.toString()));
                return request;
            }
        },
        POST {
            @Override
            HttpRequestBase getRequest(Uri uri) {
                return null;
            }
        },
        PUT {
            @Override
            HttpRequestBase getRequest(Uri uri) {
                return null;
            }
        },
        DELETE {
            @Override
            HttpRequestBase getRequest(Uri uri) {
                return null;
            }
        };


        abstract HttpRequestBase getRequest(Uri uri) throws URISyntaxException;
    }


    public TweetsLoader(Context context, HTTPVerb verb, String query) {
        super(context);
        this.mVerb = verb;
        this.mUri= buildUri(query);
    }

    private Uri buildUri(String criteria){
        Uri uri = new Uri.Builder().scheme("http")
                .authority("search.twitter.com")
                .path("/search.json")
                .appendQueryParameter("q", criteria)
                .build();

        return uri;
    }

    @Override
    public ArrayList<Tweet> loadInBackground() {
        try {
            HttpRequestBase  request = mVerb.getRequest(mUri);
            HttpClient client = new DefaultHttpClient();

            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine != null ? statusLine.getStatusCode() : 0;

            ArrayList<Tweet> tweets = null;
            if (entity != null) {
                tweets = getTweetsFromJson(EntityUtils.toString(entity));

            }

            return tweets;

        } catch (URISyntaxException e) {
            Log.e(TAG, "", e);
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }

        return null;
    }

    public ArrayList<String> parseTweets(String json){
        ArrayList<String> tweetList = new ArrayList<String>();

        try {
            JSONObject tweetWrapper = new JSONObject(json);
            JSONArray tweetsJson = tweetWrapper.getJSONArray("results");
            for (int i = 0; i < tweetsJson.length(); i++) {
                JSONObject tweetJson = tweetsJson.getJSONObject(i);
                String text = tweetJson.getString("text");

                tweetList.add(text);
            }
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        return tweetList;
    }
    private static ArrayList<Tweet> getTweetsFromJson(String json) {
        ArrayList<Tweet> tweetList = new ArrayList<Tweet>();

        try {
            JSONObject tweetsWrapper = (JSONObject) new JSONTokener(json).nextValue();
            JSONArray  tweets        = tweetsWrapper.getJSONArray("results");
            for (int i = 0; i < tweets.length(); i++) {
                JSONObject tweetJson = tweets.getJSONObject(i);
                String text = tweetJson.getString("text");
                String user = tweetJson.getString("from_user_name");
                String url = tweetJson.getString("profile_image_url_https");

                tweetList.add(new Tweet(text, user, url));
            }
        }
        catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON.", e);
        }

        return tweetList;
    }


    @Override
    protected void onStartLoading() {
        if(mData != null) {
            deliverResult(mData);
        }
        forceLoad();
    }

    // store data, reset boolean
    @Override
    public void deliverResult(ArrayList<Tweet> data) {
        mData = data;
        mCancelled = false;
        if( isStarted()) {
            super.deliverResult(mData);
        }
    }

    @Override
    public void onCanceled(ArrayList<Tweet> data) {
        super.onCanceled(data);
        // Attempt to cancel our asynctask
        cancelLoad();
        mCancelled = true;
        // Don't need any data
        mData = null;
    }

    //called when an Activity requests a loader to be reset
    @Override
    public void onReset() {
        super.onReset();
        mData = null;
        cancelLoad();
    }

    public static class TweetsResponse<T> {
        private final int code;
        private final ArrayList<T> tweets;

        public TweetsResponse(int code, ArrayList<T> tweets) {
            this.code = code;
            this.tweets = tweets;
        }

        public int getCode() {
            return code;
        }

        public ArrayList<T> getTweets() {
            return tweets;
        }
    }
}
