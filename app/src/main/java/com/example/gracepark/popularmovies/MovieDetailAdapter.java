package com.example.gracepark.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import static android.support.v4.content.ContextCompat.startActivity;
import static com.example.gracepark.popularmovies.NetworkUtils.buildYoutubeUrl;

/**
 * Adapter to load and handle details of movies.
 */

class MovieDetailAdapter extends RecyclerView.Adapter {
    private LayoutInflater mLayoutInflater;
    private List<MovieReviewItem> reviewsList;
    private List<String> trailersNameList;
    private List<String> trailersKeyList;
    private Context context;
    private ParcelableMovie mMovie;

    private final int HEADER_COUNT = 1;

    private final int HEADER_VIEW_TYPE = 0;
    private final int REVIEW_VIEW_TYPE = 1;
    private final int TRAILER_VIEW_TYPE = 2;

    public MovieDetailAdapter(Context context, ParcelableMovie movie, List<MovieReviewItem> reviews, List<String> trailers) {
        this.context = context;
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reviewsList = reviews;
        trailersNameList = trailers;
        mMovie = movie;
    }

    public void setTrailersData(List<String> data, List<String> keys) {
        trailersNameList = data;
        trailersKeyList = keys;
    }

    public void setReviewsData(List<MovieReviewItem> data) {
        reviewsList = data;
    }


    @Override
    public int getItemCount() {
        return getTrailersCount() + getReviewsCount() + HEADER_COUNT;
    }

    public int getTrailersCount() {
        return trailersNameList.size();
    }

    public int getReviewsCount() {
        return reviewsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class MovieDetailViewHolder extends RecyclerView.ViewHolder{
        TextView mMovieTitle;
        TextView mPlot;
        TextView mDate;
        TextView mRating;
        ImageView mThumbnail;

        public MovieDetailViewHolder(View view) {
            super(view);
            mMovieTitle = (TextView) view.findViewById(R.id.movie_title);
            mPlot = (TextView) view.findViewById(R.id.plot);
            mDate = (TextView) view.findViewById(R.id.date);
            mRating  = (TextView) view.findViewById(R.id.rating);
            mThumbnail = (ImageView) view.findViewById(R.id.movie_thumbnail);
        }

        public void bind(Context context, ParcelableMovie movie) {
            mMovieTitle.setText(movie.title);
            mPlot.setText(movie.overview);
            mDate.setText(movie.releaseDate);
            mRating.setText(movie.voteAverage);
            NetworkUtils.loadImageWithPicasso(context, mThumbnail, movie.thumbnail);
        }
    }

    private static class ReviewViewHolder extends RecyclerView.ViewHolder{
        TextView mAuthor;
        TextView mContent;

        public ReviewViewHolder(View view) {
            super(view);
            mAuthor = (TextView) view.findViewById(R.id.author_name);
            mContent = (TextView) view.findViewById(R.id.review_text);
        }

        public void bind(MovieReviewItem reviewItem) {
            mAuthor.setText(reviewItem.getAuthor());
            mContent.setText(reviewItem.getContent());
        }
    }

    private static class TrailerViewHolder extends RecyclerView.ViewHolder{
        TextView mTrailerTitle;
        LinearLayout mPanel;

        public TrailerViewHolder(View view) {
            super(view);
            mTrailerTitle = (TextView) view.findViewById(R.id.trailer_title);
            mPanel = (LinearLayout) view.findViewById(R.id.trailer_panel);
        }

        public void bind(String title, final Context context, final String key) {
            mTrailerTitle.setText(title);
            mPanel.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key));
                            startActivity(context, intent, null);
                        }
                    }
            );

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(viewType) {
            case HEADER_VIEW_TYPE:
                return new MovieDetailViewHolder(mLayoutInflater.inflate(R.layout.activity_movie_detail_header, parent, false));
            case REVIEW_VIEW_TYPE:
                return new ReviewViewHolder(mLayoutInflater.inflate(R.layout.panel_review, parent, false));
            case TRAILER_VIEW_TYPE:
                return new TrailerViewHolder(mLayoutInflater.inflate(R.layout.panel_trailer, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case HEADER_VIEW_TYPE:
                MovieDetailViewHolder detailsViewHolder = (MovieDetailViewHolder) holder;
                detailsViewHolder.bind(context, mMovie);
                break;
            case REVIEW_VIEW_TYPE:
                ReviewViewHolder reviewViewHolder = (ReviewViewHolder) holder;
                reviewViewHolder.bind(reviewsList.get(position - HEADER_COUNT - getTrailersCount()));
                break;
            case TRAILER_VIEW_TYPE:
                TrailerViewHolder trailerViewHolder = (TrailerViewHolder) holder;
                trailerViewHolder.bind(trailersNameList.get(position - HEADER_COUNT), context, trailersKeyList.get(position - HEADER_COUNT));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_VIEW_TYPE;
        } else if (position < getTrailersCount() + HEADER_COUNT) {
            return TRAILER_VIEW_TYPE;
        } else {
            return REVIEW_VIEW_TYPE;
        }
    }
}
