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
import android.content.Intent;
import android.net.Uri;
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
import com.iamutkarshtiwari.popularmovies.models.Trailer;
import com.iamutkarshtiwari.popularmovies.utils.JSONUtils;
import com.iamutkarshtiwari.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.List;

import static com.iamutkarshtiwari.popularmovies.models.Constants.TRAILERS_QUERY_LOADER_MOVIE_ID_EXTRA;

/**
 * Created by utkarshtiwari on 28.01.2018.
 */

public class TrailersQueryLoader implements LoaderManager.LoaderCallbacks<List<Trailer>> {

    private static final String LOG_TAG = TrailersQueryLoader.class.getSimpleName();

    private static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    private final LinearLayout trailerListLayout;
    private final TextView trailersTitle;
    private final Context context;
    private final MovieDetailsActivity activity;

    public TrailersQueryLoader(@NonNull Context context, MovieDetailsActivity activity) {
        this.context = context;
        trailerListLayout = activity.findViewById(R.id.trailer_list_layout);
        trailersTitle = activity.findViewById(R.id.trailers_title);
        this.activity = activity;
    }

    @Override
    public Loader<List<Trailer>> onCreateLoader(int id, Bundle args) {
        return new TrailersQueryTaskLoader(this.context, args);
    }

    @Override
    public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> trailerList) {
        if (trailerList == null || trailerList.size() == 0) {
            trailerListLayout.setVisibility(View.GONE);
            trailersTitle.setVisibility(View.GONE);
            Log.e(LOG_TAG, "trailer list is empty!");
        } else {
            for (Trailer trailer : trailerList) {
                trailerListLayout.addView(getTrailerView(trailer));
            }
            activity.restoreViewState();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Trailer>> loader) {

    }

    private View getTrailerView(final Trailer trailer) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.trailer_list_item, trailerListLayout, false);
        TextView trailerNameTextView = view.findViewById(R.id.trailer_name);
        trailerNameTextView.setText(trailer.getName());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(YOUTUBE_URL + trailer.getKey()));
                context.startActivity(intent);
            }
        });
        return view;
    }
}

class TrailersQueryTaskLoader extends AsyncTaskLoader<List<Trailer>> {

    private static final String LOG_TAG = TrailersQueryTaskLoader.class.getSimpleName();

    private final Bundle args;

    TrailersQueryTaskLoader(@NonNull Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        if (this.args == null || !this.args.containsKey(TRAILERS_QUERY_LOADER_MOVIE_ID_EXTRA)) {
            return;
        }
        forceLoad();
    }

    @Nullable
    @Override
    public List<Trailer> loadInBackground() {
        long id = this.args.getLong(TRAILERS_QUERY_LOADER_MOVIE_ID_EXTRA);
        URL trailersRequestUrl = NetworkUtils.buildTrailersUrl(id);
        try {
            String jsonTrailersResponse = NetworkUtils.getResponseFromHttpUrl(trailersRequestUrl);
            return JSONUtils.parseTrailersJson(jsonTrailersResponse);
        } catch (Exception e) {
            Log.e(LOG_TAG, "id: " + id, e);
            return null;
        }
    }
}
