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
        ParcelableMovie movie = movieIntent.getParcelable(ActivityMoviePosters.EXTRA_MOVIE);

        ((TextView) findViewById(R.id.movie_title)).setText(movie.title);
        ((TextView) findViewById(R.id.plot)).setText(movie.overview);
        ((TextView) findViewById(R.id.date)).setText(movie.releaseDate);
        ((TextView) findViewById(R.id.rating)).setText(getString(R.string.rating_score, movie.voteAverage));

        ImageView thumbnail = ((ImageView) findViewById(R.id.movie_thumbnail));
        NetworkUtils.loadImageWithPicasso(ActivityMovieDetails.this, thumbnail, movieIntent.getString(movie.thumbnail));

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        thumbnail.setMinimumHeight(metrics.heightPixels/2);
    }
}
