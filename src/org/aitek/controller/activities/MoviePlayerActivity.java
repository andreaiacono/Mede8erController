package org.aitek.controller.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import org.aitek.controller.R;
import org.aitek.controller.core.Movie;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.aitek.controller.mede8er.MovieCommand;
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
    private SeekBar seekBar;
    private int length;

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
                playMovie();
            }
        });

        setButton(R.id.PauseButton, RemoteCommand.PAUSE);
        setButton(R.id.StopButton, RemoteCommand.STOP);
        setButton(R.id.PrevTrackButton, RemoteCommand.PREVIOUS_TRACK);
        setButton(R.id.FastReverseButton, RemoteCommand.FAST_REVERSE);
        setButton(R.id.FastForwardButton, RemoteCommand.FAST_FORWARD);
        setButton(R.id.NextTrackButton, RemoteCommand.NEXT_TRACK);

        seekBar = (SeekBar) findViewById(R.id.movejump_seekbar);
        // TODO: fix get status call to have a real value here
        seekBar.setMax(9000);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                try {
                    mede8erCommander.movieCommand(MovieCommand.JUMP, "" + i);
                }
                catch (IOException e) {
                    Logger.log("An error has occurred sending movie jump command: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void playMovie() {

        try {
            switch (movie.getType()) {

                case FOLDER:
                    mede8erCommander.playMovieDir(movie.getJukebox().getAbsolutePath() + movie.getJukebox().getSubdir() + movie.getFolder());
                    break;
                case FILE:
                    mede8erCommander.playFile(movie.getJukebox().getAbsolutePath() + movie.getJukebox().getSubdir() + movie.getFolder() + "/" + movie.getName());
                    break;
            }

            //mede8erCommander.getMovieLength();

        }
        catch (Exception e) {
            Logger.both("An error has occurred issuing the play command: " + e.getMessage(), this);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
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

    private void sendRemoteCommand(RemoteCommand remoteCommand, String param) {
        try {
            mede8erCommander.remoteCommand(remoteCommand, param);
        }
        catch (IOException e) {
            Logger.toast(e.getMessage(), this);
        }
    }

    private void sendRemoteCommand(RemoteCommand remoteCommand) {
        sendRemoteCommand(remoteCommand, null);
    }

}