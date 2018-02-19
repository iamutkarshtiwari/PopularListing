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

package com.iamutkarshtiwari.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by utkarshtiwari on 22.01.2018.
 */

class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(
                "CREATE TABLE " +
                        MoviesContract.MoviesEntry.TABLE_NAME + "(" +
                        MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                        MoviesContract.MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                        MoviesContract.MoviesEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL," +
                        MoviesContract.MoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                        MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                        MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " LONG NOT NULL" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        onCreate(database);
    }
}
