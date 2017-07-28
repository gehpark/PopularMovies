package com.example.gracepark.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper to create and define the database for favorite movies.
 */

public class FavoritesDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public FavoritesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + FavoritesContract.FavoritesEntry.TABLE_NAME + " (" +

                        FavoritesContract.FavoritesEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        FavoritesContract.FavoritesEntry.COLUMN_ID       + " INTEGER NOT NULL, "                 +

                        FavoritesContract.FavoritesEntry.COLUMN_POSTER + " INTEGER NOT NULL)";

        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritesContract.FavoritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
