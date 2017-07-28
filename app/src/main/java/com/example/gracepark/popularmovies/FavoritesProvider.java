package com.example.gracepark.popularmovies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.gracepark.popularmovies.FavoritesContract.FavoritesEntry.TABLE_NAME;

/**
 * Provider responsible for handling favorited movie data.
 */

public class FavoritesProvider extends ContentProvider {
    private FavoritesDBHelper mOpenHelper;

    public static final int MOVIES = 100;
    public static final int MOVIE = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoritesContract.CONTENT_AUTHORITY, FavoritesContract.PATH_DETAIL, MOVIES);
        uriMatcher.addURI(FavoritesContract.CONTENT_AUTHORITY, FavoritesContract.PATH_DETAIL + "/#", MOVIE);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mOpenHelper = new FavoritesDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor retCursor;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE:
                String id = uri.getPathSegments().get(1);
                String mSelection = "id=?";
                String[] mSelectionArgs = new String[]{id};

                retCursor = db.query(TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(FavoritesContract.FavoritesEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int tasksDeleted;
        switch (match) {
            case MOVIE:
                final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                String id = uri.getPathSegments().get(1);
                tasksDeleted = db.delete(TABLE_NAME, "id=?", new String[]{id});

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return tasksDeleted;
    }
}


