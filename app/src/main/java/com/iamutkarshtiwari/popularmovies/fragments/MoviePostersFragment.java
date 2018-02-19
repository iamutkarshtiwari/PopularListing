package com.iamutkarshtiwari.popularmovies.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iamutkarshtiwari.popularmovies.R;
import com.iamutkarshtiwari.popularmovies.adapters.FavoriteMoviePostersAdapter;
import com.iamutkarshtiwari.popularmovies.adapters.MoviePostersAdapter;
import com.iamutkarshtiwari.popularmovies.models.Movie;
import com.iamutkarshtiwari.popularmovies.services.FavoriteMoviesCursorLoader;
import com.iamutkarshtiwari.popularmovies.services.MoviesQueryLoader;

import java.util.List;

import static com.iamutkarshtiwari.popularmovies.models.Constants.MOVIES_QUERY_LOADER_FILTER_TYPE_EXTRA;

/**
 * A fragment containing the list view of Android versions.
 */
public class MoviePostersFragment extends Fragment {

    private MoviePostersAdapter moviePostersAdapter;
    private FavoriteMoviePostersAdapter favoriteMoviePostersAdapter;

    private RecyclerView recyclerView;

    private static final int MOVIES_QUERY_LOADER_ID = 11121990;
    public static final int FAVORITE_MOVIES_LOADER_ID = 12121994;

    private MoviesQueryLoader moviesQueryLoader;
    private FavoriteMoviesCursorLoader favoriteMoviesCursorLoader;

    private static final String FILTER_TYPE_POPULAR = "popular";
    private static final String FILTER_TYPE_TOP_RATED = "top_rated";

    private static final String SELECTED_ACTION_ID_KEY = "selected_action_id";
    private static final String SAVED_LAYOUT_MANAGER_KEY = "saved_layout_manager";
    private static int selectedActionId = R.id.action_popular;

    private Bundle savedInstanceState;

    public MoviePostersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SELECTED_ACTION_ID_KEY, selectedActionId);
        outState.putParcelable(SAVED_LAYOUT_MANAGER_KEY, recyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_posters, container, false);
        applyConfiguration(rootView);
        this.savedInstanceState = savedInstanceState;
        if (savedInstanceState == null) {
            applyPopularConfiguration();
        } else {
            applyNewConfiguration(savedInstanceState.getInt(SELECTED_ACTION_ID_KEY, R.id.action_popular));
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return applyNewConfiguration(item.getItemId()) || super.onOptionsItemSelected(item);
    }

    private boolean applyNewConfiguration(int id) {
        switch (id) {
            case R.id.action_popular:
                applyPopularConfiguration();
                return true;
            case R.id.action_top_rated:
                applyTopRatedConfiguration();
                return true;
            case R.id.action_favorites:
                applyFavoritesConfiguration();
                return true;
        }
        return false;
    }

    private void applyConfiguration(View rootView) {
        Context context = getContext();
        FragmentActivity activity = getActivity();
        if (activity == null || context == null) {
            return;
        }
        TextView emptyMessageView = rootView.findViewById(R.id.empty_message_view);
        recyclerView = rootView.findViewById(R.id.movie_posters_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(context, getGridLayoutSpan()));
        recyclerView.setHasFixedSize(true);
        moviePostersAdapter = new MoviePostersAdapter(this);
        moviesQueryLoader = new MoviesQueryLoader(context, moviePostersAdapter, emptyMessageView);
        favoriteMoviePostersAdapter = new FavoriteMoviePostersAdapter(this);
        favoriteMoviesCursorLoader = new FavoriteMoviesCursorLoader(context, favoriteMoviePostersAdapter, emptyMessageView);
    }


    private int getGridLayoutSpan() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return getResources().getInteger(R.integer.grid_view_landscape_column_number);
        }
        return getResources().getInteger(R.integer.grid_view_portrait_column_number);
    }

    private void applyTopRatedConfiguration() {
        recyclerView.setAdapter(moviePostersAdapter);
        selectedActionId = R.id.action_top_rated;
        setMoviePosters(FILTER_TYPE_TOP_RATED);
    }

    private void applyPopularConfiguration() {
        recyclerView.setAdapter(moviePostersAdapter);
        selectedActionId = R.id.action_popular;
        setMoviePosters(FILTER_TYPE_POPULAR);
    }

    private void applyFavoritesConfiguration() {
        recyclerView.setAdapter(favoriteMoviePostersAdapter);
        selectedActionId = R.id.action_favorites;
        assert getActivity() != null;
        getActivity().getSupportLoaderManager()
                .initLoader(FAVORITE_MOVIES_LOADER_ID, null, favoriteMoviesCursorLoader);
    }

    private void setMoviePosters(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(MOVIES_QUERY_LOADER_FILTER_TYPE_EXTRA, type);
        assert getActivity() != null;
        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        Loader<List<Movie>> loader = loaderManager.getLoader(MOVIES_QUERY_LOADER_ID);
        if (loader == null) {
            loaderManager.initLoader(MOVIES_QUERY_LOADER_ID, bundle, moviesQueryLoader);
        } else {
            loaderManager.restartLoader(MOVIES_QUERY_LOADER_ID, bundle, moviesQueryLoader);
        }
    }

    public void restoreViewState() {
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER_KEY);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

}
