package com.example.android.popularmoviesist2;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.popularmoviesist2.data.FetchDetailMovieTask;
import com.example.android.popularmoviesist2.data.FetchTrailerMovieTask;
import com.example.android.popularmoviesist2.data.MovieContract;
import com.squareup.picasso.Picasso;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private ShareActionProvider mShareActionProvider;

    private Uri mUri;

    private static final int DETAIL_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {

            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE,
            MovieContract.MovieEntry.COLUMN_RELEASE

    };

    static final int COL_TABLE_MOVIE_ID = 0;
    static final int COL_ID = 1;
    static final int COL_POSTER = 2;
    static final int COL_TITLE = 3;
    static final int COL_OVERVIEW = 4;
    static final int COL_VOTE = 5;
    static final int COL_RELEASE = 6;

    private ImageView mImageView;
    private TextView mTitleView;
    private TextView mOverviewView;
    private TextView mReleaseView;
    private TextView mAverageView;
    public static Button mButton;
    public static TextView mReviewView;
    public static ListView mListView1;

    public static String url_poster;
    public static String title;
    public static String overview;
    public static String release;
    public static String vote;
    public static String move_id;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail_w_list, container, false);


        mImageView = ((ImageView) rootView.findViewById(R.id.imageView));
        mTitleView = ((TextView) rootView.findViewById(R.id.title_text));
        mOverviewView = ((TextView) rootView.findViewById(R.id.overview_text));
        mReleaseView = ((TextView) rootView.findViewById(R.id.release_text));
        mAverageView = ((TextView) rootView.findViewById(R.id.average_text));
        mReviewView = ((TextView) rootView.findViewById(R.id.review_text));

        mListView1 = (ListView)rootView.findViewById(R.id.listViewTrailer);

        mListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Log.d("OnItemClick", "Movies position: " + FetchTrailerMovieTask.urlYoutbe.get(position));
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + FetchTrailerMovieTask.urlYoutbe.get(position))));

            }
        });


        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onChanged( ) {

        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (null != mUri) {

            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {

            move_id = data.getString(COL_ID);

            url_poster = data.getString(COL_POSTER);
            Picasso.with(getActivity()).load(Utility.getUrlMoviesDB() + url_poster).fit().into(mImageView);

            title = data.getString(COL_TITLE);
            mTitleView.setText(title);

            overview = data.getString(COL_OVERVIEW);
            mOverviewView.setText(overview);

            release = data.getString(COL_RELEASE);
            mReleaseView.setText(release);

            vote = data.getString(COL_VOTE);
            mAverageView.setText(vote);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }




}