package com.iamutkarshtiwari.popularmovies.services;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.iamutkarshtiwari.popularmovies.adapters.MoviePostersAdapter;
import com.iamutkarshtiwari.popularmovies.models.Movie;
import com.iamutkarshtiwari.popularmovies.utils.JSONUtils;
import com.iamutkarshtiwari.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.List;

import static com.iamutkarshtiwari.popularmovies.models.Constants.MOVIES_QUERY_LOADER_FILTER_TYPE_EXTRA;

/**
 * Created by utkarshtiwari on 22.12.2017.
 */

public class MoviesQueryLoader implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String LOG_TAG = MoviesQueryLoader.class.getSimpleName();

    private final MoviePostersAdapter moviePostersAdapter;
    private final TextView emptyMessageView;
    private final Context context;

    public MoviesQueryLoader(@NonNull Context context, MoviePostersAdapter moviePostersAdapter, TextView emptyMessageView) {
        this.moviePostersAdapter = moviePostersAdapter;
        this.context = context;
        this.emptyMessageView = emptyMessageView;
        emptyMessageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new MoviesQueryTaskLoader(this.context, args);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movieList) {
        if (movieList == null) {
            emptyMessageView.setVisibility(View.VISIBLE);
            Log.e(LOG_TAG, "movie list is empty!");
        } else {
            emptyMessageView.setVisibility(View.INVISIBLE);
            moviePostersAdapter.setMovieList(movieList);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }

}

class MoviesQueryTaskLoader extends AsyncTaskLoader<List<Movie>> {

    private static final String LOG_TAG = MoviesQueryTaskLoader.class.getSimpleName();

    private final Bundle args;

    MoviesQueryTaskLoader(@NonNull Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        if (this.args == null || !this.args.containsKey(MOVIES_QUERY_LOADER_FILTER_TYPE_EXTRA)) {
            return;
        }
        forceLoad();
    }

    @Nullable
    @Override
    public List<Movie> loadInBackground() {
        String filterType = this.args.getString(MOVIES_QUERY_LOADER_FILTER_TYPE_EXTRA);
        URL moviesRequestUrl = NetworkUtils.buildUrl(filterType);
        try {
            String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);
            return JSONUtils.parseMoviesJson(jsonMoviesResponse);
        } catch (Exception e) {
            Log.e(LOG_TAG, "filter type: " + filterType, e);
            return null;
        }
    }
}
