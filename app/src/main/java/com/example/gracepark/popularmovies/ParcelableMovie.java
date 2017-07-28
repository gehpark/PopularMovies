package com.example.gracepark.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gracepark on 7/12/17.
 */

public class ParcelableMovie implements Parcelable {

    public String title;
    public String thumbnail;
    public String overview;
    public String releaseDate;
    public String voteAverage;
    public String id;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.title,
                this.thumbnail,
                this.overview,
                this.releaseDate,
                this.voteAverage,
                this.id});
    }

    public ParcelableMovie(JSONObject item) throws JSONException {
        title = item.get("title").toString();
        thumbnail = item.get("poster_path").toString();
        overview = item.get("overview").toString();
        releaseDate = item.get("release_date").toString();
        voteAverage = item.get("vote_average").toString();
        id = item.get("id").toString();
    }

    protected ParcelableMovie(Parcel in) {
        String[] data = new String[6];

        in.readStringArray(data);
        this.title = data[0];
        this.thumbnail = data[1];
        this.overview = data[2];
        this.releaseDate = data[3];
        this.voteAverage = data[4];
        this.id = data[5];
    }

    public static final Creator<ParcelableMovie> CREATOR = new Creator<ParcelableMovie>() {
        @Override
        public ParcelableMovie createFromParcel(Parcel source) {
            return new ParcelableMovie(source);
        }

        @Override
        public ParcelableMovie[] newArray(int size) {
            return new ParcelableMovie[size];
        }
    };
}
