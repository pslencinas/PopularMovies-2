package com.example.android.popularmoviesist2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.android.popularmoviesist2.data.FetchMovieTask;
import com.example.android.popularmoviesist2.data.MovieContract;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = MovieFragment.class.getSimpleName();
    private MovieAdapter mMovieAdapter;
    public static GridView gridView;
    private int qtyMovies;
    private int mPosition = ListView.INVALID_POSITION;
    private String orderBy;

    public Context mContext;

    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {

            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE,
            MovieContract.MovieEntry.COLUMN_RELEASE

    };

    private static final String[] FAVORITE_COLUMNS = {

            MovieContract.FavoriteEntry.TABLE_NAME + "." + MovieContract.FavoriteEntry._ID,
            MovieContract.FavoriteEntry.COLUMN_ID,
            MovieContract.FavoriteEntry.COLUMN_POSTER,
            MovieContract.FavoriteEntry.COLUMN_TITLE,
            MovieContract.FavoriteEntry.COLUMN_OVERVIEW,
            MovieContract.FavoriteEntry.COLUMN_VOTE,
            MovieContract.FavoriteEntry.COLUMN_RELEASE

    };

    static final int COL_TABLE_MOVIE_ID = 0;
    static final int COL_ID = 1;
    static final int COL_POSTER = 2;
    static final int COL_TITLE = 3;
    static final int COL_OVERVIEW = 4;
    static final int COL_VOTE = 5;
    static final int COL_RELEASE = 6;


    public MovieFragment() {

    }

    public interface Callback {
        /**
         *  for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {

                    ((Callback) getActivity()).onItemSelected(MovieContract.MovieEntry.
                            buildMovieUriByMovie(cursor.getLong(COL_ID)));

                }
                mPosition = position;
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        orderBy = Utility.getPreferredOrder(getActivity());
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    void onMovieChanged( ) {
        updateMovies();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }




    public void updateMovies(){

        orderBy = Utility.getPreferredOrder(getActivity());

        if(!orderBy.equals("favorites")){
            FetchMovieTask moviesTask = new FetchMovieTask(getActivity());
            moviesTask.execute(orderBy);
        }



    }




    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri orderUri;

        if(orderBy.equals("favorites")){
            orderUri = MovieContract.FavoriteEntry.CONTENT_URI;

            return new CursorLoader(getActivity(),  //context
                    orderUri,                       //URI
                    FAVORITE_COLUMNS,               //Projection = columnas a devolver
                    null,                           //condicion del query
                    null,                           //argumentos
                    null);                          //orden

        }else{
            orderUri = MovieContract.MovieEntry.CONTENT_URI;

            return new CursorLoader(getActivity(),  //context
                    orderUri,                       //URI
                    MOVIE_COLUMNS,                  //Projection = columnas a devolver
                    null,                           //condicion del query
                    null,                           //argumentos
                    null);                          //orden

        }



    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mMovieAdapter.swapCursor(data);

    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {

    }
}




