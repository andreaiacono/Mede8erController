package org.aitek.controller.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import org.aitek.controller.R;
import org.aitek.controller.core.Movie;
import org.aitek.controller.loaders.ImageShowerTask;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

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
        ImageView imageView = (ImageView) findViewById(R.id.movie_fullscreen_view);
        position = (Integer) intent.getExtras().get(MovieDetailActivity.class.getName());

        try {
            movie = Mede8erCommander.getInstance(this.getApplicationContext()).getMoviesManager().getMovie(position);
            movie.showImage(imageView, Constants.THUMBNAIL_WIDTH, Constants.THUMBNAIL_HEIGHT);
        }
        catch (Exception e) {
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

        if (item.getItemId() == R.id.action_play) {

            try {
                Intent moviePlayerIntent = new Intent(this, MoviePlayerActivity.class);
                moviePlayerIntent.putExtra(MoviePlayerActivity.class.getName(), position);
                startActivity(moviePlayerIntent);
            }
            catch (Exception e) {
                Logger.toast("An error occurred trying to play the movie: " + e.getMessage(), getApplicationContext());
            }

            return true;
        }

        return true;
    }
}