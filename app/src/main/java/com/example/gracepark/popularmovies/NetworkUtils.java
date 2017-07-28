package com.example.gracepark.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Helper functions to handle all network related work.
 */

public class NetworkUtils {

    private final static String BASE_URL =
            "http://api.themoviedb.org/3/movie/";
    private final static String PICASSO_BASE = "http://image.tmdb.org/t/p/w185";

    private final static String REVIEWS = "/reviews";
    private final static String TRAILERS = "/videos";

    public static String SORT_POPULAR = "popular";
    public static String SORT_TOP_RATED = "top_rated";

    private final static String DELIM = "\\A";
    private final static String PARAM_QUERY = "?api_key=";
    private final static String API_KEY = "5848353b0582ec0f5038d64343da0388";

    public static URL buildMoviesUrl(String sortBy) {
        Uri builtUri = Uri.parse(BASE_URL + sortBy + PARAM_QUERY + API_KEY);

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildMovieDetailsUrl(String movieId) {
        Uri builtUri = Uri.parse(BASE_URL + movieId + PARAM_QUERY + API_KEY);

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildMovieReviewsUrl(String movieId) {
        Uri builtUri = Uri.parse(BASE_URL + movieId + REVIEWS + PARAM_QUERY + API_KEY);

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildMovieTrailersUrl(String movieId) {
        Uri builtUri = Uri.parse(BASE_URL + movieId + TRAILERS + PARAM_QUERY + API_KEY);

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            // set the connection timeout to 5 seconds and the read timeout to 10 seconds
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(10000);

            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter(DELIM);

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static void loadImageWithPicasso(Context context, ImageView target, String photoPath) {
        Log.d("Picasso", "Loading " + photoPath);
        String url = PICASSO_BASE + photoPath;
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
                .into(target);
    }
}
