package com.example.gracepark.popularmovies;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
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
public class ActivityMoviePosters extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    MovieAdapter mAdapter;
    private JSONArray mMovieData;

    private final int LOADER_ID = 0;

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
                if (mSortByState == R.id.show_favorites) {
                    URL movieDetailsUrl = NetworkUtils.buildMovieDetailsUrl(mAdapter.getIdUsingCursor(position));
                    new MovieDetailsRequestTask().execute(movieDetailsUrl);

                } else {
                    try {
                        JSONObject item = mMovieData.getJSONObject(position);
                        ParcelableMovie movie = new ParcelableMovie(item);
                        intent.putExtra(EXTRA_MOVIE, movie);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                }
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
        URL moviesUrl = NetworkUtils.buildMoviesUrl(sortBy);
        new MovieRequestTask().execute(moviesUrl);
    }

    private class MovieRequestTask extends AsyncTask<URL, Void, String> {
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

    private class MovieDetailsRequestTask extends AsyncTask<URL, Void, String> {
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
                    Intent intent = new Intent(getApplicationContext(), ActivityMovieDetails.class);
                    intent.putExtra(EXTRA_MOVIE, parseDetailsData(s));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ParcelableMovie parseDetailsData(String response) throws JSONException {
        JSONObject data = new JSONObject(response);
        ParcelableMovie movie = new ParcelableMovie(data);
        return movie;
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
        } else if (item.getItemId() == R.id.show_favorites) {
            mAdapter.setData(new ArrayList<String>());
            mAdapter.notifyDataSetChanged();
            mSortByStateDisplay.setText(getString(R.string.showing_favorites));
            final Uri uri = FavoritesContract.FavoritesEntry.CONTENT_URI;
            try {
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                mAdapter.setCursor(cursor);
                mAdapter.notifyDataSetChanged();
            } catch (Exception e){
                Log.d(e.getLocalizedMessage(), "yaaho");
            }
            mSortByState = R.id.show_favorites;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mData = null;

            @Override
            protected void onStartLoading() {
                if (mData != null) {
                    deliverResult(mData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            FavoritesContract.FavoritesEntry.COLUMN_ID);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mData = data;
                super.deliverResult(data);
            }
        };

    }


    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mAdapter.swapCursor(data);
    }


    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}

