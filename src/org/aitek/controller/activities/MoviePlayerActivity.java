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
import org.aitek.controller.mede8er.RemoteCommand;
import org.aitek.controller.utils.Logger;

import java.io.IOException;

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
                    Logger.log("type= " + movie.getType());
                    if (movie.getType() == Element.Type.FOLDER) {
                        mede8erCommander.playMovieDir(movie.getJukebox().getAbsolutePath() + movie.getJukebox().getSubdir() + movie.getFolder());
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });

        setButton(R.id.PauseButton, RemoteCommand.PAUSE);
        setButton(R.id.StopButton, RemoteCommand.STOP);
        setButton(R.id.PrevTrackButton, RemoteCommand.PREVIOUS_TRACK);
        setButton(R.id.FastReverseButton, RemoteCommand.FAST_REVERSE);
        setButton(R.id.FastForwardButton, RemoteCommand.FAST_FORWARD);
        setButton(R.id.NextTrackButton, RemoteCommand.NEXT_TRACK);
    }

    private void setButton(int buttonRes, final RemoteCommand remoteCommand) {
        ImageButton button = (ImageButton) findViewById(buttonRes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRemoteCommand(remoteCommand);
            }
        });
    }

    private void sendRemoteCommand(RemoteCommand remoteCommand) {
        try {
            mede8erCommander.remoteCommand(remoteCommand);
        }
        catch (IOException e) {
            Logger.toast(e.getMessage(), this);
        }
    }
}