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
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.TextView;

import com.iamutkarshtiwari.popularmovies.adapters.FavoriteMoviePostersAdapter;
import com.iamutkarshtiwari.popularmovies.data.MoviesContract;

import static com.iamutkarshtiwari.popularmovies.fragments.MoviePostersFragment.FAVORITE_MOVIES_LOADER_ID;

/**
 * Created by utkarshtiwari on 22.01.2018.
 */

public class FavoriteMoviesCursorLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private final FavoriteMoviePostersAdapter favoriteMoviePostersAdapter;
    private final TextView emptyMessageView;
    private final Context context;

    public FavoriteMoviesCursorLoader(Context context, FavoriteMoviePostersAdapter favoriteMoviePostersAdapter, TextView emptyMessageView) {
        this.context = context;
        this.favoriteMoviePostersAdapter = favoriteMoviePostersAdapter;
        this.emptyMessageView = emptyMessageView;
        emptyMessageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case FAVORITE_MOVIES_LOADER_ID:
                return new CursorLoader(context,
                        MoviesContract.MoviesEntry.CONTENT_URI,
                        this.getFavoriteMoviesProjection(),
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Unknown loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        emptyMessageView.setVisibility(cursor.getCount() == 0 ? View.VISIBLE : View.INVISIBLE);
        favoriteMoviePostersAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        favoriteMoviePostersAdapter.swapCursor(null);
    }

    private String[] getFavoriteMoviesProjection() {
        return new String[]{
                MoviesContract.MoviesEntry._ID,
                MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
                MoviesContract.MoviesEntry.COLUMN_TITLE,
                MoviesContract.MoviesEntry.COLUMN_DESCRIPTION,
                MoviesContract.MoviesEntry.COLUMN_POSTER_PATH,
                MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
                MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE
        };
    }
}
