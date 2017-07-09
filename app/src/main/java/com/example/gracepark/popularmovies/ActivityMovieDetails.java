package com.example.gracepark.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This activity displays the details of a particular movie. Accessible from ActivityMoviePosters.
 */

public class ActivityMovieDetails extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Bundle movieIntent = getIntent().getExtras();
        ((TextView) findViewById(R.id.movie_title)).setText(movieIntent.getString(ActivityMoviePosters.EXTRA_TITLE));
        ((TextView) findViewById(R.id.plot)).setText(movieIntent.getString(ActivityMoviePosters.EXTRA_PLOT));
        ((TextView) findViewById(R.id.date)).setText(movieIntent.getString(ActivityMoviePosters.EXTRA_DATE));
        ((TextView) findViewById(R.id.rating)).setText(getString(R.string.rating_score, movieIntent.getString(ActivityMoviePosters.EXTRA_RATING)));

        ImageView thumbnail = ((ImageView) findViewById(R.id.movie_thumbnail));
        NetworkUtils.loadImageWithPicasso(ActivityMovieDetails.this, thumbnail, movieIntent.getString(ActivityMoviePosters.EXTRA_THUMBNAIL));

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        thumbnail.setMinimumHeight(metrics.heightPixels/2);
    }
}
