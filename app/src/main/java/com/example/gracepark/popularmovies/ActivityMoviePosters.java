package com.example.gracepark.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Main activity for popular movie browsing.
 */
public class ActivityMoviePosters extends AppCompatActivity {

    MovieAdapter mAdapter;
    private JSONArray mMovieData;

    private int mSortByState = R.id.sort_by_popularity;
    private TextView mSortByStateDisplay;
    private TextView mErrorMessage;
    private GridView mPosterGrid;

    private int mErrorMessageString = 0;

    public static String EXTRA_MOVIE = "movie_object";

    public static int mHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_posters);

        // Determine screen height to use to size images in the adapter.
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mHeight = metrics.heightPixels;

        mPosterGrid = (GridView) findViewById(R.id.movie_grid);
        mErrorMessage = (TextView) findViewById(R.id.error_message);

        mSortByStateDisplay = ((TextView) findViewById(R.id.sort_by_status));
        mSortByStateDisplay.setText(getString(R.string.sorted_by_popularity));

        fetchPosters(NetworkUtils.SORT_POPULAR);

        mAdapter = new MovieAdapter(ActivityMoviePosters.this, new ArrayList<String>());
        mPosterGrid.setAdapter(mAdapter);

        mPosterGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ActivityMoviePosters.this, ActivityMovieDetails.class);
                try {
                    JSONObject item = mMovieData.getJSONObject(position);
                    ParcelableMovie movie = new ParcelableMovie(item);
                    intent.putExtra(EXTRA_MOVIE, movie);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
    }


    private void showError(int message) {
        mErrorMessage.setText(getString(message));
        mErrorMessage.setVisibility(VISIBLE);
        mSortByStateDisplay.setVisibility(GONE);
        mPosterGrid.setVisibility(GONE);
    }

    private void hideError() {
        mErrorMessage.setVisibility(GONE);
        mSortByStateDisplay.setVisibility(VISIBLE);
        mPosterGrid.setVisibility(VISIBLE);
    }

    public void fetchPosters(String sortBy) {
        URL moviesUrl = NetworkUtils.buildUrl(sortBy);
        new MovieRequestTask().execute(moviesUrl);
    }

    public class MovieRequestTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... params) {
            String response = null;
            try {
                response = NetworkUtils.getResponseFromHttpUrl(params[0]);
            } catch (FileNotFoundException e) {
                mErrorMessageString = R.string.file_not_found;
                e.printStackTrace();
            } catch (IOException e) {
                mErrorMessageString = R.string.generic_error;
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (mErrorMessageString != 0) {
                showError(mErrorMessageString);
            } else if (s != null && !s.equalsIgnoreCase("")) {
                hideError();
                try {
                    mAdapter.setData(parseData(s));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private List<String> parseData(String response) throws JSONException {
        JSONObject responseJSON = new JSONObject(response);
        mMovieData = responseJSON.getJSONArray("results");
        List<String> movieIdList = new ArrayList();
        for (int i = 0; i < mMovieData.length(); i++) {
            movieIdList.add(mMovieData.getJSONObject(i).get("poster_path").toString());
        }
        return movieIdList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort_by_popularity) {
            if (mSortByState != R.id.sort_by_popularity) {
                fetchPosters(NetworkUtils.SORT_POPULAR);
                mSortByState = R.id.sort_by_popularity;
                mSortByStateDisplay.setText(getString(R.string.sorted_by_popularity));
            }
            return true;
        } else if (item.getItemId() == R.id.sort_by_rating) {
            if (mSortByState != R.id.sort_by_rating) {
                fetchPosters(NetworkUtils.SORT_TOP_RATED);
                mSortByState = R.id.sort_by_rating;
                mSortByStateDisplay.setText(getString(R.string.sorted_by_rating));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
