package com.example.gracepark.popularmovies;

import android.content.Context;
import android.net.Uri;
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

    final static String BASE_URL =
            "http://api.themoviedb.org/3/movie/";
    final static String PICASSO_BASE = "http://image.tmdb.org/t/p/w185";

    public static String SORT_POPULAR = "popular";
    public static String SORT_TOP_RATED = "top_rated";

    final static String DELIM = "\\A";
    final static String PARAM_QUERY = "?api_key=";
    final static String API_KEY = "[ENTER_YOUR_API_KEY_HERE]";

    public static URL buildUrl(String sortBy) {
        Uri builtUri = Uri.parse(BASE_URL + sortBy + PARAM_QUERY + API_KEY);

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
        String url = PICASSO_BASE + photoPath;
        Picasso.with(context).load(url).into(target);
    }
}
