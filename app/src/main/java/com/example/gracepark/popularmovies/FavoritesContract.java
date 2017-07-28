package com.example.gracepark.popularmovies;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract defining database details for the favorited movies.
 */

public class FavoritesContract {

        public static final String CONTENT_AUTHORITY = "com.example.gracepark.popularmovies";

        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        public static final String PATH_DETAIL = "favorites";

        public static final class FavoritesEntry implements BaseColumns {

            public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_DETAIL)
                    .build();

            public static final String TABLE_NAME = "favorites";

            public static final String COLUMN_ID = "id";
            public static final String COLUMN_POSTER = "poster_path";
    }
}
