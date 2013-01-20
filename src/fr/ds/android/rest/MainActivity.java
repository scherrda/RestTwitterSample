package fr.ds.android.rest;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import fr.ds.android.rest.rest.R;

import java.util.*;

public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<ArrayList<Tweet>> {
    private static final String ARGS_LOADER = "search_args_for_loader";
    public static final int LOADER_ID = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        List data = getOldLoadedTweets();
        setListAdapter(new ArrayTweetAdapter(this, data));


        //Search
        final Button searchButton = (Button)findViewById(R.id.searchBtn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText searchText = (EditText)findViewById(R.id.searchText);

                launchSearch(searchText.getText().toString());
            }
        }

        );

   }

    private List<Tweet> getOldLoadedTweets(){
        final Object data = getLastNonConfigurationInstance();
        if(data != null){
            return Arrays.asList((Tweet[])data);

        }
        return new ArrayList<Tweet>();
    }
    private void launchSearch(String criteria) {
        Bundle args = new Bundle();
        args.putString(ARGS_LOADER, criteria);

        getLoaderManager().initLoader(LOADER_ID, args, this);

    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        List<Tweet> tweets = ((ArrayTweetAdapter)getListAdapter()).getItems();
        Tweet[] tweetsArray = new Tweet[tweets.size()];
        for(int i = 0; i< tweets.size(); i++){
            tweetsArray[i] = tweets.get(i);
        }
        return tweetsArray;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        outState.putParcelable("listItems", tweets);

        super.onSaveInstanceState(outState);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Loader<ArrayList<Tweet>> onCreateLoader(int id, Bundle args) {
        return new TweetsLoader(this, TweetsLoader.HTTPVerb.GET, args.getString(ARGS_LOADER));
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Tweet>> loader, ArrayList<Tweet> tweets) {
        if (tweets!= null) {
            setTweets(tweets);

        } else {
            Toast.makeText(this, "Error retrieving tweets", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Tweet>> loader) {
        loader.reset();
    }

    private void setTweets(List<Tweet> tweets) {
        ArrayTweetAdapter adapter = (ArrayTweetAdapter)getListAdapter();
        adapter.clear();
        adapter.addAll(tweets);
        adapter.notifyDataSetChanged();

    }
}
