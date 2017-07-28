package com.example.gracepark.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * This activity displays the details of a particular movie. Accessible from ActivityMoviePosters.
 */

public class ActivityMovieDetails extends AppCompatActivity {

    MovieDetailAdapter mAdapter;
    private JSONArray mMovieReviewsData;
    private JSONArray mMovieTrailersData;
    private int mErrorMessageString;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_recyclerview);

        Bundle movieIntent = getIntent().getExtras();
        final ParcelableMovie movie = movieIntent.getParcelable(ActivityMoviePosters.EXTRA_MOVIE);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mAdapter = new MovieDetailAdapter(ActivityMovieDetails.this, movie, new ArrayList<MovieReviewItem>(), new ArrayList<String>());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchMovieData(movie.id);

        findViewById(R.id.favorite_floating_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Uri uri = FavoritesContract.FavoritesEntry.CONTENT_URI.buildUpon().appendPath(movie.id).build();
                        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                        if (cursor.getCount() != 0) {
                            getContentResolver().delete(uri, null, null);
                            Toast.makeText(ActivityMovieDetails.this, getString(R.string.marked_as_not_fav), Toast.LENGTH_SHORT).show();
                        } else {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_ID, movie.id);
                            contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_POSTER, movie.thumbnail);
                            getContentResolver().insert(FavoritesContract.FavoritesEntry.CONTENT_URI, contentValues);
                            Toast.makeText(ActivityMovieDetails.this, getString(R.string.marked_as_fav), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void fetchMovieData(String movieId) {
        new MovieReviewsRequestTask().execute(NetworkUtils.buildMovieReviewsUrl(movieId));
        new MovieTrailersRequestTask().execute(NetworkUtils.buildMovieTrailersUrl(movieId));
    }

    private class MovieReviewsRequestTask extends AsyncTask<URL, Void, String> {
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
                Toast.makeText(getApplicationContext(), mErrorMessageString, LENGTH_SHORT).show();
            } else if (s != null && !s.equalsIgnoreCase("")) {
                try {
                    mAdapter.setReviewsData(parseReviewData(s));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class MovieTrailersRequestTask extends AsyncTask<URL, Void, String> {
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
                Toast.makeText(getApplicationContext(), mErrorMessageString, LENGTH_SHORT).show();
            } else if (s != null && !s.equalsIgnoreCase("")) {
                try {
                    mAdapter.setTrailersData(parseTrailerData(s));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private List<MovieReviewItem> parseReviewData(String response) throws JSONException {
        JSONObject responseJSON = new JSONObject(response);
        mMovieReviewsData = responseJSON.getJSONArray("results");
        List<MovieReviewItem> movieReviewList = new ArrayList();
        for (int i = 0; i < mMovieReviewsData.length(); i++) {
            movieReviewList.add(new MovieReviewItem(mMovieReviewsData.getJSONObject(i)));
        }
        return movieReviewList;
    }

    private List<String> parseTrailerData(String response) throws JSONException {
        JSONObject responseJSON = new JSONObject(response);
        mMovieTrailersData = responseJSON.getJSONArray("results");
        List<String> movieTrailerList = new ArrayList();
        for (int i = 0; i < mMovieTrailersData.length(); i++) {
            movieTrailerList.add(mMovieTrailersData.getJSONObject(i).get("name").toString());
        }
        return movieTrailerList;
    }
}
