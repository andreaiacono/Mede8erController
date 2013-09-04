package org.aitek.controller.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import org.aitek.controller.R;
import org.aitek.controller.core.Movie;
import org.aitek.controller.core.MoviesManager;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.aitek.controller.utils.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/16/13
 * Time: 6:23 PM
 */
public class MovieDetailActivity extends Activity {

    private Movie movie;
    private int position;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        Intent intent = getIntent();
        ImageView imageView = (ImageView) findViewById(R.id.full_screen_view);
        position = (Integer) intent.getExtras().get(MovieDetailActivity.class.getName());

        try {
            movie = Mede8erCommander.getInstance(this).getMoviesManager().getMovie(position);
            imageView.setImageBitmap(movie.getImage());
        }
        catch (Exception e) {
            Logger.toast("Error: " + e.getMessage(), this);
            e.printStackTrace();
        }
        Logger.toast("Created detail", this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        Logger.toast("Starting movie detail act23", this);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Logger.toast("Starting movie detail act2", this);
        if (item.getItemId() == R.id.action_play) {

            try {
                Logger.toast("Starting movie detail act", this);
                Intent mediaPLayerIntent = new Intent(getApplicationContext(), MoviePlayerActivity.class);
                mediaPLayerIntent.putExtra(MoviePlayerActivity.class.getName(), position);
                startActivity(mediaPLayerIntent);
            }
            catch (Exception e) {
                Logger.toast("An error occurred trying to play the movie: " + e.getMessage(), getApplicationContext());
            }

            return true;
        }

        return true;
    }
}