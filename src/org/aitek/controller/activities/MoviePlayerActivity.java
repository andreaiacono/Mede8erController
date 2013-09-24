package org.aitek.controller.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import org.aitek.controller.R;
import org.aitek.controller.core.Element;
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
    private ImageButton playButton;
    private Mede8erCommander mede8erCommander;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_player);
        Intent intent = getIntent();
        try {
            mede8erCommander = Mede8erCommander.getInstance(this);

            int position = (Integer) intent.getExtras().get(MoviePlayerActivity.class.getName());
            movie = Mede8erCommander.getInstance(this).getMoviesManager().getMovie(position);
            ImageView imageView = (ImageView) findViewById(R.id.movieplayer_thumbnail);
            movie.showImage(imageView, 500, 260);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        playButton = (ImageButton) findViewById(R.id.PlayButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (movie.getType() == Element.Type.MOVIE_FOLDER) {
                        mede8erCommander.playMovieDir(movie.getJukebox().getAbsolutePath() + movie.getFolder().substring(0, movie.getFolder().length()-1));
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });

    }


}