package japps.trendymovies.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import japps.trendymovies.R;
import japps.trendymovies.activities.MainActivity;
import japps.trendymovies.activities.MovieDetailActivity;
import japps.trendymovies.data.ImageAdapter;
import japps.trendymovies.data.MovieData;
import japps.trendymovies.data.MovieHandler;
import japps.trendymovies.data.MovieListData;
import japps.trendymovies.tasks.FetchMovieTask;
import japps.trendymovies.utilities.Utils;

/**
 * Created by Julio on 21/1/2016.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ImageAdapter mAdapter;
    private GridView mGrid;
    private Context mContext;
    private MovieHandler mMovies;
    private FetchMovieTask task;
    private final String LOG_TAG = MainFragment.class.getSimpleName();

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final int fragmentMainRes = R.layout.fragment_main;
        final int imageViewId = R.id.poster_view;
        final int gridItemRes = R.layout.list_item_poster;

        if (savedInstanceState != null) {
            Log.d(LOG_TAG, "onCreateView " + savedInstanceState.getString("state"));
        }

        View root = inflater.inflate(fragmentMainRes, container, false);

        mAdapter = new ImageAdapter(mContext, gridItemRes, imageViewId);

        mGrid = (GridView) root.findViewById(R.id.gridView);
        mGrid.setOnItemClickListener(this);
        Log.d(LOG_TAG, "LOCALE: " + Utils.getLocale());

        return root;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        task = new FetchMovieTask() {
            @Override
            protected void onPostExecute(MovieHandler data) {
                super.onPostExecute(data);
                if (data == null) {
                    return;
                }
                MovieData movieData = (MovieData) data;
                Intent intent = new Intent(mContext,MovieDetailActivity.class);
                intent.putExtra(MovieData.TITLE_PARAM,movieData.getMovieTitle());
                intent.putExtra(MovieData.SYNOPSIS_PARAM,movieData.getMovieSynopsis());
                intent.putExtra(MovieData.POSTER_PATH_PARAM,movieData.getMoviePosterPath());
                intent.putExtra(MovieData.RELEASE_DATE_PARAM,movieData.getMovieReleaseDate());
                intent.putExtra(MovieData.RUNTIME_PARAM,movieData.getMovieRuntime());
                intent.putExtra(MovieData.RATE_PARAM,movieData.getMovieRate());
                mContext.startActivity(intent);
            }

        };
        task.execute(FetchMovieTask.FETCH_MOVIE, ((MovieListData) mMovies).getMovieIdList().get(position));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        ActionBar actionBar = ((MainActivity) mContext).getSupportActionBar();
        if (actionBar != null) actionBar.setElevation(0);
        task = new FetchMovieTask() {
            @Override
            protected void onPostExecute(MovieHandler data) {
                super.onPostExecute(data);
                if (data == null) {
                    return;
                }
                mMovies = data;
                mAdapter.setItems(((MovieListData) data).getMoviePosterPathList());
                mGrid.setAdapter(mAdapter);
            }
        };
        task.execute(FetchMovieTask.FETCH_MOVIE_LIST, "");
    }
}
