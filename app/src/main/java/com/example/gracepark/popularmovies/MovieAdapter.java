package com.example.gracepark.popularmovies;

import android.content.Context;
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
    private Context context;

    public MovieAdapter(Context context, List<String> customizedListView) {
        this.context = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
    }

    public void setData(List<String> data) {
        listStorage = data;
    }

    @Override
    public int getCount() {
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

            convertView.findViewById(R.id.poster_image).setMinimumHeight(ActivityMoviePosters.mHeight/2);

            listViewHolder.posterView = (ImageView)convertView.findViewById(R.id.poster_image);
            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder)convertView.getTag();
        }

        NetworkUtils.loadImageWithPicasso(context, listViewHolder.posterView, listStorage.get(position));

        return convertView;
    }

    private static class ViewHolder{
        ImageView posterView;
    }
}
