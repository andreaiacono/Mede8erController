package org.aitek.controller.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import org.aitek.controller.R;
import org.aitek.controller.core.Element;
import org.aitek.controller.core.Movie;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.aitek.controller.mede8er.MovieCommand;
import org.aitek.controller.mede8er.RemoteCommand;
import org.aitek.controller.utils.Constants;
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
    private SeekBar moviePositionSeekBar;
    private int actualVolume;
    private SeekBar volumeSeekBar;
    private ImageView fullVolumeButton;
    private ImageView muteVolumeButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_player);
        Intent intent = getIntent();
        try {
            mede8erCommander = Mede8erCommander.getInstance(this);

            int position = (Integer) intent.getExtras().get(MoviePlayerActivity.class.getName());
            movie = Mede8erCommander.getInstance(this).getMoviesManager().getMovie(position);
            ImageView imageView = (ImageView) findViewById(R.id.movieplayer_thumbnail);

            // TODO
            movie.showImage(imageView, 500, 260);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        playButton = (ImageButton) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playMovie();
            }
        });

        setButton(R.id.pauseButton, RemoteCommand.PAUSE);
        setButton(R.id.stopButton, RemoteCommand.STOP);
        setButton(R.id.prevTrackButton, RemoteCommand.PREVIOUS_TRACK);
        setButton(R.id.fastReverseButton, RemoteCommand.FAST_REVERSE);
        setButton(R.id.fastForwardButton, RemoteCommand.FAST_FORWARD);
        setButton(R.id.nextTrackButton, RemoteCommand.NEXT_TRACK);

        moviePositionSeekBar = (SeekBar) findViewById(R.id.movieJumpSeekBar);
        moviePositionSeekBar.setMax(9000);
        moviePositionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                try {
                    mede8erCommander.movieCommand(MovieCommand.JUMP, "" + i);
                }
                catch (IOException e) {
                    Logger.log("An error has occurred sending movie jump command: " + e.getMessage());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        volumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setMax(Constants.VOLUME_STEPS);
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i < actualVolume) {
                    changeVolume(RemoteCommand.VOL_DOWN, 1);
                }
                else {
                    changeVolume(RemoteCommand.VOL_UP, 1);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        fullVolumeButton = (ImageView) findViewById(R.id.fullVolumeImageView);
        fullVolumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeVolume(RemoteCommand.VOL_UP, Constants.VOLUME_STEPS);
                volumeSeekBar.setProgress(Constants.VOLUME_STEPS);
            }
        });
        muteVolumeButton = (ImageView) findViewById(R.id.muteImageView2);
        muteVolumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeVolume(RemoteCommand.VOL_DOWN, Constants.VOLUME_STEPS);
                volumeSeekBar.setProgress(0);
            }
        });

        setControlsStatus(false, null);
    }

    private void changeVolume(RemoteCommand remoteCommand, int times) {

        for (int j = 0; j < times; j++) {
            try {
                mede8erCommander.remoteCommand(remoteCommand);
                if (remoteCommand == RemoteCommand.VOL_DOWN) {
                    actualVolume--;
                }
                else {
                    actualVolume++;
                }
            }
            catch (IOException e) {
                Logger.log("An error has occurred changing volume: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movie_player, menu);
        return true;
    }


    private void playMovie() {

        try {
            switch (movie.getType()) {

                case FOLDER:
                    mede8erCommander.playMovieDir(movie.getJukebox().getAbsolutePath() + movie.getJukebox().getSubdir() + movie.getFolder());

                    break;
                case FILE:
                    mede8erCommander.playFile(movie.getJukebox().getAbsolutePath() + movie.getJukebox().getSubdir() + movie.getFolder() + "/" + movie.getName());
                    //mede8erCommander.getMovieLength();
                    break;
            }

            // lowers the volume
            changeVolume(RemoteCommand.VOL_DOWN, Constants.VOLUME_STEPS);

            // raises the volume up to 1/3 of max
            int volumeLevel = Constants.VOLUME_STEPS / 3;
            changeVolume(RemoteCommand.VOL_UP, volumeLevel);

            setControlsStatus(true, movie.getType());
        }
        catch (Exception e) {
            Logger.both("An error has occurred issuing the play command: " + e.getMessage(), this);
            e.printStackTrace();
        }
    }

    private void setControlsStatus(boolean isEnabled, Element.Type type) {

        volumeSeekBar.setEnabled(isEnabled);
        fullVolumeButton.setEnabled(isEnabled);
        muteVolumeButton.setEnabled(isEnabled);

        setButtonStatus(R.id.pauseButton, isEnabled);
        setButtonStatus(R.id.stopButton, isEnabled);
        setButtonStatus(R.id.prevTrackButton, isEnabled);
        setButtonStatus(R.id.fastReverseButton, isEnabled);
        setButtonStatus(R.id.fastForwardButton, isEnabled);
        setButtonStatus(R.id.nextTrackButton, isEnabled);

        if (type == Element.Type.FILE) {
            moviePositionSeekBar.setEnabled(isEnabled);
        }
        else {
            moviePositionSeekBar.setEnabled(false);
        }
    }

    private void setButtonStatus(int buttonRes, boolean isEnabled) {
        ImageButton button = (ImageButton) findViewById(buttonRes);
        button.setEnabled(isEnabled);
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