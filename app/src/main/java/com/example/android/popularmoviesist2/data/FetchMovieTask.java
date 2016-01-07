/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularmoviesist2.data;


import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmoviesist2.BuildConfig;
import com.example.android.popularmoviesist2.data.MovieContract.MovieEntry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private String orderBy;
    public Context mContext;

    public FetchMovieTask(Context context) {
        mContext = context;
    }


    private void getMoviesDataFromJson(String moviesJsonStr)
            throws JSONException {

        final String JSON_LIST = "results";
        final String JSON_POSTER = "poster_path";
        final String JSON_TITLE = "original_title";
        final String JSON_OVERVIEW = "overview";
        final String JSON_VOTE = "vote_average";
        final String JSON_RELEASE = "release_date";
        final String JSON_ID = "id";

        try {
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(JSON_LIST);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());

            for (int i = 0; i < moviesArray.length(); i++) {

                JSONObject movie = moviesArray.getJSONObject(i);
                String overview = movie.getString(JSON_OVERVIEW);
                String release = movie.getString(JSON_RELEASE);
                String poster = movie.getString(JSON_POSTER);
                String id = movie.getString(JSON_ID);
                String title = movie.getString(JSON_TITLE);
                String vote = movie.getString(JSON_VOTE);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieEntry.COLUMN_ID, id);
                movieValues.put(MovieEntry.COLUMN_POSTER, poster);
                movieValues.put(MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MovieEntry.COLUMN_OVERVIEW, overview);
                movieValues.put(MovieEntry.COLUMN_VOTE, vote);
                movieValues.put(MovieEntry.COLUMN_RELEASE, release);

                cVVector.add(movieValues);

            }

            int inserted = 0;

            //delete database
            int rowdeleted= mContext.getContentResolver().delete(MovieEntry.CONTENT_URI, null, null);

            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);

                inserted = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
            }


        }
        catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }
    @Override
    protected String[] doInBackground(String... params) {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        orderBy = params[0];

        String moviesJsonStr = null;


        try {

            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/"+orderBy;
            final String APIKEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(APIKEY_PARAM, BuildConfig.OPEN_TMDB_MAP_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            moviesJsonStr = buffer.toString();

            Log.d(LOG_TAG, "Buffer " + moviesJsonStr);

            getMoviesDataFromJson(moviesJsonStr);


        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }


        return null;
    }


}