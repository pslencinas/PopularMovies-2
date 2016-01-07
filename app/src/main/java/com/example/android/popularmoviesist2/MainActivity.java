package com.example.android.popularmoviesist2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmoviesist2.data.FetchDetailMovieTask;
import com.example.android.popularmoviesist2.data.FetchTrailerMovieTask;
import com.example.android.popularmoviesist2.data.MovieContract;

public class MainActivity extends ActionBarActivity implements MovieFragment.Callback  {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane=false;
    private String mOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOrder = Utility.getPreferredOrder(this);
        Log.d(LOG_TAG, "Dentro del onCreate MainActivity!!!!!");


        if (findViewById(R.id.movies_detail_container) != null) {
            mTwoPane = true;
            Log.d(LOG_TAG, "TABLET!!!!!");
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movies_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            Log.d(LOG_TAG, "Dentro del ELSE de onCreate MainActivity!!!!!");
            mTwoPane = false;

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        String order = Utility.getPreferredOrder(this);

        if (order != null && !order.equals(mOrder)) {
            MovieFragment ff = (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
            if ( null != ff ) {
                ff.onMovieChanged();
            }


            mOrder = order;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movies_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();

            updateMovies(contentUri);

        } else {

            updateMovies(contentUri);
            Intent intent = new Intent(this, DetailActivity.class).setData(contentUri);
            startActivity(intent);
        }
    }

    public void updateMovies(Uri uri){
        new FetchTrailerMovieTask(this).execute(MovieContract.MovieEntry.getMovieIDbyUri(uri));
        new FetchDetailMovieTask(this).execute(MovieContract.MovieEntry.getMovieIDbyUri(uri));

    }
}
