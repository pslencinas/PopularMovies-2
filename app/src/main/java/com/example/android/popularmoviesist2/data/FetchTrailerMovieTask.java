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


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmoviesist2.BuildConfig;
import com.example.android.popularmoviesist2.DetailFragment;
import com.example.android.popularmoviesist2.R;
import com.example.android.popularmoviesist2.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FetchTrailerMovieTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchTrailerMovieTask.class.getSimpleName();
    private String movieId;
    public Context mContext;
    public static ArrayList<String> urlYoutbe = new ArrayList<String>();

    public FetchTrailerMovieTask(Context context) {
        mContext = context;
    }


    private String[] getMoviesDataFromJson(String moviesJsonStr)
            throws JSONException {

        final String JSON_LIST = "results";
        final String JSON_ID = "id";
        final String JSON_KEY = "key";
        final String JSON_NAME = "name";
        final String JSON_SITE = "site";

        urlYoutbe.clear();


        try {
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(JSON_LIST);

            final String [] result = new String [moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {

                JSONObject movie = moviesArray.getJSONObject(i);
                String id = movie.getString(JSON_ID);
                String key = movie.getString(JSON_KEY);
                String name = movie.getString(JSON_NAME);
                String site = movie.getString(JSON_SITE);

                result[i] = key;
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

        final String MOVIE_VIDEO_URL = "http://api.themoviedb.org/3/movie/"+movieId+"/videos";

        final String APIKEY_PARAM = "api_key";

        try {
            Uri builtUri = Uri.parse(MOVIE_VIDEO_URL).buildUpon()
                    .appendQueryParameter(APIKEY_PARAM, BuildConfig.OPEN_TMDB_MAP_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

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

        for (int i = 0; i < result.length; ++i) {
            urlYoutbe.add(result[i]);

        }

        final SimpleArrayAdapter adapter = new SimpleArrayAdapter(mContext, result);
        DetailFragment.mListView1.setAdapter(adapter);

        Utility.setDynamicHeight(DetailFragment.mListView1);
    }
}