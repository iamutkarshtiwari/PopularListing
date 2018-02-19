/*
 * Copyright 2018 Utkarsh Tiwari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iamutkarshtiwari.popularmovies.services;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iamutkarshtiwari.popularmovies.R;
import com.iamutkarshtiwari.popularmovies.activities.MovieDetailsActivity;
import com.iamutkarshtiwari.popularmovies.models.Review;
import com.iamutkarshtiwari.popularmovies.utils.JSONUtils;
import com.iamutkarshtiwari.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.List;

import static com.iamutkarshtiwari.popularmovies.models.Constants.REVIEWS_QUERY_LOADER_MOVIE_ID_EXTRA;

/**
 * Created by utkarshtiwari on 27.01.2018.
 */

public class ReviewsQueryLoader implements LoaderManager.LoaderCallbacks<List<Review>> {

    private static final String LOG_TAG = ReviewsQueryLoader.class.getSimpleName();

    private final Context context;
    private final LinearLayout reviewListLayout;
    private final TextView reviewsTitle;
    private final MovieDetailsActivity activity;

    public ReviewsQueryLoader(@NonNull Context context, MovieDetailsActivity activity) {
        this.context = context;
        reviewListLayout = activity.findViewById(R.id.review_list_layout);
        reviewsTitle = activity.findViewById(R.id.reviews_title);
        this.activity = activity;
    }

    @Override
    public Loader<List<Review>> onCreateLoader(int id, Bundle args) {
        return new ReviewsQueryTaskLoader(this.context, args);
    }

    @Override
    public void onLoadFinished(Loader<List<Review>> loader, List<Review> reviewList) {
        if (reviewList == null || reviewList.size() == 0) {
            reviewListLayout.setVisibility(View.GONE);
            reviewsTitle.setVisibility(View.GONE);
            Log.e(LOG_TAG, "review list is empty!");
        } else {
            for (Review review : reviewList) {
                reviewListLayout.addView(getReviewView(review));
            }
            activity.restoreViewState();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Review>> loader) {

    }

    private View getReviewView(final Review review) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.review_list_item, this.reviewListLayout, false);
        TextView contentTextView = view.findViewById(R.id.review_content_text);
        TextView authorTextView = view.findViewById(R.id.review_author_text);
        contentTextView.setText(review.getContent());
        authorTextView.setText(review.getAuthor());
        return view;
    }
}

class ReviewsQueryTaskLoader extends AsyncTaskLoader<List<Review>> {

    private static final String LOG_TAG = ReviewsQueryTaskLoader.class.getSimpleName();

    private final Bundle args;

    ReviewsQueryTaskLoader(@NonNull Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        if (this.args == null || !this.args.containsKey(REVIEWS_QUERY_LOADER_MOVIE_ID_EXTRA)) {
            return;
        }
        forceLoad();
    }

    @Nullable
    @Override
    public List<Review> loadInBackground() {
        long id = this.args.getLong(REVIEWS_QUERY_LOADER_MOVIE_ID_EXTRA);
        URL reviewsRequestUrl = NetworkUtils.buildReviewsUrl(id);
        try {
            String jsonReviewsResponse = NetworkUtils.getResponseFromHttpUrl(reviewsRequestUrl);
            return JSONUtils.parseReviewsJson(jsonReviewsResponse);
        } catch (Exception e) {
            Log.e(LOG_TAG, "id: " + id, e);
            return null;
        }
    }
}
