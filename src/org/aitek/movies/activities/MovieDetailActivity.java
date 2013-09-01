package org.aitek.movies.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import org.aitek.movies.R;
import org.aitek.movies.core.Movie;
import org.aitek.movies.core.MoviesManager;
import org.aitek.movies.utils.Logger;
import org.aitek.movies.utils.Mede8erCommander;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/16/13
 * Time: 6:23 PM
 */
public class MovieDetailActivity extends Activity {

    private Movie movie;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        Intent intent = getIntent();
        ImageView imageView = (ImageView) findViewById(R.id.full_screen_view);
        int position = (Integer) intent.getExtras().get(MovieDetailActivity.class.getName());

        try {
            movie = Mede8erCommander.getInstance(this).getMoviesManager().getMovie(position);
            imageView.setImageBitmap(movie.getImage());
        } catch (Exception e) {
            Logger.toast("Error: " + e.getMessage(), this);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movie_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_settings) {

            try {
                Mede8erCommander.getInstance(this).playMovieDir(movie.getPath());
            }
            catch (Exception e) {
                Logger.toast("An error occurred trying to play the movie: " + e.getMessage(), getApplicationContext());
            }

            return true;
        }

        return true;
    }
}