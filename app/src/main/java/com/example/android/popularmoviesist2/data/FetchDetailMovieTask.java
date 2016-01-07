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
import com.example.android.popularmoviesist2.DetailFragment;

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

public class FetchDetailMovieTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchDetailMovieTask.class.getSimpleName();
    private String movieId;
    public Context mContext;

    public FetchDetailMovieTask(Context context) {
        mContext = context;
    }


    private String[] getMoviesDataFromJson(String moviesJsonStr)
            throws JSONException {

        final String JSON_LIST = "results";
        final String JSON_AUTHOR = "author";
        final String JSON_CONTENT = "content";

        final String [] result = new String [2];

        try {
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(JSON_LIST);

            for (int i = 0; i < moviesArray.length(); i++) {

                JSONObject movie = moviesArray.getJSONObject(i);
                String author = movie.getString(JSON_AUTHOR);
                String content = movie.getString(JSON_CONTENT);

                result[0] = author;
                result[1] = content;

            }
            return result;
        }
        catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected String[] doInBackground(String... params) {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        movieId = params[0];

        String moviesJsonStr = null;

       final String MOVIE_REVIEW_URL = "http://api.themoviedb.org/3/movie/"+movieId+"/reviews";

        final String APIKEY_PARAM = "api_key";

        try {
            Uri builtUri = Uri.parse(MOVIE_REVIEW_URL).buildUpon()
                    .appendQueryParameter(APIKEY_PARAM, BuildConfig.OPEN_TMDB_MAP_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {  return null;    }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {  return null;     }

            moviesJsonStr = buffer.toString();

            //Log.v(LOG_TAG, "Movies string: " + moviesJsonStr);
            return getMoviesDataFromJson(moviesJsonStr);

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


    @Override
    protected void onPostExecute(String[] result) {

        DetailFragment.mReviewView.setText(result[1]);
    }
}