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

package com.iamutkarshtiwari.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iamutkarshtiwari.popularmovies.R;
import com.iamutkarshtiwari.popularmovies.activities.MovieDetailsActivity;
import com.iamutkarshtiwari.popularmovies.data.MoviesContract;
import com.iamutkarshtiwari.popularmovies.fragments.MoviePostersFragment;
import com.iamutkarshtiwari.popularmovies.models.Movie;
import com.iamutkarshtiwari.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import static com.iamutkarshtiwari.popularmovies.models.Constants.INTENT_EXTRA_NAME_MOVIE_DETAILS;

/**
 * Created by utkarshtiwari on 22.01.2018.
 */

public class FavoriteMoviePostersAdapter extends RecyclerView.Adapter<FavoriteMoviePostersAdapter.FavoriteMoviePostersAdapterViewHolder> {

    private Cursor cursor;

    private final MoviePostersFragment moviePostersFragment;

    public FavoriteMoviePostersAdapter(MoviePostersFragment moviePostersFragment) {
        this.moviePostersFragment = moviePostersFragment;
    }

    public void swapCursor(Cursor cursor) {
        if (this.cursor != null) {
            this.cursor.close();
        }
        this.cursor = cursor;
        if (cursor != null) {
            notifyDataSetChanged();
        }
        moviePostersFragment.restoreViewState();
    }

    @Override
    public FavoriteMoviePostersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_poster_item, parent, false);
        return new FavoriteMoviePostersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteMoviePostersAdapterViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String posterPath = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH));
        Picasso.with(holder.poster.getContext())
                .load(NetworkUtils.buildPosterUrl(posterPath))
                .placeholder(R.drawable.movie_poster_placeholder)
                .into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }


    public class FavoriteMoviePostersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView poster;

        FavoriteMoviePostersAdapterViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            poster = view.findViewById(R.id.movie_poster);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(context, MovieDetailsActivity.class);
            Movie selectedMovie = getMovieFromCursor(getAdapterPosition());
            intent.putExtra(INTENT_EXTRA_NAME_MOVIE_DETAILS, selectedMovie);
            context.startActivity(intent);
        }
    }

    private Movie getMovieFromCursor(int position) {
        cursor.moveToPosition(position);
        Movie movie = new Movie();
        movie.setId(cursor.getLong(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID)));
        movie.setTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE)));
        movie.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_DESCRIPTION)));
        movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH)));
        movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE)));
        movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE)));
        return movie;
    }
}
