package com.example.gracepark.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gpark on 7/27/17.
 */

public class MovieReviewItem {
    private String mAuthor;
    private String mContent;

    public MovieReviewItem(JSONObject object) throws JSONException {
        mAuthor = object.get("author").toString();
        mContent = object.get("content").toString();
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }
}
