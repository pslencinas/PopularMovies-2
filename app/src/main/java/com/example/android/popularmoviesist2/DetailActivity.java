package com.example.android.popularmoviesist2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesist2.R;
import com.example.android.popularmoviesist2.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.Vector;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movies_detail_container, fragment)
                    .commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        DetailFragment ff = (DetailFragment)getSupportFragmentManager().findFragmentById(R.id.movies_detail_container);
        ff.onChanged();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickFavorites (View view){
       addToFavorites();
    }

    public void addToFavorites(){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        int inserted=0;

        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieContract.FavoriteEntry.COLUMN_ID, DetailFragment.move_id);
        movieValues.put(MovieContract.FavoriteEntry.COLUMN_POSTER, DetailFragment.url_poster);
        movieValues.put(MovieContract.FavoriteEntry.COLUMN_TITLE, DetailFragment.title);
        movieValues.put(MovieContract.FavoriteEntry.COLUMN_OVERVIEW, DetailFragment.overview);
        movieValues.put(MovieContract.FavoriteEntry.COLUMN_VOTE, DetailFragment.vote);
        movieValues.put(MovieContract.FavoriteEntry.COLUMN_RELEASE, DetailFragment.release);

        int rowdeleted= context.getContentResolver().
                delete(MovieContract.FavoriteEntry.buildMovieUriByMovie(Long.parseLong(DetailFragment.move_id)),
                        MovieContract.FavoriteEntry.COLUMN_ID + "=" + DetailFragment.move_id, null);

        Uri uri = context.getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, movieValues);

        Toast toast = Toast.makeText(context, "Added to Favorites", duration);
        toast.show();


    }

}

