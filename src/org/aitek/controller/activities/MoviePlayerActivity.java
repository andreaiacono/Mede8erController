package org.aitek.controller.activities;

import android.app.Activity;
import android.os.Bundle;
import org.aitek.controller.R;
import org.aitek.controller.core.Movie;
import org.aitek.controller.mede8er.Mede8erCommander;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/16/13
 * Time: 6:23 PM
 */
public class MoviePlayerActivity extends Activity {

    private Movie movie;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_player);

        try {
            int position = (Integer) getIntent().getExtras().get(MovieDetailActivity.class.getName());
            movie = Mede8erCommander.getInstance(this).getMoviesManager().getMovie(position);
            Mede8erCommander.getInstance(this).playMovieDir(movie.getBasePath());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}