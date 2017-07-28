package com.example.gracepark.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * This adapter loads movie posters into a gridview for ActivityMoviePosters
 */

public class MovieAdapter extends BaseAdapter {

    private LayoutInflater layoutinflater;
    private List<String> listStorage;
    private Context mContext;
    private Cursor mCursor;

    public MovieAdapter(Context context, List<String> customizedListView) {
        this.mContext = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
    }

    public void setData(List<String> data) {
        listStorage = data;
    }

    @Override
    public int getCount() {
        if (mCursor != null && mCursor.getCount() != 0 && listStorage.isEmpty()) {
            return mCursor.getCount();
        }
        return listStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder listViewHolder;
        if(convertView == null){
            listViewHolder = new ViewHolder();
            convertView = layoutinflater.inflate(R.layout.panel_movie_poster, parent, false);

            listViewHolder.posterView = (ImageView)convertView.findViewById(R.id.poster_image);
            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder)convertView.getTag();
        }

        String posterPath = "";
        if (listStorage.isEmpty()) {
            int posterIndex = mCursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_POSTER);
            mCursor.moveToPosition(position);
            posterPath = mCursor.getString(posterIndex);
        } else {
            posterPath = listStorage.get(position);
        }
        NetworkUtils.loadImageWithPicasso(mContext, listViewHolder.posterView, posterPath);

        return convertView;
    }

    private static class ViewHolder{
        ImageView posterView;
    }

    public void setCursor(Cursor cursor) {
        mCursor = cursor;
    }

    public String getIdUsingCursor(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getString(mCursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_ID));
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }

        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

}
